package com.latte.drink.repository;

import com.latte.drink.response.DateResponse;
import com.latte.drink.response.DateStatusResponse;
import com.latte.drink.response.DrinkMenuResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DrinkMapper {

    List<DateResponse> findCalendar(@Param("memberNo") String memberNo,
                                    @Param("startDateTime") LocalDateTime startDateTime,
                                    @Param("lastDateTime") LocalDateTime lastDateTime);

    int findSumCaffeineByMonth(@Param("memberNo") String memberNo,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("lastDateTime") LocalDateTime lastDateTime);


    DateStatusResponse findSumCaffeineByDate(@Param("memberNo") String memberNo,
                                             @Param("localDateTime") LocalDateTime localDateTime,
                                             @Param("minNormal") int minNormal,
                                             @Param("maxNormal") int maxNormal);

    List<DrinkMenuResponse> findMenuByDate(@Param("memberNo") String memberNo,
                                           @Param("localDateTime") LocalDateTime localDateTime);
}
