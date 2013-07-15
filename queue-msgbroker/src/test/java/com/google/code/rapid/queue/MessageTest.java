package com.google.code.rapid.queue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.google.code.rapid.queue.model.Message;
import com.google.code.rapid.queue.model.MessageProperties;
import com.google.code.rapid.queue.util.JavaSerUtil;


public class MessageTest {
	
	Message m = new Message();
	public @Rule TestName testName = new TestName();
	
	@Before
	public void setUp() {
		System.out.println("\n"+testName.getMethodName());
		m.setExchange("exchange");
		m.setRouterKey("routerKey");
		m.setBody(StringUtils.repeat("a", 4024).getBytes());
		
		m.setMessageProperties(new MessageProperties());		
	}
	
	int count = 100000;
	@Test
	public void test_java_ser_perf() {
		
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < count; i++) {
			byte[] b = JavaSerUtil.toBytes(m,5000);
		}
		printTps("toBytes()",start, count);
		
		byte[] bytes = JavaSerUtil.toBytes(m,5000);
		start = System.currentTimeMillis();
		for(int i = 0; i < count; i++) {
			Message msg = (Message)JavaSerUtil.fromBytes(bytes);
		}
		printTps("fromBytes()",start, count);
	}
	
	private void printTps(String info,long start, int count) {
		long cost = System.currentTimeMillis() - start;
		long tps = (long)(count * 1000.0 / cost);
		System.out.println(info+" cost:"+cost+" count:"+count+" tps:"+tps);
	}
}
