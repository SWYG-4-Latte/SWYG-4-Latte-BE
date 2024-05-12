package com.latte.article.response;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Text;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class CommentResponse {

    private int commentNo;			// 댓글 번호

    private int articleNo;			// 아티클 번호

    private Text content;			// 내용

    private int likeCnt;			// 좋아요(추천수)

    private int coWriter;			// 작성자 번호


    private boolean deleteYn;		// 삭제여부

    private int reportCount;		// 신고횟수

    private Timestamp reqDate;		// 등록일

    private Timestamp updateDate;	// 변경일

}
