package com.latte.menu.response;

import lombok.Data;

@Data
public class MenuCompareResponse {
    private Long menuNo;
    private String brand;
    private String menuName;
    private String menuSize;
    private String volume;
    private String caffeine;
    private int price;
    private String allergy;
    private String kcal;
    private String imageUrl;
}
