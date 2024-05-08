package com.latte.drink.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateStatusResponse {
    private String status;
    private String caffeine;
    private String sentence;
}
