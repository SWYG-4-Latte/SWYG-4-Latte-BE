package com.latte.article.response;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Text;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class ArticleResponse {



    private int articleNo;          // 아티클 번호

    private String imageUrl;        // 이미지

    private Map<String, String> images;       // 이미지 분리

    private String title;           // 아티클 제목

    private String subTitle;        // 아티클 서브 제목

    private String content;         // 아티클 내용

    private int writerNo;           // 작성자 번호

    private String nickname;           // 작성자 닉네임

    private int viewCnt;           // 조회수

    private int likeCnt;            // 좋아요(추천수)

    private String deleteYn;       // 삭제여부

    private Timestamp regDate;      // 등록일

    private Timestamp updateDate;       // 수정일


}
