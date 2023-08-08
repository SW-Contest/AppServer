package com.artfolio.artfolio.user.entity;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.UserArtPiece;
import com.artfolio.artfolio.business.domain.UserAuction;
import com.artfolio.artfolio.user.dto.Role;
import com.artfolio.artfolio.user.dto.SocialType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profilePhoto;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)  // 로그인한 소셜 타입의 식별자 값
    private String socialId;

    @Column(length = 1500, nullable = false)
    private String content;

    private String refreshToken;

    @OneToMany(mappedBy = "artist")
    private final List<ArtPiece> artPieces = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private final List<UserAuction> userAuctions = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser")
    private final List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "toUser")
    private final List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private final List<UserArtPiece> userArtPieces = new ArrayList<>();

    @Builder
    public User(String email, String nickname, String profilePhoto, Role role, SocialType socialType, String socialId, String content, String refreshToken) {
        this.email = email;
        this.nickname = nickname;
        this.profilePhoto = profilePhoto;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.content = content;
        this.refreshToken = refreshToken;
    }

    /* 연관관계 편의 메서드 (유저 - 예술품) */
    public void addArtPiece(ArtPiece artPiece) {
        this.artPieces.add(artPiece);
        artPiece.setArtist(this);
    }

    public void addFollower(Follow follow) {
        this.followers.add(follow);
        follow.setToUser(this);
    }

    public void addFollowing(Follow follow) {
        this.followings.add(follow);
        follow.setFromUser(this);
    }

    public void deleteFollower(Follow follow) {
        if (this.followers.contains(follow)) {
            followers.remove(follow);
        }
    }

    public void deleteFollowing(Follow follow) {
        if (this.followings.contains(follow)) {
            followings.remove(follow);
        }
    }

    /* 유저 권한 설정 메서드 */
    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void authorizeArtist() {
        this.role = Role.ARTIST;
    }

    /* refresh-token 업데이트 메서드 */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}