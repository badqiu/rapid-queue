<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<duowan:override name="head">
	<title>Vhost 信息</title>
</duowan:override>

<duowan:override name="content">
	<form:form modelAttribute="vhost"  >
		<input type="button" value="返回列表" onclick="window.location='${ctx}/vhost/index.do'"/>
		<input type="button" value="后退" onclick="history.back();"/>
		
		<input type="hidden" id="vhostName" name="vhostName" value="${vhost.vhostName}"/>
	
		<table class="formTable">
			<tr>	
				<td class="tdLabel">备注</td>	
				<td><c:out value='${vhost.remarks}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">实际部署的主机</td>	
				<td><c:out value='${vhost.host}'/></td>
			</tr>
		</table>
	</form:form>
</duowan:override>

<%-- jsp模板继承,具体使用请查看: http://code.google.com/p/rapid-framework/wiki/rapid_jsp_extends --%>
<%@ include file="base.jsp" %>