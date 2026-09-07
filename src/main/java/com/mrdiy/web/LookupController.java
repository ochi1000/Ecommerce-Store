package com.mrdiy.web;

import com.mrdiy.common.ApiResponse;
import com.mrdiy.domain.AttributeDefinition;
import com.mrdiy.domain.Brand;
import com.mrdiy.domain.Category;
import com.mrdiy.domain.Tag;
import com.mrdiy.dto.SimpleDtos;
import com.mrdiy.service.LookupService;
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
public class LookupController {
    private final LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/category/get-all-categories")
    public ApiResponse<Page<Category>> categories(Pageable pageable) {
        return ApiResponse.ok("Categories fetched", lookupService.categories(pageable));
    }

    @GetMapping("/category/view-category/{id}")
    public ApiResponse<Category> category(@PathVariable UUID id) {
        return ApiResponse.ok("Category fetched", lookupService.category(id));
    }

    @PostMapping("/admin/category")
    public ApiResponse<Category> createCategory(@RequestBody SimpleDtos.NameRequest request) {
        return ApiResponse.ok("Category created", lookupService.saveCategory(request));
    }

    @PostMapping("/admin/brand")
    public ApiResponse<Brand> createBrand(@RequestBody SimpleDtos.NameRequest request) {
        return ApiResponse.ok("Brand created", lookupService.saveBrand(request));
    }

    @PostMapping("/admin/tag")
    public ApiResponse<Tag> createTag(@RequestBody SimpleDtos.NameRequest request) {
        return ApiResponse.ok("Tag created", lookupService.saveTag(request));
    }

    @PostMapping("/admin/attribute")
    public ApiResponse<AttributeDefinition> createAttribute(@RequestBody SimpleDtos.NameRequest request) {
        return ApiResponse.ok("Attribute created", lookupService.saveAttribute(request));
    }
}
