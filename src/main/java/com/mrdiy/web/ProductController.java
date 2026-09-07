package com.mrdiy.web;

import com.mrdiy.common.ApiResponse;
import com.mrdiy.domain.Product;
import com.mrdiy.dto.ProductDtos;
import com.mrdiy.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/get-all-products")
    public ApiResponse<Page<Product>> index(ProductDtos.ProductSearch search, Pageable pageable) {
        return ApiResponse.ok("Products fetched", productService.getProducts(search, pageable));
    }

    @GetMapping("/product/view-product/{id}")
    public ApiResponse<Product> show(@PathVariable UUID id) {
        return ApiResponse.ok("Product fetched", productService.getProduct(id));
    }

    @PostMapping("/admin/product")
    public ApiResponse<Product> store(@RequestBody ProductDtos.ProductRequest request) {
        return ApiResponse.ok("Product created", productService.create(request));
    }
}
