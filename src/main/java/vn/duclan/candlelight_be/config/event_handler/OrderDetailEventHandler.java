package vn.duclan.candlelight_be.config.event_handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;

import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.model.OrderDetail;

@RepositoryEventHandler
@Slf4j
public class OrderDetailEventHandler {
    @Autowired
    EntityManager entityManager;

    @HandleBeforeSave
    void handleBeforeSave(OrderDetail orderDetail) {
        if (orderDetail.getOrder() != null) {
            orderDetail.getOrder().calculateTotalPrice();
            log.info("Before Save - Total Price: {}", orderDetail.getOrder().getTotalPrice());
        }
    }

    @HandleAfterDelete
    @Transactional
    void handleAfterDelete(OrderDetail orderDetail) {
        if (orderDetail.getOrder() != null) {
            orderDetail.getOrder().calculateTotalPrice();
            log.info("After Delete - Total Price: {}", orderDetail.getOrder().getTotalPrice());
            entityManager.flush();
        }
    }
}
