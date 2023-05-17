package com.artfolio.artfolio.app.service;

import com.artfolio.artfolio.app.domain.Member;
import com.artfolio.artfolio.app.repository.MemberRepository;
import com.artfolio.artfolio.dto.MemberInfo;
import com.artfolio.artfolio.global.exception.DuplicateIdException;
import com.artfolio.artfolio.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    public Long saveMember(MemberInfo memberInfo) {
        String username = memberInfo.getUsername();
        String password = memberInfo.getPassword();

        if (memberRepository.existsByUsername((username)))
            throw new DuplicateIdException(username);

        Member member = Member.builder()
                .username(username)
                .password(password)
                .email(memberInfo.getEmail())
                .like(null)
                .content(memberInfo.getContent())
                // 이미지 업로드 로직 필요
                //.profilePhoto()
                .active(true)
                .build();

        memberRepository.save(member);
        return member.getId();
    }

    public Member getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // member가 active 상태인지 확인
        if (!member.isActive())
            throw new MemberNotFoundException(memberId);

        return member;
    }

    public Member getMemberByName(String memberName) {
        Member member = memberRepository.findByUsername(memberName)
                .orElseThrow(() -> new MemberNotFoundException(memberName));

        // member가 active 상태인지 확인
        if (!member.isActive())
            throw new MemberNotFoundException(memberName);

        return member;
    }

    public Long deactivateMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.isActive()) {
            member.setActive(false);
            memberRepository.save(member);
            return 1L;
        }

        return 0L;
    }
}
