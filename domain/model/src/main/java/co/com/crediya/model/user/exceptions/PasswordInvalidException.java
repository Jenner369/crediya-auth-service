package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.common.exception.DomainException;

public class PasswordInvalidException extends DomainException {
    public PasswordInvalidException() {
        super(
                "USER_PASSWORD_INVALID",
                "La contraseña no cumple con los requisitos de seguridad"
        );
    }
}
