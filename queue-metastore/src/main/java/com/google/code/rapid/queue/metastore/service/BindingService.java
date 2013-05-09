/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service;

import java.util.List;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.query.BindingQuery;


/**
 * [Binding] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public interface BindingService {

	/** 
	 * 创建Binding
	 **/
	public Binding create(Binding binding);
	
	/** 
	 * 更新Binding
	 **/	
    public Binding update(Binding binding);
    
	/** 
	 * 删除Binding
	 **/
    public void removeById(String queueName, String exchangeName, String vhostName);
    
	/** 
	 * 根据ID得到Binding
	 **/    
    public Binding getById(String queueName, String exchangeName, String vhostName);
    
	/** 
	 * 分页查询: Binding
	 **/      
	public Page<Binding> findPage(BindingQuery query);

	public List<Binding> findBindingByVhostName(String vhostName,String exchangeName);
	
    
}
