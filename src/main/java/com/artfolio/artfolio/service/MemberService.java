package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.dto.MemberInfo;
import com.artfolio.artfolio.exception.MemberNotFoundException;
import com.artfolio.artfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    public Long saveMemberInfo(MemberInfo memberInfo) {

        // 예외처리 추가..

        Member member = Member.builder()
                .name(memberInfo.getName())
                .email(memberInfo.getEmail())
                .like(0L)
                .content(memberInfo.getContent())
                // 이미지 업로드 로직 필요
                //.profilePhoto()
                .build();

        memberRepository.save(member);
        return 1L;
    }

    public Object getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException(memberId));
    }

//    // name으로 찾는거 추가하고 싶었는데 exception 수정해야 할듯..
//    public Object getMemberByName(String name) {
//        return memberRepository.findByName(name)
//                .orElseThrow(() -> new MemberNotFoundException(name));
//    }

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // MemberNotFound 말고 처리해야 할 예외가 더 있을까?
        // 근데 Member 지우면 DB에 쌓인 정보들은 어떻게 처리됨?
        // delete말고 비활성화(deactive)로 해야하나?
        memberRepository.delete(member);
    }
}
