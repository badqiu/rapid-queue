package com.google.code.rapid.queue.server;

import java.net.InetAddress;
import java.net.Socket;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class TServerEventHandlerImpl implements TServerEventHandler{

	@Override
	public ServerContext createContext(TProtocol input, TProtocol output) {
		return new ServerContextImpl();
	}

	@Override
	public void deleteContext(ServerContext serverContext, TProtocol input,
			TProtocol output) {
		((ServerContextImpl)serverContext).clear();
	}

	@Override
	public void preServe() {
	}

	@Override
	public void processContext(ServerContext serverContext,
			TTransport inputTransport, TTransport outputTransport) {
		ServerContextImpl context = ((ServerContextImpl)serverContext);
		
		InetAddress addr = inputTransport instanceof TSocket ? ((TSocket) inputTransport).getSocket().getInetAddress() : null;
		ThriftContext.setServerContext(context);
		if(addr != null) {
			ThriftContext.set(ThriftContext.CLIENT_IP, addr.getHostAddress());
		}
	}

}
