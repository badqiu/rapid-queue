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
import com.google.code.rapid.queue.metastore.model.Vhost;
import com.google.code.rapid.queue.metastore.query.VhostQuery;
import com.google.code.rapid.queue.metastore.service.VhostService;
import com.google.code.rapid.queue.metastore.util.BaseController;


/**
 * [Vhost] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 *
 */
@Controller
@RequestMapping("/vhost")
public class VhostController extends BaseController {
	
	private VhostService vhostService;
	
	private final String LIST_ACTION = "redirect:/vhost/index.do";
	
	/** 
	 * 增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,注意大小写
	 **/
	public void setVhostService(VhostService service) {
		this.vhostService = service;
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
	@RequestMapping()
	public String index(ModelMap model,VhostQuery query,HttpServletRequest request) {
		Page<Vhost> page = this.vhostService.findPage(query);
		
		model.addAllAttributes(toModelMap(page, query));
		return "/vhost/index";
	}
	
	/** 显示 */
	@RequestMapping()
	public String show(ModelMap model,@RequestParam("vhostName") String vhostName) throws Exception {
		Vhost vhost = (Vhost)vhostService.getById(vhostName);
		model.addAttribute("vhost",vhost);
		return "/vhost/show";
	}

	/** 进入新增 */
	@RequestMapping()
	public String add(ModelMap model,Vhost vhost) throws Exception {
		model.addAttribute("vhost",vhost);
		return "/vhost/add";
	}
	
	/** 保存新增,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping()
	public String create(ModelMap model,Vhost vhost,BindingResult errors) throws Exception {
		try {
			vhostService.create(vhost);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			logger.info("ConstraintViolationException,"+e.getConstraintViolations(),e);
			return  "/vhost/add";
		}catch(MessageException e) {
			logger.info("message error"+e,e);
			Flash.current().error(e.getMessage());
			return  "/vhost/add";
		}
		Flash.current().success(CREATED_SUCCESS); //存放在Flash中的数据,在下一次http请求中仍然可以读取数据,error()用于显示错误消息
		return LIST_ACTION;
	}
	
	/** 编辑 */
	@RequestMapping()
	public String edit(ModelMap model,@RequestParam("vhostName") String vhostName) throws Exception {
		Vhost vhost = (Vhost)vhostService.getById(vhostName);
		model.addAttribute("vhost",vhost);
		return "/vhost/edit";
	}
	
	/** 保存更新,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping()
	public String update(ModelMap model,@RequestParam("vhostName") String vhostName,Vhost vhost,BindingResult errors) throws Exception {
		try {
			vhostService.update(vhost);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			return  "/vhost/edit";
		}catch(MessageException e) {
			Flash.current().error(e.getMessage());
			return  "/vhost/edit";
		}
		Flash.current().success(UPDATE_SUCCESS);
		return LIST_ACTION;
	}
	
	/** 批量删除 */
	@RequestMapping()
	public String delete(ModelMap model,@RequestParam("vhostName") String vhostName) {
		vhostService.removeById(vhostName);
		Flash.current().success(DELETE_SUCCESS);
		return LIST_ACTION;
	}
	
}

