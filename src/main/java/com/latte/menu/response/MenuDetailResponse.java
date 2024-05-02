package com.latte.menu.response;

import lombok.Data;

import java.util.List;

@Data
public class MenuDetailResponse {
    private Long menuNo;
    private String brand;
    private String menuName;
    private String caffeine;
    private String percent;
    private int price;
    private Nutrient nutrient;
    private String imageUrl;
    private List<MenuSimpleResponse> lowCaffeineMenus;

    @Data
    public static class Nutrient {
        private String kcal;
        private String sugar;
        private String salt;
        private String protein;
        private String satFat;
    }
}
