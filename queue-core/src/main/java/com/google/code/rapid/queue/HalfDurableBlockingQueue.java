package com.google.code.rapid.queue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 半持久的BlockingQueue,部分数据放在内存中,用于提高效率;其它部分数据放在硬盘中,用于保障数据安全
 * 
 * 注意：该队列会丢失部分在内存中的数据
 * 
 * @author badqiu
 * 
 */
public class HalfDurableBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
	private int memoryCapacity; // 放在内存的容量
	BlockingQueue<E> memory;
	BlockingQueue<E> persistence;

	/**
	 * 构造函数
	 * @param memoryCapacity 内存队列大小
	 * @param persistenceQueue 持久队列
	 */
	public HalfDurableBlockingQueue(int memoryCapacity,BlockingQueue<E> persistenceQueue) {
		this(memoryCapacity,new LinkedBlockingQueue(memoryCapacity),persistenceQueue);
	}
	
	/**
	 * 构造函数
	 * @param memoryCapacity 内存队列大小
	 * @param memoryQueue 内存队列
	 * @param persistenceQueue 持久队列
	 */
	public HalfDurableBlockingQueue(int memoryCapacity,BlockingQueue<E> memoryQueue,BlockingQueue<E> persistenceQueue) {
		if(memoryCapacity <= 0) {
			throw new IllegalArgumentException("memoryCapacity must be > 0");
		}
		if(memoryQueue == null) {
			throw new IllegalArgumentException("memoryQueue must be not null");
		}
		if(persistenceQueue == null) {
			throw new IllegalArgumentException("persistenceQueue must be not null");
		}
		
		this.memoryCapacity = memoryCapacity;
		this.persistence = persistenceQueue;
		this.memory = memoryQueue;
	}
	
	@Override
	public boolean offer(E e) {
		if (memory.size() >= memoryCapacity) {
			return persistence.offer(e);
		} else {
			return memory.offer(e);
		}
	}

	@Override
	public E poll() {
		if (memory.isEmpty()) {
			return persistence.poll();
		} else {
			return memory.poll();
		}
	}

	@Override
	public E peek() {
		if (memory.isEmpty()) {
			return persistence.peek();
		} else {
			return memory.peek();
		}
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return memory.size() + persistence.size();
	}

	@Override
	public void put(E e) throws InterruptedException {
		if (memory.size() >= memoryCapacity) {
			persistence.put(e);
		} else {
			memory.put(e);
		}
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (memory.size() >= memoryCapacity) {
			return persistence.offer(e, timeout, unit);
		} else {
			return memory.offer(e, timeout, unit);
		}
	}

	@Override
	public E take() throws InterruptedException {
		if (memory.isEmpty()) {
			return persistence.take();
		} else {
			return memory.take();
		}
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		if (memory.isEmpty()) {
			return persistence.poll(timeout, unit);
		} else {
			return memory.poll(timeout, unit);
		}
	}

	@Override
	public int remainingCapacity() {
		return memory.remainingCapacity() + persistence.remainingCapacity();
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		int result = memory.drainTo(c);
		result += persistence.drainTo(c);
		return result;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		int result = memory.drainTo(c,maxElements);
		
		int secondMaxElements = maxElements - result;
		if(secondMaxElements <= 0) {
			return result;
		}
		result += persistence.drainTo(c,secondMaxElements);
		
		return result;
	}

}
