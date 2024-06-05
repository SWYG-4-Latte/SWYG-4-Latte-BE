package com.latte.menu.response;

import lombok.Data;

@Data
public class MenuSimpleResponse {
    private Long menuNo;
    private String menuName;
    private String imageUrl;

    public static MenuSimpleResponse convertDetailToSimple(MenuDetailResponse menuDetailResponse) {
        MenuSimpleResponse menuSimpleResponse = new MenuSimpleResponse();
        menuSimpleResponse.setMenuNo(menuDetailResponse.getMenuNo());
        menuSimpleResponse.setMenuName(menuDetailResponse.getMenuName());
        menuSimpleResponse.setImageUrl(menuDetailResponse.getImageUrl());
        return menuSimpleResponse;
    }
}
