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
			<span class="required">*</span>虚拟host:
		</td>		
		<td>
		<form:input path="vhostName" id="vhostName" cssClass="required " maxlength="50" />
		<font color='red'><form:errors path="vhostName"/></font>
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
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>是否持久队列:
		</td>		
		<td>
		<form:input path="durable" id="durable" cssClass="required validate-integer max-value-2147483647" maxlength="3" />
		<font color='red'><form:errors path="durable"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>是否自动删除:
		</td>		
		<td>
		<form:input path="autoDelete" id="autoDelete" cssClass="required validate-integer max-value-2147483647" maxlength="3" />
		<font color='red'><form:errors path="autoDelete"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			自动删除的时过期时长，单位毫秒:
		</td>		
		<td>
		<form:input path="autoDeleteExpires" id="autoDeleteExpires" cssClass="validate-integer " maxlength="19" />
		<font color='red'><form:errors path="autoDeleteExpires"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>是否互斥，即该队列只能有一个客户端连接:
		</td>		
		<td>
		<form:input path="exclusive" id="exclusive" cssClass="required validate-integer max-value-2147483647" maxlength="3" />
		<font color='red'><form:errors path="exclusive"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>队列当前大小:
		</td>		
		<td>
		<form:input path="size" id="size" cssClass="required validate-integer max-value-2147483647" maxlength="10" />
		<font color='red'><form:errors path="size"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>队列最大大小:
		</td>		
		<td>
		<form:input path="maxSize" id="maxSize" cssClass="required validate-integer max-value-2147483647" maxlength="10" />
		<font color='red'><form:errors path="maxSize"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			time to live in queue,发送至这个队列的数据多久过期:
		</td>		
		<td>
		<form:input path="ttl" id="ttl" cssClass="validate-integer " maxlength="19" />
		<font color='red'><form:errors path="ttl"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>创建时间:
		</td>		
		<td>
		<input value='<fmt:formatDate value="${queue.createdTime}" pattern="yyyy-MM-dd"/>' onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" id="createdTime" name="createdTime"  maxlength="0" class="required " />
		<font color='red'><form:errors path="createdTime"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>创建人:
		</td>		
		<td>
		<form:input path="operator" id="operator" cssClass="required " maxlength="50" />
		<font color='red'><form:errors path="operator"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>最后更新时间:
		</td>		
		<td>
		<input value='<fmt:formatDate value="${queue.lastUpdatedTime}" pattern="yyyy-MM-dd"/>' onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" id="lastUpdatedTime" name="lastUpdatedTime"  maxlength="0" class="required " />
		<font color='red'><form:errors path="lastUpdatedTime"/></font>
		</td>
	</tr>	
	
		