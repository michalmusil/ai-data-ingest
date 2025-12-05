package org.michalmusil.aidataingest.application.dtos.in;

import org.michalmusil.aidataingest.domain.entities.DataType;
import org.michalmusil.aidataingest.domain.entities.Field;
import org.michalmusil.aidataingest.domain.entities.Schema;

public record CreateFieldDto(
        String name,
        DataType type
) {
    public Field toDomain(Schema schema) {
        Field field = new Field();
        field.setName(this.name);
        field.setDataType(this.type);
        field.setSchema(schema);
        return field;
    }
}