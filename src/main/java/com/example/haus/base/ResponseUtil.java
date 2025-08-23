package com.example.haus.base;


import com.example.haus.domain.response.utils.ResponseData;
import com.example.haus.domain.response.utils.ResponseError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class ResponseUtil {
    public static ResponseEntity<ResponseData<?>> success(Object data) {
        return success(HttpStatus.OK, data);
    }

    public static ResponseEntity<ResponseData<?>> success(HttpStatus status, Object data) {
        ResponseData<?> response = new ResponseData<>(data);
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<ResponseData<?>> success(MultiValueMap<String, String> header, Object data) {
        return success(HttpStatus.OK, header, data);
    }

    public static ResponseEntity<ResponseData<?>> success(HttpStatus status, MultiValueMap<String, String> header, Object data) {
        ResponseData<?> response = new ResponseData<>(data);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.addAll(header);
        return ResponseEntity.ok().headers(responseHeaders).body(response);
    }

    public static ResponseEntity<ResponseError<?>> error(HttpStatus status, String message) {
        ResponseError<?> response = new ResponseError<>(status.value(), message);
        return new ResponseEntity<>(response, status);
    }
}
