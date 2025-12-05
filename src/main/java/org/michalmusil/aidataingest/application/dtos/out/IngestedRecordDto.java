package org.michalmusil.aidataingest.application.dtos.out;

import org.michalmusil.aidataingest.domain.entities.IngestedRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record IngestedRecordDto(
        Long id,
        Long schemaId,
        String schemaTitle,
        LocalDateTime ingestionTimestamp,
        List<RecordValueDto> values
) {
    public static IngestedRecordDto fromDomain(IngestedRecord record) {
        List<RecordValueDto> valueDtos = record.getValues().stream()
                .map(RecordValueDto::fromDomain)
                .collect(Collectors.toList());

        return new IngestedRecordDto(
                record.getId(),
                record.getSchema().getId(),
                record.getSchema().getTitle(),
                record.getIngestionTimestamp(),
                valueDtos
        );
    }
}