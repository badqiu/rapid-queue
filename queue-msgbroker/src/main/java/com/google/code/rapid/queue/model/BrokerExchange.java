package com.google.code.rapid.queue.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;
/**
 * 消息队列交换机,实现  exchange => queue, exchange => exchange的消息传递
 * 
 * @author badqiu
 *
 */
public class BrokerExchange implements InitializingBean{
	private static Logger logger = LoggerFactory.getLogger(BrokerExchange.class);
	
	private DurableTypeEnum durableType;
	private boolean autoDelete;  //auto delete exchange by timeout
	private int maxSize;
	private int memorySize;
	private String exchangeName; //exchange名称
	private String remarks; // 备注
	
	private BlockingQueue<byte[]> exchangeQueue; //内部exchange的一个队列
	
	private Map<String,BrokerBinding> bindQueueMap = new HashMap<String,BrokerBinding>();
	
	private ExecutorService executor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("TopicExchangeComsumeThread"));
	
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
	
	private void router2QueueList(String routerKey, byte[] data) {
		for(BrokerBinding binding : bindQueueMap.values()) {
			if(binding.matchRouterKey(routerKey)) {
				binding.getQueue().getQueue().offer(data);
			}
		}
	}
	
	public void bindQueue(BrokerQueue queue,String routerKey) {
		if(bindQueueMap.containsKey(queue.getQueueName())) {
			throw new IllegalArgumentException("already bind queue:"+queue.getQueueName()+" on exchange:"+exchangeName);
		}
		BrokerBinding binding = bindQueueMap.get(queue.getQueueName());
		if(binding == null) {
			binding = new BrokerBinding(queue);
			bindQueueMap.put(queue.getQueueName(),binding);
		}
		
		binding.addRouterKey(routerKey);
	}

	public void unbindQueue(String queueName) {
		if(StringUtils.isBlank(queueName)) throw new IllegalArgumentException("queueName must be not empty");
		bindQueueMap.remove(queueName);
	}
	
	public void unbindQueue(String queueName,String routerKey) {
		if(StringUtils.isBlank(queueName)) throw new IllegalArgumentException("queueName must be not empty");
		
		BrokerBinding binding = bindQueueMap.get(queueName);
		binding.removeRouterKey(routerKey);
	}
	
	public Collection<BrokerBinding> getBindQueueList() {
		return bindQueueMap.values();
	}

	public void clearAllBind() {
		bindQueueMap.clear();
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
		BrokerExchange other = (BrokerExchange) obj;
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


	public void destroy() throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
		//exchangeQueue.clear(); //FIXME 增加exchange queue的 destroy
	}
	
}
