/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore;

import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.query.VhostQuery;


/**
 * 用于生成Vhost相关数据对象的默认值
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 * 
 */
public class VhostDataFactory {
	
	public static VhostQuery newVhostQuery() {
		VhostQuery query = new VhostQuery();
		query.setPage(1);
		query.setPageSize(10);
		
	  	query.setRemarks(new String("1"));
	  	query.setHost(new String("1"));
		return query;
	}
	
	public static Vhost newVhost() {
		Vhost obj = new Vhost();
		obj.setVhostName("1");
	  	obj.setRemarks(new java.lang.String("1"));
	  	obj.setHost(new java.lang.String("1"));
		return obj;
	}
}