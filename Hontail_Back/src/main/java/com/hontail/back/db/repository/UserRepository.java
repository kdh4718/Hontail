package com.hontail.back.db.repository;

import com.hontail.back.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 이메일로 사용자 찾기
    Optional<User> findByUserEmail(String email);
}