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
package com.google.code.rapid.queue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.google.code.rapid.queue.log.FileEOFException;
import com.google.code.rapid.queue.log.FileRunner;
import com.google.code.rapid.queue.log.LogEntity;
import com.google.code.rapid.queue.log.LogEntity.WriteFlagEnum;
import com.google.code.rapid.queue.log.LogIndex;

/**
 * 完成基于文件的先进先出(FIFO)的读写功能
 * 
 * @author badqiu
 * @date 2010-8-13
 */
public class FileQueue {
	private static final Log log = LogFactory.getLog(FileQueue.class);
	private static final String DB_NAME = LogEntity.MAGIC+".index";
	private static final String FILE_SEPERATOR = System.getProperty("file.separator");
	
	public static final String LOG_ENTITY_FILE_PREFIX = LogEntity.MAGIC+"_";
	public static int DEFAULT_FILE_LIMIT_LENGTH = 1024 * 1024 * 100;
	
	private final Executor executor = Executors.newSingleThreadExecutor();
	
	private int fileLimitLength;
	private String path = null;
	/**
	 * 文件操作实例
	 */
	private LogIndex logIndexDb = null;
	private LogEntity writerHandle = null;
	private LogEntity readerHandle = null;
	
	private boolean closed = false;

	public FileQueue(String path) throws Exception {
		this(path, DEFAULT_FILE_LIMIT_LENGTH);
	}

	/**
	 * 在指定的目录中，以fileLimitLength为单个数据文件的最大大小限制初始化队列存储
	 * 
	 * @param dir
	 *            队列数据存储的路径
	 * @param fileLimitLength
	 *            单个数据文件的大小，不能超过2G
	 * @throws Exception
	 */
	public FileQueue(String dir, int fileLimitLength) throws Exception {
		if(fileLimitLength <= 0) throw new IllegalArgumentException("fileLimitLength <= 0 is error,fileLimitLength:"+fileLimitLength);
		if(dir == null) throw new IllegalArgumentException("dir must be not null");
		
		this.fileLimitLength = fileLimitLength;
		File fileDir = new File(dir);
		if (fileDir.exists() == false && fileDir.isDirectory() == false) {
			if (fileDir.mkdirs() == false) {
				throw new IllegalArgumentException("create dir error,dir:"+dir);
			}
		}
		path = fileDir.getAbsolutePath();
		// 打开db
		logIndexDb = new LogIndex(getDbIndexPath());
		setWriterHandle(openWriterHandle(logIndexDb.getWriterIndex()));
		openReaderHandle(logIndexDb.getReaderIndex());
		
		FileRunner deleteFileRunner = new FileRunner(path, fileLimitLength);
		executor.execute(deleteFileRunner);
	}

	private LogEntity openWriterHandle(int writerIndex) throws IOException {
		LogEntity logEntity = createLogEntity(getLogEntityPath(logIndexDb.getWriterIndex()), logIndexDb,writerIndex);
		log.info("open LogEntity for writer:"+writerIndex+" logEntity:"+logEntity);
		return logEntity;
	}

	private String getDbIndexPath() {
		return path + FILE_SEPERATOR + DB_NAME;
	}

	private String getLogEntityPath(int index) {
		return getLogEntityPath(path,index);
	}

	public static String getLogEntityPath(String basePath,int index) {
		return basePath + FILE_SEPERATOR + LOG_ENTITY_FILE_PREFIX + index + ".data";
	}
	
	/**
	 * 创建或者获取一个数据读写实例
	 * 
	 * @param dbpath
	 * @param db
	 * @param fileNumber
	 * @return
	 * @throws IOException
	 */
	private LogEntity createLogEntity(String dbpath, LogIndex db, int fileNumber) throws IOException {
		return LogEntity.newInstance(dbpath, db, fileNumber, this.fileLimitLength);
	}

	/**
	 * 一个文件的数据写入达到fileLimitLength的时候，滚动到下一个文件实例
	 * 
	 * @throws IOException
	 */
	private void rotateNextLogWriter() throws IOException {
		int writerIndex = logIndexDb.getWriterIndex() + 1;
		getWriterHandle().close();
		
		logIndexDb.putWriterIndex(writerIndex);
		logIndexDb.putWriterPosition(LogEntity.MESSAGE_START_POSITION);
		setWriterHandle(openWriterHandle(writerIndex));
	}

	/**
	 * 向队列存储添加一个字符串
	 * 
	 * @param message
	 *            message
	 * @throws IOException
	 * @throws FileFormatException
	 */
	public void add(String message) throws IOException {
		add(message.getBytes());
	}

	/**
	 * 向队列存储添加一个byte数组
	 * 
	 * @param message
	 * @throws IOException
	 * @throws FileFormatException
	 */
	public void add(byte[] message) throws IOException {
		assertOpen();
		
		WriteFlagEnum status = getWriterHandle().write(message);
		if (status == WriteFlagEnum.WRITEFULL) {
			rotateNextLogWriter();
			status = getWriterHandle().write(message);
		}
		if (status == WriteFlagEnum.WRITESUCCESS) {
			logIndexDb.incrementQueueSize();
		}else {
			throw new RuntimeException("write fail,status:"+status);
		}
	}

	private void assertOpen() {
		if(closed) throw new IllegalStateException("FileQueue already closed ");
	}
	
	/**
	 * 从队列存储中取出最先入队的数据，并移除它
	 * @return
	 * @throws IOException
	 */
	public byte[] readNextAndRemove() throws IOException {
		assertOpen();
		
		byte[] b = null;
		try {
			b = readerHandle.read();
		} catch (FileEOFException e) {
			rotateNextLogReader();
			try {
				b = readerHandle.read();
			} catch (FileEOFException e1) {
				log.error("read new log file FileEOFException error occurred",e1);
			}
		}
		if (b != null) {
			logIndexDb.decrementQueueSize();
		}
		return b;
	}

	private void rotateNextLogReader() throws IOException {
		int deleteNum = readerHandle.getCurrentFileNumber();
		readerHandle.close();
		FileRunner.addDeleteFile(getLogEntityPath(deleteNum));
		
		int nextReaderIndex = logIndexDb.getReaderIndex() + 1;
		// 更新下一次读取的位置和索引
		logIndexDb.putReaderIndex(nextReaderIndex);
		logIndexDb.putReaderPosition(LogEntity.MESSAGE_START_POSITION);
		openReaderHandle(nextReaderIndex);
	}

	private void openReaderHandle(int index) throws IOException {
//		if (!getWriterHandle().isClosed() && getWriterHandle().getCurrentFileNumber() == index) {
//			readerHandle = getWriterHandle();
//			readerHandle.incrementUseCount();
//		} else {
			readerHandle = createLogEntity(getLogEntityPath(index), logIndexDb,index);
//		}
		log.info("open LogEntity for reader:"+index+" readerHandle:"+readerHandle);
	}
	
	public void close() {
		closed = true;
		readerHandle.close();
		getWriterHandle().close();
	}

	public int getQueuSize() {
		return (int)logIndexDb.getQueueSize();
	}

	private void setWriterHandle(LogEntity writerHandle) {
		this.writerHandle = writerHandle;
	}

	private LogEntity getWriterHandle() {
		return writerHandle;
	}

	public String toString() {
		return "LogIndex:"+logIndexDb+" Reader:"+readerHandle+" Writer:"+writerHandle;
	}
	
}
