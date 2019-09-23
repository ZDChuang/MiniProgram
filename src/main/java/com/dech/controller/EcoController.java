package com.dech.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dech.domain.Economy;
import com.dech.repository.EcoRepository;

@RestController
public class EcoController {
	private static final Logger logger = LoggerFactory.getLogger(EcoController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	private EcoRepository ecoRepository;

	@GetMapping(value = "/receive/economy/income")
	public BigDecimal calculateIncome(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {
		if (openid == null || openid.equals("")) {
			logger.error("recentDate: the openid is null");
			return BigDecimal.ZERO;
		}

		if (end <= start) {
			logger.error("start date great than end date.");
			return BigDecimal.ZERO;
		}

		BigDecimal income = ecoRepository.calculateIncome(openid, start, end);
		if (income == null) {
			income = BigDecimal.ZERO;
		}
		return income;
	}

	@GetMapping(value = "/receive/economy/total")
	public BigDecimal calculateTotal(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {
		if (openid == null || openid.equals("")) {
			logger.error("calculateTotal: the openid is null");
			return BigDecimal.ZERO;
		}

		if (end <= start) {
			logger.error("start date great than end date.");
			return BigDecimal.ZERO;
		}

		Economy eco = ecoRepository.findStartRecords(openid, start);
		Economy eco2 = ecoRepository.findEndRecords(openid, end);

		BigDecimal s = BigDecimal.ZERO;
		BigDecimal e = BigDecimal.ZERO;

		if (eco != null) {
			s = eco.getTotal();
		}
		if (eco2 != null) {
			e = eco2.getTotal();
		}
		return e.subtract(s);
	}

	@GetMapping(value = "/receive/economy/benifit")
	public BigDecimal calculateBenifit(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {
		if (openid == null || openid.equals("")) {
			logger.error("calculateTotal: the openid is null");
			return BigDecimal.ZERO;
		}

		if (end <= start) {
			logger.error("start date great than end date.");
			return BigDecimal.ZERO;
		}

		Economy eco = ecoRepository.findStartRecords(openid, start);
		Economy eco2 = ecoRepository.findEndRecords(openid, end);

		BigDecimal s = BigDecimal.ZERO;
		BigDecimal e = BigDecimal.ZERO;

		if (eco != null) {
			s = eco.getBenifitsum();
		}
		if (eco2 != null) {
			e = eco2.getBenifitsum();
		}
		return e.subtract(s);
	}

	@GetMapping(value = "/receive/economy/consume")
	public BigDecimal calculateConsume(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {
		if (openid == null || openid.equals("")) {
			logger.error("calculateTotal: the openid is null");
			return BigDecimal.ZERO;
		}

		if (end <= start) {
			logger.error("start date great than end date.");
			return BigDecimal.ZERO;
		}

		Economy eco = ecoRepository.findStartRecords(openid, start);
		Economy eco2 = ecoRepository.findEndRecords(openid, end);

		BigDecimal totalStart = BigDecimal.ZERO;
		BigDecimal totalEnd = BigDecimal.ZERO;
		BigDecimal benifitStart = BigDecimal.ZERO;
		BigDecimal benifitEnd = BigDecimal.ZERO;

		BigDecimal income = calculateIncome(openid, start, end);

		if (eco != null) {
			benifitStart = eco.getBenifitsum();
			totalStart = eco.getTotal();
		}
		if (eco2 != null) {
			benifitEnd = eco2.getBenifitsum();
			totalEnd = eco2.getTotal();
		}
		// 结束金额 - 起始金额 = 收入 + 盈利 - 消费
		return income.add(benifitEnd.subtract(benifitStart)).subtract(totalEnd.subtract(totalStart));
	}

	@GetMapping(value = "/receive/economy/month")
	public Economy findByMonth(@RequestParam String openid, @RequestParam String date) {
		Economy eco = ecoRepository.findMonthRecords(openid, Integer.valueOf(date));
		return eco;
	}

	@GetMapping(value = "/receive/economy/recentDate")
	public int findRecentDate(@RequestParam String openid) {
		if (openid == null || openid.equals("")) {
			logger.error("recentDate: the openid is null");
			return 0;
		}

		int date = Integer.valueOf(sdf.format(new Date()));
		Integer recentDate = ecoRepository.findRecentRecords(openid, date);
		if (recentDate == null) {
			recentDate = 0;
		}
		return recentDate;
	}

	@PostMapping(value = "/receive/economy")
	@Transactional
	public int saveEcoData(@RequestBody Economy info) {

		String openId = info.getOpenid();
		if (openId == null || openId.equals("")) {
			logger.error("the openid is null");
			return -1;
		}

		int date = Integer.valueOf(sdf.format(new Date()));

		Economy eco = ecoRepository.findRecords(openId, date);

		int code = 0;

		if (eco == null) {
			eco = new Economy();
			eco.setOpenid(openId);
			eco.setDate(date);

			eco.setWchange(info.getWchange());
			eco.setWlicaitong(info.getWlicaitong());
			eco.setAlipay(info.getAlipay());
			eco.setAhuabei(info.getAhuabei());
			eco.setCmb(info.getCmb());
			eco.setCredit(info.getCredit());
			eco.setClicai(info.getClicai());
			eco.setZz(info.getZz());
			eco.setPingan(info.getPingan());
			eco.setFund(info.getFund());
			eco.setMoney(info.getMoney());
			eco.setDebt(info.getDebt());
			eco.setIncome(info.getIncome());

			eco.setWbenifit(info.getWbenifit());
			eco.setAbenifit(info.getAbenifit());
			eco.setSbenifit(info.getSbenifit());
			eco.setCbenifit(info.getCbenifit());
			eco.setPbenifit(info.getPbenifit());

			eco.setBenifitsum(eco.getWbenifit().add(eco.getAbenifit()).add(eco.getSbenifit())
					.add(eco.getCbenifit().add(eco.getPbenifit())));

			eco.setTotal(eco.getWchange().add(eco.getWlicaitong()).add(eco.getAlipay()).add(eco.getAhuabei())
					.add(eco.getCmb()).add(eco.getCredit()).add(eco.getClicai()).add(eco.getZz()).add(eco.getPingan())
					.add(eco.getMoney()).add(eco.getDebt()));

			eco.setFundsum(eco.getTotal().add(eco.getFund()));

			ecoRepository.save(eco);

		} else {
			code = 1;
		}

		return code;
	}

}
