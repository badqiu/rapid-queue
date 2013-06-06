package com.google.code.rapid.queue.benchmark;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


public class RabbitMqBenchmark {

	private String host = System.getProperty("host","localhost");
	private int port = Integer.parseInt(System.getProperty("port","13672"));
	private String username = System.getProperty("username","admin");
	private String password = System.getProperty("password","admin");
	private String virtualHost = System.getProperty("virtualHost","vhost");
	
	ConnectionFactory factory = new ConnectionFactory();
	Connection connection = null;
	
	public RabbitMqBenchmark() throws IOException {
		setUp();
	}
	
	public void setUp() throws IOException {
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setHost(host);
		factory.setPort(port);
		factory.setVirtualHost(virtualHost);
		
		System.out.println("setUp start");
		connection = factory.newConnection();
		
		Channel channel = connection.createChannel();
		channel.exchangeDeclare("exchange_durable", "topic", true);
		channel.exchangeDeclare("exchange_memory", "topic", false);
		channel.queueDeclare("queue_durable", true, false, false, null);
		channel.queueDeclare("queue_memory", true, false, false, null);
		channel.queueBind("queue_durable", "exchange_durable", "*");
		channel.queueBind("queue_memory", "exchange_memory", "*");
		channel.close();
		System.out.println("setUp end");
	}
	
	public void test_perf() throws IOException, InterruptedException {
		System.out.println("start test_perf");
		
		int[] countArray = { 200000 };
		int[] concurrencyArray = { 8, 16 };
		int[] bodySizeArray = { 1,40, 1024, 1024 * 4 };
		
		for (final int count : countArray) {
			for (final int bodySize : bodySizeArray) {
				for (final int concurrency : concurrencyArray) {
					runSendPerf("exchange_durable",count, bodySize, concurrency);
				}
			}
		}
		
		for (final int count : countArray) {
			for (final int bodySize : bodySizeArray) {
				for (final int concurrency : concurrencyArray) {
					runSendPerf("exchange_memory",count, bodySize, concurrency);
				}
			}
		}
		
		for (final int count : countArray) {
			for (final int concurrency : concurrencyArray) {
				for (final int prefetchCount : new int[]{1,50,1000}) {
					runReceivePerf("queue_durable",count, prefetchCount,concurrency);
				}
			}
		}

		for (final int count : countArray) {
			for (final int concurrency : concurrencyArray) {
				for (final int prefetchCount : new int[]{1,50,1000}) {
					runReceivePerf("queue_memory",count, prefetchCount,concurrency);
				}
			}
		}
	}

	private void runReceivePerf(final String queue,final int count,final int prefetchCount,final int concurrency) throws InterruptedException {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					Channel channel = connection.createChannel();
					QueueingConsumer consumer = new QueueingConsumer(channel);
					channel.basicQos(prefetchCount);
					channel.basicConsume(queue, false, consumer);

					for(int i = 0; i < count; i++) {
						consumer.nextDelivery();
					}
					
					channel.close();
				}catch(Exception e) {
					throw new RuntimeException("error",e);
				}
			}
		};
		
		ClientPerfBenchmark.runPerf("rabbitmq receive perf,queue:"+queue+" concurrency:"+concurrency+" prefetchCount:"+prefetchCount, count, concurrency, task);
	}

	private void runSendPerf(final String exchange,final int count, final int bodySize,final int concurrency) throws InterruptedException {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					Connection connection = factory.newConnection();
					Channel channel = connection.createChannel();
					byte[] body = StringUtils.repeat('a', bodySize).getBytes();
					for(int i = 0; i < count /concurrency; i++) {
						channel.basicPublish(exchange, "a", null, body );
//						if(i % 2000 == 0) {
//							System.out.println("i="+i + " threadName:"+Thread.currentThread().getName());
//						}
					}
				}catch(Exception e) {
					throw new RuntimeException("error",e);
				}
			}
		};
		
		ClientPerfBenchmark.runPerf("rabbitmq send perf,exchange:"+exchange+" concurrency:"+concurrency+" bodySize:"+bodySize, count, concurrency, task);
	}
	
	public static void main(String[] args) throws Exception {
		new RabbitMqBenchmark().test_perf();
	}
	
}
