//global variables
var mySetTimeout;
var guide, store, streaming, playback, controllable, popups;
var currentComponent;
var comps = [];
var url, vars, user, cookie;
var cookieName = "9x9.tv.4.0";
var categoryList, category;
var s3url = "https://9x9ui.s3.amazonaws.com/tv4.0/";
var lang = "zh";
var sphere = "zh";
var date = new Date();
var portal, categories, categoryInfo;
var preloader;
var newCookie = {
	user : {
		name : "Guest",
		token : false,
		signed : false,
		facebook : {},
		lang : "zh",
		password : "",
		phone : "",
		gender : "1",
		year : ""
	}, 
	settings : {
		"sphere" : "zh",
		"ui-lang" : "zh",
		"resolution" : "default",
		"guideMode" : "1"
	},
	device : {
		token : false
	},
	playback :  { referer : "" }
};
var states, state;
var currentComponent = false, lastComponent = false;
var apiQueue = new ApiQueue();
var testLayer;
var userToken = false;
var channelLineup = [], programInfo;
var referer = "";
var msoList = ["cts","cts40","tzuchi","tzuchi40"];
var hostname = location.hostname;
var mso = getMso();
var _gaq;
var acct = document.location.host.match (/(dev|stage|alpha)/) ? 'UA-31930874-1' : 'UA-21595932-1';
var loader, request;
var guideChannels = false, streamingChannels = false;
var pops = {};
var emailSharing, storeItemPreview, guideShortCut, 
subscribePreview, playbackSubscribePreview, forgotPassword, overlayNotice, terms, privacy;
var popEvents = {}, gaEvents = {};
var defaultThumbUrl = "https://9x9ui.s3.amazonaws.com/tv4.0/img/ch_default.jpg";
var now = new Date();
var timezero = now.getTime();
var loaderEvent;
var channelLineupReady = false;
var promoSet = false;
var navigatables = [];
var gridGroups;
var skipGA = false;
function getMso(){
	
	var arr = hostname.split(".");
	var m = "9x9";
	for(var i = 0; i<arr.length; i++){
		for(var j = 0; j<msoList.length; j++){
			if(arr[i] == msoList[j]){
				m = msoList[j];
				m = m.replace("40","");
				break;
			}
		}
	}
	return m;
}

$.ajax({
	url: "https://9x9ui.s3.amazonaws.com/9x9miniV23j/javascripts/jquery.titlecard.js",
	dataType: "script",
	success: function(){
		var titleCardLayer = $("<div id='titlecard-layer'></div>");
		titleCardLayer.css("width", "100%").css("height", "100%").css("position","absolute").css("z-index", -1);
		$("body").append(titleCardLayer);
	}
});
	
