package com.hontail.back.db.repository;

import com.hontail.back.db.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    // 사용자 ID로 토큰 찾기
    Optional<AccessToken> findByUserId(int userId);

    // 토큰으로 찾기
    Optional<AccessToken> findByToken(String token);

    // 사용자 ID로 토큰 삭제 (명시적인 삭제 메서드)
    @Modifying
    @Transactional
    @Query("DELETE FROM AccessToken a WHERE a.userId = :userId")
    void deleteAllByUserId(@Param("userId") int userId);

    // 기존 메서드 유지
    void deleteByUserId(int userId);
}