package com.google.code.rapid.queue.springmvc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
/**
 * 约定大于配置的@RequestMapping映射器
 * 修改如下:
 * <pre>
 * 1.避免每一个方法都需要写@RequestMapping value,原来 @RequestMapping(value="create") => @RequestMapping,具体描述如下
 * @RequestMapping()
 * public String create(ModelMap model)
 * 将自动映射为
 * /create/*
 * 
 * 2.
 * </pre>
 * 
 * 
 * @author badqiu
 *
 */
public class ConventionAnnotationHandlerMapping extends DefaultAnnotationHandlerMapping {
	
	public ConventionAnnotationHandlerMapping() {
		setUseDefaultSuffixPattern(false); //避免  sample.do 再增加 sample.do/.*,这个性能很差
	}
	
	/**
	 * Derive URL mappings from the handler's method-level mappings.
	 * @param handlerType the handler type to introspect
	 * @param hasTypeLevelMapping whether the method-level mappings are nested
	 * within a type-level mapping
	 * @return the array of mapped URLs
	 */
	@Override
	protected String[] determineUrlsForHandlerMethods(Class<?> handlerType, final boolean hasTypeLevelMapping) {
		String[] subclassResult = determineUrlsForHandlerMethods(handlerType);
		if (subclassResult != null) {
			return subclassResult;
		}

		final Set<String> urls = new LinkedHashSet<String>();
		Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
		handlerTypes.add(handlerType);
		List interfaces = Arrays.asList(handlerType.getInterfaces());
		handlerTypes.addAll(interfaces);
		for (Class<?> currentHandlerType : handlerTypes) {
			ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
				public void doWith(Method method) {
					RequestMapping mapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
					if (mapping != null) {
						String[] mappedPatterns = mapping.value();
						if (mappedPatterns.length > 0) {
							for (String mappedPattern : mappedPatterns) {
								if (!hasTypeLevelMapping && !mappedPattern.startsWith("/")) {
									mappedPattern = "/" + mappedPattern;
								}
								addUrlsForPath(urls, mappedPattern);
							}
						}else {
							addConventionMethodPattern(hasTypeLevelMapping,urls, method,".do");
							addConventionMethodPattern(hasTypeLevelMapping,urls, method,"");
						}
					}
				}

				private void addConventionMethodPattern(final boolean hasTypeLevelMapping,final Set<String> urls, Method method,String suffix) {
					Assert.notNull(suffix,"suffix must be not null");
					String mappedPattern = method.getName();
					if (!hasTypeLevelMapping && !mappedPattern.startsWith("/")) {
						mappedPattern = "/" + mappedPattern;
					}
					addUrlsForPath(urls, mappedPattern+suffix);
				}
				
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}
		return StringUtils.toStringArray(urls);
	}
	
	protected boolean supportsTypeLevelMappings() {
		return false;
	}
}
