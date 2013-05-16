package com.google.code.rapid.queue.server;

import java.util.Arrays;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerService;

public class Client {

	public void startClient() {
         TTransport transport = null;
         try {
             transport = new TSocket("localhost",Server.DEFAULT_PORT);
             TProtocol protocol = new TBinaryProtocol(transport);
             transport.open();
             
             MessageBrokerService.Client client = new MessageBrokerService.Client(protocol);
             Message msg = new Message();
             msg.setExchange("ex_demo");
             msg.setBody(new byte[]{1});
             msg.setRouterKey("yygame.ddt");
             client.login("badqiu", "123456", "vhost");
			 client.send(msg);
			 
			 Message receiveMsg = client.receive("queue_demo", 100);
			 System.out.println("receiveMsg:"+Arrays.toString(receiveMsg.getBody()));
         } catch (TTransportException e) {
             e.printStackTrace();
         } catch (TException e) {
             e.printStackTrace();
         }finally {
        	 if(transport != null) transport.close();
         }
     }

	public static void main(String[] args) {
		Client client = new Client();
		client.startClient();
	}
}