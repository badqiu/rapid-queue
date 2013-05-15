package com.google.code.rapid.queue.server.impl;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerBuilder;
import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerException;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService.Iface;

public class MessageBrokerServiceImpl implements Iface,InitializingBean{
	private Logger logger = LoggerFactory.getLogger(MessageBrokerServiceImpl.class);
	
	private MessageBrokerBuilder messageBrokerBuilder;
	private Map<String,MessageBroker> messageBrokerMap;
	
	public void setMessageBrokerBuilder(MessageBrokerBuilder messageBrokerBuilder) {
		this.messageBrokerBuilder = messageBrokerBuilder;
	}

	@Override
	public void send(Message msg) throws TException {
		try {
			MessageBroker mb = getRequiredMessageBroker(getVhost());
			mb.send(MessageConvertUtil.toMessageBrokerMessage(msg));
		}catch(RuntimeException e) {
			logger.error("error on send(),msg="+msg,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}

	@Override
	public void sendBatch(List<Message> msgList) throws TException {
		try {
			MessageBroker mb = getRequiredMessageBroker(getVhost());
			mb.sendBatch(MessageConvertUtil.totoMessageBrokerMessageList(msgList));
		}catch(RuntimeException e) {
			logger.error("error on sendBatch(),msgList="+msgList,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}

	@Override
	public Message receive(String queueName, int timeout) throws TException {
		try {
			MessageBroker mb = getRequiredMessageBroker(getVhost());
			return MessageConvertUtil.toThriftMessage(mb.receive(queueName, timeout));
		}catch(RuntimeException e) {
			logger.error("error on receive(),queueName="+queueName+" timeout="+timeout,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}

	@Override
	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws TException {
		try {
			MessageBroker mb = getRequiredMessageBroker(getVhost());
			return MessageConvertUtil.toThriftMessageList(mb.receiveBatch(queueName, timeout,batchSize));
		}catch(RuntimeException e) {
			logger.error("error on receiveBatch(),queueName="+queueName+" timeout="+timeout+" batchSize="+batchSize,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}
	
	private String getVhost() {
		return "vhost";
	}
	
	private MessageBroker getRequiredMessageBroker(String vhost) {
		MessageBroker mb = messageBrokerMap.get(vhost);
		if(mb == null) {
			throw new IllegalArgumentException("not found messageBroker by vhost"+vhost);
		}
		return mb;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(messageBrokerBuilder,"messageBrokerBuilder must be not null");
		messageBrokerMap = messageBrokerBuilder.build();
	}

}
