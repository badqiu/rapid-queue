package com.google.code.rapid.queue.model;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;


public class BrokerBindingTest extends Assert{
	
	@Test
	public void test() {
		BrokerBinding binding = new BrokerBinding(null);
		binding.updateRouterKey("aaaa     \n bbbb");
		assertEquals(2,binding.getRouterKeyList().size());
		Iterator<String> iterator = binding.getRouterKeyList().iterator();
		assertEquals("bbbb",iterator.next());
		assertEquals("aaaa",iterator.next());
	}
	
}
