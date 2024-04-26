package com.latte.drink.controller;

import com.latte.common.response.ResponseData;
import com.latte.drink.response.CalendarResponse;
import com.latte.drink.response.DateStatusResponse;
import com.latte.drink.response.DrinkMenuResponse;
import com.latte.drink.service.DrinkService;
import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/drink")
@RequiredArgsConstructor
public class DrinkController {

    private final DrinkService drinkService;

    /**
     * 임시용 데이터 
     */
    /*
    private final MemberResponse member = new MemberResponse("1", "이름", "비밀번호", "닉네임", "연락처", "이메일", Gender.M,
            false, 0, "", "", "이미지", "권한", "26", "N", "3", null, null);
    */

    /**
     * 날짜별 높음, 보통, 낮음
     * 지난 달보다 카페인 섭취량이 어떻게 변했는지
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> findCaffeineByMonth(@RequestParam("datetime") LocalDateTime dateTime) {
        MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CalendarResponse calendar = drinkService.findCaffeineByMonth(member, dateTime);
        ResponseData<?> dateResponse = new ResponseData<>(null, calendar);
        return new ResponseEntity<>(dateResponse, HttpStatus.OK);
    }

    /**
     * 날짜별 카페인 섭취량
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/date")
    public ResponseEntity<?> findCaffeineByDate(@RequestParam("datetime") LocalDateTime dateTime) {
        MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DateStatusResponse caffeineByToday = drinkService.findCaffeineByDate(member, dateTime);
        ResponseData<?> dateResponse = new ResponseData<>(null, caffeineByToday);
        return new ResponseEntity<>(dateResponse, HttpStatus.OK);
    }

    /**
     * 날짜별 마신 음료 조회
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/date/menu")
    public ResponseEntity<?> findMenuByDate(@RequestParam("datetime") LocalDateTime dateTime) {
        MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<DrinkMenuResponse> menuByToday = drinkService.findMenuByDate(member, dateTime);
        ResponseData<?> menuDate = new ResponseData<>(null, menuByToday);
        return new ResponseEntity<>(menuDate, HttpStatus.OK);
    }
}
