package com.latte.menu.response;

import lombok.Data;

@Data
public class BrandCategoryResponse {
    private Long menuNo;
    private String menuName;
    private String caffeine;
    private String brand;
    private int price;
    private String imageUrl;
}
