/**
 * JacksonUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.ganjp.glib.core.entity.Response;

public class JacksonUtil {
	private static JsonGenerator jsonGenerator = null;
	private static ObjectMapper objectMapper = null;
	private static Response response = null;

	public static void writeEntity2Json() throws IOException {
		jsonGenerator.writeObject(response);
		objectMapper.writeValue(System.out, response);
	}

	public static void writeList2Json() throws IOException {
		List<Response> responseList = new ArrayList<Response>();
		Response response = new Response();
		response.setStatus(Response.STATUS_SUCCESS);
		responseList.add(response);
		objectMapper.writeValue(System.out, responseList);
	}

	public static void writeMap2Json() {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", "1");
			map.put("uname", "ganjp");
			jsonGenerator.writeObject(map);
			objectMapper.writeValue(System.out, map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readJson2Entity() {
		String json = "{\"status\":\"Success\"}";
		try {
			Response acc = objectMapper.readValue(json, Response.class);
			System.out.println(acc.getStatus());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readJson2List() {
		String json = "[{\"status\":\"Success\"},{\"status\":\"Fail\"}"; 
		try {
			List<LinkedHashMap<String, Object>> list = objectMapper.readValue(json, List.class);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				Set<String> set = map.keySet();
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String key = it.next();
					System.out.println(key + ":" + map.get(key));
				}
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readJson2Array() {
		String json = "[{\"status\":\"Success\"},{\"status\":\"Fail\"}"; 
		try {
			Response[] arr = objectMapper.readValue(json, Response[].class);
			System.out.println(arr.length);
			for (int i = 0; i < arr.length; i++) {
				System.out.println(arr[i]);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readJson2Map() {
		String json = "{\"success\":true,\"A\":{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"},"
				+ "\"B\":{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}}";
		try {
			Map<String, Map<String, Object>> maps = objectMapper.readValue(json, Map.class);
			System.out.println(maps.size());
			Set<String> key = maps.keySet();
			Iterator<String> iter = key.iterator();
			while (iter.hasNext()) {
				String field = iter.next();
				System.out.println(field + ":" + maps.get(field));
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		objectMapper = new ObjectMapper();
		try {
			// jsonGenerator =
			// objectMapper.getJsonFactory().createJsonGenerator(System.out,
			// JsonEncoding.UTF8);
			// writeEntity2Json();
			// writeMap2Json();
			// writeList2Json();
			// writeOthersJSON();
			// readJson2Entity();
			// readJson2List();
			// readJson2Array();
			readJson2Map();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
