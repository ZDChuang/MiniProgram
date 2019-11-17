package com.dech.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(indexes = { @Index(columnList = "openId") })
@EntityListeners(AuditingEntityListener.class)
public class PushInfo {

	@Id
	@GeneratedValue
	private Integer id;

	private String openId;
	private String formId;

	// push time
	private Date pushTime;

	// E: expire, A: active, S: success, F: fail
	private String status;

	// 推送返回的信息
	private String message;

	@CreatedDate
	private Date createTime;

	@LastModifiedDate
	private Date updateTime;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
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

	public Date getPushTime() {
		return pushTime;
	}

	public void setPushTime(Date pushTime) {
		this.pushTime = pushTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
