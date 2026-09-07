package com.mrdiy.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_tags")
public class ProductTag extends BaseEntity {
    @ManyToOne(optional = false)
    private Product product;
    @ManyToOne(optional = false)
    private Tag tag;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
