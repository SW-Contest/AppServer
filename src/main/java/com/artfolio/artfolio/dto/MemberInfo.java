package com.artfolio.artfolio.dto;

import com.artfolio.artfolio.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberInfo {
    @Id @JsonProperty("memberId")
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    @JsonProperty("likeAuctions")
    private Set<Long> likeAuctions = new HashSet<>();              // 좋아요 누른 경매 불러오기
    private String profilePhoto;
    private String content;
    private boolean anonymized;
}
