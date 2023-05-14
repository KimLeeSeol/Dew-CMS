package com.kdew.orderapi.service;

import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.product.AddProductForm;
import com.kdew.orderapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form) {
        return productRepository.save(Product.of(sellerId,form));
    }
}
