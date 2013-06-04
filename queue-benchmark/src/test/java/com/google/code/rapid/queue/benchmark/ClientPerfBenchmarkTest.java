package com.google.code.rapid.queue.benchmark;

import org.junit.Test;


public class ClientPerfBenchmarkTest extends junit.framework.Assert{

	ClientPerfBenchmark b = new ClientPerfBenchmark();
	@Test
	public void test() throws Exception {
		assertTrue(b.test_perf_one_row());
	}
}
