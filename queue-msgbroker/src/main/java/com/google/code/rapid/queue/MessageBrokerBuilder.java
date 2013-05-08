package com.google.code.rapid.queue;

import java.util.List;

import com.google.code.rapid.queue.exchange.TopicExchange;
import com.google.code.rapid.queue.metastore.model.Binding;
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

	public MessageBroker build() {
		return new Build().execute();
	}
	
	private class Build {
		private MessageBroker mb = new MessageBroker();
		private List<Exchange> exchangeList = null;
		private List<Binding> bindingList = null;
		private List<Binding> bindingList = null;
		
		public MessageBroker execute() {
			
			List<Vhost> vhostList = vhostService.findAll();
			for(Vhost vhost : vhostList) {
				List<Exchange> exchangeList = exchangeService.findByVhostName(vhost.getVhostName());
				
				for(Exchange exchange : exchangeList) {
					List<Queue> queueList = bindingService.findQueueByVhostName(vhost.getVhostName(),exchange.getExchangeName());
					TopicExchange ex = buildTopicExchange(exchange,queueList);
					List<Queue> queueList = queueService.findByVhostName(vhost.getVhostName(),exchange.getExchangeName());
				}
			}
			
			
			return mb;
		}
		
		
	}
}
