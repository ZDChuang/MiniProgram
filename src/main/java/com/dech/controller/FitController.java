package com.dech.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dech.domain.Fit;
import com.dech.repository.FitRepository;

@RestController
public class FitController {
	private static final Logger logger = LoggerFactory.getLogger(FitController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	private FitRepository fitRepository;

	@PostMapping(value = "/receive/exercise")
	@Transactional
	public String saveExerciseData(@RequestBody Fit info) {

		String openId = info.getOpenid();
		if (openId == null || openId.equals("")) {
			logger.info("the openid is null");
			return "the openid is null";
		}

		int date = Integer.valueOf(sdf.format(new Date()));
		int code = 0;
		Fit fit = fitRepository.findRecords(openId, date);

		if (fit == null) {
			fit = new Fit();
			fit.setOpenid(openId);
			fit.setDate(date);
			fit.setBody(info.getBody());
			fit.setTime(info.getTime());
			fit.setType(info.getType());
			fit.setWeight(info.getWeight());

			fitRepository.save(fit);
			code = 0;

		} else {
			code = 1;
		}

		return findAll(openId, code);
	}

	@GetMapping(value = "/receive/exercise/findall")
	public String findAllData(@RequestParam String openid) {

		if (openid == null || openid.equals("")) {
			logger.info("the openid is null");
			return "the openid is null";
		}

		return findAll(openid, 0);
	}

	@GetMapping(value = "/receive/exercise/days")
	public Map<String, List<Integer>> countTimes(@RequestParam String openid) {

		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

		if (openid == null || openid.equals("")) {
			logger.error("getTotal: the openid is null");
			return map;
		}

		List<Integer> months = new ArrayList<Integer>();
		List<Integer> counts = new ArrayList<Integer>();
		List<Fit> list = fitRepository.findAllData(openid);
		int month = 0;
		int count = 0;

		for (Fit f : list) {
			if (f.getDate() == 0) {
				continue;
			}

			if (month == 0) {
				month = f.getDate() / 100;
			}

			if (f.getDate() / 100 != month) {
				months.add(month);
				counts.add(count);
				month = f.getDate() / 100;
				count = 1;
			} else {
				count++;
			}
		}
		months.add(month);
		counts.add(count);
		map.put("months", months);
		map.put("counts", counts);

		return map;

	}

	private String findAll(String openId, int code) {
		List<Fit> list = fitRepository.findByOpenid(openId);
		int arm = 0;
		int chest = 0;
		int leg = 0;
		int back = 0;
		int belly = 0;

		int home = 0;
		int gym = 0;
		int park = 0;

		double hours = 0;

		for (Fit f : list) {
			if ("arm".equals(f.getBody())) {
				arm++;
			} else if ("chest".equals(f.getBody())) {
				chest++;
			} else if ("leg".equals(f.getBody())) {
				leg++;
			} else if ("back".equals(f.getBody())) {
				back++;
			} else if ("belly".equals(f.getBody())) {
				belly++;
			}

			if ("home".equals(f.getType())) {
				home++;
			} else if ("gym".equals(f.getType())) {
				gym++;
			} else if ("park".equals(f.getType())) {
				park++;
			}

			hours += f.getTime();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);

		map.put("days", list.size());

		map.put("arm", arm);
		map.put("chest", chest);
		map.put("leg", leg);
		map.put("back", back);
		map.put("belly", belly);

		map.put("home", home);
		map.put("park", park);
		map.put("gym", gym);

		map.put("hours", new BigDecimal(hours / 60).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());

		return JSONObject.toJSONString(map);
	}
}
