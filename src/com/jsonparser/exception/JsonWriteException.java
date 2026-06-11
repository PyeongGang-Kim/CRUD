package com.jsonparser.exception;

public class JsonWriteException extends RuntimeException {
    public JsonWriteException(String message) {
        super(message);
    }

    public JsonWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
