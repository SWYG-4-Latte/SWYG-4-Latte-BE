package com.latte.article.service;



import com.latte.article.repository.ArticleMapper;
import com.latte.article.request.ArticleRequest;
import com.latte.article.request.LikeRequest;
import com.latte.article.response.ArticleResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleService {


    @Autowired
    public ArticleMapper mapper;

    @Autowired
    public AuthService authService;

    /**
     * 아티클 게시글 등록
     *
     * @param request
     */
    @Transactional
    public boolean insertArticle(ArticleRequest request, MultipartHttpServletRequest file) throws Exception {

        request.setDeleteYn("N");
        request.setLikeCnt(0);
        request.setViewCnt(0);


        return mapper.insertArticle(request);
    }


    /**
     * 아티클 게시글 수정
     * @param request
     */
    public boolean updateArticle(ArticleRequest request) {

        request.setDeleteYn("N");

        return mapper.updateArticle(request);
    }

    /**
     * 아티클 게시글 삭제
     * @param articleNo
     */
    public boolean deleteArticle(int articleNo) {


        return mapper.deleteArticle(articleNo);

    }


    /**
     * 아티클 목록 조회
     * @return
     */
    @Transactional
    public Page<ArticleResponse> articleList(@Param("sort") String sort, @Param("keyword") String keyword, @Param("pageable") Pageable pageable) {

        // 페이지 수
        int totalCount = mapper.totalCount(keyword);

        List<ArticleResponse> list =  mapper.getArticleList(sort, keyword, pageable);
        for (ArticleResponse article : list) {
            Map<String, String> imgMap = new HashMap<>();
            MemberResponse member = authService.getMemberSeq(article.getWriterNo());
            article.setNickname(member.getNickname());

            if(article.getImageUrl() != null) {
                List<String> data = new ArrayList<>();
                data = List.of(article.getImageUrl().split(","));

                for(int i = 0; i < data.size(); i++) {
                    String img = data.get(i);
                    imgMap.put("imgUrl"+(i+1), img);
                }


                article.setImages(imgMap);
            }

        }

        return new PageImpl<>(list, pageable, totalCount);
    }


    /**
     * 아티클 상세 조회
     * @param articleNo
     * @return
     */
    public ArticleResponse detailArticle(int articleNo) {

        ArticleResponse user = new ArticleResponse();
        Map<String, String> imgMap = new HashMap<>();

        user = mapper.detailArticle(articleNo);



        if(user.getImageUrl() != null) {

            List<String> data = new ArrayList<>();
            data = List.of(user.getImageUrl().split(","));

            for(int i = 0; i < data.size(); i++) {
                String img = data.get(i);
                imgMap.put("imgUrl"+(i+1), img);
            }

            user.setImages(imgMap);
        }

        MemberResponse member = authService.getMemberSeq(user.getWriterNo());
        user.setNickname(member.getNickname());

        return user;
    }

    /**
     * 아티클 작성자 확인
     * @param writerNo
     * @return
     */
    public boolean isArticleAuthor(@Param("articleNo") int articleNo, @Param("writerNo") int writerNo) {

        int result = mapper.isArticleAuthor(articleNo, writerNo);

        // 작성자가 맞음
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 아티클 조회수 증가
     * @param articleNo
     */
    public void viewCount(int articleNo) {

        mapper.viewCount(articleNo);
    }


    /**
     * 아티클 좋아요
     * @param articleNo
     */
    @Transactional
    public boolean likeCount(int articleNo, int regNo) {

        // 작성자 여부 확인
        Integer likeNo = mapper.findLikeByArticleRegNo(articleNo, regNo);


        //MemberResponse memberResponse = authService.getMemberSeq(mbrNo);

        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setArticleNo(articleNo);
        likeRequest.setRegNo(regNo);


        // 해당 아티클에 작성자가 like를 누른 적이 없을 때

        if(likeNo == null) {
            mapper.likeCount(articleNo, regNo);
        } else {

            mapper.unlikeCount(likeNo);
        }

        int res = mapper.updateArticleLike(articleNo);

        if(res > 0) {
            return true;
        } else {
            return false;
        }


    }




}
