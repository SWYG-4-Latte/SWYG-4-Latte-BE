package com.latte.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.member.response.MemberResponse;
import com.latte.menu.response.MenuDetailResponse;
import com.latte.menu.response.MenuSearchRankingResponse;
import com.latte.menu.response.MenuSimpleResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final String rankingKey = "menuSearchRanking";  // 인기 검색어 key
    private final String recentKey = "_RecentMenu";         // 최근 확인한 메뉴 key

    private final RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;  // 인기 검색어
    private HashOperations<String, Object, Object> hashOperations;  // 브랜드명과 메뉴명
    private ListOperations<String, String> recentList;
    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet();
        hashOperations = redisTemplate.opsForHash();
        recentList = redisTemplate.opsForList();
        objectMapper = new ObjectMapper();
    }


    /**
     * 검색어 score 증가
     */
    public void increasePopularSearchWord(String word) {
        zSetOperations.incrementScore(rankingKey, word, 1);
    }


    /**
     * 인기 검색어 조회
     */
    public List<MenuSearchRankingResponse> findPopularSearchWord() {
        List<MenuSearchRankingResponse> rankingResponses = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(rankingKey, 0, 4);

        if (typedTuples.size() == 0) {
            return rankingResponses;
        }

        int rank = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String word = typedTuple.getValue();
            MenuSearchRankingResponse response = new MenuSearchRankingResponse(rank, word);
            rankingResponses.add(response);
            rank++;
        }
        return rankingResponses;
    }
    

    /**
     * 메뉴 상세 정보 저장
     * 최초 상세 조회 시, 모든 사이즈에 대한 상세 정보를 모두 가져와 Redis 에 저장
     * 브랜드명_메뉴명을 key 값으로 사용, Map 은 사이즈명을 key 값으로 사용
     * 저장하면서 요청에 맞는 메뉴 상세 정보 기록 및 반환
     */
    public MenuDetailResponse saveMenuDetails(Long menuNo, String key, List<MenuDetailResponse> menuDetailList) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        MenuDetailResponse detailResponse = null;  // 반환할 정보
        List<String> others = new ArrayList<>();    // 다른 사이즈 저장

        // 다른 사이즈 정보 추출
        for (MenuDetailResponse menuDetailResponse : menuDetailList) {
            others.add(menuDetailResponse.getMenuSize());
        }

        for (MenuDetailResponse menuDetailResponse : menuDetailList) {
            menuDetailResponse.setOtherSizes(others);
            map.put(menuDetailResponse.getMenuSize(), objectMapper.writeValueAsString(menuDetailResponse));
            if (Objects.equals(menuNo, menuDetailResponse.getMenuNo())) {
                detailResponse = menuDetailResponse;
            }
        }
        hashOperations.putAll(key, map);
        return detailResponse;
    }


    /**
     * 메뉴 상세 조회
     * 로그인 한 사용자의 경우, 최근 확인한 음료로 추가
     */
    public MenuDetailResponse findMenuDetails(Long menuNo, String key, String menuSize, MemberResponse member) throws JsonProcessingException {
        Map<Object, Object> entries = hashOperations.entries(key);
        MenuDetailResponse menuDetailResponse = objectMapper.readValue((String) entries.get(menuSize), MenuDetailResponse.class);
        saveRecentMenu(menuNo, menuDetailResponse, member);
        return menuDetailResponse;
    }

    /**
     * 최근 확인한 음료 저장
     * 사이즈별로 담기지 않게 하기 위해 토큰 전달 최초 조회( 메뉴번호의 사이즈명 = 전달 받은 사이즈명 )일 때만 최근 확인한 음료로 저장
     */
    public void saveRecentMenu(Long menuNo, MenuDetailResponse menuDetailResponse, MemberResponse member) throws JsonProcessingException {
        if (member != null && Objects.equals(menuNo, menuDetailResponse.getMenuNo())) {
            log.info("##################### 로그인 사용자 최근 확인 음료 저장 #####################");
            MenuSimpleResponse menuSimpleResponse = MenuSimpleResponse.convertDetailToSimple(menuDetailResponse);
            recentList.leftPush(member.getMbrId() + recentKey, objectMapper.writeValueAsString(menuSimpleResponse));
        }
    }


    /**
     * 최근 확인한 음료 조회
     */
    public List<MenuSimpleResponse> findRecentMenu(MemberResponse member) throws JsonProcessingException {
        List<MenuSimpleResponse> menuSimpleResponses = new ArrayList<>();

        if (member != null) {
            List<String> range = recentList.range(member.getMbrId() + recentKey, 0, 3);
            if (range != null) {
                for (String str : range) {
                    menuSimpleResponses.add(objectMapper.readValue(str, MenuSimpleResponse.class));
                }
            }
        }

        return menuSimpleResponses;
    }
}
