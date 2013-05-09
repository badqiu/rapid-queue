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

import com.google.code.rapid.queue.metastore.dao.ExchangeDao;
import com.google.code.rapid.queue.metastore.model.Exchange;
import com.google.code.rapid.queue.metastore.query.ExchangeQuery;
import com.google.code.rapid.queue.metastore.service.ExchangeService;
import static cn.org.rapid_framework.util.holder.BeanValidatorHolder.validateWithException;

/**
 * [Exchange] 的业务操作实现类
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service("exchangeService")
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

	protected static final Logger log = LoggerFactory.getLogger(ExchangeServiceImpl.class);
	
	//
	// 请删除无用的方法，代码生成器只是为你生成一个架子
	//
	
	private ExchangeDao exchangeDao;
	/**增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,请注意大小写*/
	public void setExchangeDao(ExchangeDao dao) {
		this.exchangeDao = dao;
	}
	
	/** 
	 * 创建Exchange
	 **/
	public Exchange create(Exchange exchange) {
	    Assert.notNull(exchange,"'exchange' must be not null");
	    initDefaultValuesForCreate(exchange);
	    new ExchangeChecker().checkCreateExchange(exchange);
	    exchangeDao.insert(exchange);
	    return exchange;
	}
	
	/** 
	 * 更新Exchange
	 **/	
    public Exchange update(Exchange exchange) {
        Assert.notNull(exchange,"'exchange' must be not null");
        new ExchangeChecker().checkUpdateExchange(exchange);
        exchangeDao.update(exchange);
        return exchange;
    }	
    
	/** 
	 * 删除Exchange
	 **/
    public void removeById(String exchangeName, String vhostName) {
        exchangeDao.deleteById(exchangeName,vhostName);
    }
    
	/** 
	 * 根据ID得到Exchange
	 **/    
    public Exchange getById(String exchangeName, String vhostName) {
        return exchangeDao.getById(exchangeName,vhostName);
    }
    
	/** 
	 * 分页查询: Exchange
	 **/      
	@Transactional(readOnly=true)
	public Page<Exchange> findPage(ExchangeQuery query) {
	    Assert.notNull(query,"'query' must be not null");
		return exchangeDao.findPage(query);
	}
	
    
	/**
	 * 为创建时初始化相关默认值 
	 **/
    public void initDefaultValuesForCreate(Exchange exchange) {
    }
    
    /**
     * Exchange的属性检查类,根据自己需要编写自定义检查
     **/
    public class ExchangeChecker {
        /**可以在此检查只有更新才需要的特殊检查 */
        public void checkUpdateExchange(Exchange exchange) {
            checkExchange(exchange);
        }
    
        /**可以在此检查只有创建才需要的特殊检查 */
        public void checkCreateExchange(Exchange exchange) {
            checkExchange(exchange);
        }
        
        /** 检查到有错误请直接抛异常，不要使用 return errorCode的方式 */
        public void checkExchange(Exchange exchange) {
        	// Bean Validator检查,属性检查失败将抛异常
            validateWithException(exchange);
            
        	//复杂的属性的检查一般需要分开写几个方法，如 checkProperty1(v),checkProperty2(v)
        }
    }

	@Override
	public List<Exchange> findByVhostName(String vhostName) {
		return exchangeDao.findByVhostName(vhostName);
	}
}
