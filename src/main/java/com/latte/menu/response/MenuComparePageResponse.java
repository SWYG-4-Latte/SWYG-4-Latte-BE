package com.latte.menu.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuComparePageResponse {
    private List<MenuCompareResponse> compare;
    private List<MenuSimpleResponse> recent;
}
