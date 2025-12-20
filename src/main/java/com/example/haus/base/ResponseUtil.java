package com.example.haus.base;

import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.domain.dto.response.utils.ResponseError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class ResponseUtil {

    private ResponseUtil() {}

    public static <T> ResponseEntity<ResponseData<T>> success(String message, T data) {
        return success(HttpStatus.OK, message, data);
    }

    public static ResponseEntity<ResponseData<Void>> success(HttpStatus status, String message) {
        ResponseData<Void> response = new ResponseData<>(status.value(), message);
        return new ResponseEntity<>(response, status == HttpStatus.NO_CONTENT ? HttpStatus.OK : status);
    }

    public static <T> ResponseEntity<ResponseData<T>> success(HttpStatus status, String message, T data) {
        ResponseData<T> response = new ResponseData<>(status.value(), message, data);
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<ResponseData<T>> success(MultiValueMap<String, String> header, T data) {
        return success(HttpStatus.OK, header, data);
    }

    public static <T> ResponseEntity<ResponseData<T>> success(HttpStatus status, MultiValueMap<String, String> header, T data) {
        ResponseData<T> response = new ResponseData<>(data);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addAll(header);
        return ResponseEntity.status(status).headers(responseHeaders).body(response);
    }

    public static <T> ResponseEntity<ResponseError<T>> error(HttpStatus status, String message) {
        ResponseError<T> response = new ResponseError<>(status.value(), message);
        return new ResponseEntity<>(response, status);
    }
}
