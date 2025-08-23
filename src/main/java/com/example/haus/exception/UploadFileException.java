package com.example.haus.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class UploadFileException extends RuntimeException {
    public UploadFileException(String message) {
        super(message);
    }
}
