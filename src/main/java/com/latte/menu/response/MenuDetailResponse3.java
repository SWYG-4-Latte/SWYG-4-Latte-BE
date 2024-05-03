package com.latte.menu.response;

import lombok.Data;

import java.util.List;

@Data
public class MenuDetailResponse3 {
    private Long menuNo;
    private String brand;
    private String menuName;
    private String menuSize;
    private String caffeine;
    private String percent;
    private int price;
    private Nutrient nutrient;
    private Level level;
    private String imageUrl;
    private List<MenuSimpleResponse> lowCaffeineMenus;
    private List<String> otherSizes;

    @Data
    public static class Nutrient {
        private String kcal;
        private String sugar;
        private String salt;
        private String protein;
        private String satFat;
    }

    @Data
    public static class Level {
        private String kcalLevel;
        private String sugarLevel;
        private String saltLevel;
        private String proteinLevel;
        private String satFatLevel;
    }
}
