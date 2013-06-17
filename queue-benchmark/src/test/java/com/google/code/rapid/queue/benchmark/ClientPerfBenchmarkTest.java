package com.google.code.rapid.queue.benchmark;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;


public class ClientPerfBenchmarkTest extends junit.framework.Assert{

	ClientPerfBenchmark b = new ClientPerfBenchmark();
	@Test
	public void test() throws Exception {
		System.out.println(RandomStringUtils.randomAlphabetic(100).getBytes().length);
		System.out.println("中国".getBytes().length);
		assertTrue(b.test_perf_one_row());
	}
}
