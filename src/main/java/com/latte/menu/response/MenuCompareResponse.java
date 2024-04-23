package com.latte.menu.response;

import lombok.Data;

@Data
public class MenuCompareResponse {
    private Long menuNo;
    private String brand;
    private String menuName;
    private String caffeine;
    private String price;
    private String allergy;
    private String kcal;
    private String imageUrl;
}
