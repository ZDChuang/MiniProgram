package com.dech.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.dech.domain.PushInfo;
import com.dech.domain.PushRule;
import com.dech.domain.Secret;
import com.dech.repository.PushRepository;
import com.dech.repository.RuleRepository;
import com.dech.repository.SecretRepository;

@Component
@EnableScheduling
public class PushScheduleTask {
	private static final Logger logger = LoggerFactory.getLogger(PushScheduleTask.class);

	@Autowired
	private SecretRepository secretRepository;

	@Autowired
	private PushRepository pushRepository;

	@Autowired
	private RuleRepository ruleRepository;

	/**
	 * 推送
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void pushMsg() {
		List<Secret> list = secretRepository.findAll();
		if (list == null || list.size() == 0) {
			logger.error("no token find.");
			return;
		}

		Secret s = list.get(0);

		List<PushRule> rules = ruleRepository.findByStatus("A");
		if (rules == null || rules.size() == 0) {
			return;
		}

		for (PushRule rule : rules) {
			PushInfo push = pushRepository.findPushInfo(rule.getOpenid(), "A");
			if (push == null) {
				continue;
			}
			
			JSONObject obj = MiniProgramUtils.push(push, rule.getOpenid(), s.getToken());
			if ((int) obj.get("errcode") == 0) {
				push.setStatus("S");
			} else {
				push.setStatus("F");
			}
			
			push.setMessage((String) obj.get("errmsg"));
			push.setPushTime(new Date());

			pushRepository.save(push);
		}

	}

	/**
	 * 定时刷新，获取access_token。每10分钟执行一次，在token过期之前的15分钟到25分钟内执行
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void updateToken() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		List<Secret> list = secretRepository.findAll();
		if (list == null || list.size() == 0) {
			JSONObject obj = JSONObject.parseObject(MiniProgramUtils.getToken());

			if (obj.get("access_token") != null) {
				Secret s = new Secret();
				s.setAppId(MiniProgramUtils.APPID);
				s.setAppSecret(MiniProgramUtils.APPSECRET);
				calendar.add(Calendar.SECOND, obj.getInteger("expires_in"));
				s.setExpires(calendar.getTime());
				s.setToken(obj.getString("access_token"));
				secretRepository.save(s);
			}

		} else {
			Secret s = list.get(0);
			calendar.add(Calendar.SECOND, 60 * 25);

			if (calendar.getTime().getTime() > s.getExpires().getTime()) {
				JSONObject obj = JSONObject.parseObject(MiniProgramUtils.getToken());

				if (obj.get("access_token") != null) {
					calendar.add(Calendar.SECOND, obj.getInteger("expires_in"));
					s.setExpires(calendar.getTime());
					s.setToken(obj.getString("access_token"));
					secretRepository.save(s);
					logger.info("shedule: update access token.");
				}
			}
		}
	}

	/**
	 * 更新form id状态，每小时执行一次，整点更新，如13:00:00
	 */
	@Scheduled(cron = "0 0 * * * ?")
	public void updateStatus() {
		List<PushInfo> list = pushRepository.findAll();
		if (list == null) {
			return;
		}

		Calendar calendar = Calendar.getInstance();
		PushInfo p = null;
		for (int i = 0; i < list.size(); i++) {
			p = list.get(i);
			if (!p.getStatus().equals("A")) {
				continue;
			}

			calendar.setTime(p.getCreateTime());
			calendar.add(Calendar.DATE, 7);
			if (calendar.getTime().getTime() < new Date().getTime()) {
				p.setStatus("E");
				p.setMessage("Expired");
				pushRepository.save(p);
				logger.info("shedule: update formid status.");
			}
		}
	}
}
