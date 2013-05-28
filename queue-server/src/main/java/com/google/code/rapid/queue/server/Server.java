package com.google.code.rapid.queue.server;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.org.rapid_framework.util.JVMUtil;

import com.google.code.rapid.queue.server.util.SpringContext;
import com.google.code.rapid.queue.thrift.api.Constants;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService.Processor;

public class Server {
	private static Logger logger = LoggerFactory.getLogger(Server.class);
	
	private int port = Constants.DEFAULT_SERVER_PORT;
	
	public Server() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("uncaughtException,Thread:"+t+" cuase:"+e,e);
			}
		});
		
	}
	
	public Server(int port) {
		this.port = port;
	}

	public void startServer() throws TTransportException {
		JVMUtil.lockFileForOnlyProcess("rapid-queue-port-"+port);
		
		MessageBrokerService.Iface iface = SpringContext.getBean(MessageBrokerService.Iface.class);
		TServerTransport serverTransport = new TServerSocket(port);

		MessageBrokerService.Processor processor = new Processor(iface);

		Factory portFactory = new TBinaryProtocol.Factory(true, true);

		Args args = new Args(serverTransport);
		args.maxWorkerThreads(2000);
		args.minWorkerThreads(8);
		args.processor(processor);
		args.protocolFactory(portFactory);
		
		TServer server = new TThreadPoolServer(args); // 有多种server可选择
		server.setServerEventHandler(new TServerEventHandlerImpl());
		logger.info("start MessageBrokerService thrift server on port:"+port);
		server.serve();
	}

	public static void main(String[] args) throws TTransportException {
		Server server = new Server();
		server.startServer();
	}
}