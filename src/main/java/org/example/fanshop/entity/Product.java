package org.example.fanshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.fanshop.entity.enums.ProductType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private Double originalPrice;
    private Double discountedPrice;

    @Column(nullable = true)
    private Integer discountPercentage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Image> images = new ArrayList<>();
}
