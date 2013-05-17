package com.google.code.rapid.queue.client;

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
	
	@Override
	public String toString() {
		return "errorCode:"+errorCode+" message:"+getMessage();
	}
	
}
