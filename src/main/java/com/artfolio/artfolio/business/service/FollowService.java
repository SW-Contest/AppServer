package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.business.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;

    public void addFollower() {

    }

    public void addFollowing() {

    }

    @Transactional(readOnly = true)
    public void getFollower() {

    }

    @Transactional(readOnly = true)
    public void getFollowing() {

    }
}
