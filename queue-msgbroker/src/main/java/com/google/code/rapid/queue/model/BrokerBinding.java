package com.google.code.rapid.queue.model;

import java.util.ArrayList;
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

	public boolean addRouterKey(String e) {
		return routerKeyList.add(e);
	}

	public boolean removeRouterKey(String o) {
		return routerKeyList.remove(o);
	}

	public void clearRouterKey() {
		routerKeyList.clear();
	}

	public boolean matchRouterKey(String routerKeyValue) {
		return RouterKeyUtil.matchRouterKey(routerKeyList, routerKeyValue);
	}

}
