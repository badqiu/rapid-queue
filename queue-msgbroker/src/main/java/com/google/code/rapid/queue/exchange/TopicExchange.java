package com.google.code.rapid.queue.exchange;

import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import com.google.code.rapid.queue.Message;
import com.google.code.rapid.queue.TopicQueue;
import com.google.code.rapid.queue.util.RouterKeyUtil;
/**
 * 消息队列交换机,实现  exchange => queue, exchange => exchange的消息传递
 * 
 * @author badqiu
 *
 */
public class TopicExchange {

	private boolean durable;
	private boolean autoDelete;  //auto delete exchange by timeout
	private String exchangeName; //exchange名称
	private String remarkds; // 备注
	private List<String> routerKeyList;	//exchange routerKey for exchange <=> exchange msg transfer
	
	private Queue<Message> exchangeQueue; //内部exchange的一个队列
	
	private List<TopicQueue> bindQueueList;
	private List<TopicExchange> bindExchangeList;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public void offer(Message msg) {
		exchangeQueue.offer(msg);
	}
	
	public void startComsumeThread() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while(true) {
					exchangeComsume();
				}
			}
		});
	}
	
	private void exchangeComsume() {
		Message msg = exchangeQueue.poll();
		if(msg != null) {
			router2QueueList(msg.getRouterKey(),msg.getBody());
			router2ExchangeList(msg);
		}
	}
	
	private void router2ExchangeList(Message msg) {
		if(bindExchangeList != null) {
			for(TopicExchange exchange : bindExchangeList) {
				if(exchange.matchRouterKey(msg.getRouterKey())) {
					exchange.offer(msg);
				}
			}
		}
	}

	private boolean matchRouterKey(String routerKeyValue) {
		return RouterKeyUtil.matchRouterKey(routerKeyList, routerKeyValue);
	}

	private void router2QueueList(String routerKey, byte[] data) {
		if(bindQueueList != null) {
			for(TopicQueue queue : bindQueueList) {
				if(queue.matchRouterKey(routerKey)) {
					queue.getQueue().offer(data);
				}
			}
		}
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public boolean isAutoDelete() {
		return autoDelete;
	}

	public void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getRemarkds() {
		return remarkds;
	}

	public void setRemarkds(String remarkds) {
		this.remarkds = remarkds;
	}
	
	public void bindQueue(TopicQueue queue) {
		if(bindQueueList.contains(queue)) {
			throw new IllegalArgumentException("already bind queue:"+queue.getQueueName()+" on exchange:"+exchangeName);
		}
		
		bindQueueList.add(queue);
	}

	public void unbindQueue(String queueName) {
		if(StringUtils.isBlank(queueName)) throw new IllegalArgumentException("queueName must be not empty");
		
		for(ListIterator<TopicQueue> it = bindQueueList.listIterator(); it.hasNext(); ) {
			TopicQueue q = it.next();
			if(q.getQueueName().equals(queueName)) {
				it.remove();
			}
		}
	}
	
	public void bindExchange(TopicExchange exchange) {
		if(bindExchangeList.contains(exchange)) {
			throw new IllegalArgumentException("already bind exchange:"+exchange.getExchangeName()+" on exchange:"+exchangeName);
		}
		
		bindExchangeList.add(exchange);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((exchangeName == null) ? 0 : exchangeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicExchange other = (TopicExchange) obj;
		if (exchangeName == null) {
			if (other.exchangeName != null)
				return false;
		} else if (!exchangeName.equals(other.exchangeName))
			return false;
		return true;
	}
	
}
