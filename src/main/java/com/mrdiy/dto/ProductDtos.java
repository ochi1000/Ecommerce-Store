package com.mrdiy.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ProductDtos {
    private ProductDtos() {
    }

    public static class ProductRequest {
        public String name;
        public UUID brandId;
        public UUID categoryId;
        public String model;
        public String sku;
        public BigDecimal minSellingPrice = BigDecimal.ZERO;
        public BigDecimal listingPrice = BigDecimal.ZERO;
        public BigDecimal discountPrice = BigDecimal.ZERO;
        public String warranty;
        public String shortDescription;
        public String longDescription;
        public String whatsInTheBox;
        public boolean onDiscount;
        public boolean inStock = true;
        public boolean stockItem = true;
        public boolean status = true;
        public List<String> imgs = new ArrayList<>();
        public List<AttributeSelection> attributes = new ArrayList<>();
        public List<UUID> tags = new ArrayList<>();
    }

    public static class AttributeSelection {
        public UUID attributeId;
        public String value;
        public List<String> imgs = new ArrayList<>();
    }

    public static class ProductSearch {
        public UUID categoryId;
        public String search;
        public String pricing;
    }
}
