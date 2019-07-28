package com.dech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dech.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Object>{
	public Post findById(Integer id);
	public List<Post> findByCategory(String category);
	public Post findByUrl(String url);
	
	@Query(value="select * from post limit 10",nativeQuery=true)
	public List<Post> findFirst10Record();
}
