package com.google.code.rapid.queue.client;

import java.util.Hashtable;
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
import org.springframework.util.Assert;

import com.google.code.rapid.queue.client.util.InflateCompressUtil;
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
	private int connectionTimeout = 0;
	private ObjectPool<MessageBrokerService.Client> clientPool;
	
	/**
	 * 是否压缩及解压数据
	 */
	private boolean compress = false;
	
	private CompressHelper compressHelper = new CompressHelper();
	
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
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public Message receive(String queueName) throws MessageBrokerException {
		return receive(queueName, -1);
	}
	
	public Message receive(String queueName, int timeout)
			throws MessageBrokerException {
		Client client = borrowObject();
		try {
			Message result = compressHelper.decompressIfNeed(client.receive(queueName, timeout));
			returnObject(client);
			return result;
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on receive() queueName:" + queueName + " timeout:" + timeout, e);
		} catch(Exception e) {
			invalidateObject(client);
			throw new RuntimeException("error on receive() queueName:"+ queueName + " timeout:" + timeout, e);
		}
	}

	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws MessageBrokerException {
		Client client = borrowObject();
		try {
			List<Message> result = compressHelper.decompressIfNeed(client.receiveBatch(queueName, timeout, batchSize));
			returnObject(client);
			return result;
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on receiveBatch() queueName:"
					+ queueName + " timeout:" + timeout + " batchSize:"
					+ batchSize, e);
		} catch(Exception e) {
			invalidateObject(client);
			throw new RuntimeException("error on receiveBatch() queueName:"
					+ queueName + " timeout:" + timeout + " batchSize:"
					+ batchSize, e);
		}
	}



	public void send(Message msg) throws MessageBrokerException {
		Client client = borrowObject();
		try {
			client.send(compressHelper.compressIfNeed(msg));
			returnObject(client);
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on send() exchange:"
					+ msg.getExchange() + " routerKey:" + msg.getRouterKey(), e);
		} catch(Exception e) {
			invalidateObject(client);
			throw new RuntimeException("error on send() exchange:"
					+ msg.getExchange() + " routerKey:" + msg.getRouterKey(), e);
		}
	}



	public void sendBatch(List<Message> msgList) throws MessageBrokerException {
		Client client = borrowObject();
		try {
			client.sendBatch(compressHelper.compressIfNeed(msgList));
			returnObject(client);
		} catch (TException e) {
			invalidateObject(client);
			throw new RuntimeException("error on sendBatch(), msgList.size:"
					+ msgList.size(), e);
		} catch(Exception e) {
			invalidateObject(client);
			throw new RuntimeException("error on sendBatch(), msgList.size:"
					+ msgList.size(), e);
		}
	}
	
	private class CompressHelper {
		private List<Message> compressIfNeed(List<Message> msgList) {
			if(compress) {
				for(Message msg : msgList) {
					msg.setBody(InflateCompressUtil.compress(msg.getBody()));
				}
			}
			return msgList;
		}
	
		private Message compressIfNeed(Message msg) {
			if(compress) {
				msg.setBody(InflateCompressUtil.compress(msg.getBody()));
			}
			return msg;
		}
		
		private List<Message> decompressIfNeed(List<Message> msgList) {
			if(compress) {
				for(Message msg : msgList) {
					msg.setBody(InflateCompressUtil.decompress(msg.getBody()));
				}
			}
			return msgList;
		}
		
		private Message decompressIfNeed(Message msg) {
			if(compress) {
				msg.setBody(InflateCompressUtil.decompress(msg.getBody()));
			}
			return msg;
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
		final Map<Client,TTransport> clientTTransportMap = new Hashtable<MessageBrokerService.Client, TTransport>();
		@Override
		public MessageBrokerService.Client makeObject() throws Exception {
			TTransport transport = new TSocket(host, port,connectionTimeout);
			TProtocol protocol = new TBinaryProtocol(transport);
			transport.open();
			MessageBrokerService.Client client = new MessageBrokerService.Client(protocol);
			login(client);
			
			Assert.isTrue(validateObject(client),"client ping() error");
			
			clientTTransportMap.put(client, transport);
			logger.info("connected_to_server:"+host+":"+port+" by username:"+username+" vhost:"+vhost+" clientPool.numActive:"+clientPool.getNumActive()+" clientPool.numIdle:"+clientPool.getNumIdle()+" clientTTransportMap.size:"+clientTTransportMap.size()+" compress:"+compress);
			return client;
		}
		
		@Override
		public void destroyObject(MessageBrokerService.Client obj) throws Exception {
			TTransport transport = clientTTransportMap.remove(obj);
			if(transport != null) {
				logger.info("destroyObject() closed_transport, server:"+host+":"+port+" by username:"+username+" vhost:"+vhost+" clientPool.numActive:"+clientPool.getNumActive()+" clientPool.numIdle:"+clientPool.getNumIdle()+" clientTTransportMap.size:"+clientTTransportMap.size());
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