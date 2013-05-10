package com.google.code.rapid.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 缓冲数据在内存中,半持久的Queue,部分数据放在内存中,用于提高效率;其它部分数据放在硬盘中,用于保障数据安全
 * 
 * 注意：该队列会丢失部分在内存中的数据
 * 
 * @author badqiu
 * 
 */
public class BufferedQueue<E> extends AbstractQueue<E> implements Queue<E> {
	private int bufferSize; // 放在内存的容量
	Queue<E> buffer;
	Queue<E> target;
	
	/**
	 * 构造函数
	 * @param bufferSize 内存队列大小
	 * @param targetQueue 持久队列
	 */
	public BufferedQueue(int bufferSize,Queue<E> targetQueue) {
		this(bufferSize,new LinkedList<E>(),targetQueue);
	}

	/**
	 * 构造函数
	 * @param bufferSize 内存队列大小
	 * @param targetQueue 持久队列
	 */
	public BufferedQueue(int bufferSize,Queue<E> bufferQueue,Queue<E> targetQueue) {
		if(bufferSize <= 0) {
			throw new IllegalArgumentException("bufferSize must be > 0");
		}
		if(bufferQueue == null) {
			throw new IllegalArgumentException("bufferQueue must be not null");
		}
		if(targetQueue == null) {
			throw new IllegalArgumentException("targetQueue must be not null");
		}
		
		this.bufferSize = bufferSize;
		this.target = targetQueue;
		this.buffer = bufferQueue;
	}
	
	@Override
	public boolean offer(E e) {
		if (buffer.size() >= bufferSize) {
			return target.offer(e);
		} else {
			return buffer.offer(e);
		}
	}

	@Override
	public E poll() {
		if (buffer.isEmpty()) {
			return target.poll();
		} else {
			return buffer.poll();
		}
	}

	@Override
	public E peek() {
		if (target.isEmpty()) {
			return buffer.peek();
		} else {
			return target.peek();
		}
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return buffer.size() + target.size();
	}

}
