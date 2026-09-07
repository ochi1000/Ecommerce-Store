package com.mrdiy.domain;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attribute_values")
public class AttributeValue extends BaseEntity {
    @ManyToOne(optional = false)
    private ProductAttribute productAttribute;
    @Column(name = "attribute_value", nullable = false)
    private String value;

    @ElementCollection
    @CollectionTable(name = "attribute_value_images", joinColumns = @JoinColumn(name = "attribute_value_id"))
    @Column(name = "path")
    private List<String> imgs = new ArrayList<>();

    public ProductAttribute getProductAttribute() {
        return productAttribute;
    }

    public void setProductAttribute(ProductAttribute productAttribute) {
        this.productAttribute = productAttribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }
}
