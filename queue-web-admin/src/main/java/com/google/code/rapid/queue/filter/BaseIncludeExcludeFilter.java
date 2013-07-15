package com.google.code.rapid.queue.filter;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class BaseIncludeExcludeFilter extends OncePerRequestFilter implements Filter{

	static protected AntPathMatcher antPathMatcher = new AntPathMatcher();
	protected Set<String> includeSet = new HashSet<String>();
	protected Set<String> excludeSet = new HashSet<String>();

	@Override
	protected void initFilterBean() throws ServletException {
		includeSet = splitForSet(getFilterConfig(),"includes");
		excludeSet = splitForSet(getFilterConfig(),"excludes");
		logger.info("includeSet:" + includeSet+" excludeSet:"+excludeSet);
	}

	private HashSet<String> splitForSet(FilterConfig config,String paramName) {
		String value = getFilterConfig().getInitParameter(paramName);
		HashSet<String> set = new HashSet<String>();
		if (StringUtils.hasText(value)) {
			for (String str : StringUtils.tokenizeToStringArray(value, ", \t\n")) {
				set.add(str);
			}
		}
		return set;
	}
	
	public static boolean isMatch(HttpServletRequest request,Set<String> patterns) {
		String path = request.getServletPath();
		String requestURI = request.getRequestURI().substring(request.getContextPath().length());
		return isMatch(path,patterns) || isMatch(requestURI,patterns);
	}

	public static boolean isMatch(String path,Set<String> patterns) {
		if(patterns.contains(path)) {
			for(String pathern : patterns) {
				if(antPathMatcher.match(pathern, path)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
