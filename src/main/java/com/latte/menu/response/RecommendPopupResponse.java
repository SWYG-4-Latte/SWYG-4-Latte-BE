package com.latte.menu.response;

import lombok.Data;

@Data
public class RecommendPopupResponse {
    private Long menuNo;
    private String imageUrl;
    private String content;
}
