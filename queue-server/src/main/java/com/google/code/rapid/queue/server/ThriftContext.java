package com.google.code.rapid.queue.server;

import java.util.HashMap;
import java.util.Map;

public class ThriftContext {
	private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

	public static Map<String, Object> get() {
		return context.get();
	}

	public static void remove() {
		context.remove();
	}

	public static void set(Map<String, Object> value) {
		context.set(value);
	}

	public static void set(String key,Object value) {
		Map<String,Object> map = context.get();
		if(map == null) {
			map = new HashMap<String,Object>();
			context.set(map);
		}
		map.put(key, value);
	}
	
	public static Object get(String key) {
		Map<String,Object> map = context.get();
		return map.get(key);
	}
	
}
