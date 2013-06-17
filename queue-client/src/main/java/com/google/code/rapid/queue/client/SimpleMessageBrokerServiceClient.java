package com.google.code.rapid.queue.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;

public class SimpleMessageBrokerServiceClient implements InitializingBean{
	
	private MessageBrokerServiceClient client;
	
	private SerDsHelper serDsHelper = new SerDsHelper();

	public void setClient(MessageBrokerServiceClient client) {
		this.client = client;
	}


	public void setSerDsHelper(SerDsHelper serDsHelper) {
		this.serDsHelper = serDsHelper;
	}

	public void send(SimpleMessage<?> msg) {
		Assert.notNull(msg.getPayload(),"payload must be not null");
		try {
			SimpleMessage clone = msg.clone();
			serDsHelper.serializerPayload(clone);
			client.send(clone);
		} catch (MessageBrokerException e) {
			throw new MessageBrokerRuntimeException(e);
		}
	}

	public void sendBatch(List<SimpleMessage<?>> msgList)  {
		try {
			for(SimpleMessage msg : msgList) {
				serDsHelper.serializerPayload(msg);
			}
			
			List sendList = msgList;
			client.sendBatch(sendList);
		} catch (MessageBrokerException e) {
			throw new MessageBrokerRuntimeException(e);
		}
	}

	public <T> SimpleMessage<T> receive(String queueName,Class<T> clazz) {
		return receive(queueName,-1,clazz);
	}
	
	public <T> SimpleMessage<T> receive(String queueName, int timeout,Class<T> clazz) {
		try {
			Message msg = client.receive(queueName, timeout);
			return serDsHelper.toSimpleMessage(msg,clazz);
		} catch (MessageBrokerException e) {
			throw new MessageBrokerRuntimeException(e);
		}
	}
	
	public <T> List<SimpleMessage<T>> receiveBatch(String queueName, int timeout,int batchSize,Class<T> clazz)  {
		try {
			List<Message> msgList = client.receiveBatch(queueName, timeout, batchSize);
			List<SimpleMessage<T>> result = new ArrayList<SimpleMessage<T>>();
			for(Message msg : msgList) {
				result.add(serDsHelper.toSimpleMessage(msg,clazz));
			}
			return result;
		} catch (MessageBrokerException e) {
			throw new MessageBrokerRuntimeException(e);
		}
	}

	public static class SerDsHelper {
		private static Serializer serializer = new DefaultSerializer();
		private static Deserializer deserializer = new DefaultDeserializer();
		
		@SuppressWarnings("rawtypes")
		public void serializerPayload(SimpleMessage msg) {
			byte[] output = toBytes(msg);
			msg.setBody(output);
		}
		
		@SuppressWarnings("unchecked")
		public static <T> SimpleMessage<T> toSimpleMessage(Message msg, Class<T> clazz) {
			SimpleMessage<T> result = new SimpleMessage<T>(msg);
			T payload = (T)fromBytes(msg.getBody());
			result.setPayload(payload);
			return result;
		}

		@SuppressWarnings("unchecked")
		public static byte[] toBytes(Object body) {
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				serializer.serialize(body,output);
				return output.toByteArray();
			} catch (IOException e) {
				throw new RuntimeException("toBytes error",e);
			}
		}

		public static Object fromBytes(byte[] bytes) {
			if(bytes == null) return null;
			try {
				return deserializer.deserialize(new ByteArrayInputStream(bytes));
			} catch (IOException e) {
				throw new RuntimeException("fromBytes error,bytes.length="+ArrayUtils.getLength(bytes),e);
			}
		}
		
		@SuppressWarnings("rawtypes")
		public static byte[] toBytes(SimpleMessage msg) {
			if(msg.getBody() == null && msg.getPayload() == null) {
				throw new RuntimeException("msg.body and msg.payload must be not null, choose only one");
			}
			if(msg.getBody() != null && msg.getPayload() != null) {
				throw new RuntimeException("msg.body and msg.payload must be set only one,msg.body="+msg.getBody()+" msg.payload:"+msg.getPayload());
			}
			
			if(msg.getBody() == null) {
				return toBytes(msg.getPayload());
			}else {
				return msg.getBody();
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(client == null) throw new RuntimeException("client must be not null");
		
	}
	
}
