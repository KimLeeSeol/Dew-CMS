package com.kdew.orderapi.service;

import com.kdew.orderapi.exception.CustomException;
import com.kdew.orderapi.exception.ErrorCode;
import com.kdew.orderapi.model.Product;
import com.kdew.orderapi.model.ProductItem;
import com.kdew.orderapi.product.AddProductCartForm;
import com.kdew.orderapi.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    // response는 메세지가 나가고 redis에는 없는걸로 저장이 되어야 함
    // 메세지를 본 다음에는, 이미 본 메세지는 스팸이 되기 때문에 제거함
    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>()); // 메세지에 빈 ArrayList 넣기

        //메세지 없는 것
        cartService.putCart(customerId, cart);
        return returnCart;
    }

    public void clearCart(Long customerId) {
        cartService.putCart(customerId, null);
    }

    public Cart refreshCart(Cart cart) {
        // 1. 상품이나 상품의 아이템 정보, 가격, 수량이 변동되었는지 체크
        // 변동 되었으면 알람
        // 2. 상품의 수량, 가격을 우리가 임의로 변경
        Map<Long, Product> productMap =
                productSearchService.getListByProductIds(cart.getProducts().stream()
                                .map(Cart.Product::getId).collect(Collectors.toList()))
                                .stream()
                                .collect(Collectors.toMap(Product::getId, product -> product));


        // 카트에서 해당 상품을 제거하려고 하는데
        // 해당 상품을 제거했을 때 for문이 정상적으로 동작하지 않을 수 있음
        // 그래서 아이템에 변화가 있을 때 명시적으로 인덱스를 태우면서 가야함
        for (int i = 0; i < cart.getProducts().size(); i++) {

            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());
            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            // 각각 케이스별로 에러를 쪼개고, 에러가 정상 출력 되야하는지 체크
            List<String> tmpMessages = new ArrayList<>();

            for (int j = 0; j < cartProduct.getItems().size(); j++) {

                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if (pi == null) {
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tmpMessages.add(cartProductItem.getName() + " 옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChanges = false, isCountNotEnough = false; // 아이템의 가격도 바뀌고, 수량도 부족하면 메시지를 두줄로 표현해야하는데, 이렇게 변수를 쓰면


                if (!cartProductItem.getPrice().equals(pi.getPrice())) {
                    isPriceChanges = true;
                    cartProductItem.setPrice(pi.getPrice()); // 변동이 되었으면 카트의 정보를 변경을 해줘야 함
                }
                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }
                if(isPriceChanges && isCountNotEnough) {
                    // message 1
                    tmpMessages.add(cartProductItem.getName() + " 가격변동과 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChanges) {
                    // message 2
                    tmpMessages.add(cartProductItem.getName() + " 가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    // message 3
                    tmpMessages.add(cartProductItem.getName() + " 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }
            // 아이템 1, 2가 있었는데 둘다 없어졌으면, 프로덕트가 없지만 아이템들이 아무것도 없어서 사용 못함 그래서 카트에서 삭제를 해주는게 맞음
            if(cartProduct.getItems().size() == 0) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;
            }
            // 품절이 아니라 상품이 아예 사라지는 경우 장바구니 상품 제거
            else if (tmpMessages.size() > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + " 상품의 변동 사항 : ");
                for (String message : tmpMessages) {
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;
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
