<%@page import="com.google.code.rapid.queue.metastore.model.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<duowan:override name="head">
	<title>Exchange 信息</title>
</duowan:override>

<duowan:override name="content">
	<form:form modelAttribute="exchange"  >
		<input type="button" value="返回列表" onclick="window.location='${ctx}/exchange/index.do'"/>
		<input type="button" value="后退" onclick="history.back();"/>
		
		<input type="hidden" id="exchangeName" name="exchangeName" value="${exchange.exchangeName}"/>
		<input type="hidden" id="vhostName" name="vhostName" value="${exchange.vhostName}"/>
	
		<table class="formTable">
			<tr>	
				<td class="tdLabel">交换机名称</td>	
				<td><c:out value='${exchange.exchangeName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">虚拟host</td>	
				<td><c:out value='${exchange.vhostName}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">备注</td>	
				<td><c:out value='${exchange.remarks}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">持久模式:memory,durable,haft_durable</td>	
				<td><c:out value='${exchange.durableType}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">是否自动删除</td>	
				<td><c:out value='${exchange.autoDelete}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">自动删除的时过期时长，单位毫秒</td>	
				<td><c:out value='${exchange.autoDeleteExpires}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">类型: topic,fanout,direct</td>	
				<td><c:out value='${exchange.type}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">当前交换机大小</td>	
				<td><c:out value='${exchange.size}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">当使用半持久模式,放在内存中的元素大小</td>	
				<td><c:out value='${exchange.memorySize}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">交换机的大小</td>	
				<td><c:out value='${exchange.maxSize}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">创建时间</td>	
				<td><fmt:formatDate value='${exchange.createdTime}' pattern="yyyy-MM-dd"/></td>
			</tr>
			<tr>	
				<td class="tdLabel">操作人员</td>	
				<td><c:out value='${exchange.operator}'/></td>
			</tr>
			<tr>	
				<td class="tdLabel">最后更新时间</td>	
				<td><fmt:formatDate value='${exchange.lastUpdatedTime}' pattern="yyyy-MM-dd"/></td>
			</tr>
		</table>
	</form:form>
</duowan:override>

<%-- jsp模板继承,具体使用请查看: http://code.google.com/p/rapid-framework/wiki/rapid_jsp_extends --%>
<%@ include file="base.jsp" %>