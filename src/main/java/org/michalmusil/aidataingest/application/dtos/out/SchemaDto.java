package org.michalmusil.aidataingest.application.dtos.out;

import org.michalmusil.aidataingest.domain.entities.Schema;

import java.util.List;
import java.util.stream.Collectors;

public record SchemaDto(
        Long id,
        String title,
        String description,
        List<FieldDto> fields
) {
    public static SchemaDto fromDomain(Schema schema) {
        List<FieldDto> fieldDtos = schema.getFields().stream()
                .map(FieldDto::fromDomain)
                .collect(Collectors.toList());

        return new SchemaDto(
                schema.getId(),
                schema.getTitle(),
                schema.getDescription(),
                fieldDtos
        );
    }
}