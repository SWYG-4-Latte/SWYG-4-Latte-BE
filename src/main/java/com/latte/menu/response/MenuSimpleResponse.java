package com.latte.menu.response;

import lombok.Data;

@Data
public class MenuSimpleResponse {
    private Long menuNo;
    private String menuName;
    private String imageUrl;
}
