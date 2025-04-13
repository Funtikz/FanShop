package org.example.fanshop.service.impl;


import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.CartItemResponseDto;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.entity.*;
import org.example.fanshop.entity.enums.ClothingSize;
import org.example.fanshop.repository.CartItemRepository;
import org.example.fanshop.repository.CartRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class CartServiceImpl {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserServiceImpl userService;
    private final ProductService productService;

    public Double getCartPrice() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        // Получаем пользователя по номеру телефона
        User user = userService.findByPhoneNumber(phoneNumber);

        return cartItemRepository.findCartItemByCartId(user.getCart().getId()).stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }

    @Transactional
    public void changeItemFromCart(Long productId, Integer quantity, ClothingSize clothingSize) {
        // Получаем номер телефона аутентифицированного пользователя
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        // Получаем пользователя по номеру телефона
        User user = userService.findByPhoneNumber(phoneNumber);

        // Получаем корзину пользователя
        Cart cart = user.getCart();
        Product product = productService.findById(productId);

        // Находим товар в корзине по ID и размеру
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId) && cartItem.getClothingSize().equals(clothingSize))
                .findFirst();

        // Получаем вариант товара по размеру
        ProductVariant productVariant = product.getVariants().stream()
                .filter(variant -> variant.getSize().equals(clothingSize))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Размер товара не найден"));

        // Получаем доступное количество товара на складе
        int availableQuantity = productVariant.getQuantity();

        // Если товар найден
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();

            //Увеличиваем или уменьшаем количество товара в корзине
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > availableQuantity){
                throw new RuntimeException("Недостаточно товара на складе");
            }
            if (newQuantity > 0) {
                // Если количество товара больше нуля, просто обновляем количество и цену
                cartItem.setQuantity(newQuantity);
                cartItem.setPrice(cartItem.getProduct().getDiscountedPrice() * newQuantity);
                cartItemRepository.save(cartItem);  // Сохраняем изменения в корзине
            } else {
                // Если количество товара равно нулю или меньше, удаляем товар из корзины
                cart.getItems().remove(cartItem);
                cartItemRepository.delete(cartItem);  // Удаляем товар из корзины
            }

            // После изменения товара в корзине, обновляем общую стоимость корзины
            updateCartTotalPrice(cart);
        }
    }


    @Transactional
    public void addItemToCart(ProductResponseDto responseDto, Integer quantity, ClothingSize clothingSize){
        // Получаем продукт по ID
        Product product = productService.findById(responseDto.getId());

        // Получаем номер телефона аутентифицированного пользователя
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();

        // Получаем пользователя по номеру телефона
        User user = userService.findByPhoneNumber(phoneNumber);

        // Получаем корзину пользователя
        Cart cart = user.getCart();

        // Проверяем, есть ли уже этот товар в корзине
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                .findFirst();


        if (existingCartItem.isPresent()  && existingCartItem.get().getClothingSize().equals(clothingSize) ) {
            // Если товар есть в корзине, увеличиваем количество
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setPrice(cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity());
            cartItemRepository.save(cartItem);  // Сохраняем изменения в корзине
        } else {
            // Если товара нет в корзине, создаем новый элемент корзины
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getDiscountedPrice() * quantity);
            cartItem.setClothingSize(clothingSize);
            cartItemRepository.save(cartItem);  // Сохраняем новый элемент корзины
        }

        // После добавления товара обновляем общую стоимость корзины
        updateCartTotalPrice(cart);
    }

    @Transactional
    // Метод для обновления общей стоимости корзины
    public void updateCartTotalPrice(Cart cart) {
        double totalPrice = cart.getItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);  // Сохраняем изменения в корзине
    }

    public List<CartItemResponseDto> getCartItems(){
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByPhoneNumber(phoneNumber);
        Long cartId = user.getCart().getId();
        return cartItemRepository.findCartItemByCartId(cartId)
                .stream()
                .map(cartItem -> {
                    CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
                    cartItemResponseDto.setCartId(cartItem.getId());
                    cartItemResponseDto.setPrice(cartItem.getPrice());
                    cartItemResponseDto.setProductId(cartItem.getProduct().getId());
                    cartItemResponseDto.setId(cartItem.getId());
                    cartItemResponseDto.setQuantity(cartItem.getQuantity());
                    cartItemResponseDto.setClothingSize(cartItem.getClothingSize());
                    cartItemResponseDto.setName(cartItem.getProduct().getName());
                    return cartItemResponseDto;
                }).collect(Collectors.toList());
    }


}
