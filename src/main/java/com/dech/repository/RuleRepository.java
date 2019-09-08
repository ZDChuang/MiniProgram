package com.dech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dech.domain.PushRule;

@Repository
public interface RuleRepository extends JpaRepository<PushRule, Object> {

	public PushRule findByOpenid(String openid);

	public List<PushRule> findByStatus(String satus);
}
