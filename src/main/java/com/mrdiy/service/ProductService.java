package com.mrdiy.service;

import com.mrdiy.common.ApiException;
import com.mrdiy.common.Slugger;
import com.mrdiy.domain.AttributeDefinition;
import com.mrdiy.domain.AttributeValue;
import com.mrdiy.domain.Product;
import com.mrdiy.domain.ProductAttribute;
import com.mrdiy.domain.ProductTag;
import com.mrdiy.dto.ProductDtos;
import com.mrdiy.repository.AttributeDefinitionRepository;
import com.mrdiy.repository.AttributeValueRepository;
import com.mrdiy.repository.BrandRepository;
import com.mrdiy.repository.CategoryRepository;
import com.mrdiy.repository.ProductAttributeRepository;
import com.mrdiy.repository.ProductRepository;
import com.mrdiy.repository.ProductTagRepository;
import com.mrdiy.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final AttributeDefinitionRepository attributeRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final TagRepository tagRepository;
    private final ProductTagRepository productTagRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository,
                          CategoryRepository categoryRepository, AttributeDefinitionRepository attributeRepository,
                          ProductAttributeRepository productAttributeRepository,
                          AttributeValueRepository attributeValueRepository, TagRepository tagRepository,
                          ProductTagRepository productTagRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.tagRepository = tagRepository;
        this.productTagRepository = productTagRepository;
    }

    public Page<Product> getProducts(ProductDtos.ProductSearch search, Pageable pageable) {
        Sort sort = Sort.unsorted();
        if ("low_to_high".equals(search.pricing)) {
            sort = Sort.by("listingPrice").ascending();
        } else if ("high_to_low".equals(search.pricing)) {
            sort = Sort.by("listingPrice").descending();
        }
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return productRepository.search(search.categoryId, search.search, sorted);
    }

    public Product getProduct(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Transactional
    public Product create(ProductDtos.ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        product.setSlug(uniqueSlug(request.name));
        Product saved = productRepository.save(product);

        for (ProductDtos.AttributeSelection selection : request.attributes) {
            AttributeDefinition attribute = attributeRepository.findById(selection.attributeId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attribute not found"));
            ProductAttribute productAttribute = productAttributeRepository
                    .findByProductIdAndAttributeId(saved.getId(), attribute.getId())
                    .orElseGet(() -> {
                        ProductAttribute next = new ProductAttribute();
                        next.setProduct(saved);
                        next.setAttribute(attribute);
                        return productAttributeRepository.save(next);
                    });
            AttributeValue value = new AttributeValue();
            value.setProductAttribute(productAttribute);
            value.setValue(selection.value);
            value.setImgs(selection.imgs);
            attributeValueRepository.save(value);
        }

        for (UUID tagId : request.tags) {
            ProductTag productTag = new ProductTag();
            productTag.setProduct(saved);
            productTag.setTag(tagRepository.findById(tagId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tag not found")));
            productTagRepository.save(productTag);
        }
        return saved;
    }

    private void apply(Product product, ProductDtos.ProductRequest request) {
        product.setName(request.name);
        product.setBrand(request.brandId == null ? null : brandRepository.findById(request.brandId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Brand not found")));
        product.setCategory(request.categoryId == null ? null : categoryRepository.findById(request.categoryId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found")));
        product.setModel(request.model);
        product.setSku(request.sku);
        product.setMinSellingPrice(request.minSellingPrice);
        product.setListingPrice(request.listingPrice);
        product.setDiscountPrice(request.discountPrice);
        product.setWarranty(request.warranty);
        product.setShortDescription(request.shortDescription);
        product.setLongDescription(request.longDescription);
        product.setWhatsInTheBox(request.whatsInTheBox);
        product.setOnDiscount(request.onDiscount);
        product.setInStock(request.inStock);
        product.setStockItem(request.stockItem);
        product.setStatus(request.status);
        product.setImgs(request.imgs);
    }

    private String uniqueSlug(String name) {
        String base = Slugger.slug(name);
        String slug = base;
        int count = 2;
        while (productRepository.existsBySlug(slug)) {
            slug = base + "-" + count++;
        }
        return slug;
    }
}
