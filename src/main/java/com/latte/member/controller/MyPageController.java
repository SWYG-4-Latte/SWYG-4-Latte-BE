package com.latte.member.controller;


import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/myPage")
public class MyPageController {

    @Autowired
    private final StandardValueCalculate standardValueCalculate;

    @Autowired
    private AuthService authService;


    @PostMapping("/memberInfo")
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

        dataMap.put("성별" , gender);
        dataMap.put("임신여부", preg);
        dataMap.put("임신개월수", pregMonth);
        dataMap.put("적정 카페인량", maxCaffeine);
        dataMap.put("알레르기", allergy);

        ResponseData<?> responseData = new ResponseData<>(null, dataMap);
        return new ResponseEntity<>(responseData, OK);

    }





}
