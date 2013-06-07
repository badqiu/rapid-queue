package com.google.code.rapid.queue;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BufferedBlockingQueueTest extends Assert{

	BlockingQueue persistence = new DurableBlockingQueue("target/test_db/half_durable_block_queue");
	BufferedBlockingQueue<byte[]> q = new BufferedBlockingQueue<byte[]>(100,persistence);
	
	@Before
	public void setUp() {
		persistence.clear();
	}
	
	@Test
	public void test() {
		for(int i = 0; i < 10; i++) {
			offerAndPoll();
		}
	}

	private void offerAndPoll() {
		for(int i = 0; i < 127; i++) {
			byte[] offet = new byte[]{(byte)i};
			q.offer(offet);
			System.out.println("offered="+Arrays.toString(offet));
		}
		
		assertEquals(q.buffer.size(),100);
		assertEquals(q.target.size(),27);
		
		
		for(int i = 0; i < 127; i++) {
			byte[] b = q.poll();
			byte v = b[0];
			assertEquals("error on i="+i+" on bytes="+Arrays.toString(b),v,i);
		}
	}
}
