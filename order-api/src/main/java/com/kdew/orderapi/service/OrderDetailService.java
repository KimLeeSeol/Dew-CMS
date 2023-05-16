package com.kdew.orderapi.service;

import com.kdew.orderapi.client.UserClient;
import com.kdew.orderapi.client.user.ChangeBalanceForm;
import com.kdew.orderapi.client.user.CustomerDto;
import com.kdew.orderapi.exception.CustomException;
import com.kdew.orderapi.exception.ErrorCode;
import com.kdew.orderapi.model.ProductItem;
import com.kdew.orderapi.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    // 결제를 위해 필요한 것
    // 1번 : 물건들이 전부 주문 가능한 상태인지 확인
    // 2번 : 가격 변동이 있었는지에 대해 확인
    // 3번 : 고객의 돈이 충분한지
    // 4번 : 결제 & 상품의 재고 관리

    private final CartDetailService cartDetailService;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    @Transactional
    public void order(String token, Cart cart) {

        // 1번 : 물건들이 전부 주문 가능한 상태인지 확인
        Cart orderCart = cartDetailService.refreshCart(cart);

        // 2번 : 가격 변동이 있었는지에 대해 확인
        if (orderCart.getMessages().size()>0) {
            //문제가 있음
            throw new CustomException(ErrorCode.ORDER_FAIL_CHECK_CART);
        }

        // 3번 : 고객의 돈이 충분한지
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);
        if(customerDto.getBalance() < getTotalPrice(cart)) {
            throw new CustomException(ErrorCode.ORDER_FAIL_NO_MONEY);
        }

        // 돈 바꾸기
        userClient.chanageBalance(token, ChangeBalanceForm.builder()
                .from("USER")
                .message("Order")
                .money(-totalPrice)
                .build());

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount()-cartItem.getCount());
            }
        }
    }

    private Integer getTotalPrice(Cart cart) {

        return cart.getProducts().stream()
                .flatMapToInt(product -> product.getItems().stream()
                        .flatMapToInt(productItem ->
                                IntStream.of(
                                        productItem.getPrice() * productItem.getCount())))
                .sum();
    }


}
