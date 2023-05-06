package com.artfolio.artfolio.repository;

import com.artfolio.artfolio.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
