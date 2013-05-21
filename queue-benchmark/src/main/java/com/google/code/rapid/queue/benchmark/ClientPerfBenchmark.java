package com.google.code.rapid.queue.benchmark;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;

import cn.org.rapid_framework.test.util.MultiThreadTestUtils;
import cn.org.rapid_framework.util.Profiler;

import com.google.code.rapid.queue.client.MessageBrokerServiceClient;
import com.google.code.rapid.queue.client.SimpleMessageBrokerServiceClient;
import com.google.code.rapid.queue.thrift.api.Message;

public class ClientPerfBenchmark {

	MessageBrokerServiceClient client = new MessageBrokerServiceClient();
	SimpleMessageBrokerServiceClient simpleClient = new SimpleMessageBrokerServiceClient();
	
	Message msg = new Message();
	@Before
	public void setUp() throws Exception {
		client.setHost("localhost");
		client.setUsername("user_demo");
		client.setPassword("pwd");
		client.setVhost("vhost");
		client.afterPropertiesSet();
		simpleClient.setClient(client);
		simpleClient.afterPropertiesSet();
		
		msg.setExchange("ex_demo");
		msg.setBody(StringUtils.repeat("a", 100).getBytes());
		msg.setRouterKey("yygame.ddt");
	}
	
	
	public void test_100(final int conrrent) throws Exception{
		final int count = 100000;
		Profiler.start(count);
		
		MultiThreadTestUtils.executeAndWait(conrrent, new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < count / conrrent; i++) {
					simpleClient.send(msg);
				}
			}
		});
		
		
		Profiler.release();
	}
	
}
