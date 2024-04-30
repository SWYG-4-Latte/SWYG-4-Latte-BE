package com.latte.drink.controller;

import com.latte.common.response.ResponseData;
import com.latte.drink.request.DrinkMenuRequest;
import com.latte.drink.response.CalendarResponse;
import com.latte.drink.response.DateStatusResponse;
import com.latte.drink.response.DrinkMenuResponse;
import com.latte.drink.service.DrinkService;
import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/drink")
@RequiredArgsConstructor
public class DrinkController {

    private final DrinkService drinkService;

    /**
     * 임시용 데이터 
     */
    private final MemberResponse member = new MemberResponse("1", "testUser", "이름", "비밀번호", "닉네임", "연락처", "이메일", Gender.M,
            false, 0, "없어요", "", "이미지", "권한", "26", "N", "3", null, null);

    /**
     * 홈화면 
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping()
    public ResponseEntity<?> homeResponse() {
        ResponseData<Object> responseData;
//        Optional<MemberResponse> memberResponse = isLogin();
//        if (memberResponse.isEmpty()) {
//            responseData = new ResponseData<>("로그인 하지 않은 사용자입니다", null);
//            return new ResponseEntity<>(responseData, HttpStatus.OK);
//        }
//        MemberResponse member = memberResponse.get();
        responseData = new ResponseData<>(null, drinkService.findHomeResponse(member));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    /**
     * 날짜별 높음, 보통, 낮음
     * 지난 달보다 카페인 섭취량이 어떻게 변했는지
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> findCaffeineByMonth(@RequestParam("datetime") String dateTime) {
        ResponseData<Object> responseData;
//        Optional<MemberResponse> memberResponse = isLogin();
//        if (memberResponse.isEmpty()) {
//            responseData = new ResponseData<>("로그인 하지 않은 사용자입니다", null);
//            return new ResponseEntity<>(responseData, HttpStatus.OK);
//        }
//        MemberResponse member = memberResponse.get();
        CalendarResponse calendar = drinkService.findCaffeineByMonth(member, dateTime);
        responseData = new ResponseData<>(null, calendar);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 날짜별 카페인 섭취량
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/date")
    public ResponseEntity<?> findCaffeineByDate(@RequestParam("datetime") LocalDateTime dateTime) {
        ResponseData<Object> responseData;
//        Optional<MemberResponse> memberResponse = isLogin();
//        if (memberResponse.isEmpty()) {
//            responseData = new ResponseData<>("로그인 하지 않은 사용자입니다", null);
//            return new ResponseEntity<>(responseData, HttpStatus.OK);
//        }
//        MemberResponse member = memberResponse.get();
        DateStatusResponse caffeineByToday = drinkService.findCaffeineByDate(member, dateTime);
        responseData = new ResponseData<>(null, caffeineByToday);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 날짜별 마신 음료 조회
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/date/menu")
    public ResponseEntity<?> findMenuByDate(@RequestParam("datetime") LocalDateTime dateTime) {
        ResponseData<Object> responseData;
//        Optional<MemberResponse> memberResponse = isLogin();
//        if (memberResponse.isEmpty()) {
//            responseData = new ResponseData<>("로그인 하지 않은 사용자입니다", null);
//            return new ResponseEntity<>(responseData, HttpStatus.OK);
//        }
//        MemberResponse member = memberResponse.get();
        List<DrinkMenuResponse> menuByToday = drinkService.findMenuByDate(member, dateTime);
        responseData = new ResponseData<>(null, menuByToday);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/date/menu")
    public ResponseEntity<?> saveDrinkMenu(@RequestBody DrinkMenuRequest drinkMenuRequest) {
        ResponseData<?> responseData;
//        Optional<MemberResponse> memberResponse = isLogin();
//        if (memberResponse.isEmpty()) {
//            responseData = new ResponseData<>("로그인 후 이용 가능합니다", null);
//            return new ResponseEntity<>(responseData, HttpStatus.FORBIDDEN);
//        }
//        MemberResponse member = memberResponse.get();
        try {
            drinkService.saveDrinkMenu(member, drinkMenuRequest.getMenuNo(), drinkMenuRequest.getDateTime());
            responseData = new ResponseData<>("기록이 완료되었습니다", null);
        } catch (DataIntegrityViolationException exception) {
            responseData = new ResponseData<>("존재하지 않는 사용자 혹은 메뉴입니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    private Optional<MemberResponse> isLogin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ("anonymousUser".equals(principal)) {
            return Optional.empty();
        }
        return Optional.of((MemberResponse) principal);
    }
}
