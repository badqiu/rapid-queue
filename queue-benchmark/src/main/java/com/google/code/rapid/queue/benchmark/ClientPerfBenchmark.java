package com.google.code.rapid.queue.benchmark;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import cn.org.rapid_framework.test.util.MultiThreadTestUtils;

import com.google.code.rapid.queue.client.MessageBrokerServiceClient;
import com.google.code.rapid.queue.client.SimpleMessage;
import com.google.code.rapid.queue.client.SimpleMessageBrokerServiceClient;

public class ClientPerfBenchmark {

	MessageBrokerServiceClient client = new MessageBrokerServiceClient();
	SimpleMessageBrokerServiceClient simpleClient = new SimpleMessageBrokerServiceClient();
	
	SimpleMessage msg = new SimpleMessage();
	@Before
	public void setUp() throws Exception {
		client.setHost("localhost");
		client.setUsername("admin");
		client.setPassword("admin");
		client.setVhost("vhost");
		client.setClientPoolSize(20);
		client.afterPropertiesSet();
		simpleClient.setClient(client);
		simpleClient.afterPropertiesSet();
		
		msg.setExchange("ex_demo");
		msg.setBody(StringUtils.repeat("a", 100).getBytes());
		msg.setRouterKey("yygame.ddt");
	}
	
	@Test
	public void test_perf() throws Exception {
		runPerf(100000,2);
		runPerf(400000,10);
	}
	
	public void runPerf(final int count,final int concurrency) throws Exception{
		long start = System.currentTimeMillis();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < count / concurrency; i++) {
					simpleClient.send(msg);
				}
			}
		};
		
		MultiThreadTestUtils.executeAndWait(concurrency, task);
		printTps("runPerf,concurrency:"+concurrency,count,start);
	}
	
	private static void printTps(String info, int count, long start) {
		long cost = System.currentTimeMillis() - start;
		System.out.println(info+" cost:" + cost + " count:"+count+" tps:"+(count * 1000.0 / cost));
	}
	
}
