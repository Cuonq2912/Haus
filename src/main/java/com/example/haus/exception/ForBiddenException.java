package com.example.haus.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForBiddenException extends RuntimeException {
    public ForBiddenException(String message) {
        super(message);
    }
}
