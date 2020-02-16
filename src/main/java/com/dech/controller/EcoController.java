package com.dech.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dech.domain.Consume;
import com.dech.domain.Economy;
import com.dech.repository.ConsumeRepository;
import com.dech.repository.EcoRepository;
import com.dech.util.CalculateType;

@RestController
public class EcoController {
	private static final Logger logger = LoggerFactory.getLogger(EcoController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	private EcoRepository ecoRepository;

	@Autowired
	private ConsumeRepository consumeRepository;

	@GetMapping(value = "/receive/economy/other")
	public Map<String, List<Object>> getOther(@RequestParam String openid) {
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();

		if (openid == null || openid.equals("")) {
			logger.error("getOther: the openid is null");
			return map;
		}

		List<Economy> eco = ecoRepository.findAllData(openid);
		if (eco == null) {
			return map;
		}

		List<Object> consume = new ArrayList<Object>();
		List<Object> income = new ArrayList<Object>();
		List<Object> benifit = new ArrayList<Object>();
		List<Object> net = new ArrayList<Object>();
		List<Object> dates = new ArrayList<Object>();

		int first = 0;
		int date = 0;
		BigDecimal temp = BigDecimal.ZERO;

		// 取当月最初的一条数据
		for (Economy e : eco) {
			date = e.getDate() / 100;
			if (first != date) {
				first = date;
				dates.add(date);

				consume.add(e.getConsume());

				income.add(e.getIncomes());
				benifit.add(e.getBenifitsum());

				// 每月净增加（当月总金额 -上月总金额）
				if (temp.compareTo(BigDecimal.ZERO) == 0) {
					net.add(temp);
				} else {
					if (date == 202001) {
						net.add(e.getOwntotal().subtract(temp).subtract(new BigDecimal("53448.96")));
					} else {
						net.add(e.getOwntotal().subtract(temp));
					}
				}
				temp = e.getOwntotal();
			}
		}
		map.put("consume", consume);
		map.put("income", income);
		map.put("benifit", benifit);
		map.put("net", net);
		map.put("date", dates);
		return map;
	}

	@GetMapping(value = "/receive/economy/percent")
	public Map<String, BigDecimal> getPercent(@RequestParam String openid, @RequestParam int date) {

		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();

		if (openid == null || openid.equals("")) {
			logger.error("getPercent: the openid is null");
			return map;
		}

		if (date < 20180100 || date > 20301231) {
			logger.error("getPercent: the date is invalid");
			return map;
		}

		Integer.valueOf(date / 100 + "01");
		Economy eco = ecoRepository.findLastMonth(openid, Integer.valueOf(date / 100 + "01"),
				Integer.valueOf(date / 100 + "31"));
		if (eco == null) {
			return map;
		}

		map.put("current", eco.getWchange().add(eco.getWlicaitong()).add(eco.getMoney()));
		map.put("hfund", eco.getCmb());
		map.put("fund", eco.getAlipay().add(eco.getZz()));
		map.put("stock", eco.getPingan().add(eco.getClicai()));
		return map;

	}

	@GetMapping(value = "/receive/economy/total")
	public Map<String, List<Object>> getTotal(@RequestParam String openid) {
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();

		if (openid == null || openid.equals("")) {
			logger.error("getTotal: the openid is null");
			return map;
		}

		List<Economy> eco = ecoRepository.findAllData(openid);
		if (eco == null) {
			return map;
		}

		List<Object> total = new ArrayList<Object>();
		List<Object> fundsum = new ArrayList<Object>();
		List<Object> dates = new ArrayList<Object>();

		int first = 0;
		int date = 0;

		// 取当月最初的一条数据
		for (Economy e : eco) {
			date = e.getDate() / 100;
			if (first != date) {
				first = date;
				dates.add(date);
				total.add(e.getTotal());
				fundsum.add(e.getFundsum());
			}
		}
		map.put(CalculateType.TOTAL.getName(), total);
		map.put("fundsum", fundsum);
		map.put("date", dates);
		return map;
	}

	@GetMapping(value = "/receive/economy/calculate")
	public Map<String, BigDecimal> calculate(@RequestParam String openid, @RequestParam int start,
			@RequestParam int end, @RequestParam List<String> list) {
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		if (openid == null || openid.equals("")) {
			logger.error("recentDate: the openid is null");
			return map;
		}

		if (end <= start) {
			logger.error("start date great than end date.");
			return map;
		}

		if (list == null || list.size() < 1) {
			logger.error("no data to query.");
			return map;
		}

		for (String s : list) {
			s = s.replace("\"", "").replace("[", "").replace("]", "");
			if (s.equals(CalculateType.INCOME.getName())) {
				map.put(s, calculateIncome(openid, start, end));
			} else if (s.equals(CalculateType.BENEFIT.getName())) {
				map.put(s, calculateBenefit(openid, start, end));
			} else if (s.equals(CalculateType.COSUME.getName())) {
				map.put(s, calculateConsume(openid, start, end));
			} else if (s.equals(CalculateType.TOTAL.getName())) {
				map.put(s, calculateTotal(openid, start, end));
			}
		}
		return map;
	}

	private BigDecimal calculateIncome(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {

		BigDecimal income = ecoRepository.calculateIncome(openid, start, end);
		if (income == null) {
			income = BigDecimal.ZERO;
		}
		return income;
	}

	private BigDecimal calculateTotal(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {

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

	private BigDecimal calculateBenefit(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {

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

	private BigDecimal calculateConsume(@RequestParam String openid, @RequestParam int start, @RequestParam int end) {

		Economy eco = ecoRepository.findStartRecords(openid, start);
		Economy eco2 = ecoRepository.findEndRecords(openid, end);

		BigDecimal totalStart = BigDecimal.ZERO;
		BigDecimal totalEnd = BigDecimal.ZERO;
		BigDecimal benifitStart = BigDecimal.ZERO;
		BigDecimal benifitEnd = BigDecimal.ZERO;

		BigDecimal income = calculateIncome(openid, start, end);

		if (eco == null || eco2 == null) {
			return BigDecimal.ZERO;
		}

		if (eco.getId() == eco2.getId()) {
			return BigDecimal.ZERO;
		}

		benifitStart = eco.getBenifitsum();
		totalStart = eco.getTotal();
		benifitEnd = eco2.getBenifitsum();
		totalEnd = eco2.getTotal();

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

	@GetMapping(value = "/receive/economy/consumeQuery")
	public Consume queryConsume(@RequestParam String openid, @RequestParam String date) {
		if (openid == null || openid.equals("")) {
			logger.error("the openid is null");
			return null;
		}

		int start = Integer.valueOf(date + "01");
		int end = Integer.valueOf(date + "31");
		Consume consume = consumeRepository.findRecentConsume(openid, start, end);
		if (consume == null) {
			consume = new Consume();
		}
		return consume;
	}

	@GetMapping(value = "/receive/economy/consumeQuery2")
	public Consume queryConsume2(@RequestParam String openid, @RequestParam int date) {
		if (openid == null || openid.equals("")) {
			logger.error("the openid is null");
			return new Consume();
		}

		if (date <= 20180100) {
			return new Consume();
		}

		Consume consume = consumeRepository.findRecentConsume2(openid, date);
		if (consume == null) {
			consume = new Consume();
		}
		return consume;
	}

	@PostMapping(value = "/receive/economy/consumeSave")
	public int saveConsumeData(@RequestBody Consume info) {

		String openId = info.getOpenid();
		if (openId == null || openId.equals("")) {
			logger.error("the openid is null");
			return -1;
		}
		int date = info.getDate();

		if (date <= 20180100 || date > 20300100 || date % 100 > 31 || date % 100 <= 0 || date / 100 % 100 > 12
				|| date / 100 % 100 < 1) {
			return -3;
		}

		if (info.getConsume1() == null || info.getConsume1().compareTo(BigDecimal.ZERO) <= 0) {
			logger.error("the consume is null");
			return -2;
		}

		if (info.getNote1() == null || info.getNote1().trim().equals("")) {
			logger.error("the remark is null");
			return -2;
		}

		if (info.getNote2() == null || info.getNote2().trim().equals("")) {
			info.setNote2("消费2");
		}

		if (info.getNote3() == null || info.getNote3().trim().equals("")) {
			info.setNote3("消费3");
		}

		if (info.getNote4() == null || info.getNote4().trim().equals("")) {
			info.setNote4("消费4");
		}

		if (info.getNote5() == null || info.getNote5().trim().equals("")) {
			info.setNote5("消费5");
		}

		if (info.getNote6() == null || info.getNote6().trim().equals("")) {
			info.setNote6("消费6");
		}
		if (info.getNote7() == null || info.getNote7().trim().equals("")) {
			info.setNote7("消费7");
		}

		if (info.getConsume2() == null || info.getConsume2().compareTo(BigDecimal.ZERO) < 0) {
			info.setConsume2(BigDecimal.ZERO);
		}

		if (info.getConsume3() == null || info.getConsume3().compareTo(BigDecimal.ZERO) < 0) {
			info.setConsume3(BigDecimal.ZERO);
		}

		if (info.getConsume4() == null || info.getConsume4().compareTo(BigDecimal.ZERO) < 0) {
			info.setConsume4(BigDecimal.ZERO);
		}

		if (info.getConsume5() == null || info.getConsume5().compareTo(BigDecimal.ZERO) < 0) {
			info.setConsume5(BigDecimal.ZERO);
		}
		if (info.getConsume6() == null || info.getConsume6().compareTo(BigDecimal.ZERO) < 0) {
			info.setConsume6(BigDecimal.ZERO);
		}
		if (info.getConsume7() == null || info.getConsume7().compareTo(BigDecimal.ZERO) < 0) {
			info.setConsume7(BigDecimal.ZERO);
		}

		info.setConsume(info.getConsume1().add(info.getConsume2()).add(info.getConsume3()).add(info.getConsume4())
				.add(info.getConsume5()).add(info.getConsume6()).add(info.getConsume7()));

		Consume consume = consumeRepository.findConsume(openId, info.getDate());
		if (consume == null) {
			consumeRepository.save(info);
		} else {
			consume.setConsume1(info.getConsume1());
			consume.setConsume2(info.getConsume2());
			consume.setConsume3(info.getConsume3());
			consume.setConsume4(info.getConsume4());
			consume.setConsume5(info.getConsume5());
			consume.setConsume6(info.getConsume6());
			consume.setConsume7(info.getConsume7());

			consume.setNote1(info.getNote1());
			consume.setNote2(info.getNote2());
			consume.setNote3(info.getNote3());
			consume.setNote4(info.getNote4());
			consume.setNote5(info.getNote5());
			consume.setNote6(info.getNote6());
			consume.setNote7(info.getNote7());

			consume.setConsume(info.getConsume());
			consumeRepository.save(consume);
		}

		return 0;
	}

	@PostMapping(value = "/receive/economy")
//	@Transactional
	public int saveEcoData(@RequestBody Economy info) {

		String openId = info.getOpenid();
		if (openId == null || openId.equals("")) {
			logger.error("the openid is null");
			return -1;
		}

		if (info.getIncome() == null || info.getIncome().equals("")) {
			logger.error("the income is null");
			return -2;
		}

		// +、*、|、\等符号在正则表达示中有相应的不同意义。
		String[] incomes = info.getIncome().split("\\+");
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal b = BigDecimal.ZERO;
		List<BigDecimal> list = new ArrayList<BigDecimal>();

		for (String income1 : incomes) {
			String[] income2 = income1.split("\\-");

			for (int i = 0; i < income2.length; i++) {
				try {
					b = new BigDecimal(income2[i]);
				} catch (Exception e) {
					logger.error(e.getMessage());
					return -2;
				}

				// 第一个数字前面一定是正号
				if (i == 0) {
					list.add(b);
					sum = sum.add(b);
				} else {
					list.add(b.negate());
					sum = sum.subtract(b);
				}
			}
		}

		int date = Integer.valueOf(sdf.format(new Date()));

		int month = date / 100 % 100;
		int year = date / 10000;
		if (month == 1) {
			month = 12;
			year = year - 1;
		} else {
			month = month - 1;
		}
		int lastDate = Integer.valueOf(year + String.format("%02d", month) + "01");
		int lastDate2 = Integer.valueOf(year + String.format("%02d", month) + "31");
		Economy lastMonth = ecoRepository.findLastMonth(openId, lastDate, lastDate2);

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
			eco.setIncomes(sum);

			int size = list.size();
			switch (size) {
			case 0:
				break;
			case 1:
				eco.setIncome1(list.get(0));
				break;
			case 2:
				eco.setIncome1(list.get(0));
				eco.setIncome2(list.get(1));
				break;
			case 3:
				eco.setIncome1(list.get(0));
				eco.setIncome2(list.get(1));
				eco.setIncome3(list.get(2));
				break;
			case 4:
				eco.setIncome1(list.get(0));
				eco.setIncome2(list.get(1));
				eco.setIncome3(list.get(2));
				eco.setIncome4(list.get(3));
				break;
			case 5:
				eco.setIncome1(list.get(0));
				eco.setIncome2(list.get(1));
				eco.setIncome3(list.get(2));
				eco.setIncome4(list.get(3));
				eco.setIncome5(list.get(4));
				break;
			case 6:
				eco.setIncome1(list.get(0));
				eco.setIncome2(list.get(1));
				eco.setIncome3(list.get(2));
				eco.setIncome4(list.get(3));
				eco.setIncome5(list.get(4));
				eco.setIncome6(list.get(5));
				break;
			default:
				eco.setIncome1(list.get(0));
				eco.setIncome2(list.get(1));
				eco.setIncome3(list.get(2));
				eco.setIncome4(list.get(3));
				eco.setIncome5(list.get(4));
				eco.setIncome6(list.get(5));
				eco.setIncome7(list.get(6));
				break;
			}

			eco.setWbenifit(info.getWbenifit());
			eco.setAbenifit(info.getAbenifit());
			eco.setSbenifit(info.getSbenifit());
			eco.setCbenifit(info.getCbenifit());
			eco.setPbenifit(info.getPbenifit());

			BigDecimal lastZzBalance = eco.getZz();
			if (lastMonth != null) {
				lastZzBalance = lastMonth.getZz();
			}

			eco.setBenifitsum(eco.getWbenifit().add(eco.getAbenifit()).add(eco.getSbenifit()).add(eco.getCbenifit())
					.add(eco.getPbenifit()).add(eco.getZz().subtract(lastZzBalance)));

			eco.setTotal(eco.getWchange().add(eco.getWlicaitong()).add(eco.getAlipay()).add(eco.getAhuabei())
					.add(eco.getCmb()).add(eco.getCredit()).add(eco.getClicai()).add(eco.getZz()).add(eco.getPingan())
					.add(eco.getMoney()).add(eco.getDebt()));

			eco.setFundsum(eco.getTotal().add(eco.getFund()));
			eco.setOwntotal(eco.getTotal());

			if (lastMonth == null) {
				eco.setConsume(BigDecimal.ZERO);
			} else {
				// 当月总金额-上月总金额=收入-消费+当月理财总收益-上月理财总收益
				eco.setConsume(eco.getIncomes().add(eco.getBenifitsum().subtract(lastMonth.getBenifitsum()))
						.subtract(eco.getTotal().subtract(lastMonth.getTotal())));
			}

			ecoRepository.save(eco);

		} else {
			code = 1;
		}

		return code;
	}

}
