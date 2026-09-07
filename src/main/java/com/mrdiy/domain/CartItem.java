package com.mrdiy.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {
    @ManyToOne(optional = false)
    private Cart cart;
    @ManyToOne(optional = false)
    private Product product;
    private int quantity;
    @Lob
    private String selectedAttributesJson = "[]";

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSelectedAttributesJson() {
        return selectedAttributesJson;
    }

    public void setSelectedAttributesJson(String selectedAttributesJson) {
        this.selectedAttributesJson = selectedAttributesJson;
    }
}
