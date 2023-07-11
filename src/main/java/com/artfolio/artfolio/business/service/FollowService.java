package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.user.entity.Follow;
import com.artfolio.artfolio.user.dto.FollowDto;
import com.artfolio.artfolio.business.repository.FollowRepository;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public Long toggleFollow(FollowDto.FollowUserReq req) {
        Long fromUserId = req.getFromUserId();
        Long toUserId = req.getToUserId();

        if (fromUserId.equals(toUserId)) {
            return 0L;
        }

        // fromUser follow toUser
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException(fromUserId));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException(toUserId));

        Optional<Follow> followOp = followRepository.findFollow(fromUser, toUser);

        if (followOp.isPresent()) {
            Follow follow = followOp.get();
            followRepository.delete(follow);
            fromUser.deleteFollowing(follow);
            toUser.deleteFollower(follow);
        } else {
            Follow follow = new Follow(fromUser, toUser);
            followRepository.save(follow);
            fromUser.addFollowing(follow);
            toUser.addFollower(follow);
        }

        return 1L;
    }

    @Transactional(readOnly = true)
    public FollowDto.getFollowInfoRes getFollowInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Follow> followers = user.getFollowers();
        List<Follow> followings = user.getFollowings();

        return FollowDto.getFollowInfoRes.of(userId, followers, followings);
    }

    @Transactional(readOnly = true)
    public boolean checkFollow(FollowDto.FollowUserReq req) {
        Long fromUserId = req.getFromUserId();
        Long toUserId = req.getToUserId();

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException(fromUserId));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException(toUserId));

        return followRepository.findFollow(fromUser, toUser).isPresent();
    }
}
