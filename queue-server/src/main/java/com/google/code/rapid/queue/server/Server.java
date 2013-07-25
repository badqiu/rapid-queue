package com.google.code.rapid.queue.server;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.thrift.TProcessor;
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
	private int managePort = Constants.DEFAULT_MANAGE_SERVER_PORT;
	
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

	public void startServer() throws TTransportException, UnknownHostException {
		JVMUtil.lockFileForOnlyProcess("rapid-queue-port-"+port);
		
		final MessageBrokerService.Iface messageBrokerService = SpringContext.getBean("messageBrokerService",MessageBrokerService.Iface.class);
		TServerTransport serverTransport = new TServerSocket(port);
		
		Thread t = new Thread("MsgBrokerManageServer"){
			public void run() {
				try {
					startProcessor(new Processor(messageBrokerService), new TServerSocket(new InetSocketAddress(InetAddress.getLocalHost(),managePort)));
				}catch(Exception e) {
					logger.error("error on start messageBrokerManagerService",e);
				}
			}
		};
		t.start();
		
		startProcessor(new Processor(messageBrokerService), serverTransport);
	}

	private void startProcessor(TProcessor processor,TServerTransport serverTransport) {
		Factory portFactory = new TBinaryProtocol.Factory(true, true);

		Args args = new Args(serverTransport);
		args.maxWorkerThreads(2000);
		args.minWorkerThreads(8);
		args.processor(processor);
		args.protocolFactory(portFactory);
		
		TServer server = new TThreadPoolServer(args); // 有多种server可选择
		server.setServerEventHandler(new TServerEventHandlerImpl());
		logger.info("start processor:"+processor+" thrift server");
		server.serve();
	}

	public static void main(String[] args) throws TTransportException, UnknownHostException {
		Server server = new Server();
//		server.startNoBlockServer();
		server.startServer();
	}
}