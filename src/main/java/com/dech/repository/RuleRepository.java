package com.dech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.PushRule;

@Repository
public interface RuleRepository extends JpaRepository<PushRule, Object> {

	public List<PushRule> findByOpenid(String openid);

	@Query(value = "select * from push_rule where status=?1 order by openid", nativeQuery = true)
	public List<PushRule> findByStatus(String satus);
	
	@Query(value = "select * from push_rule where openid=?1 and type=?2", nativeQuery = true)
	public PushRule findRecord(String openid, String type);
}
