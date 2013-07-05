package com.google.code.rapid.queue.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.code.rapid.queue.client.SimpleMessageBrokerServiceClient.SerDsHelper;


public class InflateCompressUtilTest extends Assert{
	
	@Test
	public void test() throws DataFormatException {
		String input = "11111";
		compressAndDecompress(input);
		compressAndDecompress("act=/login&pro=yygame%26logver%3D3.0%26proxy_ip%3D%26stime%3D2013-07-03+14%3A20%3A40%26ip%3D127.0.0.1&");
//		
		List<String> list = Arrays.asList("act=/login&pro=yygame%26logver%3D3.0%26proxy_ip%3D%26stime%3D2013-07-03+14%3A20%3A40%26ip%3D127.0.0.1&");
		compressAndDecompress(SerDsHelper.toBytes(list));
		compressAndDecompress(SerDsHelper.toBytes(new ArrayList<String>(list)));
	}
	
	private void compressAndDecompress(String input) {
		compressAndDecompress(input.getBytes());
	}

	private void compressAndDecompress(byte[] input) {
		System.out.println("input:"+Arrays.toString(input));
		byte[] compressed = InflateCompressUtil.compress(input);
		System.out.println("compressed:"+Arrays.toString(compressed));
		byte[] decompress = InflateCompressUtil.decompress(compressed);
		System.out.println("decompress:"+Arrays.toString(decompress));
		assertTrue(Arrays.equals(input,decompress));
	}
	
	@Test
	public void test_ser() throws DataFormatException {
		byte[] input = SerDsHelper.toBytes(StringUtils.repeat("中", 1000));
		byte[] compressed = InflateCompressUtil.compress(input);
		byte[] decompress = InflateCompressUtil.decompress(compressed);
		
		System.out.println("input:"+input.length+","+Arrays.toString(input));
		System.out.println("compressed:"+compressed.length+","+Arrays.toString(compressed));
		System.out.println("decompress:"+decompress.length+","+Arrays.toString(decompress));
		
		assertTrue(Arrays.equals(input,decompress));
	}
	
}
