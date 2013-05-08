package com.google.code.rapid.queue.client;

import java.util.Map;
import java.util.UUID;

public class Message {
	private String messageId = UUID.randomUUID().toString().replace("-", ""); // 消息ID
	private Integer priority = 0; // 优先级
	private String contentType = "byte"; // json,xml,text,
	private String encoding = "UTF-8"; // UTF-8
	private long expiration = -1; // 过期时间，单位毫稍

	private Map<String, Object> messageProperties = null; // 消息附加属性
	private Map<String, Object> headers = null; // 消息头
	private boolean persistent = true;
	
	private String routerKey; // 路由key
	private String exchange; // exchange
	private byte[] body; // 消息体
	
	public String getRouterKey() {
		return routerKey;
	}

	public void setRouterKey(String routerKey) {
		this.routerKey = routerKey;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public Map<String, Object> getMessageProperties() {
		return messageProperties;
	}

	public void setMessageProperties(Map<String, Object> messageProperties) {
		this.messageProperties = messageProperties;
	}

}
