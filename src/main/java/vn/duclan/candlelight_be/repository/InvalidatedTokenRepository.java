package vn.duclan.candlelight_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.duclan.candlelight_be.model.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

}
