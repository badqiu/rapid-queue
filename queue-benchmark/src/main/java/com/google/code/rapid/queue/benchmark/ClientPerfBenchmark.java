package com.google.code.rapid.queue.benchmark;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.google.code.rapid.queue.client.MessageBrokerServiceClient;
import com.google.code.rapid.queue.client.SimpleMessage;
import com.google.code.rapid.queue.client.SimpleMessageBrokerServiceClient;
import com.google.code.rapid.queue.client.SimpleMessageBrokerServiceClient.SerDsHelper;
import com.google.code.rapid.queue.thrift.api.Constants;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService;

public class ClientPerfBenchmark {

	MessageBrokerServiceClient client = new MessageBrokerServiceClient();
	SimpleMessageBrokerServiceClient simpleClient = new SimpleMessageBrokerServiceClient();

	SimpleMessage msg = new SimpleMessage();
	String host = System.getProperty("host","localhost");
	String username = System.getProperty("username","admin");
	String password = System.getProperty("password","admin");
	String vhost = System.getProperty("vhost","vhost");

	public ClientPerfBenchmark() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.err.println("uncaughtException,Thread:"+t+" cuase:"+e);
				e.printStackTrace();
			}
		});
		
		try {
			setUp();
		} catch (Exception e) {
			throw new RuntimeException("setUp error", e);
		}
	}

	public void setUp() throws Exception {
		// client.setHost("121.9.240.16");
		client.setHost(host);
		client.setUsername(username);
		client.setPassword(password);
		client.setVhost(vhost);
		client.setClientPoolSize(202);
		client.afterPropertiesSet();
		
		
		
		simpleClient.setClient(client);
		simpleClient.afterPropertiesSet();

		
		msg.setRouterKey("yygame.ddt");
	}

	public boolean test_perf_one_row() throws Exception {
		msg.setPayload("100");
		simpleClient.send(msg);
		Thread.sleep(100);
		return "100".equals(simpleClient.receive("queue_demo", 100,
				String.class).getPayload());
	}

	public void test_perf() throws Exception {
		int[] countArray = { 200000 };
		int[] concurrencyArray = { 8, 16 };
		int[] bodySizeArray = { 1, 1024, 1024 * 4 };

		
//		for(int count : new int[]{500000,5000000}) {
//			for (int concurrency : new int[]{8,16,30,40,50,200}) {
//				runObjectPoolPerf(count,concurrency);
//				runSimpleObjectPoolPerf(count,concurrency);
//			}
//		}
//		
//		int[] pingCountArray = {1000000,200000};
//		for (int count : pingCountArray) {
//			for (int concurrency : new int[]{8,16,30,40,50,200}) {
//				runPingPerf(count,concurrency);
//			}
//		}
		
		for (int count : countArray) {
			for (int bodySize : bodySizeArray) {
				for (int concurrency : concurrencyArray) {
					runSendPerf("ex_demo",count, concurrency, bodySize);
				}
			}
		}

		for (int count : countArray) {
			for (int bodySize : bodySizeArray) {
				for (int concurrency : concurrencyArray) {
					runSendPerf("ex_memory",count, concurrency, bodySize);
				}
			}
		}
		
		for (int count : countArray) {
			for (int concurrency : concurrencyArray) {
				runReceivePerf("queue_demo",count, concurrency);
			}
		}

		for (int count : countArray) {
			for (int concurrency : concurrencyArray) {
				runReceivePerf("queue_memory",count, concurrency);
			}
		}
		
	}

	public void runSendPerf(String exchange,final int count, final int concurrency, final int bodySize)
			throws Exception {
		msg.setExchange(exchange);
//		msg.setPayload(StringUtils.repeat("a", bodySize));
		msg.setBody(SerDsHelper.toBytes(StringUtils.repeat("a", bodySize)));
		long start = System.currentTimeMillis();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				MessageBrokerService.Client client = newClient();
				for (int i = 0; i < count / concurrency; i++) {
					try {
						client.send(msg);
					} catch (TException e) {
						e.printStackTrace();
					}
				}
			}
		};

		executeAndWait(concurrency, task);
		printTps("runSendPerf,concurrency:" + concurrency + " bodySize:"
				+ bodySize+" exchange:"+exchange, count, start,concurrency);
	}

	public void runPingPerf(final int count,
			final int concurrency) throws Exception {
		long start = System.currentTimeMillis();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					MessageBrokerService.Client client = newClient();
					for (int i = 0; i < count / concurrency; i++) {
						try {
							client.ping();
						} catch (TException e) {
							e.printStackTrace();
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

		};

		executeAndWait(concurrency, task);
		printTps("runPingPerf,concurrency:" + concurrency, count, start, concurrency);
	}

	private MessageBrokerService.Client newClient()  {
		TTransport transport = new TSocket(host, Constants.DEFAULT_SERVER_PORT);
		TProtocol protocol = new TBinaryProtocol(transport);
		try {
			transport.open();
			MessageBrokerService.Client client = new MessageBrokerService.Client(protocol);
			client.login(username, password, vhost);
			return client;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private synchronized void executeAndWait(final int concurrency, final Runnable task)
			throws InterruptedException {
		final AtomicInteger doneCount = new AtomicInteger(0);
		final AtomicInteger startCount = new AtomicInteger(0);
		Runnable delegate = new Runnable() {
			@Override
			public void run() {
//				System.out.println("task_start,startCount:"+ startCount.incrementAndGet()+" concurrency:"+concurrency+" doneCount:"+doneCount);
				task.run();
//				System.out.println("task_done,doneCount:"+ doneCount.incrementAndGet()+" concurrency:"+concurrency);
			}
		};
		MultiThreadTestUtils.executeAndWait(concurrency, delegate);
	}
	
	public void runReceivePerf(final String queueName,final int count, final int concurrency)
			throws Exception {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				MessageBrokerService.Client client = newClient();
				for (int i = 0; i < count / concurrency; i++) {
					try {
						client.receive(queueName, 1);
					} catch (MessageBrokerException e) {
						e.printStackTrace();
					} catch (TException e) {
						e.printStackTrace();
					}
				}
			}
		};
		runPerf("runReceivePerf,concurrency:" + concurrency+" queueName:"+queueName, count, concurrency,task);
	}

	private void runPerf(final String logInfo, final int count,
			final int concurrency,Runnable task) throws InterruptedException {
		long start = System.currentTimeMillis();
		executeAndWait(concurrency, task);
		printTps(logInfo, count, start,concurrency);
	}

	private static void printTps(String info, int count, long start,int concurrency) {
		long cost = System.currentTimeMillis() - start;
		int allTps = (int)(count * 1000.0 / cost);
		System.out.println(info + " cost:" + cost + " count:" + count + " all_tps:"
				+ allTps +" per_thread_tps:"+(allTps/concurrency));
	}

	public static void main(String[] args) throws Exception {
		new ClientPerfBenchmark().test_perf();
	}

}
