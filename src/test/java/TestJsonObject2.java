import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dech.domain.TemplateMessage;

public class TestJsonObject2 {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void main(String[] args) {
		TemplateMessage tm = new TemplateMessage();
		tm.setAccess_token("123");
		tm.setTouser("3333");
		tm.setTemplate_id("-A7JnfPHzOGX0TqMDeGateI9Zsx7bxjBFi7ukOxlEp4");
		tm.setForm_id("456");
		tm.setPage("");
		tm.setEmphasis_keyword("");
		tm.setData("");
		
		Map<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> m1 = new HashMap<String, String>();
		HashMap<String, String> m2 = new HashMap<String, String>();
		HashMap<String, String> m3 = new HashMap<String, String>();
		m1.put("value", "Hello World");
		map.put("keyword1", m1);
		
		m2.put("value", "工作、生活、学习");
		map.put("keyword2", m2);

		
		String date = sdf.format(new Date());
		m3.put("value", date);
		map.put("keyword3", m3);
		
		tm.setData(map);
		JSONObject obj = (JSONObject) JSONObject.parse("{\"access_token\":\"123\",\"touser\":\"3333\",\"data\":{\"a\":\"b\"}}");
		System.out.println(obj.getString("touser"));
		System.out.println(obj.getJSONObject("data").getString("a"));
		Map<String, String> s = (Map)JSONObject.toJSON(tm);
		for(Map.Entry entry : s.entrySet()) {
			System.out.print("Key = " + entry.getKey() + ",value=" + entry.getValue());
		}
	}

}
