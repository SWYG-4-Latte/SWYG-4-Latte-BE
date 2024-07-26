package com.latte.menu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.latte.common.response.ResponseData;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.menu.exception.NotCorrectIndexException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final AuthService authService;

    /**
     * 토큰이 전달되지 않았으면 빈 값 반환
     * 부가 정보를 입력하지 않았으면 빈 값 반환
     */
    @GetMapping("/popup")
    public ResponseEntity<?> menuPopup() {
        ResponseData<?> responseData;
        try {
            MemberResponse member = isLogin();
            if (member == null) {
                responseData = new ResponseData<>("로그인 하지 않은 사용자입니다", null);
                return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
            }
            responseData = new ResponseData<>(null, menuService.popup(member));
        } catch (NotEnoughInfoException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 브랜드 조회
     */
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

    /**
     * 브랜드별 인기 메뉴 조회
     */
    @GetMapping("/ranking/{brandName}")
    public ResponseEntity<?> brandRanking(@PathVariable String brandName) {
        List<BrandRankingResponse> brandRankingList = menuService.findBrandRankingList(brandName);
        ResponseData<?> responseData = new ResponseData<>(null, brandRankingList);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 브랜드별 메뉴 조회 (미사용)
     */
    @GetMapping("/{brandName}")
    public ResponseEntity<?> brandCategory(@PathVariable String brandName,
                                           @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                           @RequestParam(value = "cond", defaultValue = "") String cond,
                                           @PageableDefault(size = 4) Pageable pageable) {
        Page<BrandCategoryResponse> brandCategory = menuService.findBrandCategoryList(brandName, sortBy, cond, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, brandCategory);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 카테고리별 메뉴 조회
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> category(@PathVariable String categoryName,
                                           @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                           @RequestParam(value = "cond", defaultValue = "") String cond,
                                           @PageableDefault(size = 4) Pageable pageable) {
        Page<BrandCategoryResponse> brandCategory = menuService.findCategoryList(categoryName, sortBy, cond, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, brandCategory);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    /**
     * 검색 API
     */
    @GetMapping("/list")
    public ResponseEntity<?> searchMenu(@RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                        @RequestParam(value = "cond", defaultValue = "") String cond,
                                        @RequestParam(value = "word", defaultValue = "") String word,
                                        @PageableDefault(size = 6) Pageable pageable) {
        MemberResponse member = isLogin();
        Page<MenuSearchResponse> menuList = menuService.findMenuList(member, sortBy, cond, word, pageable);
        ResponseData<?> responseData = new ResponseData<>(null, menuList);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 인기 검색어 조회
     */
    @GetMapping("/ranking/word")
    public ResponseEntity<?> searchWordRanking() {
        List<MenuSearchRankingResponse> searchWordRanking = menuService.getSearchWordRanking();
        ResponseData<?> responseData = new ResponseData<>(null, searchWordRanking);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 최근 검색어 조회
     */
    @GetMapping("/recent/word")
    public ResponseEntity<?> recentSearchWord() {
        MemberResponse member = isLogin();
        ResponseData<?> responseData = new ResponseData<>(null, menuService.getRecentSearchWord(member));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 최근 검색어 제거
     */
    @DeleteMapping("/recent/word/{wordIdx}")
    public ResponseEntity<?> deleteRecentSearchWord(@PathVariable int wordIdx) {
        ResponseData<?> responseData;
        try {
            MemberResponse member = isLogin();
            menuService.deleteRecentSearchWord(member, wordIdx);
            responseData = new ResponseData<>("최근 검색어가 삭제되었습니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (NotCorrectIndexException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 비교하기
     */
    @GetMapping("/compare")
    public ResponseEntity<?> compareMenu(@RequestParam(value = "menu1", defaultValue = "") Long menuNo1,
                                         @RequestParam(value = "menu2", defaultValue = "") Long menuNo2) {
        ResponseData<?> responseData = new ResponseData<>(null, menuService.menuCompare(menuNo1, menuNo2));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    /**
     * 최근 확인한 메뉴
     */
    @GetMapping("/recent")
    public ResponseEntity<?> vieRecentMenu() {
        ResponseData<?> responseData;
        try {
            MemberResponse member = isLogin();
            responseData = new ResponseData<>(null, menuService.recentMenu(member));
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("최근 마신 음료 조회에 실패하였습니다.", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
    

    /**
     * 상세 조회
     * 토큰이 전달되지 않아도 일부는 전달 되어야함
     * 부가정보가 입력되지 않아도 일부는 전달 되어야함
     */
    @GetMapping("/detail/{menuNo}")
    public ResponseEntity<?> menuDetail(@PathVariable Long menuNo,
                                         @RequestParam(value = "menu_size", defaultValue = "") String menuSize) {
        ResponseData<?> responseData;
        try {
            MemberResponse member = isLogin();
            responseData = new ResponseData<>(null, menuService.menuDetail(menuNo, menuSize, member));
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("상세 조회에 실패하였습니다.", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    private MemberResponse isLogin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if ("anonymousUser".equals(principal)) {
            return null;
        } else {
            User tokenUser = (User) principal;
            username = tokenUser.getUsername();
            log.info("username = {}", username);
        }

        MemberResponse member = null;
        if(username.contains("@")) {
            FindIdResponse find = authService.findIdByNameEmail(username);
            member = authService.getMemberInfo(find.getMbrId());
        } else {
            member = authService.getMemberInfo(username);
        }
        return member;
    }
}