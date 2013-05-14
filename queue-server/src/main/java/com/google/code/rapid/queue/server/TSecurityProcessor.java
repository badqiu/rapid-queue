package com.google.code.rapid.queue.server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class TSecurityProcessor implements TProcessor {
	private TProcessor delegate;
	private Map<String,String> session = new HashMap<String,String>();
	
	@Override
	public boolean process(TProtocol in, TProtocol out) throws TException {
		TTransport t = in.getTransport();
		Socket socket = t instanceof TSocket ? ((TSocket) t).getSocket() : null;
		InetAddress addr = t instanceof TSocket ? ((TSocket) t).getSocket().getInetAddress() : null;
		if(socket != null) {
			ThriftContext.set("clientIp", addr.getHostName());
			ThriftContext.set("socket", socket);
		}
		
		if(!hasLogin(socket)) {
			throw new RuntimeException("not yet login on client ip:"+addr.getHostAddress());
		}
		
		return delegate.process(in, out);
	}

	//通过 TServerEventHandler 处理
	
	String LOGIN_SESSION_KEY = "login_user";
	public boolean hasLogin(Socket socket) {
		if(socket == null) return false;
		
		String sessionId = getSessionId(socket);
		Map<String,String> session = getSession(sessionId);
		if(session.containsKey(LOGIN_SESSION_KEY)) {
			return true;
		}
		return false;
	}
	
	public void login(Socket socket,String username) {
		String sessionId = getSessionId(socket);
		Map<String,String> session = getSession(sessionId);
		session.put(LOGIN_SESSION_KEY, username);
	}

	public void logout(Socket socket) {
		String sessionId = getSessionId(socket);
		Map<String,String> session = getSession(sessionId);
		session.clear();
	}
	
	private Map<String, String> getSession(String sessionId) {
		if(sessionId == null) {
			return new HashMap<String, String>();
		}
		return session;
	}
	
	private String getSessionId(Socket socket) {
		if(!socket.isConnected() || socket.isClosed()) {
			return null;
		}
		String sessionId = socket.toString();
		return sessionId;
	}

}
