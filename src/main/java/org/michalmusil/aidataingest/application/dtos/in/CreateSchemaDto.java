package org.michalmusil.aidataingest.application.dtos.in;

import org.michalmusil.aidataingest.domain.entities.Schema;

import java.util.List;

public record CreateSchemaDto(
        String title,
        String description,
        List<CreateFieldDto> fields
) {
    public Schema toDomain() {
        Schema schema = new Schema();
        schema.setTitle(this.title);
        schema.setDescription(this.description);

        this.fields.stream()
                .map(createFieldDto -> createFieldDto.toDomain(schema))
                .forEach(schema::addField);

        return schema;
    }
}