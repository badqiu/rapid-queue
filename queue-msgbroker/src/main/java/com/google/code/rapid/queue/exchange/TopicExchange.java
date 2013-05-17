package com.google.code.rapid.queue.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.DurableTypeEnum;
import com.google.code.rapid.queue.Message;
import com.google.code.rapid.queue.TopicQueue;
import com.google.code.rapid.queue.util.RouterKeyUtil;
/**
 * 消息队列交换机,实现  exchange => queue, exchange => exchange的消息传递
 * 
 * @author badqiu
 *
 */
public class TopicExchange implements InitializingBean{
	private static Logger logger = LoggerFactory.getLogger(TopicExchange.class);
	
	private DurableTypeEnum durableType;
	private boolean autoDelete;  //auto delete exchange by timeout
	private int maxSize;
	private int memorySize;
	private String exchangeName; //exchange名称
	private String remarks; // 备注
	private List<String> routerKeyList;	//exchange routerKey for exchange <=> exchange msg transfer
	
	private BlockingQueue<byte[]> exchangeQueue; //内部exchange的一个队列
	
	private List<TopicQueue> bindQueueList = new ArrayList<TopicQueue>();
	private List<TopicExchange> bindExchangeList = new ArrayList<TopicExchange>();
	
	private ExecutorService executor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("TopicExchangeComsumeThread"));
	
	public void offer(Message msg) throws InterruptedException {
		logger.debug("offer {} msg:{}",exchangeName,msg);
		exchangeQueue.put(Message.toBytes(msg));
	}
	
	public void startComsumeThread() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("started comsumeThread for exchange:"+exchangeName);
				while(true) {
					try {
						exchangeComsume();
					} catch (InterruptedException e) {
						break;
					} catch(Exception e) {
						logger.error("error on consume exchange:"+exchangeName,e);
					}
				}
				logger.info("stoped comsumeThread for exchange:"+exchangeName);
			}
		});
	}
	
	public Queue<byte[]> getExchangeQueue() {
		return exchangeQueue;
	}


	public void setExchangeQueue(BlockingQueue<byte[]> exchangeQueue) {
		this.exchangeQueue = exchangeQueue;
	}

	private void exchangeComsume() throws InterruptedException {
		Message msg = Message.fromBytes(exchangeQueue.take());
		logger.debug("exchangeComsume {} msg:{}",exchangeName,msg);
		if(msg != null) {
			router2QueueList(msg.getRouterKey(),msg.getBody());
			router2ExchangeList(msg);
		}
	}
	
	private void router2ExchangeList(Message msg) {
		if(bindExchangeList != null) {
			for(TopicExchange exchange : bindExchangeList) {
				if(exchange.matchRouterKey(msg.getRouterKey())) {
					try {
						exchange.offer(msg);
					}catch(Exception e) {
						logger.error("error on router2ExchangeList,exchange:"+exchange);
					}
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

	public DurableTypeEnum getDurableType() {
		return durableType;
	}

	public void setDurableType(DurableTypeEnum durableType) {
		this.durableType = durableType;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public int getMemorySize() {
		return memorySize;
	}


	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
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
	
	public void unbindQueue(String queueName,String routerKey) {
		if(StringUtils.isBlank(queueName)) throw new IllegalArgumentException("queueName must be not empty");
		
		for(ListIterator<TopicQueue> it = bindQueueList.listIterator(); it.hasNext(); ) {
			TopicQueue q = it.next();
			if(q.getQueueName().equals(queueName)) {
				q.getRouterKeyList().remove(routerKey);
				if(q.getRouterKeyList().isEmpty()) {
					it.remove();
				}
			}
		}
	}
	
	public void bindExchange(TopicExchange exchange) {
		if(bindExchangeList.contains(exchange)) {
			throw new IllegalArgumentException("already bind exchange:"+exchange.getExchangeName()+" on exchange:"+exchangeName);
		}
		
		bindExchangeList.add(exchange);
	}
	
	public void clearAllBind() {
		bindExchangeList.clear();
		bindQueueList.clear();
	}
	
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
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
	
	@Override
	public String toString() {
		return "TopicExchange [exchangeName=" + exchangeName + ", remarks="
				+ remarks + ", durableType=" + durableType + ", autoDelete="
				+ autoDelete + ", maxSize=" + maxSize + ", memorySize="
				+ memorySize + "]";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(exchangeName,"exchangeName must be not blank");
		Assert.notNull(exchangeQueue,"exchangeQueue must be not null");
		
		startComsumeThread();
	}
	
}
