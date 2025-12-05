package org.michalmusil.aidataingest.application.dtos.in;

import org.michalmusil.aidataingest.domain.entities.Field;
import org.michalmusil.aidataingest.domain.entities.IngestedRecord;
import org.michalmusil.aidataingest.domain.entities.RecordValue;

public record CreateRecordValueDto(
        Long fieldId,
        String stringValue
) {
    public RecordValue toDomain(IngestedRecord record, Field field) {
        RecordValue recordValue = new RecordValue();
        recordValue.setStringValue(this.stringValue);
        recordValue.setField(field);
        recordValue.setRecord(record);
        return recordValue;
    }
}