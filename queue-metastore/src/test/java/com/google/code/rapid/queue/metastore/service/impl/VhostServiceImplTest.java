/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */


package com.google.code.rapid.queue.metastore.service.impl;

import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.metastore.VhostDataFactory;
import com.google.code.rapid.queue.metastore.dao.VhostDao;
import com.google.code.rapid.queue.metastore.model.Vhost;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class VhostServiceImplTest extends BaseServiceTestCase{

	//mock框架使用Mockito 具体使用请查看: http://code.google.com/p/mockito/wiki/MockitoVSEasyMock
	
	private VhostServiceImpl service = new VhostServiceImpl();
	private VhostDao vhostDao = mock(VhostDao.class);
	
	@Before
	public void setUp() {
		service.setVhostDao(vhostDao);
	}
	
	@Test
	public void test_create() {
		Vhost obj = VhostDataFactory.newVhost();
		service.create(obj);
		
		verify(vhostDao).insert(obj); //验证执行了该语句
	}
	
	@Test
	public void test_update() {
		Vhost obj = VhostDataFactory.newVhost();
		service.update(obj);
		
		verify(vhostDao).update(obj); //验证执行了该语句
	}
	
	@Test
	public void test_removeById() {
		service.removeById(new java.lang.String("1"));
		
		verify(vhostDao).deleteById(new java.lang.String("1")); //验证执行了该语句
	}
	
	@Test
	public void test_getById() {
		when(vhostDao.getById(new java.lang.String("1"))).thenReturn(VhostDataFactory.newVhost()); // mock方法调用
		
		Vhost vhost = service.getById(new java.lang.String("1"));
		
		verify(vhostDao).getById(new java.lang.String("1")); //验证执行了该语句
		assertNotNull(vhost);
	}
	
	
}

