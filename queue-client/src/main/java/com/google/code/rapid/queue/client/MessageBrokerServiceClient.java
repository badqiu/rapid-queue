package com.google.code.rapid.queue.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.server.Server;
import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerException;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService.Iface;

public class MessageBrokerServiceClient implements Iface {
	private static Logger logger = LoggerFactory.getLogger(MessageBrokerServiceClient.class);
	
	private String host;
	private int port = Server.DEFAULT_PORT;

	private String username;
	private String password;
	private String vhost;

	private TTransport transport = null;
	private MessageBrokerService.Client client;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	public Message receive(String queueName, int timeout)
			throws MessageBrokerException {
		try {
			return client.receive(queueName, timeout);
		} catch (TException e) {
			reopen();
			throw new RuntimeException("error on receive() queueName:"
					+ queueName + " timeout:" + timeout, e);
		}
	}

	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws MessageBrokerException {
		try {
			return client.receiveBatch(queueName, timeout, batchSize);
		} catch (TException e) {
			reopen();
			throw new RuntimeException("error on receiveBatch() queueName:"
					+ queueName + " timeout:" + timeout + " batchSize:"
					+ batchSize, e);
		}
	}

	public void send(Message msg) throws MessageBrokerException {
		try {
			client.send(msg);
		} catch (TException e) {
			reopen();
			throw new RuntimeException("error on send() exchange:"
					+ msg.getExchange() + " routerKey:" + msg.getRouterKey(), e);
		}
	}

	public void sendBatch(List<Message> msgList) throws MessageBrokerException {
		try {
			client.sendBatch(msgList);
		} catch (TException e) {
			reopen();
			throw new RuntimeException("error on sendBatch(), msgList.size:"
					+ msgList.size(), e);
		}
	}

	public void open() throws MessageBrokerException, TException {
		Assert.hasText(host, "host must be not empty");
		Assert.isTrue(port > 0, "port > 0 must be true");
		
		transport = new TSocket(host, port);
		TProtocol protocol = new TBinaryProtocol(transport);
		transport.open();
		client = new MessageBrokerService.Client(protocol);
		
		login(client);
		logger.info("connected to server:"+host+":"+port+" by username:"+username+" vhost:"+vhost);
	}

	private void login(MessageBrokerService.Client client)
			throws MessageBrokerException, TException {
		Assert.hasText(username, "username must be not empty");
		Assert.hasText(password, "password must be not empty");
		Assert.hasText(vhost, "vhost must be not empty");
		client.login(username, password, vhost);
	}

	public void close() {
		if (transport != null)
			transport.close();
	}

	public void reopen() throws MessageBrokerException {
		close();
		try {
			open();
		} catch (TException e) {
			throw new RuntimeException("open error");
		}
	}

	@Override
	public void login(String username, String password, String vhost)
			throws MessageBrokerException, TException {
		client.login(username, password, vhost);
	}

	@Override
	public void logout() throws MessageBrokerException, TException {
		client.logout();
	}

}