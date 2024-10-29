package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    // spring data jpa auto generate
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}