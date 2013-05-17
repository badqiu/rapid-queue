/*
 *  Copyright 2011 badqiu [badqiu1223@gmail.com][weibo.com@badqiu1223]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.google.code.rapid.queue.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rapid.queue.util.MappedByteBufferUtil;

/**
 * 数据索引文件
 * 
 * @author badqiu
 * @date 2012-5-18
 */
public class LogIndex {
	private static final Logger log = LoggerFactory.getLogger(LogIndex.class);

	private static int VERSION = 1;
	
	private final int dbFileLimitLength = 32;
	private RandomAccessFile dbRandFile = null;
	private FileChannel fc;
	private MappedByteBuffer mappedByteBuffer;

	/**
	 * 文件操作位置信息
	 */
	private String magicString = null;
	private int version = VERSION;
	
	private int readerPosition = -1; // 读的位置
	private int readerIndex = -1;	 // 读的文件索引
	
	private int writerPosition = -1; // 写的位置
	private int writerIndex = -1;    // 写的文件索引
	
	private AtomicInteger queueSize = new AtomicInteger(); // queue size

	public LogIndex(String path) throws IOException {
		File dbFile = new File(path);

		if (dbFile.exists()) {
			openLogIndexFile(dbFile);
			log.info("create queue LogIndex file:"+path+",header:"+this);
		} else {
			// 文件不存在，创建文件
			newLogIndexFile(dbFile);
			openLogIndexFile(dbFile);
		}
		
	}
	private void openLogIndexFile(File dbFile) throws IOException,FileNotFoundException {
		dbRandFile = new RandomAccessFile(dbFile, "rwd");
		if (dbRandFile.length() < 32) {
			throw new IllegalStateException("file format error,filesize < 32,dbFile:"+dbFile);
		}
		
		byte[] b = new byte[this.dbFileLimitLength];
		dbRandFile.read(b);
		ByteBuffer buffer = ByteBuffer.wrap(b);
		b = new byte[LogEntity.MAGIC.getBytes().length];
		buffer.get(b);
		magicString = new String(b);
		version = buffer.getInt();
		readerPosition = buffer.getInt();
		writerPosition = buffer.getInt();
		readerIndex = buffer.getInt();
		writerIndex = buffer.getInt();
		queueSize.set(buffer.getInt());
		log.info("open queue LogIndex file:"+dbFile+",header:"+this);
		
		fc = dbRandFile.getChannel();
		mappedByteBuffer = fc.map(MapMode.READ_WRITE, 0, this.dbFileLimitLength);
	}
	
	private void newLogIndexFile(File dbFile) throws IOException,
			FileNotFoundException {
		dbFile.createNewFile();
		RandomAccessFile dbRandFile = new RandomAccessFile(dbFile, "rwd");
		fc = dbRandFile.getChannel();
		mappedByteBuffer = fc.map(MapMode.READ_WRITE, 0, this.dbFileLimitLength);
		try {
			mappedByteBuffer.put(LogEntity.MAGIC.getBytes());
			mappedByteBuffer.putInt(VERSION); // version
			putReaderPosition(LogEntity.MESSAGE_START_POSITION);// 12 reader
			putWriterPosition(LogEntity.MESSAGE_START_POSITION);// 16write
			putReaderIndex(1); //20 readerIndex
			putWriterIndex(1); //24 writerIndex
			putSize(0); // queue size
		}finally {
			mappedByteBuffer.force();
			MappedByteBufferUtil.clean(mappedByteBuffer);
			dbRandFile.close();
			fc.close();
		}
	}

	/**
	 * 记录写位置
	 * 
	 * @param pos
	 */
	public void putWriterPosition(int pos) {
		mappedByteBuffer.position(16);
		mappedByteBuffer.putInt(pos);
		this.writerPosition = pos;
	}

	/**
	 * 记录读取的位置
	 * 
	 * @param pos
	 */
	public void putReaderPosition(int pos) {
		mappedByteBuffer.position(12);
		mappedByteBuffer.putInt(pos);
		this.readerPosition = pos;
	}

	/**
	 * 记录写文件索引
	 * 
	 * @param index
	 */
	public void putWriterIndex(int index) {
		if(index >= Integer.MAX_VALUE) {
			throw new RuntimeException("index exceed Integer.MAX_VALUE,cur value:"+index);
		}
		
		mappedByteBuffer.position(24);
		mappedByteBuffer.putInt(index);
		this.writerIndex = index;
	}

	/**
	 * 记录读取文件索引
	 * 
	 * @param index
	 */
	public void putReaderIndex(int index) {
		if(index >= Integer.MAX_VALUE) {
			throw new RuntimeException("index exceed Integer.MAX_VALUE,cur value:"+index);
		}
		
		mappedByteBuffer.position(20);
		mappedByteBuffer.putInt(index);
		this.readerIndex = index;
	}

	private void putSize(int num) {
		if(num < 0) {
			throw new IllegalStateException("size must be >0, input size:"+num);
		}
		mappedByteBuffer.position(28);
		mappedByteBuffer.putInt(num);
	}
	
	public void incrementQueueSize() {
		int newSize = queueSize.get()+1;
		putSize(newSize);
		queueSize.incrementAndGet();
	}

	public void decrementQueueSize() {
		int newSize = queueSize.get()-1;
		putSize(newSize);
		queueSize.decrementAndGet();
	}

	public String getMagicString() {
		return magicString;
	}

	public int getVersion() {
		return version;
	}

	public int getReaderPosition() {
		return readerPosition;
	}

	public int getWriterPosition() {
		return writerPosition;
	}

	public int getReaderIndex() {
		return readerIndex;
	}

	public int getWriterIndex() {
		return writerIndex;
	}

	public long getQueueSize() {
		return queueSize.get();
	}

	/**
	 * 关闭索引文件
	 */
	public void close() {
		try {
			mappedByteBuffer.force();
			MappedByteBufferUtil.clean(mappedByteBuffer);
			fc.close();
			dbRandFile.close();
			mappedByteBuffer = null;
			fc = null;
			dbRandFile = null;
		} catch (IOException e) {
			log.error("close logindex file error:", e);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LogIndex ");
		sb.append(" queueSize:");
		sb.append(queueSize);
		
		sb.append(" writerIndex:");
		sb.append(writerIndex);
		sb.append(" writerPosition:");
		sb.append(writerPosition);

		sb.append(" readerIndex:");
		sb.append(readerIndex);
		sb.append(" readerPosition:");
		sb.append(readerPosition);

		return sb.toString();
	}

}
