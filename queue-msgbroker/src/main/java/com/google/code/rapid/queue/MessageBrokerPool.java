package com.google.code.rapid.queue;

import java.util.HashMap;
import java.util.Map;

public class MessageBrokerPool {
	
	private Map<String,MessageBroker> messageBrokerMap = new HashMap<String,MessageBroker>();
	
	public MessageBroker getMessageBroker(String vhost) {
		MessageBroker mb = messageBrokerMap.get(vhost);
		if(mb == null) {
			throw new IllegalArgumentException("not found vhost:"+vhost);
		}
		return mb;
	}
	
}
