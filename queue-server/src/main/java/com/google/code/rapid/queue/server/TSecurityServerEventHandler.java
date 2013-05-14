package com.google.code.rapid.queue.server;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;

public class TSecurityServerEventHandler implements TServerEventHandler{

	@Override
	public ServerContext createContext(TProtocol input, TProtocol output) {
		return null;
	}

	@Override
	public void deleteContext(ServerContext serverContext, TProtocol input,
			TProtocol output) {
	}

	@Override
	public void preServe() {
	}

	@Override
	public void processContext(ServerContext serverContext,
			TTransport inputTransport, TTransport outputTransport) {
	}

}
