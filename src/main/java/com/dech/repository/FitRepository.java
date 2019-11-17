package com.dech.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.Fit;

@Repository
public interface FitRepository extends JpaRepository<Fit, Object> {
	public Fit findById(Integer id);

	public List<Fit> findByOpenid(String openid);

	// pessimistic lock
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select f from Fit f where f.openid=?1 and f.date=?2")
	public Fit findRecords(String openid, int date);
	
	@Query(value = "select * from fit where openid=?1 order by date asc", nativeQuery = true)
	public List<Fit> findAllData(String openid);
}
