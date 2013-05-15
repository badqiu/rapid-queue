package com.google.code.rapid.queue.server.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerBuilder;


public class SpringContextTest extends Assert{

	@Test
	public void test() {
		MessageBrokerBuilder builder = SpringContext.getBean("messageBrokerBuilder",MessageBrokerBuilder.class);
		Map<String,MessageBroker> map = builder.build();
		assertNotNull(map);
	}
	
}
