package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.OrderDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "order-details")
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}