package com.artfolio.artfolio.app.dto;

import com.artfolio.artfolio.app.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberInfo {
    private Long memberId;
    private String name;
    private String email;
    private String profilePhotoPath;
    private Long like;

    public static MemberInfo of(Member member) {
        return new MemberInfo(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getProfilePhoto(),
                member.getLike()
        );
    }
}

