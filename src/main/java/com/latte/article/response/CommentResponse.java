package com.latte.article.response;


import lombok.*;
import org.w3c.dom.Text;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class CommentResponse {

    private int commentNo;			// 댓글 번호

    private int articleNo;			// 아티클 번호

    private String content;			// 내용

    private int likeCnt;			// 좋아요(추천수)

    private int writerNo;			// 작성자 번호

    private String nickname;         // 작성자 닉네임

    private String deleteYn;		// 삭제여부

    private int reportCount;		// 신고횟수

    private Timestamp regDate;		// 등록일

    private Timestamp updateDate;	// 변경일

}
