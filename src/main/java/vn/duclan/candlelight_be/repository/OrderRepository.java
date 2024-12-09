package vn.duclan.candlelight_be.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import vn.duclan.candlelight_be.model.Order;

@RepositoryRestResource(path = "orders")
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Get latest Order By User
    Page<Order> findTopByUser_UserIdOrderByOrderIdDesc(@RequestParam("userId") int userId, Pageable pageable);

    @Query("SELECT COUNT(o.orderId) FROM Order o")
    public long countOrders();

}
