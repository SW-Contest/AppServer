package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.dto.MemberInfo;
import com.artfolio.artfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    public Long saveMemberInfo(MemberInfo memberInfo){

        // 예외처리 추가..

        Member member = Member.builder()
                .name(memberInfo.getName())
                .email(memberInfo.getEmail())
                .like(0L)
                .content(memberInfo.getContent())
                .profilePhoto(memberInfo.getProfilePhoto())
                .build();

        memberRepository.save(member);
        return 1L;
    }
}
