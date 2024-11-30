package vn.duclan.candlelight_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import vn.duclan.candlelight_be.model.Role;

@RepositoryRestResource(path = "roles")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    public Role findByRoleName(String roleName);
}
