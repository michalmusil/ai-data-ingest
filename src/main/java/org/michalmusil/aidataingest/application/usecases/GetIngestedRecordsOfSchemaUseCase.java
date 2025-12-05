package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.IngestedRecordRepository;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetIngestedRecordsOfSchemaUseCase implements UseCase<Long, List<IngestedRecordDto>> {

    private final IngestedRecordRepository recordRepository;
    private final SchemaRepository schemaRepository;

    public GetIngestedRecordsOfSchemaUseCase(IngestedRecordRepository recordRepository, SchemaRepository schemaRepository) {
        this.recordRepository = recordRepository;
        this.schemaRepository = schemaRepository;
    }

    @Override
    @Transactional
    public List<IngestedRecordDto> execute(Long schemaId) {
        if (!schemaRepository.existsById(schemaId)) {
            throw new ResourceNotFoundException("Schema", schemaId);
        }

        return recordRepository.findBySchemaId(schemaId).stream()
                .map(IngestedRecordDto::fromDomain)
                .collect(Collectors.toList());
    }
}