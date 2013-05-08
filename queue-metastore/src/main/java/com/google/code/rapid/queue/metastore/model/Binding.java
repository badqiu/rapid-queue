/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;


/**
 * tableName: rq_binding [Binding] 
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class Binding  implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;
	
	//date formats
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
    /**
     * 队列名称       db_column: queue_name 
     */ 	
	@Length(max=50)
	private java.lang.String queueName;
	
    /**
     * 交换机名称       db_column: exchange_name 
     */ 	
	@Length(max=50)
	private java.lang.String exchangeName;
	
    /**
     * 虚拟host       db_column: vhost_name 
     */ 	
	@Length(max=50)
	private java.lang.String vhostName;
	
    /**
     * 交换机的router_key       db_column: router_key 
     */ 	
	@Length(max=3000)
	private java.lang.String routerKey;
	
    /**
     * 备注       db_column: remarks 
     */ 	
	@Length(max=200)
	private java.lang.String remarks;
	
	//columns END

	public Binding(){
	}

	public Binding(
		java.lang.String queueName,
		java.lang.String exchangeName,
		java.lang.String vhostName
	){
		this.queueName = queueName;
		this.exchangeName = exchangeName;
		this.vhostName = vhostName;
	}

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
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getQueueName())
			.append(getExchangeName())
			.append(getVhostName())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof Binding == false) return false;
		Binding other = (Binding)obj;
		return new EqualsBuilder()
			.append(getQueueName(),other.getQueueName())
			.append(getExchangeName(),other.getExchangeName())
			.append(getVhostName(),other.getVhostName())
			.isEquals();
	}
}

