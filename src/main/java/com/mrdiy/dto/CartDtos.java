package com.mrdiy.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CartDtos {
    private CartDtos() {
    }

    public static class AddToCartRequest {
        @NotNull
        public UUID productId;
        @Min(1)
        public int quantity = 1;
        public List<SelectedAttribute> selectedAttributes = new ArrayList<>();
    }

    public static class SelectedAttribute {
        public UUID attributeId;
        public String value;
    }

    public static class UpdateQuantityRequest {
        @NotNull
        public UUID cartItemId;
        @Min(1)
        public int quantity;
    }

    public static class RemoveCartItemRequest {
        @NotNull
        public UUID cartItemId;
    }
}
