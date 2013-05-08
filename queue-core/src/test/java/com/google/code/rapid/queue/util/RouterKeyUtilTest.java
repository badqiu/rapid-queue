package com.google.code.rapid.queue.util;

import org.junit.Assert;
import org.junit.Test;


public class RouterKeyUtilTest extends Assert{
	
	@Test
	public void test_perf() {
		int count = 1000000;
		long start = System.currentTimeMillis();
		for(int i = 0; i < count; i++) {
			assertTrue(RouterKeyUtil.matchRouterKey("yygame.*.s1", "yygame.ddt.s1"));
		}
		printTPS(count, start);
	}

	private void printTPS(int count, long start) {
		long cost = System.currentTimeMillis() - start;
		
		System.out.println("count:"+count+" tps:"+(count * 1000.0 / cost));
	}
	
	@Test
	public void test() {
		assertTrue(RouterKeyUtil.matchRouterKey("yygame.*.s1", "yygame.ddt.s1"));
		assertFalse(RouterKeyUtil.matchRouterKey("yygame.*.s2", "yygame.ddt.s1"));
		
	}
}
