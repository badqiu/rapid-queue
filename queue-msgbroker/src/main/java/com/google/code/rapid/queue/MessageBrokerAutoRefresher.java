package com.google.code.rapid.queue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import cn.org.rapid_framework.util.CollectionHelper;

import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.model.BrokerExchange;
import com.google.code.rapid.queue.model.BrokerQueue;

/**
 * 自动更新 MessageBroker状态,实现MessageBroker中 queue,exchange,vhost,binding的增删改自动更新
 * 
 * @author badqiu
 *
 */
public class MessageBrokerAutoRefresher extends MessageBrokerPoolBuilder implements InitializingBean{
	private Logger logger = LoggerFactory.getLogger(MessageBrokerAutoRefresher.class);
	
	private MessageBrokerPool messageBrokerPool;
	
	public void setMessageBrokerPool(MessageBrokerPool messageBrokerPool) {
		this.messageBrokerPool = messageBrokerPool;
	}
	
	public void execute() {
		refreshMessageBroker(messageBrokerPool);
	}

	public void refreshMessageBroker(MessageBrokerPool pool) {
		List<Vhost> list = vhostService.findAll();
		for(Vhost vhost : list) {
			MessageBroker mb = pool.getMessageBroker(vhost.getVhostName());
			if(mb == null) {
				mb = buildMessageBroker(vhost);
				pool.putMessageBroker(mb);
			}
			refreshMessageBroker(mb);
		}
	}
	
	private void refreshMessageBroker(MessageBroker mb) {
		refreshExchanges(mb);
		refreshQueues(mb);
		refreshBindings(mb);
	}

	
	private void refreshBindings(final MessageBroker mb) {
		List<Binding> list = bindingService.findByVhostName(mb.getVhostName());
		List<String> names = new ArrayList<String>(list.size());
		for(Binding binding : list) {
			names.add(new QueueExchangeBinding(binding.getExchangeName(),binding.getQueueName()).toString());
		}
		
		List<String> holdedNames = new ArrayList<String>();
		Map<String,List<String>> bindingMap = mb.getManager().getAllBinding();
		for(Map.Entry<String, List<String>> entry : bindingMap.entrySet()) {
			String exchangeName = entry.getKey();
			for(String queueName : entry.getValue()) {
				holdedNames.add(new QueueExchangeBinding(exchangeName,queueName).toString());
			}
		}
		
		refreshBy(new AutoRefersher() {
			@Override
			public void update(String name) {
			}
			
			@Override
			public void delete(String name) {
				QueueExchangeBinding b = QueueExchangeBinding.fromString(name);
				mb.getManager().queueUnbind(b.exchangeName, b.queueName);
			}
			
			@Override
			public void create(String name) throws Exception {
				QueueExchangeBinding b = QueueExchangeBinding.fromString(name);
				Binding binding = bindingService.getById(b.queueName, b.exchangeName, mb.getVhostName());
				mb.getManager().queueBind(binding.getExchangeName(), binding.getQueueName(), binding.getRouterKey());
			}
		},new HashSet<String>(names),new HashSet<String>(holdedNames));
	}
	
	private static class QueueExchangeBinding {
		private static String SEPERATOR = "\1\1\1\1";
		String exchangeName;
		String queueName;
		
		public QueueExchangeBinding(String exchangeName, String queueName) {
			super();
			this.exchangeName = exchangeName;
			this.queueName = queueName;
		}

		public String toString() {
			return exchangeName + SEPERATOR + queueName;
		}
		
		public static QueueExchangeBinding fromString(String str) {
			if(StringUtils.isBlank(str)) {
				return null;
			}
			String[] array = StringUtils.split(str,SEPERATOR);
			if(array.length != 2) {
				throw new IllegalArgumentException("error QueueExchangeBinding fromString format,str:"+str);
			}
			return new QueueExchangeBinding(array[0],array[1]);
		}
	}

	private void refreshQueues(final MessageBroker mb) {
		List<Queue> list = queueService.findByVhostName(mb.getVhostName());
		List<String> names = CollectionHelper.selectProperty(list, "queueName");
		Set<String> holdedNames = mb.getManager().getQueueNames();
		refreshBy(new AutoRefersher() {
			@Override
			public void update(String name) {
			}
			
			@Override
			public void delete(String name) {
				mb.getManager().queueDelete(name);
			}
			
			@Override
			public void create(String name) throws Exception {
				Queue queue = queueService.getById(name, mb.getVhostName());
				BrokerQueue tq = newBrokerQueue(queue);
				mb.getManager().queueAdd(tq);
			}
		},new HashSet<String>(names),new HashSet(holdedNames));
	}
	
	private void refreshExchanges(final MessageBroker mb) {
		List<Exchange> list = exchangeService.findByVhostName(mb.getVhostName());
		List<String> names = CollectionHelper.selectProperty(list, "exchangeName");
		Set<String> holdedNames = mb.getManager().getExchangeNames();
		refreshBy(new AutoRefersher() {
			@Override
			public void update(String name) {
			}
			
			@Override
			public void delete(String name) {
				mb.getManager().exchangeDelete(name);
			}
			
			@Override
			public void create(String name) throws Exception {
				Exchange ex = exchangeService.getById(name, mb.getVhostName());
				BrokerExchange te = newTopicExchange(ex);
				mb.getManager().exchangeAdd(te);
			}
		},new HashSet(names),new HashSet(holdedNames));
	}
	
	private void refreshBy(AutoRefersher refersher,Set<String> names,Set<String> holdNames) {
		for(String name : names) {
			try {
				if(holdNames.contains(name)) {
					refersher.update(name);
				}else {
					refersher.create(name);
				}
			}catch(Exception e) {
				logger.error("error on refersher,name:"+name,e);
			}
		}
		
		for(String holdedName : holdNames) {
			if(!names.contains(holdedName)) {
				try {
					refersher.delete(holdedName);
				}catch(Exception e) {
					logger.error("error on delete,name:"+holdedName,e);
				}
			}
		}
	}
	
	public interface AutoRefersher {
		void create(String name) throws Exception;
		void update(String name) throws Exception;
		void delete(String name) throws Exception;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(messageBrokerPool,"messageBrokerPool must be not null");
	}

}
