package com.google.code.rapid.queue.model;

import junit.framework.Assert;

import org.junit.Test;


public class BrokerBindingTest extends Assert{
	
	@Test
	public void test() {
		BrokerBinding binding = new BrokerBinding(null);
		binding.addRouterKey("aaaa     \n bbbb");
		assertEquals(2,binding.getRouterKeyList().size());
		assertEquals("aaaa",binding.getRouterKeyList().get(0));
		assertEquals("bbbb",binding.getRouterKeyList().get(1));
	}
	
}
