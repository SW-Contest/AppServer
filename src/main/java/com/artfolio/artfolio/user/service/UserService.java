package com.artfolio.artfolio.user.service;

import com.artfolio.artfolio.user.dto.Role;
import com.artfolio.artfolio.user.dto.SocialType;
import com.artfolio.artfolio.user.dto.UserSignUpDto;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일");
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임");
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .profilePhoto(userSignUpDto.getProfilePhoto())
                .role(Role.USER)
                .socialType(SocialType.NAVER)
                .socialId(userSignUpDto.getSocialId())
                .content(userSignUpDto.getContent())
                .build();

        user.passwordEncode(passwordEncoder);
        return userRepository.save(user).getId();
    }
}
