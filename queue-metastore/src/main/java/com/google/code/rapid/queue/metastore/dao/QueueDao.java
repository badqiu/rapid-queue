/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.query.QueueQuery;

/**
 * tableName: rq_queue
 * [Queue] 的Dao操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public interface QueueDao {
	
	public void insert(Queue entity);
	
	public int update(Queue entity);

	public int deleteById(String queueName, String vhostName);
	
	public Queue getById(String queueName, String vhostName);
	

	public Page<Queue> findPage(QueueQuery query);	
	
}
