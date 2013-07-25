package com.google.code.rapid.queue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public class MessageBrokerPool {
	
	private Map<String,MessageBroker> map = new HashMap<String,MessageBroker>();
	
	public MessageBrokerPool(Map<String, MessageBroker> messageBrokerMap) {
		super();
		this.map = messageBrokerMap;
	}
	
	public MessageBroker getMessageBroker(String vhost) {
		return map.get(vhost);
	}

	public MessageBroker getRequiredMessageBroker(String vhost) {
		MessageBroker mb = getMessageBroker(vhost);
		if(mb == null) {
			throw new IllegalArgumentException("not found vhost:"+vhost);
		}
		return mb;
	}
	
	public void putMessageBroker(MessageBroker mb) {
		Assert.hasText(mb.getVhostName());
		map.put(mb.getVhostName(), mb);
	}

	public MessageBroker removeMessageBroker(String vhostName) {
		Assert.hasText(vhostName);
		MessageBroker mb = map.remove(vhostName);
		mb.destroy();
		return mb;
	}
	
	public Collection<MessageBroker> getAll() {
		return map.values();
	}
	
	
	
	
}
