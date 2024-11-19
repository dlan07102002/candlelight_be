package vn.duclan.candlelight_be.repository;

import vn.duclan.candlelight_be.model.PaymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "payment-methods")
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

}