package com.kdew.orderapi.controller;

import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.orderapi.product.AddProductCartForm;
import com.kdew.orderapi.redis.Cart;
import com.kdew.orderapi.service.CartDetailService;
import com.kdew.orderapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CartDetailService cartDetailService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartForm form) {
        return ResponseEntity.ok(cartDetailService.addCart(provider.getUserVo(token).getId(), form));
    }

    @GetMapping
    public ResponseEntity<Cart> showCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token) {
        return ResponseEntity.ok(cartDetailService.getCart(provider.getUserVo(token).getId()));
    }
}
