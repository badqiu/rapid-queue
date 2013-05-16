package com.google.code.rapid.queue;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 基于文件系统的持久化阻塞队列
 * 
 * @author badqiu
 *
 */
public class DurableBlockingQueue extends DurableQueue implements BlockingQueue<byte[]> {
	private static final long serialVersionUID = 704113155173708184L;
	
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition notEmpty = lock.newCondition();
	
	public DurableBlockingQueue(String path) {
		super(path);
	}

	@Override
	public boolean add(byte[] e) {
		return offer(e);
	}
	
	@Override
	public void put(byte[] e) throws InterruptedException {
		if(e == null) throw new NullPointerException();
		
		offer(e);
	}
	
	@Override
	public boolean offer(byte[] e) {
		if(e == null) throw new NullPointerException();
		try {
			lock.lock();
			boolean result = super.offer(e);
			notEmpty.signalAll();
			return result;
		}finally {
			lock.unlock();
		}
	}

	@Override
	public boolean offer(byte[] e, long timeout, TimeUnit unit) throws InterruptedException {
		if(e == null) throw new NullPointerException();
		
		return offer(e);
	}

	@Override
	public byte[] take() throws InterruptedException {
		return poll(-1,null);
	}

	@Override
	public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
		byte[] b = poll();
		if(b == null) {
			await(timeout, unit);
			b = poll();
		}
		return b;
	}
	
//	@Override
//	public byte[] peek() {
//		byte[] b = super.peek();
//		if(b == null) {
//			try {
//				await(-1, null);
//			}catch(Exception e) {
//				//ignore
//			}
//			b = super.peek();
//		}
//		return b;
//	}
//	
	private void await(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			lock.lock();
			if(timeout <= 0) {
				notEmpty.await();
			}else {
				notEmpty.await(timeout, unit);
			}
		}finally {
			lock.unlock();
		}
	}

	@Override
	public int remainingCapacity() {
		return Integer.MAX_VALUE - size();
	}

	@Override
	public int drainTo(Collection<? super byte[]> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super byte[]> c, int maxElements) {
		throw new UnsupportedOperationException();
	}

}
