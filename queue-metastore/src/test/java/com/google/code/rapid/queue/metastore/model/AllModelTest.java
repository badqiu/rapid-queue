package com.google.code.rapid.queue.metastore.model;

import org.junit.Assert;
import org.junit.Test;

import cn.org.rapid_framework.test.util.BeanAssert;
import cn.org.rapid_framework.test.util.BeanDefaultValueUtils;


public class AllModelTest extends Assert{
	
	@Test
	public void test() throws InstantiationException, IllegalAccessException {
		testPropertiesAndCommonMethod(Binding.class);
		testPropertiesAndCommonMethod(Exchange.class);
		testPropertiesAndCommonMethod(Queue.class);
		testPropertiesAndCommonMethod(User.class);
		testPropertiesAndCommonMethod(Vhost.class);
	}

	private static void testPropertiesAndCommonMethod(Class clazz) throws InstantiationException, IllegalAccessException {
		Object bean = clazz.newInstance();
		BeanDefaultValueUtils.setBeanProperties(bean);
		BeanAssert.assertPropertiesNotNull(bean);
		
		
		Object origian = clazz.newInstance();
		assertFalse(bean.equals(origian));
		assertFalse(bean.hashCode() == origian.hashCode());
		assertFalse(bean.toString().equals(origian.toString()));
	}
	
}