$(document).bind("keydown", onKeyDown);
$(document).bind("keyup", onKeyUp);
$(document).ready(function () {
	
	fixMarkup();
	hideLayers();
	checkPromo(); 
	
	loader = new APIRequestLoader();
	streamingChannels = new StreamingChannels([]);
	
	onResize();
	//guideChannels = new GuideChannels([]);
	if(userToken){
	
		showLoading();
		
		$.ajax({
			url : "/playerAPI/channelLineup?user=" + userToken + "&v=40&programInfo=true" + "&mso=" + mso + rx(),
			success : function(res){
				hideLoading();
				guideChannels = getChannelLineup(res, true);
				channelLineupReady = true;
				$(document).trigger("channelLineupReady");
			}
		});
		init();

		//get group names
		$.ajax({
			url : "/playerAPI/channelLineup?user=" + userToken + "&v=40&setInfo=true" + "&mso=" + mso + rx(),
			success : function(res){
				gridGroups = getGridGroups(res);
				initGroupNames();
			}
		});

		// $.ajax({
		// 	url : "/playerAPI/channelLineup?channel=8751&v=40&programInfo=true" + "&mso=" + mso + rx(),
		// 	success : function(res){
		// 		var data = getChannelLineup(res);
		// 		console.debug(data);
		// 	}
		// });
		
	}else{
		guideChannels = new GuideChannels([]);
		channelLineupReady = true;
		$(loaderEvent).trigger("channelLineupReady");
		init();
	}

	function initGroupNames(){

		var group, j, h;
		for(var i = 0; i<gridGroups.length; i++){
			group = gridGroups[i];
			j = parseInt(group.pos, 10) - 1;
			h = $(".guide-group-list>li").eq(j);
			h.find(".guide-group-title").html(group.name);
		}
	}
	
	function checkPromo(){
		
		var states;
		var _t = function(str){
			try{
				return eval(str);
			}catch(e){
				return ""
			}
		}
		states = $.address.value();
		states = states.substr(1);
		states = states.split("/");
		state = states[0];
		
		promoSet = false;
		if(state == "promotion"){
			promoSet = {
				channel : _t("states[1]"),
				episode : _t("states[2]")
			}
			var q = _t("states[1]");
			if(states[2]){
				q += "/" + states[2];
			}
			$.address.value("streaming/promo/" + q);
			return;
		}else if(states[0] == "streaming" && states[1] == "promo"){
			promoSet = {
				channel : _t("states[2]"),
				episode : _t("states[3]")
			}
		}
	}
	
	
	function init(){
		
		initComps();
		initVars();
		initGA();
		
		$(document).bind("showPopup", showPopup);
		$(document).bind("hidePopup", hidePopup);
		$(window).resize(onResize);
		onResize();
		
		$.address.change(onAddressChange);
	}
	
	function initVars(){
		
		url = $.url(location.href);
		vars = url.param();
		
		if(vars.dev){
			$("body").append("<div id='dev' />");
			$("body").css("position", "relative");
			$("#dev").width(400).height(100).css("background", "white").css("position", "absolute").css("right", "10px").css("top", "10px").css("padding", "10px");
			$("#dev").append("<a id='logout' href='#' >Sign Out</a>");
			$("#dev").append("<p>version 061918</p>");
			$("#dev #logout").click(function(){
				
				removeCookie();
				$.cookie.jason = false;
				$.cookie("user", null, { expires : -1 });
				$.cookie.jason = true;
				
				location.href = "/tv";
				$.get("/playerAPI/signout?user=" + userToken, function(res){
					console.debug(res);
				});
				return false;
			});
		}
	}
	
	function initFirstLoad(){
		
		states = $.address.value();
		states = states.substr(1);
		states = states.split("/");
		state = states[0];
		
		var comp = getComp(state);
		comp = comp.name;
		
		//if first load is playback, load programs directly then initialize playback
		//if not, wait until the api queue completes loading
		if(comp != "Playback"){
			$(apiQueue).bind("completed", function(){
				playback.startLoadPrograms();
			});
		}
		$(playback).bind("programsLoaded", onProgramsLoaded);
	}
	
	function onProgramsLoaded(){
		
		// console.debug("program loaded");
		// console.debug(channelLineup);
		$(playback).unbind("programsLoaded");
		
		for(var i = 0; i<channelLineup.length; i++){
			if(channelLineup[i].nature == "3"){
				//console.debug(channelLineup[i]);
			}
			if(channelLineup[i].id == "10151"){
				//console.debug(channelLineup[i]);
			}
		}
	}
	
	function onAddressChange(){
		
		if(currentComponent){
			currentComponent.exit();
		}
		
		states = $.address.value();
		states = states.substr(1);
		states = states.split("/");
		state = states[0];
		
		var comp = getComp(state);
		
		
		lastComponent = currentComponent;
		currentComponent = comp;
		
		//init will be called only when address changed
		states.shift();
		currentComponent.init(states);
		currentComponent.show();
	}
	
	function getComp(state){
		
		var n;
		for(var i = 0; i<comps.length; i++){
			n = comps[i].name.toLowerCase();
			if(n == state){
				return comps[i];
			}
		}
		return comps[0]; // return streaming portal if not found
	}
	
	function initGA(){
		
		var noBounceInterval;
		initNoBounce();
		
		$(gaEvents).bind("programChanged", function(e, ch, ep, pr, title){
			if(ep == false){
				reportGA("/view/" + ch + "/" + pr, title);
			}else{
				reportGA("/view/" + ch + "/" + ep + "/" + pr, title);
			}
			initNoBounce();
		});
		
		$(gaEvents).bind("episodeChanged", function(e, ch, ep, title){
			reportGA("/view/" + ch + "/" + ep, title);
		});
		
		$(gaEvents).bind("channelChanged", function(e, ch, title){
			reportGA("/view/" + ch, title);
		});

		$(gaEvents).bind("promoChannel", function(e, ch, title){
			reportGA("/promotion/" + ch, title);
		});

		$(gaEvents).bind("promoEpisode", function(e, ch, ep, title){
			reportGA("/promotion/" + ch + "/" + ep, title);
		});

		$(gaEvents).bind("promoProgram", function(e, ch, ep, pg, title){
			reportGA("/promotion/" + ch + "/" + ep + "/" + pg, title);
		});
		
		$(gaEvents).bind("exitPlayback", function(e, ch, title){
			reportGAEof();
		});

		$(gaEvents).bind("eof", function(e, ch, title){
			reportGAEof();
		});
		
		$(gaEvents).bind("paused", function(e, ch, title){
			console.info("paused");
			reportGAEof();
			clearInterval(noBounceInterval);
		});
		
		$(gaEvents).bind("resumed", function(e, ch, ep, pr, title){
			console.info("resumed");
			initNoBounce();
		});

		$(gaEvents).bind("enter", function(){
			console.info("enter");
			_gaq = [];
			_gaq.push (['_trackEvent', 'Enter', 'into fillscreen']);
			_ga();
		});
		
		window.onbeforeunload = function(e){
			_gaq = [];
			_gaq.push(['_setAccount', acct]);
			_gaq.push(['_trackPageview', "eof"]);
			_ga();
			var msg = lang == "zh" ? "您是否確定要離開?" : "Are you sure you to leave now?";
			return "";
			//if(ans) return false;
			/*
			if(!e) e = window.event;
			//e.cancelBubble is supported by IE - this will kill the bubbling process.
			e.cancelBubble = true;
			e.returnValue = 'You sure you want to leave?'; //This is displayed on the dialog

			//e.stopPropagation works in Firefox.
			if (e.stopPropagation) {
				e.stopPropagation();
				e.preventDefault();
			}*/
		}
		
		function initNoBounce(){
			
			var dur = 1000 * 60 * 5;
			//var dur = 1000;
			clearInterval(noBounceInterval);
			noBounceInterval = setInterval(function(){
				_gaq = [];
				_gaq.push (['_trackEvent', 'NoBounce', '5 min ping']);
				_ga();
			}, dur);
		}
		
		function _ga(){
			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
				ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			})();
		}
		
		var eofCount = 0;
		setInterval(postEof, 400)
		function reportGAEof(){
			eofCount++;
			console.info("report eof");
		}
		function postEof(){
			if(eofCount <= 0) return;
			_gaq = [];
			_gaq.push(['_setAccount', acct]);
			_gaq.push(['_trackPageview', "eof"]);
			_ga();
			eofCount--;
		}
		
		var gaQueue = [];
		setInterval(postGA, 800);
		function reportGA(url, title){
			gaQueue.push({
				url : url,
				title : title
			});
		}

		function postGA(){

			if(gaQueue.length<=0) return;

			var item = gaQueue[0];
			var url = item.url;
			var title = item.title;

			_gaq = [];
			_gaq.push(['_setAccount', acct]);
			_gaq.push(['_set', 'title', title]);
			_gaq.push(['_trackPageview', url]);
			
			console.info("report url:" + url);
			console.info("report title:" + title);
			_ga();

			gaQueue.shift(); 
		}
	}
	
	function initComps(){

		streaming = new Streaming();
		store = new Store();
		guide = new Guide();
		playback = new Playback();
		
		comps = [streaming, store, guide, playback];
		
		emailSharing = new EmailSharing();
		storeItemPreview = new StoreItemPreview();
		guideShortCut = new GuideShortCut();
		subscribePreview = new Preview('.subscribe-preview');
		playbackSubscribePreview = new PlaybackSubscribePreview();
		forgotPassword = new ForgotPassword();
		overlayNotice = new OverlayNotice();
		terms = new TermsAndPrivacy('.terms-service');
		privacy = new TermsAndPrivacy('.privacy-policy');
		
		
		playback.hide();
		
		for(var i = 0; i<comps.length; i++){
			$(comps[i]).bind("controllOut", onControllOut);
		}
		
		function onControllOut(e, targetName){
			currentComponent.hide();
			for(var i = 0; i<comps.length; i++){
				if(comps[i].name == targetName){
					currentComponent = comps[i];
				}
			}
			currentComponent.show();
		}
	}
	
	function fixMarkup(){
	
		var error = lang == "zh" ? "<span class='notice-desc' style='display:none'>與主機連線失敗，請稍後再試。</span>" : "<span class='notice-desc' style='display:none'>Could not connect to the server, please try again later.</span>";
		$(".notice-desc-wrap").append(error);

		if(mso != "9x9"){
			$("#logo-layer").find("img").eq(0).hide();
			var brand = mso.replace("40", "");
			$("#logo-layer").find("img").eq(0).attr("src", s3url + "img/" + brand + "-logo.png");
			$("#logo-layer").find("img").eq(0).show();
		}
		
		$("#player-holder").css("overflow", "hidden").css("position", "relative");
		$("#playback-layer").show();
		hideLayers();
		
		$("body").css("background", "black");
		$("#streaming-layer").css("position","absolute").css("left",0).css("top",0);
		
		$("#logo-layer").css("z-index", 501);
		$("#logo-layer a").click(function(){
			$.address.value("streaming");
			return false;
		});
		$("a").click(function(){
			return false;
		});
	}
	
	function hideLayers(){
		
		$("#osd-layer").hide();
		$("#poi-layer").hide();
		$("#guide-layer").hide();
		$("#store-layer").hide();
		$("#streaming-layer").hide();
		
		$("#overlay-layer").hide();
		$("#overlay-layer>div").hide();
		$("#overlay-layer>div>div").hide();
		$("#manual-layer>div").hide();
	}
	
	function onResize(){

		var newWidth = $(window).width() / 16;
		var newHeight = $(window).height() / 16;
		var xtimes = newWidth / 64 * 100;
		var ytimes = newHeight / 36 * 100;
		$("html").css("font-size", ((xtimes >= ytimes) ? ytimes : xtimes) + "%");
		//$(".scroll_container").height($(window).height() - $(".scroll_container").offset().top - $("#manual-layer").height());
	}
});

