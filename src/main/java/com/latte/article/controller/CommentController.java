package com.latte.article.controller;


import com.latte.article.request.ArticleRequest;
import com.latte.article.request.CommentRequest;
import com.latte.article.response.ArticleResponse;
import com.latte.article.response.CommentResponse;
import com.latte.article.service.CommentService;
import com.latte.member.config.SecurityUtil;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    public AuthService authService;

    @Autowired
    public CommentService commentService;


    /**
     * 댓글 작성 API
     * @param articleNo
     * @param request
     * @return
     */
    @PostMapping("/write/{articleNo}")
    @ResponseBody
    public ResponseEntity<?> write(@PathVariable("articleNo") int articleNo, @RequestBody CommentRequest request) {
        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> dataMap = new HashMap<>();
        String message ="";
        boolean result = false;

        if("anonymousUser".equals(authentication)) {
            message = "로그인 후 등록해주세요";
        } else {
            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);
            request.setArticleNo(articleNo);
            request.setWriterNo(member.getMbrNo());

            result = commentService.saveComment(request);
            CommentResponse comment = commentService.findCommentById(request.getCommentNo());
            dataMap.put("commentInfo", comment);
            if(result) {
                message = "댓글 등록이 완료되었습니다";
            } else {
                message = "댓글 등록에 실패하였습니다";
            }

        }

        dataMap.put("resultYn", result);


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 댓글 수정 API
     * @param commentNo
     * @param request
     * @return
     */
    @PostMapping("/update/{commentNo}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable("commentNo") int commentNo, @RequestBody CommentRequest request) {
        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> dataMap = new HashMap<>();
        String message ="";
        boolean result = false;

        if("anonymousUser".equals(authentication)) {
            message = "로그인 후 등록해주세요";
        } else {
            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);

            // 요청된 글의 작성자 확인
            boolean isAuthor = commentService.isCommentAuthor(commentNo, member.getMbrNo());

            // 작성자인 경우에만 수정 권한 부여
            if (isAuthor) {
                request.setCommentNo(commentNo);

                result = commentService.updateComment(request);
                CommentResponse comment = commentService.findCommentById(commentNo);
                dataMap.put("commentInfo", comment);
                if(result) {
                    message = "댓글 수정이 완료되었습니다";
                } else {
                    message = "댓글 수정에 실패하였습니다";
                }
            } else {
                message = "글 작성자가 아닙니다";
            }

        }

        dataMap.put("resultYn", result);


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 댓글 삭제 API
     * @param commentNo
     * @return
     */
    @DeleteMapping("/delete/{commentNo}")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable("commentNo") int commentNo) {

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean result = false;
        String message = "";

        // 로그인 상태 확인
        if("anonymousUser".equals(authentication)) {
            message = "로그인을 해주세요";

        } else {

            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);
            // 요청된 댓글의 작성자 확인
            boolean isAuthor = commentService.isCommentAuthor(commentNo, member.getMbrNo());
            boolean isAdmin = false;
            MemberResponse user = authService.getMemberSeq(member.getMbrNo());
            // 관리자일 경우
            if(user.getRole().equals("ADMIN")) {
                isAdmin = true;
            }

            // 작성자 혹은 관리자인 경우에만 수정 권한 부여
            if (isAuthor || isAdmin) {
                result = commentService.deleteComment(commentNo);

                if (result) {
                    message = "아티클 삭제에 성공했습니다.";
                } else {
                    message = "아티클 삭제에 실패했습니다.";
                }
            } else {
                message = "글 작성자가 아닙니다";
            }
        }

        ResponseData<?> responseData = new ResponseData<>(message, result);
        return new ResponseEntity<>(responseData, OK);
    }


    @GetMapping("/list/{articleNo}")
    public ResponseEntity<?> list(@PathVariable("articleNo") int articleNo) {

        String message = "아티클 "+articleNo +"번의 댓글 리스트 입니다.";

        List<CommentResponse> list = commentService.findAllComment(articleNo);


        ResponseData<?> responseData = new ResponseData<>(message, list);
        return new ResponseEntity<>(responseData, OK);
    }



    @PostMapping("/report/{commentNo}")
    @ResponseBody
    public ResponseEntity<?> report(@PathVariable("commentNo") int commentNo) {
        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String message ="";
        boolean result = false;

        if("anonymousUser".equals(authentication)) {
            message = "로그인 후 등록해주세요";
        } else {
            //int mbrNo = Integer.parseInt(SecurityUtil.getCurrentUsername());

            result = commentService.commentReport(commentNo);

            if(result) {
                message = "댓글 신고를 완료했습니다.";
            } else {
                message = "댓글 신고를 실패하였습니다.";
            }

        }



        ResponseData<?> responseData = new ResponseData<>(message, result);
        return new ResponseEntity<>(responseData, OK);
    }



}
