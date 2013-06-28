package com.google.code.rapid.queue.model;

import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class BrokerExchangeTest extends Assert{

	BrokerExchange ex = new BrokerExchange();
	@Before
	public void setUp() {
		ex.setExchangeQueue(new LinkedBlockingQueue<byte[]>());
	}
	@Test
	public void test_offer() throws InterruptedException {
		ex.offer(new Message(new byte[0]));
		
	}
	
	@Test
	public void test_exchangeComsume() throws InterruptedException {
		ex.offer(new Message(new byte[0]));
		assertTrue(ex.exchangeComsume());
	}
	
}
