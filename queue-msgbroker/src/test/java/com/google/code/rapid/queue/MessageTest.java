package com.google.code.rapid.queue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.esotericsoftware.kryo.Kryo;
import com.google.code.rapid.queue.model.Message;
import com.google.code.rapid.queue.model.MessageProperties;
import com.google.code.rapid.queue.util.JavaSerUtil;
import com.google.code.rapid.queue.util.KryoUtil;


public class MessageTest {
	
	Message m = new Message();
	public @Rule TestName testName = new TestName();
	
	@Before
	public void setUp() {
		System.out.println("\n"+testName.getMethodName());
		kryo.register(Message.class);
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

	@Test
	public void test_kryo_ser_perf() {
		
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < count; i++) {
			toBytes(m);
		}
		printTps("toBytes()",start, count);
		
		byte[] bytes = toBytes(m);
		start = System.currentTimeMillis();
		for(int i = 0; i < count; i++) {
			Message m = KryoUtil.fromBytes(bytes, Message.class);
		}
		printTps("fromBytes()",start, count);
		System.out.println(KryoUtil.fromBytes(bytes, Message.class));
	}

	@Test
	public void test_kryo_ser_list_perf() {
		List<Message> list = new ArrayList<Message>();
		for(int i = 0 ; i < 100; i++) {
			list.add(m);
		}
		
		long start = System.currentTimeMillis();
		
		for(int i = 0; i < count; i++) {
			toBytes(list);
		}
		printTps("toBytes()",start, count);
		
		byte[] bytes = toBytes(list);
		start = System.currentTimeMillis();
		for(int i = 0; i < count; i++) {
			List l = KryoUtil.fromBytes(bytes, ArrayList.class);
		}
		printTps("fromBytes()",start, count);
		System.out.println("List:"+StringUtils.join(KryoUtil.fromBytes(bytes, ArrayList.class),"\n"));
	}
	
	static Kryo kryo = new Kryo();
	private static byte[] toBytes(Object obj) {
		return KryoUtil.toBytes(obj, 5000);
	}
	
	private void printTps(String info,long start, int count) {
		long cost = System.currentTimeMillis() - start;
		long tps = (long)(count * 1000.0 / cost);
		System.out.println(info+" cost:"+cost+" count:"+count+" tps:"+tps);
	}
}
