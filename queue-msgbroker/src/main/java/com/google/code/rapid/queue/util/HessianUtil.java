package com.google.code.rapid.queue.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;


public class HessianUtil {
	
	static SerializerFactory serializerFactory = new SerializerFactory();
	public static synchronized byte[] toBytes(Object obj,int bufSize) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(bufSize);
			
			AbstractHessianOutput out = new Hessian2Output(baos);;
			out.setSerializerFactory(serializerFactory);
	        out.writeObject(obj);     
	        out.flush();
	        
			return baos.toByteArray();
		}catch(IOException e) {
			throw new RuntimeException("toBytes() error,obj:"+obj,e);
		}
	}
	
	public static synchronized <T> T fromBytes(byte[] bytes,Class<T> clazz) {
		if(bytes == null) return null;
		
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			Hessian2Input in = new Hessian2Input(bais);
			in.setSerializerFactory(serializerFactory);
	        Object value = in.readObject();
	        return (T)value;
		}catch(IOException e) {
			throw new RuntimeException("fromBytes() error",e);
		}
	}
}
