package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.common.exception.DomainException;

public class LastNameRequiredException extends DomainException {
    public LastNameRequiredException() {
        super("USER_LAST_NAME_REQUIRED", "El campo apellido es obligatorio");
    }
}
