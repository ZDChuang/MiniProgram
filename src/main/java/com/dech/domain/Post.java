package com.dech.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames = { "url" }),
indexes=@Index( columnList = "lights, replies"))
public class Post {
	@Id
	@GeneratedValue
	private Integer id;
	
	private String url;
	
	private String post_name;
	
	private String category;
	
	private Integer replies;
	
	private Integer lights;
	private Integer clicks;

	private String author;
	private String authorUrl;
	private Date create_time;
	
	//the last reply link and time
	private String lastUrl;
	private Date lastReply;
	
	//the time and count of this record updates
	private Date update_time;
	private Integer update_count;
	
	private String isBrowsed;
	
	//the love degree of this post
	private Integer degree;
	
	public Post() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getPost_name() {
		return post_name;
	}

	public void setPost_name(String post_name) {
		this.post_name = post_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getReplies() {
		return replies;
	}

	public void setReplies(Integer replies) {
		this.replies = replies;
	}

	public Integer getLights() {
		return lights;
	}

	public void setLights(Integer lights) {
		this.lights = lights;
	}
	
	@Override
	public String toString() {
		return "Post{id=" + id + "post_name=" + post_name + "}";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getLastReply() {
		return lastReply;
	}

	public void setLastReply(Date lastReply) {
		this.lastReply = lastReply;
	}

	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	public String getIsBrowsed() {
		return isBrowsed;
	}

	public void setIsBrowsed(String isBrowsed) {
		this.isBrowsed = isBrowsed;
	}

	public Integer getDegree() {
		return degree;
	}

	public void setDegree(Integer degree) {
		this.degree = degree;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public String getLastUrl() {
		return lastUrl;
	}

	public void setLastUrl(String lastUrl) {
		this.lastUrl = lastUrl;
	}

	public Integer getUpdate_count() {
		return update_count;
	}

	public void setUpdate_count(Integer update_count) {
		this.update_count = update_count;
	}

	public String getAuthorUrl() {
		return authorUrl;
	}

	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	
}
