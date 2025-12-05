package org.michalmusil.aidataingest.application.usecases;

import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.services.InputFileIngestProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FindCorrespondingSchemaUseCase implements UseCase<MultipartFile, SchemaDto> {
    private final GetSchemaByIdUseCase getSchemaByIdUseCase;
    private final GetAllSchemasUseCase getAllSchemasUseCase;
    private final InputFileIngestProcessor ingestProcessor;

    public FindCorrespondingSchemaUseCase(GetSchemaByIdUseCase getSchemaByIdUseCase, GetAllSchemasUseCase getAllSchemasUseCase, InputFileIngestProcessor ingestProcessor) {
        this.getSchemaByIdUseCase = getSchemaByIdUseCase;
        this.getAllSchemasUseCase = getAllSchemasUseCase;
        this.ingestProcessor = ingestProcessor;
    }

    @Override
    public SchemaDto execute(MultipartFile file) {
        var availableSchemas = getAllSchemasUseCase.execute(null);

        var foundSchemaId = ingestProcessor.findCorrespondingSchema(file, availableSchemas);
        return getSchemaByIdUseCase.execute(foundSchemaId);
    }
}