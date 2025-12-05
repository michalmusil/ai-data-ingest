package org.michalmusil.aidataingest.application.usecases;

import jakarta.transaction.Transactional;
import org.michalmusil.aidataingest.application.common.UseCase;
import org.michalmusil.aidataingest.application.dtos.in.CreateIngestedRecordDto;
import org.michalmusil.aidataingest.application.dtos.in.CreateRecordValueDto;
import org.michalmusil.aidataingest.application.dtos.out.IngestedRecordDto;
import org.michalmusil.aidataingest.application.exceptions.InvalidInputException;
import org.michalmusil.aidataingest.application.exceptions.ResourceNotFoundException;
import org.michalmusil.aidataingest.application.repositories.IngestedRecordRepository;
import org.michalmusil.aidataingest.application.repositories.SchemaRepository;
import org.michalmusil.aidataingest.domain.entities.Field;
import org.michalmusil.aidataingest.domain.entities.IngestedRecord;
import org.michalmusil.aidataingest.domain.entities.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        Map<Long, Field> fieldMap = schema.getFields().stream()
                .collect(Collectors.toMap(Field::getId, Function.identity()));

        validateFieldPresence(input, schema);
        input.values().forEach(value -> {
            validateRecordValueAgainstFieldDataType(fieldMap.get(value.fieldId()), value);
        });

        var newRecord = input.toDomain(schema);
        // fill in individual values manually
        input.values().forEach(valueDto -> {
            var fieldEntity = fieldMap.get(valueDto.fieldId());
            newRecord.addValue(valueDto.toDomain(newRecord, fieldEntity));
        });

        IngestedRecord savedRecord = recordRepository.save(newRecord);

        return IngestedRecordDto.fromDomain(savedRecord);
    }

    private void validateFieldPresence(CreateIngestedRecordDto input, Schema foundSchema) {
        Set<Long> savedFieldIds = foundSchema.getFields().stream()
                .map(Field::getId)
                .collect(Collectors.toSet());

        Set<Long> inputFieldIds = input.values().stream()
                .map(CreateRecordValueDto::fieldId)
                .collect(Collectors.toSet());

        for (var valueDto : input.values()) {
            if (!savedFieldIds.contains(valueDto.fieldId())) {
                throw new InvalidInputException(
                        String.format("Field ID %d is not part of Schema '%s' (ID: %d).",
                                valueDto.fieldId(), foundSchema.getTitle(), foundSchema.getId())
                );
            }
        }

        for (var field : foundSchema.getFields()) {
            if (!inputFieldIds.contains(field.getId())) {
                throw new InvalidInputException(
                        String.format("Value is missing field with id %d of Schema '%s' (ID: %d).",
                                field.getId(), foundSchema.getTitle(), foundSchema.getId())
                );
            }
        }
    }

    private void validateRecordValueAgainstFieldDataType(Field field, CreateRecordValueDto valueDto) {
        var value = valueDto.stringValue();
        var dataType = field.getDataType();

        try {
            switch (dataType) {
                case NUMBER:
                    Double.parseDouble(value);
                    break;
                case DATE:
                    LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                    break;
                case BOOLEAN:
                    var lowerValue = value.toLowerCase();
                    if (!("true".equals(lowerValue) || "false".equals(lowerValue))) {
                        throw new InvalidInputException("Value is not a valid boolean ('true' or 'false').");
                    }
                    break;
                case DATETIME:
                    LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    break;
                case STRING:
                    break;
                default:
                    throw new InvalidInputException("Unsupported DataType encountered: " + dataType);
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(
                    String.format("Value '%s' is not a valid %s for field '%s'. Expected numeric format.",
                            value, dataType.name(), field.getName())
            );
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(
                    String.format("Value '%s' is not a valid %s for field '%s'. Expected format: YYYY-MM-DD.",
                            value, dataType.name(), field.getName())
            );
        }
    }
}