package com.dech.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "openid", "type" }))
@EntityListeners(AuditingEntityListener.class)
public class PushRule {
	@Id
	@GeneratedValue
	private Integer id;

	private String openid;

	// 推送类型 如喝水、读书、待办事项等
	private String type;
	
	// 简述
	private String info;
	
	// 推送周期 每周、每天
	private String period;
	
	// 推送周期为每周，如周一、周四
	private String periodweek;
	
	// 固定时间
	private String fixtime;
	
	// 间隔一定时间
	private int hours;

	// 周末是否推送
	private boolean pushweek;

	// 推送开始时间
	private Date begin;

	// 推送结束时间
	private Date end;
	
	// 推送协议状态
	private String status;

	@CreatedDate
	private Date createTime;

	@LastModifiedDate
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getPeriodweek() {
		return periodweek;
	}

	public void setPeriodweek(String periodweek) {
		this.periodweek = periodweek;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public boolean isPushweek() {
		return pushweek;
	}

	public void setPushweek(boolean pushweek) {
		this.pushweek = pushweek;
	}

	public String getFixtime() {
		return fixtime;
	}

	public void setFixtime(String fixtime) {
		this.fixtime = fixtime;
	}

}