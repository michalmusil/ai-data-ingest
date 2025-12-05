package org.michalmusil.aidataingest.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoCompatibleSchemaFoundException extends RuntimeException {
    public NoCompatibleSchemaFoundException(String filename, Throwable cause) {
        super(String.format("No matching schema found for file: '%s'", filename), cause);
    }
}