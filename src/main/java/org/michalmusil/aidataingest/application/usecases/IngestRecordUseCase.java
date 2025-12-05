package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.in.CreateIngestedRecordDto;
import org.michalmusil.aidataingest.application.dtos.in.CreateRecordValueDto;
import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.IngestedRecordRepository;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.michalmusil.aidataingest.domain.entities.Field;
import org.michalmusil.aidataingest.domain.entities.IngestedRecord;
import org.michalmusil.aidataingest.domain.entities.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IngestRecordUseCase implements UseCase<CreateIngestedRecordDto, IngestedRecordDto> {

    private final IngestedRecordRepository recordRepository;
    private final SchemaRepository schemaRepository;

    @Autowired
    public IngestRecordUseCase(IngestedRecordRepository recordRepository, SchemaRepository schemaRepository) {
        this.recordRepository = recordRepository;
        this.schemaRepository = schemaRepository;
    }

    @Override
    @Transactional
    public IngestedRecordDto execute(CreateIngestedRecordDto input) {
        var schema = schemaRepository.findById(input.schemaId())
                .orElseThrow(() -> new ResourceNotFoundException("Schema", input.schemaId()));

        validateInput(input, schema);

        Map<Long, Field> fieldMap = schema.getFields().stream()
                .collect(Collectors.toMap(Field::getId, Function.identity()));

        var newRecord = input.toDomain(schema);
        // fill in individual values manually
        input.values().forEach(valueDto -> {
            var fieldEntity = fieldMap.get(valueDto.fieldId());
            newRecord.addValue(valueDto.toDomain(newRecord, fieldEntity));
        });

        IngestedRecord savedRecord = recordRepository.save(newRecord);

        return IngestedRecordDto.fromDomain(savedRecord);
    }

    private void validateInput(CreateIngestedRecordDto input, Schema foundSchema) {
        Set<Long> savedFieldIds = foundSchema.getFields().stream()
                .map(Field::getId)
                .collect(Collectors.toSet());

        Set<Long> inputFieldIds = input.values().stream()
                .map(CreateRecordValueDto::fieldId)
                .collect(Collectors.toSet());

        for (var valueDto : input.values()) {
            if (!savedFieldIds.contains(valueDto.fieldId())) {
                throw new IllegalArgumentException(
                        String.format("Field ID %d is not part of Schema '%s' (ID: %d).",
                                valueDto.fieldId(), foundSchema.getTitle(), foundSchema.getId())
                );
            }
        }

        for (var field : foundSchema.getFields()) {
            if (!inputFieldIds.contains(field.getId())) {
                throw new IllegalArgumentException(
                        String.format("Value is missing field with id %d of Schema '%s' (ID: %d).",
                                field.getId(), foundSchema.getTitle(), foundSchema.getId())
                );
            }
        }
    }
}