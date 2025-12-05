package org.michalmusil.aidataingest.application.dtos.out;

import org.michalmusil.aidataingest.domain.entities.RecordValue;

public record RecordValueDto(
        Long id,
        String stringValue,
        String fieldName,
        Long fieldId
) {
    public static RecordValueDto fromDomain(RecordValue recordValue) {
        return new RecordValueDto(
                recordValue.getId(),
                recordValue.getStringValue(),
                recordValue.getField().getName(),
                recordValue.getField().getId()
        );
    }
}