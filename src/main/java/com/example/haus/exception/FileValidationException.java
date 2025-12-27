package com.example.haus.exception;

import lombok.Getter;


@Getter
public class FileValidationException extends RuntimeException {

    private final FileValidationErrorCode errorCode;

    public FileValidationException(String message, FileValidationErrorCode errorCode) {
      super(message);
      this.errorCode = errorCode;
    }

    public FileValidationException(String message, FileValidationErrorCode errorCode, Throwable cause) {
      super(message, cause);
      this.errorCode = errorCode;
    }


    public enum FileValidationErrorCode {

        INVALID_MIME_TYPE,

        INVALID_MAGIC_BYTES,

        FILE_TOO_LARGE,

        EMPTY_FILE,

        VIRUS_DETECTED,

        SCAN_FAILED
    }
}
