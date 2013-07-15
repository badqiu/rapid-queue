<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>


	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>队列名称:
		</td>		
		<td>
		<form:input path="queueName" id="queueName" cssClass="required " maxlength="50" size="30" />
		<font color='red'><form:errors path="queueName"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>虚拟host:
		</td>		
		<td>
		<form:input path="vhostName" id="vhostName" cssClass="required " maxlength="50" size="30" />
		<font color='red'><form:errors path="vhostName"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			备注:
		</td>		
		<td>
		<form:input path="remarks" id="remarks" cssClass="" maxlength="200" size="30" />
		<font color='red'><form:errors path="remarks"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>持久模式
		</td>		
		<td>
		<form:radiobuttons path="durableType" id="durableType" cssClass="required " items="${DurableTypeEnum}" />
		<font color='red'><form:errors path="durableType"/></font>
		</td>
	</tr>	
	
	<!-- 
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>是否自动删除:
		</td>		
		<td>
		<form:radiobutton path="autoDelete" value="true"/>true
		<form:radiobutton path="autoDelete" value="false"/>false
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
		<form:radiobutton path="exclusive" value="true"/>true
		<form:radiobutton path="exclusive" value="false"/>false
		<font color='red'><form:errors path="exclusive"/></font>
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
	 -->
	
	
	<tr>	
		<td class="tdLabel">
			半持久模式,内存元素大小:
		</td>		
		<td>
		<form:input path="memorySize" id="memorySize" cssClass="validate-integer max-value-2147483647" maxlength="10" size="30" />
		<font color='red'><form:errors path="memorySize"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>队列最大大小:
		</td>		
		<td>
		<form:input path="maxSize" id="maxSize" cssClass="required validate-integer max-value-2147483647" maxlength="10" size="30" />
		<font color='red'><form:errors path="maxSize"/></font>
		</td>
	</tr>	
	

	<tr>	
		<td class="tdLabel">
			<span class="required">*</span>是否激活
		</td>		
		<td>
		<form:radiobutton path="enabled" value="true"/>true
		<form:radiobutton path="enabled" value="false"/>false
		<font color='red'><form:errors path="enabled"/></font>
		</td>
	</tr>		

	
		