package com.google.code.rapid.queue.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.google.code.rapid.queue.thrift.api.Constants;
import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService.Client;
import com.google.code.rapid.queue.thrift.api.MessageBrokerService.Iface;

public class MessageBrokerServiceClient implements Iface,InitializingBean,DisposableBean{
	private static Logger logger = LoggerFactory.getLogger(MessageBrokerServiceClient.class);
	
	private String host;
	private int port = Constants.DEFAULT_SERVER_PORT;

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
	
	private void login(MessageBrokerService.Client client)
			throws MessageBrokerException, TException {
		if(StringUtils.isBlank(vhost)) throw new IllegalStateException("vhost must be not empty");
		if(StringUtils.isBlank(password)) throw new IllegalStateException("password must be not empty");
		if(StringUtils.isBlank(username)) throw new IllegalStateException("username must be not empty");
		
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
	public String ping() throws MessageBrokerException, TException {
		Client client = borrowObject();
		try {
			return client.ping();
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

	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isBlank(host)) throw new IllegalStateException("host must be not empty");
		if(port <= 0) throw new IllegalArgumentException("port > 0 must be true");
		if(clientPoolSize <= 0) throw new IllegalArgumentException("clientPoolSize > 0 must be true");
		
		clientPool = new GenericObjectPool<MessageBrokerService.Client>(new ClientPoolableObjectFactory(),clientPoolSize);
		logger.info("init end,server="+host+":"+port+" clientPoolSize:"+clientPoolSize+" username:"+username);
	}
	
	public void destroy() throws Exception {
		if(clientPool != null) {
			clientPool.close();
		}
	}
	
	private class ClientPoolableObjectFactory extends BasePoolableObjectFactory<MessageBrokerService.Client> {
		final Map<Client,TTransport> clientTTransportMap = new HashMap<Client,TTransport> ();
		@Override
		public MessageBrokerService.Client makeObject() throws Exception {
			TTransport transport = new TSocket(host, port);
			TProtocol protocol = new TBinaryProtocol(transport);
			transport.open();
			MessageBrokerService.Client client = new MessageBrokerService.Client(protocol);
			login(client);
			clientTTransportMap.put(client, transport);
			logger.info("connected_to_server:"+host+":"+port+" by username:"+username+" vhost:"+vhost);
			return client;
		}
		
		@Override
		public void destroyObject(MessageBrokerService.Client obj) throws Exception {
			TTransport transport = clientTTransportMap.get(obj);
			if(transport != null) {
				logger.info("closed_transport, server:"+host+":"+port+" by username:"+username+" vhost:"+vhost);
				transport.close();
			}
		}
		
		@Override
		public boolean validateObject(Client obj) {
			String ping = null;
			try {
				ping = obj.ping();
				if(Constants.PING_RESPONSE.equals(ping)) {
					return true;
				}
				return false;
			} catch (Exception e) {
				ping = e.toString();
				return false;
			}finally {
				logger.info("validateObject,MessageBrokerService.Client ping() "+host+" and get response:"+ping);
			}
		}
	}




}