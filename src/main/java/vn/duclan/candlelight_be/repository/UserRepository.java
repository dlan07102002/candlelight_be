package vn.duclan.candlelight_be.repository;

import vn.duclan.candlelight_be.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import jakarta.transaction.Transactional;

@RepositoryRestResource(path = "users")
@Transactional

public interface UserRepository extends JpaRepository<User, Integer> {
    // spring data jpa auto generate
    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

}