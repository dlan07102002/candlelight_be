package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    // spring data jpa auto generate
    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    public User findByUsername(String username);

    public User findByEmail(String email);

}