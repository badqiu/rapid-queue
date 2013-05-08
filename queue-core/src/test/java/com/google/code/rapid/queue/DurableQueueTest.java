package com.google.code.rapid.queue;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class DurableQueueTest {
	DurableQueue queue = new DurableQueue("test_db/durable_test");
	
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
}
