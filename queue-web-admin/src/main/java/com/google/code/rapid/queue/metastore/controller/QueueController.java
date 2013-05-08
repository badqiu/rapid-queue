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
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.query.QueueQuery;
import com.google.code.rapid.queue.metastore.service.QueueService;
import com.google.code.rapid.queue.metastore.util.BaseController;


/**
 * [Queue] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 *
 */
@Controller
@RequestMapping("/queue")
public class QueueController extends BaseController{
	
	private QueueService queueService;
	
	private final String LIST_ACTION = "redirect:/queue/index.do";
	
	/** 
	 * 增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,注意大小写
	 **/
	public void setQueueService(QueueService service) {
		this.queueService = service;
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
	public String index(ModelMap model,QueueQuery query,HttpServletRequest request) {
		Page<Queue> page = this.queueService.findPage(query);
		
		model.addAllAttributes(toModelMap(page, query));
		return "/queue/index";
	}
	
	/** 显示 */
	@RequestMapping(value="/show")
	public String show(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("vhostName") String vhostName) throws Exception {
		Queue queue = (Queue)queueService.getById(queueName,vhostName);
		model.addAttribute("queue",queue);
		return "/queue/show";
	}

	/** 进入新增 */
	@RequestMapping(value="/add")
	public String add(ModelMap model,Queue queue) throws Exception {
		model.addAttribute("queue",queue);
		return "/queue/add";
	}
	
	/** 保存新增,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping(value="/create")
	public String create(ModelMap model,Queue queue,BindingResult errors) throws Exception {
		try {
			queueService.create(queue);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			return  "/queue/add";
		}catch(MessageException e) {
			Flash.current().error(e.getMessage());
			return  "/queue/add";
		}
		Flash.current().success(CREATED_SUCCESS); //存放在Flash中的数据,在下一次http请求中仍然可以读取数据,error()用于显示错误消息
		return LIST_ACTION;
	}
	
	/** 编辑 */
	@RequestMapping(value="/edit")
	public String edit(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("vhostName") String vhostName) throws Exception {
		Queue queue = (Queue)queueService.getById(queueName,vhostName);
		model.addAttribute("queue",queue);
		return "/queue/edit";
	}
	
	/** 保存更新,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping(value="/update")
	public String update(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("vhostName") String vhostName,Queue queue,BindingResult errors) throws Exception {
		try {
			queueService.update(queue);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			return  "/queue/edit";
		}catch(MessageException e) {
			Flash.current().error(e.getMessage());
			return  "/queue/edit";
		}
		Flash.current().success(UPDATE_SUCCESS);
		return LIST_ACTION;
	}
	
	/** 批量删除 */
	@RequestMapping(value="/delete")
	public String delete(ModelMap model,@RequestParam("queueName") String queueName, @RequestParam("vhostName") String vhostName) {
		queueService.removeById(queueName,vhostName);
		Flash.current().success(DELETE_SUCCESS);
		return LIST_ACTION;
	}
	
}

