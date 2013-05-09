package com.google.code.rapid.queue;

import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.rapid.queue.exchange.TopicExchange;


public class MessageBrokerTest extends Assert{
	MessageBroker mb = new MessageBroker();
	Message msg = new Message();
	byte[] body = new byte[]{1};
	
	String exchangeName = "ex";
	String queueName = "q";
	
	@Test
	public void test_send() throws Exception {
		bind("user.*");
		
		mb.send(new Message(exchangeName,"user.badqiu",body));
		assertEquals(body,mb.receive(queueName, 1).getBody());
		
		mb.send(new Message(exchangeName,"errorqueue.badqiu",body));
		assertEquals(null,mb.receive(queueName, 1).getBody());
	}

	private void bind(String routerKey) throws Exception {
		TopicExchange exchange = new TopicExchange();
		exchange.setExchangeName(exchangeName);
		exchange.setExchangeQueue(new LinkedBlockingDeque<Message>());
		exchange.afterPropertiesSet();
		mb.getManager().exchangeAdd(exchange);
		
		TopicQueue queue = new TopicQueue();
		queue.setQueueName(queueName);
		queue.setQueue(new LinkedBlockingDeque<byte[]>());
		mb.getManager().queueAdd(queue);
		
		mb.getManager().queueBind(exchangeName, queueName, routerKey);
	}
}
