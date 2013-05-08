/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2013
 */


package com.google.code.rapid.queue.metastore.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.web.scope.Flash;

import com.google.code.rapid.queue.metastore.MessageException;
import com.google.code.rapid.queue.metastore.model.Binding;
import com.google.code.rapid.queue.metastore.query.BindingQuery;
import com.google.code.rapid.queue.metastore.service.BindingService;
import com.google.code.rapid.queue.metastore.util.BaseController;


/**
 * [Binding] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 *
 */
@Controller
@RequestMapping("/binding")
public class BindingController extends BaseController{
	
	private BindingService bindingService;
	
	private final String LIST_ACTION = "redirect:/binding/index.do";
	
	/** 
	 * 增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,注意大小写
	 **/
	public void setBindingService(BindingService service) {
		this.bindingService = service;
	}
	
	/** binder用于bean属性的设置 */
	@InitBinder  
	public void initBinder(WebDataBinder binder) {  
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));  
	}
	   
	/**
	 * 增加了@ModelAttribute的方法可以在本controller方法调用前执行,可以存放一些共享变量,如枚举值,或是一些初始化操作
	 */
	@ModelAttribute
	public void init(ModelMap model) {
	}
	
	/** 列表 */
	@RequestMapping(value="/index")
	public String index(ModelMap model,BindingQuery query,HttpServletRequest request) {
		Page<Binding> page = this.bindingService.findPage(query);
		
		model.addAllAttributes(toModelMap(page, query));
		return "/binding/index";
	}
	
	/** 显示 */
	@RequestMapping(value="/show")
	public String show(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("exchangeName") String exchangeName, @RequestParam("vhostName") String vhostName) throws Exception {
		Binding binding = (Binding)bindingService.getById(queueName,exchangeName,vhostName);
		model.addAttribute("binding",binding);
		return "/binding/show";
	}

	/** 进入新增 */
	@RequestMapping(value="/add")
	public String add(ModelMap model,Binding binding) throws Exception {
		model.addAttribute("binding",binding);
		return "/binding/add";
	}
	
	/** 保存新增,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping(value="/create")
	public String create(ModelMap model,Binding binding,BindingResult errors) throws Exception {
		try {
			bindingService.create(binding);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			return  "/binding/add";
		}catch(MessageException e) {
			Flash.current().error(e.getMessage());
			return  "/binding/add";
		}
		Flash.current().success(CREATED_SUCCESS); //存放在Flash中的数据,在下一次http请求中仍然可以读取数据,error()用于显示错误消息
		return LIST_ACTION;
	}
	
	/** 编辑 */
	@RequestMapping(value="/edit")
	public String edit(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("exchangeName") String exchangeName, @RequestParam("vhostName") String vhostName) throws Exception {
		Binding binding = (Binding)bindingService.getById(queueName,exchangeName,vhostName);
		model.addAttribute("binding",binding);
		return "/binding/edit";
	}
	
	/** 保存更新,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping(value="/update")
	public String update(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("exchangeName") String exchangeName, @RequestParam("vhostName") String vhostName,Binding binding,BindingResult errors) throws Exception {
		try {
			bindingService.update(binding);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			return  "/binding/edit";
		}catch(MessageException e) {
			Flash.current().error(e.getMessage());
			return  "/binding/edit";
		}
		Flash.current().success(UPDATE_SUCCESS);
		return LIST_ACTION;
	}
	
	/** 批量删除 */
	@RequestMapping(value="/delete")
	public String delete(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("exchangeName") String exchangeName, @RequestParam("vhostName") String vhostName) {
		bindingService.removeById(queueName,exchangeName,vhostName);
		Flash.current().success(DELETE_SUCCESS);
		return LIST_ACTION;
	}
	
}

