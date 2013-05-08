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
 * tableName: rq_vhost [Vhost] 
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class Vhost  implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;
	
	//date formats
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
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
     * 实际部署的主机       db_column: host 
     */ 	
	@Length(max=50)
	private java.lang.String host;
	
	//columns END

	public Vhost(){
	}

	public Vhost(
		java.lang.String vhostName
	){
		this.vhostName = vhostName;
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
	
	public java.lang.String getHost() {
		return this.host;
	}
	
	public void setHost(java.lang.String value) {
		this.host = value;
	}
	

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getVhostName())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof Vhost == false) return false;
		Vhost other = (Vhost)obj;
		return new EqualsBuilder()
			.append(getVhostName(),other.getVhostName())
			.isEquals();
	}
}

