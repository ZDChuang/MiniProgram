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
public class Economy {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private String openid;

	// yyyyMMdd
	private int date;

	private BigDecimal wchange;
	private BigDecimal wlicaitong;
	private BigDecimal wbenifit;
	
	
	private BigDecimal alipay;
	private BigDecimal ahuabei;
	private BigDecimal abenifit;
	
	private BigDecimal cmb;
	private BigDecimal cbenifit;
	private BigDecimal credit;
	
	private BigDecimal clicai;
	private BigDecimal sbenifit;
	
	private BigDecimal zz;
	private BigDecimal pingan;
	private BigDecimal pbenifit;
	
	
	private BigDecimal money;
	private BigDecimal debt;

	private BigDecimal fund;
	
	private String income;
	private BigDecimal incomes;
	private BigDecimal income1;
	private BigDecimal income2;
	private BigDecimal income3;
	private BigDecimal income4;
	private BigDecimal income5;
	private BigDecimal income6;
	private BigDecimal income7;

	private BigDecimal benifitsum;

	// 总金额（不含公积金）
	private BigDecimal total;

	// 含公积金总额
	private BigDecimal fundsum;

	// 归属个人总额
	private BigDecimal owntotal;

	private BigDecimal consume;

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

	public BigDecimal getWchange() {
		return wchange;
	}

	public void setWchange(BigDecimal wchange) {
		this.wchange = wchange;
	}

	public BigDecimal getWlicaitong() {
		return wlicaitong;
	}

	public void setWlicaitong(BigDecimal wlicaitong) {
		this.wlicaitong = wlicaitong;
	}

	public BigDecimal getAlipay() {
		return alipay;
	}

	public void setAlipay(BigDecimal alipay) {
		this.alipay = alipay;
	}

	public BigDecimal getAhuabei() {
		return ahuabei;
	}

	public void setAhuabei(BigDecimal ahuabei) {
		this.ahuabei = ahuabei;
	}

	public BigDecimal getCmb() {
		return cmb;
	}

	public void setCmb(BigDecimal cmb) {
		this.cmb = cmb;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getClicai() {
		return clicai;
	}

	public void setClicai(BigDecimal clicai) {
		this.clicai = clicai;
	}

	public BigDecimal getZz() {
		return zz;
	}

	public void setZz(BigDecimal zz) {
		this.zz = zz;
	}

	public BigDecimal getPingan() {
		return pingan;
	}

	public void setPingan(BigDecimal pingan) {
		this.pingan = pingan;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public BigDecimal getDebt() {
		return debt;
	}

	public void setDebt(BigDecimal debt) {
		this.debt = debt;
	}

	public BigDecimal getFund() {
		return fund;
	}

	public void setFund(BigDecimal fund) {
		this.fund = fund;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getFundsum() {
		return fundsum;
	}

	public void setFundsum(BigDecimal fundsum) {
		this.fundsum = fundsum;
	}

	public BigDecimal getOwntotal() {
		return owntotal;
	}

	public void setOwntotal(BigDecimal owntotal) {
		this.owntotal = owntotal;
	}

	public BigDecimal getConsume() {
		return consume;
	}

	public void setConsume(BigDecimal consume) {
		this.consume = consume;
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

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public BigDecimal getWbenifit() {
		return wbenifit;
	}

	public void setWbenifit(BigDecimal wbenifit) {
		this.wbenifit = wbenifit;
	}

	public BigDecimal getAbenifit() {
		return abenifit;
	}

	public void setAbenifit(BigDecimal abenifit) {
		this.abenifit = abenifit;
	}

	public BigDecimal getCbenifit() {
		return cbenifit;
	}

	public void setCbenifit(BigDecimal cbenifit) {
		this.cbenifit = cbenifit;
	}

	public BigDecimal getSbenifit() {
		return sbenifit;
	}

	public void setSbenifit(BigDecimal sbenifit) {
		this.sbenifit = sbenifit;
	}

	public BigDecimal getPbenifit() {
		return pbenifit;
	}

	public void setPbenifit(BigDecimal pbenifit) {
		this.pbenifit = pbenifit;
	}

	public BigDecimal getBenifitsum() {
		return benifitsum;
	}

	public void setBenifitsum(BigDecimal benifitsum) {
		this.benifitsum = benifitsum;
	}


	public BigDecimal getIncome1() {
		return income1;
	}

	public void setIncome1(BigDecimal income1) {
		this.income1 = income1;
	}


	public BigDecimal getIncome2() {
		return income2;
	}

	public void setIncome2(BigDecimal income2) {
		this.income2 = income2;
	}

	public BigDecimal getIncome3() {
		return income3;
	}

	public void setIncome3(BigDecimal income3) {
		this.income3 = income3;
	}

	public BigDecimal getIncome4() {
		return income4;
	}

	public void setIncome4(BigDecimal income4) {
		this.income4 = income4;
	}

	public BigDecimal getIncome5() {
		return income5;
	}

	public void setIncome5(BigDecimal income5) {
		this.income5 = income5;
	}

	public BigDecimal getIncome6() {
		return income6;
	}

	public void setIncome6(BigDecimal income6) {
		this.income6 = income6;
	}

	public BigDecimal getIncome7() {
		return income7;
	}

	public void setIncome7(BigDecimal income7) {
		this.income7 = income7;
	}

	public BigDecimal getIncomes() {
		return incomes;
	}

	public void setIncomes(BigDecimal incomes) {
		this.incomes = incomes;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}
}
