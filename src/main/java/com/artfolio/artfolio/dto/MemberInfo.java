package com.artfolio.artfolio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MemberInfo {
    @Id @JsonProperty("memberId")
    private String id;
    private String name;
    private String email;
    @JsonProperty("memberLike")
    private Long like;
    private String profilePhoto;
    private String content;
}
