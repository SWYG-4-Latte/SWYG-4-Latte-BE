package com.latte.menu.response;

import lombok.Data;

@Data
public class BrandRankingResponse {
    private Long menuNo;
    private String menuName;
    private String brand;
    private String caffeine;
    private String menuSize;
    private String imageUrl;
}
