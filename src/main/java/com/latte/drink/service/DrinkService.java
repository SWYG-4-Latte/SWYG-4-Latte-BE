package com.latte.drink.service;

import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.repository.DrinkMapper;
import com.latte.drink.response.*;
import com.latte.drink.standard.DateSentence;
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
@RequiredArgsConstructor
public class DrinkService {

    private final DrinkMapper drinkMapper;
    private final StandardValueCalculate standardValueCalculate;

    /**
     * 홈화면 데이터
     * 닉네임, 오늘 카페인 섭취 상태, 오늘 카페인 섭취량, 기준값과의 차이, 최근 마신 음료
     * 부가정보 미입력 사용자는 닉네임만 반환
     */
    public HomeCaffeineResponse findHomeResponse(MemberResponse member) {
        String interval, status;
        int todayCaffeine, maxCaffeine;
        List<DrinkMenuResponse> recent;
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        recent = drinkMapper.findHomeResponse(member.getMbrNo(), today); // 최근 마신 음료
        todayCaffeine = drinkMapper.findSumCaffeineByToday(member.getMbrNo(), today);        // 오늘 마신 카페인 합계

        try {
            maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();   // 카페인 섭취량 기준값

            if (maxCaffeine < todayCaffeine) {
                status = "초과";
                interval = Math.abs(maxCaffeine - todayCaffeine) + "mg";
            } else {
                status = "적정";
                interval = (maxCaffeine - todayCaffeine) + "mg";
            }
        } catch (NotEnoughInfoException exception) {
            return new HomeCaffeineResponse(member.getNickname(), null, todayCaffeine + "mg", null, recent);
        }

        return new HomeCaffeineResponse(member.getNickname(), status, todayCaffeine + "mg", interval, recent);
    }

    public CalendarResponse findCaffeineByMonth(MemberResponse member, String dateTime) {

        String[] yearMonth = dateTime.split("-");
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime firstDayOfMonth, lastDayOfMonth, lastDayOfLastMonth, firstDayOfLastMonth;

        /**
         * 전달 받은 달이 이번 달 경우 -> 지난 달 N 일 ~ 이번 달 N 일
         * 전달 받은 달이 이번 달이 아닌 경우 -> 기준 달의 이전 달 전체 ~ 기준 달 전체
         */
        // 이번 달을 조회하는 경우
        if (today.getYear() == Integer.parseInt(yearMonth[0]) && today.getMonthValue() == Integer.parseInt(yearMonth[1])) {
            firstDayOfMonth = today.withDayOfMonth(1); // 이번 달 1일
            lastDayOfMonth = today;                    // 이번 달 N 일
            lastDayOfLastMonth = today.minusMonths(1); // 지난 달 N 일
            firstDayOfLastMonth = lastDayOfLastMonth.withDayOfMonth(1);   // 지난 달 1일
        } else {
            // 기준 달 1일
            firstDayOfMonth = LocalDateTime.of(Integer.parseInt(yearMonth[0]), Integer.parseInt(yearMonth[1]), 1, 0, 0, 0);
            lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.toLocalDate().lengthOfMonth());    // 기준 달 마지막 날짜
            firstDayOfLastMonth = firstDayOfMonth.minusMonths(1);    // 지난 달 1일
            lastDayOfLastMonth = firstDayOfLastMonth.withDayOfMonth(firstDayOfLastMonth.toLocalDate().lengthOfMonth()); // 지난 달 마지막 날짜
        }

        // 월별 카페인 섭취량 통계
        String monthStatus = getStatusByMonth(member, firstDayOfMonth, lastDayOfMonth, lastDayOfLastMonth, firstDayOfLastMonth);

        // 날짜별 카페인 섭취 상태
        Map<String, String> dateMap = getStatusByDate(member, firstDayOfMonth, lastDayOfMonth);

        return new CalendarResponse(monthStatus, dateMap);
    }

    /**
     * 월 카페인 섭취량 합계에 따른 섭취 상태 판단
     */
    private String getStatusByMonth(MemberResponse member,
                                    LocalDateTime firstDayOfMonth,
                                    LocalDateTime lastDayOfMonth,
                                    LocalDateTime lastDayOfLastMonth,
                                    LocalDateTime firstDayOfLastMonth) {
        // 이번 달 N 일까지의 카페인 섭취량
        int sumCaffeineByMonth = drinkMapper.findSumCaffeineByMonth(member.getMbrNo(), firstDayOfMonth, lastDayOfMonth);
        // 지난 달 N 일까지의 카페인 섭취량
        int sumCaffeineByLastMonth = drinkMapper.findSumCaffeineByMonth(member.getMbrNo(), firstDayOfLastMonth, lastDayOfLastMonth);

        if (sumCaffeineByLastMonth == -1) {
            return "없음";
        } else if (sumCaffeineByMonth > sumCaffeineByLastMonth) {
            return "증가";
        } else if (sumCaffeineByMonth == sumCaffeineByLastMonth) {
            return "동일";
        } else {
            return "감소";
        }
    }


    /**
     * 날짜별 카페인 섭취량 합계에 따른 섭취 상태 판단
     */
    private Map<String, String> getStatusByDate(MemberResponse member, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth) {
        Map<String, String> mapResponse = new HashMap<>();
        StandardValue memberStandardValue = standardValueCalculate.getMemberStandardValue(member);
        List<DateResponse> calendar = drinkMapper.findCalendar(member.getMbrNo(), firstDayOfMonth, lastDayOfMonth);

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

        return mapResponse;
    }


    /**
     * 특정 날짜의 카페인 섭취량 합계에 따른 섭취 상태 및 추천 문구
     */
    public DateStatusResponse findCaffeineByDate(MemberResponse member, LocalDateTime dateTime) {
        StandardValue memberStandardValue = standardValueCalculate.getMemberStandardValue(member);
        DateStatusResponse caffeineByDate = drinkMapper.findSumCaffeineByDate(member.getMbrNo(), dateTime,
                memberStandardValue.getMinNormal(), memberStandardValue.getMaxNormal());
        caffeineByDate.setSentence(getDateSentence(caffeineByDate.getStatus()));
        return caffeineByDate;
    }

    /**
     * 카페인 상태에 따른 문구 지정
     */
    private String getDateSentence(String status) {
        if ("없음".equals(status) || "낮음".equals(status)) {
            return DateSentence.getLowSentence();
        } else {
            return DateSentence.getHighSentence();
        }
    }

    /**
     * 특정 날짜에 따른 마신 메뉴 조회
     */
    public List<DrinkMenuResponse> findMenuByDate(MemberResponse member, LocalDateTime dateTime) {
        return drinkMapper.findMenuByDate(member.getMbrNo(), dateTime);
    }

    /**
     * 마신 메뉴 등록
     * 날짜는 현재 날짜를 기준으로함
     */
    @Transactional
    public void saveDrinkMenu(MemberResponse member, Long menuNo) {
        LocalDateTime dateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        drinkMapper.saveDrinkMenu(member.getMbrNo(), menuNo, dateTime);
    }
}