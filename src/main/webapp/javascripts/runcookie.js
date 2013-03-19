// JavaScript Document
$(document).ready(function() {
  
  p = $.cookie("page");
  if (p == "create-channel") {
    $(".titlebarL").html('<li><span class="tabNum">1</span><span class="tabName">New channel - Taipei Channel</span></li><li class="on"><span class="tabNum">2</span><span class="tabName">Create new show</span></li><li class="title-note"><span class="star">*</span> Fields are mandatory.</li><li class="right"><a href="javascript:;" id="btn-moreshow"></a></li>');
	$.cookie("page", "");
  } else {
	$(".titlebar").html('Create new show<span class="title-note"><span class="star">*</span> Fields are mandatory.</span>');
	$.cookie("page", "");
  }
  
});