package com.kdew.orderapi.service;

import com.kdew.orderapi.exception.CustomException;
import com.kdew.orderapi.exception.ErrorCode;
import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.model.ProductItem;
import com.kdew.orderapi.product.AddProductCartForm;
import com.kdew.orderapi.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartDetailService {
    // 상품이 사라질수도 있기 때문..
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {

        // 상품이 있는지 확인
        Product product = productSearchService.getByProductId(form.getId());
        if(product==null) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }

        Cart cart = cartService.getCart(customerId); // customerId의 카트를 가져오고

        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGHE);
        }

        return cartService.addCart(customerId,form);
    }

    // 카트에 아이템이 추가가될 수 있는지 검사
    private boolean addAble (Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart
                        .ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount() + cartCount > currentCount;
                });
    }
}
