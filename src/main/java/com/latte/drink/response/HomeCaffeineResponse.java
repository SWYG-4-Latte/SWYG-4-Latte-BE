package com.latte.drink.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HomeCaffeineResponse {
    private String status;
    private String today;
    private String interval;
    private List<DrinkMenuResponse> recent;
}
