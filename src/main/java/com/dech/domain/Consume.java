package com.dech.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "openid", "date" }))
@EntityListeners(AuditingEntityListener.class)
public class Consume {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private String openid;

	// yyyyMMdd
	private int date;

	private BigDecimal consume;
	
	// 第一项消费及备注
	private BigDecimal consume1;
	private String note1;

	private BigDecimal consume2;
	private String note2;
	
	private BigDecimal consume3;
	private String note3;
	
	private BigDecimal consume4;
	private String note4;
	
	private BigDecimal consume5;
	private String note5;
	
	private BigDecimal consume6;
	private String note6;
	
	private BigDecimal consume7;
	private String note7;
	
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

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public BigDecimal getConsume() {
		return consume;
	}

	public void setConsume(BigDecimal consume) {
		this.consume = consume;
	}

	public BigDecimal getConsume1() {
		return consume1;
	}

	public void setConsume1(BigDecimal consume1) {
		this.consume1 = consume1;
	}

	public String getNote1() {
		return note1;
	}

	public void setNote1(String note1) {
		this.note1 = note1;
	}

	public BigDecimal getConsume2() {
		return consume2;
	}

	public void setConsume2(BigDecimal consume2) {
		this.consume2 = consume2;
	}

	public String getNote2() {
		return note2;
	}

	public void setNote2(String note2) {
		this.note2 = note2;
	}

	public BigDecimal getConsume3() {
		return consume3;
	}

	public void setConsume3(BigDecimal consume3) {
		this.consume3 = consume3;
	}

	public String getNote3() {
		return note3;
	}

	public void setNote3(String note3) {
		this.note3 = note3;
	}

	public BigDecimal getConsume4() {
		return consume4;
	}

	public void setConsume4(BigDecimal consume4) {
		this.consume4 = consume4;
	}

	public String getNote4() {
		return note4;
	}

	public void setNote4(String note4) {
		this.note4 = note4;
	}

	public BigDecimal getConsume5() {
		return consume5;
	}

	public void setConsume5(BigDecimal consume5) {
		this.consume5 = consume5;
	}

	public String getNote5() {
		return note5;
	}

	public void setNote5(String note5) {
		this.note5 = note5;
	}

	public BigDecimal getConsume6() {
		return consume6;
	}

	public void setConsume6(BigDecimal consume6) {
		this.consume6 = consume6;
	}

	public String getNote6() {
		return note6;
	}

	public void setNote6(String note6) {
		this.note6 = note6;
	}

	public BigDecimal getConsume7() {
		return consume7;
	}

	public void setConsume7(BigDecimal consume7) {
		this.consume7 = consume7;
	}

	public String getNote7() {
		return note7;
	}

	public void setNote7(String note7) {
		this.note7 = note7;
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

}
