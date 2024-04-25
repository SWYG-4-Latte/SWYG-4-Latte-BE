package com.latte.member.mapper;

import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthMapper {



    int updateMember(MemberRequest request);

    //public MemberResponse getMemberInfo(String Id);

    MemberResponse findById(String Id);

    List<MemberResponse> getMemberList();

    void insertMember(MemberRequest request);

    int countByLoginId(String id);

    void deleteMember(String id);
}
