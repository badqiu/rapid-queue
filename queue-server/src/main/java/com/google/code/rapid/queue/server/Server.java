package com.google.code.rapid.queue.server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import com.google.code.rapid.queue.server.impl.MessageBrokerServiceImpl;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService.Processor;

public class Server {
	int port = 9088;
	public void startServer() {
		try {

			TServerTransport serverTransport = new TServerSocket(port);

			MessageBrokerService.Processor processor = new Processor(new MessageBrokerServiceImpl());

			Factory portFactory = new TBinaryProtocol.Factory(true, true);

			Args args = new Args(serverTransport);
			args.processor(processor);
			args.protocolFactory(portFactory);

			TServer server = new TThreadPoolServer(args); // 有多种server可选择
			server.serve();
			System.out.println("started server on port:"+port);
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.startServer();
	}
}