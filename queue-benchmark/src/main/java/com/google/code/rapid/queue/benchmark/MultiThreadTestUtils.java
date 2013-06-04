package com.google.code.rapid.queue.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadTestUtils {
	
	/**
	 * 执行测试并等待执行结束,返回值为消耗时间
	 * 
	 * @param threadCount 线程数
	 * @param task 任务
	 * @return costTime
	 * @throws InterruptedException 
	 */
	@Deprecated
	public static long executeAndWait(int threadCount,final Runnable task) throws InterruptedException {
		return execute(threadCount,task);
	}
	
	/**
	 * 执行测试并等待执行结束,返回值为消耗时间
	 * 
	 * @param threadCount 线程数
	 * @param task 任务
	 * @return costTime
	 * @throws InterruptedException 
	 */
	public static long execute(int threadCount,final Runnable task) throws InterruptedException {
		CountDownLatch doneSignal = execute0(threadCount, task);
		long startTime = System.currentTimeMillis();
		doneSignal.await();
		return System.currentTimeMillis() - startTime;
	}

	private static CountDownLatch execute0(final int threadCount,final Runnable task) {
		final AtomicInteger doneCount = new AtomicInteger(0);
		final AtomicInteger startCount = new AtomicInteger(0);
		final CountDownLatch doneSignal = new CountDownLatch(threadCount);
		for(int i = 0; i < threadCount; i++) {
			Thread t = new Thread(){
				public void run() {
					long start = System.currentTimeMillis();
					try {
//						System.out.println("task started,startCount:"+startCount.incrementAndGet()+" threadCount:"+threadCount+" doneCount:"+doneCount);
						task.run();
					}finally {
						doneSignal.countDown();
//						long cost = System.currentTimeMillis() - start;
//						System.out.println("task done,startCount:"+startCount+" threadCount:"+threadCount+" doneCount:"+doneCount.incrementAndGet()+" cost:"+cost);
					}
				}
			};
			
			t.start();
		}
		
		return doneSignal;
	}
	
}