package com.google.code.rapid.queue.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {
	private static ApplicationContext context;
	
	public synchronized static ApplicationContext getContext() {
		if(context == null) {
			context = new ClassPathXmlApplicationContext("classpath*:rapid_queue_spring/**/*.xml");
		}
		return context;
	}
	
	public static <T> T getBean(String beanId,Class<T> clazz) {
		return getContext().getBean(beanId,clazz);
	}
	
	public static Object getBean(String beanId) {
		return getContext().getBean(beanId);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return getContext().getBean(clazz);
	}
	
}
