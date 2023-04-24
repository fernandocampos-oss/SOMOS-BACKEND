package pe.gob.essalud.apps.exceptions;

import pe.gob.essalud.apps.common.constants.ErrorMsg;

public class NotFoundException extends RuntimeException {

    private static final String DESCRIPTION = "Not Found Exception (404)";

    public NotFoundException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
    public NotFoundException() {
        super(DESCRIPTION + ". " + ErrorMsg.RECURSO_NO_ENCONTRATO);
    }

}