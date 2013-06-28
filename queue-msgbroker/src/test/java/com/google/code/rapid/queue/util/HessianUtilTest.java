package com.google.code.rapid.queue.util;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import cn.org.rapid_framework.test.util.MultiThreadTestUtils;
import cn.org.rapid_framework.util.Profiler;

import com.google.code.rapid.queue.model.Message;


public class HessianUtilTest extends Assert{
	
	int count = 50000;
	@Rule public TestName testName = new TestName();
	@Before
	public void setUp() {
		Profiler.start(testName.getMethodName());
	}
	
	@After
	public void tearDown() {
		Profiler.printDump();
	}
	
	@Test
	public void test() throws InterruptedException {
		
		for(int i = 0; i < count; i++) {
			byte[] bytes = HessianUtil.toBytes("100", 10);
			String str = (String)HessianUtil.fromBytes(bytes,String.class);
			assertEquals(str,"100");
		}
		
		Profiler.release(count);
		
	}
	@Test
	public void testMessage() throws InterruptedException {
		Message msg = new Message();
		msg.setBody(RandomStringUtils.randomAlphabetic(1024).getBytes());
		for(int i = 0; i < count; i++) {
			byte[] bytes = HessianUtil.toBytes(msg, 10);
			Message from = HessianUtil.fromBytes(bytes,Message.class);
		}
		
		Profiler.release(count);
		System.out.println("HessianUtil:"+HessianUtil.toBytes(msg, 10).length);
		System.out.println("JavaSerUtil:"+JavaSerUtil.toBytes(msg, 10).length);
		
	}
	@Test
	public void testMultiThreads() throws InterruptedException {
		MultiThreadTestUtils.executeAndWait(10, new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0; i < count; i++) {
					byte[] bytes = HessianUtil.toBytes("100", 10);
					String str = (String)HessianUtil.fromBytes(bytes,String.class);
					assertEquals(str,"100");
				}
			}
		});
		Profiler.release(count * 10);
	}
}
