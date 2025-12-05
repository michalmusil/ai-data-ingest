package org.michalmusil.aidataingest.infrastructure.dtos.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CandidateDto(
        ContentDto content
) {
}
