package com.google.code.rapid.queue.util;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileMappedByteBuffer {
	private RandomAccessFile file;
	private FileChannel channel;
	private MappedByteBuffer mappedByteBuffer;
	
	public FileMappedByteBuffer(RandomAccessFile file, FileChannel channel,
			MappedByteBuffer mappedByteBuffer) {
		super();
		this.file = file;
		this.channel = channel;
		this.mappedByteBuffer = mappedByteBuffer;
	}
	
	public RandomAccessFile getFile() {
		return file;
	}

	public FileChannel getChannel() {
		return channel;
	}

	public MappedByteBuffer getMappedByteBuffer() {
		return mappedByteBuffer;
	}

	public void close() {
		try {
			mappedByteBuffer.force();
			MappedByteBufferUtil.clean(mappedByteBuffer);
			channel.close();
			file.close();
		}catch(Exception e) {
			throw new RuntimeException("close file error",e);
		}
	}
}
