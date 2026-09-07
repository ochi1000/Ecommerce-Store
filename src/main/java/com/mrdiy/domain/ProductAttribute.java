package com.mrdiy.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_attributes")
public class ProductAttribute extends BaseEntity {
    @ManyToOne(optional = false)
    private Product product;
    @ManyToOne(optional = false)
    private AttributeDefinition attribute;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public AttributeDefinition getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeDefinition attribute) {
        this.attribute = attribute;
    }
}
