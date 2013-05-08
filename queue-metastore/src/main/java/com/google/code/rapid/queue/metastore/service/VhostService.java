/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service;

import java.util.List;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.query.VhostQuery;


/**
 * [Vhost] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public interface VhostService {

	/** 
	 * 创建Vhost
	 **/
	public Vhost create(Vhost vhost);
	
	/** 
	 * 更新Vhost
	 **/	
    public Vhost update(Vhost vhost);
    
	/** 
	 * 删除Vhost
	 **/
    public void removeById(String vhostName);
    
	/** 
	 * 根据ID得到Vhost
	 **/    
    public Vhost getById(String vhostName);
    
	/** 
	 * 分页查询: Vhost
	 **/      
	public Page<Vhost> findPage(VhostQuery query);

	public List<Vhost> findAll();
	
    
}
