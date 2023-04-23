package com.marcas.exceptions;

public class UserSessionNotFoundException extends RuntimeException {
    private static final String DESCRIPTION = "User Session Not Found Exception(403)";

    public UserSessionNotFoundException() {
        super(DESCRIPTION);
    }

}