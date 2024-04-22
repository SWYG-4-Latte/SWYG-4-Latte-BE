package com.latte.menu.response;

import lombok.Data;

@Data
public class MenuSearchResponse {
    private Long menuNo;
    private String menuName;
    private String caffeine;
    private String brand;
    private String price;
    private String imageUrl;
}
