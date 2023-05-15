package com.kdew.orderapi.service;

import com.kdew.orderapi.exception.CustomException;
import com.kdew.orderapi.exception.ErrorCode;
import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    public List<Product> searchByName(String name) {
        return productRepository.searchByName(name);
    }

    // 아이템코드를 입력했을 때 아이템 페이지를 볼 수 있음
    public Product getByProductId(Long productId) {
        return productRepository.findWithProductItemsById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    //목록 보기
    public List<Product> getListByProductIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

}
