package com.example.haus.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidDataException extends RuntimeException {

    private String message;

    private HttpStatus status;

    private String[] params;

    public InvalidDataException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
    }

    public InvalidDataException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public InvalidDataException(String message, String[] params) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
        this.params = params;
    }

    public InvalidDataException(HttpStatus status, String message, String[] params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

}
