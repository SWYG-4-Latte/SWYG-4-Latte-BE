package com.latte.drink.response;

import lombok.Data;

@Data
public class DrinkMenuResponse {
    private Long menuNo;
    private String menuName;
    private String brand;
    private String caffeine;
    private String menuSize;
    private String imageUrl;
}
