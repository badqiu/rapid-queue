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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

/**
 * @author badqiu
 * @date 2010-8-13
 * @version $Id: FSQueueTest.java 42 2012-08-16 03:00:00Z badqiu1223@gmail.com $
 */
public class FileQueueTest extends TestCase {
    private static DurableQueue queue;
    static {
        try {
        	System.out.println("new DurableQueue:db");
            queue = new DurableQueue("target/test_db/db");
            queue.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void tesssstCrash() {
        queue.offer("testqueueoffer".getBytes());
        System.exit(9);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testOffer() {
        queue.offer("testqueueoffer".getBytes());
        assertEquals(new String(queue.poll()), "testqueueoffer");
    }

    public void testPoll() {
        queue.offer("testqueuepoll".getBytes());
        assertEquals(new String(queue.poll()), "testqueuepoll");
    }

    public void testAdd() {
        queue.add("testqueueadd".getBytes());
        assertEquals(new String(queue.poll()), "testqueueadd");
    }

    public void testAll() {
        queue.add("test1".getBytes());
        queue.add("test2".getBytes());
        assertEquals(new String(queue.poll()), "test1");
        queue.add("test3".getBytes());
        queue.add("test4".getBytes());
        assertEquals(new String(queue.poll()), "test2");
        assertEquals(new String(queue.poll()), "test3");
        System.out.println(new String(queue.poll()));
        StringBuffer sBuffer = new StringBuffer(1024);
        for (int i = 0; i < 1024; i++) {
            sBuffer.append("a");
        }
        String string = sBuffer.toString();
        assertEquals(0, queue.size());
        for (int i = 0; i < 100000; i++) {
            byte[] b = (string + i).getBytes();
            queue.offer(b);
        }
        assertEquals(100000, queue.size());
        for (int i = 0; i < 100000; i++) {
            if (i == 85301) {
                System.out.println(i);
            }
            byte[] b = queue.poll();
            if (b == null) {
                i--;
                System.out.println("null" + i);
                continue;
            }
            assertEquals(new String(b), (string + i));
        }
        queue.add("123".getBytes());
        queue.add("123".getBytes());
        assertEquals(queue.size(), 2);
        queue.clear();
        assertNull(queue.poll());
    }

    public void testFqueueVSList() {
        String message = "1234567890";
        byte[] bytes = message.getBytes();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            queue.add(bytes);
        }
        System.out.println("Fqueue写入10字节10000000次:" + (System.currentTimeMillis() - start));
        queue.clear();
        List<byte[]> list = new LinkedList<byte[]>();
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            list.add(bytes);
        }
        System.out.println("LinkedList写入10字节10000000次:" + (System.currentTimeMillis() - start));
    }

    public void testPerformance() {
        StringBuffer sBuffer = new StringBuffer(1024);
        for (int i = 0; i < 1024; i++) {
            sBuffer.append("a");
        }
        String string = sBuffer.toString();
        System.out.println("Test write 1000000 times 1K data to queue");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            byte[] b = (string + i).getBytes();
            queue.offer(b);
        }
        System.out.println("spend time:" + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Test read 1000000 times 1K data from queue");
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {

            byte[] b = queue.poll();
            if (b == null) {
                i--;
                System.out.println("null" + i);
                continue;
            }
        }
        assertEquals(0, queue.size());
        System.out.println("spend:" + (System.currentTimeMillis() - start) + "ms");
    }

    int count = 400000;
    public void testPerformance2() {
        String string = StringUtils.repeat("a", 1024);
        
		offer(queue,count,string);
        
        poll(queue,count);
        
        assertEquals(0, queue.size());
    }

	private void poll(Queue queue,int count) {
		System.out.println("Test read "+count+" times from queue");
		long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            byte[] b = (byte[])queue.poll();
            if (b == null) {
                i--;
                System.out.println("null" + i);
                break;
            }
        }
        printTps(System.currentTimeMillis() - start,count);
	}

	private void offer(Queue queue,int count,String string) {
		System.out.println("Test write "+count+" times "+string.getBytes().length + " Bytes data to queue,");
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
            byte[] b = (string + i).getBytes();
            queue.offer(b);
        }
        printTps(System.currentTimeMillis() - start,count);
	}

    public void test_reader_perf() {
        poll(queue,count);
    }
    
    public static void printTps(long cost,long count) {
    	System.out.println("spend time:"+cost+"ms"+" tps:"+(count * 1000.0 / cost));
    }
    
}
