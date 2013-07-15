package com.google.code.rapid.queue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.service.BindingService;
import com.google.code.rapid.queue.metastore.service.ExchangeService;
import com.google.code.rapid.queue.metastore.service.QueueService;
import com.google.code.rapid.queue.metastore.service.VhostService;
import com.google.code.rapid.queue.model.BrokerExchange;
import com.google.code.rapid.queue.model.BrokerQueue;
import com.google.code.rapid.queue.model.DurableTypeEnum;

public class MessageBrokerPoolBuilder implements InitializingBean {

	private Logger logger = LoggerFactory.getLogger(MessageBrokerPoolFactoryBean.class);
	
	protected BindingService bindingService;
	protected QueueService queueService;
	protected ExchangeService exchangeService;
	protected VhostService vhostService;
	
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
	
	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;
	}

	public MessageBrokerPool build() {
		Assert.notNull(dataDir,"dataDir must be not null");
		Assert.notNull(bindingService,"bindingService must be not null");
		Assert.notNull(queueService,"queueService must be not null");
		Assert.notNull(exchangeService,"exchangeService must be not null");
		Assert.notNull(vhostService,"vhostService must be not null");
		if(!dataDir.exists()) {
			dataDir.mkdirs();
		}
		
		Map<String,MessageBroker> map = new Builder().execute();
		MessageBrokerPool pool = new MessageBrokerPool(map);
		return pool;
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
	}
	
	public MessageBroker buildMessageBroker(Vhost vhost) {
		MessageBroker mb = new MessageBroker();
		mb.setVhostName(vhost.getVhostName());
		addAllExchange(vhost, mb);
		addAllQueue(vhost, mb);
		bindQueuesAndExchanges(vhost, mb);
		return mb;
	}

	private void bindQueuesAndExchanges(Vhost vhost, MessageBroker mb) {
		List<Binding> bindingList = bindingService.findByVhostName(vhost.getVhostName());
		for(Binding binding : bindingList) {
			try {
				mb.getManager().queueBind(binding.getExchangeName(), binding.getQueueName(),binding.getRouterKey());
			}catch(Exception e) {
				logger.error("error on bindding:"+binding,e);
			}
		}
	}

	private void addAllQueue(Vhost vhost, MessageBroker mb) {
		for(Queue queue : queueService.findByVhostName(vhost.getVhostName())) {
			try {
				BrokerQueue q = newBrokerQueue(queue);
				mb.getManager().queueAdd(q);
			}catch(Exception e) {
				logger.error("error on create queue:"+queue,e);
			}
		}
	}

	private void addAllExchange(Vhost vhost, MessageBroker mb) {
		List<Exchange> exchangeList = exchangeService.findByVhostName(vhost.getVhostName());
		for(Exchange exchange : exchangeList) {
			try {
				BrokerExchange topicExchange = newTopicExchange(exchange);
				mb.getManager().exchangeAdd(topicExchange);
			}catch(Exception e) {
				logger.error("error on create exchcnage:"+exchange,e);
			}
		}
	}
	
	public BrokerQueue newBrokerQueue(Queue queue) throws Exception{
		BrokerQueue r = new BrokerQueue();
		r.setQueueName(queue.getQueueName());
		r.setRemarks(queue.getRemarks());
		r.setAutoDelete(queue.getAutoDelete());
		r.setEnabled(queue.getEnabled());
		r.setDurableType(DurableTypeEnum.valueOf(queue.getDurableType()));
		r.setQueue(newBlockQueue(r.getDurableType(),queue.getMemorySize(),queue.getMaxSize(),queue.getVhostName()+"/queue/"+queue.getQueueName()));
		r.setMaxSize(queue.getMaxSize());
		return r;
	}
	
	public BrokerExchange newTopicExchange(Exchange exchange) throws Exception {
		BrokerExchange r = new BrokerExchange();
		r.setExchangeName(exchange.getExchangeName());
		r.setRemarks(exchange.getRemarks());
		r.setAutoDelete(exchange.getAutoDelete());
		r.setDurableType(DurableTypeEnum.valueOf(exchange.getDurableType()));
		r.setExchangeQueue(newBlockQueue(r.getDurableType(),exchange.getMemorySize(),exchange.getMaxSize(),exchange.getVhostName()+"/exchange/"+exchange.getExchangeName()));
		r.setMaxSize(exchange.getMaxSize());
		r.setEnabled(exchange.getEnabled());
		
		r.afterPropertiesSet();
		return r;
	}

	private BlockingQueue newBlockQueue(DurableTypeEnum durableType,int memorySize,int maxSize,String subPath) {
		if(dataDir == null) throw new IllegalArgumentException("dataDir must be not null");
		if(durableType == null) throw new IllegalArgumentException("durableType must be not null");
		if(subPath == null) throw new IllegalArgumentException("subPath must be not null");
		
		if(durableType == DurableTypeEnum.MEMORY) {
			if(maxSize > 0) {
				return new LinkedBlockingQueue(maxSize);
			}else {
				return new LinkedBlockingQueue();
			}
		}else if(durableType == DurableTypeEnum.DURABLE) {
			return new DurableBlockingQueue(new File(dataDir,subPath).getAbsolutePath());
		}else if(durableType == DurableTypeEnum.HALF_DURABLE) {
			if(memorySize <= 0) {
				throw new IllegalArgumentException("memorySize > 0 must be true on HALF_DURABLE,current:"+memorySize);
			}
			return new BufferedBlockingQueue(memorySize,new DurableBlockingQueue(new File(dataDir,subPath).getAbsolutePath()));
		}else {
			throw new IllegalArgumentException("unknow durableType:"+durableType);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dataDir,"dataDir must be not null");
		Assert.notNull(bindingService,"bindingService must be not null");
		Assert.notNull(queueService,"queueService must be not null");
		Assert.notNull(exchangeService,"exchangeService must be not null");
		Assert.notNull(vhostService,"vhostService must be not null");
	}
	
}
