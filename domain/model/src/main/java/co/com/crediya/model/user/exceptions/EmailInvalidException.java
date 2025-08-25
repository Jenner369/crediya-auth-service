package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.common.exception.DomainException;

public class EmailInvalidException extends DomainException {
    public EmailInvalidException() {
        super("USER_EMAIL_INVALID", "El correo electrónico no es válido");
    }
}
