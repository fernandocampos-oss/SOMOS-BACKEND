package pe.gob.essalud.apps.exceptions;

public class ValidationException extends RuntimeException {

    public ValidationException(String detail) {
        super(detail);
    }

}
