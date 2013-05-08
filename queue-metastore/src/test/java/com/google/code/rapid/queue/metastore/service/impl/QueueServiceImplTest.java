/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */


package com.google.code.rapid.queue.metastore.service.impl;

import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.metastore.QueueDataFactory;
import com.google.code.rapid.queue.metastore.dao.QueueDao;
import com.google.code.rapid.queue.metastore.model.Queue;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class QueueServiceImplTest extends BaseServiceTestCase{

	//mock框架使用Mockito 具体使用请查看: http://code.google.com/p/mockito/wiki/MockitoVSEasyMock
	
	private QueueServiceImpl service = new QueueServiceImpl();
	private QueueDao queueDao = mock(QueueDao.class);
	
	@Before
	public void setUp() {
		service.setQueueDao(queueDao);
	}
	
	@Test
	public void test_create() {
		Queue obj = QueueDataFactory.newQueue();
		service.create(obj);
		
		verify(queueDao).insert(obj); //验证执行了该语句
	}
	
	@Test
	public void test_update() {
		Queue obj = QueueDataFactory.newQueue();
		service.update(obj);
		
		verify(queueDao).update(obj); //验证执行了该语句
	}
	
	@Test
	public void test_removeById() {
		service.removeById(new java.lang.String("1"),new java.lang.String("1"));
		
		verify(queueDao).deleteById(new java.lang.String("1"),new java.lang.String("1")); //验证执行了该语句
	}
	
	@Test
	public void test_getById() {
		when(queueDao.getById(new java.lang.String("1"),new java.lang.String("1"))).thenReturn(QueueDataFactory.newQueue()); // mock方法调用
		
		Queue queue = service.getById(new java.lang.String("1"),new java.lang.String("1"));
		
		verify(queueDao).getById(new java.lang.String("1"),new java.lang.String("1")); //验证执行了该语句
		assertNotNull(queue);
	}
	
	
}

