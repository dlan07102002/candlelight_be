package vn.duclan.candlelight_be.repository;

import vn.duclan.candlelight_be.model.DeliveryMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "delivery-methods")
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Integer> {

}