package com.daniel.test.util;

import io.restassured.path.json.JsonPath;

import java.util.Map;

public class TestUtil {
	public static int countLinks(String linkText) {
		String word = "rel";
		String temp[] = linkText.split(" ");
		int count = 0;
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].contains(word))
				count++;
		}
		return count;
	}

	public static void getPageMap(Map<String, Integer> pageMap, String content) {
		String pageText;
		String[] page;
		pageText = JsonPath.from(content).get("page").toString();

		page = pageText.replace("{", "").replace("}", "").split(",");
		for (String e : page) {
			String[] tmp = e.split("=");
			pageMap.put(tmp[0].trim(), Integer.valueOf(tmp[1]));
		}
	}
}
