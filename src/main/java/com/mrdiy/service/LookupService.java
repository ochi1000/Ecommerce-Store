package com.mrdiy.service;

import com.mrdiy.common.ApiException;
import com.mrdiy.domain.AttributeDefinition;
import com.mrdiy.domain.Brand;
import com.mrdiy.domain.Category;
import com.mrdiy.domain.Tag;
import com.mrdiy.dto.SimpleDtos;
import com.mrdiy.repository.AttributeDefinitionRepository;
import com.mrdiy.repository.BrandRepository;
import com.mrdiy.repository.CategoryRepository;
import com.mrdiy.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LookupService {
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final AttributeDefinitionRepository attributeRepository;

    public LookupService(CategoryRepository categoryRepository, BrandRepository brandRepository,
                         TagRepository tagRepository, AttributeDefinitionRepository attributeRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.tagRepository = tagRepository;
        this.attributeRepository = attributeRepository;
    }

    public Page<Category> categories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category category(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    public Category saveCategory(SimpleDtos.NameRequest request) {
        Category category = new Category();
        category.setName(request.name);
        category.setDescription(request.description);
        category.setParentId(request.parentId);
        category.setStatus(request.status);
        return categoryRepository.save(category);
    }

    public Brand saveBrand(SimpleDtos.NameRequest request) {
        Brand brand = new Brand();
        brand.setName(request.name);
        brand.setStatus(request.status);
        return brandRepository.save(brand);
    }

    public Tag saveTag(SimpleDtos.NameRequest request) {
        Tag tag = new Tag();
        tag.setName(request.name);
        return tagRepository.save(tag);
    }

    public AttributeDefinition saveAttribute(SimpleDtos.NameRequest request) {
        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setName(request.name);
        attribute.setHasImages(request.hasImages);
        return attributeRepository.save(attribute);
    }
}
