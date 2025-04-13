package org.example.fanshop.controller;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.CartItemRemoveRequest;
import org.example.fanshop.dto.CartItemResponseDto;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.entity.enums.ClothingSize;
import org.example.fanshop.service.impl.CartServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
@Data
public class CartController {
    private final CartServiceImpl cartService;

    @Transactional
    @PostMapping("add-to-cart/{quantity}/{clothingSize}")
    public ResponseEntity<String> addToCart(@RequestBody ProductResponseDto responseDto,
                                    @PathVariable("quantity") Integer quantity,
                                            @PathVariable("clothingSize") ClothingSize clothingSize){
        cartService.addItemToCart(responseDto, quantity, clothingSize);
        return ResponseEntity.ok("Успешно добавлено в корзину");
    }

    @GetMapping("get-my-items")
    public ResponseEntity<List<CartItemResponseDto>> getMyCart(){
        return new ResponseEntity<>(cartService.getCartItems(), HttpStatus.OK);
    }
    @GetMapping("get-cart-total-price")
    public ResponseEntity<Double> getCartPrice(){
        return new ResponseEntity<>(cartService.getCartPrice(), HttpStatus.OK);
    }

    // Эндпоинт для удаления товара или уменьшения его количества
    @PostMapping("/change-quantity")
    public ResponseEntity<String> removeItemFromCart(@RequestBody CartItemRemoveRequest request) {
        cartService.changeItemFromCart(request.getProductId(), request.getQuantity(), request.getClothingSize());
        return ResponseEntity.ok("Товар успешно удален или уменьшен из корзины");
    }
}
