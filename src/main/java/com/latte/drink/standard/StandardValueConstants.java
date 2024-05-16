package com.latte.drink.standard;

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

    int MN_MIN_NORMAL = 134;
    int MN_MAX_NORMAL = 267;
    int MN_MAX_CAFFEINE = 400;


    int MY_MIN_NORMAL = 74;
    int MY_MAX_NORMAL = 133;
    int MY_MAX_CAFFEINE = 268;

    int FNN_MIN_NORMAL = 134;
    int FNN_MAX_NORMAL = 267;
    int FNN_MAX_CAFFEINE = 400;

    int FNY_MIN_NORMAL = 60;
    int FNY_MAX_NORMAL = 133;
    int FNY_MAX_CAFFEINE = 268;

    int FYE_MIN_NORMAL = 0;
    int FYE_MAX_NORMAL = 0;
    int FYE_MAX_CAFFEINE = 0;

    int FYM_MIN_NORMAL = 51;
    int FYM_MAX_NORMAL = 200;
    int FYM_MAX_CAFFEINE = 200;

    int FYL_MIN_NORMAL = 51;
    int FYL_MAX_NORMAL = 200;
    int FYL_MAX_CAFFEINE = 200;
}
