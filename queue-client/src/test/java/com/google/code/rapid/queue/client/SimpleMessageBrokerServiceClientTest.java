package com.google.code.rapid.queue.client;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Serializer;

import com.google.code.rapid.queue.client.SimpleMessageBrokerServiceClient.SerDsHelper;
import com.google.code.rapid.queue.thrift.api.Message;
import com.google.code.rapid.queue.thrift.api.MessageBrokerException;


public class SimpleMessageBrokerServiceClientTest extends Mockito{
	SimpleMessageBrokerServiceClient simple = new SimpleMessageBrokerServiceClient();
	MessageBrokerServiceClient client = mock(MessageBrokerServiceClient.class);
	private Serializer serializer = new DefaultSerializer();
	@Before
	public void setUp() {
		simple.setClient(client);
	}
	
	@Test
	public void test_send() {
		String input = "100";
		byte[] bytes = SerDsHelper.toBytes(input);
		System.out.println(Arrays.toString(ByteBuffer.wrap(bytes).array()));
		assertEquals(input,SerDsHelper.fromBytes(bytes));
		
		SimpleMessage msg = new SimpleMessage();
		msg.setPayload(input);
		simple.send(msg);
	}
	
	@Test
	public void test_receive() throws MessageBrokerException, IOException {
		when(client.receive("1", 100)).thenReturn(new Message("ex","router",ByteBuffer.wrap(ser("100")),null));
		SimpleMessage msg = simple.receive("1", 100, String.class);
		assertEquals(msg.getPayload(),"100");
	}

	private byte[] ser(Object obj) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		serializer.serialize(obj, os);
		return os.toByteArray();
	}
}
