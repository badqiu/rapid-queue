package com.google.code.rapid.queue;

import java.util.Map;
import java.util.UUID;

public class Message {
	private String messageId = UUID.randomUUID().toString().replace("-", ""); // 消息ID
	private Integer priority = 0; // 优先级
	private String contentType = "byte"; // json,xml,text,
	private String encoding = "UTF-8"; // UTF-8
	private long expiration = -1; // 过期时间，单位毫稍

	private Map<String, Object> messageProperties = null; // 消息附加属性
	private boolean persistent = true;

	private String sourceIp;	//发送消息的来源IP
	private String sourceApp;   //发送消息的来源App
	private String sourceUser;  //发送消息的来源User
	
	private String exchange; // exchange
	private String routerKey; // 路由key
	private byte[] body; // 消息体
	
	public Message() {
	}
	
	public Message(byte[] body) {
		super();
		this.body = body;
	}

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
