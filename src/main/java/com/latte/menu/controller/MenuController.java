package com.latte.menu.controller;

import com.latte.common.response.ResponseData;
import com.latte.menu.response.*;
import com.latte.menu.service.BrandType;
import com.latte.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/brand")
    public ResponseEntity<?> brandList() {
        List<BrandListResponse> brandList = new ArrayList<>();
        BrandType[] brands = BrandType.values();
        for (BrandType brand : brands) {
            brandList.add(new BrandListResponse(brand.getValue(), brand.getImageUrl()));
        }
        ResponseData<?> responseData = new ResponseData<>(null, brandList);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/ranking/{brandName}")
    public ResponseEntity<?> brandRanking(@PathVariable String brandName,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                          @PageableDefault(size = 3) Pageable pageable) {
        Page<BrandRankingResponse> brandRanking = menuService.findBrandRankingList(brandName, sortBy, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, brandRanking);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/{brandName}")
    public ResponseEntity<?> brandCategory(@PathVariable String brandName,
                                           @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                           @RequestParam(value = "cond", defaultValue = "") String cond,
                                           @PageableDefault(size = 4) Pageable pageable) {
        Page<BrandCategoryResponse> brandCategory = menuService.findBrandCategoryList(brandName, sortBy, cond, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, brandCategory);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> searchMenu(@RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                        @RequestParam(value = "cond", defaultValue = "") String cond,
                                        @RequestParam(value = "word", defaultValue = "") String word,
                                        @PageableDefault(size = 6) Pageable pageable) {
        Page<MenuSearchResponse> menuList = menuService.findMenuList(sortBy, cond, word, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, menuList);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/ranking/word")
    public ResponseEntity<?> searchWordRanking() {
        List<SearchRankingResponse> searchWordRanking = menuService.getSearchWordRanking();
        ResponseData<?> responseData = new ResponseData<>(null, searchWordRanking);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/compare")
    public ResponseEntity<?> compareMenu(@RequestParam(value = "menu1", defaultValue = "") Long menuNo1,
                                         @RequestParam(value = "menu2", defaultValue = "") Long menuNo2,
                                         @RequestParam(value = "recent", defaultValue = "") String recent) {
        log.info("recent = {}", recent);
        MenuComparePageResponse menuComparePageResponse = menuService.menuCompare(menuNo1, menuNo2, recent);
        ResponseData<?> responseData = new ResponseData<>(null, menuComparePageResponse);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/detail/{menuNo}")
    public ResponseEntity<?> menuDetail(@PathVariable Long menuNo) {
        MenuDetailResponse menuDetailResponse = menuService.menuDetail(menuNo);
        ResponseData<?> responseData = new ResponseData<>(null, menuDetailResponse);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
