package com.mrdiy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrdiy.common.ApiException;
import com.mrdiy.domain.Cart;
import com.mrdiy.domain.CartItem;
import com.mrdiy.domain.Product;
import com.mrdiy.domain.ProductAttribute;
import com.mrdiy.domain.User;
import com.mrdiy.dto.CartDtos;
import com.mrdiy.repository.AttributeValueRepository;
import com.mrdiy.repository.CartItemRepository;
import com.mrdiy.repository.CartRepository;
import com.mrdiy.repository.ProductAttributeRepository;
import com.mrdiy.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ObjectMapper objectMapper;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, ProductAttributeRepository productAttributeRepository,
                       AttributeValueRepository attributeValueRepository, ObjectMapper objectMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CartItem addToCart(User user, String sessionId, CartDtos.AddToCartRequest request) {
        Cart cart = resolveCart(user, sessionId);
        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
        validateProductSelection(product, request.selectedAttributes);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(request.quantity);
        item.setSelectedAttributesJson(toJson(request.selectedAttributes));
        return cartItemRepository.save(item);
    }

    public List<CartItem> viewCart(User user, String sessionId) {
        Cart cart = findCart(user, sessionId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));
        return cartItemRepository.findByCartId(cart.getId());
    }

    @Transactional
    public void updateQuantity(User user, String sessionId, CartDtos.UpdateQuantityRequest request) {
        Cart cart = findCart(user, sessionId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem item = cartItemRepository.findByIdAndCartId(request.cartItemId, cart.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item is not in cart"));
        item.setQuantity(request.quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void remove(User user, String sessionId, UUID cartItemId) {
        Cart cart = findCart(user, sessionId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem item = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item is not in cart"));
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clear(User user, String sessionId) {
        Cart cart = findCart(user, sessionId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart is empty"));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    Cart resolveCart(User user, String sessionId) {
        if (user != null) {
            return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
                Cart cart = new Cart();
                cart.setUser(user);
                return cartRepository.save(cart);
            });
        }
        String effectiveSessionId = sessionId == null || sessionId.isBlank() ? UUID.randomUUID().toString() : sessionId;
        return cartRepository.findBySessionId(effectiveSessionId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setSessionId(effectiveSessionId);
            return cartRepository.save(cart);
        });
    }

    private java.util.Optional<Cart> findCart(User user, String sessionId) {
        if (user != null) {
            return cartRepository.findByUserId(user.getId());
        }
        return cartRepository.findBySessionId(sessionId);
    }

    private void validateProductSelection(Product product, List<CartDtos.SelectedAttribute> selectedAttributes) {
        if (!product.isInStock()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product not in stock");
        }
        List<ProductAttribute> productAttributes = productAttributeRepository.findByProductId(product.getId());
        boolean hasAttributes = !productAttributes.isEmpty();
        boolean hasSelected = selectedAttributes != null && !selectedAttributes.isEmpty();
        if (hasAttributes && !hasSelected) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No selected product attributes");
        }
        if (!hasAttributes && hasSelected) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Product does not have selected attributes");
        }
        if (!hasSelected) {
            return;
        }

        Set<UUID> seen = new HashSet<>();
        for (CartDtos.SelectedAttribute selected : selectedAttributes) {
            if (!seen.add(selected.attributeId)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Can not have multiple selections of same the attribute");
            }
            ProductAttribute productAttribute = productAttributeRepository
                    .findByProductIdAndAttributeId(product.getId(), selected.attributeId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attribute with ID " + selected.attributeId + " not found in product"));
            if (!attributeValueRepository.existsByProductAttributeIdAndValue(productAttribute.getId(), selected.value)) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Selected attribute value " + selected.value + " does not exist on product");
            }
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid selected attributes");
        }
    }
}
