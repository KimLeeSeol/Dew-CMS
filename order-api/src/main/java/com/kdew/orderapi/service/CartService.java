package com.kdew.orderapi.service;

import com.kdew.orderapi.client.RedisClient;
import com.kdew.orderapi.product.AddProductCartForm;
import com.kdew.orderapi.redis.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final RedisClient redisClient;

    // 상품이 부족한데 추가하면 안되니까
    public Cart getCart(Long customerId) {
        Cart cart = redisClient.get(customerId, Cart.class);
        // 장바구니가 비어있으면!
        return cart!=null?cart:new Cart();
    }

    public Cart putCart(Long cusotmerId, Cart cart) {
        redisClient.put(cusotmerId, cart);
        return cart;
    }

    public Cart addCart(Long customerId, AddProductCartForm form) {
        Cart cart = redisClient.get(customerId, Cart.class);
        if (cart == null) {
            // 장바구니가 없으면 새로운 카트를 만들어줄 것임
            cart = new Cart();
            cart.setCustomerId(customerId);
        }
        // 상품을 추가하려고 할 때 고려할 것
        // 이미 같은 상품이 있는지
        Optional<Cart.Product> productOptional = cart.getProducts().stream()
                .filter(product1 -> product1.getId().equals(form.getId()))
                .findFirst();

        if (productOptional.isPresent()) {
            Cart.Product redisProduct = productOptional.get();
            List<Cart.ProductItem> items = form.getItems().stream().map(Cart.ProductItem::from).collect(Collectors.toList()); // 카트 안에 있는 아이템들 먼저 확인

            // redis에서 가져와야할 아이템
            Map<Long, Cart.ProductItem> redisItemMap = redisProduct.getItems().stream()
                    .collect(Collectors.toMap(Cart.ProductItem::getId, it->it)); // 검색속도 빠르게 하기 위해서 Map으로

            if (!redisProduct.getName().equals(form.getName())) {
                cart.addMessage(redisProduct.getName()+"의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }
            for (Cart.ProductItem item : items) {
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                if (redisItem == null) {
                    // null이면 그냥 추가 해주면 됨!!
                    redisProduct.getItems().add(item);
                }
                else {
                    if (!redisItem.getPrice().equals(item.getPrice())) {
                        // 가격이 만약 변경되었다면
                        cart.addMessage(redisProduct.getName()+item.getName()+"의 정보가 변경되었습니다. 확인 부탁드립니다.");
                    }
                    redisItem.setCount(redisItem.getCount()+item.getCount());
                }
            }
        }
        else {
            // 만약 같은 상품이 없으면
            Cart.Product product = Cart.Product.from(form);
            cart.getProducts().add(product); // 추가
        }
        redisClient.put(customerId,cart);// 해당 고객에게 추가
        return cart;
    }
}
