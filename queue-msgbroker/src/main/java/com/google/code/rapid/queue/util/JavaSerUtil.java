package com.google.code.rapid.queue.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerUtil {

	public static byte[] toBytes(Object obj,int bufSize) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(bufSize);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		}catch(IOException e) {
			throw new RuntimeException("toBytes() error,obj:"+obj,e);
		}
	}
	
	public static Object fromBytes(byte[] bytes) {
		if(bytes == null) return null;
		
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream oos = new ObjectInputStream(bais);
			return oos.readObject();
		}catch(IOException e) {
			throw new RuntimeException("fromBytes() error",e);
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("fromBytes() error",e);
		}
	}
}
