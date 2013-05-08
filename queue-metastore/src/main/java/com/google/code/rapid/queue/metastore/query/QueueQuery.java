/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.query;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import cn.org.rapid_framework.util.page.PageQuery;


/**
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class QueueQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;
    

	/** 队列名称 */
	private java.lang.String queueName;
	/** 虚拟host */
	private java.lang.String vhostName;
	/** 备注 */
	private java.lang.String remarks;
	/** 是否持久队列 */
	private Integer durable;
	/** 是否自动删除 */
	private Integer autoDelete;
	/** 自动删除的时过期时长，单位毫秒 */
	private java.lang.Long autoDeleteExpires;
	/** 是否互斥，即该队列只能有一个客户端连接 */
	private Integer exclusive;
	/** 队列当前大小 */
	private java.lang.Integer size;
	/** 队列最大大小 */
	private java.lang.Integer maxSize;
	/** time to live in queue,发送至这个队列的数据多久过期 */
	private java.lang.Long ttl;
	/** 创建时间 */
	private java.util.Date createdTimeBegin;
	private java.util.Date createdTimeEnd;
	/** 创建人 */
	private java.lang.String operator;
	/** 最后更新时间 */
	private java.util.Date lastUpdatedTimeBegin;
	private java.util.Date lastUpdatedTimeEnd;

	public java.lang.String getQueueName() {
		return this.queueName;
	}
	
	public void setQueueName(java.lang.String value) {
		this.queueName = value;
	}
	
	public java.lang.String getVhostName() {
		return this.vhostName;
	}
	
	public void setVhostName(java.lang.String value) {
		this.vhostName = value;
	}
	
	public java.lang.String getRemarks() {
		return this.remarks;
	}
	
	public void setRemarks(java.lang.String value) {
		this.remarks = value;
	}
	
	public Integer getDurable() {
		return this.durable;
	}
	
	public void setDurable(Integer value) {
		this.durable = value;
	}
	
	public Integer getAutoDelete() {
		return this.autoDelete;
	}
	
	public void setAutoDelete(Integer value) {
		this.autoDelete = value;
	}
	
	public java.lang.Long getAutoDeleteExpires() {
		return this.autoDeleteExpires;
	}
	
	public void setAutoDeleteExpires(java.lang.Long value) {
		this.autoDeleteExpires = value;
	}
	
	public Integer getExclusive() {
		return this.exclusive;
	}
	
	public void setExclusive(Integer value) {
		this.exclusive = value;
	}
	
	public java.lang.Integer getSize() {
		return this.size;
	}
	
	public void setSize(java.lang.Integer value) {
		this.size = value;
	}
	
	public java.lang.Integer getMaxSize() {
		return this.maxSize;
	}
	
	public void setMaxSize(java.lang.Integer value) {
		this.maxSize = value;
	}
	
	public java.lang.Long getTtl() {
		return this.ttl;
	}
	
	public void setTtl(java.lang.Long value) {
		this.ttl = value;
	}
	
	public java.util.Date getCreatedTimeBegin() {
		return this.createdTimeBegin;
	}
	
	public void setCreatedTimeBegin(java.util.Date value) {
		this.createdTimeBegin = value;
	}	
	
	public java.util.Date getCreatedTimeEnd() {
		return this.createdTimeEnd;
	}
	
	public void setCreatedTimeEnd(java.util.Date value) {
		this.createdTimeEnd = value;
	}
	
	public java.lang.String getOperator() {
		return this.operator;
	}
	
	public void setOperator(java.lang.String value) {
		this.operator = value;
	}
	
	public java.util.Date getLastUpdatedTimeBegin() {
		return this.lastUpdatedTimeBegin;
	}
	
	public void setLastUpdatedTimeBegin(java.util.Date value) {
		this.lastUpdatedTimeBegin = value;
	}	
	
	public java.util.Date getLastUpdatedTimeEnd() {
		return this.lastUpdatedTimeEnd;
	}
	
	public void setLastUpdatedTimeEnd(java.util.Date value) {
		this.lastUpdatedTimeEnd = value;
	}
	

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}

