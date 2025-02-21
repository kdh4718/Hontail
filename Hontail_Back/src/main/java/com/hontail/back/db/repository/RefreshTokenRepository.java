//package com.hontail.back.db.repository;
//
//import com.hontail.back.db.entity.RefreshToken;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.repository.query.Param;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.Optional;
//
//@Repository
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//
//    // 사용자 ID로 토큰 삭제
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM RefreshToken r WHERE r.userId = :userId")
//    void deleteByUserId(@Param("userId") int userId);
//}

package com.hontail.back.db.repository;

import com.hontail.back.db.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Integer userId);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Integer userId);
}