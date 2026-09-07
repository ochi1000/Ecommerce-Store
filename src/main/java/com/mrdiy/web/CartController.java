package com.mrdiy.web;

import com.mrdiy.common.ApiResponse;
import com.mrdiy.domain.CartItem;
import com.mrdiy.dto.CartDtos;
import com.mrdiy.security.CurrentUser;
import com.mrdiy.service.CartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PutMapping("/add-to-cart")
    public ApiResponse<CartItem> add(@RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                     @Valid @RequestBody CartDtos.AddToCartRequest request) {
        return ApiResponse.ok("Item added to cart", cartService.addToCart(CurrentUser.get(), sessionId, request));
    }

    @GetMapping("/view-cart")
    public ApiResponse<List<CartItem>> view(@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        return ApiResponse.ok("Cart fetched", cartService.viewCart(CurrentUser.get(), sessionId));
    }

    @PutMapping("/update-cart-item-quantity")
    public ApiResponse<Void> update(@RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                    @Valid @RequestBody CartDtos.UpdateQuantityRequest request) {
        cartService.updateQuantity(CurrentUser.get(), sessionId, request);
        return ApiResponse.ok("Cart item updated", null);
    }

    @DeleteMapping("/remove-from-cart")
    public ApiResponse<Void> remove(@RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                    @RequestBody CartDtos.RemoveCartItemRequest request) {
        cartService.remove(CurrentUser.get(), sessionId, request.cartItemId);
        return ApiResponse.ok("Item removed from cart", null);
    }

    @DeleteMapping("/clear-cart")
    public ApiResponse<Void> clear(@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        cartService.clear(CurrentUser.get(), sessionId);
        return ApiResponse.ok("Cart cleared", null);
    }
}
