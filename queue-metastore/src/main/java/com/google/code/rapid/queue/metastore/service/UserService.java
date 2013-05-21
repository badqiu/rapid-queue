/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.query.UserQuery;


/**
 * [User] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public interface UserService {

	public void auth(String username,String password);
	
	
	public void changePassword(String username,String oldPassword,String newPassword);
	
	/** 
	 * 创建User
	 **/
	public User create(User user);
	
	/** 
	 * 更新User
	 **/	
    public User update(User user);
    
	/** 
	 * 删除User
	 **/
    public void removeById(String username);
    
	/** 
	 * 根据ID得到User
	 **/    
    public User getById(String username);
    
	/** 
	 * 分页查询: User
	 **/      
	public Page<User> findPage(UserQuery query);
	
    
}
