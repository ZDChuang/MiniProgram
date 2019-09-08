package com.dech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.PushInfo;

@Repository
public interface PushRepository extends JpaRepository<PushInfo, Object> {

	@Query(value = "select * from push_info where open_id=?1 and status=?2 order by create_time asc limit 1", nativeQuery = true)
	public PushInfo findPushInfo(String openid, String satus);
}
