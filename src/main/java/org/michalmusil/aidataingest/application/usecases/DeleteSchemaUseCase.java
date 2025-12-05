package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteSchemaUseCase implements UseCase<Long, Void> {

    private final SchemaRepository schemaRepository;

    @Autowired
    public DeleteSchemaUseCase(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    @Override
    @Transactional
    public Void execute(Long schemaId) {
        if (!schemaRepository.existsById(schemaId)) {
            throw new ResourceNotFoundException("Schema", schemaId);
        }

        schemaRepository.deleteById(schemaId);
        return null;
    }
}