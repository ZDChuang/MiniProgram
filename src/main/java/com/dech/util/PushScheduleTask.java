package com.dech.util;

import java.text.SimpleDateFormat;
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

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 推送
	 */
	@Scheduled(cron = "0/30 * * * * ?")
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

		Calendar cal = Calendar.getInstance();

		// 检查是否需要推送
		String openid = "";
		for (PushRule rule : rules) {
			if (rule.getOpenid().equals(openid)) {
				continue;
			}

			PushInfo push = pushRepository.findPushInfo(rule.getOpenid(), "A");
			if (push == null) {
				openid = rule.getOpenid();
				continue;
			}

			Date date = new Date();

			if ("day".equals(rule.getPeriod())) {

				if (!checkPushtime(rule, date)) {
					continue;
				}

			} else if ("week".equals(rule.getPeriod())) {
				boolean flag = false;
				cal.setTime(date);
				int d = cal.get(Calendar.DAY_OF_WEEK);
				String day = "";
				switch (d) {
				case 1:
					day = "Sunday";
					break;
				case 2:
					day = "Monday";
					break;
				case 3:
					day = "Tuesday";
					break;
				case 4:
					day = "Wednesday";
					break;
				case 5:
					day = "Thursday";
					break;
				case 6:
					day = "Friday";
					break;
				case 7:
					day = "Saturday";
					break;
				}

				String[] week = rule.getPeriodweek().split("-");
				for (String w : week) {
					if (w.equals(day)) {
						flag = true;
						break;
					}
				}

				if (!flag) {
					continue;
				}

				if (!checkPushtime(rule, date)) {
					continue;
				}

			} else {
				continue;
			}

			// 检查通过，开始推送消息
			JSONObject obj = MiniProgramUtils.push(push, rule, s.getToken());
			if ((int) obj.get("errcode") == 0) {
				push.setStatus("S");
			} else {
				push.setStatus("F");
			}

			// 更新推送信息
			push.setMessage((String) obj.get("errmsg"));
			push.setPushTime(date);
			pushRepository.save(push);

			// 更新推送规则信息
			if ("S".equals(push.getStatus())) {
				rule.setPushtime(date);
				rule.setPushtimes(rule.getPushtimes() + 1);
				ruleRepository.save(rule);
			}
		}
	}

	private boolean checkPushtime(PushRule rule, Date date) {
		int time = Integer.valueOf(sdf.format(date));
		if(time < 0630) {
			return false;
		}
		
		// push every X hours
		if (rule.getHours() > 0) {
			if (rule.getPushtime() != null) {
				// 距离上次推送时间小于X小时，不推送
				if ((date.getTime() - rule.getPushtime().getTime()) / (1000 * 60 * 60) < rule.getHours()) {
					return false;
				}
			}

			// push at fixed time
		} else if (!"".equals(rule.getFixtime())) {
			int fixtime = Integer.valueOf(rule.getFixtime().replace(":", ""));
			int currenttime = Integer.valueOf(sdf.format(date));

			// 当日已推送，则不再推送
			if (rule.getPushtime() != null && sdf2.format(rule.getPushtime()).equals(sdf2.format(date))) {
				return false;
			}

			if (currenttime < fixtime) {
				return false;
			}

		} else {
			return false;
		}
		return true;
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
