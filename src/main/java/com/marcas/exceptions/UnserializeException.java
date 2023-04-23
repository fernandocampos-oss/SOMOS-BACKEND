package com.marcas.exceptions;

public class UnserializeException extends RuntimeException {

    private static final String DESCRIPTION = "Deserializar Exception";

    public UnserializeException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
    public UnserializeException() {
        super(DESCRIPTION);
    }

}