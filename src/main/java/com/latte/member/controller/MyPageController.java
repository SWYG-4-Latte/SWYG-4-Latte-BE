package com.latte.member.controller;


import com.latte.article.response.CommentResponse;
import com.latte.article.service.ArticleService;
import com.latte.article.service.CommentService;
import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.drink.standard.StandardValueCalculate;
import com.latte.member.config.SecurityUtil;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;


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
     * @param
     * @return
     */
    @GetMapping("/tokenInfo")
    public ResponseEntity<?> getUserInfo() {
        // Authorization 헤더에서 토큰 추출 (Bearer 토큰)
        //String jwtToken = token.substring(7); // "Bearer " 이후의 토큰 부분만 추출

        String mbrId = SecurityUtil.getCurrentUsername();
        String message = "";
        Map<String, Object> dataMap = new HashMap<>();
        MemberResponse member = null;
        if(mbrId.contains("@")) {
            FindIdResponse find = authService.findIdByNameEmail(mbrId);
            member = authService.getMemberInfo(find.getMbrId());
        } else {
            member = authService.getMemberInfo(mbrId);
        }

        // 토큰을 사용하여 회원 정보 확인
        //MemberResponse member = authService.getMemberInfoFromToken(jwtToken);

        if (member != null) {
            String maxCaffeine;
            if(member.getDeleteYn().equals("N")) {
                try {
                    maxCaffeine = String.valueOf(standardValueCalculate.getMemberStandardValue(member).getMaxNormal());
                } catch (NotEnoughInfoException e) {
                    maxCaffeine = null;
                }


                dataMap.put("member", member);
                dataMap.put("caffeinIntake", maxCaffeine);
                message = "회원 정보입니다.";
            } else {
                message = "탈퇴한 회원입니다";
            }

        } else {
            message = String.valueOf(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token"));
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token"); // 토큰이 유효하지 않은 경우 401 Unauthorized 반환
        }


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 댓글 리스트 조회(유저)
     * @return sort 정렬
     */
    @GetMapping("/myCommentList")
    public ResponseEntity<?> myCommentList(@RequestParam(value="sort", required = false) String sort) {

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String mbrId = SecurityUtil.getCurrentUsername();
        String message = "";
        List<Map<String, String>> commentDataList = new ArrayList<>();

        if(mbrId.contains("@")) {
            FindIdResponse find = authService.findIdByNameEmail(mbrId);
            mbrId = find.getMbrId();
        }
        // 댓글 개수
        Map<String, String> userCount = new HashMap<>();
        String count = String.valueOf(commentService.userCommentCount(mbrId));
        userCount.put("userCount", count);


        // 로그인 상태 확인
        if("anonymousUser".equals(authentication)) {
            message = "로그인을 해주세요";

        } else {
            message = mbrId + "의 댓글 리스트입니다.";
            List<CommentResponse> commentList = commentService.commentListByMember(sort, mbrId);

            for (CommentResponse comment : commentList) {
                // 댓글 내용
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("content", comment.getContent());
                dataMap.put("title", comment.getTitle());
                dataMap.put("likeCnt", String.valueOf(comment.getLikeCnt()));
                dataMap.put("regDate", String.valueOf(comment.getRegDate()));
                dataMap.put("updateDate", String.valueOf(comment.getUpdateDate()));
                commentDataList.add(dataMap);
            }
            commentDataList.add(userCount);

        }

        ResponseData<?> responseData = new ResponseData<>(message, commentDataList);
        return new ResponseEntity<>(responseData, OK);
    }






}
