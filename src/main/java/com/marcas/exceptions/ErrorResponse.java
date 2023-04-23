package com.marcas.exceptions;

import lombok.Getter;

@Getter
class ErrorResponse {

    private final String message;
    private final String path;

    ErrorResponse(Exception exception, String path) {
        this.message = exception.getMessage();
        this.path = path;
    }

}
