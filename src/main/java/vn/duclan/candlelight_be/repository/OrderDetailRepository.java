package vn.duclan.candlelight_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import vn.duclan.candlelight_be.model.OrderDetail;

@RepositoryRestResource(path = "order-details")
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {}
