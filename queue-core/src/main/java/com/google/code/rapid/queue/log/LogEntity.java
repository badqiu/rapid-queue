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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rapid.queue.util.FileMappedByteBuffer;
/**
 *@author badqiu
 *@date 2012-5-18
 */
public class LogEntity {
	private static final Logger log = LoggerFactory.getLogger(LogEntity.class);
	
	public static enum WriteFlagEnum {
		WRITESUCCESS(1),WRITEFAILURE(2),WRITEFULL(3);
		
		private WriteFlagEnum(int code) {
			this.code = (byte)code;
		}
		
		public byte getCode() {
			return code;
		}

		private byte code;
	}
	
	public static final String MAGIC = "DurableQ";
	public static int MESSAGE_START_POSITION = 16;
	public static int VERSION = 1;
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private File file;
	public FileMappedByteBuffer fmbb;
	public MappedByteBuffer mappedByteBuffer;
	private int fileLimitLength = 1024 * 1024 * 40;

	private LogIndex db = null;
	private int currentFileNumber = -1;
	private boolean closed = false;
	
	/**
	 * 文件操作header: 位置信息
	 */
	private String magicString = null;
	private int version = VERSION;
	private int endPosition = -1;
	
	private int useCount = 0;
	
	
	private LogEntity(String path, LogIndex db, int fileNumber,int fileLimitLength) throws IOException {
		this.currentFileNumber = fileNumber;
		this.fileLimitLength = fileLimitLength;
		this.db = db;
		file = new File(path);
		if (file.exists()) {
			log.info("file exists,so open file"+file);
			init(openLogEntryFile(file,fileLimitLength));
		} else {
			log.info("file not exists,so create file"+file);
			FileMappedByteBuffer fmbb = createLogEntityFile(file,fileLimitLength);
			fmbb.close();
			
			init(openLogEntryFile(file,fileLimitLength));
			FileRunner.addCreateFile(fileNumber + 1);
		}
		
		executor.execute(new MappedByteBufferSync());
		if(mappedByteBuffer == null) {
			throw new IllegalStateException("mappedByteBuffer must be not null");
		}
		if(fmbb == null) {
			throw new IllegalStateException("fmbb must be not null");
		}
		useCount++;
	}
	
	private static Map<String,LogEntity> logEntityCache = new HashMap<String,LogEntity>();
	public static synchronized LogEntity newInstance(String path,LogIndex db,int fileNumber,int fileLimitLength) throws IOException {
		String cacheKey = new File(path).getAbsolutePath();
		LogEntity entity = logEntityCache.get(cacheKey);
		if(entity == null || entity.closed) {
			entity = new LogEntity(path, db, fileNumber, fileLimitLength);
			logEntityCache.put(cacheKey, entity);
		}else {
			entity.incrementUseCount();
		}
		return entity;
	}

	private void init(FileMappedByteBuffer fmbb1) {
		this.fmbb = fmbb1;
		this.mappedByteBuffer = fmbb1.getMappedByteBuffer();
		readLogEntiryStatus(fmbb1.getMappedByteBuffer());
		log.info("init() " + this);
	}

	private void readLogEntiryStatus(MappedByteBuffer mbb) {
		// magicString
		byte[] b = new byte[8];
		mbb.get(b);
		magicString = new String(b);
		if (magicString.equals(MAGIC) == false) {
			throw new IllegalStateException("file format error,magic flag errpr,expected:"+MAGIC+" Actual:"+magicString);
		}
		
		// version
		version = mbb.getInt();

		endPosition = mbb.getInt();
	}



	public class MappedByteBufferSync implements Runnable {
		@Override
		public void run() {
			while (true) {
				if (mappedByteBuffer != null) {
					try {
						mappedByteBuffer.force();
					} catch (Exception e) {
						break;
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						break;
					}
				} else {
					break;
				}
			}
			log.info("stop LogEntity MappedByteBufferSync thread,index:"+getCurrentFileNumber());
		}
	}

	public int getCurrentFileNumber() {
		return this.currentFileNumber;
	}

	/**
	 * 当前文件是否写满
	 * @param increment
	 * @return
	 */
	public boolean isFull(int increment) {
		// confirm if the file is full
		if (this.fileLimitLength < this.db.getWriterPosition() + increment) {
			return true;
		}
		return false;
	}

	public WriteFlagEnum write(byte[] log) {
		assertOpen();
		int increment = log.length + 4;
		if (isFull(increment)) {
			if(mappedByteBuffer == null) {
				throw new IllegalStateException("mappedByteBuffer must be not null");
			}
			putLogEntryEndPosition(this.db.getWriterPosition());
			return WriteFlagEnum.WRITEFULL;
		}
		
		mappedByteBuffer.position(this.db.getWriterPosition());
		mappedByteBuffer.putInt(log.length);
		mappedByteBuffer.put(log);
		
		this.db.putWriterPosition((this.db.getWriterPosition() + increment));
		
		return WriteFlagEnum.WRITESUCCESS;
	}

