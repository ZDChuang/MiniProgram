package com.dech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Object> {
	@Query(value = "select * from note where openid=?1 and status = 'A' order by update_time desc", nativeQuery = true)
	public List<Note> findByOpenid(String openid);
	
	@Query(value = "select * from note where openid=?1 and status = 'A' order by update_time desc limit 5", nativeQuery = true)
	public List<Note> findFirst5(String openid);
}
