package com.google.code.rapid.queue.server.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBroker.MessageBrokerManager;
import com.google.code.rapid.queue.MessageBrokerPool;
import com.google.code.rapid.queue.MessageBrokerPoolBuilder;
import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.service.ExchangeService;
import com.google.code.rapid.queue.metastore.service.QueueService;
import com.google.code.rapid.queue.model.BrokerExchange;
import com.google.code.rapid.queue.model.BrokerQueue;
import com.google.code.rapid.queue.server.ThriftContext;
import com.google.code.rapid.queue.thrift.api.MessageBrokerManagerService.Iface;

public class MessageBrokerManagerServiceImpl implements Iface {
	private MessageBrokerPool messageBrokerPool;
	private ExchangeService exchangeService;
	private QueueService queueService;
	private MessageBrokerPoolBuilder messageBrokerPoolBuilder;
	
	public void setMessageBrokerPool(MessageBrokerPool messageBrokerPool) {
		this.messageBrokerPool = messageBrokerPool;
	}

	public void queueAdd(BrokerQueue queue) {
		getMessageBrokerManager().queueAdd(queue);
	}

	public void queueDelete(String queueName) {
		getMessageBrokerManager().queueDelete(queueName);
	}

	public void queueClear(String queueName) {
		getMessageBrokerManager().queueClear(queueName);
	}

	public void queueUnbindAllExchange(String queueName) {
		getMessageBrokerManager().queueUnbindAllExchange(queueName);
	}

	public void queueBind(String exchangeName, String queueName,
			String routerKey) {
		getMessageBrokerManager().queueBind(exchangeName, queueName, routerKey);
	}

	public void queueUnbind(String exchangeName, String queueName,
			String routerKey) {
		getMessageBrokerManager().queueUnbind(exchangeName, queueName, routerKey);
	}

	public void queueUnbind(String exchangeName, String queueName) {
		getMessageBrokerManager().queueUnbind(exchangeName, queueName);
	}

	public void exchangeAdd(BrokerExchange exchange) {
		getMessageBrokerManager().exchangeAdd(exchange);
	}

	public void exchangeDelete(String exchangeName) {
		getMessageBrokerManager().exchangeDelete(exchangeName);
	}

	public Map<String, List<String>> getAllBinding() {
		return getMessageBrokerManager().getAllBinding();
	}

	public Set<String> getQueueNames() {
		return getMessageBrokerManager().getQueueNames();
	}

	public Set<String> getExchangeNames() {
		return getMessageBrokerManager().getExchangeNames();
	}

	public Map<String, Integer> listExchangeSize() {
		return getMessageBrokerManager().listExchangeSize();
	}

	public Map<String, Integer> listQueueSize() {
		return getMessageBrokerManager().listQueueSize();
	}

	@Override
	public void queueUnbindByRouterKey(String exchangeName, String queueName,String routerKey) throws TException {
		getMessageBrokerManager().queueUnbind(exchangeName, queueName, routerKey);
	}
	
	public void selectVhost(String vhost) {
		ThriftContext.getServerContext().put(MessageBrokerServiceImpl.VHOST_KEY, vhost);
	}

	@Override
	public void queueAdd(String queueName) throws TException {
		try {
			Queue queue = queueService.getById(queueName, getVhost());
			Assert.notNull(queue,"not found queue by:"+queueName);
			BrokerQueue bq = messageBrokerPoolBuilder.newBrokerQueue(queue);
			getMessageBrokerManager().queueAdd(bq);
		}catch(Exception e) {
			throw new TException("queueAdd,name="+queueName+" error,cause:"+e,e);
		}
	}

	@Override
	public void exchangeAdd(String exchangeName) throws TException {
		try {
			Exchange exchange = exchangeService.getById(exchangeName, getVhost());
			Assert.notNull(exchange,"not found exchange by:"+exchange);
			BrokerExchange bex = messageBrokerPoolBuilder.newBrokerExchange(exchange);
			getMessageBrokerManager().exchangeAdd(bex);
		}catch(Exception e) {
			throw new TException("exchangeAdd,name="+exchangeName+" error,cause:"+e,e);
		}
	}

	private static String getVhost() {
		return MessageBrokerServiceImpl.getVhost();
	}
	
	MessageBrokerManager getMessageBrokerManager() {
		MessageBroker mb = messageBrokerPool.getMessageBroker(getVhost());
		return mb.getManager();
	}

}
