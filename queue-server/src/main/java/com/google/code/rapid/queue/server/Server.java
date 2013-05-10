package com.google.code.rapid.queue.server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.google.code.rapid.queue.server.impl.MessageBrokerServerImpl;
import com.google.code.rapid.queue.server.thrift.MessageBrokerServer;
import com.google.code.rapid.queue.server.thrift.MessageBrokerServer.Processor;

public class Server {
	int port = 9088;
	public void startServer() {
		try {

			TServerSocket serverTransport = new TServerSocket(port);

			MessageBrokerServer.Processor process = new Processor(new MessageBrokerServerImpl());

			Factory portFactory = new TBinaryProtocol.Factory(true, true);

			Args args = new Args(serverTransport);
			args.processor(process);
			args.protocolFactory(portFactory);

			TServer server = new TThreadPoolServer(args);
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