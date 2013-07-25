<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>
	
	<tr>	
		<td class="tdLabel">
			username:
		</td>		
		<td>
		<form:input path="username" id="username" cssClass="" size="30"/>
		<font color='red'><form:errors path="username"/></font>
		</td>
	</tr>
	
	<tr>	
		<td class="tdLabel">
			password:
		</td>		
		<td>
		<form:input path="password" id="password" cssClass="" maxlength="30" size="30" />
		<font color='red'><form:errors path="password"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			remarks:
		</td>		
		<td>
		<form:input path="remarks" id="remarks" cssClass="" maxlength="200" size="30"/>
		<font color='red'><form:errors path="remarks"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			email:
		</td>		
		<td>
		<form:input path="email" id="email" cssClass="validate-email " maxlength="30" size="30"/>
		<font color='red'><form:errors path="email"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			mobile:
		</td>		
		<td>
		<form:input path="mobile" id="mobile" cssClass="" maxlength="20" size="30"/>
		<font color='red'><form:errors path="mobile"/></font>
		</td>
	</tr>	

	<tr>	
		<td class="tdLabel">
			允许web管理后台登录:
		</td>		
		<td>
		<form:radiobutton path="allowWebadminLogin" value="true"/>true
		<form:radiobutton path="allowWebadminLogin" value="false"/>false
		<font color='red'><form:errors path="allowWebadminLogin"/></font>
		</td>
	</tr>
	
	<tr>	
		<td class="tdLabel">
			queue权限列表:
		</td>		
		<td>
		*号代表所有权限,其它情况不能使用通配符,通过换行符分隔权限<br />
		<form:textarea path="queuePermissionList" id="queuePermissionList" cssClass="" rows="20" cols="40" />
		<font color='red'><form:errors path="queuePermissionList"/></font>
		</td>
	</tr>
	
	<tr>	
		<td class="tdLabel">
			exchange权限列表:
		</td>		
		<td>
		*号代表所有权限,其它情况不能使用通配符,通过换行符分隔权限<br />
		<form:textarea path="exchangePermissionList" id="exchangePermissionList" cssClass=""  rows="20" cols="40"/>
		<font color='red'><form:errors path="exchangePermissionList"/></font>
		</td>
	</tr>
				
		