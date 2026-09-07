package com.mrdiy.web;

import com.mrdiy.common.ApiException;
import com.mrdiy.common.ApiResponse;
import com.mrdiy.domain.DeliveryStatus;
import com.mrdiy.domain.Order;
import com.mrdiy.domain.OrderChannel;
import com.mrdiy.domain.PaymentStatus;
import com.mrdiy.domain.ReturnedItem;
import com.mrdiy.domain.Transaction;
import com.mrdiy.domain.User;
import com.mrdiy.dto.SimpleDtos;
import com.mrdiy.security.CurrentUser;
import com.mrdiy.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/order/create-order")
    public ApiResponse<Transaction> createOrder(@RequestBody SimpleDtos.CreateOrderRequest request) {
        return ApiResponse.ok("Order created", orderService.createOrder(user(), request.deliveryAddressId));
    }

    @GetMapping("/user/order/view-user-orders")
    public ApiResponse<List<Order>> userOrders() {
        return ApiResponse.ok("Orders fetched", orderService.userOrders(user()));
    }

    @PostMapping("/user/order/test-order-payment")
    public ApiResponse<Void> markPaid(@RequestParam String transactionRef, @RequestParam BigDecimal amountPaid) {
        orderService.markPaymentSuccessful(transactionRef, amountPaid);
        return ApiResponse.ok("Payment status updated", null);
    }

    @GetMapping("/admin/order/get-all-order-delivery-status")
    public ApiResponse<List<DeliveryStatus>> deliveryStatuses() {
        return ApiResponse.ok("Delivery statuses fetched", Arrays.asList(DeliveryStatus.values()));
    }

    @GetMapping("/admin/order/get-all-order-channels")
    public ApiResponse<List<OrderChannel>> channels() {
        return ApiResponse.ok("Order channels fetched", Arrays.asList(OrderChannel.values()));
    }

    @GetMapping("/admin/order/get-all-order-payment-status")
    public ApiResponse<List<PaymentStatus>> paymentStatuses() {
        return ApiResponse.ok("Payment statuses fetched", Arrays.asList(PaymentStatus.values()));
    }

    @PutMapping("/admin/order/update-order-delivery-status/{id}")
    public ApiResponse<Void> updateDeliveryStatus(@PathVariable UUID id, @RequestBody SimpleDtos.StatusRequest request) {
        orderService.updateOrderDeliveryStatus(user(), id, DeliveryStatus.valueOf(request.status.toUpperCase().replace(' ', '_').replace('-', '_')));
        return ApiResponse.ok("Delivery status updated", null);
    }

    @PostMapping("/admin/order/return-items/{orderId}")
    public ApiResponse<ReturnedItem> returnItem(@PathVariable UUID orderId, @RequestBody SimpleDtos.ReturnItemRequest request) {
        return ApiResponse.ok("Returned item created", orderService.returnItem(orderId, request.orderItemId));
    }

    @PutMapping("/admin/order/returned-items/{id}/sell")
    public ApiResponse<ReturnedItem> sellReturnedItem(@PathVariable UUID id) {
        return ApiResponse.ok("Returned item sold", orderService.sellReturnedItem(user(), id));
    }

    private User user() {
        if (CurrentUser.get() == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        return CurrentUser.get();
    }
}
