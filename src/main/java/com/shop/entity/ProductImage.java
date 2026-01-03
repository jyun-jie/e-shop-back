package com.shop.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_image")
public class ProductImage {
    private int id;
    private int productId;
    private String imageUrl;
    private int sort_order;
    private LocalDateTime create_at;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    @Enumerated(EnumType.STRING)
    private ImageType image_type;



    public ProductImage(int productId, String imageUrl, int sort_order) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.sort_order = sort_order;
    }
}
