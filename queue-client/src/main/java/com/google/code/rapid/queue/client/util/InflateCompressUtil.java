package com.google.code.rapid.queue.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Deflater,Inflater压缩解压的工具类
 * 
 * @author badqiu
 *
 */
public class InflateCompressUtil {

	public static byte[] compress(byte[] input) {
		if(input == null) return null;
		
		return compress(input.length,input);
	}
	
	public static byte[] compress(int bufSize, byte[] input) {
		if(input == null) return null;
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream(bufSize);
			Deflater deflater = new Deflater(3);
			DeflaterOutputStream dos = new DeflaterOutputStream(output,deflater,bufSize);
			dos.write(input);
			dos.flush();
			dos.close();
			
			return output.toByteArray();
		}catch(IOException e) {
			throw new RuntimeException("cannot compress input data,"+e,e);
		}
	}

	public static byte[] decompress(byte[] input)  {
		if(input == null) return null;
		
		int bufSize = (int)(input.length + input.length * 0.4);
		return decompress(bufSize,input);
	}
	
	public static byte[] decompress(int bufSize, byte[] input)  {
		if(input == null) return null;
		
		try {
			Inflater inflater = new Inflater();
			ByteArrayInputStream bis = new ByteArrayInputStream(input);
			InflaterInputStream in = new InflaterInputStream(bis,inflater);
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream(bufSize);
			ioCopy(bufSize, in,bout);
			return bout.toByteArray();
		}catch(IOException e) {
			throw new RuntimeException("cannot decompress input data,"+e,e);
		}
	}

	private static void ioCopy(int bufSize, InputStream in,OutputStream out
			) throws IOException {
		copyLarge(in,out,bufSize);
	}
	
    public static long copyLarge(InputStream input, OutputStream output,int bufSize) throws IOException {
		byte[] buffer = new byte[bufSize];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
		    output.write(buffer, 0, n);
		    count += n;
		}
		return count;
    }
    
}
