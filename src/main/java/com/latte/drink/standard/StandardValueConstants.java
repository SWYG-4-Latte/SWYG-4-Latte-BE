package com.latte.drink.standard;

import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;

import java.util.HashMap;
import java.util.Map;

public interface StandardValueConstants {
    /**
     * MN = 남성 + 카페인 부작용 X
     * MY = 남성 + 카페인 부작용 O
     * FNN = 여성 + 임신 X + 카페인 부작용 X
     * FNY = 여성 + 임신 X + 카페인 부작용 O
     * FYE = 여성 + 임신 O + 임신 초기 ( ~ 3개월 ) + 카페인 부작용 X, O
     * FYM = 여성 + 임신 O + 임신 중기 ( ~ 7개월 ) + 카페인 부작용 X, O
     * FYL = 여성 + 임신 O + 임신 말기 ( ~ 10개월 ) + 카페인 부작용 X, O
     * 
     * MIN_NORMAL : 카페인 섭취량 보통의 최솟값
     * MAX_NORMAL : 카페인 섭취량 보통의 최댓값
     * MAX_CAFFEINE : 카페인 섭취 최대량
     */

/*    int MN_MIN_NORMAL = 134;

    int MN_MAX_NORMAL = 267;

    int MN_MAX_CAFFEINE = 400;

    int MY_MIN_NORMAL = 74;

    int MY_MAX_NORMAL = 133;

    int MY_MAX_CAFFEINE = 268;
    int FNN_MIN_NORMAL = 134;
    int FNN_MAX_NORMAL = 267;
    int FNN_MAX_CAFFEINE = 40;
    int FNY_MIN_NORMAL = 60;

    int FNY_MAX_NORMAL = 133;
    int FNY_MAX_CAFFEINE = 26;
    int FYE_MIN_NORMAL = 0;

    int FYE_MAX_NORMAL = 0;

    int FYE_MAX_CAFFEINE = 0;
    int FYM_MIN_NORMAL = 51;

    int FYM_MAX_NORMAL = 200;
    int FYM_MAX_CAFFEINE = 20;
    int FYL_MIN_NORMAL = 51;

    int FYL_MAX_NORMAL = 200;
    int FYL_MAX_CAFFEINE = 20;

    public static int StandardValueCal(int type, MemberResponse member) {





        // 나이 기준 카페인 섭취량 설정
        int age = Integer.parseInt(member.getAge());
        int ageLimit = 0;
        if(age >= 20 && age <= 39) {
            ageLimit = 400;
        } else if (age >= 40 && age <= 59) {
            ageLimit = 300;
        } else {
            ageLimit = 200;
        }

        *//**
         * 몸무게 기준 카페인 섭취량 설정
         *//*

        int weight = Integer.parseInt(member.getWeight());
        int weightLimitMin = weight * 3;
        int weightLimitMax = Math.min(weight * 6, ageLimit);


        *//**
         * 성별에 따른 카페인 섭취량
         *//*

        if(member.getGender().equals(Gender.F) && member.isPregnancy()) {
            int pregMonth = member.getPregMonth();
            if (0 <= pregMonth && pregMonth <= 3) {
                weightLimitMax = 0; // 임신 초기: 권장량 0mg
            } else if (4 <= pregMonth && pregMonth <= 7) {
                weightLimitMax = Math.min(weightLimitMax, 200); // 임신 중기
            } else if (8 <= pregMonth && pregMonth <= 10) {
                weightLimitMax = Math.min(weightLimitMax, 200); // 임신 후기
            }
        }

        *//**
         * 알레르기 여부 저장 방식에 따라 달라질 수 있음
         *//*

        Map<String, Double> sensitivityFactor = new HashMap<>();
        sensitivityFactor.put("심장이 빨리 뛰어요", 0.5);
        sensitivityFactor.put("속이 메스꺼워요", 0.6);
        sensitivityFactor.put("예민해요", 0.7);
        sensitivityFactor.put("잠을 못 자요", 0.8);
        sensitivityFactor.put("none", 1.0);

        double allergyRate = sensitivityFactor.getOrDefault(member.getAllergy(), 1.0);


        return 1;

    }*/
}
