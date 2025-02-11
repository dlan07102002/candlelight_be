package vn.duclan.candlelight_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.enums.PaymentStatus;

@RepositoryRestResource(path = "orders")
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Get latest Order By User
    Optional<Order> findTopByUser_UserIdOrderByOrderIdDesc(@RequestParam("userId") Long userId);

    @Query("SELECT COUNT(o.orderId) FROM Order o")
    public long countOrders();

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.paymentStatus = :status")
    Double calculateRevenue(@Param("status") PaymentStatus status);

}
