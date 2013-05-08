package com.google.code.rapid.queue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.code.rapid.queue.exchange.TopicExchange;
import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.service.BindingService;
import com.google.code.rapid.queue.metastore.service.ExchangeService;
import com.google.code.rapid.queue.metastore.service.QueueService;
import com.google.code.rapid.queue.metastore.service.VhostService;

public class MessageBrokerBuilder {
	private BindingService bindingService;
	private QueueService queueService;
	private ExchangeService exchangeService;
	private VhostService vhostService;
	
	private File dataDir;
	
	public void setBindingService(BindingService bindingService) {
		this.bindingService = bindingService;
	}

	public void setQueueService(QueueService queueService) {
		this.queueService = queueService;
	}

	public void setExchangeService(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

	public void setVhostService(VhostService vhostService) {
		this.vhostService = vhostService;
	}

	public Map<String,MessageBroker> build() {
		return new Builder().execute();
	}
	
	private class Builder {
		
		public Map<String,MessageBroker> execute() {
			Map<String,MessageBroker> messageBrokerMap = new HashMap<String,MessageBroker>();
			List<Vhost> vhostList = vhostService.findAll();
			for(Vhost vhost : vhostList) {
				MessageBroker mb = buildMessageBroker(vhost);
				messageBrokerMap.put(vhost.getVhostName(), mb);
			}
			return messageBrokerMap;
		}

		private MessageBroker buildMessageBroker(Vhost vhost) {
			MessageBroker mb = new MessageBroker();
			
			List<Exchange> exchangeList = exchangeService.findByVhostName(vhost.getVhostName());
			for(Exchange exchange : exchangeList) {
				TopicExchange topicExchange = newTopicExchange(exchange);
				mb.getManager().exchangeAdd(topicExchange);
				
				List<Queue> queueList = bindingService.findQueueByVhostName(vhost.getVhostName(),exchange.getExchangeName());
				for(Queue queue : queueList) {
					TopicQueue q = newTopicQueue(queue);
					mb.getManager().queueAdd(q);
					mb.getManager().queueBind(exchange.getExchangeName(), q.getQueueName());
				}
			}
			
			return mb;
		}
	}
	
	private TopicQueue newTopicQueue(Queue queue) {
		TopicQueue r = new TopicQueue();
		r.setQueueName(queue.getQueueName());
		r.setRemarks(queue.getRemarks());
		r.setAutoDelete(queue.getAutoDelete());
		r.setDurable(queue.getDurable());
		r.setQueue(newBlockQueue(queue.getDurable(),queue.getMaxSize(),"queue/"+queue.getQueueName()));
		r.setMaxSize(queue.getMaxSize());
		return r;
	}
	
	private TopicExchange newTopicExchange(Exchange exchange) {
		TopicExchange r = new TopicExchange();
		r.setExchangeName(exchange.getExchangeName());
		r.setRemarks(exchange.getRemarks());
		r.setAutoDelete(exchange.getAutoDelete());
		r.setDurable(exchange.getDurable());
		r.setExchangeQueue(newBlockQueue(exchange.getDurable(),exchange.getMaxSize(),"exchange/"+exchange.getExchangeName()));
		r.setMaxSize(exchange.getMaxSize());
		return r;
	}

	private BlockingQueue newBlockQueue(boolean durable,int maxSize,String subPath) {
		if(dataDir == null) throw new IllegalArgumentException("dataDir must be not null");
		
		if(durable) {
			return new DurableBlockingQueue(new File(dataDir,subPath).getAbsolutePath());
		}else {
			if(maxSize > 0) {
				return new LinkedBlockingQueue(maxSize);
			}else {
				return new LinkedBlockingQueue();
			}
		}
	}
}
