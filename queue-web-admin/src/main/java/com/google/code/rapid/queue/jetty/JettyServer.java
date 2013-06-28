package com.google.code.rapid.queue.jetty;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.util.Assert;
/**
 * 开发调试使用的 Jetty Server
 * @author badqiu
 *
 */
public class JettyServer {
	
	public static void main(String[] args) throws Exception {
		String webAppDir = System.getProperty("webappDir");
		
		Server server = buildNormalServer(webAppDir,8080, "/");
		server.start();
	}
	
	/**
	 * 创建用于正常运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 */
	public static Server buildNormalServer(String webappDir,int port, String contextPath) {
		Assert.hasText(webappDir,"webappDir must be not empty");
		
		Server server = new Server(port);
		WebAppContext webContext = new WebAppContext(webappDir, contextPath);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(webContext);
		server.setStopAtShutdown(true);
		return server;
	}


}
