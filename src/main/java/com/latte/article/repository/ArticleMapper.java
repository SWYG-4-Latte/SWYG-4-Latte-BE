package com.latte.article.repository;

import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper {


    // 아티클 등록
    void insertArticle(ArticleRequest request);

    // 아티클 수정
    void updateArticle(ArticleRequest request);

    // 아티클 삭제
    void deleteArticle(int seq);

    // 아티클 목록 조회
    List<ArticleResponse> getArticleList();

    // 아티클 상세보기
    ArticleResponse detailArticle(int articleNo);


}
