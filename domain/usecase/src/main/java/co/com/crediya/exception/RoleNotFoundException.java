package co.com.crediya.exception;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super("ROLE_NOT_FOUND", "El rol no fue encontrado");
    }
}
