package com.latte.member.controller;


import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private final StandardValueCalculate standardValueCalculate;

    @Autowired
    private AuthService authService;


/*
    @GetMapping("/memberInfo")
    @ResponseBody
    public ResponseEntity<?> memberInfo(@RequestParam("mbrNo") int mbrNo) {

        Map<String, Object> dataMap = new HashMap<>();

        MemberResponse member = authService.getMemberSeq(mbrNo);
        String gender;
        String preg;
        String pregMonth;
        String allergy;
        // 성별
        gender = Objects.equals(String.valueOf(member.getGender()), "") ? null : String.valueOf(member.getGender());
        // 임신 여부
        preg = Objects.equals(String.valueOf(member.isPregnancy()), "") ? null : String.valueOf(member.isPregnancy());
        // 임신 개월 수
        pregMonth = Objects.equals(String.valueOf(member.getPregMonth()), "") ? null : String.valueOf(member.getPregMonth());
        // 적정 카페인량

        String maxCaffeine;
        try{
            maxCaffeine = String.valueOf(standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine());
        } catch (NotEnoughInfoException ignored) {

            maxCaffeine = null;
        }

        // 알레르기 정보
        allergy = Objects.equals(String.valueOf(member.getAllergy()), "") ? null : String.valueOf(member.getAllergy());


        dataMap.put("gender " , gender);
        dataMap.put("pregnancy ", preg);
        dataMap.put("pregMonth ", pregMonth);
        dataMap.put("caffeinIntake ", maxCaffeine);
        dataMap.put("allergy ", allergy);

        ResponseData<?> responseData = new ResponseData<>(null, dataMap);
        return new ResponseEntity<>(responseData, OK);

    }
*/


    /**
     * 토큰을 통한 회원정보
     * @param token
     * @return
     */
    @GetMapping("/tokenInfo")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        // Authorization 헤더에서 토큰 추출 (Bearer 토큰)
        String jwtToken = token; // "Bearer " 이후의 토큰 부분만 추출

        String message = "";
        Map<String, Object> dataMap = new HashMap<>();

        // 토큰을 사용하여 회원 정보 확인
        MemberResponse member = authService.getMemberInfoFromToken(jwtToken);
        if (member != null) {

            String maxCaffeine = String.valueOf(standardValueCalculate.getMemberStandardValue(member).getMaxCaffeine());

            dataMap.put("member", member);
            dataMap.put("caffeinIntake", maxCaffeine);
            message = "회원 정보입니다.";

        } else {
            message = String.valueOf(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token"));
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token"); // 토큰이 유효하지 않은 경우 401 Unauthorized 반환
        }


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }





}
