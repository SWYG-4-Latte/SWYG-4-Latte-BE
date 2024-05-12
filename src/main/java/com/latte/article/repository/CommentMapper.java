package com.latte.article.repository;

import com.latte.article.request.CommentRequest;
import com.latte.article.response.CommentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CommentMapper {

    /**
     * 댓글 저장
     *
     * @param request - 댓글 정보
     * @return
     */
    int save(CommentRequest request);

    /**
     * 댓글 상세정보 조회
     * @param commentNo - PK
     * @return 댓글 상세정보
     */
    CommentResponse findByComment(int commentNo);

    /**
     * 댓글 수정
     * @param commentRequest - 댓글 정보
     */
    int update(CommentRequest commentRequest);

    /**
     * 댓글 삭제
     * @param commentNo - PK
     */
    int deleteByComment(int commentNo);

    /**
     * 댓글 리스트 조회
     * @param articleNo - 게시글 번호 (FK)
     * @return 댓글 리스트
     */
    List<CommentResponse> findAll(int articleNo);

    /**
     * 댓글 수 카운팅
     * @param articleNo - 게시글 번호 (FK)
     * @return 댓글 수
     */
    int count(int articleNo);

    // 아티클 작성자 확인
    int isCommentAuthor(@Param("commentNo") int commentNo, @Param("writerNo") int writerNo);

    /**
     * 댓글 신고 증가
     * @param commentNo
     * @return
     */
    int reportCount(int commentNo);
}