function initCookie(){
	
	$.cookie.json = true;
	cookie = $.cookie(cookieName);

	if(cookie == undefined){
		$.cookie(cookieName, newCookie, {
			"expires" : 7
		});
		cookie = newCookie;
	}
	if(!cookie.playback){
		cookie.playback = { referer : "" };
	}
	
	if(cookie.settings["ui-lang"] != undefined){
		lang = cookie.settings["ui-lang"];
	}
	if(cookie.settings["sphere"] != undefined){
		sphere = cookie.settings["sphere"];
	}
	
	$.cookie.json = false;
	var u = $.cookie("user");
	$.cookie.json = true;
	
	function toTitleCase(str){  return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});}
	
	if(u){
		userToken = u;
		$.get("/playerAPI/getUserProfile?v=40&user=" + u, function(res){
			var code = res.split("\t")[0];
			if(code == "0"){
				
				data = getUserProfile(res);
				
				cookie.user.name = data.name || cookie.user.name;
				cookie.user.email = data.email || cookie.user.email;
				cookie.user.description = data.description || cookie.user.description;
				cookie.user.image = data.image || cookie.user.image;
				cookie.user.gender = data.gender || cookie.user.gender;
				cookie.user.year = data.year || cookie.user.year;
				cookie.user.sphere = data.sphere || cookie.user.sphere;
				cookie.user["ui-lang"] = data["ui-lang"] || cookie.user["ui-lang"];
				cookie.user.phone = data.phone || cookie.user.phone;
				
				cookie.settings.sphere = data.sphere;
				cookie.settings["ui-lang"] = data["ui-lang"];
				
				updateCookie();
				
				$("#member-layer #sign-in-up-layer").html(toTitleCase(cookie.user.name));
				
			}else{
				//alert("failed!");
			}
		});
		
		$("#member-layer #sign-in-up-layer").html(toTitleCase(cookie.user.name));
		
	}else{
		var guest = lang == "zh" ? "訪客" : "Guest";
		$("#member-layer #sign-in-up-layer").html(guest);
	}
	
	if(lang == ""){
		lang = "zh";
	}
	
	cookie.user.token = userToken;
	updateCookie();

	if(lang == "en"){
		$(document).ready(function(){
			//$("body").css("font-family", "微軟正黑體, Arial");
			//$("body").googleFonts({fontname: "Karla"});
			//$("body").googleFonts({fontname: "Noto Sans, 微軟正黑體, Arial"});
			//$("body").googleFonts({fontname: "Noto Sans"}); 
		});
	}
}

