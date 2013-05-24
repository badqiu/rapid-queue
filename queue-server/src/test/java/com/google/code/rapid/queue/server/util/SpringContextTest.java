package com.google.code.rapid.queue.server.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerPoolFactoryBean;
import com.google.code.rapid.queue.MessageBrokerPool;


public class SpringContextTest extends Assert{

	@Test
	public void test_build() {
		MessageBrokerPoolFactoryBean builder = SpringContext.getBean("messageBrokerBuilder",MessageBrokerPoolFactoryBean.class);
		MessageBrokerPool pool = builder.build();
		assertNotNull(pool);
	}
	
}
