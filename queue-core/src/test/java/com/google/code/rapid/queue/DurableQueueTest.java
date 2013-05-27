package com.google.code.rapid.queue;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DurableQueueTest extends Assert{
	DurableQueue queue = new DurableQueue("test_db/durable_test");
	@Before
	public void setUp(){
		queue.clear();
	}
	
	@Test
	public void test_close_and_restar_queue() throws Exception {
		
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
		
		for(int i = 0; i < 1000; i++) {
			Thread.sleep(1000 * 10);
			
			System.out.println(i + " ----------------restart Queue----------------------- ");
			System.out.println(" queue: " + queue);
			System.out.println(" -------------------------------------------------------- ");
			queue.close();
			queue = new DurableQueue("test_db/durable_test");
		}
		
	}
	
	@Test
	public void test_peek() throws Exception {
		for(int i = 0; i < 100; i++) {
			queue.offer(new byte[]{(byte)i});
		}
		
		for(int i = 0; i < 50; i++) {
			byte[] bytes = queue.poll();
			int v = bytes[0];
			assertEquals(i,v);
		}
		
		for(int i = 0; i < 50; i++) {
			byte[] bytes = queue.peek();
			int v = bytes[0];
			assertEquals(v,50);
		}
		
	}
	
	@Test
	public void test_close() throws Exception {
		int beforeStartActiveCount = Thread.activeCount();
		System.out.println("before start:"+beforeStartActiveCount);
		DurableQueue queue = new DurableQueue("test_db/durable_test_close");
		System.out.println("running:"+Thread.activeCount());
		queue.close();
		System.out.println("closed:"+Thread.activeCount());
		assertEquals(Thread.activeCount(),beforeStartActiveCount);
		
	}
	
	@Test
	public void test_clear() throws Exception {
		for(int i = 0; i < 100; i++) {
			queue.offer(String.valueOf(i).getBytes());
		}
		assertEquals("0",new String(queue.poll()));
		assertEquals(queue.size() , 99);
		queue.clear();
		assertEquals(null,queue.poll());
		assertEquals(queue.size() , 0);
	}
	
}
