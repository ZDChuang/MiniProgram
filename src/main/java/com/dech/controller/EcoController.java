package com.dech.controller;

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

	@GetMapping(value = "/receive/economy/month")
	public Economy findByMonth(@RequestParam String openid, @RequestParam String date) {
		logger.info(date);
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
