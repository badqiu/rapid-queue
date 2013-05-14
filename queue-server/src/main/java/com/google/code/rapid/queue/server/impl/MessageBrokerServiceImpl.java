package com.google.code.rapid.queue.server.impl;

import java.util.List;

import org.apache.thrift.TException;

import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService.Iface;

public class MessageBrokerServiceImpl implements Iface{

	@Override
	public void send(Message msg) throws TException {
		System.out.println("send(msg)");
	}

	@Override
	public void sendBatch(List<Message> msg) throws TException {
		System.out.println("sendBatch(msg)");
	}

	@Override
	public Message receive(String queueName, int timeout) throws TException {
		System.out.println("receive(msg)");
		return null;
	}

	@Override
	public List<Message> receiveBatch(String queueName, int timeout,
			int batchSize) throws TException {
		System.out.println("receiveBatch(msg)");
		return null;
	}

}
