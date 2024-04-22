package com.latte.menu.service;

import com.latte.menu.repository.MenuMapper;
import com.latte.menu.response.BrandCategoryResponse;
import com.latte.menu.response.BrandRankingResponse;
import com.latte.menu.response.MenuSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;

    public Page<BrandRankingResponse> findBrandRankingList(String brandName, String sortBy, Pageable pageable) {
        List<BrandRankingResponse> content = menuMapper.findBrandRankingList(BrandType.valueOf(brandName.toUpperCase()).getValue(), sortBy, pageable);
        int total = menuMapper.getBrandRankingListCnt(BrandType.valueOf(brandName.toUpperCase()).getValue());
        return new PageImpl<>(content, pageable, total);
    }

    public Page<BrandCategoryResponse> findBrandCategoryList(String brandName, String sortBy, String cond, Pageable pageable) {
        List<BrandCategoryResponse> content = menuMapper.findBrandCategoryList(BrandType.valueOf(brandName.toUpperCase()).getValue(), sortBy, cond, pageable);
        int total = menuMapper.getBrandCategoryCnt(BrandType.valueOf(brandName.toUpperCase()).getValue(), cond);
        return new PageImpl<>(content, pageable, total);
    }

    public Page<MenuSearchResponse> findMenuList(String sortBy, String cond, String word, Pageable pageable) {
        List<MenuSearchResponse> content = menuMapper.findMenuList(sortBy, cond, word, pageable);
        int total = menuMapper.getFindMenuListCnt(cond, word);
        return new PageImpl<>(content, pageable, total);
    }
}