function showLoading(){
	$("#overlay-layer").css("z-index", 499);
	$("#overlay-layer,#overlay-layer .overlay-loading-wrap, #overlay-layer .overlay-bg").fadeIn(200);
}

function hideLoading(){
	$("#overlay-layer,#overlay-layer .overlay-loading-wrap, #overlay-layer .overlay-bg").fadeOut(200);
}

function updateCookie(){
	$.cookie(cookieName, cookie, {"expires" : 7});
	cookie = $.cookie(cookieName);
}

function removeCookie(){
	$.removeCookie(cookieName);
	cookie = newCookie;
}

function rx(){
	return "&rx=" + Math.floor(Math.random()*100000000000);
}

function showPopup(e, popUpSelector){
	
	$("#overlay-layer").show();
	$(".overlay-bg").fadeIn();
	
	$(popUpSelector).fadeIn();
}

function hidePopup(){
	
	$("#overlay-layer").hide();
	$(".overlay-container").hide();
	$(".overlay-container>div").fadeOut();
	$(".overlay-bg").fadeOut();
}

function dialog(name, comp, arg){
	
	if(name == "custom"){
		overlayNotice.setNotice("custom", comp);
		overlayNotice.show(function(){
			currentComponent = arg;
		}, {
			message : comp
		});
	}else{
		overlayNotice.setNotice(name);
		overlayNotice.show(function(){
			currentComponent = comp;
		});
	}
	currentComponent = overlayNotice;
}

