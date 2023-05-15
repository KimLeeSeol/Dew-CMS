package com.kdew.orderapi.service;

import com.kdew.orderapi.exception.CustomException;
import com.kdew.orderapi.exception.ErrorCode;
import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.model.ProductItem;
import com.kdew.orderapi.product.AddProductForm;
import com.kdew.orderapi.product.UpdateProductForm;
import com.kdew.orderapi.product.UpdateProductItemForm;
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

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form) {
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        product.setName(form.getName());
        product.setDescription(form.getDescription());

        for(UpdateProductItemForm itemForm : form.getItems()) {
            ProductItem productItem = product.getProductItems().stream()
                    .filter(pi->pi.getId().equals(itemForm.getId()))
                    .findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ITEM));
            productItem.setName(itemForm.getName());
            productItem.setPrice(itemForm.getPrice());
            productItem.setCount(itemForm.getCount());
        }

        return product;
    }
}
