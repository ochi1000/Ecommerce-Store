package com.mrdiy.dto;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public final class SimpleDtos {
    private SimpleDtos() {
    }

    public static class NameRequest {
        @NotBlank
        public String name;
        public String description;
        public UUID parentId;
        public boolean hasImages;
        public boolean status = true;
    }

    public static class FavoriteRequest {
        public UUID productId;
    }

    public static class DeliveryAddressRequest {
        @NotBlank
        public String address;
        public String city;
        @NotBlank
        public String state;
    }

    public static class CreateOrderRequest {
        public UUID deliveryAddressId;
    }

    public static class StatusRequest {
        @NotBlank
        public String status;
    }

    public static class ReturnItemRequest {
        public UUID orderItemId;
    }
}
