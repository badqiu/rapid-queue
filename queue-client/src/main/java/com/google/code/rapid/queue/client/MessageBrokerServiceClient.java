package com.google.code.rapid.queue.client;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
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
import com.google.code.rapid.queue.server.thrift.MessageBrokerService.Client;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService.Iface;

public class MessageBrokerServiceClient implements Iface {
	private static Logger logger = LoggerFactory.getLogger(MessageBrokerServiceClient.class);
	
	private String host;
	private int port = Server.DEFAULT_PORT;

	private String username;
	private String password;
	private String vhost;

	private int clientPoolSize = 2;
	private ObjectPool<MessageBrokerService.Client> clientPool;
	
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
	
	public int getClientPoolSize() {
		return clientPoolSize;
	}

	public void setClientPoolSize(int clientPoolSize) {
		this.clientPoolSize = clientPoolSize;
	}

	public Message receive(String queueName, int timeout)
			throws MessageBrokerException {
		Client client = borrowObject();
		try {
			return client.receive(queueName, timeout);
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on receive() queueName:"
					+ queueName + " timeout:" + timeout, e);
		}finally {
			returnObject(client);
		}
	}

	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws MessageBrokerException {
		Client client = borrowObject();
		try {
			return client.receiveBatch(queueName, timeout, batchSize);
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on receiveBatch() queueName:"
					+ queueName + " timeout:" + timeout + " batchSize:"
					+ batchSize, e);
		}finally {
			returnObject(client);
		}
	}

	public void send(Message msg) throws MessageBrokerException {
		Client client = borrowObject();
		try {
			client.send(msg);
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on send() exchange:"
					+ msg.getExchange() + " routerKey:" + msg.getRouterKey(), e);
		}finally {
			returnObject(client);
		}
	}

	public void sendBatch(List<Message> msgList) throws MessageBrokerException {
		Client client = borrowObject();
		try {
			client.sendBatch(msgList);
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on sendBatch(), msgList.size:"
					+ msgList.size(), e);
		}finally {
			returnObject(client);
		}
	}

	@SuppressWarnings("unchecked")
	public void open() throws MessageBrokerException, TException {
		Assert.hasText(host, "host must be not empty");
		Assert.isTrue(port > 0, "port > 0 must be true");
		
		clientPool = new GenericObjectPool<MessageBrokerService.Client>(new BasePoolableObjectFactory() {
			@Override
			public Object makeObject() throws Exception {
				TTransport transport = new TSocket(host, port);
				TProtocol protocol = new TBinaryProtocol(transport);
				transport.open();
				MessageBrokerService.Client client = new MessageBrokerService.Client(protocol);
				login(client);
				return client;
			}
		},clientPoolSize);
		
		logger.info("connected to server:"+host+":"+port+" by username:"+username+" vhost:"+vhost);
	}

	private void login(MessageBrokerService.Client client)
			throws MessageBrokerException, TException {
		Assert.hasText(username, "username must be not empty");
		Assert.hasText(password, "password must be not empty");
		Assert.hasText(vhost, "vhost must be not empty");
		client.login(username, password, vhost);
	}

	@Override
	public void login(String username, String password, String vhost)
			throws MessageBrokerException, TException {
		Client client = borrowObject();
		try {
			client.login(username, password, vhost);
		}finally {
			returnObject(client);
		}
	}

	@Override
	public void logout() throws MessageBrokerException, TException {
		Client client = borrowObject();
		try {
			client.logout();
		}finally {
			returnObject(client);
		}
	}

	private void invalidateObject(Client client) {
		try {
			clientPool.invalidateObject(client);
		} catch (Exception e) {
			throw new RuntimeException("invalidateObject error",e);
		}
	}
	
	private void returnObject(Client client) {
		try {
			clientPool.returnObject(client);
		} catch (Exception e) {
			throw new RuntimeException("returnObject error",e);
		}
	}

	private Client borrowObject() {
		try {
			return clientPool.borrowObject();
		} catch (Exception e) {
			throw new RuntimeException("borrowObject error",e);
		}
	}

}