package com.google.code.rapid.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 半持久的Queue,部分数据放在内存中,用于提高效率;其它部分数据放在硬盘中,用于保障数据安全
 * 
 * 注意：该队列会丢失部分在内存中的数据
 * 
 * @author badqiu
 * 
 */
public class HalfDurableQueue<E> extends AbstractQueue<E> implements Queue<E> {
	private int memoryCapacity; // 放在内存的容量
	Queue<E> memory;
	Queue<E> persistence;
	
	/**
	 * 构造函数
	 * @param memoryCapacity 内存队列大小
	 * @param persistenceQueue 持久队列
	 */
	public HalfDurableQueue(int memoryCapacity,Queue<E> persistenceQueue) {
		this(memoryCapacity,new LinkedList<E>(),persistenceQueue);
	}

	/**
	 * 构造函数
	 * @param memoryCapacity 内存队列大小
	 * @param persistenceQueue 持久队列
	 */
	public HalfDurableQueue(int memoryCapacity,Queue<E> memoryQueue,Queue<E> persistenceQueue) {
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
		if (persistence.isEmpty()) {
			return memory.peek();
		} else {
			return persistence.peek();
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

}
