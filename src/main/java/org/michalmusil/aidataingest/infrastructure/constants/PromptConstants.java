package org.michalmusil.aidataingest.infrastructure.constants;

import java.util.Map;

public class PromptConstants {
    public static String FIND_SCHEMA_PROMPT_TEMPLATE = """
            Analyze the content of the attached file.
            The available schemas are provided below in JSON format.
            Determine which Schema best corresponds to the data structure in the file.
            
            Available Schemas:
            %s
            
            INSTRUCTION: Respond ONLY with the corresponding Schema ID (e.g., '123').
            If NO schema is a suitable match, respond ONLY with the letter 'N'.
            DO NOT output any other text, explanation, or JSON formatting.
            """;

    public static String PARSE_DATA_PROMPT_TEMPLATE = """
            You are an expert data parsing and transformation engine.
            Your task is to extract all relevant data points from the attached file and convert them into a structured JSON.
            The target schema ID is %d.
            
            Target Schema Details (for field mapping):
            %s
            
            Instructions:
            1. Use the actual 'schemaId' (%d) in the output.
            2. Map the field names in the file to the corresponding 'fieldId' in the Target Schema Details.
            3. Ensure all 'stringValue' values adhere to the required FieldType (e.g., '2024-01-01' for DATE, 'true' for BOOLEAN, numbers without quotes for NUMBER).
            """;

    public static Map<String, Object> REQUIRED_PARSING_RESPONSE_SCHEMA = Map.of(
            "type", "ARRAY",
            "items", Map.of(
                    "type", "OBJECT",
                    "properties", Map.of(
                            "schemaId", Map.of(
                                    "type", "INTEGER",
                                    "description", "The ID of the schema this record belongs to."
                            ),
                            "values", Map.of(
                                    "type", "ARRAY",
                                    "items", Map.of(
                                            "type", "OBJECT",
                                            "properties", Map.of(
                                                    "fieldId", Map.of(
                                                            "type", "INTEGER",
                                                            "description", "The ID of the schema field definition."
                                                    ),
                                                    "stringValue", Map.of(
                                                            "type", "STRING",
                                                            "description", "The value of the field, formatted as a string."
                                                    )
                                            )
                                    )
                            )
                    )
            )
    );

}
