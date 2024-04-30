package com.latte.drink.service;

import com.latte.drink.repository.DrinkMapper;
import com.latte.drink.response.*;
import com.latte.drink.standard.StandardValue;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DrinkService {

    private final DrinkMapper drinkMapper;
    private final StandardValueCalculate standardValueCalculate;

    public HomeCaffeineResponse findHomeResponse(MemberResponse member) {
        int today = 0;
        String remain = "";
        LocalDateTime lastDateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startDateTime = lastDateTime.minusDays(3);    // 3일 전까지만 조회
        List<DrinkMenuResponse> recent = drinkMapper.findHomeResponse(member.getMbrNo(), startDateTime, lastDateTime);
        int maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();

        for (DrinkMenuResponse drinkMenuResponse : recent) {
            today += Integer.parseInt(drinkMenuResponse.getCaffeine().replace("mg", ""));
        }

        if (today > maxCaffeine) {
            remain = "0mg";
        } else {
            remain = (maxCaffeine - today) + "mg";
        }

        return new HomeCaffeineResponse(today + "mg", remain, recent);
    }

    public CalendarResponse findCaffeineByMonth(MemberResponse member, String dateTime) {

        String[] yearMonth = dateTime.split("-");
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime firstDayOfMonth;
        LocalDateTime lastDayOfMonth;
        LocalDateTime lastDayOfLastMonth;
        LocalDateTime firstDayOfLastMonth;

        /**
         * 전달 받은 달이 이번 달 경우 -> 지난 달 N 일 ~ 이번 달 N 일
         * 전달 받은 달이 이번 달이 아닌 경우 -> 기준 달의 이전 달 전체 ~ 기준 달 전체
         */
        if (today.getYear() == Integer.parseInt(yearMonth[0]) && today.getMonthValue() == Integer.parseInt(yearMonth[1])) {
            firstDayOfMonth = today.withDayOfMonth(1); // 이번 달 1일
            lastDayOfMonth = today;                    // 이번 달 N 일
            lastDayOfLastMonth = today.minusMonths(1); // 지난 달 N 일
            firstDayOfLastMonth = lastDayOfLastMonth.withDayOfMonth(1);   // 지난 달 1일
        } else {    // 지난 달인 경우
            // 기준 달 1일
            firstDayOfMonth = LocalDateTime.of(Integer.parseInt(yearMonth[0]), Integer.parseInt(yearMonth[1]), 1, 0, 0, 0);
            lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.toLocalDate().lengthOfMonth());    // 기준 달 마지막 날짜
            firstDayOfLastMonth = firstDayOfMonth.minusMonths(1);    // 지난 달 1일
            lastDayOfLastMonth = firstDayOfLastMonth.withDayOfMonth(firstDayOfLastMonth.toLocalDate().lengthOfMonth()); // 지난 달 마지막 날짜
        }

        log.info("firstDayOfMonth = {}", firstDayOfMonth);
        log.info("lastDayOfMonth = {}", lastDayOfMonth);
        log.info("lastDayOfLastMonth = {}", lastDayOfLastMonth);
        log.info("firstDayOfLastMonth = {}", firstDayOfLastMonth);

        /**
         * 월별 카페인 섭취량 통계
         */
        // 이번 달 카페인 섭취량
        int sumCaffeineByMonth = drinkMapper.findSumCaffeineByMonth(member.getMbrNo(), firstDayOfMonth, lastDayOfMonth);
        // 지난 달 N 일까지의 카페인 섭취량
        int sumCaffeineByLastMonth = drinkMapper.findSumCaffeineByMonth(member.getMbrNo(), firstDayOfLastMonth, lastDayOfLastMonth);

        // 월 통계량
        String status = "";
        if (sumCaffeineByLastMonth == -1) {
            status = "없음";
        } else if (sumCaffeineByMonth > sumCaffeineByLastMonth) {
            status = "증가";
        } else if (sumCaffeineByMonth == sumCaffeineByLastMonth) {
            status = "동일";
        } else {
            status = "감소";
        }


        /**
         * 날짜별 카페인 섭취 상태
         */
        StandardValue memberStandardValue = standardValueCalculate.getMemberStandardValue(member);
        List<DateResponse> calendar = drinkMapper.findCalendar(member.getMbrNo(), firstDayOfMonth, lastDayOfMonth);
        Map<String, String> mapResponse = new HashMap<>();

        for (DateResponse response : calendar) {
            String value = "";
            int caffeine = Integer.parseInt(response.getCaffeine());
            if (caffeine > memberStandardValue.getMaxNormal()) {
                value = "높음";
            } else if(caffeine < memberStandardValue.getMinNormal()) {
                value = "낮음";
            } else {
                value = "보통";
            }
            mapResponse.put(response.getDate(), value);
        }

        return new CalendarResponse(status, mapResponse);
    }

    public DateStatusResponse findCaffeineByDate(MemberResponse member, LocalDateTime dateTime) {
        StandardValue memberStandardValue = standardValueCalculate.getMemberStandardValue(member);
        return drinkMapper.findSumCaffeineByDate(member.getMbrNo(), dateTime, memberStandardValue.getMinNormal(), memberStandardValue.getMaxNormal());
    }

    public List<DrinkMenuResponse> findMenuByDate(MemberResponse member, LocalDateTime dateTime) {
        return drinkMapper.findMenuByDate(member.getMbrNo(), dateTime);
    }

    public void saveDrinkMenu(MemberResponse member, Long menuNo) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        drinkMapper.saveDrinkMenu(member.getMbrNo(), menuNo, today);
    }
}
