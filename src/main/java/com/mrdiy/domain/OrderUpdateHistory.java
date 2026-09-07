package com.mrdiy.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_update_histories")
public class OrderUpdateHistory extends BaseEntity {
    @ManyToOne(optional = false)
    private Order order;
    @ManyToOne(optional = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
}
