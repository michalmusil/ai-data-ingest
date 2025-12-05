package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.in.CreateSchemaDto;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.exceptions.DuplicateResourceException;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSchemaUseCase implements UseCase<CreateSchemaDto, SchemaDto> {
    private final SchemaRepository schemaRepository;

    @Autowired
    public CreateSchemaUseCase(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    @Override
    @Transactional
    public SchemaDto execute(CreateSchemaDto input) {
        schemaRepository.findByTitle(input.title())
                .ifPresent(s -> {
                    throw new DuplicateResourceException("Schema", input.title());
                });

        var newSchema = input.toDomain();
        var savedSchema = schemaRepository.save(newSchema);
        return SchemaDto.fromDomain(savedSchema);
    }
}