<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>

<head>
	<%@ include file="/commons/meta.jsp" %>
	<base href="<%=basePath%>">
	<title>登陆</title>
<style type="text/css">            

</style> 	
</head>

<body>
<%@ include file="/commons/messages.jsp" %>

<div style="margin: 0 auto; width:960px;">
<h1>RapidQueue登陆页面</h1>
<form action="${ctx}/user/login.do" method="post" >
	<table align="center">
		<tr>	
			<th>用户名&nbsp;</th>	
			<td>
			<input name="username" class="required"/>
			</td>
		</tr>
		
		<tr>	
			<th>密码&nbsp;</th>	
			<td>
			<input type="password" name="password" class="required"/>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
				<input type="submit" value="登陆" />
			</td>
		</tr>		
	</table>
</form>
<br />
<font color="red"><b>不登录</b></font>,查看:<a href="/vhost/index.do"><b>rapid-queue只读状态</b></a>
</div>

<script>
	new Validation(document.forms[0],{onSubmit:true});
</script>

</body>
</html>
