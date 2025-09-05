package com.example.haus.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}