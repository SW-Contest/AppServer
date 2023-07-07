package com.artfolio.artfolio.user.entity;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.Follow;
import com.artfolio.artfolio.business.domain.UserAuction;
import com.artfolio.artfolio.user.dto.Role;
import com.artfolio.artfolio.user.dto.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Table(name = "users")
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

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

    @OneToMany(mappedBy = "follower")
    private final List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private final List<Follow> followings = new ArrayList<>();

    @Builder
    public User(String email, String password, String nickname, String profilePhoto, Role role, SocialType socialType, String socialId, String content, String refreshToken) {
        this.email = email;
        this.password = password;
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
        follow.setFollower(this);
    }

    public void addFollowing(Follow follow) {
        this.followings.add(follow);
        follow.setFollowing(this);
    }

    /* 유저 권한 설정 메서드 */
    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void authorizeArtist() {
        this.role = Role.ARTIST;
    }

    /* 비밀번호 암호화 메서드 */
    public void passwordEncode(PasswordEncoder encoder) {
        this.password = encoder.encode(this.password);
    }

    /* refresh-token 업데이트 메서드 */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User o1)) return false;
        return Objects.equals(this.email, o1.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}