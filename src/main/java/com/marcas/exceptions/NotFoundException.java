package com.marcas.exceptions;

import com.marcas.common.constants.ErrorMsg;

public class NotFoundException extends RuntimeException {

    private static final String DESCRIPTION = "Not Found Exception (404)";

    public NotFoundException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
    public NotFoundException() {
        super(DESCRIPTION + ". " + ErrorMsg.RECURSO_NO_ENCONTRATO);
    }

}