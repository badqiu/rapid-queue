/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */


package com.google.code.rapid.queue.metastore.service.impl;

import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.metastore.ExchangeDataFactory;
import com.google.code.rapid.queue.metastore.dao.ExchangeDao;
import com.google.code.rapid.queue.metastore.model.Exchange;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class ExchangeServiceImplTest extends BaseServiceTestCase{

	//mock框架使用Mockito 具体使用请查看: http://code.google.com/p/mockito/wiki/MockitoVSEasyMock
	
	private ExchangeServiceImpl service = new ExchangeServiceImpl();
	private ExchangeDao exchangeDao = mock(ExchangeDao.class);
	
	@Before
	public void setUp() {
		service.setExchangeDao(exchangeDao);
	}
	
	@Test
	public void test_create() {
		Exchange obj = ExchangeDataFactory.newExchange();
		service.create(obj);
		
		verify(exchangeDao).insert(obj); //验证执行了该语句
	}
	
	@Test
	public void test_update() {
		Exchange obj = ExchangeDataFactory.newExchange();
		service.update(obj);
		
		verify(exchangeDao).update(obj); //验证执行了该语句
	}
	
	@Test
	public void test_removeById() {
		service.removeById(new java.lang.String("1"),new java.lang.String("1"));
		
		verify(exchangeDao).deleteById(new java.lang.String("1"),new java.lang.String("1")); //验证执行了该语句
	}
	
	@Test
	public void test_getById() {
		when(exchangeDao.getById(new java.lang.String("1"),new java.lang.String("1"))).thenReturn(ExchangeDataFactory.newExchange()); // mock方法调用
		
		Exchange exchange = service.getById(new java.lang.String("1"),new java.lang.String("1"));
		
		verify(exchangeDao).getById(new java.lang.String("1"),new java.lang.String("1")); //验证执行了该语句
		assertNotNull(exchange);
	}
	
	
}

