package com.google.code.rapid.queue;


public class Message {

	private String exchange; // exchange
	private String routerKey; // 路由key
	private byte[] body; // 消息体
	
	private MessageProperties messageProperties; // 消息附加属性
	
	public Message() {
	}

	public Message(byte[] body) {
		super();
		this.body = body;
	}

	public Message(String exchange, String routerKey, byte[] body) {
		super();
		this.exchange = exchange;
		this.routerKey = routerKey;
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

	public MessageProperties getMessageProperties() {
		return messageProperties;
	}

	public void setMessageProperties(MessageProperties messageProperties) {
		this.messageProperties = messageProperties;
	}
	
}
