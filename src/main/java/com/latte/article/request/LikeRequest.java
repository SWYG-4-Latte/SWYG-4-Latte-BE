package com.latte.article.request;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeRequest {


    private int likeNo;         // 아티클 좋아요 번호

    private int articleNo;      // 아티클 번호

    private int regNo;          // 등록자 번호

    private String regDate;     // 좋아요 등록일


}
