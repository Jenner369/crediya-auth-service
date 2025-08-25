package co.com.crediya.exception;

import co.com.crediya.model.common.exceptions.DomainException;

public class IdentityDocumentAlreadyExists extends DomainException {
    public IdentityDocumentAlreadyExists(String identityDocument) {
        super("IDENTITY_DOCUMENT_ALREADY_EXISTS", String.format("El documento de identidad %s ya está registrado", identityDocument));
    }
}