	private void putLogEntryEndPosition(int pos) {
		mappedByteBuffer.position(12);
		mappedByteBuffer.putInt(pos);
		this.endPosition = pos;
		log.info("putLogEntryEndPosition() "+this);
	}

	public byte[] readAndMove2Next() throws FileEOFException {
		DataBlock block = readBlock();
		if(block == null) {
			return null;
		}
		move2NextReadPosition(block);
		return block.data;
	}

	public void move2NextReadPosition(DataBlock block) {
		this.db.putReaderPosition(block.nextReaderPosition());
	}

	public byte[] read() throws FileEOFException {
		return readBlock().data;
	}
	
	public DataBlock readBlock() throws FileEOFException {
		assertOpen();
		
		int readerPosition = this.db.getReaderPosition();
		int readerIndex = db.getReaderIndex();
		if (this.endPosition > 0 && readerPosition >= this.endPosition) {
			throw new FileEOFException("file eof");
		}
		if(readerIndex > db.getWriterIndex()) {
//			throw new IllegalStateException("readerIndex < writerIndex mube true,current readerIndex:"+readerIndex+" writerIndex:"+db.getWriterIndex());
			return null;
		}
		
		if(readerIndex == db.getWriterIndex()) {
			// readerPosition must be less than writerPosition
			if (readerPosition >= this.db.getWriterPosition()) {
				return null;
			}
		}

		try {
			//length
			mappedByteBuffer.position(readerPosition);
			int length = mappedByteBuffer.getInt();
			
			//data[]
			byte[] b = new byte[length];
			mappedByteBuffer.get(b);
			return new DataBlock(readerIndex,readerPosition,length,b);
		}catch(Throwable e) {
			throw new RuntimeException("error read, current LogEntity:"+this+" LogIndex:"+db,e);
		}
	}
	
	private class DataBlock {
		int readerIndex;
		int readerPosition;
		int length;
		byte[] data;
		public DataBlock(int readerIndex, int readerPosition, int length,
				byte[] data) {
			super();
			this.readerIndex = readerIndex;
			this.readerPosition = readerPosition;
			this.length = length;
			this.data = data;
		}
		public int nextReaderPosition() {
			return readerPosition + (length + 4);
		}
	}

	private void assertOpen() {
		if(closed) {
			throw new IllegalStateException("already closed LogEntity:"+currentFileNumber);
		}
	}
	
	public void incrementUseCount() {
		useCount ++;
		log.info("incrementUseCount:"+this);
	}
	
	public void close() {
		useCount--;
		if(useCount <= 0) {
			close0();
		}else {
			log.info("not yet close by useCount:"+useCount+" LogEntity:"+this);
		}
	}

	private void close0() {
		closed = true;
		log.info("close LogEntity:"+this);
		
		logEntityCache.remove(file.getAbsolutePath());
	    if(fmbb != null) {
	    	fmbb.close();
	    }
	    executor.shutdownNow();
	    try {
			executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LogEntity currentFileNumber:");
		sb.append(currentFileNumber);
		sb.append(" endPosition:");
		sb.append(endPosition);
		return sb.toString();
	}

	public static FileMappedByteBuffer createLogEntityFile(File file,int fileLimitLength) throws IOException {
		if (file.createNewFile() == false) {
			return null;
		}
		RandomAccessFile raFile = new RandomAccessFile(file, "rwd");
		FileChannel fc = raFile.getChannel();
		MappedByteBuffer mappedByteBuffer = fc.map(MapMode.READ_WRITE, 0, fileLimitLength);
		mappedByteBuffer.put(MAGIC.getBytes()); // 0 magic
		mappedByteBuffer.putInt(VERSION);// 8 version
		mappedByteBuffer.putInt(-1);// 12 next fileindex
		mappedByteBuffer.putInt(-1);// 16 endPosition
		mappedByteBuffer.force();
		
		FileMappedByteBuffer result = new FileMappedByteBuffer(raFile,fc,mappedByteBuffer);
		log.info("create file:"+file);
		return result;
	}
	
	private static FileMappedByteBuffer openLogEntryFile(File file,int fileLimitLength) throws FileNotFoundException, IOException {
		RandomAccessFile raFile = new RandomAccessFile(file, "rwd");
		if (raFile.length() < LogEntity.MESSAGE_START_POSITION) {
			throw new IllegalStateException("file format error,file size < "+LogEntity.MESSAGE_START_POSITION+" on file:"+file);
		}
		
		FileChannel fc = raFile.getChannel();
		MappedByteBuffer mappedByteBuffer = fc.map(MapMode.READ_WRITE, 0,fileLimitLength);
		FileMappedByteBuffer fmbb = new FileMappedByteBuffer(raFile, fc, mappedByteBuffer);
		return fmbb;
	}

	public boolean isClosed() {
		return closed;
	}
}
