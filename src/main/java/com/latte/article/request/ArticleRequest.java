package com.latte.article.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest {


    private int articleNo;          // 아티클 번호

    private String imageUrl;        // 이미지

	private String title;           // 아티클 제목

	private String content;         // 아티클 내용

	private int writerNo;           // 작성자 번호

	private int viewCnt;           // 조회수

	private int likeCnt;            // 좋아요(추천수)

    private String deleteYn;       // 삭제여부

}
