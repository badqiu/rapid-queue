package com.google.code.rapid.queue;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DurableBlockingQueueTest extends Assert{

	DurableBlockingQueue queue = new DurableBlockingQueue("target/test_db/durable_blocking_test");
	@Before
	public void setUp() {
		queue.clear();
	}
	
	@Test
	public void test_logic() throws Exception {
		Thread pollT = new Thread() {
			public void run() {
				while(true) {
					try {
						byte[] data = queue.take();
						System.out.println(new String(data));
						execCount++;
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		pollT.start();
		
		int count = 10;
		for(int i = 0; i < count; i++) {
			queue.put(("input-"+i).getBytes());
			Thread.sleep(300);
		}
		assertEquals(execCount,count);
	}
	
	@Test
	public void test_conn() throws Exception {
		
		
		final String offerData = StringUtils.repeat("a", 1024);
		for(int i = 0; i < 10; i++) {
			Thread offerT = new Thread() {
				public void run() {
					while(true) {
						try {
							queue.offer(offerData.getBytes());
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			offerT.start();
			
			Thread pollT = new Thread() {
				public void run() {
					while(true) {
						try {
							byte[] data = queue.poll();
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			pollT.start();
		}
		Thread.sleep(100);
		
		for(int i = 0; i < 10; i++) {
			Thread.sleep(1000);
			
			System.out.println(i + " ----------------restart Queue-----------------------");
			queue.close();
			queue = new DurableBlockingQueue("target/test_db/durable_blocking_test");
		}
		
	}
	
	int execCount = 0;
	@Test
	public void test_multi_threads() throws InterruptedException {
		for(int i = 0; i < 10; i++) {
			Thread pollT = new Thread() {
				public void run() {
					while(true) {
						try {
							byte[] data = queue.poll(-1, null);
							System.out.println(new String(data)+" Thread:"+Thread.currentThread().getName());
							execCount++;
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			pollT.start();
		}
		
		Thread.sleep(1000);
		
		int count = 20;
		for(int i = 0; i < count; i++) {
			String string = ""+i;
			queue.offer(string.getBytes());
			Thread.sleep(100);
		}
		
		assertEquals(count,execCount);
	}
	
	@Test
	public void test_poll_timeout() throws Exception {
		Thread pollT = new Thread() {
			public void run() {
				while(true) {
					try {
						byte[] data = queue.poll(100, TimeUnit.MILLISECONDS);
						if(data != null) {
							System.out.println(new String(data));
							execCount++;
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		pollT.start();
		
		int count = 10;
		for(int i = 0; i < count; i++) {
			queue.put(("input-"+i).getBytes());
			Thread.sleep(300);
		}
		assertEquals(execCount,count);
	}
	
	
	int dataNullCount;
	int dataNotNullCount;
	@Test
	public void test_multi_threads_put() throws Exception {
		Runnable poll = new Runnable() {
			public void run() {
				while(true) {
					try {
						byte[] data = queue.take();
						if(data == null) {
							dataNullCount++;
//							System.err.println("data=null");
						}else {
							dataNotNullCount++;
						}
						if((dataNotNullCount % 10000 == 1) || (dataNullCount % 10000 == 1)) {
							System.out.println("dataNotNullCount:"+dataNotNullCount+" dataNullCount:"+dataNullCount);
						}
						
						execCount++;
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		startWithMultiThread(poll,10);
		
		Runnable put = new Runnable() {
			public void run() {
				while(true) {
					try {
						for(int i = 0; i < 1000; i++) {
							String string = Thread.currentThread().getName()+"-"+i;
							queue.put(string.getBytes());
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		startWithMultiThread(put,10);
	
		Thread.sleep(10000);
		assertEquals(dataNullCount,0);
	}

	private void startWithMultiThread(Runnable task,int threads) {
		for(int i = 0; i < threads; i++) {
			Thread pollT = new Thread(task) {
			};
			pollT.start();
		}
	}
}
