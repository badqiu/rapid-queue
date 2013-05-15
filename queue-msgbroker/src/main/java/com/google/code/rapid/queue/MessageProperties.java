package com.google.code.rapid.queue;

import java.util.Date;
import java.util.Map;

public class MessageProperties {
	/** delivery mode constants */
	public static final int DELIVERY_MODE_PERSISTENT = 1;

	public static final int DELIVERY_MODE_NON_PERSISTENT = 2;

	public static final int DELIVERY_MODE_HALF_PERSISTENT = 3;

	/** priority constants */
	public static final int PRIORITY_VERY_LOW = 1;

	public static final int PRIORITY_LOW = 3;

	public static final int PRIORITY_NORMAL = 5;

	public static final int PRIORITY_HIGH = 8;

	public static final int PRIORITY_VERY_HIGH = 10;

	/** content type constants */
	public static final String CONTENT_TYPE_BYTES = "application/octet-stream";

	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

	public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

	public static final String CONTENT_TYPE_JSON = "application/json";

	private String messageId = null; // 消息ID
	private Integer priority = PRIORITY_NORMAL; // 优先级
	private String contentType = CONTENT_TYPE_BYTES; // json,xml,text,
	private String contentEncoding = null; // content encoding,value=UTF8,GBK
											// ...
	private long contentLength;
	private long expiration = -1; // 过期时间，单位毫秒
	private Date timestamp; // 消息生成时间

	private int deliveryMode = DELIVERY_MODE_PERSISTENT; // 消息投递模式

	private String sourceIp; // 发送消息的来源IP
	private String sourceApp; // 发送消息的来源App
	private String sourceUser; // 发送消息的来源User

	private String vhost;
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getDeliveryMode() {
		return deliveryMode;
	}

	public void setDeliveryMode(int deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}
	
}
