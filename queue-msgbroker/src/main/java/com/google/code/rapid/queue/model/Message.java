package com.google.code.rapid.queue.model;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;

import com.google.code.rapid.queue.util.KryoUtil;


public class Message implements Serializable{

	private static final long serialVersionUID = 7156950587289464765L;
	
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
	
	public byte[] toBytes() {
		return KryoUtil.toBytes(this, body.length + 500);
	}
	
	public static byte[] toBytes(Message msg) {
		if(msg == null) return null;
		return msg.toBytes();
	}
	
	public static Message fromBytes(byte[] bytes) {
		return KryoUtil.fromBytes(bytes, Message.class);
	}

	@Override
	public String toString() {
		return "Message [exchange=" + exchange + ", routerKey=" + routerKey
				+ ", body.length=" + (body == null ? "null" : body.length) + ", messageProperties="
				+ messageProperties + "]";
	}
	
}
