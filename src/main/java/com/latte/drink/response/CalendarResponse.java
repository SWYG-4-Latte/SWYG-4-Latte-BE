package com.latte.drink.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse {
    private String status;
    private Map<String, String> date;
}
