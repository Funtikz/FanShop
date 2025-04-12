package org.example.fanshop.repository;

import org.example.fanshop.entity.Product;
import org.example.fanshop.entity.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByProductType(ProductType productType);
}
