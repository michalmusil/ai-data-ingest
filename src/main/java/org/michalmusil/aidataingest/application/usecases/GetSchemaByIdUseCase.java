package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.springframework.stereotype.Component;

@Component
public class GetSchemaByIdUseCase implements UseCase<Long, SchemaDto> {

    private final SchemaRepository schemaRepository;

    public GetSchemaByIdUseCase(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    @Override
    @Transactional
    public SchemaDto execute(Long schemaId) {
        return schemaRepository.findById(schemaId)
                .map(SchemaDto::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Schema", schemaId));
    }
}