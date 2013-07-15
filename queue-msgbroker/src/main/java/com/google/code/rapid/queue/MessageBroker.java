package com.google.code.rapid.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rapid.queue.model.BrokerBinding;
import com.google.code.rapid.queue.model.BrokerExchange;
import com.google.code.rapid.queue.model.BrokerQueue;
import com.google.code.rapid.queue.model.Message;
import com.google.code.rapid.queue.util.Profiler;

public class MessageBroker {
	private static final Logger logger = LoggerFactory.getLogger(MessageBroker.class);
	
	// Map<exchangeName,TopicExchange>
	private Map<String,BrokerExchange> exchangeMap = new HashMap<String,BrokerExchange>();
	
	// Map<queueName,TopicExchange>
	private Map<String,BrokerQueue> queueMap = new HashMap<String,BrokerQueue>();
	
	private MessageBrokerManager manager = new MessageBrokerManager();
	
	private String vhostName;
	
	public String getVhostName() {
		return vhostName;
	}

	public void setVhostName(String vhostName) {
		this.vhostName = vhostName;
	}

	/**
	 * 发送消息
	 * @param msg
	 */
	public void send(Message msg) {
		if(StringUtils.isEmpty(msg.getExchange())) throw new IllegalArgumentException("'msg.exchange' must be not empty");
		if(StringUtils.isEmpty(msg.getRouterKey())) throw new IllegalArgumentException("'msg.routerKey' must be not empty");
		if(msg.getBody() == null) throw new IllegalArgumentException("'msg.body' must be not null");
		if(msg.getExchange().length() > 50) {
			throw new IllegalArgumentException("'msg.exchange' length must <= 50");
		}
		if(msg.getRouterKey().length()> 50) {
			throw new IllegalArgumentException("'msg.routerKey' length must <= 50");
		}
		BrokerExchange exchange = lookupExchange(msg.getExchange());
		try {
			exchange.offer(msg);
		} catch (InterruptedException e) {
			throw new RuntimeException("InterruptedException on msg:"+msg);
		}
	}
	
	/**
	 * 批量发送消息
	 * @param msg
	 */	
	public void sendBatch(List<Message> msgList) {
		for(Message msg : msgList) {
			send(msg);
		}
	}
	
	/**
	 * 接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 */	
	public Message receive(String queueName,int timeout) {
		BrokerQueue queue = lookupQueue(queueName);
		return receive(timeout, queue);
	}

	private Message receive(int timeout, BrokerQueue queue) {
		try {
			byte[] messageBody = queue.poll(timeout,TimeUnit.MILLISECONDS);
			if(messageBody == null) {
				return null;
			}
			Message msg = new Message(messageBody);
			msg.setQueueName(queue.getQueueName());
			return msg;
		} catch (InterruptedException e) {
			throw new RuntimeException("InterruptedException on receive msg:"+e,e);
		}
	}

	/**
	 * 批量接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 * @param batchSize 批量接收的大小
	 */		
	public List<Message> receiveBatch(String queueName,int timeout,int batchSize) {
		if(timeout <= 0) {
			throw new IllegalArgumentException("timeout > 0 must be true");
		}
		if(batchSize <= 0) {
			throw new IllegalArgumentException("batchSize > 0 must be true");
		}
		
		BrokerQueue queue = lookupQueue(queueName);
		List<Message> result = new ArrayList<Message>(batchSize);
		
		int totalCostTime = 0;
		int nextWaittime = timeout;
		for(int i = 0; i < batchSize; i++) {
			long start = System.currentTimeMillis();
			
			Message msg = receive(nextWaittime,queue);
			if(msg != null) {
				result.add(msg);
			}
			
			totalCostTime += System.currentTimeMillis() - start;
			nextWaittime = timeout - totalCostTime;
			
			if(totalCostTime >= timeout || nextWaittime <= 0) {
				break;
			}
		}
		
		return result;
	}
	
	public MessageBrokerManager getManager() {
		return manager;
	}

	private BrokerExchange lookupExchange(String exchangeName) {
		BrokerExchange exchange = exchangeMap.get(exchangeName);
		if(exchange == null) {
			throw new IllegalArgumentException("not found exchange by name:"+exchangeName);
		}
		return exchange;
	}

	private BrokerQueue lookupQueue(String queueName) {
		BrokerQueue queue = queueMap.get(queueName);
		if(queue == null) {
			throw new IllegalArgumentException("not found queue by name:"+queueName);
		}
		return queue;
	}
	
