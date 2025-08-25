package co.com.crediya.exception;

import co.com.crediya.model.common.exceptions.DomainException;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", String.format("El email %s ya está registrado", email));
    }
}
