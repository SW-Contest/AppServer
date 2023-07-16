package com.artfolio.artfolio.business.domain;

import lombok.Getter;

@Getter
public enum OrderType {
    ASC("asc"), DESC("desc");

    private final String orderType;

    OrderType(String orderType) {
        this.orderType = orderType;
    }
}
