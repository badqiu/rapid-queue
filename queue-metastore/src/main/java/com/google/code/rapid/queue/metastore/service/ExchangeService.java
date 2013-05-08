/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service;

import java.util.List;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.query.ExchangeQuery;


/**
 * [Exchange] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public interface ExchangeService {

	/** 
	 * 创建Exchange
	 **/
	public Exchange create(Exchange exchange);
	
	/** 
	 * 更新Exchange
	 **/	
    public Exchange update(Exchange exchange);
    
	/** 
	 * 删除Exchange
	 **/
    public void removeById(String exchangeName, String vhostName);
    
	/** 
	 * 根据ID得到Exchange
	 **/    
    public Exchange getById(String exchangeName, String vhostName);
    
	/** 
	 * 分页查询: Exchange
	 **/      
	public Page<Exchange> findPage(ExchangeQuery query);

	public List<Exchange> findByVhostName(String vhostName);
	
    
}
