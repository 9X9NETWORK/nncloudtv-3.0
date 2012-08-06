// JavaScript Document

$(function() {

  Elastic();

  function Elastic() {
	
    newWidth = $(window).width() / 16 ;
	oriWidth = 64;
	xtimes = newWidth / oriWidth * 100;
	
	newHeight = $(window).height() / 16;
	oriHeight = 36;
	ytimes = newHeight / oriHeight *100;
	
    if (xtimes >= ytimes) {
	  times = ytimes+"%";
	} else {
	  times = xtimes+"%";
	}
	
	$("body").css("font-size",times);
	
	AlignMiddle();
	FullBg();
	
  }
  
  function AlignMiddle() {
    wh = $(window).height();	
	mh = $("#maintenance-holder").height();
	mt = (wh-mh)/2;
	$("#maintenance-holder").css("margin-top",mt);
  }
  
  function FullBg() {
	ww = $(window).width();
	wh = $(window).height();
	if ($.browser.msie) {
      $("#maintenance-layer").css({"width":ww,"height":wh});
	}
  }
  
  $(window).resize(function(){
	Elastic();
  });
  

});
