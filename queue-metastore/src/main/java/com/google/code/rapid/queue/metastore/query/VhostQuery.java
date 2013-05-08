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
public class VhostQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;
    

	/** 虚拟host */
	private java.lang.String vhostName;
	/** 备注 */
	private java.lang.String remarks;
	/** 实际部署的主机 */
	private java.lang.String host;

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
	
}

