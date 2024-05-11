package com.latte.article.service;



import com.latte.article.repository.ArticleMapper;
import com.latte.article.request.ArticleRequest;
import com.latte.article.response.ArticleResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {


    @Autowired
    public ArticleMapper mapper;


    /**
     * 아티클 게시글 등록
     *
     * @param request
     */
    public boolean insertArticle(ArticleRequest request) {

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
    public List<ArticleResponse> articleList() {

        return mapper.getArticleList();
    }


    /**
     * 아티클 상세 조회
     * @param articleNo
     * @return
     */
    public ArticleResponse detailArticle(int articleNo) {

        return mapper.detailArticle(articleNo);
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
     * @param viewsNumber
     */
    public void viewCount(int viewsNumber) {

        mapper.viewCount(viewsNumber);
    }






}
