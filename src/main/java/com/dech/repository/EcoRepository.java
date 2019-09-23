package com.dech.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.Economy;

@Repository
public interface EcoRepository extends JpaRepository<Economy, Object> {
	public List<Economy> findByOpenid(String openid);

	// pessimistic lock
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select e from Economy e where e.openid=?1 and e.date=?2")
	public Economy findRecords(String openid, int date);

	@Query(value = "select * from Economy where openid=?1 and date like ?2% order by date desc limit 0,1", nativeQuery = true)
	public Economy findMonthRecords(String openid, int date);
	
	@Query(value = "select date from Economy where openid=?1 and date < ?2 order by date desc limit 0,1", nativeQuery = true)
	public Integer findRecentRecords(String openid, int date);
}
