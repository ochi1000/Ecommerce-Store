package com.mrdiy.service;

import com.mrdiy.common.ApiException;
import com.mrdiy.domain.Cart;
import com.mrdiy.domain.CartItem;
import com.mrdiy.domain.DeliveryAddress;
import com.mrdiy.domain.DeliveryStatus;
import com.mrdiy.domain.Order;
import com.mrdiy.domain.OrderItem;
import com.mrdiy.domain.OrderUpdateHistory;
import com.mrdiy.domain.PaymentStatus;
import com.mrdiy.domain.ReturnedItem;
import com.mrdiy.domain.Transaction;
import com.mrdiy.domain.User;
import com.mrdiy.repository.CartItemRepository;
import com.mrdiy.repository.CartRepository;
import com.mrdiy.repository.DeliveryAddressRepository;
import com.mrdiy.repository.OrderItemRepository;
import com.mrdiy.repository.OrderRepository;
import com.mrdiy.repository.OrderUpdateHistoryRepository;
import com.mrdiy.repository.ReturnedItemRepository;
import com.mrdiy.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionRepository transactionRepository;
    private final OrderUpdateHistoryRepository historyRepository;
    private final ReturnedItemRepository returnedItemRepository;

    public OrderService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                        DeliveryAddressRepository deliveryAddressRepository, OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository, TransactionRepository transactionRepository,
                        OrderUpdateHistoryRepository historyRepository, ReturnedItemRepository returnedItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.transactionRepository = transactionRepository;
        this.historyRepository = historyRepository;
        this.returnedItemRepository = returnedItemRepository;
    }

    @Transactional
    public Transaction createOrder(User user, UUID deliveryAddressId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Cart is empty"));
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByIdAndUserId(deliveryAddressId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Delivery address not found"));

        BigDecimal total = items.stream()
                .map(item -> item.getProduct().currentPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().currentPrice());
            orderItem.setSelectedAttributesJson(cartItem.getSelectedAttributesJson());
            orderItemRepository.save(orderItem);
        }

        Transaction transaction = new Transaction();
        transaction.setOrder(savedOrder);
        transaction.setUser(user);
        transaction.setAmount(total);
        transaction.setTransactionRef("MRDIY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        transaction.setPaymentGateway("paystack");
        return transactionRepository.save(transaction);
    }

    public List<Order> userOrders(User user) {
        return orderRepository.findByUserIdAndPaymentStatusNot(user.getId(), PaymentStatus.PENDING);
    }

    @Transactional
    public void markPaymentSuccessful(String transactionRef, BigDecimal amountPaid) {
        Transaction transaction = transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));
        Order order = transaction.getOrder();
        if (order.getTotal().compareTo(amountPaid) <= 0) {
            order.setPaymentStatus(PaymentStatus.PAID);
            orderRepository.save(order);
            for (OrderItem item : orderItemRepository.findByOrderId(order.getId())) {
                item.setPaymentStatus(PaymentStatus.PAID);
                orderItemRepository.save(item);
            }
            cartRepository.findByUserId(order.getUser().getId()).ifPresent(cart -> {
                cartItemRepository.deleteByCartId(cart.getId());
                cartRepository.delete(cart);
            });
            transaction.setAmountPaid(amountPaid);
            transaction.setPaymentStatus(PaymentStatus.PAID);
            transactionRepository.save(transaction);
        }
    }

    @Transactional
    public void updateOrderDeliveryStatus(User actor, UUID orderId, DeliveryStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setDeliveryStatus(status);
        orderRepository.save(order);
        for (OrderItem item : orderItemRepository.findByOrderId(orderId)) {
            item.setDeliveryStatus(status);
            orderItemRepository.save(item);
        }
        OrderUpdateHistory history = new OrderUpdateHistory();
        history.setOrder(order);
        history.setUser(actor);
        history.setStatus(status);
        historyRepository.save(history);
    }

    @Transactional
    public ReturnedItem returnItem(UUID orderId, UUID orderItemId) {
        OrderItem item = orderItemRepository.findByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order Item not found"));
        if (item.getPaymentStatus() != PaymentStatus.PAID) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Order Item has not been paid");
        }
        returnedItemRepository.findByOrderItemId(item.getId()).ifPresent(existing -> {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Order Item has already been returned");
        });
        ReturnedItem returnedItem = new ReturnedItem();
        returnedItem.setOrderItem(item);
        returnedItem.setProduct(item.getProduct());
        returnedItem.setSellingPrice(item.getPrice());
        return returnedItemRepository.save(returnedItem);
    }

    @Transactional
    public ReturnedItem sellReturnedItem(User user, UUID returnedItemId) {
        ReturnedItem returnedItem = returnedItemRepository.findById(returnedItemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Returned item not found"));
        if (returnedItem.isSold()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Returned item already sold");
        }
        returnedItem.setSold(true);
        returnedItem.setSoldDate(Instant.now());
        returnedItem.setSoldBy(user);
        return returnedItemRepository.save(returnedItem);
    }
}
