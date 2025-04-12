package org.example.fanshop.service.impl;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.entity.Cart;
import org.example.fanshop.entity.CartItem;
import org.example.fanshop.entity.Product;
import org.example.fanshop.entity.User;
import org.example.fanshop.repository.CartItemRepository;
import org.example.fanshop.repository.CartRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class CartServiceImpl {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final UserServiceImpl userService;
    private final ProductService productService;

    public void addItemToCart(ProductResponseDto responseDto, Integer quantity){
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

        if (existingCartItem.isPresent()) {
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
            cartItemRepository.save(cartItem);  // Сохраняем новый элемент корзины
        }

        // После добавления товара обновляем общую стоимость корзины
        updateCartTotalPrice(cart);
    }

    // Метод для обновления общей стоимости корзины
    public void updateCartTotalPrice(Cart cart) {
        double totalPrice = cart.getItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);  // Сохраняем изменения в корзине
    }


}
