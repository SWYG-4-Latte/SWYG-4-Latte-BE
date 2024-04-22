package com.latte.menu.controller;

import com.latte.common.response.ResponseData;
import com.latte.menu.response.BrandCategoryResponse;
import com.latte.menu.response.BrandRankingResponse;
import com.latte.menu.response.MenuSearchResponse;
import com.latte.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/ranking/{brandName}")
    public ResponseEntity<?> brandRanking(@PathVariable String brandName,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                          @PageableDefault(size = 3) Pageable pageable) {
        Page<BrandRankingResponse> brandRanking = menuService.findBrandRankingList(brandName, sortBy, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, brandRanking);
        return new ResponseEntity<>(responseData,  HttpStatus.OK);
    }

    @GetMapping("/{brandName}")
    public ResponseEntity<?> brandCategory(@PathVariable String brandName,
                                      @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                      @RequestParam(value = "cond", defaultValue = "") String cond,
                                      @PageableDefault(size = 4) Pageable pageable) {
        Page<BrandCategoryResponse> brandCategory = menuService.findBrandCategoryList(brandName, sortBy, cond, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, brandCategory);
        return new ResponseEntity<>(responseData,  HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> searchMenu(@RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                        @RequestParam(value = "cond", defaultValue = "") String cond,
                                        @RequestParam(value = "word", defaultValue = "") String word,
                                        @PageableDefault(size = 6) Pageable pageable) {
        Page<MenuSearchResponse> menuList = menuService.findMenuList(sortBy, cond, word, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, menuList);
        return new ResponseEntity<>(responseData,  HttpStatus.OK);
    }
}
