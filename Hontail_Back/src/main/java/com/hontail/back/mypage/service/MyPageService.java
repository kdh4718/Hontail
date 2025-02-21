package com.hontail.back.mypage.service;

import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.LikeRepository;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.mypage.dto.UpdateNicknameRequest;
import com.hontail.back.mypage.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hontail.back.global.exception.ErrorCode;
import com.hontail.back.global.exception.CustomException;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private static final Logger log = LoggerFactory.getLogger(MyPageService.class);
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public UserResponse getCurrentUser(String email) { //이메일을 기반으로 DB에서 사용자 정보를 조회.
        log.debug("현재 사용자 정보 조회 - Email: {}", email);

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> {
                    log.error("사용자 정보 조회 실패 - Email: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });

        log.info("사용자 정보 조회 성공 - Email: {}", email);
        return UserResponse.fromEntity(user);
    }

    // 닉네임을 기준으로 사용자를 찾으려고 시도 >> 닉네임(이름) 중복되는 경우가 있어서 500에러 발생 > 이메일 기준으로 사용자 검색으로 변경

    @Transactional
    public UserResponse updateNickname(String email, UpdateNicknameRequest request) { // 이메일을 기준으로 사용자를 찾고 닉네임을 변경.
        log.debug("닉네임 변경 처리 시작 - Email: {}, New Nickname: {}", email, request.getNickname());

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> {
                    log.error("닉네임 변경 실패 - 사용자 찾을 수 없음: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });

        user.setUserNickname(request.getNickname());
        User updatedUser = userRepository.save(user);

        log.info("닉네임 변경 성공 - Email: {}, New Nickname: {}", email, updatedUser.getUserNickname());
        return UserResponse.fromEntity(updatedUser);
    }

    // userId로 사용자 정보 조회하는 메서드(이메일로 조회하는 것만 있어서 추가함..)
    public UserResponse getCurrentUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.fromEntity(user);
    }

    public Long getUserLikeCount(Integer userId) {
        return likeRepository.countByUserId(userId);
    }

}