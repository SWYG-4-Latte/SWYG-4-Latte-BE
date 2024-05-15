package com.latte.article.repository;

import com.latte.article.request.CommentRequest;
import com.latte.article.response.CommentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CommentMapper {


    // 댓글 저장
    int save(CommentRequest request);


    // 댓글 상세정보 조회
    CommentResponse findByComment(int commentNo);


    // 댓글 수정
    int update(CommentRequest commentRequest);


    // 댓글 삭제
    int deleteByComment(int commentNo);


    // 댓글 리스트 조회
    List<CommentResponse> findAll(int articleNo);


    // 댓글 수 카운팅
    int count(int articleNo);

    // 아티클 작성자 확인
    int isCommentAuthor(@Param("commentNo") int commentNo, @Param("writerNo") int writerNo);


     // 댓글 신고 증가
    int reportCount(int commentNo);


    // 댓글 좋아요 수 추가
    void likeCount(@Param("commentNo") int commentNo, @Param("articleNo") int articleNo, @Param("regNo") int regNo);

    // 댓글 좋아요 삭제
    void unlikeCount(int likeNo);

    // 댓글 테이블에 좋아요 수정
    int updateCommentLike(@Param("commentNo") int commentNo);

    // 작성자가 좋아요를 눌렀는지 여부 확인
    Integer findLikeByCommentRegNo(@Param("commentNo") int commentNo, @Param("regNo") int regNo);

    CommentResponse detailComment(int commentNo);
}
