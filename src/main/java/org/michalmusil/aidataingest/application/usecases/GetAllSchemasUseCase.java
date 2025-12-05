package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.out.SchemaDto;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetAllSchemasUseCase implements UseCase<Void, List<SchemaDto>> {

    private final SchemaRepository schemaRepository;


    public GetAllSchemasUseCase(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    @Override
    @Transactional
    public List<SchemaDto> execute(Void input) {
        return schemaRepository.findAll().stream()
                .map(SchemaDto::fromDomain)
                .collect(Collectors.toList());
    }
}