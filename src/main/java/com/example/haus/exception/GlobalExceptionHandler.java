package com.example.haus.exception;


import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.haus.base.RestData;
import com.example.haus.base.VsResponseUtil;
import com.example.haus.constant.ErrorMessage;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    //Error validate for param
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> result = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String fieldName = ((PathImpl) error.getPropertyPath()).getLeafNode().getName();
            String errorMessage = messageSource.getMessage(Objects.requireNonNull(error.getMessage()), null,
                    LocaleContextHolder.getLocale());
            result.put(fieldName, errorMessage);
        });
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
    }

    //Error validate for body
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleValidException(BindException ex) {
        Map<String, String> result = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null,
                    LocaleContextHolder.getLocale());
            result.put(fieldName, errorMessage);
        });
        return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RestData<?>> handlerInternalServerError(Exception ex) {
        log.error(ex.getMessage(), ex);
        String message = messageSource.getMessage(ErrorMessage.ERR_EXCEPTION_GENERAL, null,
                LocaleContextHolder.getLocale());
        return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    //Exception custom
    @ExceptionHandler(VsException.class)
    public ResponseEntity<RestData<?>> handleVsException(VsException ex) {
        log.error(ex.getMessage(), ex);
        return VsResponseUtil.error(ex.getStatus(), ex.getErrMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestData<?>> handlerNotFoundException(NotFoundException ex) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<RestData<?>> handlerInvalidException(InvalidDataException ex) {
        log.error(ex.getMessage(), ex);
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<RestData<?>> handlerInternalServerException(InternalServerException ex) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<RestData<?>> handleUploadImageException(UploadFileException ex) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException ex) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<RestData<?>> handleAccessDeniedException(ForbiddenException ex) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        log.error(message, ex);
        return VsResponseUtil.error(ex.getStatus(), message);
    }

}