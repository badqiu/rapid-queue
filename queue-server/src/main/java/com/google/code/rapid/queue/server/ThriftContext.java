package com.google.code.rapid.queue.server;

import java.util.HashMap;
import java.util.Map;
/**
 * Thrift上下文
 * 
 * @author badqiu
 *
 */
public class ThriftContext {
	private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();
	public static final String CLIENT_IP = "clientIp";
	public static final String SERVER_CONTEXT = "serverContext";
	
	public static Map<String, Object> get() {
		Map<String, Object> map = context.get();
		if(map == null) {
			map = new HashMap<String,Object>();
			context.set(map);
		}
		return map;
	}

	public static void remove() {
		context.remove();
	}

	public static void set(Map<String, Object> value) {
		context.set(value);
	}

	public static void set(String key,Object value) {
		get().put(key, value);
	}
	
	public static Object get(String key) {
		return get().get(key);
	}
	
	public static void setServerContext(Map<String,Object> map) {
		set(SERVER_CONTEXT,map);
	}
	
	public static Map<String,Object> getServerContext() {
		return (Map<String,Object>)get(SERVER_CONTEXT);
	}
}
