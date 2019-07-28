package com.dech.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MiniProgramUtils {
	public static final String APPID = "wx7d2be53e59324611";
	public static final String APPSECRET = "5570a01af552b6d9ab80e08686c746d3";
	public static final String TEMPLATE_STUDY = "-A7JnfPHzOGX0TqMDeGateI9Zsx7bxjBFi7ukOxlEp4";

	private static final String URL_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
			+ APPID + "&secret=" + APPSECRET;

	private static final String URL_OPENID = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret="
			+ APPSECRET + "&js_code=RES_CODE&grant_type=authorization_code";

	/**
	 * 获取token
	 * 
	 * @return
	 */
	public static String getToken() {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.getForEntity(URL_TOKEN, String.class);
		return res.getBody();
	}

	/**
	 * 获取open id
	 * 
	 * @param code
	 * @return
	 */
	public static String getOpenid(String code) {

		String url = URL_OPENID.replace("RES_CODE", code);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
		return res.getBody();
	}

	/**
	 * 推送模板消息
	 * 
	 * @param token
	 * @param request
	 * @return
	 */
	public static String push(String token, String request) {
		String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + token;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(request, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//		JSONObject.parse(res.getBody());
		return res.getBody();
	}

	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] array = new String[] { "dech", timestamp, nonce };
		Arrays.sort(array);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}

		MessageDigest md = null;
		String encriptStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		byte[] digest = md.digest(sb.toString().getBytes());
		encriptStr = byteToStr(digest);

		return encriptStr != null ? encriptStr.equals(signature.toUpperCase()) : false;
	}

	private static String byteToStr(byte[] digest) {
		String tmp = "";
		for (int i = 0; i < digest.length; i++) {
			tmp += byteToHexStr(digest[i]);
		}
		return tmp;
	}

	private static String byteToHexStr(byte b) {
		char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = digit[(b >>> 4) & 0X0F];
		tempArr[1] = digit[b & 0X0F];
		return new String(tempArr);
	}
}
