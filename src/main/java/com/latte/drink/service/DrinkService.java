package com.latte.drink.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.exception.NotLoginException;
import com.latte.drink.repository.DrinkMapper;
import com.latte.drink.response.*;
import com.latte.drink.standard.DateSentence;
import com.latte.drink.standard.StandardValue;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.response.MemberResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrinkService {

    private final DrinkMapper drinkMapper;
    private final AuthMapper authMapper;
    private final StandardValueCalculate standardValueCalculate;

    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        valueOperations = redisTemplate.opsForValue();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }



    /**
     * 사용자 검증 및 Redis 에 저장
     */
    public MemberResponse isLoginMember() throws JsonProcessingException {
        log.info("##################### 사용자 검증 실행 #####################");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ("anonymousUser".equals(principal)) {
            log.info("##################### 비로그인 사용자 #####################");
            throw new NotLoginException("로그인하지 않은 사용자입니다");
        }

        User tokenUser = (User) principal;
        String username = tokenUser.getUsername();
        log.info("username = {}", username);

        String stringMember = valueOperations.get(username);
        if (StringUtils.hasText(stringMember)) {
            log.info("##################### Redis 에서 멤버 조회 #####################");
            return objectMapper.readValue(stringMember, MemberResponse.class);
        }

        log.info("##################### DB 에서 멤버 조회 #####################");
        MemberResponse memberResponse = authMapper.findById(username);
        String jsonMember = objectMapper.writeValueAsString(memberResponse);
        jsonMember = removeFieldFromJson(jsonMember, "enabled");
        jsonMember = removeFieldFromJson(jsonMember, "accountNonLocked");
        jsonMember = removeFieldFromJson(jsonMember, "accountNonExpired");
        jsonMember = removeFieldFromJson(jsonMember, "credentialsNonExpired");
        jsonMember = removeFieldFromJson(jsonMember, "authorities");
        valueOperations.set(username, jsonMember, Duration.ofMinutes(30));  // 30분동안 유효
        return memberResponse;
    }

    /**
     * Redis 저장 시, 불필요한 필드 제거
     */
    private String removeFieldFromJson(String json, String fieldName) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            ((ObjectNode) rootNode).remove(fieldName);
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }
    }




    /**
     * 홈화면 데이터
     * 닉네임, 오늘 카페인 섭취 상태, 오늘 카페인 섭취량, 기준값과의 차이, 최근 마신 음료
     */
    public HomeCaffeineResponse findHomeResponse(MemberResponse member) {
        int maxCaffeine;
        String interval, status;
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<DrinkMenuResponse> recent = drinkMapper.findHomeResponse(member.getMbrNo(), today); // 최근 마신 음료
        int todayCaffeine = drinkMapper.findSumCaffeineByToday(member.getMbrNo(), today);        // 오늘 마신 카페인 합계

        try {
            maxCaffeine = standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine();   // 카페인 섭취량 기준값
            status = maxCaffeine < todayCaffeine ? "초과" : "적정";
            interval = Math.abs(maxCaffeine - todayCaffeine) + "mg";
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