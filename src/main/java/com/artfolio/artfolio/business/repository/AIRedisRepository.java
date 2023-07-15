package com.artfolio.artfolio.business.repository;

import com.artfolio.artfolio.business.domain.AIInfo;
import org.springframework.data.repository.CrudRepository;

public interface AIRedisRepository extends CrudRepository<AIInfo, Long> {
}
