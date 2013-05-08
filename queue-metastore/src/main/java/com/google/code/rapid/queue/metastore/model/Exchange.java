/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.model;

import javax.validation.constraints.Max;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import com.sun.istack.internal.NotNull;


/**
 * tableName: rq_exchange [Exchange] 
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class Exchange  implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;
	
	//date formats
	public static final String FORMAT_CREATED_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_LAST_UPDATED_TIME = "yyyy-MM-dd HH:mm:ss";
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
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
     * 备注       db_column: remarks 
     */ 	
	@Length(max=200)
	private java.lang.String remarks;
	
    /**
     * 是否持久性交换机       db_column: durable 
     */ 	
	@NotNull @Max(127)
	private Boolean durable;
	
    /**
     * 是否自动删除       db_column: auto_delete 
     */ 	
	@NotNull @Max(127)
	private Boolean autoDelete;
	
    /**
     * 类型: topic,fanout,direct       db_column: type 
     */ 	
	@Length(max=30)
	private java.lang.String type;
	
    /**
     * 当前交换机大小       db_column: size 
     */ 	
	
	private java.lang.Integer size;
	
    /**
     * 交换机的大小       db_column: max_size 
     */ 	
	
	private java.lang.Integer maxSize;
	
    /**
     * 创建时间       db_column: created_time 
     */ 	
	@NotNull 
	private java.util.Date createdTime;
	
    /**
     * 操作人员       db_column: operator 
     */ 	
	@Length(max=50)
	private java.lang.String operator;
	
    /**
     * 最后更新时间       db_column: last_updated_time 
     */ 	
	
	private java.util.Date lastUpdatedTime;
	
	//columns END

	public Exchange(){
	}

	public Exchange(
		java.lang.String exchangeName,
		java.lang.String vhostName
	){
		this.exchangeName = exchangeName;
		this.vhostName = vhostName;
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
	
	public java.lang.String getRemarks() {
		return this.remarks;
	}
	
	public void setRemarks(java.lang.String value) {
		this.remarks = value;
	}
	
	public Boolean getDurable() {
		return this.durable;
	}
	
	public void setDurable(Boolean value) {
		this.durable = value;
	}
	
	public Boolean getAutoDelete() {
		return this.autoDelete;
	}
	
	public void setAutoDelete(Boolean value) {
		this.autoDelete = value;
	}
	
	public java.lang.String getType() {
		return this.type;
	}
	
	public void setType(java.lang.String value) {
		this.type = value;
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
	
	public java.util.Date getCreatedTime() {
		return this.createdTime;
	}
	
	public void setCreatedTime(java.util.Date value) {
		this.createdTime = value;
	}
	
	public java.lang.String getOperator() {
		return this.operator;
	}
	
	public void setOperator(java.lang.String value) {
		this.operator = value;
	}
	
	public java.util.Date getLastUpdatedTime() {
		return this.lastUpdatedTime;
	}
	
	public void setLastUpdatedTime(java.util.Date value) {
		this.lastUpdatedTime = value;
	}
	

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getExchangeName())
			.append(getVhostName())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof Exchange == false) return false;
		Exchange other = (Exchange)obj;
		return new EqualsBuilder()
			.append(getExchangeName(),other.getExchangeName())
			.append(getVhostName(),other.getVhostName())
			.isEquals();
	}
}

