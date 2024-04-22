package com.latte.menu.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrandType {
    STARBUCKS("스타벅스"),
    COMPOSE("컴포즈"),
    TWOSOME("투썸"),
    PAIK("빽다방"),
    EDIYA(" 이디야");

    String value;
}
