package com.example.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.jobhunter.entity.RefreshToken;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndDeviceKey(String refreshToken, String deviceKey);
    Optional<RefreshToken> findByUserEmailAndDeviceKey(String email, String deviceKey);
    Optional<RefreshToken> findByUserIdAndDeviceKey(long id, String deviceKey);
}

