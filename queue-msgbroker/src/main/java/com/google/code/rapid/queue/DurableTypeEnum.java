package com.google.code.rapid.queue;

public enum DurableTypeEnum {
	MEMORY("内存模式"),
	DURABLE("持久模式"),
	HALF_DURABLE("半持久模式");
	
	DurableTypeEnum(String desc) {
		this.desc = desc;
	}
	
	private String desc;
	public String getDesc() {
		return desc;
	}
}
