var POI = {
	HYPER_CHANNEL : "hyperChannel",
	SHOPPING_INFO : "shoppingInfo",
	TV_SHOW_NOTICE : "tvShowNotice",
	POLL : "poll"
};

(function($){
	var defaults = {
  		"type" : POI.HYPER_CHANNEL,
  		"displayText" : "",
  		"buttons" : ["new button"],
  		"duration" : 10,
  		"onSelect" : function(){
  			
  		}
  	};
  	var opt;
  	var selectedIndex = 0;
  	var buttons, type;
  	var tp = '<!--[if lt IE 10]><style type="text/css">.manual-icon.enter-button ,.manual-icon.ok-button {background-image: none!important;} .poi-blue{background-color: #83B6CE;} .poi-red{ background-color: #BC7173;} .poi-yellow{background-color: #D6BD47;}.poi-green{background-color: #A4BA8E;}</style><![endif]--><div id="poi-layer">   <div id="poi-line" class="poi-blue"></div> 	<h1 id="poi-msg"></h1> 	<div id="poi-btn" >     <div id="poi-btn-holder" class="poi-blue-button">      	  <a id="poi-ok" class="manual-icon ok-button"></a> 	<span>or</span>       	  <a  id="poi-enter" class="manual-icon enter-button"></a>  	<span class="poi-button-input-text"></span>     </div>     	</div> </div>';
  	var container = '<!--[if lt IE 10]><style type="text/css">.manual-icon.enter-button ,.manual-icon.ok-button {background-image: none!important;} .poi-blue{background-color: #83B6CE;} .poi-red{ background-color: #BC7173;} .poi-yellow{background-color: #D6BD47;}.poi-green{background-color: #A4BA8E;}</style><![endif]--><div id="poi-layer">  <div id="poi-line" class="poi-blue"></div><h1 id="poi-msg"></h1><div id="poi-btn" ><div id="poi-btn-holder"></div></div></div>';
  	var sbtn = '<a  id="poi-ok" class="manual-icon ok-button"></a>  <span>or</span> <a id="poi-enter" class="manual-icon enter-button"></a>        <span class="poi-button-input-text"></span>';
  	var tp1; 
  	var getClass = function (type){
		var _t;
		switch(type){
			case "hyperChannel":
				return "poi-blue-button";
			break;
			case "tvShowNotice":
				return "poi-green-button";
			break;
			case "shoppingInfo":
				return "poi-red-button";
			break;
			case "poll":
				return "poi-yellow-button";
			break;
		}
	}
	
	var isSingleBtn = function(){
		return $("#poi-enter").length>0;
	} 
	
	var singleToMulti = function(){
		$("#poi-btn-holder").removeAttr("class");
    	var btnText = $(".poi-button-input-text").html();
		$("#poi-btn-holder").html("");
		btn = $("<div></div>").html(btnText);
		$("#poi-btn-holder").append(btn);
		if(selectedIndex >= 0){
			$("#poi-btn-holder div").eq(selectedIndex).addClass("on");
		}
	}
	var multiToSingle = function(){
		var btnText = $("#poi-btn-holder div").eq(0).html();
		$("#poi-btn-holder").html(sbtn);
		$("#poi-btn-holder .poi-button-input-text").html(btnText);
		$("#poi-btn-holder").addClass(getClass(type));
	}
  	var methods = {
  		
  		init : function(options){
  			
  			opt = $.extend(defaults, options);
  			
  			//$(this).html("");
  			$(this).append(container);
  			$(this).css("overflow", "hidden");
  			
  			//display text
  			$("#poi-msg").html(opt.displayText);
  			
  			buttons = opt.buttons
  			type = opt.type;
			var btn;
			if(buttons.length == 1){
				
				$("#poi-btn-holder").addClass(getClass(type));
				$("#poi-btn-holder").append(sbtn);
				$("#poi-btn-holder .poi-button-input-text").html(buttons[0]);
				
			}else if(buttons.length > 1){
				
				$("#poi-btn-holder").removeAttr("class");
				
				for(var i = 0; i<buttons.length; i++){
					btn = $("<div></div>").html(buttons[i]);
					$("#poi-btn-holder").append(btn);
				}
				$("#poi-btn-holder div").addClass(getClass(type));

			}

			var self = this;
			$("#poi-btn-holder div").click(function(){
				console.debug("click");
				$("#poi-btn-holder div").each(function(i,e){
					console.debug(e);
					console.debug(this);
					if(e == this){
						selectedIndex = i;
						console.debug(selectedIndex);
						$(self).poi("select");
					}
				});
			});
			
			var _t = getClass(type).split("-");
			_t.pop();
			_t = _t.join("-");
			
			$("#poi-line").attr("class", _t);
			
			if(opt.duration>0){
				$("#poi-layer").show().animate({
					bottom: 0
				}).delay(opt.duration*1000).animate({
					bottom: -$("#poi-layer").height() - 50
				},300);
			}
  		},
	    displayText : function(txt) {
	    	$("#poi-msg").html(txt);
	    },
	    buttonText : function(txt, i) {
	    	if(isSingleBtn()){
	    		$("#poi-btn-holder .poi-button-input-text").html(txt);
	    	}else{
	    		$("#poi-btn-holder div").eq(i).html(txt);
	    	}
	    },
	    addButton : function(txt){
	    	
	    	if(isSingleBtn()){
	    		singleToMulti();
	    	}
	    	
	    	btn = $("<div></div>").html(txt);
			$("#poi-btn-holder").append(btn);
			$("#poi-btn-holder div").addClass(getClass(type));

			var self = this;
			$(btn).click(function(){
				console.debug("click");
				$("#poi-btn-holder div").each(function(i,e){
					console.debug(e);
					console.debug(this);
					if(e == this){
						selectedIndex = i;
						console.debug(selectedIndex);
						$(self).poi("select");
					}
				});
			});
			
			if(selectedIndex >= 0){
				selectedIndex = $("#poi-btn-holder div").index($("#poi-btn-holder div.on"));
			}
	    },
	    addButtonAt : function(txt, index){
	    	
	    	if(isSingleBtn()){
	    		singleToMulti();
	    	}
	    	
	    	var after = $("#poi-btn-holder div").eq(index);
	    	if(after){
	    		if(index < $("#poi-btn-holder div").length){
	    			after.before("<div>"+txt+"</div>");
	    		}else{
	    			$("#poi-btn-holder").prepend("<div>"+txt+"</div>");
	    		}
	    	}
			$("#poi-btn-holder div").addClass(getClass(type));
			
			if(selectedIndex >= 0){
				selectedIndex = $("#poi-btn-holder div").index($("#poi-btn-holder div.on"));
			}
	    },
	    removeButtonAt : function(index){
	    	
	    	$("#poi-btn-holder div").eq(index).remove();
	    	if($("#poi-btn-holder div.on").length > 0){
				this.selectedIndex = $("#poi-btn-holder div").index($("#poi-btn-holder div.on"));
			}
			if($("#poi-btn-holder div").length == 1){
				multiToSingle();
				if(selectedIndex >= 0){
					selectedIndex = 0;
				}
			}else{
				if(selectedIndex >= 0){
					selectedIndex = $("#poi-btn-holder div").index($("#poi-btn-holder div.on"));
				}
			}
	    },
	    play : function() {
	    	$("#poi-layer").animate({
				bottom: 0
			},300);
			
			if(opt.duration>0){
				$("#poi-layer").delay(opt.duration*1000).animate({
					bottom: -$("#poi-layer").height()
				},300);
			}
	    },
	    selectLeft : function(){
	    	if(isSingleBtn()){
	    		selectedIndex = 0;
	    		return;
	    	}
	    	$("#poi-btn-holder div").removeClass("on");
	    	selectedIndex = Math.max(selectedIndex-1, 0);
	    	$("#poi-btn-holder div").eq(selectedIndex).addClass("on");
	    },
	    selectRight : function(){
	    	if(isSingleBtn()){
	    		selectedIndex = 0;
	    		return;
	    	}
	    	$("#poi-btn-holder div").removeClass("on");
	    	selectedIndex = Math.min(selectedIndex+1, $("#poi-btn-holder div").length-1);
	    	$("#poi-btn-holder div").eq(selectedIndex).addClass("on");
	    },
	    select : function(){
	    	opt.onSelected(selectedIndex);
	    },
	    cancel : function() {
	    	 $("#poi-layer").stop().animate({
				bottom: -$("#poi-layer").height()
			},300);
	    }
	};
	
	
	$.fn.poi = function(method) {
	    // Method calling logic
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.poi');
		}
	}
	
})(jQuery);


