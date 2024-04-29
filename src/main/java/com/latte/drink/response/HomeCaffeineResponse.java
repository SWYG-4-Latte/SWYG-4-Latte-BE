package com.latte.drink.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HomeCaffeineResponse {
    private String today;
    private String remain;
    private List<DrinkMenuResponse> recent;
}
