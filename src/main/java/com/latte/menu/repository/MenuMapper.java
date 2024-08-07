package com.latte.menu.repository;

import com.latte.menu.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MenuMapper {
    List<BrandRankingResponse> findBrandRankingList(@Param("brand") String brand);

    List<BrandCategoryResponse> findBrandCategoryList(@Param("brand") String brand,
                                                      @Param("sortBy") String sortBy,
                                                      @Param("cond") String cond,
                                                      @Param("pageable") Pageable pageable);

    int getBrandCategoryCnt(@Param("brand") String brand,
                            @Param("sortBy") String sortBy,
                            @Param("cond") String cond);

    List<BrandCategoryResponse> findCategoryList(@Param("brand") String brand,
                                                 @Param("category") String category,
                                                 @Param("sortBy") String sortBy,
                                                 @Param("cond") String cond,
                                                 @Param("pageable") Pageable pageable);

    int getCategoryCnt(@Param("brand") String brand,
                       @Param("category") String category,
                       @Param("sortBy") String sortBy,
                       @Param("cond") String cond);

    List<MenuSearchResponse> findMenuList(@Param("sortBy") String sortBy,
                                          @Param("cond") String cond,
                                          @Param("word") String word,
                                          @Param("pageable") Pageable pageable);

    int getFindMenuListCnt(@Param("sortBy") String sortBy,
                           @Param("cond") String cond,
                           @Param("word") String word);

    String findTodaySumCaffeine(@Param("mbrNo") int mbrNo,
                                @Param("localDateTime") LocalDateTime localDateTime,
                                @Param("minNormal") double minNormal,
                                @Param("maxNormal") double maxNormal);

    RecommendPopupResponse findRecommendMenu(@Param("todayStatus") String todayStatus);

    List<MenuCompareResponse> compare(@Param("menu1") Long menu1, @Param("menu2") Long menu2);

    List<MenuSimpleResponse> getRecentMenu(@Param("menus") String[] menus);

    List<MenuDetailResponse> getMenuDetail(@Param("no") Long no,
                                            @Param("menuSize") String menuSize);

    String findMenuById(@Param("menuNo") Long menuNo);
}