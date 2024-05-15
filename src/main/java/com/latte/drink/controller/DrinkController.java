package com.latte.drink.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.latte.common.response.ResponseData;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.exception.NotLoginException;
import com.latte.drink.request.DrinkMenuRequest;
import com.latte.drink.response.CalendarResponse;
import com.latte.drink.response.DateStatusResponse;
import com.latte.drink.response.DrinkMenuResponse;
import com.latte.drink.service.DrinkService;
import com.latte.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * 홈화면
     * 오늘 마신 카페인, 남은 혹은 초과한 카페인
     * 최근 마신 음료 ( 최대 5개 )
     */
    @GetMapping()
    public ResponseEntity<?> homeResponse() {
        ResponseData<?> responseData;
        try {
            MemberResponse member = drinkService.isLoginMember();
            responseData = new ResponseData<>(null, drinkService.findHomeResponse(member));
        } catch (NotLoginException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("사용자 검증에 실패하였습니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    /**
     * 날짜별 높음, 보통, 낮음
     * 지난 달보다 카페인 섭취량이 어떻게 변했는지
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> findCaffeineByMonth(@RequestParam("datetime") String dateTime) {
        ResponseData<?> responseData;
        try {
            MemberResponse member = drinkService.isLoginMember();
            CalendarResponse calendar = drinkService.findCaffeineByMonth(member, dateTime);
            responseData = new ResponseData<>(null, calendar);
        } catch (NotLoginException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        } catch (NotEnoughInfoException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("사용자 검증에 실패하였습니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 날짜별 카페인 섭취량
     */
    @GetMapping("/date")
    public ResponseEntity<?> findCaffeineByDate(@RequestParam("datetime") LocalDateTime dateTime) {
        ResponseData<?> responseData;
        try {
            MemberResponse member = drinkService.isLoginMember();
            DateStatusResponse caffeineByToday = drinkService.findCaffeineByDate(member, dateTime);
            responseData = new ResponseData<>(null, caffeineByToday);
        } catch (NotLoginException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        } catch (NotEnoughInfoException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("사용자 검증에 실패하였습니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 날짜별 마신 음료 조회
     * 부가정보 입력하지 않아도 가능
     */
    @GetMapping("/date/menu")
    public ResponseEntity<?> findMenuByDate(@RequestParam("datetime") LocalDateTime dateTime) {
        ResponseData<?> responseData;
        try {
            MemberResponse member = drinkService.isLoginMember();;
            List<DrinkMenuResponse> menuByToday = drinkService.findMenuByDate(member, dateTime);
            responseData = new ResponseData<>(null, menuByToday);
        } catch (NotLoginException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("사용자 검증에 실패하였습니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 마신 메뉴 등록
     * 부가정보 입력하지 않아도 가능
     */
    @PostMapping("/date/menu")
    public ResponseEntity<?> saveDrinkMenu(@RequestBody DrinkMenuRequest drinkMenuRequest) {
        ResponseData<?> responseData;
        try {
            MemberResponse member = drinkService.isLoginMember();
            drinkService.saveDrinkMenu(member, drinkMenuRequest.getMenuNo());
            responseData = new ResponseData<>("기록이 완료되었습니다", null);
        } catch (NotLoginException exception) {
            responseData = new ResponseData<>(exception.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        } catch (DataIntegrityViolationException exception) {
            responseData = new ResponseData<>("존재하지 않는 사용자 혹은 메뉴입니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException exception) {
            responseData = new ResponseData<>("사용자 검증에 실패하였습니다", null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}