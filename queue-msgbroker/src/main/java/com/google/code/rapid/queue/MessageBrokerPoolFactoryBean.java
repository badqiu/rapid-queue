package com.google.code.rapid.queue;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class MessageBrokerPoolFactoryBean extends MessageBrokerPoolBuilder implements FactoryBean<MessageBrokerPool>,InitializingBean {

	@Override
	public MessageBrokerPool getObject() throws Exception {
		return build();
	}

	@Override
	public Class<MessageBrokerPool> getObjectType() {
		return MessageBrokerPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
