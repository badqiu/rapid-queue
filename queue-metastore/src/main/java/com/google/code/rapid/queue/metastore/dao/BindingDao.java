/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao;

import java.util.List;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.query.BindingQuery;

/**
 * tableName: rq_binding
 * [Binding] 的Dao操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public interface BindingDao {
	
	public void insert(Binding entity);
	
	public int update(Binding entity);

	public int deleteById(String queueName, String exchangeName, String vhostName);
	
	public Binding getById(String queueName, String exchangeName, String vhostName);

	public Page<Binding> findPage(BindingQuery query);

	public List<Binding> findBindingByVhostName(String vhostName,
			String exchangeName);	
	
}
