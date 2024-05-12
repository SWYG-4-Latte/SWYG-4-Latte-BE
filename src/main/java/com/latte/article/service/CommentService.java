package com.latte.article.service;


import com.latte.article.repository.CommentMapper;
import com.latte.article.request.CommentRequest;
import com.latte.article.response.CommentResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    private final AuthService authService;


    /**
     * 댓글 저장
     * @param request - 댓글 정보
     * @return Generated PK
     */
    @Transactional
    public boolean saveComment(final CommentRequest request) {

        int result = commentMapper.save(request);
        if(result > 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 댓글 상세정보 조회
     * @param commentNo - PK
     * @return 댓글 상세정보
     */
    public CommentResponse findCommentById(final int commentNo) {
        return commentMapper.findByComment(commentNo);
    }

    /**
     * 댓글 수정
     * @param request - 댓글 정보
     * @return PK
     */
    @Transactional
    public boolean updateComment(final CommentRequest request) {

        int result = commentMapper.update(request);
        if(result > 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 댓글 삭제
     * @param commentNo - PK
     * @return PK
     */
    @Transactional
    public boolean deleteComment(final int commentNo) {

        int result = commentMapper.deleteByComment(commentNo);
        if(result > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 댓글 리스트 조회
     * @param articleNo - 게시글 번호 (FK)
     * @return 특정 게시글에 등록된 댓글 리스트
     */
    public List<CommentResponse> findAllComment(final int articleNo) {

        List<CommentResponse> list = commentMapper.findAll(articleNo);
        for (CommentResponse comment : list) {
            MemberResponse member = authService.getMemberSeq(comment.getWriterNo());
            comment.setNickname(member.getNickname());
        }

        return list;
    }


    /**
     * 댓글 작성자 확인
     * @param commentNo
     * @param writerNo
     * @return
     */
    public boolean isCommentAuthor(@Param("commentNo") int commentNo, @Param("writerNo") int writerNo) {

        int result = commentMapper.isCommentAuthor(commentNo, writerNo);

        // 작성자가 맞음
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 댓글 신고
     * @param commentNo
     * @return
     */
    public boolean commentReport(@Param("commentNo") int commentNo) {

        int result = commentMapper.reportCount(commentNo);

        // 신고 완료
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

}
