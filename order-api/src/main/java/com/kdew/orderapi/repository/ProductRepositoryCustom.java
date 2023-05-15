package com.kdew.orderapi.repository;

import com.kdew.orderapi.model.Product;

import java.util.List;


public interface ProductRepositoryCustom {
    List<Product> searchByName(String name);
}