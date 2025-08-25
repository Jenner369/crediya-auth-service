package co.com.crediya.exception;

import co.com.crediya.model.common.gateways.exceptions.DomainException;

public abstract class NotFoundException extends DomainException {
    protected NotFoundException(String code, String message) {
        super(code, message);
    }
}
