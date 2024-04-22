package com.latte.menu.repository;

import com.latte.menu.response.BrandCategoryResponse;
import com.latte.menu.response.BrandRankingResponse;
import com.latte.menu.response.MenuSearchResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<BrandRankingResponse> findBrandRankingList(@Param("brand") String brand,
                                                    @Param("sortBy") String sortBy,
                                                    @Param("pageable") Pageable pageable);

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
}
