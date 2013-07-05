package com.google.code.rapid.queue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 缓冲数据在内存中,半持久的BlockingQueue,部分数据放在内存中,用于提高效率;其它部分数据放在硬盘中,用于保障数据安全
 * 
 * 注意：该队列会丢失部分在内存中的数据
 * 
 * @author badqiu
 * 
 */
public class BufferedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
	private int bufferSize; // 放在内存的容量
	BlockingQueue<E> buffer;
	BlockingQueue<E> target;

	/**
	 * 构造函数
	 * @param bufferSize 内存队列大小
	 * @param persistenceQueue 持久队列
	 */
	public BufferedBlockingQueue(int bufferSize,BlockingQueue<E> targetQueue) {
		this(bufferSize,new LinkedBlockingQueue(bufferSize),targetQueue);
	}
	
	/**
	 * 构造函数
	 * @param bufferSize 内存队列大小
	 * @param bufferQueue 内存队列
	 * @param targetQueue 持久队列
	 */
	public BufferedBlockingQueue(int bufferSize,BlockingQueue<E> bufferQueue,BlockingQueue<E> targetQueue) {
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
		if(target.isEmpty()){
			if (buffer.size() >= bufferSize) {
				return target.offer(e);
			} else {
				return buffer.offer(e);
			}
		}else {
			return target.offer(e);
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
		if (buffer.isEmpty()) {
			return target.peek();
		} else {
			return buffer.peek();
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

	@Override
	public void put(E e) throws InterruptedException {
		if (buffer.size() >= bufferSize) {
			target.put(e);
		} else {
			buffer.put(e);
		}
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (buffer.size() >= bufferSize) {
			return target.offer(e, timeout, unit);
		} else {
			return buffer.offer(e, timeout, unit);
		}
	}

	@Override
	public E take() throws InterruptedException {
		if (buffer.isEmpty() && target.isEmpty()) {
			return buffer.take();
		} else if(!target.isEmpty()) {
			return target.take();
		}else {
			return buffer.take();
		}
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		if (buffer.isEmpty()) {
			return target.poll(timeout, unit);
		} else {
			return buffer.poll(timeout, unit);
		}
	}

	@Override
	public int remainingCapacity() {
		return buffer.remainingCapacity() + target.remainingCapacity();
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		int result = buffer.drainTo(c);
		result += target.drainTo(c);
		return result;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		int result = buffer.drainTo(c,maxElements);
		
		int secondMaxElements = maxElements - result;
		if(secondMaxElements <= 0) {
			return result;
		}
		result += target.drainTo(c,secondMaxElements);
		
		return result;
	}

}
