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

import com.google.code.rapid.queue.metastore.dao.QueueDao;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.query.QueueQuery;
import com.google.code.rapid.queue.metastore.service.QueueService;
import static cn.org.rapid_framework.util.holder.BeanValidatorHolder.validateWithException;

/**
 * [Queue] 的业务操作实现类
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service("queueService")
@Transactional
public class QueueServiceImpl implements QueueService {

	protected static final Logger log = LoggerFactory.getLogger(QueueServiceImpl.class);
	
	//
	// 请删除无用的方法，代码生成器只是为你生成一个架子
	//
	
	private QueueDao queueDao;
	/**增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,请注意大小写*/
	public void setQueueDao(QueueDao dao) {
		this.queueDao = dao;
	}
	
	/** 
	 * 创建Queue
	 **/
	public Queue create(Queue queue) {
	    Assert.notNull(queue,"'queue' must be not null");
	    initDefaultValuesForCreate(queue);
	    new QueueChecker().checkCreateQueue(queue);
	    queueDao.insert(queue);
	    return queue;
	}
	
	/** 
	 * 更新Queue
	 **/	
    public Queue update(Queue queue) {
        Assert.notNull(queue,"'queue' must be not null");
        new QueueChecker().checkUpdateQueue(queue);
        queueDao.update(queue);
        return queue;
    }	
    
	/** 
	 * 删除Queue
	 **/
    public void removeById(String queueName, String vhostName) {
        queueDao.deleteById(queueName,vhostName);
    }
    
	/** 
	 * 根据ID得到Queue
	 **/    
    public Queue getById(String queueName, String vhostName) {
        return queueDao.getById(queueName,vhostName);
    }
    
	/** 
	 * 分页查询: Queue
	 **/      
	@Transactional(readOnly=true)
	public Page<Queue> findPage(QueueQuery query) {
	    Assert.notNull(query,"'query' must be not null");
		return queueDao.findPage(query);
	}
	
    
	/**
	 * 为创建时初始化相关默认值 
	 **/
    public void initDefaultValuesForCreate(Queue queue) {
    }
    
    /**
     * Queue的属性检查类,根据自己需要编写自定义检查
     **/
    public class QueueChecker {
        /**可以在此检查只有更新才需要的特殊检查 */
        public void checkUpdateQueue(Queue queue) {
            checkQueue(queue);
        }
    
        /**可以在此检查只有创建才需要的特殊检查 */
        public void checkCreateQueue(Queue queue) {
            checkQueue(queue);
        }
        
        /** 检查到有错误请直接抛异常，不要使用 return errorCode的方式 */
        public void checkQueue(Queue queue) {
        	// Bean Validator检查,属性检查失败将抛异常
            validateWithException(queue);
            
        	//复杂的属性的检查一般需要分开写几个方法，如 checkProperty1(v),checkProperty2(v)
        }
    }

	@Override
	public List<Queue> findByVhostName(String vhostName) {
		return queueDao.findByVhostName(vhostName);
	}

}
