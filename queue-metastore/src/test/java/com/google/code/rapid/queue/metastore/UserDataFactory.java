/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore;

import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.query.UserQuery;


/**
 * 用于生成User相关数据对象的默认值
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 * 
 */
public class UserDataFactory {
	
	public static UserQuery newUserQuery() {
		UserQuery query = new UserQuery();
		query.setPage(1);
		query.setPageSize(10);
		
	  	query.setPassword(new String("1"));
	  	query.setRemarks(new String("1"));
	  	query.setEmail(new String("1"));
	  	query.setMobile(new String("1"));
		return query;
	}
	
	public static User newUser() {
		User obj = new User();
		obj.setUsername("1");
	  	obj.setPassword(new java.lang.String("1"));
	  	obj.setRemarks(new java.lang.String("1"));
	  	obj.setEmail("badqiu@gmail.com");
	  	obj.setMobile(new java.lang.String("1"));
		return obj;
	}
}