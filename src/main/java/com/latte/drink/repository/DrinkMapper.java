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

    List<DrinkMenuResponse> findHomeResponse(@Param("mbrNo") String mbrNo,
                                          @Param("startDateTime") LocalDateTime startDateTime,
                                          @Param("lastDateTime") LocalDateTime lastDateTime);

    List<DateResponse> findCalendar(@Param("mbrNo") String mbrNo,
                                    @Param("startDateTime") LocalDateTime startDateTime,
                                    @Param("lastDateTime") LocalDateTime lastDateTime);

    int findSumCaffeineByMonth(@Param("mbrNo") String mbrNo,
                               @Param("startDateTime") LocalDateTime startDateTime,
                               @Param("lastDateTime") LocalDateTime lastDateTime);


    DateStatusResponse findSumCaffeineByDate(@Param("mbrNo") String mbrNo,
                                             @Param("localDateTime") LocalDateTime localDateTime,
                                             @Param("minNormal") int minNormal,
                                             @Param("maxNormal") int maxNormal);

    List<DrinkMenuResponse> findMenuByDate(@Param("mbrNo") String mbrNo,
                                           @Param("localDateTime") LocalDateTime localDateTime);

    void saveDrinkMenu(@Param("mbrNo") String mbrNo,
                       @Param("menuNo") Long menuNo,
                       @Param("localDateTime") LocalDateTime localDateTime);
}
