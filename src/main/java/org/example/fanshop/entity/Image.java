package org.example.fanshop.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String minioId;

    private String originalFilename;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;
}