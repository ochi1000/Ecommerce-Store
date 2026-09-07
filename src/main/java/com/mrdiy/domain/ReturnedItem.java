package com.mrdiy.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "returned_items")
public class ReturnedItem extends BaseEntity {
    @ManyToOne(optional = false)
    private Product product;
    @ManyToOne(optional = false)
    private OrderItem orderItem;
    private BigDecimal sellingPrice = BigDecimal.ZERO;
    private boolean sold;
    private Instant soldDate;
    @ManyToOne
    private User soldBy;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public Instant getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Instant soldDate) {
        this.soldDate = soldDate;
    }

    public User getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(User soldBy) {
        this.soldBy = soldBy;
    }
}
