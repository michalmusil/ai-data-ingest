package org.michalmusil.aidataingest.application.dtos.out;

import org.michalmusil.aidataingest.domain.entities.DataType;
import org.michalmusil.aidataingest.domain.entities.Field;

public record FieldDto(
        Long id,
        String name,
        DataType type
) {
    public static FieldDto fromDomain(Field field) {
        return new FieldDto(
                field.getId(),
                field.getName(),
                field.getDataType()
        );
    }
}