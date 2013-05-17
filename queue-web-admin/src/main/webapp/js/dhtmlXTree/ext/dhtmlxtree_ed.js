//v.3.0 build 110707

/*
Copyright DHTMLX LTD. http://www.dhtmlx.com
You allowed to use this component or parts of it under GPL terms
To use it on other terms or get Professional edition of the component please contact us at sales@dhtmlx.com
*/
dhtmlXTreeObject.prototype.enableItemEditor=function(b){this._eItEd=convertStringToBoolean(b);if(!this._eItEdFlag)this._edn_dblclick=this._edn_click_IE=!0,this._ie_aFunc=this.aFunc,this._ie_dblclickFuncHandler=this.dblclickFuncHandler,this.setOnDblClickHandler(function(a,b){this._edn_dblclick&&this._editItem(a,b);return!0}),this.setOnClickHandler(function(a,b){this._stopEditItem(a,b);this.ed_hist_clcik==a&&this._edn_click_IE&&this._editItem(a,b);this.ed_hist_clcik=a;return!0}),this._eItEdFlag=!0};
dhtmlXTreeObject.prototype.setOnEditHandler=function(b){this.attachEvent("onEdit",b)};dhtmlXTreeObject.prototype.setEditStartAction=function(b,a){this._edn_click_IE=convertStringToBoolean(b);this._edn_dblclick=convertStringToBoolean(a)};
dhtmlXTreeObject.prototype._stopEdit=function(b){if(this._editCell&&(this.dADTempOff=this.dADTempOffEd,this._editCell.id!=b)){var a=!0,a=this.callEvent("onEdit",[2,this._editCell.id,this,this._editCell.span.childNodes[0].value]);if(a===!0)a=this._editCell.span.childNodes[0].value;else if(a===!1)a=this._editCell._oldValue;var c=a!=this._editCell._oldValue;this._editCell.span.innerHTML=a;this._editCell.label=this._editCell.span.innerHTML;var d=this._editCell.i_sel?"selectedTreeRow":"standartTreeRow";
this._editCell.span.className=d;this._editCell.span.parentNode.className="standartTreeRow";this._editCell.span.style.paddingRight=this._editCell.span.style.paddingLeft="5px";this._editCell.span.onclick=this._editCell.span.ondblclick=function(){};var e=this._editCell.id;this.childCalc&&this._fixChildCountLabel(this._editCell);this._editCell=null;this.callEvent("onEdit",[3,e,this,c]);this._enblkbrd&&(this.parentObject.lastChild.focus(),this.parentObject.lastChild.focus())}};
dhtmlXTreeObject.prototype._stopEditItem=function(b){this._stopEdit(b)};dhtmlXTreeObject.prototype.stopEdit=function(){this._editCell&&this._stopEdit(this._editCell.id+"_non")};dhtmlXTreeObject.prototype.editItem=function(b){this._editItem(b,this)};
dhtmlXTreeObject.prototype._editItem=function(b){if(this._eItEd){this._stopEdit();var a=this._globalIdStorageFind(b);if(a){editText=this.callEvent("onEdit",[0,b,this,a.span.innerHTML]);if(editText===!0)editText=a.span.innerText||a.span.textContent;else if(editText===!1)return;this.dADTempOffEd=this.dADTempOff;this.dADTempOff=!1;this._editCell=a;a._oldValue=editText;a.span.innerHTML="<input type='text' class='intreeeditRow' />";a.span.style.paddingRight=a.span.style.paddingLeft="0px";a.span.onclick=
a.span.ondblclick=function(a){(a||event).cancelBubble=!0};a.span.childNodes[0].value=editText;a.span.childNodes[0].onselectstart=function(a){return(a||event).cancelBubble=!0};a.span.childNodes[0].onmousedown=function(a){return(a||event).cancelBubble=!0};a.span.childNodes[0].focus();a.span.childNodes[0].focus();a.span.onclick=function(a){(a||event).cancelBubble=!0;return!1};a.span.className="";a.span.parentNode.className="";var c=this;a.span.childNodes[0].onkeydown=function(a){if(!a)a=window.event;
if(a.keyCode==13)a.cancelBubble=!0,c._stopEdit(window.undefined);else if(a.keyCode==27)c._editCell.span.childNodes[0].value=c._editCell._oldValue,c._stopEdit(window.undefined);(a||event).cancelBubble=!0};this.callEvent("onEdit",[1,b,this])}}};

//v.3.0 build 110707

/*
Copyright DHTMLX LTD. http://www.dhtmlx.com
You allowed to use this component or parts of it under GPL terms
To use it on other terms or get Professional edition of the component please contact us at sales@dhtmlx.com
*/