package com.intellikart.inventory_service.service;

import com.intellikart.inventory_service.converter.ProductConverter;
import com.intellikart.inventory_service.dto.CreateProductRequest;
import com.intellikart.inventory_service.dto.ProductResponse;
import com.intellikart.inventory_service.entity.Product;
import com.intellikart.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productConverter.toEntity(request);
        Product saved = productRepository.save(product);

        // Elasticsearch sync disabled locally to save resources — uncomment when ES is running
        // ProductDocument document = productConverter.toDocument(saved);
        // productSearchRepository.save(document);

        return productConverter.toResponse(saved);
    }

    // Disabled along with Elasticsearch — uncomment when ES is running, and restore
    // the productSearchRepository field above
    /*
    public List<ProductResponse> searchProducts(String query) {
        return productSearchRepository.findByNameContainingIgnoreCase(query).stream()
                .map(doc -> {
                    Product product = productRepository.findById(doc.getProductId()).orElseThrow();
                    return productConverter.toResponse(product);
                })
                .toList();
    }
    */
}
