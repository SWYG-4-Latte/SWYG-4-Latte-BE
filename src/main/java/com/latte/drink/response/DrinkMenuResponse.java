package com.latte.drink.response;

import lombok.Data;

@Data
public class DrinkMenuResponse {
    private String menuName;
    private String caffeine;
    private String brand;
    private String imageUrl;
}