	public class MessageBrokerManager {
		public void queueAdd(BrokerQueue queue) {
			if(StringUtils.isBlank(queue.getQueueName())) throw new IllegalArgumentException("queueName must be not blank");
			if(queueMap.containsKey(queue.getQueueName())) throw new IllegalArgumentException("already contain queue:"+queue.getQueueName());
			
			logger.info("queueAdd(),queue:"+queue);
			queueMap.put(queue.getQueueName(),queue);
		}
		
		public void queueDelete(String queueName) {
			logger.info("queueDelete() queueName:"+queueName);
			queueUnbindAllExchange(queueName);
			BrokerQueue queue = queueMap.remove(queueName);
			queue.delete();
		}

		public void queueClear(String queueName) {
			logger.info("queueClear() queueName:"+queueName);
			queueUnbindAllExchange(queueName);
			BrokerQueue queue = queueMap.remove(queueName);
			queue.clear();
		}
		
		public void queueUnbindAllExchange(String queueName) {
			for(String exchangeName : exchangeMap.keySet()) {
				BrokerExchange exchange = lookupExchange(exchangeName);
				exchange.unbindQueue(queueName);
			}
		}
		
		public void queueBind(String exchangeName,String queueName,String routerKey) {
			logger.info("queueBind(),exchangeName:"+exchangeName+" queueName:"+queueName+" routerKey:"+routerKey);
			BrokerExchange exchange = lookupExchange(exchangeName);
			BrokerQueue queue = lookupQueue(queueName);
			exchange.bindQueue(queue,routerKey);
		}
	
		public void queueUnbind(String exchangeName,String queueName,String routerKey) {
			logger.info("queueUnbind(),exchangeName:"+exchangeName+" queueName:"+queueName+" routerKey:"+routerKey);
			BrokerExchange exchange = lookupExchange(exchangeName);
			exchange.unbindQueue(queueName,routerKey);
		}

		public void queueUnbind(String exchangeName,String queueName) {
			logger.info("queueUnbind(),exchangeName:"+exchangeName+" queueName:"+queueName);
			BrokerExchange exchange = lookupExchange(exchangeName);
			exchange.unbindQueue(queueName);
		}
		
		public void exchangeAdd(BrokerExchange exchange) {
			if(StringUtils.isBlank(exchange.getExchangeName())) throw new IllegalArgumentException("exchangeName must be not blank");
			if(exchangeMap.containsKey(exchange.getExchangeName())) throw new IllegalArgumentException("already contain exchange:"+exchange.getExchangeName());
			
			logger.info("exchangeAdd(),exchange:"+exchange);
			exchangeMap.put(exchange.getExchangeName(),exchange);
		}
		
		public void exchangeDelete(String exchangeName) {
			logger.info("exchangeDelete(),exchangeName:"+exchangeName);
			BrokerExchange exchange = exchangeMap.remove(exchangeName);
			if(exchange != null) {
				try {
					exchange.destroy();
				} catch (InterruptedException e) {
					logger.error("exchangeDelete() error,exchangeName:"+exchangeName,e);
				}
			}
		}
		
		/**
		 * 返回所有binding
		 * 
		 * @return Map<exchangeName,List<queueName>>
		 */
		public Map<String,List<String>> getAllBinding() {
			Map<String,List<String>> map = new HashMap<String,List<String>>();
			for(BrokerExchange ex : exchangeMap.values()) {
				String exchangeName = ex.getExchangeName();
				List<String> queueNames = new ArrayList<String>();
				for(BrokerBinding binding : ex.getBindQueueList()) {
					queueNames.add(binding.getQueue().getQueueName());
				}
				
				map.put(exchangeName, queueNames);
			}
			return map;
		}
		
		public Set<String> getQueueNames() {
			return new HashSet<String>(queueMap.keySet());
		}

		public Set<String> getExchangeNames() {
			return new HashSet<String>(exchangeMap.keySet());
		}
		
		public Map<String,Integer> listExchangeSize() {
			Map<String,Integer> map = new HashMap<String,Integer>();
			for(BrokerExchange ex : exchangeMap.values()) {
				int size = ex.getExchangeQueue().size();
				map.put(ex.getExchangeName(), size);
			}
			return map;
		}

		public Map<String,Integer> listQueueSize() {
			Map<String,Integer> map = new HashMap<String,Integer>();
			for(BrokerQueue queue : queueMap.values()) {
				int size = queue.size();
				map.put(queue.getQueueName(), size);
			}
			return map;
		}
	}
}
