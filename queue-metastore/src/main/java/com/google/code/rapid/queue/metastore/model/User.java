/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */

package com.google.code.rapid.queue.metastore.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;


/**
 * tableName: rq_user [User] 
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 */
public class User  implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;
	
	//date formats
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
    /**
     * username       db_column: username 
     */ 	
	@Length(max=30)
	private java.lang.String username;
	
    /**
     * password       db_column: password 
     */ 	
	@Length(max=32)
	private java.lang.String password;
	
    /**
     * remarks       db_column: remarks 
     */ 	
	@Length(max=200)
	private java.lang.String remarks;
	
    /**
     * email       db_column: email 
     */ 	
	@Email @Length(max=30)
	private java.lang.String email;
	
    /**
     * mobile       db_column: mobile 
     */ 	
	@Length(max=20)
	private java.lang.String mobile;
	
	/**
	 * 拥有的队列权限
	 */
	private String queuePermissionList;
	
	/**
	 * 拥有的exchange权限
	 */
	private String exchangePermissionList;
	
	/**
	 * 允许后台登录
	 */
	private boolean allowWebadminLogin;
	
	//columns END

	public User(){
	}

	public User(
		java.lang.String username
	){
		this.username = username;
	}

	public java.lang.String getUsername() {
		return this.username;
	}
	
	public void setUsername(java.lang.String value) {
		this.username = value;
	}
	
	public java.lang.String getPassword() {
		return this.password;
	}
	
	public void setPassword(java.lang.String value) {
		this.password = value;
	}
	
	public java.lang.String getRemarks() {
		return this.remarks;
	}
	
	public void setRemarks(java.lang.String value) {
		this.remarks = value;
	}
	
	public java.lang.String getEmail() {
		return this.email;
	}
	
	public void setEmail(java.lang.String value) {
		this.email = value;
	}
	
	public java.lang.String getMobile() {
		return this.mobile;
	}
	
	public void setMobile(java.lang.String value) {
		this.mobile = value;
	}
	
	public String getQueuePermissionList() {
		return queuePermissionList;
	}

	public void setQueuePermissionList(String queuePermissionList) {
		this.queuePermissionList = queuePermissionList;
	}

	public String getExchangePermissionList() {
		return exchangePermissionList;
	}

	public void setExchangePermissionList(String exchangePermissionList) {
		this.exchangePermissionList = exchangePermissionList;
	}

	public boolean isAllowWebadminLogin() {
		return allowWebadminLogin;
	}

	public void setAllowWebadminLogin(boolean allowWebadminLogin) {
		this.allowWebadminLogin = allowWebadminLogin;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getUsername())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof User == false) return false;
		User other = (User)obj;
		return new EqualsBuilder()
			.append(getUsername(),other.getUsername())
			.isEquals();
	}

	public boolean hasExchangePermission(String exchange) {
		if(getExchangePermissionSet().contains("*")) {
			return true;
		}
		return getExchangePermissionSet().contains(exchange);
	}

	Set<String> exchangePermissionSet = null;
	private Set<String> getExchangePermissionSet() {
		if(exchangePermissionSet == null) {
			exchangePermissionSet = newPermissionSet(exchangePermissionList);
		}
		return exchangePermissionSet;
	}

	public boolean hasQueuePermission(String queueName) {
		if(getQueuePermissionSet().contains("*")) {
			return true;
		}
		return getQueuePermissionSet().contains(queueName);
	}
	
	Set<String> queuePermissionSet = null;
	private Set<String> getQueuePermissionSet() {
		if(queuePermissionSet == null) {
			queuePermissionSet = newPermissionSet(queuePermissionList);
		}
		return queuePermissionSet;
	}
	
	private static Set<String> newPermissionSet(String permissionList) {
		if(StringUtils.isBlank(permissionList)) {
			return new HashSet<String>();
		}
		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList(StringUtils.split(permissionList, "\r\t\n ")));
		return set;
	}
}

