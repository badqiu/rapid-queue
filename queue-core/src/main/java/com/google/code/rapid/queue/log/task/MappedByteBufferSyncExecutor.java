package com.google.code.rapid.queue.log.task;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 同步 MappedByteBuffer的后台进程
 * 
 * @author badqiu
 *
 */
public class MappedByteBufferSyncExecutor {
	private static Logger logger = LoggerFactory.getLogger(MappedByteBufferSyncExecutor.class);
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private List<MappedByteBuffer> bufList = new ArrayList<MappedByteBuffer>();
	
	private static MappedByteBufferSyncExecutor instance = new MappedByteBufferSyncExecutor();
	
	private MappedByteBufferSyncExecutor() {
		start();
	}
	
	public static MappedByteBufferSyncExecutor getInstance() {
		return instance;
	}
	

	public synchronized boolean add(MappedByteBuffer e) {
		return bufList.add(e);
	}

	public synchronized boolean remove(MappedByteBuffer o) {
		return bufList.remove(o);
	}
	
	public int size() {
		return bufList.size();
	}

	public void start() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				for(MappedByteBuffer buf : bufList) {
					try {
						buf.force();
					}catch(Exception e) {
						logger.warn("MappedByteBuffer.force() sync error",e);
					}
				}
			}
		};
		
		executor.scheduleAtFixedRate(task, 10, 10, TimeUnit.MILLISECONDS);
	}
}
