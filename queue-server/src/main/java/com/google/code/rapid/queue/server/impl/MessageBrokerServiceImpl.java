package com.google.code.rapid.queue.server.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerPool;
import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.service.UserService;
import com.google.code.rapid.queue.server.ThriftContext;
import com.google.code.rapid.queue.thrift.api.Constants;
import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService.Iface;

public class MessageBrokerServiceImpl implements Iface,InitializingBean{
	private Logger logger = LoggerFactory.getLogger(MessageBrokerServiceImpl.class);
	
	public static final String LOGIN_USER_KEY = "LOGIN_USER";
	public static final String VHOST_KEY = "VHOST";
	
	private UserService userService;
	private MessageBrokerPool messageBrokerPool;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setMessageBrokerPool(MessageBrokerPool messageBrokerPool) {
		this.messageBrokerPool = messageBrokerPool;
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
			String vhost = getVhost();
			
			MessageBroker mb = getRequiredMessageBroker(vhost);
			com.google.code.rapid.queue.model.Message receive = LoseMessageVhostStore.receive(vhost,queueName);
			if(receive == null) {
				receive = mb.receive(queueName, timeout);
			}
			
			if(receive != null) {
				ThriftContext.set("receiveMessage", receive);
			}
			
			return MessageConvertUtil.toThriftMessage(receive);
		}catch(RuntimeException e) {
			logger.error("error on receive(),queueName="+queueName+" timeout="+timeout,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}

	@Override
	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws TException {
		try {
			String vhost = getVhost();
			
			MessageBroker mb = getRequiredMessageBroker(vhost);
			
			List<com.google.code.rapid.queue.model.Message> receiveBatch = LoseMessageVhostStore.receiveBatch(vhost,queueName, batchSize);
			if(CollectionUtils.isEmpty(receiveBatch)) {
				receiveBatch = mb.receiveBatch(queueName, timeout,batchSize);
			}
			if(CollectionUtils.isNotEmpty(receiveBatch)) {
				ThriftContext.set("receiveBatchMessage", receiveBatch);
			}
			return MessageConvertUtil.toThriftMessageList(receiveBatch);
		}catch(RuntimeException e) {
			logger.error("error on receiveBatch(),queueName="+queueName+" timeout="+timeout+" batchSize="+batchSize,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}
	
	private String getVhost() {
		return (String)ThriftContext.getServerContext().get(VHOST_KEY);
	}
	
	private MessageBroker getRequiredMessageBroker(String vhost) {
		if(StringUtils.isBlank(vhost)) {
			throw new IllegalArgumentException("vhost must be not empty");
		}
		
		MessageBroker mb = messageBrokerPool.getMessageBroker(vhost);
		if(mb == null) {
			throw new IllegalArgumentException("not found messageBroker by vhost"+vhost);
		}
		return mb;
	}
	
	
	@Override
	public void login(String username, String password, String vhost)
			throws MessageBrokerException, TException {
		try {
			User user = userService.auth(username, password);
			ThriftContext.getServerContext().put(LOGIN_USER_KEY, user);
			ThriftContext.getServerContext().put(VHOST_KEY, vhost);
			logger.info("login_success by username:"+username+" vhost:"+vhost+" on clientIp:"+ThriftContext.getServerContext().get(ThriftContext.CLIENT_IP));
		}catch(RuntimeException e) {
			logger.error("error on login(),username="+username+" password="+password+" vhost="+vhost,e);
			throw new MessageBrokerException(e.getClass().getSimpleName(),e.getMessage());
		}
	}
	
	@Override
	public String ping() throws MessageBrokerException, TException {
		return Constants.PING_RESPONSE;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(messageBrokerPool,"messageBrokerPool must be not null");
		Assert.notNull(userService,"userService must be not null");
	}

}
