package com.dech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dech.domain.PushInfo;

@Repository
public interface PushRepository extends JpaRepository<PushInfo, Object>{
	public List<PushInfo> findByOpenId(String openId);
}