function onKeyUp(e){
	
	firstDown = true;
	clearTimeout(longPressTimeout);
	if(skipUp){
		skipUp = false;
		return;
	}
	
	switch(e.keyCode){
		//tab
		case 9:
			return false;
		break;
		case 37 :
			currentComponent.selectLeft();
			return false;
		break;
		case 39 :		    
			currentComponent.selectRight();
			return false;
		break;
		case 38 : 
			currentComponent.selectUp();
			return false;
		break;
		case 40 : 
			currentComponent.selectDown();
			return false;
		break;
		//enter
		case 13 :
		    currentComponent.select();
		break;
		//space
		case 32 :
			if(currentComponent.togglePlay){
				currentComponent.togglePlay();
			} 
		break;
		case 220 :
		case 116 :
		    $.address.value("streaming");
		    return false;
		break;
		
		//esc
		case 27 :
			if($(".overlay-bg").css("display") === "block"){
				return;
			}
			history.back();
			$(".countdown-layer").hide();
			$("body").removeClass("focus-video");
			
		break;
		
		//F1
		case 112 :
			if(currentComponent.f1){
				currentComponent.f1();
			}
		break;
		
		//F6
		case 117 :
			 currentComponent.f6();
		break;
			
		//num pad +
		case 107:
			if(currentComponent == guide){
				guide.setMode(2);
			}
		break;
		//num pad -
		case 109:
			if(currentComponent == guide){
				guide.setMode(1);
			}
		break;
	}
}

function onLongPressed(){
	if(currentComponent.f1){
		currentComponent.f1();
	}
}

var longPressTimeout;
var skipUp = false;
var firstDown = true;
function onKeyDown(e){
	
	if(!firstDown){
		return;
	}
	firstDown = false;
	
	switch(e.keyCode){
		case 13 :
			clearTimeout(longPressTimeout);
			longPressTimeout = setTimeout(function(){
				firstDown = true;
				skipUp = true;
				onLongPressed();
			}, 2000);
		break;
		case 37 :
		case 39 :
		case 38 :
		case 40 :
		case 220 :
		case 116 :
		case 112 : 
		case 117 : 
			return false;
		break;
	}
}

