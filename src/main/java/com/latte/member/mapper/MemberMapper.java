package com.latte.member.mapper;

import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {


    
    public MemberResponse findById(String Id);

   /* public List<MemberResponse> getMemberList();

    int countByLoginId(String id);*/

}
