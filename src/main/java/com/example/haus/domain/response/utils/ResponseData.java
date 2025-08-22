package com.example.haus.domain.response.utils;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResponseData<T> implements Serializable {
    private final int status;
    private final String message;
    private T data;

    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}