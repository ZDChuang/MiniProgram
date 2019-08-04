package com.dech.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.alibaba.fastjson.JSONObject;
import com.dech.domain.PushInfo;
import com.dech.domain.Secret;
import com.dech.domain.TemplateMessage;
import com.dech.domain.Users;
import com.dech.repository.PushRepository;
import com.dech.repository.SecretRepository;
import com.dech.repository.UserRepository;
import com.dech.util.MiniProgramUtils;

@RestController
public class PushController {
	private static final Logger logger = LoggerFactory.getLogger(PushController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PushRepository pushRepository;

	@Autowired
	private SecretRepository secretRepository;

	
	/**
	 * 向用户推送模板消息
	 * @param openid
	 */
	@GetMapping(value = "/send/push")
	public void push(@RequestParam String openid) {
		List<Secret> list = secretRepository.findAll();
		if (list == null || list.size() == 0) {
			logger.error("no token find.");
			return;
		}

		Secret s = list.get(0);

		List<PushInfo> info = pushRepository.findByOpenId(openid);
		if (info == null || info.size() == 0) {
			logger.error("no openid find.");
			return;
		}

		PushInfo push = null;
		for(int i = 0; i < info.size(); i++) {
			if(info.get(i).getStatus().equals("A")) {
				push = info.get(i);
				break;
			}
		}
		
		if(push == null) {
			logger.error("no active formId.");
			return;
		}

		TemplateMessage tm = new TemplateMessage();
		tm.setAccess_token(s.getToken());
		tm.setTouser(openid);
		tm.setTemplate_id(push.getTemplate());
		tm.setForm_id(push.getFormId());
		tm.setPage("");
//		tm.setEmphasis_keyword("keyword1.DATA");
		tm.setEmphasis_keyword("");

		Map<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> m1 = new HashMap<String, String>();
		HashMap<String, String> m2 = new HashMap<String, String>();
		HashMap<String, String> m3 = new HashMap<String, String>();
		m1.put("value", push.getInfo1());
		map.put("keyword1", m1);

		m2.put("value", push.getInfo2());
		map.put("keyword2", m2);

		m3.put("value", sdf.format(new Date()));
		map.put("keyword3", m3);

		tm.setData(map);

		String message = MiniProgramUtils.push(s.getToken(), JSONObject.toJSONString(tm));
		
		JSONObject obj = (JSONObject) JSONObject.parse(message);
		if ((int) obj.get("errcode") == 0) {
			push.setStatus("S");
		} else {
			push.setStatus("F");
		}
		push.setMessage((String) obj.get("errmsg"));
		push.setPushTime(new Date());
		
		pushRepository.save(push);
		logger.info(message);
	}

	
	/**
	 * 手动刷新获取access_token，防止定时刷新滞后。
	 */
	@GetMapping(value = "/send/token")
	public void refresh() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		
		List<Secret> list = secretRepository.findAll();
		if(list == null || list.size() == 0) {
			logger.info("no secret find.");
			return;
		}
		
		Secret s = list.get(0);
		JSONObject obj = JSONObject.parseObject(MiniProgramUtils.getToken());
		if (obj.get("access_token") != null) {
			calendar.add(Calendar.SECOND, obj.getInteger("expires_in"));
			s.setExpires(calendar.getTime());
			s.setToken(obj.getString("access_token"));
			secretRepository.save(s);
		}
	}
	
	/**
	 * 校验消息推送
	 * 启用并设置消息推送配置后，用户发给小程序的消息以及开发者需要的事件推送，都将被微信转发至该服务器地址中
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	@GetMapping(value = "/receive/check")
	public String receive(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce,
			@RequestParam String echostr) {
		logger.info("receive: " + new Date());
		logger.info(signature);
		logger.info(timestamp);
		logger.info(nonce);
		logger.info(echostr);

		boolean pass = MiniProgramUtils.checkSignature(signature, timestamp, nonce);

		if (pass) {
			return echostr;
		}
		logger.info("check failed.");
		return "receive check failed.";
	}

	/**
	 * 根据小程序登录码获取open id
	 * @param code
	 * @return
	 */
	@GetMapping(value = "/receive/openid")
	public String receive(@RequestParam String code) {
		logger.info("code: " + code);
		String res = MiniProgramUtils.getOpenid(code);
		logger.info("login: " + res);

		JSONObject obj = (JSONObject) JSONObject.parse(res);

		if (obj.get("openid") == null) {
			return null;
		}

		String openid = (String) obj.get("openid");
		String sessionKey = (String) obj.get("session_key");

		List<Users> users = userRepository.findByOpenId(openid);
		if (users == null || users.size() == 0) {
			Users u = new Users();
			u.setOpenId(openid);
			u.setSessionKey(sessionKey);
			u.setUpdateCount(0);
			userRepository.save(u);
		}

		return res;
	}

	
	/**
	 * 添加模板信息
	 * @param push
	 */
	@PostMapping(value = "/receive/formid")
	public void generateFormid(@RequestBody PushInfo push) {
		String openId = push.getOpenId();
		if (openId == null || openId.equals("")) {
			logger.info("the openid is null");
			return;
		}

		String formId = push.getFormId();
		if (formId == null || formId.equals("")) {
			logger.info("the formId is null");
			return;
		}

		push.setInfo1("This is a test message");
		push.setInfo2("工作计划、健身计划、饮食计划、读书计划、记账经济");
		push.setStatus("A");
		push.setTemplate((MiniProgramUtils.TEMPLATE_STUDY));
		pushRepository.save(push);
		logger.info("/receive/formid insert a record: " + push.getFormId());
	}

	
	/**
	 * 接收小程序用户信息
	 * @param user
	 * @return
	 */
	@PostMapping(value = "/receive/user")
	public Users getUserInfo(@RequestBody Users user) {
		String openId = user.getOpenId();
		if (openId == null || openId.equals("")) {

			String unionId = user.getUnionId();
			if (unionId == null || unionId.equals("")) {
				logger.info("openId and unionId are all blanks.");
				return null;
			} else {
				Users u = userRepository.findByUnionId(unionId);
				if (u == null) {
					user.setUpdateCount(0);
					userRepository.save(user);
					logger.info("insert a record: " + user.getUnionId());
				} else {
					u.setUpdateCount(u.getUpdateCount() + 1);
					u.setCity(user.getCity());
					u.setCountry(user.getCountry());
					u.setNickName(user.getNickName());
					u.setSessionKey(user.getSessionKey());
					u.setTelephone(user.getTelephone());

					userRepository.save(u);
					logger.info("update a record: " + u.getUnionId());
				}
			}
		} else {
			List<Users> list = userRepository.findByOpenId(openId);
			if (list.size() != 1) {
				logger.info("the numbers query by openid is " + list.size());
			}

			if (list.size() == 0) {
				user.setUpdateCount(0);
				userRepository.save(user);
				logger.info("insert a record: " + user.getOpenId());
			} else {
				Users u = list.get(0);
				u.setUpdateCount(u.getUpdateCount() + 1);
				u.setCity(user.getCity());
				u.setCountry(user.getCountry());
				u.setNickName(user.getNickName());
				u.setSessionKey(user.getSessionKey());
				u.setTelephone(user.getTelephone());

				userRepository.save(u);
				logger.info("/receive/info update a record: " + u.getOpenId());
			}
		}
		return user;
	}

}