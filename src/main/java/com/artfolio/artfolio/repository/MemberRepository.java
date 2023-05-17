package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.Member;
import com.artfolio.artfolio.dto.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username);
    Optional<Member> findByName(String name);
}
