package com.google.code.rapid.queue.server.impl;

import java.util.List;

import org.apache.thrift.TException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.server.ThriftContext;
import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService.Iface;
/**
 * MessageBrokerService 的安全代理，负责各种权限检查
 * 
 * @author badqiu
 *
 */
public class MessageBrokerServiceSecurityProxy implements Iface,
		InitializingBean {
	private Iface delegate;

	public MessageBrokerServiceSecurityProxy(Iface delegate) {
		super();
		this.delegate = delegate;
	}

	public void send(Message msg) throws MessageBrokerException, TException {
		checkHasExchangePermission(msg.getExchange());
		delegate.send(msg);
	}

	public void sendBatch(List<Message> msgList) throws MessageBrokerException,
			TException {
		for(Message msg : msgList) {
			checkHasExchangePermission(msg.getExchange());
		}
		delegate.sendBatch(msgList);
	}

	public Message receive(String queueName, int timeout)
			throws MessageBrokerException, TException {
		checkHasQueuePermission(queueName);
		return delegate.receive(queueName, timeout);
	}

	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws MessageBrokerException, TException {
		checkHasQueuePermission(queueName);
		return delegate.receiveBatch(queueName, timeout, batchSize);
	}

	public void login(String username, String password, String vhost)
			throws MessageBrokerException, TException {
		delegate.login(username, password, vhost);
	}

	public String ping() throws MessageBrokerException, TException {
		return delegate.ping();
	}

	private User getRequiredLoginUser() throws MessageBrokerException {
		User user = (User)ThriftContext.getServerContext().get(MessageBrokerServiceImpl.LOGIN_USER_KEY);
		if(user == null) {
			throw new MessageBrokerException("SecurityError","need login,please login before execute action!");
		}
		return user;
	}
	
	private void checkHasExchangePermission(String exchange) throws MessageBrokerException {
		Assert.hasLength(exchange,"exchange must be not null");
		User user = getRequiredLoginUser();
		if(!user.hasExchangePermission(exchange)) {
			throw new MessageBrokerException("SecurityError","security error,no exchange permission:"+exchange+" for user:"+user.getUsername());
		}
	}

	private void checkHasQueuePermission(String queueName) throws MessageBrokerException {
		Assert.hasLength(queueName,"queueName must be not null");
		User user = getRequiredLoginUser();
		if(!user.hasQueuePermission(queueName)) {
			throw new MessageBrokerException("SecurityError","security error,no queue permission:"+queueName+" for user:"+user.getUsername());
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(delegate,"delegate must be not null");
	}
	
}
