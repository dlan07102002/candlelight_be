package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.DeliveryMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Integer> {

}