package com.google.code.rapid.queue.client;

import java.util.List;

/**
 * 消息发送的客户端
 * 
 * @author badqiu
 *
 */
public interface RapidQueueClient {
	/**
	 * 发送消息
	 * @param msg
	 */
	public void send(Message msg);
	
	/**
	 * 批量发送消息
	 * @param msg
	 */	
	public void sendBatch(List<Message> msg);
	
	/**
	 * 接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 */	
	public Message receive(String queueName,int timeout);
	
	/**
	 * 批量接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 * @param batchSize 批量接收的大小
	 */		
	public List<Message> receiveBatch(String queueName,int timeout,int batchSize);
	
}
