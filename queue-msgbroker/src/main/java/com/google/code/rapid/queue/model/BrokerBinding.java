package com.google.code.rapid.queue.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.code.rapid.queue.util.RouterKeyUtil;

public class BrokerBinding {

	/**
	 * 交换机的router_key db_column: router_key
	 */
	private Set<String> routerKeyList = new HashSet<String>();

	private BrokerQueue queue;
	
	private String routerKey;

	public BrokerBinding(BrokerQueue queue) {
		super();
		this.queue = queue;
	}

	public BrokerQueue getQueue() {
		return queue;
	}

	public void setQueue(BrokerQueue queue) {
		this.queue = queue;
	}

	public synchronized boolean updateRouterKey(String e) {
		if(!StringUtils.equals(routerKey, e)) {
			String[] array = org.springframework.util.StringUtils.tokenizeToStringArray(e,"\n \t");
			Set<String> tempRouterKeyList = new HashSet<String>();
			for(String str : array) {
				tempRouterKeyList.add(str);
			}
			routerKeyList = tempRouterKeyList;
			this.routerKey = e;
			return true;
		}
		return false;
	}
	
//	public void addRouterKey(String e) {
//		String[] array = org.springframework.util.StringUtils.tokenizeToStringArray(e,"\n \t");
//		for(String str : array) {
//			routerKeyList.add(str);
//		}
//	}

	public boolean removeRouterKey(String o) {
		return routerKeyList.remove(o);
	}

	public void clearRouterKey() {
		routerKeyList.clear();
	}
	
	public Set<String> getRouterKeyList() {
		return Collections.unmodifiableSet(routerKeyList);
	}

	public boolean matchRouterKey(String routerKeyValue) {
		return RouterKeyUtil.matchRouterKey(routerKeyList, routerKeyValue);
	}

}
