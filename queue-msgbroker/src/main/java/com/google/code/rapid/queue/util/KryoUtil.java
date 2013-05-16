package com.google.code.rapid.queue.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
/**
 * Kryo序列化相关工具类方法
 * 
 * @author badqiu
 *
 */
public class KryoUtil {
	
	private static Kryo kryo = new Kryo();
	
	public static synchronized byte[] toBytes(Object obj,int bufSize) {
		if(obj == null) return null;
		ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream(bufSize);
		Output output = new Output(bytesOutputStream);
		kryo.writeObject(output, obj);
		output.close();
		return bytesOutputStream.toByteArray();
	}
	
	public static synchronized <T> T fromBytes(byte[] bytes,Class<T> clazz) {
		if(bytes == null) return null;
		return kryo.readObject(new Input(new ByteArrayInputStream(bytes)), clazz);
	}
	
}
