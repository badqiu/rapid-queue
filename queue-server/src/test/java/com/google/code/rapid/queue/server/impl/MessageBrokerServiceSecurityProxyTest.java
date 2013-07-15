package com.google.code.rapid.queue.server.impl;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.server.ThriftContext;
import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService.Iface;


public class MessageBrokerServiceSecurityProxyTest extends Mockito{

	MessageBrokerServiceSecurityProxy proxy = new MessageBrokerServiceSecurityProxy(mock(Iface.class));
	@Before
	public void setUp() {
		ThriftContext.setServerContext(new HashMap<String,Object>());
	}
	
	@Test(expected=MessageBrokerException.class)
	public void testNeedLogin() throws MessageBrokerException, TException {
		System.out.println(Arrays.toString(StringUtils.split("1111  2222   \n bbbb", "\r\t\n ")));
		
		proxy.receive("aaa", 100);
	}
	
	@Test(expected=MessageBrokerException.class)
	public void testNoPermission() throws MessageBrokerException, TException {
		User user = new User();
		ThriftContext.getServerContext().put(MessageBrokerServiceImpl.LOGIN_USER_KEY, user);
		proxy.receive("aaa", 100);
	}
	
	@Test()
	public void testQueueHasPermission() throws MessageBrokerException, TException {
		User user = new User();
		user.setQueuePermissionList("aaa \n\r bbb \t ccc");
		ThriftContext.getServerContext().put(MessageBrokerServiceImpl.LOGIN_USER_KEY, user);
		proxy.receive("aaa", 100);
		proxy.receive("bbb", 100);
		proxy.receive("ccc", 100);
	}
	
	@Test(expected=MessageBrokerException.class)
	public void testExchangeNoPermission() throws MessageBrokerException, TException {
		User user = new User();
		user.setExchangePermissionList("aaa \n\r bbb \t ccc");
		ThriftContext.getServerContext().put(MessageBrokerServiceImpl.LOGIN_USER_KEY, user);
		Message msg = new Message();
		msg.setExchange("ex");
		proxy.send(msg);
	}
	
	@Test()
	public void testExchangeHasPermission() throws MessageBrokerException, TException {
		User user = new User();
		user.setExchangePermissionList("aaa \n\r bbb \t ccc");
		ThriftContext.getServerContext().put(MessageBrokerServiceImpl.LOGIN_USER_KEY, user);
		Message msg = new Message();
		msg.setExchange("aaa");
		proxy.send(msg);
	}
}
