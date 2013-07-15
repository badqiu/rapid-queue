<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/commons/meta.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>menu</title>

<link href="${ctx}/styles/left.css" type="text/css" rel="stylesheet" />
<script src="${ctx}/js/jquery.js"></script>
<script>
	$(document).ready(function() {
		$('.urbangreymenu li a').click(function() {
			$('.urbangreymenu li a').removeClass('currentClickMenu');
			$(this).addClass('currentClickMenu');
		});
		
		$('.headerbar').click(function() {
			$(this).next().toggle();
		});
	});
</script>


</head>

<body>

<div class="urbangreymenu">
	  <h3 class="headerbar">队列管理</h3>
	  <ul>
		<li><a href="${ctx}/vhost/index.do" target="rightFrame">vhost管理</a></li>
		
		<!-- 
		<li><a href="${ctx}/exchange/index.do" target="rightFrame">exchange管理</a></li>
		<li><a href="${ctx}/queue/index.do" target="rightFrame">queue管理</a></li>
		<li><a href="${ctx}/binding/index.do" target="rightFrame">binding管理</a></li>
		 -->
		 
	  </ul>
	  <h3 class="headerbar">用户管理</h3>
	  <ul>
		<li><a href="${ctx}/user/index.do" target="rightFrame">user管理</a></li>
	  </ul>
</div>

</body>
</html>