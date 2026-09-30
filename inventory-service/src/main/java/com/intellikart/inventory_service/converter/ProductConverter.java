package com.intellikart.inventory_service.converter;

import com.intellikart.inventory_service.document.ProductDocument;
import com.intellikart.inventory_service.dto.CreateProductRequest;
import com.intellikart.inventory_service.dto.ProductResponse;
import com.intellikart.inventory_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    public Product toEntity(CreateProductRequest request) {
        return Product.builder()
                .productId(request.productId())
                .name(request.name())
                .description(request.description())
                .availableQuantity(request.availableQuantity())
                .build();
    }

    public ProductDocument toDocument(Product product) {
        return ProductDocument.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getAvailableQuantity()
        );
    }
}
