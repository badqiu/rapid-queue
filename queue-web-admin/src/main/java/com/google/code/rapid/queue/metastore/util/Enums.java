package com.google.code.rapid.queue.metastore.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.ModelMap;

public class Enums {

	public static void putDurableTypeEnum(ModelMap model) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("MEMORY", "MEMORY");
		map.put("DURABLE", "DURABLE");
		map.put("HALF_DURABLE", "HALF_DURABLE");
		model.addAttribute("DurableTypeEnum", map);
	}
	
}
