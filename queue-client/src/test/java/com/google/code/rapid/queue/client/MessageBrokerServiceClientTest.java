package com.google.code.rapid.queue.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerException;

public class MessageBrokerServiceClientTest {
	
	MessageBrokerServiceClient client = new MessageBrokerServiceClient();
	
	Message msg = new Message();
	@Before
	public void setUp() throws MessageBrokerException, TException {
		client.setHost("localhost");
		client.setUsername("user_demo");
		client.setPassword("pwd");
		client.setVhost("vhost");
		client.open();
		
		msg.setExchange("ex_demo");
		msg.setBody(StringUtils.repeat("a", 100).getBytes());
		msg.setRouterKey("yygame.ddt");
	}
	
	@Test
	public void test() throws TException {
        for(int i = 0; i < 100; i++) {
        	client.send(msg);
			
			Message receive = client.receive("queue_demo", 1);
			
			System.out.println("receive:"+ArrayUtils.getLength(receive.getBody()));
        }
	}
	
	@Test
	public void test_perf() throws TException {
		int count = 100000;
		long start = System.currentTimeMillis();
        for(int i = 0; i < count; i++) {
        	client.send(msg);
        }
        printTps("send perf",count,start);
        
        start = System.currentTimeMillis();
        for(int i = 0; i < count; i++) {
        	client.receive("queue_demo", 1);
        }
        printTps("receive perf",count,start);
	}

	@Test
	public void test_batch_perf() throws TException {
		List<Message> list = new ArrayList<Message>();
		for(int i = 0; i < 100; i++) {
			list.add(msg);
		}
		
		int count = 3000;
		long start = System.currentTimeMillis();
        for(int i = 0; i < count; i++) {
        	client.sendBatch(list);
        }
        printTps("sendBatch perf",count,start);
        
        start = System.currentTimeMillis();
        for(int i = 0; i < count; i++) {
        	client.receiveBatch("queue_demo", 1,list.size());
        }
        printTps("receiveBatch perf",count,start);
	}
	
	private void printTps(String info, int count, long start) {
		long cost = System.currentTimeMillis() - start;
		System.out.println(info+" cost:" + cost + " count:"+count+" tps:"+(count * 1000.0 / cost));
	}
	
}
