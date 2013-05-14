package com.google.code.rapid.queue.server.util;

import java.util.Map;

import org.junit.Test;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerBuilder;


public class SpringContextTest {

	@Test
	public void test() {
		MessageBrokerBuilder builder = SpringContext.getBean("messageBrokerBuilder",MessageBrokerBuilder.class);
		Map<String,MessageBroker> map = builder.build();
	}
}
