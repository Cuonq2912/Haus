package com.example.haus.exception;


import com.example.haus.domain.response.utils.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class, HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Bad Request", content =
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples =
    @ExampleObject(name = "Handle exception when the data input valid (@Request Body, @RequestParam, @PathVariable)",
            description ="Handle Bad Request",
            value = """
                            {
                                "timestamp": "2025-07-10T03:50:48.369+00:00",
                                "status": 400,
                                "path": "/api/v1",
                                "error": "Payload invalid | Parameter invalid | Invalid data",
                                "message": "{data} must not be .... " 
                            }
                            """

    )
    )
    )
    public ErrorResponse handleValidationException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = ex.getMessage();
        if(ex instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf('[');
            int end = message.lastIndexOf(']');
            message = message.substring(start, end - 1);
            errorResponse.setError("Payload Invalid");
            errorResponse.setMessage(message);
        } else if(ex instanceof ConstraintViolationException) {
            int start = message.indexOf(" ") + 1;
            message = message.substring(start);
            errorResponse.setError("Parameter Invalid");
            errorResponse.setMessage(message);
        } else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(message);
        }
        return errorResponse;
    }

    /**
     * Handle exception when user not authenticated
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({InternalAuthenticationServiceException.class, AuthenticationException.class, UnauthorizedException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "401 Response",
                                    summary = "Handle exception when resource not found",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 401,
                                              "path": "/api/v1/...",
                                              "error": "Unauthorized",
                                              "message": "Username or password is incorrect"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(UNAUTHORIZED.value());
        errorResponse.setError(UNAUTHORIZED.getReasonPhrase());
        errorResponse.setMessage("Username or password is incorrect");

        return errorResponse;
    }

    /*
     * Handle exception when the request not found data
     *
     * @param e
     * @param request
     * @return
     * */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", description = "Bad Request", content =
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples =
    @ExampleObject(name = "404 response",
            description = "Handle exception when resource not found",
            value = """
                                        {
                                            "timestamp": "2023-10-19T06:07:35.321+00:00",
                                            "status": 404,
                                            "path": "/api/v1/...",
                                            "error": "Not Found",
                                            "message": "{data} not found"
                                        }
                                        """
    )
    )
    )
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    /**
     * Handle exception when the request not found data
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({ForBiddenException.class, AccessDeniedException.class, AccessDeniedException.class})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "403 Response",
                                    summary = "Handle exception when access forbidden",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 403,
                                              "path": "/api/v1/...",
                                              "error": "Forbidden",
                                              "message": "Access Denied"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleAccessDeniedException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(FORBIDDEN.value());
        errorResponse.setError(FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(CONFLICT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "409 Response",
                                    summary = "Handle exception when input data is conflicted",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 409,
                                              "path": "/api/v1/...",
                                              "error": "Conflict",
                                              "message": "{data} exists, Please try again!"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleDuplicateKeyException(InvalidDataException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    /*
    * Handle upload file exception
    *
    * @param e
    * @param request
    * @return error
    * */
    @ExceptionHandler(UploadFileException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when upload file failed",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/api/v1/...",
                                              "error": "Bad Request",
                                              "message": "Upload file failed!"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleUploadFileException(InvalidDataException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    /**
     * Handle exception when internal server error
     *
     * @param e
     * @param request
     * @return error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "500 Response",
                                    summary = "Handle exception when internal server error",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:35:52.333+00:00",
                                              "status": 500,
                                              "path": "/api/v1/...",
                                              "error": "Internal Server Error",
                                              "message": "Connection timeout, please try again"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }
}