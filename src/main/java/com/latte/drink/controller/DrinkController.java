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
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseData<Object> responseData = new ResponseData<>(null, null);
//        if ("anonymousUser".equals(principal)) {
//            responseData.setMessage("로그인 하지 않은 사용자입니다");
//            return new ResponseEntity<>(responseData, HttpStatus.OK);
//        }
//        MemberResponse member = (MemberResponse) principal;
        responseData.setData(drinkService.findHomeResponse(member));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    /**
     * 날짜별 높음, 보통, 낮음
     * 지난 달보다 카페인 섭취량이 어떻게 변했는지
     * 일단 완성, 주석 해제 필요
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> findCaffeineByMonth(@RequestParam("datetime") String dateTime) {
        //MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        //MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        //MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<DrinkMenuResponse> menuByToday = drinkService.findMenuByDate(member, dateTime);
        ResponseData<?> menuDate = new ResponseData<>(null, menuByToday);
        return new ResponseEntity<>(menuDate, HttpStatus.OK);
    }

    @PostMapping("/date/menu")
    public ResponseEntity<?> saveDrinkMenu(@RequestBody DrinkMenuRequest drinkMenuRequest) {
        //MemberResponse member = (MemberResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseData<?> responseData = new ResponseData<>(null, null);
        try {
            drinkService.saveDrinkMenu(member, drinkMenuRequest.getMenuNo());
            responseData.setMessage("기록이 완료되었습니다");
        } catch (DataIntegrityViolationException exception) {
            responseData.setMessage("존재하지 않는 사용자 혹은 메뉴입니다");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
