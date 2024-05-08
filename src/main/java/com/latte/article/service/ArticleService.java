package com.latte.article.service;



import com.latte.article.repository.ArticleMapper;
import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {


    @Autowired
    public ArticleMapper mapper;


    /**
     * 아티클 게시글 등록
     *
     * @param request
     */
    public void insertArticle(ArticleRequest request) {

        mapper.insertArticle(request);
    }

    /**
     * 아티클 게시글 수정
     * @param request
     */
    public void updateArticle(ArticleRequest request) {

        mapper.updateArticle(request);
    }

    /**
     * 아티클 게시글 삭제
     * @param articleNo
     */
    public void deleteArticle(int articleNo) {

        mapper.deleteArticle(articleNo);
    }


    /**
     * 아티클 목록 조회
     * @return
     */
    public List<ArticleResponse> articleList() {

        return mapper.getArticleList();
    }


    /**
     * 아티클 상세 조회
     * @param articleNo
     * @return
     */
    public ArticleResponse detailArticle(int articleNo) {

        return mapper.detailArticle(articleNo);
    }



}
