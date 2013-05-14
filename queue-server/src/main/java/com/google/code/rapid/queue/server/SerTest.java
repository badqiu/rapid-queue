package com.google.code.rapid.queue.server;

import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.junit.Test;

public class SerTest {

	@Test
	public void test() {
		Work work = new Work();
		work.setAge(100);
		
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		
	}
	
	public static class Work  {
		private String username;
		private int age;
		private String[] pwds;
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String[] getPwds() {
			return pwds;
		}
		public void setPwds(String[] pwds) {
			this.pwds = pwds;
		}
	}
}
