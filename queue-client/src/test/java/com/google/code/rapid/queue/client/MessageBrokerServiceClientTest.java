package com.google.code.rapid.queue.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.junit.Test;

import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerException;

public class MessageBrokerServiceClientTest {
	
	MessageBrokerServiceClient client = new MessageBrokerServiceClient();
	
	@Test
	public void test() throws TException {
		client.setHost("localhost");
		client.setUsername("user_demo");
		client.setPassword("pwd");
		client.setVhost("vhost");
		
		Message msg = new Message();
        msg.setExchange("ex_demo");
        msg.setBody(StringUtils.repeat("a", 100).getBytes());
        msg.setRouterKey("yygame.ddt");
        client.open();
        
		client.send(msg);
		
		
		Message receive = client.receive("queue_demo", 1000);
		System.out.println("receive:"+new String(receive.getBody()));
	}
	
}
