package com.google.code.rapid.queue.metastore.service.impl;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import cn.org.rapid_framework.util.holder.BeanValidatorHolder;

/**
 * 
 * 本基类主要为子类指定好要装载的spring配置文件
 * 及在运行测试前通过dbunit插入测试数据在数据库中,运行完测试删除测试数据
 *
 * @author badqiu
 * 请设置好要装载的spring配置文件,一般开发数据库与测试数据库分开
 * 所以你要装载的资源文件应改为"classpath:/spring/*-test-resource.xml"
 */
public class BaseServiceTestCase extends Mockito{
	@Before
	public void init() {
		LocalValidatorFactoryBean factory  = new LocalValidatorFactoryBean();
		factory.afterPropertiesSet();
		new BeanValidatorHolder().setValidator(factory.getValidator());
	}
	@After
	public void tearDown() {
		new BeanValidatorHolder().cleanHolder();
	}
}
