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
import com.dech.domain.Secret;
import com.dech.repository.SecretRepository;

@Component
@EnableScheduling
public class PushScheduleTask {
	private static final Logger logger = LoggerFactory.getLogger(PushScheduleTask.class);

	@Autowired
	private SecretRepository secretRepository;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void updateToken() {
		logger.info("shedule task.");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		List<Secret> list = secretRepository.findAll();
		if (list == null || list.size() == 0) {
			Secret s = new Secret();
			s.setAppId(MiniProgramUtils.APPID);
			s.setAppSecret(MiniProgramUtils.APPSECRET);

			JSONObject obj = JSONObject.parseObject(MiniProgramUtils.getToken());
			if (obj.get("access_token") != null) {
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
				}
			}
		}
	}
}
