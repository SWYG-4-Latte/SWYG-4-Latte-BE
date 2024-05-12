package com.latte.article.repository;

import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {


    // 아티클 등록
    boolean insertArticle(ArticleRequest request);

    // 아티클 수정
    boolean updateArticle(ArticleRequest request);

    // 아티클 삭제
    boolean deleteArticle(int seq);

    // 아티클 목록 조회
    List<ArticleResponse> getArticleList();

    // 아티클 상세보기
    ArticleResponse detailArticle(int articleNo);

    // 아티클 작성자 확인
    int isArticleAuthor(@Param("articleNo") int articleNo, @Param("writerNo") int writerNo);

    // 아티클 조회수 증가
    void viewCount(int articleNo);

}
