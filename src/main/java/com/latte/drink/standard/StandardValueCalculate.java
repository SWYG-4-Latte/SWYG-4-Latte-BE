package com.latte.drink.standard;

import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Component
public class StandardValueCalculate {
    public StandardValue getMemberStandardValue(MemberResponse member) {

        if (member.getGender() == null || !StringUtils.hasText(member.getSymptom())) {
            throw new NotEnoughInfoException("부가정보를 입력하지 않은 사용자입니다");
        }

        log.info("member = {}", member.getMbrId());

        String type = "";
        int age = 0;
        int minLimit = 0;
        int maxLimit = 0;
        // 나이 기준 카페인 섭취량 설정
        if(member.getAge() != null) {
            age = Integer.parseInt(member.getAge());
            if(age >= 20 && age <= 39) {
                minLimit = 400;
                maxLimit = 400;
            } else if (age >= 40 && age <= 59) {
                minLimit = 300;
                maxLimit = 400;
            } else {
                minLimit = 200;
                maxLimit = 300;
            }
        }


        /**
         * 몸무게 기준 카페인 섭취량 설정
         */

        int weight = 0;

        if(member.getWeight() != null) {
            weight = Integer.parseInt(member.getWeight());
            minLimit = Math.min(weight * 3, minLimit);
            maxLimit = Math.max(weight * 6, maxLimit);
        }

        /**
         * 성별에 따른 카페인 섭취량
         */

        if(member.getGender().equals(Gender.F) && member.isPregnancy()) {
            int pregMonth = member.getPregMonth();
            if (0 <= pregMonth && pregMonth <= 3) {
                minLimit = 0; // 임신 초기: 권장량 0mg
                maxLimit = 0;
            } else if (4 <= pregMonth && pregMonth <= 7) {
                minLimit = Math.min(minLimit, 51); // 임신 중기
                maxLimit = Math.max(maxLimit, 200);
            } else if (8 <= pregMonth && pregMonth <= 10) {
                minLimit = Math.min(minLimit, 51); // 임신 후기
                maxLimit = Math.max(maxLimit, 200);
            }
        }

        /**
         * 알레르기 여부 저장 방식에 따라 달라질 수 있음
         */

        Map<String, Double> sensitivityFactor = new HashMap<>();
        sensitivityFactor.put("심장이 빨리 뛰어요", 0.5);
        sensitivityFactor.put("속이 메스꺼워요", 0.6);
        sensitivityFactor.put("예민해져요", 0.7);
        sensitivityFactor.put("잠이 안와요", 0.8);
        sensitivityFactor.put("별다른 증상이 없어요", 1.0);
        String[] symtoms = member.getSymptom().split(", ");

        List<Double> symtomMinList = new ArrayList<>();
        List<Double> symtomMaxList = new ArrayList<>();
        for(String i : symtoms) {
            double allergyRate = sensitivityFactor.getOrDefault(i, 1.0);
            symtomMinList.add(allergyRate * minLimit);
            symtomMaxList.add(allergyRate * maxLimit);
        }

        double allerMin = Collections.min(symtomMinList);
        double allerMax = Collections.max(symtomMaxList);


        //double allergyRate = sensitivityFactor.getOrDefault(member.getSymptom(), 1.0);

        StandardValue standardValue = new StandardValue();



        standardValue.setMinNormal(allerMin);
        standardValue.setMaxNormal(allerMax);

        //return 1;




        //log.info("type = {}", type);

        return standardValue;
    }
}
