package org.michalmusil.aidataingest.application.services;

import org.michalmusil.aidataingest.application.dtos.in.CreateIngestedRecordDto;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InputFileIngestProcessor {

    /**
     * Attempts to find a corresponding schema ID for the given file content.
     *
     * @param file             The input file.
     * @param availableSchemas A list of currently available schemas as DTOs.
     * @return The ID of the matching schema.
     */
    Long findCorrespondingSchema(MultipartFile file, List<SchemaDto> availableSchemas);

    /**
     * Parses the file content and transforms it into structured data records based on the found schema.
     *
     * @param file             The input file.
     * @param availableSchemas A list of currently available schemas as DTOs.
     * @return A list of CreateIngestedRecordDto objects ready for persistence.
     */
    List<CreateIngestedRecordDto> parseToCorrespondingSchema(MultipartFile file, List<SchemaDto> availableSchemas);
}
