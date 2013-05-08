/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.dao;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.query.UserQuery;

/**
 * tableName: rq_user
 * [User] 的Dao操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
*/
public interface UserDao {
	
	public void insert(User entity);
	
	public int update(User entity);

	public int deleteById(String username);
	
	public User getById(String username);
	

	public Page<User> findPage(UserQuery query);	
	
}
