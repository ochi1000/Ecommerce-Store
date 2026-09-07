package com.mrdiy.service;

import com.mrdiy.domain.Cart;
import com.mrdiy.domain.CartItem;
import com.mrdiy.domain.DeliveryAddress;
import com.mrdiy.domain.PaymentStatus;
import com.mrdiy.domain.Product;
import com.mrdiy.domain.Role;
import com.mrdiy.domain.Transaction;
import com.mrdiy.domain.User;
import com.mrdiy.repository.CartItemRepository;
import com.mrdiy.repository.CartRepository;
import com.mrdiy.repository.DeliveryAddressRepository;
import com.mrdiy.repository.OrderRepository;
import com.mrdiy.repository.ProductRepository;
import com.mrdiy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void paymentSuccessMarksOrderItemsPaidAndClearsCart() {
        User user = user();
        Product product = product();
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(3);
        cartItemRepository.save(cartItem);
        DeliveryAddress address = new DeliveryAddress();
        address.setUser(user);
        address.setAddress("1 Main Street");
        address.setState("LA");
        deliveryAddressRepository.save(address);

        Transaction transaction = orderService.createOrder(user, address.getId());
        orderService.markPaymentSuccessful(transaction.getTransactionRef(), BigDecimal.valueOf(150));

        assertThat(orderRepository.findAll()).singleElement().extracting("paymentStatus").isEqualTo(PaymentStatus.PAID);
        assertThat(cartRepository.findByUserId(user.getId())).isEmpty();
    }

    private User user() {
        User user = new User();
        user.setEmail("buyer@example.com");
        user.setPassword("encoded");
        user.setRoles(Collections.singleton(Role.USER));
        return userRepository.save(user);
    }

    private Product product() {
        Product product = new Product();
        product.setName("Hammer");
        product.setSlug("hammer");
        product.setListingPrice(BigDecimal.valueOf(50));
        product.setMinSellingPrice(BigDecimal.valueOf(45));
        return productRepository.save(product);
    }
}
