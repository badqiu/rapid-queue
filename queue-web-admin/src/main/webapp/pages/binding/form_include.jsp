<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>


	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>队列名称:
		</td>		
		<td>
		<form:input path="queueName" id="queueName" cssClass="required " maxlength="50" />
		<font color='red'><form:errors path="queueName"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>交换机名称:
		</td>		
		<td>
		<form:input path="exchangeName" id="exchangeName" cssClass="required " maxlength="50" />
		<font color='red'><form:errors path="exchangeName"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>虚拟host:
		</td>		
		<td>
		<form:input path="vhostName" id="vhostName" cssClass="required " maxlength="50" />
		<font color='red'><form:errors path="vhostName"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			交换机的router_key:
		</td>		
		<td>
		<form:input path="routerKey" id="routerKey" cssClass="" maxlength="3000" />
		<font color='red'><form:errors path="routerKey"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			备注:
		</td>		
		<td>
		<form:input path="remarks" id="remarks" cssClass="" maxlength="200" />
		<font color='red'><form:errors path="remarks"/></font>
		</td>
	</tr>	
	
		