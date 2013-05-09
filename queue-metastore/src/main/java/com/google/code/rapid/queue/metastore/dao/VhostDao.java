/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao;

import java.util.List;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.query.VhostQuery;

/**
 * tableName: rq_vhost
 * [Vhost] 的Dao操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public interface VhostDao {
	
	public void insert(Vhost entity);
	
	public int update(Vhost entity);

	public int deleteById(String vhostName);
	
	public Vhost getById(String vhostName);

	public Page<Vhost> findPage(VhostQuery query);

	public List<Vhost> findAll();	
	
}
