package com.latte.menu.service;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;
    private final StandardValueCalculate standardValueCalculate;
    private final String rankingKey = "menuSearchRanking";

    private final RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }


    public List<BrandRankingResponse> findBrandRankingList(String brandName) {
        return menuMapper.findBrandRankingList(BrandType.valueOf(brandName.toUpperCase()).getValue());
    }

    public Page<BrandCategoryResponse> findBrandCategoryList(String brandName, String sortBy, String cond, Pageable pageable) {
        List<BrandCategoryResponse> content = menuMapper.findBrandCategoryList(BrandType.valueOf(brandName.toUpperCase()).getValue(), sortBy, cond, pageable);
        int total = menuMapper.getBrandCategoryCnt(BrandType.valueOf(brandName.toUpperCase()).getValue(), cond);
        return new PageImpl<>(content, pageable, total);
    }

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


    public MenuComparePageResponse menuCompare(Long menuNo1, Long menuNo2, String recent) {
        MenuComparePageResponse menuComparePageResponse = new MenuComparePageResponse();
        if (menuNo1 != null || menuNo2 != null) {
            menuComparePageResponse.setCompare(menuMapper.compare(menuNo1, menuNo2));
        }
        if (!"".equals(recent)) {
            String[] split = recent.split(",");
            menuComparePageResponse.setRecent(menuMapper.getRecentMenu(split));
        }
        return menuComparePageResponse;
    }

    /**
     * 사용자에 따른 영양성분의 높음, 낮음, 카페인 섭취량의 % 계산 필요
     */
    public MenuDetailResponse menuDetail(Long menuNo, MemberResponse member) {
        Integer maxCaffeine = null;
        if (member != null) {
            maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();
        }
        MenuDetailResponse menuDetail = menuMapper.getMenuDetail(menuNo, maxCaffeine);
        // 낮은 함량의 카페인
        int baseCaffeine = Integer.parseInt(menuDetail.getCaffeine().replace("mg", ""));
        menuDetail.setLowCaffeineMenus(menuMapper.getLowCaffeineMenu(baseCaffeine));
        return menuDetail;
    }
    
}
