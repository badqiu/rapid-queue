

function generateLeftRightAD(leftDiv,rightDiv) {
	//左边
	leftDivContent	="<div id=LeftAda style='left:5px;POSITION:absolute;TOP:250px;'>";
	leftDivContent	+=leftDiv;
	leftDivContent	+="<a style='text-align:left; cursor: hand; display:block; background:#eee; width:100px;' onclick='javascript:closedivLeftRight()' hidefocus='true'>关闭</a>";
	leftDivContent	+="</div>";

	//右边
	rightDivContent = "<div id=RightAda style='right:5px;POSITION:absolute;TOP:250px;'>";
	rightDivContent	+= rightDiv;
	rightDivContent +="<a style='text-align:right; cursor: hand; display:block; background:#e0e0e0; width:100px;' onclick='javascript:closedivLeftRight()' hidefocus='true'>关闭</a>";
	rightDivContent	+="</div>";

	document.write(leftDivContent);
	document.write(rightDivContent);
	window.setInterval("heartBeatt()",1);
}

lastScrollY=0;
function getScrollNewPosition() {
	var diffYy;
	if(document.documentElement && document.documentElement.scrollTop) {
		diffYy = document.documentElement.scrollTop;
	}
	else if (document.body) {
		diffYy = document.body.scrollTop;
	}
	else {
		/*Netscape stuff*/
	}
	percent=.1*(diffYy-lastScrollY);
	if(percent>0) {
		percent=Math.ceil(percent);
	}
	else {
		percent=Math.floor(percent);
	}
	lastScrollY=lastScrollY+percent;
	return percent;
}

function heartBeatt() {
	var percent = getScrollNewPosition();
	document.getElementById("LeftAda").style.top = parseInt(document.getElementById("LeftAda").style.top)+percent+"px";
	document.getElementById("RightAda").style.top = parseInt(document.getElementById("LeftAda").style.top)+percent+"px";
}

function closedivLeftRight() {
	LeftAda.style.visibility="hidden";
	RightAda.style.visibility="hidden";
}



function generateKefuDialog(kefuDialog) {
	var dialogWidth = 400;
	var dialogHeight = 50;
	var left = (document.documentElement.clientWidth - dialogWidth) / 2;
	var top = (document.body.scrollTop  + 400 + dialogHeight) ;
	//alert("document.documentElement.clientHeight:"+document.documentElement.clientHeight);
	//左边
	dialogContent	="<div id=_kefuDialogDiv style='POSITION:absolute;left:"+left+"px;TOP:"+top+"px;'>";
	dialogContent	+=kefuDialog;
	dialogContent	+="</div>";

	//document.write(dialogContent);
	window.setInterval(function() {
		var percent = getScrollNewPosition();
		document.getElementById("_kefuDialogDiv").style.top = parseInt(document.getElementById("_kefuDialogDiv").style.top)+percent+"px";
	},1);
	return dialogContent;
}



