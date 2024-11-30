package vn.duclan.candlelight_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import vn.duclan.candlelight_be.model.DeliveryMethod;

@RepositoryRestResource(path = "delivery-methods")
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Integer> {}
