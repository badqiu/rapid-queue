package com.google.code.rapid.queue.client;

import com.google.code.rapid.queue.thrift.api.MessageBrokerException;

public class MessageBrokerRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String errorCode;
	public MessageBrokerRuntimeException() {
		super();
	}

	public MessageBrokerRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageBrokerRuntimeException(String message) {
		super(message);
	}

	public MessageBrokerRuntimeException(Throwable cause) {
		super(cause);
	}
	
	public MessageBrokerRuntimeException(MessageBrokerException cause) {
		super(cause.getMessage());
		this.errorCode = cause.getErrorCode();
	}
	
	@Override
	public String toString() {
		return "errorCode:"+errorCode+" message:"+getMessage();
	}
	
}
