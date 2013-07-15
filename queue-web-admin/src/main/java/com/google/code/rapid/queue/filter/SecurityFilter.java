package com.google.code.rapid.queue.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.google.code.rapid.queue.Constants;

public class SecurityFilter extends BaseIncludeExcludeFilter implements Filter{

	private String loginPage = "/login.jsp";
	public static String RETURN_URL = "returnUrl";
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		excludeSet.add(loginPage);
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if(!isMatch(request, excludeSet)) {
			String username = (String)request.getSession().getAttribute(Constants.ADMIN_LOGIN_USER);
			if(StringUtils.isBlank(username)) {
				response.sendRedirect(request.getContextPath()+loginPage+"?"+RETURN_URL+"="+request.getRequestURL());
				return;
			}
			filterChain.doFilter(request, response);
		}else {
			filterChain.doFilter(request, response);
		}
	}

}
