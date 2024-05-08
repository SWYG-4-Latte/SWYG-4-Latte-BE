package com.latte.article.response;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class ArticleResponse {



    private int articleNo;          // 아티클 번호

    private String imageUrl;        // 이미지

    private String title;           // 아티클 제목

    private String content;         // 아티클 내용

    private int writerNo;           // 작성자 번호

    private int view_cnt;           // 조회수

    private int likeCnt;            // 좋아요(추천수)

    private boolean deleteYn;       // 삭제여부

    private Timestamp regDate;      // 등록일

    private Timestamp updateDate;       // 수정일


}
