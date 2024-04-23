package com.latte.menu.response;

import lombok.Data;

import java.util.List;

@Data
public class MenuDetailResponse {
    private Long menuNo;
    private String brand;
    private String menuName;
    private String caffeine;
    private String price;
    private String kcal;
    private String sugar;
    private String salt;
    private String protein;
    private String satFat;
    private String imageUrl;
    private List<MenuSimpleResponse> lowCaffeineMenus;
}
