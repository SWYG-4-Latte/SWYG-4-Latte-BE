package com.latte.menu.repository;

import com.latte.menu.response.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<BrandRankingResponse> findBrandRankingList(@Param("brand") String brand,
                                                    @Param("sortBy") String sortBy);

    int getBrandRankingListCnt(@Param("brand") String brand);

    List<BrandCategoryResponse> findBrandCategoryList(@Param("brand") String brand,
                                                 @Param("sortBy") String sortBy,
                                                 @Param("cond") String cond,
                                                 @Param("pageable") Pageable pageable);

    int getBrandCategoryCnt(@Param("brand") String brand, @Param("cond") String cond);

    List<MenuSearchResponse> findMenuList(@Param("sortBy") String sortBy,
                                      @Param("cond") String cond,
                                      @Param("word") String word,
                                      @Param("pageable") Pageable pageable);

    int getFindMenuListCnt(@Param("cond") String cond, @Param("word") String word);

    List<MenuCompareResponse> compare(@Param("menu1") Long menu1, @Param("menu2") Long menu2);

    List<MenuSimpleResponse> getRecentMenu(@Param("menus") String[] menus);

    MenuDetailResponse getMenuDetail(@Param("no") Long no, @Param("maxCaffeine") Integer maxCaffeine);

    List<MenuSimpleResponse> getLowCaffeineMenu(@Param("base") int base);
}
