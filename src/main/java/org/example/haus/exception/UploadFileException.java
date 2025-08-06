package org.example.haus.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class UploadFileException extends RuntimeException {

    private String message;

    private HttpStatus status;

    private String[] params;

    public UploadFileException(String message) {
        super(message);
        this.status = HttpStatus.BAD_GATEWAY;
        this.message = message;
    }

    public UploadFileException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public UploadFileException(String message, String[] params) {
        super(message);
        this.status = HttpStatus.BAD_GATEWAY;
        this.message = message;
        this.params = params;
    }

    public UploadFileException(HttpStatus status, String message, String[] params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

}
