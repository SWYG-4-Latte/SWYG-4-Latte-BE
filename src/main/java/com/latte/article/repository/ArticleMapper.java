package com.latte.article.repository;

import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

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
    List<ArticleResponse> getArticleList(@Param("sort") String sort, @Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    // 아티클 상세보기
    ArticleResponse detailArticle(int articleNo);

    // 아티클 게시물 수
    int totalCount(@Param("keyword") String keyword);

    // 아티클 작성자 확인
    int isArticleAuthor(@Param("articleNo") int articleNo, @Param("writerNo") int writerNo);

    // 아티클 조회수 증가
    void viewCount(int articleNo);

    // 아티클 좋아요 수 추가
    void likeCount(@Param("articleNo") int articleNo, @Param("regNo") int regNo);

    // 아티클 좋아요 삭제
    void unlikeCount(int likeNo);

    // 아티클 테이블에 좋아요 수정
    int updateArticleLike(@Param("articleNo") int articleNo);

    // 작성자가 좋아요를 눌렀는지 여부 확인
    Integer findLikeByArticleRegNo(@Param("articleNo") int articleNo, @Param("regNo") int regNo);

    // 작성자/아티클 좋아요 여부
    int findLikeYnByArticleRegNo(@Param("articleNo") int articleNo, @Param("regNo") int regNo);

}


