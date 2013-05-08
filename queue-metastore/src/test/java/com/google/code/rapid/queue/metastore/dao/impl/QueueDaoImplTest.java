/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao.impl;

import static junit.framework.Assert.assertNotNull;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.QueueDataFactory;
import com.google.code.rapid.queue.metastore.dao.QueueDao;
import com.google.code.rapid.queue.metastore.query.QueueQuery;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class QueueDaoImplTest extends BaseDaoTestCase{
	
	@Rule public TestName testName = new TestName();
	
	private QueueDao dao;
	
	@Autowired
	public void setQueueDao(QueueDao dao) {
		this.dao = dao;
	}
	
	//数据库单元测试前会开始事务，结束时会回滚事务，所以测试方法可以不用关心测试数据的删除
	@Test
	public void findPage() {

		QueueQuery query = QueueDataFactory.newQueueQuery();
		Page page = dao.findPage(query);
		
		List resultList = (List)page.getResult();
		assertNotNull(resultList);
		
	}
	
	@Test
	public void test_insert() {
		dao.insert(QueueDataFactory.newQueue());
	}
	
	@Test
	public void test_update() {
		dao.update(QueueDataFactory.newQueue());
	}
	
	@Test
	public void test_delete() {
		dao.deleteById(new java.lang.String("1"),new java.lang.String("1"));
	}
	
	@Test
	public void test_getById() {
		dao.getById(new java.lang.String("1"),new java.lang.String("1"));
	}
	
	
}

