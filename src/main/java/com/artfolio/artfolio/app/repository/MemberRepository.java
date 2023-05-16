package com.artfolio.artfolio.app.repository;

import com.artfolio.artfolio.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
