package com.latte.drink.standard;

import com.latte.drink.exception.NotEnoughInfoException;
import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class StandardValueCalculate {
    public StandardValue getMemberStandardValue(MemberResponse member) {

        if (member.getGender() == null || !StringUtils.hasText(member.getAllergy())) {
            throw new NotEnoughInfoException("부가정보를 입력하지 않은 사용자입니다");
        }

        log.info("member = {}", member.getMbrId());

        String type = "";

        /**
         * 알레르기 여부 저장 방식에 따라 달라질 수 있음
         */
        if(member.getGender().equals(Gender.M)) {
            // 알레르기 여부
            if ("없어요".equals(member.getAllergy())) {
                type = "MN";
            } else {
                type = "MY";
            }
        } else {
            if (!member.isPregnancy()) {
                if ("없어요".equals(member.getAllergy())) {
                    type = "FNN";
                } else {
                    type = "FNY";
                }
            } else {
                int pregMonth = member.getPregMonth();
                if (pregMonth <= 3) {
                    type = "FYE";
                } else if (pregMonth <= 7) {
                    type = "FYM";
                } else {
                    type = "FYL";
                }
            }
        }

        log.info("type = {}", type);

        return StandardValue.createStandardValue(type);
    }
}
