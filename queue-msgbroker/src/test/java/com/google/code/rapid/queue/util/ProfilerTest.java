package com.google.code.rapid.queue.util;


import org.junit.Assert;
import org.junit.Test;

import com.google.code.rapid.queue.util.Profiler.Dur;


public class ProfilerTest extends Assert{
	@Test
	public void test() throws InterruptedException {
		for(int i = 0; i < 10; i++) {
			Profiler.enter("sleep_test");
			Thread.sleep(100);
			Profiler.release();
		}
		
		Profiler.enter("timeout_test");
		Thread.sleep(200);
		Profiler.release();
		
		System.out.println(Profiler.dumpAllDur());
		Dur sleepTest = Profiler.all.get("sleep_test");
		
		assertTrue(sleepTest.getCost() > 999 && sleepTest.getCost() < 1100);
		assertTrue(sleepTest.getTps() >= 9 && sleepTest.getTps() <= 10);
		assertEquals(sleepTest.getCount() , 10);
		assertEquals(sleepTest.getMessageId(),"sleep_test");
	}
	
}
