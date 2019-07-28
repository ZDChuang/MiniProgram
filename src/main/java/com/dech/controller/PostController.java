package com.dech.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dech.domain.Post;
import com.dech.repository.PostRepository;
import com.dech.util.HupuConfigure;
import com.dech.util.HupuCrawler;

@RestController
public class PostController {
	private static final Logger logger = LoggerFactory.getLogger(PostController.class);

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private HupuConfigure config;

	@GetMapping(value = "/first")
	public List<Post> first10() {
		logger.info("/first: " + new Date());
		return postRepository.findFirst10Record();
	}

	@GetMapping(value = "/posts")
	public int posts() {
		List<Post> list = null;
		try {
			list = postRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.size();
	}

	@GetMapping(value = "/posts/update")
	public String update() {
		List<Map<String, String>> list = null;
		try {
			list = HupuCrawler.retrieve(config.pages);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Post post = null;
		int count = 0;
		for (Map<String, String> m : list) {

			post = postRepository.findByUrl(m.get("url").trim());
			// check this post should update or insert
			if (post != null) {
				count++;
				HupuCrawler.updatePost(post, m);
				postRepository.save(post);

			} else {
				post = new Post();
				HupuCrawler.insertPost(post, m);
				postRepository.save(post);
			}
		}

		logger.info("update: " + count);
		logger.info("insert: " + (list.size() - count));
		return "update: " + count + "\ninsert: " + (list.size() - count);
	}

	@PostMapping(value = "/posts/insert")
	public void insert(List<Post> posts) {
		postRepository.saveAll(posts);
	}
}
