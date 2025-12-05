package org.michalmusil.aidataingest.infrastructure.dtos.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContentDto(
        List<PartDto> parts
) {
}