//embed css
// var style = document.createElement('style');
// style.type = 'text/css';
// style.innerHTML = ".poi-auto-trim {   white-space: nowrap;   overflow: hidden;   text-overflow: ellipsis;   -o-text-overflow: ellipsis;   -ms-text-overflow: ellipsis;   -moz-binding: url('ellipsis.xml#ellipsis'); } #poi-layer {   width: 100%;   height: 4em;   color: #fff;   position: absolute;   left: 0em;   bottom: 0em;   background: #0f0f10;   /* Old browsers */    background: -moz-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* FF3.6+ */    background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #0f0f10), color-stop(100%, #292d30));   /* Chrome,Safari4+ */    background: -webkit-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* Chrome10+,Safari5.1+ */    background: -o-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* Opera 11.10+ */    background: -ms-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* IE10+ */    background: linear-gradient(to bottom, #0f0f10 0%, #292d30 100%);   /* W3C */    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#0f0f10, endColorstr=#292d30, GradientType=0);   /* IE6-9 */    text-align: left;   overflow: hidden; } #poi-layer #poi-line {   width: 100%;   height: 0.15em;   background: #70b0cb;   /*   border-top: 0.063em solid #4285a2;   border-bottom: 0.063em solid #4285a2;   */   position: absolute;   left: 0em;   top: 0em;     }  #poi-layer #poi-line.poi-blue {   background: -webkit-linear-gradient(left, rgba(65,142,189,0.1), rgba(65,142,189,0.4), rgba(65,142,189,0.7), rgba(65,142,189,0.9), rgba(65,142,189,0.7), rgba(65,142,189,0.4),rgba(65,142,189,0.1));   background: -moz-linear-gradient(left, rgba(65,142,189,0.1), rgba(65,142,189,0.4), rgba(65,142,189,0.7), rgba(65,142,189,0.9), rgba(65,142,189,0.7), rgba(65,142,189,0.4),rgba(65,142,189,0.1));   background: -o-linear-gradient(left, rgba(65,142,189,0.1), rgba(65,142,189,0.4), rgba(65,142,189,0.7), rgba(65,142,189,0.9), rgba(65,142,189,0.7), rgba(65,142,189,0.4),rgba(65,142,189,0.1));   background: -ms-linear-gradient(left, rgba(65,142,189,0.1), rgba(65,142,189,0.4), rgba(65,142,189,0.7), rgba(65,142,189,0.9), rgba(65,142,189,0.7), rgba(65,142,189,0.4),rgba(65,142,189,0.1));   background: linear-gradient(left, rgba(65,142,189,0.1), rgba(65,142,189,0.4), rgba(65,142,189,0.7), rgba(65,142,189,0.9), rgba(65,142,189,0.7), rgba(65,142,189,0.4),rgba(65,142,189,0.1));   -webkit-box-shadow: 0 0 0.125em rgba(124, 209, 250, 0.8),0 0 0.3em rgba(124, 209, 250, 0.4);   box-shadow: 0 0 0.125em rgba(124, 209, 250, 0.2) inset,0 0 0.3em rgba(124, 209, 250, 0.4);    } #poi-layer #poi-line.poi-green {     background: -webkit-linear-gradient(left, rgba(163,207,80,0.1), rgba(163,207,80,0.4), rgba(163,207,80,0.7), rgba(163,207,80,0.9), rgba(163,207,80,0.7), rgba(163,207,80,0.4),rgba(163,207,80,0.1));   background: -moz-linear-gradient(left, rgba(163,207,80,0.1), rgba(163,207,80,0.4), rgba(163,207,80,0.7), rgba(163,207,80,0.9), rgba(163,207,80,0.7), rgba(163,207,80,0.4),rgba(163,207,80,0.1));   background: -o-linear-gradient(left, rgba(163,207,80,0.1), rgba(163,207,80,0.4), rgba(163,207,80,0.7), rgba(163,207,80,0.9), rgba(163,207,80,0.7), rgba(163,207,80,0.4),rgba(163,207,80,0.1));   background: -ms-linear-gradient(left, rgba(163,207,80,0.1), rgba(163,207,80,0.4), rgba(163,207,80,0.7), rgba(163,207,80,0.9), rgba(163,207,80,0.7), rgba(163,207,80,0.4),rgba(163,207,80,0.1));   background: linear-gradient(left, rgba(163,207,80,0.1), rgba(163,207,80,0.4), rgba(163,207,80,0.7), rgba(163,207,80,0.9), rgba(163,207,80,0.7), rgba(163,207,80,0.4),rgba(163,207,80,0.1));   -webkit-box-shadow: 0 0 0.125em rgba(163,207,80, 0.8),0 0 0.3em rgba(163,207,80, 0.4);   box-shadow: 0 0 0.125em rgba(163,207,80, 0.2) inset,0 0 0.3em rgba(163,207,80, 0.4);   }  #poi-layer #poi-line.poi-red {   background: -webkit-linear-gradient(left, rgba(237, 23, 8,0.1), rgba(237, 23, 8,0.4), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.9), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.4),rgba(237, 23, 8,0.1));   background: -moz-linear-gradient(left, rgba(237, 23, 8,0.1), rgba(237, 23, 8,0.4), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.9), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.4),rgba(237, 23, 8,0.1));   background: -o-linear-gradient(left, rgba(237, 23, 8,0.1), rgba(237, 23, 8,0.4), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.9), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.4),rgba(237, 23, 8,0.1));   background: -ms-linear-gradient(left, rgba(237, 23, 8,0.1), rgba(237, 23, 8,0.4), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.9), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.4),rgba(237, 23, 8,0.1));   background: linear-gradient(left, rgba(237, 23, 8,0.1), rgba(237, 23, 8,0.4), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.9), rgba(237, 23, 8,0.7), rgba(237, 23, 8,0.4),rgba(237, 23, 8,0.1));    -webkit-box-shadow: 0 0 0.125em rgba(210, 48, 51, 0.8),0 0 0.3em rgba(210, 48, 51, 0.4);   box-shadow: 0 0 0.125em rgba(210, 48, 51, 0.8),0 0 0.3em rgba(210, 48, 51, 0.4); }  #poi-layer #poi-line.poi-yellow {   background: -webkit-linear-gradient(left, rgba(246,215,68,0.1), rgba(246,215,68,0.4), rgba(246,215,68,0.7), rgba(246,215,68,0.9), rgba(246,215,68,0.7), rgba(246,215,68,0.4),rgba(246,215,68,0.1));   background: -moz-linear-gradient(left, rgba(246,215,68,0.1), rgba(246,215,68,0.4), rgba(246,215,68,0.7), rgba(246,215,68,0.9), rgba(246,215,68,0.7), rgba(246,215,68,0.4),rgba(246,215,68,0.1));   background: -o-linear-gradient(left, rgba(246,215,68,0.1), rgba(246,215,68,0.4), rgba(246,215,68,0.7), rgba(246,215,68,0.9), rgba(246,215,68,0.7), rgba(246,215,68,0.4),rgba(246,215,68,0.1));   background: -ms-linear-gradient(left, rgba(246,215,68,0.1), rgba(246,215,68,0.4), rgba(246,215,68,0.7), rgba(246,215,68,0.9), rgba(246,215,68,0.7), rgba(246,215,68,0.4),rgba(246,215,68,0.1));   background: linear-gradient(left, rgba(246,215,68,0.1), rgba(246,215,68,0.4), rgba(246,215,68,0.7), rgba(246,215,68,0.9), rgba(246,215,68,0.7), rgba(246,215,68,0.4),rgba(246,215,68,0.1));    -webkit-box-shadow: 0 0 0.125em rgba(246,215,68, 0.8),0 0 0.3em rgba(246,215,68, 0.4);   box-shadow: 0 0 0.125em rgba(246,215,68, 0.8),0 0 0.3em rgba(246,215,68, 0.4); }   #poi-layer #poi-line.poi-blue,#poi-layer #poi-line.poi-green,#poi-layer #poi-line.poi-red,#poi-layer #poi-line.poi-yellow{      -webkit-background-size: 200% 100%;   -moz-background-size: 200% 100%;   background-size: 200% 100%;   background-position: center left, top left;   -webkit-animation: poi-backmove 6s infinite;   -moz-animation: poi-backmove 6s infinite;   -ms-animation: poi-backmove 6s infinite;   -o-animation: poi-backmove 6s infinite;   animation: poi-backmove 6s infinite;  }   @keyframes 'poi-backmove' {  0% {     background-position: center left, top left;  }  50% {     background-position: center right, top right;  }  100% {     background-position: center left, top left;  }  }  @-moz-keyframes poi-backmove {  0% {    background-position: center left, top left;  }  50% {    background-position: center right, top right;  }  100% {    background-position: center left, top left;  }  }  @-webkit-keyframes 'poi-backmove' {  0% {    background-position: center left, top left;  }  50% {    background-position: center right, top right;  }  100% {    background-position: center left, top left;  }  }  @-ms-keyframes 'poi-backmove' {  0% {    background-position: center left, top left;  }  50% {    background-position: center right, top right;  }  100% {    background-position: center left, top left;  }  }  @-o-keyframes 'poi-backmove' {  0% {    background-position: center left, top left;  }  50% {    background-position: center right, top right;  }  100% {    background-position: center left, top left;  }  }    #poi-layer h1 {   height: 1.3125em;   line-height: 1;   font-size: 1.25em;   margin: 0em;   margin-right: 16.6875em;   margin-top: 1.125em;   margin-left: 0.5625em;   white-space: nowrap;   overflow: hidden;   text-overflow: ellipsis;   -o-text-overflow: ellipsis;   -ms-text-overflow: ellipsis;   -moz-binding: url('ellipsis.xml#ellipsis'); } #poi-layer #poi-btn {   width: 21.6875em;   height: 4em;   position: absolute;   top: 0em;   right: 0em;   overflow: visible; } /* #poi-layer #poi-btn #poi-btn-holder {   width: 21.6875em;   height: 4em;   padding-top: 1.125em;   padding-bottom: 1.125em;   position: absolute;   top: 0em;   left: 1.8125em;   white-space: nowrap;   overflow: hidden;   text-overflow: ellipsis;   -o-text-overflow: ellipsis;   -ms-text-overflow: ellipsis;   -moz-binding: url('ellipsis.xml#ellipsis');   z-index: 2; }*/  #poi-layer #poi-btn #poi-btn-holder {       padding: 0.1em 0.4em 0.2em;   margin-top: 0.85em;   margin-right: 0.6em;   white-space: nowrap;   overflow: visible;   text-overflow: ellipsis;   -o-text-overflow: ellipsis;   -ms-text-overflow: ellipsis;   -moz-binding: url('ellipsis.xml#ellipsis');    float: right; } #poi-layer #poi-btn #poi-btn-holder span {   float: left;   font-size: 1.0625em;   font-weight: bold;   padding-top: 0.375em;   padding-right: 0.1875em;   display: block;   text-align: left; } #poi-layer #poi-btn #poi-btn-holder #poi-enter {    float: left;   vertical-align: middle;   display: block;   margin-top: 0.375em;   margin-right: 0.1875em;  } #poi-layer #poi-btn #poi-btn-holder #poi-ok {     float: left;   vertical-align: middle;   display: block;   text-align: left;   margin-right: 0.1875em;   margin-top: 0.1875em;  } #poi-layer #poi-btn #bg {   width: 21.6875em;   height: 4em;   position: absolute;   top: 0em;   right: 0em;   z-index: 1; } /* poi-tutorial-layer */ #poi-tutorial-layer {   width: 100%;   height: 3.0625em;   color: #fff;   position: absolute;   left: 0em;   bottom: 0em;   text-align: center;   background: #0f0f10;   /* Old browsers */    background: -moz-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* FF3.6+ */    background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #0f0f10), color-stop(100%, #292d30));   /* Chrome,Safari4+ */    background: -webkit-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* Chrome10+,Safari5.1+ */    background: -o-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* Opera 11.10+ */    background: -ms-linear-gradient(top, #0f0f10 0%, #292d30 100%);   /* IE10+ */    background: linear-gradient(to bottom, #0f0f10 0%, #292d30 100%);   /* W3C */    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#0f0f10, endColorstr=#292d30, GradientType=0);   /* IE6-9 */    overflow: hidden; } #poi-tutorial-layer h1 {   font-size: 1.0625em;   height: 1.6875em;   font-weight: normal;   text-align: center;   display: inline-block; } #poi-tutorial-layer h1 span {   padding-right: 0.4375em;   text-align: center;   display: inline-block; } #poi-tutorial-layer h1 #info {   width: 1.125em;   height: 1.125em;   padding-right: 0.4375em;   padding-top: 0.125em;   padding-bottom: 0.375em;   display: inline-block;   text-align: center;   vertical-align: middle; } #poi-tutorial-layer h1 #left, #poi-tutorial-layer h1 #right {   width: 1.6875em;   height: 1.6875em;   padding-right: 0.4375em;   display: inline-block;   text-align: center;   vertical-align: middle; }   #poi-btn .poi-button-input-text{     white-space: nowrap;   max-width: 12.2em;   overflow: visible;   text-overflow: ellipsis;   -o-text-overflow: ellipsis;   -ms-text-overflow: ellipsis;   -moz-binding: url('ellipsis.xml#ellipsis'); }   /*normal button*/ .poi-round-button,.poi-round-corner-button{   font-size: 0.8em;   color: #333333;   text-decoration: none;   background: -webkit-linear-gradient(top,#ffffff 0%,#f3f3f3 25%,#e0e0e0 75%,#dfdfdf);   background: -moz-linear-gradient(top,#ffffff 0%,#f3f3f3 25%,#e0e0e0 75%,#dfdfdf);   background: -o-linear-gradient(top,#ffffff 0%,#f3f3f3 25%,#e0e0e0 75%,#dfdfdf);   background: -ms-linear-gradient(top,#ffffff 0%,#f3f3f3 25%,#e0e0e0 75%,#dfdfdf);   background: linear-gradient(top,#ffffff 0%,#f3f3f3 25%,#e0e0e0 75%,#dfdfdf);   background: -webkit-gradient(linear, left top, left bottom, from(#ffffff),color-stop(0.25, #f3f3f3), color-stop(0.75, #e0e0e0), to(#dfdfdf));    -webkit-box-shadow: 0 0.125em 0.125em rgba(000,000,000,0.4), inset 0 0p0.25em rgba(255,255,255,0.3);   box-shadow: 0 0.125em 0.125em rgba(000,000,000,0.4), inset 0 0 0.25em rgba(255,255,255,0.3);   text-shadow: 0 -0.0625em 0 rgba(000,000,000,0.2), 0 0.0625em 0 rgba(255,255,255,0.4);  } .poi-round-button{    border-radius: 50em;    padding: 0.4em;  } .poi-round-corner-button{          padding: 0.25em 0.4em;     border-radius: 0.23em;      }    /*glow button*/ .poi-green-button,.poi-blue-button,.poi-yellow-button,.poi-red-button {   display: inline-block;   border-radius: 0.23em;   background-color: #555;   cursor: pointer; }   .poi-green-button {   -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.6);   box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.6);   border: rgba(163,207,80,0.8) 0.0625em solid;  } .poi-green-button:hover,.poi-green-button.on{   -webkit-animation: green-buttonglow 1.5s infinite;   -moz-animation: green-buttonglow  1.5s infinite;   -ms-animation: green-buttonglow  1.5s infinite;   -o-animation: green-buttonglow  1.5s infinite;   animation: green-buttonglow  1.5s infinite;  }    .poi-blue-button {   -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.1em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.6);   box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.6);   border: rgba(124,209,250,0.8) 0.0625em solid; } .poi-blue-button:hover,.poi-blue-button.on{    -webkit-animation: blue-buttonglow 1.5s infinite;   -moz-animation: blue-buttonglow  1.5s infinite;   -ms-animation: blue-buttonglow  1.5s infinite;   -o-animation: blue-buttonglow  1.5s infinite;   animation: blue-buttonglow  1.5s infinite;  }   .poi-red-button{   -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.6) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.6);   box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.6);   border: rgba(237, 23, 8,0.6) 0.0625em solid;  } .poi-red-button:hover,.poi-red-button.on{      -webkit-animation: red-buttonglow 1.5s infinite;   -moz-animation: red-buttonglow  1.5s infinite;   -ms-animation: red-buttonglow  1.5s infinite;   -o-animation: red-buttonglow  1.5s infinite;   animation: red-buttonglow  1.5s infinite;  }  #poi-btn-holder .poi-yellow-button,#poi-btn-holder .poi-red-button,#poi-btn-holder .poi-blue-button,#poi-btn-holder .poi-green-button{    padding: 0.45em 0.7em 0.35em 0.7em;   margin-right: 1em; } #poi-btn-holder .poi-red-button{     -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);     box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  } #poi-btn-holder .poi-green-button{    -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);     box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2); } #poi-btn-holder .poi-blue-button{     -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);     box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  #poi-btn-holder .poi-yellow-button{   box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.15) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.25) inset, 0 0 0.5625em rgba(246, 215, 68, 0.15);   border: rgba(246, 215, 68,0.8) 0.0625em solid;  }  .poi-yellow-button {   -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.6);   box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);   border: rgba(246, 215, 68,0.8) 0.0625em solid;   white-space: nowrap;   font-weight: bold;     text-align: center;   color: #ddd;   cursor: pointer;  } .yellow-button.on,.poi-yellow-button:hover{   color: #fff;   -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.6);   box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);   border: rgba(246, 215, 68,0.8) 0.0625em solid;   -webkit-animation: yellow-buttonglow 1.5s infinite;   -moz-animation: yellow-buttonglow  1.5s infinite;   -ms-animation: yellow-buttonglow  1.5s infinite;   -o-animation: yellow-buttonglow  1.5s infinite;   animation: yellow-buttonglow  1.5s infinite; }  /*red ani*/ @keyframes 'red-buttonglow' {  0% {     -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);     box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  50% {     -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);     box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);  }  100% {     -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);     box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  }  @-moz-keyframes red-buttonglow {  0% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  }  @-webkit-keyframes 'red-buttonglow' {  0% {    -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  50% {    -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);  }  100% {    -webkit-box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  }  @-ms-keyframes 'red-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  }  @-o-keyframes 'red-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(237, 23, 8, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(237, 23, 8, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(237, 23, 8, 0.2);  }  }  /*green ani*/ @keyframes 'green-buttonglow' {  0% {     -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);     box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  50% {     -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);     box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);  }  100% {     -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);     box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  }  @-moz-keyframes green-buttonglow {  0% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  }  @-webkit-keyframes 'green-buttonglow' {  0% {    -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  50% {    -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);  }  100% {    -webkit-box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  }  @-ms-keyframes 'green-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  }  @-o-keyframes 'green-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(196, 233, 160, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(196, 233, 160, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(196, 233, 160, 0.2);  }  }   /*blue ani*/ @keyframes 'blue-buttonglow' {  0% {     -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);     box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  50% {     -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);     box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);  }  100% {     -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);     box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  }  @-moz-keyframes blue-buttonglow {  0% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  }  @-webkit-keyframes 'blue-buttonglow' {  0% {    -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  50% {    -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);  }  100% {    -webkit-box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  }  @-ms-keyframes 'blue-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  }  @-o-keyframes 'blue-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(124, 209, 250, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(124, 209, 250, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(124, 209, 250, 0.2);  }  }  /*yellow ani*/  @keyframes 'yellow-buttonglow' {  0% {     -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);     box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  50% {     -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);     box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);  }  100% {     -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);     box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  }  @-moz-keyframes yellow-buttonglow {  0% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  }  @-webkit-keyframes 'yellow-buttonglow' {  0% {    -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.5) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.5) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  50% {    -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 1) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 1) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);  }  100% {    -webkit-box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.5) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.5) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  }  @-ms-keyframes 'yellow-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  }  @-o-keyframes 'yellow-buttonglow' {  0% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  50% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.9) inset, 0 0 1em rgba(255, 255, 255, 0.3) inset, 0 0 0.2em rgba(255, 255, 255, 0.9) inset, 0 0 0.5625em rgba(246, 215, 68, 0.4);  }  100% {    box-shadow: 0 0.0625em 2em rgba(246, 215, 68, 0.4) inset, 0 0 1em rgba(255, 255, 255, 0.1) inset, 0 0 0.2em rgba(255, 255, 255, 0.1) inset, 0 0 0.5625em rgba(246, 215, 68, 0.2);  }  }";
// document.getElementsByTagName('head')[0].appendChild(style);


