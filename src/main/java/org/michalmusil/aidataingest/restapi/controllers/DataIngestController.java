package org.michalmusil.aidataingest.restapi.controllers;

import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.usecases.FindCorrespondingSchemaUseCase;
import org.michalmusil.aidataingest.application.usecases.ParseAndIngestFileUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ingest")
public class DataIngestController {

    private final FindCorrespondingSchemaUseCase findSchemaUseCase;
    private final ParseAndIngestFileUseCase parseAndIngestUseCase;

    public DataIngestController(
            FindCorrespondingSchemaUseCase findSchemaUseCase,
            ParseAndIngestFileUseCase parseAndIngestUseCase) {
        this.findSchemaUseCase = findSchemaUseCase;
        this.parseAndIngestUseCase = parseAndIngestUseCase;
    }

    /**
     * POST /api/ingest/findSchema : Analyzes a file and finds the best matching schema.
     *
     * @param file The uploaded file (image, CSV, PDF, etc.).
     * @return A JSON object containing the matching schema.
     */
    @PostMapping(value = "/findSchema", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SchemaDto> findSchema(@RequestParam("file") MultipartFile file) {
        var foundSchema = findSchemaUseCase.execute(file);
        return ResponseEntity.ok(foundSchema);
    }

    /**
     * POST /api/ingest/process : Uploads a file, processes its content against available schemas,
     * parses the data, and ingests the resulting records.
     *
     * @param file The uploaded file.
     * @return A list of DTOs representing the successfully ingested records.
     */
    @PostMapping(value = "/process", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<IngestedRecordDto>> procssFile(@RequestParam("file") MultipartFile file) {
        var parsedRecords = parseAndIngestUseCase.execute(file);
        return ResponseEntity.ok(parsedRecords);
    }
}
