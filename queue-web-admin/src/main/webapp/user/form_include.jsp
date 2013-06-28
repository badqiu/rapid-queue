<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

	<tr>	
		<td class="tdLabel">
			username:
		</td>		
		<td>
		<form:input path="username" id="username" cssClass="" />
		<font color='red'><form:errors path="username"/></font>
		</td>
	</tr>
	
	<tr>	
		<td class="tdLabel">
			password:
		</td>		
		<td>
		<form:input path="password" id="password" cssClass="" maxlength="30" />
		<font color='red'><form:errors path="password"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			remarks:
		</td>		
		<td>
		<form:input path="remarks" id="remarks" cssClass="" maxlength="200" />
		<font color='red'><form:errors path="remarks"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			email:
		</td>		
		<td>
		<form:input path="email" id="email" cssClass="validate-email " maxlength="30" />
		<font color='red'><form:errors path="email"/></font>
		</td>
	</tr>	
	
	<tr>	
		<td class="tdLabel">
			mobile:
		</td>		
		<td>
		<form:input path="mobile" id="mobile" cssClass="" maxlength="20" />
		<font color='red'><form:errors path="mobile"/></font>
		</td>
	</tr>	
	
		