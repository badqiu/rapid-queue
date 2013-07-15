package com.google.code.rapid.queue.server.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class LoseMessageVhostStore {
	private static Map<String,LoseMessageStore> allVhostStore = new ConcurrentHashMap<String,LoseMessageStore>();
	
	public static LoseMessageStore storeLoseMessage() {
		String vhost = (String)ThriftContext.getServerContext().get(MessageBrokerServiceImpl.VHOST_KEY);
		LoseMessageStore vhostStore = getLoseMessageStoreByVhost(vhost);
		vhostStore.storeLoseMessage();
		return vhostStore;
	}

	private static LoseMessageStore getLoseMessageStoreByVhost(String vhost) {
		LoseMessageStore vhostStore = allVhostStore.get(vhost);
		if(vhostStore == null) {
			vhostStore = new LoseMessageStore();
			allVhostStore.put(vhost, vhostStore);
		}
		return vhostStore;
	}
	
	public static List<Message> receiveBatch(String vhost,String queueName,int batchSize) {
		LoseMessageStore store = getLoseMessageStoreByVhost(vhost);
		return store.receiveBatch(queueName, batchSize);
	}
	
	public static Message receive(String vhost,String queueName) {
		LoseMessageStore store = getLoseMessageStoreByVhost(vhost);
		return store.receive(queueName);
	}
	
	public static String status() {
		return allVhostStore.toString();
	}
	
}
