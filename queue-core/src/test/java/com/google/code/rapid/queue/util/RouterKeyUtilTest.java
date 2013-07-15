package com.google.code.rapid.queue.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class RouterKeyUtilTest extends Assert{
	Map map = new HashMap();
	@Test
	public void test_map_perf() {
		map.put("111", new Object());
		int count = 100000000;
		long start = System.currentTimeMillis();
		for(int i = 0; i < count; i++) {
			map.get("111");
		}
		printTPS(count, start);
	}
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
		
		System.out.println("count:"+count+" tps:"+(long)(count * 1000.0 / cost));
	}
	
	@Test
	public void test() {
		assertTrue(RouterKeyUtil.matchRouterKey("yygame.*.s1", "yygame.ddt.s1"));
		assertFalse(RouterKeyUtil.matchRouterKey("yygame.*.s2", "yygame.ddt.s1"));
		
	}
}
