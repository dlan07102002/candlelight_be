package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "repositories")
public interface OrderRepository extends JpaRepository<Order, Integer> {

}