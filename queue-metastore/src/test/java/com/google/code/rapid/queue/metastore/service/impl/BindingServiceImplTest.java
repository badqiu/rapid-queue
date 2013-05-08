/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */


package com.google.code.rapid.queue.metastore.service.impl;

import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.metastore.BindingDataFactory;
import com.google.code.rapid.queue.metastore.dao.BindingDao;
import com.google.code.rapid.queue.metastore.model.Binding;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class BindingServiceImplTest extends BaseServiceTestCase{

	//mock框架使用Mockito 具体使用请查看: http://code.google.com/p/mockito/wiki/MockitoVSEasyMock
	
	private BindingServiceImpl service = new BindingServiceImpl();
	private BindingDao bindingDao = mock(BindingDao.class);
	
	@Before
	public void setUp() {
		service.setBindingDao(bindingDao);
	}
	
	@Test
	public void test_create() {
		Binding obj = BindingDataFactory.newBinding();
		service.create(obj);
		
		verify(bindingDao).insert(obj); //验证执行了该语句
	}
	
	@Test
	public void test_update() {
		Binding obj = BindingDataFactory.newBinding();
		service.update(obj);
		
		verify(bindingDao).update(obj); //验证执行了该语句
	}
	
	@Test
	public void test_removeById() {
		service.removeById(new java.lang.String("1"),new java.lang.String("1"),new java.lang.String("1"));
		
		verify(bindingDao).deleteById(new java.lang.String("1"),new java.lang.String("1"),new java.lang.String("1")); //验证执行了该语句
	}
	
	@Test
	public void test_getById() {
		when(bindingDao.getById(new java.lang.String("1"),new java.lang.String("1"),new java.lang.String("1"))).thenReturn(BindingDataFactory.newBinding()); // mock方法调用
		
		Binding binding = service.getById(new java.lang.String("1"),new java.lang.String("1"),new java.lang.String("1"));
		
		verify(bindingDao).getById(new java.lang.String("1"),new java.lang.String("1"),new java.lang.String("1")); //验证执行了该语句
		assertNotNull(binding);
	}
	
	
}

