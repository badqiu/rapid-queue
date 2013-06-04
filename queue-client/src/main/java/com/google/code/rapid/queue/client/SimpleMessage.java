package com.google.code.rapid.queue.client;

import java.nio.ByteBuffer;

import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageProperties;

public class SimpleMessage<T> extends Message implements Cloneable{

	private static final long serialVersionUID = 1L;
	
	private T payload;
	
	public SimpleMessage() {
		super();
	}

	public SimpleMessage(Message other) {
		super(other);
	}

	public SimpleMessage(String exchange, String routerKey, ByteBuffer body,
			MessageProperties messageProperties) {
		super(exchange, routerKey, body, messageProperties);
	}

	public SimpleMessage<T> setPayload(T payload) {
		this.payload = payload;
		return this;
	}
	
	public T getPayload() {
		return payload;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SimpleMessage<T> clone()  {
		try {
			return (SimpleMessage<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
