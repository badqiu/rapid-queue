/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.org.rapid_framework.page.Page;

import com.google.code.rapid.queue.metastore.dao.VhostDao;
import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.query.VhostQuery;
import com.google.code.rapid.queue.metastore.service.VhostService;

import static cn.org.rapid_framework.util.holder.BeanValidatorHolder.validateWithException;

/**
 * [Vhost] 的业务操作实现类
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service("vhostService")
@Transactional
public class VhostServiceImpl implements VhostService {

	protected static final Logger log = LoggerFactory.getLogger(VhostServiceImpl.class);
	
	//
	// 请删除无用的方法，代码生成器只是为你生成一个架子
	//
	
	private VhostDao vhostDao;
	/**增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,请注意大小写*/
	public void setVhostDao(VhostDao dao) {
		this.vhostDao = dao;
	}
	
	/** 
	 * 创建Vhost
	 **/
	public Vhost create(Vhost vhost) {
	    Assert.notNull(vhost,"'vhost' must be not null");
	    initDefaultValuesForCreate(vhost);
	    new VhostChecker().checkCreateVhost(vhost);
	    vhostDao.insert(vhost);
	    return vhost;
	}
	
	/** 
	 * 更新Vhost
	 **/	
    public Vhost update(Vhost vhost) {
        Assert.notNull(vhost,"'vhost' must be not null");
        new VhostChecker().checkUpdateVhost(vhost);
        vhostDao.update(vhost);
        return vhost;
    }	
    
	/** 
	 * 删除Vhost
	 **/
    public void removeById(String vhostName) {
        vhostDao.deleteById(vhostName);
    }
    
	/** 
	 * 根据ID得到Vhost
	 **/    
    public Vhost getById(String vhostName) {
        return vhostDao.getById(vhostName);
    }
    
	/** 
	 * 分页查询: Vhost
	 **/      
	@Transactional(readOnly=true)
	public Page<Vhost> findPage(VhostQuery query) {
	    Assert.notNull(query,"'query' must be not null");
		return vhostDao.findPage(query);
	}
	
    
	/**
	 * 为创建时初始化相关默认值 
	 **/
    public void initDefaultValuesForCreate(Vhost vhost) {
    }
    
    /**
     * Vhost的属性检查类,根据自己需要编写自定义检查
     **/
    public class VhostChecker {
        /**可以在此检查只有更新才需要的特殊检查 */
        public void checkUpdateVhost(Vhost vhost) {
            checkVhost(vhost);
        }
    
        /**可以在此检查只有创建才需要的特殊检查 */
        public void checkCreateVhost(Vhost vhost) {
            checkVhost(vhost);
        }
        
        /** 检查到有错误请直接抛异常，不要使用 return errorCode的方式 */
        public void checkVhost(Vhost vhost) {
        	// Bean Validator检查,属性检查失败将抛异常
            validateWithException(vhost);
            
        	//复杂的属性的检查一般需要分开写几个方法，如 checkProperty1(v),checkProperty2(v)
        }
    }

	@Override
	public List<Vhost> findAll() {
		return vhostDao.findAll();
	}
}
