package com.latte.menu.service;

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
import org.springframework.data.redis.core.ListOperations;
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
    private ListOperations<String, String> listOperations;  // 다른 사이즈 정보

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
        hashOperations = redisTemplate.opsForHash();
        listOperations = redisTemplate.opsForList();
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
     * 메뉴 사이즈가 없으면 메뉴 번호로 조회
     * 메뉴 사이즈가 있으면 브랜드명, 음료명, 사이즈로 조회
     */
    public MenuDetailResponse menuDetail(Long menuNo, String menuSize, MemberResponse member) {
        Integer maxCaffeine = null;
        String brand = "", menuName = "";
        try {
            if (member != null) {
                maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();
            }
        } catch (NotEnoughInfoException exception) {
            maxCaffeine = null;
        }

        /**
         * 다른 사이즈 정보 조회
         * 첫 상세조회라면 DB 에서 조회, 사이즈 변경 조회라면 Redis 에서 조회
         */
        List<String> otherSize = findOtherSize(menuNo, menuSize);

        /**
         * 사이즈 변경 조회 시, Redis 에서 메뉴번호를 통해 브랜드명과 메뉴명을 조회
         * 첫 상세 조회 이후 가능하기 때문에 메뉴번호의 존재와 다른 사이즈 정보의 존재를 보장
         */
        if (StringUtils.hasText(menuSize)) {
            Map<Object, Object> entries = hashOperations.entries(String.valueOf(menuNo));
            brand = (String) entries.get("brand");
            menuName = (String) entries.get("menuName");
        }

        /**
         * 메뉴 상세 조회 후, 다른 사이즈 정보를 응답에 추가
         */
        MenuDetailResponse menuDetail = menuMapper.getMenuDetail(menuNo, menuSize, maxCaffeine, brand, menuName);
        menuDetail.setOtherSizes(otherSize);

        /**
         * 현재 메뉴의 브랜드명, 메뉴명, 다른 사이즈 정보를 redis 에 저장
         */
        saveCache(menuDetail);
        return menuDetail;
    }


    /**
     * 조회된 메뉴 번호를 기준으로 브랜드명, 메뉴명, 다른 사이즈 정보를 저장
     * 사이즈 변경 조회 시, 최적화를 위해 이전 메뉴 번호를 기준으로 필요한 정보를 조회
     */
    private void saveCache(MenuDetailResponse menuDetail) {
        String stringMenuNo = String.valueOf(menuDetail.getMenuNo());
        
        // 브랜드명, 메뉴명 저장
        Map<String, String> map = new HashMap<>();
        map.put("brand", menuDetail.getBrand());
        map.put("menuName", menuDetail.getMenuName());
        hashOperations.putAll(stringMenuNo, map);

        // 다른 사이즈 정보 저장
        if (listOperations.size(stringMenuNo + "_size") == 0) {
            List<String> otherSizes = menuDetail.getOtherSizes();
            for (String otherSize : otherSizes) {
                listOperations.rightPush(stringMenuNo + "_size", otherSize);
            }
        }
    }


    /**
     * 첫 상세 조회 시에는 메뉴번호로 어떤 사이즈 정보가 있는지 탐색 필요
     * 그 이후에는 이전 메뉴 번호를 통해 사이즈를 조회
     */
    private List<String> findOtherSize(Long menuNo, String menuSize) {
        List<String> others = new ArrayList<>();

        if (!StringUtils.hasText(menuSize)) {
            others = menuMapper.findOtherSize(menuNo);
        } else {
            others = listOperations.range(menuNo + "_size", 0, -1);
        }
        return others;
    }
}