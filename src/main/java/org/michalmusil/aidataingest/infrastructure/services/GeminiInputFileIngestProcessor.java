package org.michalmusil.aidataingest.infrastructure.services;

import org.michalmusil.aidataingest.application.dtos.in.CreateIngestedRecordDto;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.exceptions.IngestParsingException;
import org.michalmusil.aidataingest.application.exceptions.NoCompatibleSchemaFoundException;
import org.michalmusil.aidataingest.application.services.InputFileIngestProcessor;
import org.michalmusil.aidataingest.infrastructure.constants.PromptConstants;
import org.michalmusil.aidataingest.infrastructure.dtos.gemini.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class GeminiInputFileIngestProcessor implements InputFileIngestProcessor {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final Pattern ID_OR_N_PATTERN = Pattern.compile("(\\d+|N)");
    private static final String GEMINI_MODEL = "gemini-2.5-flash";
    private static final String GEMINI_URL_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key={apiKey}";

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiInputFileIngestProcessor(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Long findCorrespondingSchema(MultipartFile file, List<SchemaDto> availableSchemas) {
        if (availableSchemas.isEmpty()) {
            throw new NoCompatibleSchemaFoundException(file.getOriginalFilename(), null);
        }

        String schemasJson;
        try {
            schemasJson = objectMapper.writeValueAsString(availableSchemas);
        } catch (Exception e) {
            throw new NoCompatibleSchemaFoundException(file.getOriginalFilename(), e);
        }

        String userPrompt = String.format(PromptConstants.FIND_SCHEMA_PROMPT_TEMPLATE, schemasJson);

        String base64Data;
        try {
            base64Data = toBase64(file);
        } catch (IOException e) {
            throw new NoCompatibleSchemaFoundException(file.getOriginalFilename(), e);
        }
        String mimeType = file.getContentType();

        String response;
        try {
            response = callGemini(userPrompt, mimeType, base64Data, null, null)
                    .block(); // Blocking call for simplicity in a synchronous service
        } catch (Exception e) {
            throw new NoCompatibleSchemaFoundException(file.getOriginalFilename(), e);
        }

        var responseDto = objectMapper.readValue(response, GeminiResponseDto.class);
        var responseTextPart = responseDto.candidates().stream()
                .findFirst()
                .flatMap(candidate -> Optional.ofNullable(candidate.content()))
                .flatMap(content -> content.parts().stream().findFirst())
                .flatMap(part -> Optional.ofNullable(part.text()));

        var responseText = responseTextPart.orElseThrow(() ->
                new NoCompatibleSchemaFoundException(file.getOriginalFilename(), null));
        responseText = responseText.trim();

        if ("N".equalsIgnoreCase(responseText)) {
            throw new NoCompatibleSchemaFoundException(file.getOriginalFilename(), null);
        }
        try {
            return Long.parseLong(responseText);
        } catch (NumberFormatException e) {
            throw new NoCompatibleSchemaFoundException(file.getOriginalFilename(), e);
        }
    }

    @Override
    public List<CreateIngestedRecordDto> parseToCorrespondingSchema(MultipartFile file, List<SchemaDto> availableSchemas) {
        var schemaId = findCorrespondingSchema(file, availableSchemas);

        var targetSchema = availableSchemas.stream()
                .filter(s -> s.id().equals(schemaId))
                .findFirst()
                .orElseThrow(() -> new NoCompatibleSchemaFoundException(file.getOriginalFilename(), null));

        String targetSchemaJson;
        try {
            targetSchemaJson = objectMapper.writeValueAsString(targetSchema);
        } catch (Exception e) {
            throw new IngestParsingException(file.getOriginalFilename(), e);
        }


        String systemPrompt = String.format(PromptConstants.PARSE_DATA_PROMPT_TEMPLATE,
                schemaId,
                targetSchemaJson,
                schemaId);

        String base64Data = null;
        try {
            base64Data = toBase64(file);
        } catch (IOException e) {
            throw new IngestParsingException(file.getOriginalFilename(), e);
        }
        String mimeType = file.getContentType();

        String jsonResponse;
        try {
            jsonResponse = callGemini(
                    systemPrompt,
                    mimeType,
                    base64Data,
                    "application/json",
                    PromptConstants.REQUIRED_PARSING_RESPONSE_SCHEMA
            ).block();
        } catch (Exception e) {
            throw new IngestParsingException(file.getOriginalFilename(), e);
        }

        try {
            var responseDto = objectMapper.readValue(jsonResponse, GeminiResponseDto.class);
            var responseTextPart = responseDto.candidates().stream()
                    .findFirst()
                    .flatMap(candidate -> Optional.ofNullable(candidate.content()))
                    .flatMap(content -> content.parts().stream().findFirst())
                    .flatMap(part -> Optional.ofNullable(part.text()));

            var responseText = responseTextPart.orElseThrow(() ->
                    new NoCompatibleSchemaFoundException(file.getOriginalFilename(), null));

            return objectMapper.readValue(responseText, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new IngestParsingException(file.getOriginalFilename(), e);
        }
    }

    /**
     * Converts MultipartFile content to Base64 string.
     */
    private String toBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    /**
     * Creates the Gemini API request payload for multimodal input (text + file data).
     */
    private Mono<String> callGemini(String prompt, String mimeType, String base64Data, String responseMimeType, Map<String, Object> responseSchema) {

        // Build the contents array: text prompt + inline data
        List<Object> parts = new ArrayList<>();
        parts.add(Map.of("text", prompt));

        // Add file data if provided
        if (base64Data != null && mimeType != null) {
            parts.add(Map.of("inlineData", Map.of(
                    "mimeType", mimeType,
                    "data", base64Data
            )));
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", List.of(Map.of("parts", parts)));

        // Add generation configuration for structured JSON output if schema is provided
        if (responseMimeType != null && responseSchema != null) {
            payload.put("generationConfig", Map.of(
                    "responseMimeType", responseMimeType,
                    "responseJsonSchema", responseSchema
            ));
        }

        return webClient.post()
                .uri(GEMINI_URL_TEMPLATE, apiKey)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(String.class);
    }
}