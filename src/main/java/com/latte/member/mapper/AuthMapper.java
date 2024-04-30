package com.latte.member.mapper;

import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthMapper {



    void updateMember(MemberRequest request);

    //public MemberResponse getMemberInfo(String Id);

    MemberResponse findBySeq(String seq);

    List<MemberResponse> getMemberList();

    void insertMember(MemberRequest request);

    int countByLoginId(String id);

    boolean deleteMember(String id);

    MemberResponse findById(String id);
}
