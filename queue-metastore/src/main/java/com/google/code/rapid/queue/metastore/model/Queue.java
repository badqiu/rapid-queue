/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;


/**
 * tableName: rq_queue [Queue] 
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class Queue  implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;
	
	//date formats
	public static final String FORMAT_CREATED_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_LAST_UPDATED_TIME = "yyyy-MM-dd HH:mm:ss";
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
    /**
     * 队列名称       db_column: queue_name 
     */ 	
	@Length(max=50)
	private java.lang.String queueName;
	
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
     * 持久模式:memory,durable,haft_durable       db_column: durable_type 
     */ 	
	@NotBlank @Length(max=30)
	private java.lang.String durableType;
	
    /**
     * 是否自动删除       db_column: auto_delete 
     */ 	
	private boolean autoDelete;
	
    /**
     * 自动删除的时过期时长，单位毫秒       db_column: auto_delete_expires 
     */ 	
	
	private long autoDeleteExpires;
	
    /**
     * 是否互斥，即该队列只能有一个客户端连接       db_column: exclusive 
     */ 	
	private boolean exclusive;
	
    /**
     * 队列当前大小       db_column: size 
     */ 	
	private int size;
	
    /**
     * 当使用半持久模式,放在内存中的元素大小       db_column: memory_size 
     */ 	
	
	private int memorySize;
	
    /**
     * 队列最大大小       db_column: max_size 
     */ 	
	private int maxSize;
	
    /**
     * time to live in queue,发送至这个队列的数据多久过期       db_column: ttl 
     */ 	
	
	private long ttl;
	
    /**
     * 创建时间       db_column: created_time 
     */ 	
	@NotNull 
	private java.util.Date createdTime;
	
    /**
     * 创建人       db_column: operator 
     */ 	
	@NotBlank @Length(max=50)
	private java.lang.String operator;
	
    /**
     * 最后更新时间       db_column: last_updated_time 
     */ 	
	@NotNull 
	private java.util.Date lastUpdatedTime;
	
	//columns END

	public Queue(){
	}

	public Queue(
		java.lang.String queueName,
		java.lang.String vhostName
	){
		this.queueName = queueName;
		this.vhostName = vhostName;
	}

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
	
	public java.lang.String getDurableType() {
		return this.durableType;
	}
	
	public void setDurableType(java.lang.String value) {
		this.durableType = value;
	}
	
	public boolean getAutoDelete() {
		return this.autoDelete;
	}
	
	public void setAutoDelete(boolean value) {
		this.autoDelete = value;
	}
	
	public long getAutoDeleteExpires() {
		return this.autoDeleteExpires;
	}
	
	public void setAutoDeleteExpires(long value) {
		this.autoDeleteExpires = value;
	}
	
	public boolean getExclusive() {
		return this.exclusive;
	}
	
	public void setExclusive(boolean value) {
		this.exclusive = value;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void setSize(int value) {
		this.size = value;
	}
	
	public int getMemorySize() {
		return this.memorySize;
	}
	
	public void setMemorySize(int value) {
		this.memorySize = value;
	}
	
	public int getMaxSize() {
		return this.maxSize;
	}
	
	public void setMaxSize(int value) {
		this.maxSize = value;
	}
	
	public long getTtl() {
		return this.ttl;
	}
	
	public void setTtl(long value) {
		this.ttl = value;
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
			.append(getQueueName())
			.append(getVhostName())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof Queue == false) return false;
		Queue other = (Queue)obj;
		return new EqualsBuilder()
			.append(getQueueName(),other.getQueueName())
			.append(getVhostName(),other.getVhostName())
			.isEquals();
	}
}

