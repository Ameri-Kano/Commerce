package com.amerikano.cms.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다."),
    SAVE_ITEM_NAME(HttpStatus.BAD_REQUEST, "아이템 명이 중복되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
