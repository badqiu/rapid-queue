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
public class BindingQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;
    

	/** 队列名称 */
	private java.lang.String queueName;
	/** 交换机名称 */
	private java.lang.String exchangeName;
	/** 虚拟host */
	private java.lang.String vhostName;
	/** 交换机的router_key */
	private java.lang.String routerKey;
	/** 备注 */
	private java.lang.String remarks;

	public java.lang.String getQueueName() {
		return this.queueName;
	}
	
	public void setQueueName(java.lang.String value) {
		this.queueName = value;
	}
	
	public java.lang.String getExchangeName() {
		return this.exchangeName;
	}
	
	public void setExchangeName(java.lang.String value) {
		this.exchangeName = value;
	}
	
	public java.lang.String getVhostName() {
		return this.vhostName;
	}
	
	public void setVhostName(java.lang.String value) {
		this.vhostName = value;
	}
	
	public java.lang.String getRouterKey() {
		return this.routerKey;
	}
	
	public void setRouterKey(java.lang.String value) {
		this.routerKey = value;
	}
	
	public java.lang.String getRemarks() {
		return this.remarks;
	}
	
	public void setRemarks(java.lang.String value) {
		this.remarks = value;
	}
	

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}

