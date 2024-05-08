package com.latte.article.controller;


import com.latte.article.request.ArticleRequest;
import com.latte.article.service.ArticleService;
import com.latte.member.config.jwt.JwtToken;
import com.latte.response.ResponseData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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


    /**
     * 아티클 등록
     * @param request
     * @return
     */
    @PostMapping("/write")
    @ResponseBody
    public ResponseEntity<?> write(ArticleRequest request) {

        articleService.insertArticle(request);


        ResponseData<?> responseData = new ResponseData<>(null, null);
        return new ResponseEntity<>(responseData, OK);

    }






}
