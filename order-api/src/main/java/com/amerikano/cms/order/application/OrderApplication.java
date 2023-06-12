package com.amerikano.cms.order.application;

import com.amerikano.cms.order.client.OrderMailClient;
import com.amerikano.cms.order.client.UserClient;
import com.amerikano.cms.order.client.user.ChangeBalanceForm;
import com.amerikano.cms.order.client.user.CustomerDto;
import com.amerikano.cms.order.domain.mailgun.OrderMailForm;
import com.amerikano.cms.order.domain.model.ProductItem;
import com.amerikano.cms.order.domain.redis.Cart;
import com.amerikano.cms.order.exception.CustomException;
import com.amerikano.cms.order.exception.ErrorCode;
import com.amerikano.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderApplication {

    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final OrderMailClient orderMailClient;
    private final ProductItemService productItemService;

    @Transactional
    public void order(String token, Cart cart) {
        Cart orderCart = cartApplication.refreshCart(cart);
        StringBuilder orderList = new StringBuilder("주문 내역\n");

        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ErrorCode.ORDER_FAIL_CHECK_CART);
        }

        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);
        if (customerDto.getBalance() < totalPrice) {
            throw new CustomException(ErrorCode.ORDER_FAIL_NOT_ENOUGH_MONEY);
        }

        // 롤백 고려 필요
        userClient.changeBalance(token,
                ChangeBalanceForm.builder()
                        .from("USER")
                        .message("ORDER")
                        .money(-totalPrice)
                        .build());

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                orderList.append(product.getName() + " | " + cartItem.getName() + " : "
                        + productItem.getCount() * productItem.getPrice() + "\n");
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }

        orderList.append(String.format("합계 : %d\n", totalPrice));
        orderList.append("이용해주셔서 감사합니다!");

        OrderMailForm form = OrderMailForm.builder()
                .from("order@gmail.com")
                .to(customerDto.getEmail())
                .subject("주문 내역 Email")
                .text(orderList.toString())
                .build();

        log.info(orderMailClient.sendEmail(form).getBody());
    }

    public Integer getTotalPrice(Cart cart) {

        return cart.getProducts().stream().flatMapToInt(
                        product -> product.getItems().stream().flatMapToInt(
                                productItem ->
                                        IntStream.of(productItem.getPrice() * productItem.getCount())))
                .sum();
    }
}
