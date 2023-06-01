package com.example.severdemo.repository;

import com.example.severdemo.domain.token.RefreshToken;
import com.example.severdemo.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByMember(Member member);
}
