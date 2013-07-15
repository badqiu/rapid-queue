package com.google.code.rapid.queue.server.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.CollectionUtils;

import com.google.code.rapid.queue.model.Message;
import com.google.code.rapid.queue.server.ThriftContext;

/**
 * 用于保存客户端最后一次请求的数据,以防止数据丢失
 * 
 * 丢失原因: 因为如果客户端进程被kill,会导致最后一次远程调用的返回数据没有客户端接收而导致丢失
 * 
 * @author badqiu
 *
 */
public class LoseMessageStore {
	public Map<String,Queue<Message>> lastLoseMsgStore = new ConcurrentHashMap<String,Queue<Message>>();
	
	public Message receive(String queueName) {
		Queue<Message> queue = lastLoseMsgStore.get(queueName);
		if(CollectionUtils.isEmpty(queue)) {
			return null;
		}
		
		return queue.poll();
	}
	
	public List<Message> receiveBatch(String queueName,int batchSize) {
		Queue<Message> queue = lastLoseMsgStore.get(queueName);
		if(CollectionUtils.isEmpty(queue)) {
			return Collections.EMPTY_LIST;
		}
		
		ArrayList<Message> list = new ArrayList<Message>();
		for(int i = 0; i < batchSize && !queue.isEmpty(); i++) {
			Message msg = queue.poll();
			list.add(msg);
		}
		return list;
	}
	
	public void storeLoseMessage() {
		Message msg = (Message)ThriftContext.get("receiveMessage");
		add2LoseList(msg);
		
		List<Message> receiveBatchMessage = (List<Message>)ThriftContext.get("receiveBatchMessage");
		if(CollectionUtils.isNotEmpty(receiveBatchMessage)) {
			for(Message item : receiveBatchMessage) {
				add2LoseList(item);
			}
		}
	}
	
	private void add2LoseList(Message msg) {
		if(msg == null || msg.getBody() == null) return;
		Queue<Message> loseList = lastLoseMsgStore.get(msg.getQueueName());
		if(loseList == null) {
			loseList = new LinkedBlockingQueue<Message>();
			lastLoseMsgStore.put(msg.getQueueName(), loseList);
		}
		loseList.add(msg);
	}
	
	public String toString() {
		Map<String,Integer> status = new HashMap<String,Integer>(lastLoseMsgStore.size() * 2);
		for(Map.Entry<String, Queue<Message>> entry : lastLoseMsgStore.entrySet()) {
			status.put(entry.getKey(), entry.getValue().size());
		}
		return status.toString();
	}
	
}
