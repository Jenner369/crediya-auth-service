package co.com.crediya.exception;

import co.com.crediya.model.common.gateways.exceptions.DomainException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND", "Usuario no encontrado");
    }
}
