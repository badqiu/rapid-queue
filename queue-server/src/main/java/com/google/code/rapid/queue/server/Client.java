package com.google.code.rapid.queue.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.google.code.rapid.queue.server.thrift.Message;
import com.google.code.rapid.queue.server.thrift.MessageBrokerServer;

public class Client {

	public void startClient() throws InterruptedException {
         TTransport transport = null;
         try {
             transport = new TSocket("localhost",9088);
             TProtocol protocol = new TBinaryProtocol(transport);
             transport.open();
             transport.close();
             
             if(!transport.isOpen()) {
            	 transport.open();
             }
             MessageBrokerServer.Client client = new MessageBrokerServer.Client(protocol);
             for(int i = 0; i < 1000; i++) {
            	 try {
            		 Thread.sleep(500);
	            	 client.send(new Message());
            	 }catch(Exception e) {
            		 e.printStackTrace();
            	 }
             }
             
         } catch (TTransportException e) {
             e.printStackTrace();
         } catch (TException e) {
             e.printStackTrace();
         }finally {
        	 if(transport != null) transport.close();
         }
     }

	public static void main(String[] args) throws InterruptedException {
		Client client = new Client();
		client.startClient();
	}
}