/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore;

import java.util.Date;

import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.query.QueueQuery;


/**
 * 用于生成Queue相关数据对象的默认值
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 * 
 */
public class QueueDataFactory {
	
	public static QueueQuery newQueueQuery() {
		QueueQuery query = new QueueQuery();
		query.setPage(1);
		query.setPageSize(10);
		
	  	query.setRemarks(new String("1"));
	  	query.setDurableType(new String("1"));
	  	query.setAutoDelete(new Integer("1"));
	  	query.setAutoDeleteExpires(new Long("1"));
	  	query.setExclusive(new Integer("1"));
	  	query.setSize(new Integer("1"));
	  	query.setMemorySize(new Integer("1"));
	  	query.setMaxSize(new Integer("1"));
	  	query.setTtl(new Long("1"));
		query.setCreatedTimeBegin(new Date(System.currentTimeMillis()));
		query.setCreatedTimeEnd(new Date(System.currentTimeMillis()));
	  	query.setOperator(new String("1"));
		query.setLastUpdatedTimeBegin(new Date(System.currentTimeMillis()));
		query.setLastUpdatedTimeEnd(new Date(System.currentTimeMillis()));
		return query;
	}
	
	public static Queue newQueue() {
		Queue obj = new Queue();
		
	  	obj.setRemarks(new java.lang.String("1"));
	  	obj.setDurableType(new java.lang.String("1"));
	  	obj.setAutoDelete(new Integer("1"));
	  	obj.setAutoDeleteExpires(new java.lang.Long("1"));
	  	obj.setExclusive(new Integer("1"));
	  	obj.setSize(new java.lang.Integer("1"));
	  	obj.setMemorySize(new java.lang.Integer("1"));
	  	obj.setMaxSize(new java.lang.Integer("1"));
	  	obj.setTtl(new java.lang.Long("1"));
	  	obj.setCreatedTime(new java.util.Date(System.currentTimeMillis()));
	  	obj.setOperator(new java.lang.String("1"));
	  	obj.setLastUpdatedTime(new java.util.Date(System.currentTimeMillis()));
		return obj;
	}
}