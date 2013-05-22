/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */


package com.google.code.rapid.queue.metastore.service.impl;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.code.rapid.queue.metastore.UserDataFactory;
import com.google.code.rapid.queue.metastore.dao.UserDao;
import com.google.code.rapid.queue.metastore.model.User;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class UserServiceImplTest extends BaseServiceTestCase{

	//mock框架使用Mockito 具体使用请查看: http://code.google.com/p/mockito/wiki/MockitoVSEasyMock
	
	private UserServiceImpl service = new UserServiceImpl();
	private UserDao userDao = mock(UserDao.class);
	
	@Before
	public void setUp() {
		service.setUserDao(userDao);
	}
	
	@Test
	public void test_create() {
		User obj = UserDataFactory.newUser();
		service.create(obj);
		
		verify(userDao).insert(obj); //验证执行了该语句
	}
	
	@Test
	public void test_update() {
		User obj = UserDataFactory.newUser();
		service.update(obj);
		
		verify(userDao).update(obj); //验证执行了该语句
	}
	
	@Test
	public void test_removeById() {
		service.removeById(new java.lang.String("1"));
		
		verify(userDao).deleteById(new java.lang.String("1")); //验证执行了该语句
	}
	
	@Test
	public void test_getById() {
		when(userDao.getById(new java.lang.String("1"))).thenReturn(UserDataFactory.newUser()); // mock方法调用
		
		User user = service.getById(new java.lang.String("1"));
		
		verify(userDao).getById(new java.lang.String("1")); //验证执行了该语句
		assertNotNull(user);
	}
	
	@Test
	public void test_getUserPasswordMd5() {
		String md5 = UserServiceImpl.getUserPasswordMd5("admin", "admin");
		System.out.println(md5);
		assertEquals("f6fdffe48c908deb0f4c3bd36c032e72",md5);
	}
	
}

