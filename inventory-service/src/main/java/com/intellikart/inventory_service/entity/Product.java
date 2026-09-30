package com.intellikart.inventory_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String productId;

    private String name;
    private String description;
    private Integer availableQuantity;
}
