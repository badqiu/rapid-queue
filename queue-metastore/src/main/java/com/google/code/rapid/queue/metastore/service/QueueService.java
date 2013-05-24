/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service;


import java.util.List;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.query.QueueQuery;


/**
 * [Queue] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public interface QueueService {

	/** 
	 * 创建Queue
	 **/
	public Queue create(Queue queue);
	
	/** 
	 * 更新Queue
	 **/	
    public Queue update(Queue queue);
    
	/** 
	 * 删除Queue
	 **/
    public void removeById(String queueName, String vhostName);
    
	/** 
	 * 根据ID得到Queue
	 **/    
    public Queue getById(String queueName, String vhostName);
    
	/** 
	 * 分页查询: Queue
	 **/      
	public Page<Queue> findPage(QueueQuery query);
	
	public List<Queue> findByVhostName(String vhostName);
}
