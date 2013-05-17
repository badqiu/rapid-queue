package com.google.code.rapid.queue.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

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
	
	private static GenericObjectPool<Kryo> kryoPool;
	static {
		BasePoolableObjectFactory<Kryo> poolableObjectFactory = new BasePoolableObjectFactory<Kryo>() {
			@Override
			public Kryo makeObject() throws Exception {
				return new Kryo();
			}
		};
		kryoPool = new GenericObjectPool<Kryo>(poolableObjectFactory,500);
	};
	
	public static byte[] toBytes(Object obj,int bufSize) {
		if(obj == null) return null;
		
		ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream(bufSize);
		Output output = new Output(bytesOutputStream);
		Kryo kryo = borrowObject();
		try {
			kryo.writeObject(output, obj);
		}finally {
			returnObject(kryo);
		}
		output.close();
		return bytesOutputStream.toByteArray();
	}

	public static <T> T fromBytes(byte[] bytes,Class<T> clazz) {
		if(bytes == null) return null;
		Kryo kryo = borrowObject();
		try {
			return kryo.readObject(new Input(new ByteArrayInputStream(bytes)), clazz);
		}finally {
			returnObject(kryo);
		}
	}
	
	private static void returnObject(Kryo kryo)  {
		try {
			kryoPool.returnObject(kryo);
		} catch (Exception e) {
			throw new RuntimeException("returnObject error,kryo:"+kryo,e);
		}
	}

	private static Kryo borrowObject()  {
		try {
			return kryoPool.borrowObject();
		} catch (Exception e) {
			throw new RuntimeException("borrowObject error",e);
		}
	}
}
