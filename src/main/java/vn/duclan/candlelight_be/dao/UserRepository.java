package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}