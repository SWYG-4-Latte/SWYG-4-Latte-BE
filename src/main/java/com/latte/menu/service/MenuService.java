package com.latte.menu.service;

import com.latte.menu.repository.MenuMapper;
import com.latte.menu.response.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final String rankingKey = "searchWordRanking";

    private final RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }


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


    @Cacheable(value = "searchWordRankingCache", key = "'searchWordRanking'")
    public List<SearchRankingResponse> getSearchWordRanking() {
        List<SearchRankingResponse> rankingResponses = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(rankingKey, 0, 4);

        if (typedTuples.size() == 0) {
            return rankingResponses;
        }

        int rank = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String word = typedTuple.getValue();
            SearchRankingResponse response = new SearchRankingResponse(rank, word);
            rankingResponses.add(response);
            rank++;
        }
        return rankingResponses;
    }

    @CacheEvict(value = "searchWordRankingCache", allEntries = true)
    public void clearCache() {
        log.info("cache clear!!!");
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
    public MenuDetailResponse menuDetail(Long menuNo) {
        MenuDetailResponse menuDetail = menuMapper.getMenuDetail(menuNo);
        String caffeine = menuDetail.getCaffeine();
        menuDetail.setCaffeine("카페인 " + caffeine);
        // 낮은 함량의 카페인
        int baseCaffeine = Integer.parseInt(caffeine.replace("mg", ""));
        menuDetail.setLowCaffeineMenus(menuMapper.getLowCaffeineMenu(baseCaffeine));
        return menuDetail;
    }
    
}
