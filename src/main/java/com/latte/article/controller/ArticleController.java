package com.latte.article.controller;


import com.fasterxml.jackson.core.JsonToken;
import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import com.latte.article.service.ArticleService;
import com.latte.member.config.SecurityUtil;
import com.latte.member.config.jwt.JwtAuthenticationFilter;
import com.latte.member.config.jwt.JwtToken;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    public ArticleService articleService;

    @Autowired
    public AuthService authService;




    /**
     * 아티클 등록
     * @param request
     * @return
     */
    @PostMapping("/write")
    @ResponseBody
    public ResponseEntity<?> write(@RequestBody ArticleRequest request) {

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> dataMap = new HashMap<>();
        String message ="";
        boolean result = false;

        if("anonymousUser".equals(authentication)) {
            message = "로그인 후 등록해주세요";
        } else {
            int mbrNo = Integer.parseInt(SecurityUtil.getCurrentUsername());
            request.setWriterNo(mbrNo);
            result = articleService.insertArticle(request);

            ArticleResponse article = articleService.detailArticle(request.getArticleNo());
            dataMap.put("articleInfo", article);
            if(result) {
                message = "아티클 등록에 성공하였습니다.";
            } else {
                message = "아티클 등록에 실패하였습니다.";
            }
        }

        dataMap.put("resultYn", result);

        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);

    }


    /**
     * 아티클 수정
     * @param articleNo
     * @param request
     * @return
     */
    @PostMapping("/update/{articleNo}")
    public ResponseEntity<?> update(@PathVariable("articleNo") int articleNo, @RequestBody @Validated final ArticleRequest request) {

        String message = "";
        String data = "";

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> dataMap = new HashMap<>();
        boolean result = false;

        // 로그인 상태 확인
        if("anonymousUser".equals(authentication)) {
            message = "로그인을 해주세요";

        } else {

            int mbrNo = Integer.parseInt(SecurityUtil.getCurrentUsername());
            // 요청된 글의 작성자 확인
            boolean isAuthor = articleService.isArticleAuthor(articleNo, mbrNo);

            // 작성자인 경우에만 수정 권한 부여
            if (isAuthor) {
                request.setArticleNo(articleNo);
                result = articleService.updateArticle(request);
                ArticleResponse article = articleService.detailArticle(request.getArticleNo());

                dataMap.put("articleInfo", article);
                if(result) {
                    message = "아티클 수정에 성공하였습니다.";
                } else {
                    message = "아티클 수정에 실패하였습니다.";
                }

            } else {
                message = "글 작성자가 아닙니다";
            }

            dataMap.put("resultYn", result);

        }



        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 아티클 삭제
     * @param articleNo
     * @return
     */
    @DeleteMapping("/delete/{articleNo}")
    @ResponseBody
    public ResponseEntity<?> deleteArticle(@PathVariable("articleNo") int articleNo) {

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean result = false;
        String message = "";

        // 로그인 상태 확인
        if("anonymousUser".equals(authentication)) {
            message = "로그인을 해주세요";

        } else {

            int mbrNo = Integer.parseInt(SecurityUtil.getCurrentUsername());
            // 요청된 글의 작성자 확인
            boolean isAuthor = articleService.isArticleAuthor(articleNo, mbrNo);
            boolean isAdmin = false;
            MemberResponse user = authService.getMemberSeq(mbrNo);
            // 관리자일 경우
            if(user.getRole().equals("ADMIN")) {
                isAdmin = true;
            }

            // 작성자 혹은 관리자인 경우에만 수정 권한 부여
            if (isAuthor || isAdmin) {
                result = articleService.deleteArticle(articleNo);

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


    /**
     * 아티클 목록 조회
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<?> list() {

        String message = "";

        List<ArticleResponse> list = articleService.articleList();

        ResponseData<?> responseData = new ResponseData<>(message, list);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 아티클 상세보기
     * @param articleNo
     * @return
     */
    @GetMapping("/detail/{mbrNo}")
    public ResponseEntity<?> detail(@PathVariable("mbrNo") int articleNo) {

        String message = "";


        articleService.viewCount(articleNo);
        ArticleResponse articleResponse = articleService.detailArticle(articleNo);
        if(articleResponse == null ) {
            message = "삭제되거나 존재하지 않는 게시글입니다.";
        } else {

            message = "게시글 정보입니다.";
        }

        ResponseData<?> responseData = new ResponseData<>(message, articleResponse);
        return new ResponseEntity<>(responseData, OK);
    }



    /*    *//* 파일 삭제 *//*
    @GetMapping("/deleteFile/{noticeId}/{fileId}")
    public String deleteFile(@PathVariable Long noticeId, @PathVariable Long fileId) {

        boardService.deleteFile(noticeId, fileId);

        return "redirect:/board/edit/{noticeId}";
    }


    *//* 파일 다운로드 *//*
    @GetMapping("/{seq}/{notice}")
    public void downloadFile(@PathVariable("seq") long fileId, @PathVariable("notice") long noticeId, HttpServletResponse response) throws IOException {

        FileRequest boardFile = boardService.selectBoardFileInformation(fileId, noticeId);
        if (ObjectUtils.isEmpty(boardFile) == false) {
            String fileName = boardFile.getFileName();

            byte[] files = FileUtils.readFileToByteArray(new File(boardFile.getStoredFilePath()));

            response.setContentType("application/octet-stream");
            response.setContentLength(files.length);
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");

            response.getOutputStream().write(files);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
    }*/





}
