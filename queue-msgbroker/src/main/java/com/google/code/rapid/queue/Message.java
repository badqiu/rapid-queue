package com.google.code.rapid.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


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
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			return baos.toByteArray();
		}catch(IOException e) {
			throw new RuntimeException("toBytes() error,message:"+this,e);
		}
	}
	
	public static byte[] toBytes(Message msg) {
		if(msg == null) return null;
		return msg.toBytes();
	}
	
	public static Message fromBytes(byte[] bytes) {
		if(bytes == null) return null;
		
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (Message)oos.readObject();
		}catch(IOException e) {
			throw new RuntimeException("fromBytes() error",e);
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("fromBytes() error",e);
		}
	}
}
