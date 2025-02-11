package vn.duclan.candlelight_be.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import vn.duclan.candlelight_be.model.User;

@RepositoryRestResource(path = "users")
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    // spring data jpa auto generate
    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u.userId) FROM User u")
    public long countUsers();

}
