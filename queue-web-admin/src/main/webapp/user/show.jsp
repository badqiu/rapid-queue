<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<rapid:override name="head">
	<title>User 信息</title>
</rapid:override>

<rapid:override name="content">
	<form:form modelAttribute="user"  >
		<input type="button" value="返回列表" onclick="window.location='${ctx}/user/index.do'"/>
		<input type="button" value="后退" onclick="history.back();"/>
		
		<input type="hidden" id="username" name="username" value="${user.username}"/>
	
		<table class="formTable">
			<tr>	
				<td class="tdLabel">password</td>	
				<td><c:out value='${user.password}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">remarks</td>	
				<td><c:out value='${user.remarks}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">email</td>	
				<td><c:out value='${user.email}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">mobile</td>	
				<td><c:out value='${user.mobile}'/></td>
			</tr>
		</table>
	</form:form>
</rapid:override>

<%-- jsp模板继承,具体使用请查看: http://code.google.com/p/rapid-framework/wiki/rapid_jsp_extends --%>
<%@ include file="base.jsp" %>