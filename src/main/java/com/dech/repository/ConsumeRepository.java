package com.dech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.Consume;

@Repository
public interface ConsumeRepository extends JpaRepository<Consume, Object> {

	@Query(value = "select * from consume where openid = ?1 and date = ?2", nativeQuery = true)
	public Consume findConsume(String openid, int date);
	

	@Query(value = "select * from consume where openid = ?1 and date >= ?2 and date <= ?3 order by date desc limit 0, 1", nativeQuery = true)
	public Consume findRecentConsume(String openid, int date, int date2);
	
	@Query(value = "select * from consume where openid = ?1 and date <= ?2 order by date desc limit 0, 1", nativeQuery = true)
	public Consume findRecentConsume2(String openid, int date);
}
