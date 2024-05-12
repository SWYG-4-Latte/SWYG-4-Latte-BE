package com.latte.article.request;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeRequest {


    private int coLikeNo;         // 댓글 좋아요 번호

    private int commentNo;        // 댓글 번호

    private int articleNo;      // 아티클 번호

    private int regNo;          // 등록자 번호

    private String regDate;     // 좋아요 등록일


}
