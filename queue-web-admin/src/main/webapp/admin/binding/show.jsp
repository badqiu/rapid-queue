<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<duowan:override name="head">
	<title>Binding 信息</title>
</duowan:override>

<duowan:override name="content">
	<form:form modelAttribute="binding"  >
		<input type="button" value="返回列表" onclick="window.location='${ctx}/binding/index.do'"/>
		<input type="button" value="后退" onclick="history.back();"/>
		
		<input type="hidden" id="queueName" name="queueName" value="${binding.queueName}"/>
		<input type="hidden" id="exchangeName" name="exchangeName" value="${binding.exchangeName}"/>
		<input type="hidden" id="vhostName" name="vhostName" value="${binding.vhostName}"/>
	
		<table class="formTable">
			<tr>	
				<td class="tdLabel">队列名称</td>	
				<td><c:out value='${binding.queueName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">交换机名称</td>	
				<td><c:out value='${binding.exchangeName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">虚拟host</td>	
				<td><c:out value='${binding.vhostName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">交换机的router_key</td>	
				<td><c:out value='${binding.routerKey}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">备注</td>	
				<td><c:out value='${binding.remarks}'/></td>
			</tr>
		</table>
	</form:form>
</duowan:override>

<%-- jsp模板继承,具体使用请查看: http://code.google.com/p/rapid-framework/wiki/rapid_jsp_extends --%>
<%@ include file="base.jsp" %>