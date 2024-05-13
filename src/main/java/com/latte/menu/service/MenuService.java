package com.latte.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.response.MemberResponse;
import com.latte.menu.repository.MenuMapper;
import com.latte.menu.response.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;

    private final StandardValueCalculate standardValueCalculate;

    private final String rankingKey = "menuSearchRanking";

    private final RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;  // 인기 검색어
    private HashOperations<String, Object, Object> hashOperations;  // 브랜드명과 메뉴명

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
        hashOperations = redisTemplate.opsForHash();
    }


    /**
     * 메뉴 추천 팝업
     */
    public RecommendPopupResponse popup(MemberResponse member) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        int minNormal = standardValueCalculate.getMemberStandardValue(member).getMinNormal();
        int maxNormal = standardValueCalculate.getMemberStandardValue(member).getMaxNormal();
        String todayCaffeineStatus = menuMapper.findTodaySumCaffeine(member.getMbrNo(), today, minNormal, maxNormal);
        return menuMapper.findRecommendMenu(todayCaffeineStatus);
    }


    /**
     * 브랜드별 인기순 조회
     */
    public List<BrandRankingResponse> findBrandRankingList(String brandName) {
        return menuMapper.findBrandRankingList(BrandType.valueOf(brandName.toUpperCase()).getValue());
    }


    /**
     * 카테고리 리스트 ( 브랜드별 리스트 )
     */
    public Page<BrandCategoryResponse> findBrandCategoryList(String brandName, String sortBy, String cond, Pageable pageable) {
        List<BrandCategoryResponse> content = menuMapper.findBrandCategoryList(BrandType.valueOf(brandName.toUpperCase()).getValue(), sortBy, cond, pageable);
        int total = menuMapper.getBrandCategoryCnt(BrandType.valueOf(brandName.toUpperCase()).getValue(), cond);
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 메뉴 검색
     */
    public Page<MenuSearchResponse> findMenuList(String sortBy, String cond, String word, Pageable pageable) {
        if ("".equals(word)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }

        List<MenuSearchResponse> content = menuMapper.findMenuList(sortBy, cond, word, pageable);
        int total = menuMapper.getFindMenuListCnt(cond, word);

        // 검색 성공 시에만 증가
        if (content.size() != 0) {
            zSetOperations.incrementScore(rankingKey, word, 1);
        }

        return new PageImpl<>(content, pageable, total);
    }


    /**
     * 인기 검색어 조회
     */
    public List<MenuSearchRankingResponse> getSearchWordRanking() {
        List<MenuSearchRankingResponse> rankingResponses = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(rankingKey, 0, 4);

        if (typedTuples.size() == 0) {
            return rankingResponses;
        }

        int rank = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String word = typedTuple.getValue();
            MenuSearchRankingResponse response = new MenuSearchRankingResponse(rank, word);
            rankingResponses.add(response);
            rank++;
        }
        return rankingResponses;
    }


    /**
     * 메뉴 비교하기
     */
    public List<MenuCompareResponse> menuCompare(Long menuNo1, Long menuNo2) {
        List<MenuCompareResponse> menuCompareResponses = new ArrayList<>();
        if (menuNo1 != null || menuNo2 != null) {
            menuCompareResponses = menuMapper.compare(menuNo1, menuNo2);
        }
        return menuCompareResponses;
    }


    /**
     * 최근 확인한 메뉴
     */
    public List<MenuSimpleResponse> recentMenu(String recent) {
        List<MenuSimpleResponse> menuSimpleResponses = new ArrayList<>();
        if (!"".equals(recent)) {
            String[] split = recent.split(",");
            menuSimpleResponses = menuMapper.getRecentMenu(split);
        }
        return menuSimpleResponses;
    }

    public MenuDetailResponse menuDetail(Long menuNo, String menuSize, MemberResponse member) {
        Integer maxCaffeine = null;
        try {
            if (member != null) {
                maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();
            }
        } catch (NotEnoughInfoException exception) {
            maxCaffeine = null;
        }
        return menuMapper.getMenuDetail(menuNo, menuSize, maxCaffeine);
    }

//
//    /**
//     * 메뉴 상세 조회
//     */
//    public MenuDetailResponse menuDetail(Long menuNo, String menuSize, MemberResponse member) throws JsonProcessingException {
//        /**
//         * 사이즈 정보가 포함된 경우는 반드시 첫 상세 조회 이후임을 보장
//         * 로그인 한 사용자는 아이디가 key 값, 로그인 하지 않은 사용자는 브랜드명+메뉴명이 key 값
//         */
//        String key;
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        if (StringUtils.hasText(menuSize)) {
//            if (member == null) {
//                log.info("##################### 비로그인 사용자 Redis 에서 상세 조회 #####################");
//                key = menuMapper.findMenuById(menuNo);
//            } else {
//                log.info("##################### 로그인 사용자 Redis 에서 상세 조회 #####################");
//                key = member.getMbrId();
//            }
//            Map<Object, Object> entries = hashOperations.entries(key);
//            return objectMapper.readValue((String) entries.get(menuSize), MenuDetailResponse.class);
//        }
//
//        /**
//         * 로그인 한 사용자의 경우, 현재 메뉴가 최대 카페인 섭취량의 몇 % 를 차지하는지 정보가 필요
//         */
//        Integer maxCaffeine = null;
//        try {
//            if (member != null) {
//                maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();
//            }
//        } catch (NotEnoughInfoException exception) {
//            maxCaffeine = null;
//        }
//
//        /**
//         * 해당 메뉴의 모든 사이즈를 조회하고, Redis 에 저장
//         */
//        List<MenuDetailResponse> menuDetailList = menuMapper.getMenuDetail(menuNo, menuSize, maxCaffeine);
//        if (member == null) {
//            log.info("##################### 비로그인 사용자 최초 상세 조회 #####################");
//            key = menuDetailList.get(0).getBrand() + "_" + menuDetailList.get(0).getMenuName();
//        } else {
//            log.info("##################### 로그인 사용자 최초 상세 조회 #####################");
//            key = member.getMbrId();
//        }
//        return saveCache(menuNo, key, menuDetailList);
//    }
//
//
//    /**
//     * 최초 상세 조회 시, 모든 사이즈에 대한 상세 정보를 모두 가져와 Redis 에 저장
//     * 외부 key 값은 memberId, 내부 Map 의 key 값은 각 사이즈
//     * 로그인 하지 않은 사용자라면 브랜드명+메뉴명을 외부 key 값으로 사용
//     * 저장하면서 요청에 맞는 메뉴 상세 정보 기록 및 반환
//     */
//    private MenuDetailResponse saveCache(Long menuNo, String key, List<MenuDetailResponse> menuDetailList) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, String> map = new HashMap<>();
//        MenuDetailResponse detailResponse = null;  // 반환할 정보
//        List<String> others = new ArrayList<>();    // 다른 사이즈 저장
//
//        // 다른 사이즈 정보 추출
//        for (MenuDetailResponse menuDetailResponse : menuDetailList) {
//            others.add(menuDetailResponse.getMenuSize());
//        }
//
//        for (MenuDetailResponse menuDetailResponse : menuDetailList) {
//            menuDetailResponse.setOtherSizes(others);
//            map.put(menuDetailResponse.getMenuSize(), objectMapper.writeValueAsString(menuDetailResponse));
//            if (Objects.equals(menuNo, menuDetailResponse.getMenuNo())) {
//                detailResponse = menuDetailResponse;
//            }
//        }
//        hashOperations.putAll(key, map);
//        return detailResponse;
//    }
//


}