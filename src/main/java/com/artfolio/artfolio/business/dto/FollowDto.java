package com.artfolio.artfolio.business.dto;

import com.artfolio.artfolio.business.domain.Follow;
import com.artfolio.artfolio.user.entity.User;
import lombok.*;

import java.util.List;

public class FollowDto {
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FollowUserReq {
        private Long fromUserId;
        private Long toUserId;
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class getFollowInfoRes {
        private Long userId;
        private List<FollowInfo> followerInfos;
        private List<FollowInfo> followingInfos;

        public static getFollowInfoRes of(Long userId, List<Follow> followers, List<Follow> followings) {
            List<FollowInfo> followerUsers = followers.stream()
                    .map(Follow::getToUser)
                    .map(FollowInfo::of)
                    .toList();

            List<FollowInfo> followingUsers = followings.stream()
                    .map(Follow::getFromUser)
                    .map(FollowInfo::of)
                    .toList();

            return new getFollowInfoRes(userId, followerUsers, followingUsers);
        }
    }

    @Getter @Setter @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class FollowInfo {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String photoPath;

        public static FollowInfo of(User user) {
            return FollowInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .photoPath(user.getProfilePhoto())
                    .build();
        }
    }
}
