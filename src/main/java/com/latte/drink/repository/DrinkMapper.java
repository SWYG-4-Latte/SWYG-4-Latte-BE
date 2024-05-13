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

    int findSumCaffeineByToday(@Param("mbrNo") int mbrNo,
                               @Param("today") LocalDateTime today);

    List<DrinkMenuResponse> findHomeResponse(@Param("mbrNo") int mbrNo,
                                             @Param("today") LocalDateTime today);

    List<DateResponse> findCalendar(@Param("mbrNo") int mbrNo,
                                    @Param("startDateTime") LocalDateTime startDateTime,
                                    @Param("lastDateTime") LocalDateTime lastDateTime);

    int findSumCaffeineByMonth(@Param("mbrNo") int mbrNo,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("lastDateTime") LocalDateTime lastDateTime);


    DateStatusResponse findSumCaffeineByDate(@Param("mbrNo") int mbrNo,
                                             @Param("localDateTime") LocalDateTime localDateTime,
                                             @Param("minNormal") int minNormal,
                                             @Param("maxNormal") int maxNormal);

    List<DrinkMenuResponse> findMenuByDate(@Param("mbrNo") int mbrNo,
                                           @Param("localDateTime") LocalDateTime localDateTime);

    void saveDrinkMenu(@Param("mbrNo") int mbrNo,
                       @Param("menuNo") Long menuNo,
                       @Param("localDateTime") LocalDateTime localDateTime);
}