package com.latte.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.mapper.AuthMapper;
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
    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
        hashOperations = redisTemplate.opsForHash();
        objectMapper = new ObjectMapper();
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


    /**
     * 메뉴 상세 조회
     * 전달 받은 프론트엔드 API 호출 과정
     * 1. 최초 상세 조회는 무조건 비로그인
     * 2. 최초 상세 이후 토큰과 사이즈 정보를 담아서 다시 한 번 요청 ( percent 정보 요청 )
     * 3. 사이즈 변환 시, 2번과 동일하게 토큰과 사이즈 정보를 담아서 요청
     */
    public MenuDetailResponse menuDetail(Long menuNo, String menuSize, MemberResponse member) throws JsonProcessingException {
        /**
         * 최초 상세 조회 시 ( 사이즈 전달이 없는 경우 ), 해당 메뉴의 모든 사이즈를 조회하고, Redis 에 저장
         * 최초 상세 조회는 무조건 비로그인 상태임을 ( 토큰일 전달되지 않음을 ) 보장
         */
        if (!StringUtils.hasText(menuSize)) {
            log.info("##################### 최초 상세 조회 #####################");
            List<MenuDetailResponse> menuDetailList = menuMapper.getMenuDetail(menuNo, menuSize);
            String redisKey = menuDetailList.get(0).getBrand() + "_" + menuDetailList.get(0).getMenuName();
            return saveCache(menuNo, redisKey, menuDetailList);
        }

        /**
         * 사이즈 정보가 포함된 경우는 반드시 첫 상세 조회 이후임을 보장
         * 브랜드명_메뉴명을 key 값으로 사용, Map 은 사이즈명을 key 값으로 사용
         */
        String key = menuMapper.findMenuById(menuNo);
        Map<Object, Object> entries = hashOperations.entries(key);
        MenuDetailResponse menuDetailResponse = objectMapper.readValue((String) entries.get(menuSize), MenuDetailResponse.class);

        if (member == null) {
            log.info("##################### 비로그인 사용자 Redis 에서 상세 조회 #####################");
            return menuDetailResponse;
        }

        /**
         * 부가정보를 입력한 사용자는 현재 메뉴가 최대 카페인 섭취량의 몇 % 를 차지하는지 정보가 필요
         */
        log.info("##################### 로그인 사용자 Redis 에서 상세 조회 #####################");
        try {
            int maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();
            log.info("##################### 부가 정보를 입력한 사용자 #####################");
            double caffeine = Integer.parseInt(menuDetailResponse.getCaffeine().replace("mg", ""));
            String percent = maxCaffeine == 0 ? "100%" : Math.round((caffeine / maxCaffeine) * 100) + "%";
            menuDetailResponse.setPercent(percent);
            return menuDetailResponse;
        } catch (NotEnoughInfoException exception) {
            log.info("##################### 부가 정보를 미입력한 사용자 #####################");
            return menuDetailResponse;
        }
    }


    /**
     * 최초 상세 조회 시, 모든 사이즈에 대한 상세 정보를 모두 가져와 Redis 에 저장
     * 브랜드명_메뉴명을 key 값으로 사용, Map 은 사이즈명을 key 값으로 사용
     * 저장하면서 요청에 맞는 메뉴 상세 정보 기록 및 반환
     */
    private MenuDetailResponse saveCache(Long menuNo, String key, List<MenuDetailResponse> menuDetailList) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        MenuDetailResponse detailResponse = null;  // 반환할 정보
        List<String> others = new ArrayList<>();    // 다른 사이즈 저장

        // 다른 사이즈 정보 추출
        for (MenuDetailResponse menuDetailResponse : menuDetailList) {
            others.add(menuDetailResponse.getMenuSize());
        }

        for (MenuDetailResponse menuDetailResponse : menuDetailList) {
            menuDetailResponse.setOtherSizes(others);
            map.put(menuDetailResponse.getMenuSize(), objectMapper.writeValueAsString(menuDetailResponse));
            if (Objects.equals(menuNo, menuDetailResponse.getMenuNo())) {
                detailResponse = menuDetailResponse;
            }
        }
        hashOperations.putAll(key, map);
        return detailResponse;
    }



}