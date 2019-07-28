package com.dech.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dech.domain.Post;

public class HupuCrawler {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Pattern pattern = Pattern.compile("[^0-9]");
	private static final String url = "https://bbs.hupu.com/bxj-";

	public static List<Map<String, String>> retrieve(int num) throws IOException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		if (num < 1 || num > 100) {
			return list;
		}

		Elements spans = null;
		Elements divs = null;
		boolean isFirst = true;
		Element div = null;
		Elements tags = null;

		Map<String, String> map = null;

		for (int page = 1; page <= num; page++) {

			Document doc = Jsoup.connect(url + page).get();
			Elements elements = doc.getElementsByTag("li");

			for (Element e : elements) {
				spans = e.getElementsByTag("span");
				divs = e.getElementsByTag("div");

				if (spans.size() < 2 || divs.size() < 3) {
					continue;
				}

				map = new HashMap<String, String>();

				// get replies and page views
				for (Element span : spans) {
					if (span.hasClass("ansour box")) {
						map.put("replies", span.text().split("/")[0].trim());
						map.put("views", span.text().split("/")[1].trim());
						break;
					}
				}

				for (int i = 0; i < divs.size(); i++) {
					div = divs.get(i);
					tags = div.getElementsByTag("a");

					// get the last reply times and link
					if (div.hasClass("endreply box")) {
						map.put("lastUrl", tags.first().attr("href"));
						map.put("lastTime", tags.first().text());
						continue;
					}

					isFirst = true;
					for (Element tag : tags) {

						// get title and link
						if (isFirst) {
							isFirst = false;
							if (i == 0) {
								map.put("url", tags.attr("href"));
								map.put("title", tag.text());
							} else {
								map.put("authorUrl", tags.attr("href"));
								map.put("author", tag.text());
							}
						}

						// get lights
						if (tag.hasAttr("title")) {
							map.put("lights", tag.attr("title"));
						}

						// get create time
						if (tag.hasAttr("style")) {
							map.put("createTime", tag.text());
						}
					}
				}
				list.add(map);
			}
		}
		return list;
	}

	public static void insertPost(Post post, Map<String, String> m) {
		post.setAuthor(m.get("author"));
		post.setAuthorUrl(m.get("authorUrl").trim());
		post.setCategory("JR");
		post.setDegree(0);
		post.setIsBrowsed("NO");

		post.setPost_name(m.get("title"));
		post.setUrl(m.get("url").trim());

		post.setUpdate_count(1);
		post.setUpdate_time(new Date());
		copy(post, m);
	}

	public static void updatePost(Post post, Map<String, String> m) {
		post.setUpdate_count(post.getUpdate_count() + 1);
		post.setUpdate_time(new Date());
		copy(post, m);

	}

	private static void copy(Post post, Map<String, String> m) {
		post.setClicks(Integer.valueOf(m.get("views")));
		post.setReplies(Integer.valueOf(m.get("replies").trim()));
		post.setLastUrl(m.get("lastUrl").trim());

		if (m.get("lights") != null) {
			Matcher matcher = pattern.matcher(m.get("lights").trim());
			post.setLights(Integer.valueOf(matcher.replaceAll("")));
		} else {
			post.setLights(0);
		}

		try {
			if (m.get("lastTime").trim().length() < 10) {
				post.setLastReply(sdf2.parse(m.get("createTime").trim() + " " + m.get("lastTime").trim() + ":00"));
			} else {
				post.setLastReply(sdf.parse(m.get("lastTime").trim()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
