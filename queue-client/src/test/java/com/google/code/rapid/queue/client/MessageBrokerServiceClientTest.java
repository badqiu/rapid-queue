package com.google.code.rapid.queue.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.thrift.api.Message;

public class MessageBrokerServiceClientTest extends Assert {
	
	MessageBrokerServiceClient client = new MessageBrokerServiceClient();
	SimpleMessageBrokerServiceClient simpleClient = new SimpleMessageBrokerServiceClient(client);
	
	Message msg = new Message();
	@Before
	public void setUp() throws Exception {
		client.setHost("localhost");
		client.setUsername("admin");
		client.setPassword("admin");
		client.setVhost("vhost");
		client.setCompress(true);
		client.afterPropertiesSet();
		
		msg.setExchange("ex_demo");
		msg.setBody(StringUtils.repeat("a", 1024).getBytes());
		msg.setRouterKey("yygame.ddt");
	}
	
	@After
	public void tearDown() throws Exception {
		client.destroy();
	}
	
	@Test
	public void testSimple() {
		SimpleMessage<String> msg2 = new SimpleMessage<String>();
		msg2.setPayload("100");
		msg2.setExchange("ex_demo");
		msg2.setRouterKey("yygame.ddt");
		
		simpleClient.send(msg2);
		
		SimpleMessage<String> fromMsg = simpleClient.receive("queue_demo", String.class);
		assertEquals(fromMsg.getPayload(),"100");
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
