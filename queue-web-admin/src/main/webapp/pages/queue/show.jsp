<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<duowan:override name="head">
	<title>Queue 信息</title>
</duowan:override>

<duowan:override name="content">
	<form:form modelAttribute="queue"  >
		<input type="button" value="返回列表" onclick="window.location='${ctx}/queue/index.do'"/>
		<input type="button" value="后退" onclick="history.back();"/>
		
		<input type="hidden" id="queueName" name="queueName" value="${queue.queueName}"/>
		<input type="hidden" id="vhostName" name="vhostName" value="${queue.vhostName}"/>
	
		<table class="formTable">
			<tr>	
				<td class="tdLabel">队列名称</td>	
				<td><c:out value='${queue.queueName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">虚拟host</td>	
				<td><c:out value='${queue.vhostName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">备注</td>	
				<td><c:out value='${queue.remarks}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">是否持久队列</td>	
				<td><c:out value='${queue.durable}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">是否自动删除</td>	
				<td><c:out value='${queue.autoDelete}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">自动删除的时过期时长，单位毫秒</td>	
				<td><c:out value='${queue.autoDeleteExpires}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">是否互斥，即该队列只能有一个客户端连接</td>	
				<td><c:out value='${queue.exclusive}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">队列当前大小</td>	
				<td><c:out value='${queue.size}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">队列最大大小</td>	
				<td><c:out value='${queue.maxSize}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">time to live in queue,发送至这个队列的数据多久过期</td>	
				<td><c:out value='${queue.ttl}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">创建时间</td>	
				<td><fmt:formatDate value='${queue.createdTime}' pattern="yyyy-MM-dd"/></td>
			</tr>
			<tr>	
				<td class="tdLabel">创建人</td>	
				<td><c:out value='${queue.operator}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">最后更新时间</td>	
				<td><fmt:formatDate value='${queue.lastUpdatedTime}' pattern="yyyy-MM-dd"/></td>
			</tr>
		</table>
	</form:form>
</duowan:override>

<%-- jsp模板继承,具体使用请查看: http://code.google.com/p/rapid-framework/wiki/rapid_jsp_extends --%>
<%@ include file="base.jsp" %>