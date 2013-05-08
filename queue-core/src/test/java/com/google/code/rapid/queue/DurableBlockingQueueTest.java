package com.google.code.rapid.queue;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class DurableBlockingQueueTest {

	DurableBlockingQueue queue = new DurableBlockingQueue("test_db/durable_blocking_test");
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
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		pollT.start();
		
		for(int i = 0; i < 10; i++) {
			queue.put(("input-"+i).getBytes());
			Thread.sleep(300);
		}
		
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
		
		for(int i = 0; i < 1000; i++) {
			Thread.sleep(1000 * 10);
			
			System.out.println(i + " ----------------restart Queue-----------------------");
			queue.close();
			queue = new DurableBlockingQueue("test_db/durable_blocking_test");
		}
		
	}
}
