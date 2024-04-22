package com.latte.menu.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandListResponse {
    private String brandName;
    private String imageUrl;
}
