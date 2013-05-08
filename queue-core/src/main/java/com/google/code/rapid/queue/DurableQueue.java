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

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于文件系统的持久化队列
 * 
 * @author badqiu
 * @date 2010-8-13
 */
public class DurableQueue extends AbstractQueue<byte[]> implements Queue<byte[]>,
		java.io.Serializable {
	private static final long serialVersionUID = -5960741434564940154L;
	private FileQueue fsQueue = null;
	final Logger log = LoggerFactory.getLogger(DurableQueue.class);
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock writeLock = lock.writeLock();

	public DurableQueue(String path) {
		try {
			fsQueue = new FileQueue(path);
		} catch (Exception e) {
			throw new RuntimeException("create durable queue error,path:"+path,e);
		}
	}

	public DurableQueue(String path, int logsize) {
		try {
			fsQueue = new FileQueue(path, logsize);
		} catch (Exception e) {
			throw new RuntimeException("create durable queue error,path:"+path,e);
		}
	}

	@Override
	public Iterator<byte[]> iterator() {
		throw new UnsupportedOperationException("iterator Unsupported now");
	}

	@Override
	public int size() {
		return fsQueue.getQueuSize();
	}

	@Override
	public boolean offer(byte[] item) {
		try {
			writeLock.lock();
			fsQueue.add(item);
			return true;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
	}

	@Override
	public byte[] peek() {
		throw new UnsupportedOperationException("peek Unsupported now");
	}

	@Override
	public byte[] poll() {
		try {
			writeLock.lock();
			return fsQueue.readNextAndRemove();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			writeLock.unlock();
		}
	}

	public void close() {
		if (fsQueue != null) {
			fsQueue.close();
		}
	}
	
	@Override
	public String toString() {
		return fsQueue.toString();
	}
	
}
