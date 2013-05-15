package com.google.code.rapid.queue;

import java.util.Arrays;
import java.util.List;
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
	public void test_send_and_receive() throws Exception {
		bind("user.*");
		Thread.sleep(500);
		
		for(int i = 0; i < 1000; i++) {
			try {
				mb.send(new Message(exchangeName,"user.badqiu",body));
				assertEquals(body,mb.receive(queueName, 1).getBody());
				
				mb.sendBatch(Arrays.asList(new Message(exchangeName,"user.badqiu",body)));
				List<Message> receiveBatch = mb.receiveBatch(queueName, 1,100);
				assertEquals(1,receiveBatch.size());
				assertEquals(body,receiveBatch.get(0).getBody());
				
				mb.send(new Message(exchangeName,"errorqueue.badqiu",body));
				assertEquals(null,mb.receive(queueName, 1).getBody());
			}catch(Error e) {
				throw new RuntimeException("i="+i+" "+e);
			}
		}
	}
	
	@Test
	public void test_unbind() throws Exception {
		bind("user.*");
		Thread.sleep(500);
		
		mb.send(new Message(exchangeName,"user.badqiu",body));
		assertEquals(body,mb.receive(queueName, 1).getBody());
		
		mb.getManager().queueUnbind(exchangeName, queueName, "user.badqiu");
		assertEquals(null,mb.receive(queueName, 1).getBody());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void test_delete() throws Exception {
		bind("user.*");
		Thread.sleep(500);
		
		mb.send(new Message(exchangeName,"user.badqiu",body));
		assertEquals(body,mb.receive(queueName, 1).getBody());
		
		mb.getManager().queueDelete(queueName);
		assertEquals(null,mb.receive(queueName, 1).getBody());
	}
	
	private void bind(String routerKey) throws Exception {
		TopicExchange exchange = new TopicExchange();
		exchange.setExchangeName(exchangeName);
		exchange.setExchangeQueue(new LinkedBlockingDeque<byte[]>());
		exchange.afterPropertiesSet();
		mb.getManager().exchangeAdd(exchange);
		
		TopicQueue queue = new TopicQueue();
		queue.setQueueName(queueName);
		queue.setQueue(new LinkedBlockingDeque<byte[]>());
		mb.getManager().queueAdd(queue);
		
		mb.getManager().queueBind(exchangeName, queueName, routerKey);
	}
}
