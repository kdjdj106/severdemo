package com.example.severdemo.repository;

import com.example.severdemo.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    @Query("select m from Member m left join fetch m.memberImage where m.userId = :userId")
    Optional<Member> findByUserIdEagerLoadImage(String userId);
    Optional<Member> findByEmail(String email);
    boolean existsByUserId(String userId);
}
