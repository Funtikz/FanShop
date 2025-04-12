package org.example.fanshop.controller;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.service.impl.CartServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
@Data
public class CartController {
    private final CartServiceImpl cartService;

    @Transactional
    @PostMapping("add-to-cart/{quantity}")
    public ResponseEntity<String> addToCart(@RequestBody ProductResponseDto responseDto,
                                    @PathVariable("quantity") Integer quantity){
        cartService.addItemToCart(responseDto, quantity);
        return ResponseEntity.ok("Успешно добавлено в корзину");
    }
}
