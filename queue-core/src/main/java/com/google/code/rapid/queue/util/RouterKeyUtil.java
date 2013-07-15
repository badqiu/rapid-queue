package com.google.code.rapid.queue.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterKeyUtil {
	private static Logger logger = LoggerFactory.getLogger(RouterKeyUtil.class);
	private static int maxCacheCount = 1000000;
	public static Map<String, Boolean> cache = new LRULinkedHashMapMap(1000, maxCacheCount);
//	private static Map<String, Boolean> cache = new HashMap<String,Boolean>();
	
	public static boolean matchRouterKey(String routerKey, String routerKeyValue) {
		String cacheKey = routerKey+"\01"+routerKeyValue;
		Boolean value = cache.get(cacheKey);
		if(value == null) {
			value = matchRouterKey0(routerKey,routerKeyValue);
			synchronized (cache) {
				cache.put(cacheKey, value);
//				if(cache.size() > maxCacheCount) {
//					logger.warn("RouterKeyUtil cache > "+maxCacheCount+" exceed,clear cache");
//					cache.clear();
//				}
			}
		}
		return value;
	}
	
	public static boolean matchRouterKey0(String routerKey, String routerKeyValue) {
		if("*".equals(routerKey)) {
			return true;
		}
		if(routerKey.equals(routerKeyValue)) {
			return true;
		}
		
		String regex = StringUtils.replace(StringUtils.replace(routerKey,".","\\."),"*", ".*");
		return routerKeyValue.matches(regex);
	}
	
	public static boolean matchRouterKey(List<String> routerKeyList,String routerKeyValue) {
		if(routerKeyList == null || routerKeyList.isEmpty()) {
			return false;
		}
		
		for(String routerKey : routerKeyList) {
			 if(RouterKeyUtil.matchRouterKey(routerKey, routerKeyValue)) {
				 return true;
			 }
		}
		return false;
	}
	
}
