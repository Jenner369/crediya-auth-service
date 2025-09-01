package co.com.crediya.exception;

import co.com.crediya.model.common.exception.DomainException;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Las credenciales proporcionadas no son válidas");
    }
}
