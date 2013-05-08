package com.google.code.rapid.queue.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class RouterKeyUtil {

	public static boolean matchRouterKey(String routerKey, String routerKeyValue) {
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
		for(String routerKey : routerKeyList) {
			return RouterKeyUtil.matchRouterKey(routerKey, routerKeyValue);
		}
		return false;
	}
	
}
