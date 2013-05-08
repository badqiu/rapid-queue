/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.query.ExchangeQuery;

/**
 * tableName: rq_exchange
 * [Exchange] 的Dao操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public interface ExchangeDao {
	
	public void insert(Exchange entity);
	
	public int update(Exchange entity);

	public int deleteById(String exchangeName, String vhostName);
	
	public Exchange getById(String exchangeName, String vhostName);
	

	public Page<Exchange> findPage(ExchangeQuery query);	
	
}
