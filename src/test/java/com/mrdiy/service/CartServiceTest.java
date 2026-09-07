package com.mrdiy.service;

import com.mrdiy.common.ApiException;
import com.mrdiy.domain.AttributeDefinition;
import com.mrdiy.domain.AttributeValue;
import com.mrdiy.domain.Product;
import com.mrdiy.domain.ProductAttribute;
import com.mrdiy.dto.CartDtos;
import com.mrdiy.repository.AttributeDefinitionRepository;
import com.mrdiy.repository.AttributeValueRepository;
import com.mrdiy.repository.CartItemRepository;
import com.mrdiy.repository.ProductAttributeRepository;
import com.mrdiy.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceTest {
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Autowired
    private ProductAttributeRepository productAttributeRepository;
    @Autowired
    private AttributeValueRepository attributeValueRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void addToCartRejectsMissingSelectionWhenProductHasAttributes() {
        Product product = product("Cordless Drill");
        AttributeDefinition color = attribute("Color");
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct(product);
        productAttribute.setAttribute(color);
        productAttributeRepository.save(productAttribute);
        AttributeValue red = new AttributeValue();
        red.setProductAttribute(productAttribute);
        red.setValue("red");
        attributeValueRepository.save(red);

        CartDtos.AddToCartRequest request = new CartDtos.AddToCartRequest();
        request.productId = product.getId();
        request.quantity = 1;

        assertThatThrownBy(() -> cartService.addToCart(null, "guest-1", request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("No selected product attributes");
    }

    @Test
    void addToCartAcceptsValidAttributeSelection() {
        Product product = product("Safety Glove");
        AttributeDefinition size = attribute("Size");
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct(product);
        productAttribute.setAttribute(size);
        productAttributeRepository.save(productAttribute);
        AttributeValue medium = new AttributeValue();
        medium.setProductAttribute(productAttribute);
        medium.setValue("M");
        attributeValueRepository.save(medium);

        CartDtos.AddToCartRequest request = new CartDtos.AddToCartRequest();
        request.productId = product.getId();
        request.quantity = 2;
        CartDtos.SelectedAttribute selected = new CartDtos.SelectedAttribute();
        selected.attributeId = size.getId();
        selected.value = "M";
        request.selectedAttributes.add(selected);

        cartService.addToCart(null, "guest-2", request);

        assertThat(cartItemRepository.findAll()).hasSize(1);
    }

    private Product product(String name) {
        Product product = new Product();
        product.setName(name);
        product.setSlug(name.toLowerCase().replace(" ", "-"));
        product.setListingPrice(BigDecimal.valueOf(100));
        product.setMinSellingPrice(BigDecimal.valueOf(90));
        return productRepository.save(product);
    }

    private AttributeDefinition attribute(String name) {
        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(name);
        return attributeDefinitionRepository.save(attribute);
    }
}
