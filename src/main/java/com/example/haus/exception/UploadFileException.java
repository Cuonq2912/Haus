package com.example.haus.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileException extends RuntimeException {
    public UploadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
