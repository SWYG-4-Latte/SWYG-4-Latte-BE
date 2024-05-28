package com.latte.article.controller;


import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import com.latte.article.service.ArticleService;
import com.latte.member.config.SecurityUtil;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
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
    public ResponseEntity<?> write(@RequestBody ArticleRequest request, @RequestBody MultipartHttpServletRequest file) throws Exception {

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
            request.setWriterNo(member.getMbrNo());
            result = articleService.insertArticle(request, file);

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

            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);
            // 요청된 글의 작성자 확인
            boolean isAuthor = articleService.isArticleAuthor(articleNo, member.getMbrNo());

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
            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);
            // 요청된 글의 작성자 확인
            boolean isAuthor = articleService.isArticleAuthor(articleNo, member.getMbrNo());
            boolean isAdmin = false;
            MemberResponse user = authService.getMemberSeq(member.getMbrNo());
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
    public ResponseEntity<?> list(@RequestParam(value="sort", required = false) String sort, @RequestParam(value="keyword", required = false) String keyword, @PageableDefault(size = 4, page = 0) Pageable pageable) {

        String message = null;
        Page<ArticleResponse> list = articleService.articleList(sort, keyword, pageable);

        ResponseData<?> responseData = new ResponseData<>(message, list);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 아티클 상세보기
     * @param articleNo
     * @return
     */
    @GetMapping("/detail/{articleNo}")
    public ResponseEntity<?> detail(@PathVariable("articleNo") int articleNo) {

        String message = "";



        ArticleResponse articleResponse = new ArticleResponse();
        try {
            articleService.viewCount(articleNo);


            articleResponse = articleService.detailArticle(articleNo);
            message = "게시글 정보입니다.";
        } catch (Exception e) {
            throw new RuntimeException("존재하지 않는 게시글입니다.");
        }


        ResponseData<?> responseData = new ResponseData<>(message, articleResponse);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 좋아요 API
     * @param articleNo
     * @return
     */
    @PostMapping("/like/{articleNo}")
    public ResponseEntity<?> like(@PathVariable("articleNo") int articleNo) {

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> dataMap = new HashMap<>();
        boolean result = false;
        String message = "";

        // 로그인 상태 확인
        if("anonymousUser".equals(authentication)) {
            message = "로그인을 해주세요";

        } else {
            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);
            result = articleService.likeCount(articleNo, member.getMbrNo());
            ArticleResponse articleResponse = articleService.detailArticle(articleNo);
            if (!result) {
                message = "좋아요가 실패하였습니다.";

            } else {
                dataMap.put("likeCnt", articleResponse.getLikeCnt());
                message = "값이 전달되었습니다.";
            }
        }

        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 회원/아티클 좋아요 유무
     * @param articleNo
     * @return
     */
    @PostMapping("/likeYn/{articleNo}")
    public ResponseEntity<?> likeMember(@PathVariable("articleNo") int articleNo) {


        Map<String, Object> dataMap = new HashMap<>();
        boolean result = false;
        String message = "";

        // 현재 사용자 인증 정보 가져오기
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        // 로그인 상태 확인
        if("anonymousUser".equals(authentication)) {
            message = "로그인을 해주세요";

        } else {
            String mbrId = SecurityUtil.getCurrentUsername();
            MemberResponse member = authService.getMemberInfo(mbrId);
            result = articleService.likeYn(articleNo, member.getMbrNo());

            if (result) {
                message = "좋아요를 한 작성자입니다.";
            } else {
                message = "좋아요를 한 작성자가 아닙니다";
            }

            dataMap.put("likeYn", result);
        }


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
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
