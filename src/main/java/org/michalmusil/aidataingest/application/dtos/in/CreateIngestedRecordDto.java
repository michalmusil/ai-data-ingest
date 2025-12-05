package org.michalmusil.aidataingest.application.dtos.in;

import org.michalmusil.aidataingest.domain.entities.IngestedRecord;
import org.michalmusil.aidataingest.domain.entities.Schema;

import java.util.List;

public record CreateIngestedRecordDto(
        Long schemaId,
        List<CreateRecordValueDto> values
) {
    public IngestedRecord toDomain(Schema schema) {
        IngestedRecord record = new IngestedRecord();
        record.setSchema(schema);
        // Record values must be populated manually
        return record;
    }
}