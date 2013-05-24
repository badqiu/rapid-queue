package com.google.code.rapid.queue.metastore.dao.impl;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;



/**
 * 本基类主要为子类指定好要装载的spring配置文件
 * 及在运行测试前通过dbunit插入测试数据在数据库中,运行完测试删除测试数据 
 *
 * @author badqiu
 */
@ContextConfiguration(locations = {"classpath:/rapid_queue_spring/*.xml"})  
public class BaseDaoTestCase extends AbstractTransactionalJUnit4SpringContextTests{
	
	protected String[] getDbUnitDataFiles() {
		return null;
	}
	
}
