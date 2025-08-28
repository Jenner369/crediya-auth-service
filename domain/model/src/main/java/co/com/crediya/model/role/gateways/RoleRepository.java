package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Mono<Optional<Role>> findByName(String name);
}
