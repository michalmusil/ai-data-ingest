package org.michalmusil.aidataingest.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IngestParsingException extends RuntimeException {
    public IngestParsingException(String filename, Throwable cause) {
        super(String.format("Failed to parse record values from file: '%s'", filename), cause);
    }
}