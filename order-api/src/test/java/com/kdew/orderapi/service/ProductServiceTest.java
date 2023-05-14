package com.kdew.orderapi.service;

import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.product.AddProductForm;
import com.kdew.orderapi.product.AddProductItemForm;
import com.kdew.orderapi.repository.ProductItemRepository;
import com.kdew.orderapi.repository.ProductRepository;
import com.mysql.cj.log.Log;
import org.hibernate.envers.Audited;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void addproduct(){
        //given
        Long sellerId = 1L;

        //when
        AddProductForm form = makeProductForm("아디다스 슈퍼스타", "검흰 신발입니다.", 3);
        Product p = productService.addProduct(sellerId,form);
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        //then
        assertNotNull(result);
        assertEquals(result.getName(), "아디다스 슈퍼스타");
        assertEquals(result.getDescription(), "신발");
        assertEquals(result.getProductItems().size(),3);
        assertEquals(result.getProductItems().get(0).getName(), "아디다스 슈퍼스타0");
        assertEquals(result.getProductItems().get(0).getPrice(), 10000);
        assertEquals(result.getProductItems().get(0).getCount(), 1);

    }

    // 아이템들 다 다른이름으로 생성
    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name + i));
        }

        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();
    }


}