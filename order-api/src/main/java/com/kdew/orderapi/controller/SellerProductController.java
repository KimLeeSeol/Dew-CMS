package com.kdew.orderapi.controller;

import com.kdew.dewdomain.config.JwtAuthenticationProvider;
import com.kdew.orderapi.product.AddProductForm;
import com.kdew.orderapi.product.AddProductItemForm;
import com.kdew.orderapi.product.ProductDto;
import com.kdew.orderapi.service.ProductItemService;
import com.kdew.orderapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody AddProductForm form) {

        return ResponseEntity.ok(ProductDto.from(productService
                .addProduct(provider.getUserVo(token).getId(), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                     @RequestBody AddProductItemForm form) {

        return ResponseEntity.ok(ProductDto.from(productItemService
                .addProductItem(provider.getUserVo(token).getId(), form)));
    }
}
