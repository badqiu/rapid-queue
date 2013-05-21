/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service.impl;

import static cn.org.rapid_framework.util.holder.BeanValidatorHolder.validateWithException;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.dao.UserDao;
import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.query.UserQuery;
import com.google.code.rapid.queue.metastore.service.UserService;

/**
 * [User] 的业务操作实现类
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	protected static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	//
	// 请删除无用的方法，代码生成器只是为你生成一个架子
	//
	
	private UserDao userDao;
	/**增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,请注意大小写*/
	public void setUserDao(UserDao dao) {
		this.userDao = dao;
	}
	
	/** 
	 * 创建User
	 **/
	public User create(User user) {
	    Assert.notNull(user,"'user' must be not null");
	    initDefaultValuesForCreate(user);
	    new UserChecker().checkCreateUser(user);
	    userDao.insert(user);
	    return user;
	}
	
	/** 
	 * 更新User
	 **/	
    public User update(User user) {
        Assert.notNull(user,"'user' must be not null");
        new UserChecker().checkUpdateUser(user);
        userDao.update(user);
        return user;
    }	
    
	/** 
	 * 删除User
	 **/
    public void removeById(String username) {
        userDao.deleteById(username);
    }
    
	/** 
	 * 根据ID得到User
	 **/    
    public User getById(String username) {
        return userDao.getById(username);
    }
    
	/** 
	 * 分页查询: User
	 **/      
	@Transactional(readOnly=true)
	public Page<User> findPage(UserQuery query) {
	    Assert.notNull(query,"'query' must be not null");
		return userDao.findPage(query);
	}
	
    
	/**
	 * 为创建时初始化相关默认值 
	 **/
    public void initDefaultValuesForCreate(User user) {
    }
    
    /**
     * User的属性检查类,根据自己需要编写自定义检查
     **/
    public class UserChecker {
        /**可以在此检查只有更新才需要的特殊检查 */
        public void checkUpdateUser(User user) {
            checkUser(user);
        }
    
        /**可以在此检查只有创建才需要的特殊检查 */
        public void checkCreateUser(User user) {
            checkUser(user);
        }
        
        /** 检查到有错误请直接抛异常，不要使用 return errorCode的方式 */
        public void checkUser(User user) {
        	// Bean Validator检查,属性检查失败将抛异常
            validateWithException(user);
            
        	//复杂的属性的检查一般需要分开写几个方法，如 checkProperty1(v),checkProperty2(v)
        }
    }

	@Override
	public void auth(String username, String password) {
		User user = userDao.getById(username);
		if(user == null) {
			throw new IllegalArgumentException("username not exist,username:"+username);
		}
		String md5 = getUserPasswordMd5(username, password);
		if(!user.getPassword().equals(md5)) {
			throw new IllegalArgumentException("password error,username:"+username+" password:"+password);
		}
	}

	private String getUserPasswordMd5(String username, String password) {
		return DigestUtils.md5Hex(username+password);
	}

	@Override
	public void changePassword(String username, String oldPassword,String newPassword) {
		auth(username,oldPassword);
		updatePassword(username, newPassword);
	}

	private void updatePassword(String username, String newPassword) {
		String newPasswordMd5 = getUserPasswordMd5(username,newPassword);
		User user = userDao.getById(username);
		user.setPassword(newPasswordMd5);
		userDao.update(user);
	}
	
}
