package co.com.crediya.model.user.exceptions;

import co.com.crediya.model.common.exceptions.DomainException;

public class NameRequiredException extends DomainException {
    public NameRequiredException() {
        super("USER_NAME_REQUIRED", "El campo nombre es obligatorio");
    }
}
