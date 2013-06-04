package com.google.code.rapid.queue.benchmark;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class SimpleObjectPool<E> extends LinkedBlockingDeque<E> {

	private static final long serialVersionUID = 1L;

	public SimpleObjectPool(int poolSize,ObjectFactory<E> factory) {
		super(poolSize);
		for(int i = 0; i < poolSize; i++) {
			try {
				offer((E)factory.makeObject());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static interface ObjectFactory<E> {
		public E makeObject() throws Exception;
	}
	
	public E borrowObject() {
		try {
			return poll(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public void returnObject(E e) {
		offer(e);
	}
	
}