function timeDifference(current, previous) {

    var msPerMinute = 60 * 1000;
    var msPerHour = msPerMinute * 60;
    var msPerDay = msPerHour * 24;
    var msPerMonth = msPerDay * 30;
    var msPerYear = msPerDay * 365;

    var elapsed = current - previous;

    if (elapsed < msPerMinute) {
         return Math.round(elapsed/1000) + ' seconds ago';   
    }

    else if (elapsed < msPerHour) {
         return Math.round(elapsed/msPerMinute) + ' minutes ago';   
    }

    else if (elapsed < msPerDay ) {
         return Math.round(elapsed/msPerHour ) + ' hours ago';   
    }

    else if (elapsed < msPerMonth) {
        return Math.round(elapsed/msPerDay) + ' days ago';   
    }

    else if (elapsed < msPerYear) {
        return Math.round(elapsed/msPerMonth) + ' months ago';   
    }

    else {
        return Math.round(elapsed/msPerYear ) + ' years ago';   
    }
}

function getEmptyGridId(){
	
	var ch, n;
	for(var i = 0; i<81; i++){
		n = ("" + (i+1));
		if(!guideChannels.hasGrid(n)){
			return n;
		}
	}
	return false;
}

function parseGridPos(grid){
	grid = parseInt(grid, 10);
	var row = Math.floor((grid-1)/9);
	var col = (grid-1)%9;
	var pos = [
		["1-1","1-2","1-3","2-1","2-2","2-3","3-1","3-2","3-3"],
		["1-4","1-5","1-6","2-4","2-5","2-6","3-4","3-5","3-6"],
		["1-7","1-8","1-9","2-7","2-8","2-9","3-7","3-8","3-9"],
		["4-1","4-2","4-3","5-1","5-2","5-3","6-1","6-2","6-3"],
		["4-4","4-5","4-6","5-4","5-5","5-6","6-4","6-5","6-6"],
		["4-7","4-8","4-9","5-7","5-8","5-9","6-7","6-8","6-9"],
		["7-1","7-2","7-3","8-1","8-2","8-3","9-1","9-2","9-3"],
		["7-4","7-5","7-6","8-4","8-5","8-6","9-4","9-5","9-6"],
		["7-7","7-8","7-9","8-7","8-8","8-9","9-7","9-8","9-9"]
	]
	var str = pos[row][col];
	return str.split("-");
}


function facebookShareChannel(channel, callback){
	
	var program = channel.current(); 
	var origthumb = program['thumb'];
	var name = program['name'];
	var host = location.host;
	var link = location.protocol + '//' + location.host + "/tv#/playback/" + channel.id;
	link = link.replace(/www\./, '');
	var uri = location.protocol + '//' + location.host + '/tanks';
	var desc = (program["data"]['desc']) ? program["data"]['desc'] : "Browse your favorite videos on 9x9.tv";
	if(desc == ""){
		desc = "Browse your favorite videos on 9x9.tv";
	}
	
	var q = "https://www.facebook.com/dialog/feed?";
	q += "app_id=110847978946712&";
	q += "thumb=" + origthumb + "&";
	q += "name=" + name + "&";
	
	var parms = {
		method: 'feed',
		name: name,
		link: link,
		picture: origthumb,
		description: desc,
		app_id: '110847978946712',
		//app_id : '110847978946712',
		show_error: true,
		redirect_uri: uri
	};
	
	popup_active = true;
	FB.ui(parms, function (response) {
		popup_active = false;
		$("#body").focus();
		callback();
		// log('response: ' + response);
		// log('response.post_id: ' + response.post_id);
		// if (response && response.post_id) {
			// log('published as: ' + response.post_id);
			// notice_ok(thumbing, "Shared to Facebook successfully", "");
		// } else
			// log('unsuccessful response, may not have been published');
	});
}

//yql snippet
// var url = s3url + "view/main." + lang + ".html";
// $.get("http://query.yahooapis.com/v1/public/yql?" +
	// "q=select%20*%20from%20html%20where%20url%3D%22" +
	// encodeURIComponent(url) +
	// "%22&format=xml", function (data) {
	// console.log(data);
// });
