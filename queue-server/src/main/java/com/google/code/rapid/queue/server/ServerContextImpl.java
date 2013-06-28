package com.google.code.rapid.queue.server;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.server.ServerContext;

public class ServerContextImpl extends ConcurrentHashMap<String,Object> implements ServerContext{
	private static final long serialVersionUID = 1L;
}
