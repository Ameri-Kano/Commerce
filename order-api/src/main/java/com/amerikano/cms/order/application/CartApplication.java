package com.amerikano.cms.order.application;

import com.amerikano.cms.order.domain.model.Product;
import com.amerikano.cms.order.domain.model.ProductItem;
import com.amerikano.cms.order.domain.product.AddProductCartForm;
import com.amerikano.cms.order.domain.redis.Cart;
import com.amerikano.cms.order.exception.CustomException;
import com.amerikano.cms.order.exception.ErrorCode;
import com.amerikano.cms.order.service.CartService;
import com.amerikano.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplication {

    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {

        Product product = productSearchService.getByProductId(form.getId());

        if (product == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);

        if (!addAble(cart, product, form)) {
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);
    }


    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));
        cartService.putCart(cart.getCustomerId(), cart);
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        // 3. 메세지를 확인한 뒤에는 이미 확인한 메세지는 필요없으므로 제거
        cart.setMessages(new ArrayList<>());
        // 메세지가 없는 카트
        cartService.putCart(customerId, cart);
        return returnCart;
    }

    public void clearCart(Long customerId) {
        cartService.putCart(customerId, null);
    }

    public Cart updateCart(Long customerId, Cart cart) {
        // 실질적으로 변하는 데이터
        // 상품의 삭제, 수량 변경
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }

    /**
     * 1. 장바구니에 상품 추가
     * 2. 상품의 가격이나 수량 변동
     * 하는 경우를 위해 장바구니 목록 새로고침
     */
    protected Cart refreshCart(Cart cart) {
        // 1. 상품이나 아이템의 정보가 변경되었는지 체크하고 알맞은 알람 제공
        // 2. 상품의 수량, 가격을 임의로 변경
        Map<Long, Product> productMap = productSearchService.getListByProductIds(
                        cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0; i < cart.getProducts().size(); i++) {
            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());
            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            // 아이템 1, 2, 3
            List<String> tempMessages = new ArrayList<>();
            for (int j = 0; j < cartProduct.getItems().size(); j++) {
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());
                if (pi == null) {
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tempMessages.add(cartProductItem.getName() + " 옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChanged = false, isCountNotEnough = false;

                if (!cartProductItem.getPrice()
                        .equals(pi.getPrice())) {
                    isPriceChanged = true;
                    cartProductItem.setPrice(pi.getPrice());
                }
                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }

                if (isPriceChanged && isCountNotEnough) {
                    tempMessages.add(cartProductItem.getName() + " 가격변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChanged) {
                    tempMessages.add(cartProductItem.getName() + " 가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    tempMessages.add(cartProductItem.getName() + " 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }

            if (cartProduct.getItems().size() == 0) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");

            } else if (tempMessages.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(cartProduct.getName() + " 상품의 변동 사항 : ");
                for (String message : tempMessages) {
                    stringBuilder.append(message);
                    stringBuilder.append(", ");
                }

                if (tempMessages.size() == 1) {
                    stringBuilder.setLength(stringBuilder.length() - 1);
                }
                cart.addMessage(stringBuilder.toString());
            }
        }
        return cart;
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
        Cart.Product cartProduct = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId()))
                .findFirst().orElse(Cart.Product.builder().id(product.getId())
                        .items(Collections.emptyList()).build());

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));
        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    if (cartCount == null) {
                        cartCount = 0;
                    }
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount() + cartCount > currentCount;
                });
    }
}
