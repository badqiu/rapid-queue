/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.dao.BindingDao;
import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.query.BindingQuery;
import com.google.code.rapid.queue.metastore.service.BindingService;
import static cn.org.rapid_framework.util.holder.BeanValidatorHolder.validateWithException;

/**
 * [Binding] 的业务操作实现类
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service("bindingService")
@Transactional
public class BindingServiceImpl implements BindingService {

	protected static final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);
	
	//
	// 请删除无用的方法，代码生成器只是为你生成一个架子
	//
	
	private BindingDao bindingDao;
	/**增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,请注意大小写*/
	public void setBindingDao(BindingDao dao) {
		this.bindingDao = dao;
	}
	
	/** 
	 * 创建Binding
	 **/
	public Binding create(Binding binding) {
	    Assert.notNull(binding,"'binding' must be not null");
	    initDefaultValuesForCreate(binding);
	    new BindingChecker().checkCreateBinding(binding);
	    bindingDao.insert(binding);
	    return binding;
	}
	
	/** 
	 * 更新Binding
	 **/	
    public Binding update(Binding binding) {
        Assert.notNull(binding,"'binding' must be not null");
        new BindingChecker().checkUpdateBinding(binding);
        bindingDao.update(binding);
        return binding;
    }	
    
	/** 
	 * 删除Binding
	 **/
    public void removeById(String queueName, String exchangeName, String vhostName) {
        bindingDao.deleteById(queueName,exchangeName,vhostName);
    }
    
	/** 
	 * 根据ID得到Binding
	 **/    
    public Binding getById(String queueName, String exchangeName, String vhostName) {
        return bindingDao.getById(queueName,exchangeName,vhostName);
    }
    
	/** 
	 * 分页查询: Binding
	 **/      
	@Transactional(readOnly=true)
	public Page<Binding> findPage(BindingQuery query) {
	    Assert.notNull(query,"'query' must be not null");
		return bindingDao.findPage(query);
	}
	
    
	/**
	 * 为创建时初始化相关默认值 
	 **/
    public void initDefaultValuesForCreate(Binding binding) {
    }
    
    /**
     * Binding的属性检查类,根据自己需要编写自定义检查
     **/
    public class BindingChecker {
        /**可以在此检查只有更新才需要的特殊检查 */
        public void checkUpdateBinding(Binding binding) {
            checkBinding(binding);
        }
    
        /**可以在此检查只有创建才需要的特殊检查 */
        public void checkCreateBinding(Binding binding) {
            checkBinding(binding);
        }
        
        /** 检查到有错误请直接抛异常，不要使用 return errorCode的方式 */
        public void checkBinding(Binding binding) {
        	// Bean Validator检查,属性检查失败将抛异常
            validateWithException(binding);
            
        	//复杂的属性的检查一般需要分开写几个方法，如 checkProperty1(v),checkProperty2(v)
        }
    }
}
