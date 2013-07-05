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
import com.google.code.rapid.queue.metastore.model.User;
import com.google.code.rapid.queue.metastore.query.UserQuery;
import com.google.code.rapid.queue.metastore.service.UserService;
import com.google.code.rapid.queue.metastore.util.BaseController;


/**
 * [User] 的业务操作
 * 
 * @author badqiu email:badqiu(a)gmail.com
 * @version 1.0
 * @since 1.0
 *
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{
	
	private UserService userService;
	
	private final String LIST_ACTION = "redirect:/user/index.do";
	
	/** 
	 * 增加setXXXX()方法,spring就可以通过autowire自动设置对象属性,注意大小写
	 **/
	public void setUserService(UserService service) {
		this.userService = service;
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
	public String index(ModelMap model,UserQuery query,HttpServletRequest request) {
		Page<User> page = this.userService.findPage(query);
		
		model.addAllAttributes(toModelMap(page, query));
		return "/user/index";
	}
	
	/** 显示 */
	@RequestMapping()
	public String show(ModelMap model,@RequestParam("username") String username) throws Exception {
		User user = (User)userService.getById(username);
		model.addAttribute("user",user);
		return "/user/show";
	}

	/** 进入新增 */
	@RequestMapping()
	public String add(ModelMap model,User user) throws Exception {
		model.addAttribute("user",user);
		return "/user/add";
	}
	
	/** 保存新增,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping()
	public String create(ModelMap model,User user,BindingResult errors) throws Exception {
		try {
			userService.create(user);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			logger.info("ConstraintViolationException,"+e.getConstraintViolations(),e);
			return  "/user/add";
		}catch(MessageException e) {
			logger.info("message error"+e,e);
			Flash.current().error(e.getMessage());
			return  "/user/add";
		}
		Flash.current().success(CREATED_SUCCESS); //存放在Flash中的数据,在下一次http请求中仍然可以读取数据,error()用于显示错误消息
		return LIST_ACTION;
	}
	
	/** 编辑 */
	@RequestMapping()
	public String edit(ModelMap model,@RequestParam("username") String username) throws Exception {
		User user = (User)userService.getById(username);
		model.addAttribute("user",user);
		return "/user/edit";
	}
	
	/** 保存更新,@Valid标注spirng在绑定对象时自动为我们验证对象属性并存放errors在BindingResult  */
	@RequestMapping()
	public String update(ModelMap model,@RequestParam("username") String username,User user,BindingResult errors) throws Exception {
		try {
			userService.update(user);
		}catch(ConstraintViolationException e) {
			convert(e, errors);
			return  "/user/edit";
		}catch(MessageException e) {
			Flash.current().error(e.getMessage());
			return  "/user/edit";
		}
		Flash.current().success(UPDATE_SUCCESS);
		return LIST_ACTION;
	}
	
	/** 批量删除 */
	@RequestMapping()
	public String delete(ModelMap model,@RequestParam("username") String username) {
		userService.removeById(username);
		Flash.current().success(DELETE_SUCCESS);
		return LIST_ACTION;
	}
	
}

