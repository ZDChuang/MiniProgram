package com.dech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dech.domain.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Object>{
	public Users findById(Integer id);
	public Users findByUnionId(String unionId);
	public List<Users> findByOpenId(String openId);
}
