package com.google.code.rapid.queue.server;

import java.net.InetAddress;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rapid.queue.server.impl.LoseMessageStore;
import com.google.code.rapid.queue.server.impl.LoseMessageVhostStore;


public class TServerEventHandlerImpl implements TServerEventHandler{
	static Logger logger = LoggerFactory.getLogger(TServerEventHandlerImpl.class);
	
	@Override
	public ServerContext createContext(TProtocol input, TProtocol output) {
		return new ServerContextImpl();
	}

	@Override
	public void deleteContext(ServerContext serverContext, TProtocol input,
			TProtocol output) {
		LoseMessageStore store = LoseMessageVhostStore.storeLoseMessage();
		logger.info("deleteContext clientIp:"+ThriftContext.getServerContext().get(ThriftContext.CLIENT_IP)+" LoseMessageStore:"+store);
		
		((ServerContextImpl)serverContext).clear();
		ThriftContext.remove();
	}

	@Override
	public void preServe() {
	}

	@Override
	public void processContext(ServerContext serverContext,
			TTransport inputTransport, TTransport outputTransport) {
//		LoseMessageStore.clearThriftContext();
		ThriftContext.remove();
		
		ServerContextImpl context = ((ServerContextImpl)serverContext);
		InetAddress addr = inputTransport instanceof TSocket ? ((TSocket) inputTransport).getSocket().getInetAddress() : null;
		if(addr != null) {
			context.put(ThriftContext.SOCKET, ((TSocket) inputTransport).getSocket());
			context.put(ThriftContext.CLIENT_IP, addr.getHostAddress());
		}
		
		ThriftContext.setServerContext(context);
	}

}
