package com.amerikano.cms.order.controller;

import com.amerikano.cms.order.application.CartApplication;
import com.amerikano.cms.order.application.OrderApplication;
import com.amerikano.cms.order.domain.product.AddProductCartForm;
import com.amerikano.cms.order.domain.redis.Cart;
import com.amerikano.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CartApplication cartApplication;
    private final OrderApplication orderApplication;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartForm form
    ) {
        return ResponseEntity.ok(
                cartApplication.addCart(provider.getUserVo(token).getId(), form)
        );
    }

    @GetMapping
    public ResponseEntity<Cart> showCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token
    ) {
        return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
    }

    @PutMapping
    public ResponseEntity<Cart> updateCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody Cart cart
    ) {
        return ResponseEntity.ok(cartApplication.updateCart(
                provider.getUserVo(token).getId(), cart)
        );
    }

    @PostMapping("/order")
    public ResponseEntity<Cart> order(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody Cart cart
    ) {
        orderApplication.order(token, cart);
        return ResponseEntity.ok().build();
    }
}
