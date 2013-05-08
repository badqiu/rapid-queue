package com.google.code.rapid.queue.metastore.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.util.page.PageQuery;

public class SpringMVCUtils {
	
	public static ModelMap toModelMap(Page<?> page, PageQuery query) {
		return toModelMap("", page, query);
	}

	public static ModelMap toModelMap(String tableId, Page<?> page,
			PageQuery query) {
		ModelMap model = new ModelMap();
		saveIntoModelMap(tableId, page, query, model);
		return model;
	}

	/**
	 * 用于一个页面有多个extremeTable是使用
	 * 
	 * @param tableId
	 *            等于extremeTable的tableId属性
	 */
	public static void saveIntoModelMap(String tableId, Page page,
			PageQuery query, ModelMap model) {
		Assert.notNull(tableId, "tableId must be not null");
		Assert.notNull(page, "page must be not null");

		model.addAttribute(tableId + "page", page);
		model.addAttribute(tableId + "query", query);
	}

	public static <T> T getOrCreateRequestAttribute(HttpServletRequest request,
			String key, Class<T> clazz) {
		Object value = request.getAttribute(key);
		if (value == null) {
			try {
				value = clazz.newInstance();
			} catch (Exception e) {
				ReflectionUtils.handleReflectionException(e);
			}
			request.setAttribute(key, value);
		}
		return (T) value;
	}
}
