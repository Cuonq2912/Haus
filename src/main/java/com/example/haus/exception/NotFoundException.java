package com.example.haus.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private String message;

    private HttpStatus status;

    private String[] params;

    public NotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.message = message;
    }

    public NotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public NotFoundException(String message, String[] params) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
        this.message = message;
        this.params = params;
    }

    public NotFoundException(HttpStatus status, String message, String[] params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

}