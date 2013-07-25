package com.google.code.rapid.queue.metastore.util;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.code.rapid.queue.Constants;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.util.ValidationErrorsUtils;
import cn.org.rapid_framework.util.page.PageQuery;

public class BaseController {
	protected static Logger logger = LoggerFactory.getLogger(BaseController.class); 
	public static String UPDATE_SUCCESS = "UPDATE_SUCCESS";
	public static String DELETE_SUCCESS = "DELETE_SUCCESS";
	public static String CREATED_SUCCESS = "CREATED_SUCCESS";
	
	protected ModelMap toModelMap(Page page, PageQuery query) {
		return SpringMVCUtils.toModelMap(page, query);
	}

	protected HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/**
	 * 将exception.getConstraintViolations()填充至 Errors
	 */	
	public static void convert(ConstraintViolationException exception,Errors errors) {
		ValidationErrorsUtils.convert(exception.getConstraintViolations(),errors);
	}

	protected String getLoginUser() {
		return (String)getRequest().getSession().getAttribute(Constants.ADMIN_LOGIN_USER);
	}
}
