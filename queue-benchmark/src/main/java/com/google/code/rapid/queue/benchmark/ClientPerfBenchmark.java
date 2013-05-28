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
	
	public ClientPerfBenchmark() throws Exception {
		setUp();
	}
	
	public void setUp() throws Exception {
		client.setHost("121.9.240.16");
//		client.setHost("localhost");
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
	
	public void test_perf() throws Exception {
		int[] countArray = {200000,2000000};
		int[] concurrencyArray = {1,2,8,16,50};
		int[] bodySizeArray = {1,100,1024,1024*1024};
		for(int count : countArray) {
			for(int concurrency : concurrencyArray) {
				for(int bodySize : bodySizeArray) {
					runSendPerf(count,concurrency,bodySize);
				}
			}
		}
		
//		for(int count : countArray) {
//			for(int concurrency : concurrencyArray) {
//				for(int bodySize : bodySizeArray) {
//					runReceivePerf(count,concurrency);
//				}
//			}
//		}
	}
	
	public void runSendPerf(final int count,final int concurrency,int bodySize) throws Exception{
		msg.setPayload(StringUtils.repeat("a", bodySize).getBytes());
		System.out.println("runSendPerf() started");
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
		printTps("runSendPerf,concurrency:"+concurrency+" bodySize:"+bodySize,count,start);
	}
	
	public void runReceivePerf(final int count,final int concurrency) throws Exception{
		long start = System.currentTimeMillis();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < count / concurrency; i++) {
					simpleClient.receive("queue_demo", 1, String.class);
				}
			}
		};
		
		MultiThreadTestUtils.executeAndWait(concurrency, task);
		printTps("runReceivePerf,concurrency:"+concurrency,count,start);
	}
	
	private static void printTps(String info, int count, long start) {
		long cost = System.currentTimeMillis() - start;
		System.out.println(info+" cost:" + cost + " count:"+count+" tps:"+(count * 1000.0 / cost));
	}
	
	public static void main(String[] args) throws Exception {
		new ClientPerfBenchmark().test_perf();
	}
	
}
