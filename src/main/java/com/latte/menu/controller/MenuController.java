package com.latte.menu.controller;

import com.latte.common.response.ResponseData;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.exception.NotLoginException;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.response.MemberResponse;
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
    private final AuthMapper authMapper;    // 테스트를 위한 임시

    /**
     * 토큰이 전달되지 않았으면 빈 값 반환
     * 부가 정보를 입력하지 않았으면 빈 값 반환
     */
    @GetMapping("/popup")
    public ResponseEntity<?> menuPopup() {
        ResponseData<?> responseData;
        try {
            MemberResponse member = isLogin();
            responseData = new ResponseData<>(null, menuService.popup(member));
        } catch (NotLoginException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.FORBIDDEN);
        } catch (NotEnoughInfoException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
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
     * 브랜드별 메뉴 조회
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
     * 검색 API
     */
    @GetMapping("/list")
    public ResponseEntity<?> searchMenu(@RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                        @RequestParam(value = "cond", defaultValue = "") String cond,
                                        @RequestParam(value = "word", defaultValue = "") String word,
                                        @PageableDefault(size = 6) Pageable pageable) {
        Page<MenuSearchResponse> menuList = menuService.findMenuList(sortBy, cond, word, pageable);
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
     * 비교하기
     */
    @GetMapping("/compare")
    public ResponseEntity<?> compareMenu(@RequestParam(value = "menu1", defaultValue = "") Long menuNo1,
                                         @RequestParam(value = "menu2", defaultValue = "") Long menuNo2,
                                         @RequestParam(value = "recent", defaultValue = "") String recent) {
        MenuComparePageResponse menuComparePageResponse = menuService.menuCompare(menuNo1, menuNo2, recent);
        ResponseData<?> responseData = new ResponseData<>(null, menuComparePageResponse);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 상세 조회
     * 토큰이 전달되지 않아도 일부는 전달 되어야함
     * 부가정보가 입력되지 않아도 일부는 전달 되어야함
     */
    @GetMapping("/detail/{menuNo}")
    public ResponseEntity<?> menuDetail(@PathVariable Long menuNo,
                                         @RequestParam(value = "menu_size", defaultValue = "defaultSize") String menuSize) {
        MemberResponse member;
        ResponseData<?> responseData;
        try {
            member = isLogin();
        } catch (NotLoginException exception) {
            member = null;
        }
        responseData = new ResponseData<>(null, menuService.menuDetail(menuNo, menuSize, member));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    private MemberResponse isLogin() {
        /*
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if ("anonymousUser".equals(principal)) {
            throw new NotLoginException("로그인하지 않은 사용자입니다");
        } else {
            User tokenUser = (User) principal;
            username = tokenUser.getUsername();
            log.info("username = {}", username);
        }
        return authMapper.findById(username);
        */
        return authMapper.findById("testUser");
    }
}