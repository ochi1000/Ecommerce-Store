package com.mrdiy.web;

import com.mrdiy.common.ApiException;
import com.mrdiy.common.ApiResponse;
import com.mrdiy.domain.DeliveryAddress;
import com.mrdiy.domain.Favorite;
import com.mrdiy.domain.User;
import com.mrdiy.dto.SimpleDtos;
import com.mrdiy.security.CurrentUser;
import com.mrdiy.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/favorite/add-to-favorites")
    public ApiResponse<Favorite> addFavorite(@RequestBody SimpleDtos.FavoriteRequest request) {
        return ApiResponse.ok("Favorite created", customerService.addFavorite(user(), request.productId));
    }

    @GetMapping("/favorite/view-favorites")
    public ApiResponse<List<Favorite>> favorites() {
        return ApiResponse.ok("Favorites fetched", customerService.favorites(user()));
    }

    @DeleteMapping("/favorite/remove-from-favorites")
    public ApiResponse<Void> removeFavorite(@RequestBody SimpleDtos.FavoriteRequest request) {
        customerService.removeFavorite(user(), request.productId);
        return ApiResponse.ok("Favorite removed", null);
    }

    @PostMapping("/delivery-address")
    public ApiResponse<DeliveryAddress> createAddress(@RequestBody SimpleDtos.DeliveryAddressRequest request) {
        return ApiResponse.ok("Delivery address created", customerService.createAddress(user(), request));
    }

    @GetMapping("/delivery-address")
    public ApiResponse<List<DeliveryAddress>> addresses() {
        return ApiResponse.ok("Delivery addresses fetched", customerService.addresses(user()));
    }

    @PutMapping("/delivery-address/{id}")
    public ApiResponse<DeliveryAddress> updateAddress(@PathVariable UUID id, @RequestBody SimpleDtos.DeliveryAddressRequest request) {
        return ApiResponse.ok("Delivery address updated", customerService.updateAddress(user(), id, request));
    }

    @DeleteMapping("/delivery-address/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable UUID id) {
        customerService.deleteAddress(user(), id);
        return ApiResponse.ok("Delivery address deleted", null);
    }

    private User user() {
        if (CurrentUser.get() == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        return CurrentUser.get();
    }
}
