package com.kdew.orderapi.repository;

import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
}
