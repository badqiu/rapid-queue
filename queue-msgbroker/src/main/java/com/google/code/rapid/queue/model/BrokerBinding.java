package com.google.code.rapid.queue.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.rapid.queue.util.RouterKeyUtil;

public class BrokerBinding {

	/**
	 * 交换机的router_key db_column: router_key
	 */
	private List<String> routerKeyList = new ArrayList<String>();

	private BrokerQueue queue;

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

	public void addRouterKey(String e) {
		String[] array = org.springframework.util.StringUtils.tokenizeToStringArray(e,"\n \t");
		for(String str : array) {
			routerKeyList.add(str);
		}
	}

	public boolean removeRouterKey(String o) {
		return routerKeyList.remove(o);
	}

	public void clearRouterKey() {
		routerKeyList.clear();
	}
	
	public List<String> getRouterKeyList() {
		return Collections.unmodifiableList(routerKeyList);
	}

	public boolean matchRouterKey(String routerKeyValue) {
		return RouterKeyUtil.matchRouterKey(routerKeyList, routerKeyValue);
	}

}
