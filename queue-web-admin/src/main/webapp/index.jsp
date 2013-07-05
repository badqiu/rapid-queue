<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Rapid-Queue管理后台</title>

<style type="text/css">
body {
    font-size:12px;
}
.content {
	padding-left: 10px;
}
</style>
<link href="${ctx}/styles/global.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/styles/layout.css" rel="stylesheet" type="text/css"/>

<script src="${ctx}/js/jquery.js"></script>
<script>

/** 自动调整div高度 */
function autoResizeDivFunc() {
	var autoDivHeight = (document.documentElement.clientHeight || document.body.clientHeight) - $('#main').position().top;
	if($.browser.msie) {
		$('#main iframe').height( autoDivHeight);
	}else {
		$('#main').height( autoDivHeight);
	}
};

$(document).ready(function() {
	autoResizeDivFunc();
	$(window).bind('resize',autoResizeDivFunc);
});

</script>
</head>
<body scroll="no">
 <div class="content">

  <div id="header">
   <div id="logo"></div>
   <span id="main_title">
		FPCMS管理后台
   </span>
   <div id="personal">当前用户：${ADMIN_LOGIN_USER} | 角色：管理员 | 系统启动时间：<fmt:formatDate value="${systemStartupTime}" pattern="yyyy-MM-dd HH:mm" /> | uptime：<fmt:formatNumber value="${uptime}" pattern="##.#"/>天 |<a href="${ctx}/admin/logout.do">退出</a></div>
  </div>

  <div id="nav">
   <ul>
    <li><a href="left.jsp" target="leftFrame">系统管理</a></li>
    <li><a href="left.jsp" target="leftFrame">系统管理</a></li>
   </ul>
  </div>

  <div id="main">
    <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
      <tr height="100%">
        <td align="left" id="left">
          <iframe name="leftFrame" width="100%" height="100%" src="left.jsp" frameborder="0" scrolling="no"></iframe>
        </td>
        <td align="left" id="right">
          <iframe name="rightFrame" width="100%" height="100%" src="http://code.google.com/p/rapid-framework/wiki/menu" frameborder="0" scrolling="auto"></iframe>
        </td>
      </tr>
    </table>
  </div>
 </div>

</body>
</html>
