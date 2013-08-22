
Streaming.prototype = new Controllable("Streaming");
Streaming.prototype.constructor = Streaming;
function Streaming(portal) {
	
	var comps, portal, settings, focused, sign;
	var self = this;
	var layer = "#streaming-layer";
	var state, states;
	function onConstructed(){
		
		portal = new StreamingPortal();
		settings = new Settings();
		sign = new Sign();
		
		comps = [portal, settings, sign];
		for(var i = 0; i<comps.length; i++){
			
			$(comps[i]).bind("controllOut", function(e, targetName){
				for(var i = 0 ; i<comps.length; i++){
					if(comps[i].name == targetName){
						focused = comps[i];
						if(focused == sign){
							sign.init();
						}
					}
				}
			});
			
			$(comps[i]).bind("controllIn", function(){
				for(var i = 0 ; i<comps.length; i++){
					if(comps[i].name == e.currentTarget.name){
						focused = comps[i];
					}
				}
			});
			
			$(comps[i]).bind("streamingEnd", function(e, targetName){
				$(self).trigger("controllOut", [targetName]);
			});
		}
		
		if(userToken){
			//$("#display-list li").eq(2).find(".inner-info-layer").remove();
			$("#display-list li").eq(2).find(".inner-info-layer span").hide();
			$("#display-list li").eq(2).find(".inner-info-layer span").eq(1).show();
		}
	}
	
	this.dataLoaded = false;
	this.displayItem = Array();
	this.currentIndex = 0;
	this.selectLeft = function(){
		focused.selectLeft();
	}
	this.selectRight = function(){
		focused.selectRight();
	}
	this.selectUp = function(){
		focused.selectUp();
	}
	this.selectDown = function(){
		focused.selectDown();
	}
	this.select = function(){
		focused.select();
	}
	this.togglePlay = function(){
		if(focused == portal){
			portal.togglePlay();
		}
	}
	this.f1 = function(){
		
		if(!userToken) return;
		
		var watchingChannel = portal.getWatchingChannel();
		if(watchingChannel == false){
			return;
		}
		var isFollowed = guideChannels.byId(watchingChannel.id) !== false;
		
		
		currentComponent = subscribePreview;
		
		subscribePreview.show(function(){
			currentComponent = streaming;
		},{
			isFollow : !isFollowed,
			channel : watchingChannel
		});
		
		//Email Sharing Button
		$(popEvents).unbind("emailSharing");
		$(popEvents).bind("emailSharing", function(){
			console.debug("email");
			subscribePreview.hide();
			emailSharing.show(function(){
				currentComponent = streaming;
			},{
				userToken : userToken
			});
			currentComponent = emailSharing;
		});
		
		//On Email Sharing Sumbmit
		$(popEvents).unbind("emailSubmit");
		$(popEvents).bind("emailSubmit", function(e, obj){
			$(popEvents).unbind("emailSubmit");
			var buildQuery = function(obj){
				var str = "";
				for(var i in obj){
					str += i + "=" + obj[i] + "&"
				}
				str.substr(str.length-2, 1);
				return str;
			}
			var q = buildQuery(obj);
			$.ajax({
				url : "/playerAPI/shareByEmail?" + q,
				success : function(res){
					var code = res.split("\t")[0];
					if(code == 0){
						//success
						var msg = lang == "zh" ? "信件已成功寄出" : "E-mail sent successfully";
						dialog("custom", msg, streaming);
					}else if(code == 110){
						var msg = lang == "zh" ? "驗證碼錯誤" : "Captcha does not match";
						dialog("custom", msg, streaming);
					}else{
						dialog("error", streaming);
					}
				},
				error : function(res){
					console.warn(res);
					dialog("error", streaming);
				}
			});
			//currentComponent = subscribePreview;
			currentComponent = streaming;
		});
		
		//FB Sharing Button
		$(popEvents).unbind("fbSharing");
		$(popEvents).bind("fbSharing", function(){
			currentComponent = streaming;
			facebookShareChannel(watchingChannel, function(){
				currentComponent = streaming;
			});
		})
		
		if(!isFollowed){
			
			$(popEvents).unbind("follow");
			$(popEvents).bind("follow", function(){
				//do follow
				var grid = getEmptyGridId();
				$.get("/playerAPI/subscribe?user=" + userToken + "&channel=" + watchingChannel.id + "&grid=" + grid + "&mso=" + mso, function(res){
					var code = res.split("\t")[0];
					if(code == "0"){
						//訂閱成功
						overlayNotice.setNotice('followed');
						var pos = parseGridPos(grid);
						$(".notice-desc .guide-position").html(pos.join("-"));
						overlayNotice.show(function(){
							currentComponent = streaming;
						});
						currentComponent = overlayNotice;
					}else{
						//訂閱失敗
						overlayNotice.setNotice('error');
						overlayNotice.show(function(){
							currentComponent = streaming;
						});
						currentComponent = overlayNotice;
					}
				});
			});
		}else{
			
			$(popEvents).unbind("unfollow");
			$(popEvents).bind("unfollow", function(){
				
				overlayNotice.setNotice('confirmUnfollow');
				overlayNotice.show(function(){
					currentComponent = streaming;
				});
				currentComponent = overlayNotice;
				
				//do unfollow
				$(popEvents).unbind("confirmUnfollow");
				$(popEvents).bind("confirmUnfollow", function(e, ans){
					if(ans){
						$.get("/playerAPI/unsubscribe?user=" + userToken + "&channel=" + watchingChannel.id + "&grid=" + watchingChannel.grid + "&mso=" + mso, function(res){
							var code = res.split("\t")[0];
							if(code == "0"){
								//取消訂閱成功
								overlayNotice.setNotice('unfollowed');
								overlayNotice.show(function(){
									currentComponent = streaming;
								});
								currentComponent = overlayNotice;
								
							}else{
								//取消訂閱失敗
								overlayNotice.setNotice('error');
								overlayNotice.show(function(){
									currentComponent = streaming;
								});
								currentComponent = overlayNotice;
							}
						});
					}else{
						currentComponent = streaming;
						overlayNotice.exit();
					}
				});
			});
		}
	}
	this.init = function(states){

		states = states;

		if(states.length>0){
			if(states[0] == "history"){
				$.address.value("streaming");
				return;
			}
		}

		//this is to decide how many seconds to jump to when swtich to playback
		playbackStartTime = false;


		portal.init(states);
		$(layer).css("left", "0");
	}
	this.show = function(){
		
		$("#manual-layer").show();
		$("#manual-layer>div").show();
		
		$("#manual-layer>div").eq(1).hide();
		$("#manual-layer>div").eq(4).hide();
		$("#manual-layer>div").eq(6).hide();
		
		focused = portal;
		
		cookie.playback.referer = "streaming";
		referer = "streaming";
		updateCookie();
	}
	this.hide = function(){
		//$(layer).hide();
		$(layer).css("left", "10000px");
	}
	this.exit = function(){

		if(!focused) return;
		
		states = $.address.value();
		states = states.substr(1);
		states = states.split("/");
		state = states[0];
		
		if(focused != portal){
			focused.exit();
		}
		
		if(state == "streaming"){
			return;
		}
		this.hide();
		
		for(var i = 0; i<streamingPlayers.length; i++){
			streamingPlayers[i].pause();
		}
		//portal.exit();
	}
	onConstructed();
}

StreamingPortal.prototype = new Controllable("StreamingPortal");
StreamingPortal.prototype.constructor = StreamingPortal;
var personalHistory;
function StreamingPortal(){
	
	var layer = "#streaming-layer";
	var list = layer + " #display-list>li";
	var container = layer + " #display-list";
	var countDown = layer + " .countdown-layer";
	var li, len = 0;
	var self = this;
	var size = {
		w1 : 13.95,
		w2 : 39.804
	}
	var currentChannlIndex = 0;
	var current;
	var startX = (72 - size.w2)/2;
	var popup = false;
	var sets, channels, episodes;
	var items = [], item;
	var isReady = false;
	var itemCreated = false;
	var towardRight = true;
	var initSetId = false;
	var constructing = false;
	var relocated;
	//this index indicates the position of "on" li in the physical html list
	this.currentIndex = 5;
	this.dataLoaded = false;
	function onConstructed(){
		
		constructing = true;
		$(layer).hide();
		
		len = $(list).length;
		li = $(list).eq(len-2).clone();
		$(list).eq(len-1).remove();
		$(list).eq(len-2).remove();
		
		$(container).css("width", "1000rem").css("height", "100px");
		$(countDown).hide();
		
		var item = $(layer + " #display-list li").eq(0);
		item.attr("id", "set.cms");
		item = $(layer + " #display-list li").eq(1);
		item.attr("id", "set.settings");
		item = $(layer + " #display-list li").eq(2);
		item.attr("id", "set.guide");
		item = $(layer + " #display-list li").eq(3);
		item.attr("id", "set.store");
		item = $(layer + " #display-list li").eq(4);
		item.attr("id", "set.my");
		
		var zh = "我的訂閱";
		var en = "My Following";
		
		item.find(".set-title").html(eval(lang));
		
		var tmp;
		if(promoSet != false){
			tmp = li.clone();
			$(tmp).insertAfter("li[id='set.my']");
			item = $(layer + " #display-list li").eq(5);
			item.attr("id", "set.promo").addClass("flipable");
			item.find(".set-title").html("");
		}

		$(".terms-service .overlay-button-wrap a:first-child").removeClass("on");
		$(".privacy-policy .overlay-button-wrap a:first-child").removeClass("on");
	}
	
	function createHistoryItem(){
		
		var tp = li.clone();
		var title = lang == "zh" ? "瀏覽紀錄" : "Personal History";
		var data = personalHistory;
		var ch = data[0];

		tp.find(".set-title").html(title);
		tp.find(".ch-thumb img").attr("src", ch.images[0]);
		tp.find(".ch-title").html("");
		tp.find(".ch-which").html("");
		tp.find(".ep-title").html("");
		tp.find(".ep-update").html("");
		tp.find(".ch-info-layer").hide();
		tp.removeClass("middle-stream").removeClass("right-stream").removeClass("left-stream").addClass("flipable").attr("id", "set.history");

		$(list).parent().append(tp);
		
		$("li[id='set.history']").each(function(i,e){
			$(e).find(".inner-info-layer").remove();
			item = new Flipable();
			items.push(item);
			item.init($(e));
		});
	}
	
	function createItems(){
		
		//create sets got from portal
		sets = portal.sets;
		for(var i = 0; i<sets.length; i++){
				
			set = sets[i];
			
			tmp = li.clone();
			tmp.find(".set-title").html(set.name);
			
			var src = set.images[0] == "" ? defaultThumbUrl : set.images[0];
			
			tmp.find(".ch-thumb img").attr("src", src);
			
			tmp.find(".ch-title").html("");
			tmp.find(".ch-which").html("");
			tmp.find(".ep-title").html("");
			tmp.find(".ep-update").html("");
			tmp.find(".ch-info-layer").hide();
			tmp.removeClass("middle-stream").removeClass("right-stream").removeClass("left-stream").addClass("flipable").attr("id", "set." + set.id);
			$(list).parent().append(tmp);
		}
		
		$(list).each(function(i,e){
			
			item = new Flipable();
			if(i == 4){
				
				var fn = function(){
					
					$(e).attr("id", "set.my");
					
					var zh = "我的訂閱";
					var en = "My Following";
					$(e).removeClass("left-stream").addClass("flipable").find(".set-title").html(eval(lang)+"("+guideChannels.length()+")");
					
					$(e).find(".inner-info-layer>span").hide();
					$(e).find(".no-video-title").hide();
					//$(e).find(".inner-info-layer").hide();
					$(e).find(".sign-in-text").hide();
					$(e).find(".ch-info-layer").append('<figure class="ch-thumb"> <div class="ch-thumb-inner-center"> <img alt="" src="#"> </div> </figure> <div class="info-wrap"> <h1 class="ch-info"> <span>頻道:<span class="ch-title"></span></span> <span> <span class="ch-which"></span> </h1> <h2 class="ep-info"> <span class="ep-title"></span> <span class="ep-update"></span> </h2> </div>');
					
					if(userToken){
						if(guideChannels.length() == 0){
							$(e).find(".sign-in-text").show();
							$(e).find(".with-more-video").remove();
						}
					}else{
						$(e).find(".sign-in-text, .no-video-title").show();
						$(e).find(".info-wrap, .ch-thumb").hide();
						$(e).find(".with-more-video").remove();
						
						var arr = $.address.value().split("/");
						if(arr[arr.length-1] == "my"){
							//$(e).find(".inner-info-layer").show();
						}
					}
				}
				
				if(channelLineupReady){
					fn();
				}else{
					$(document).bind("channelLineupReady", fn);
				}
				
			}
			else if(i>4){
				$(e).find(".inner-info-layer").remove();
			}else if(i<4){
				$(e).addClass("flipable");
			}
			
			items.push(item);
			$(item).bind("dataLoaded", function(e){
				$(this).unbind("dataLoaded");
				var i = -1;
				for(var j = 0; j<items.length; j++){
					if(items[j] == e.currentTarget){
						i = j;
						_fn(i);
					}
				}
			});
			item.init($(e));
		});
		
		self.items = items;
		isReady = true;
		itemCreated = true;
		
		for(var i = 0; i<items.length; i++){
			if(items[i].setId == initSetId){
				self.currentIndex = i;
			}
		}
		if(self.currentIndex == 0 || self.currentIndex >= $(list).length-2){
			self.currentIndex = 4;
		}
		
		$(self).trigger("itemCreated");
		$(items[self.currentIndex]).bind("itemReady", function(){
			$(this).unbind("itemReady");
			this.on();
		});
		
		$(container).find("li").eq(self.currentIndex).addClass("middle-stream");
		$(container).find("li").eq(self.currentIndex-1).addClass("left-stream");
		$(container).find("li").eq(self.currentIndex+1).addClass("right-stream");
		
		var display = $(".middle-stream").find(".with-more-video").css("display");
		if(display == "block"){
			$(".direction-button-wrap").show();
		}else{
			$(".direction-button-wrap").hide();
		}
		
		$(container).stop().animate({
			left : startX - (self.currentIndex*(size.w1 + 2))  + "rem"
		}, 0);
		
		if(currentComponent.name == "Streaming"){
			$(layer).fadeIn(500);
		}
		
		constructing = false;
		
		function _fn(i){
			if(i == self.currentIndex-1 || i == self.currentIndex || i == self.currentIndex+1){
				items[i].getReady();
			}
		}
		
		// loader.pause();
		// setTimeout(function(){
		// 	loader.resume();
		// },5000);
		
		hideLoading();
	}
	
	function findItemBySetId(itemId){
		for(var i = 0; i<items.length; i++){
			if("set." + items[i].setId == itemId){
				return items[i];
			}
		}
		return false;
	} 
	
	function afterCreated(){
			
		$(container).find("li").eq(self.currentIndex-1).addClass("left-stream");
		$(container).find("li").eq(self.currentIndex).addClass("middle-stream");
		$(container).find("li").eq(self.currentIndex+1).addClass("right-stream");
		
		$(".flipable").each(function(i,e){
			item = findItemBySetId($(e).attr("id"));
			if($(e).hasClass("ready")){
				item.getReady();
				if($(e).hasClass("middle-stream")){
					item.on();
				}else{
					item.off();
				}
			}else{
				item.destroyPlayer();
			}
		});
		
		$(container).stop().animate({
			left : startX - (self.currentIndex*(size.w1 + 2))  + "rem"
		}, 600, "easeOutQuart");
	}
	
	function findIndex(state){
		for(var i = 0; i<items.length; i++){
			if(items[i].setId == state){
				return i;
			}
		}
		return 5;
	}
	
	this.getWatchingChannel = function(){
		
		var set;
		//if(this.currentIndex <= 3) return false;
		if(this.currentIndex == 4){
			if(guideChannels.length() == 0){
				return false;
			}else{
				set = findItemBySetId("set.my");
				return set.getCurrentChannel();
			}
		}
		set = items[this.currentIndex];
		return set.getCurrentChannel();
	}
	
	this.init = function (states) {

		if(!states) states = [];
		
		if(states.length>0){
			initSetId = states[0];
			currentSetId = initSetId;
		}

		if(!constructing){
			
			
			
		}else{
			
			showLoading();
			
			request = new APIRequest({
				url : "/playerAPI/portal?v=40&minimal=true&lang=" + sphere + "&time=" + date.getHours() + "&mso=" + mso + rx(), 
				success : function(data){
					
					portal = getPortal(data);
					createItems();
					$(self).trigger("portalLoaded");
					
					if(userToken){
						request = new APIRequest({
							url : "/playerAPI/personalHistory?v=40&user=" + userToken,
							success : function(data){
								
								personalHistory = getPersonalHistory(data);
								for(var i = 0; i<personalHistory.length; i++){
									personalHistory[i].load();
								}
								if(personalHistory.length > 0){
									createHistoryItem();
								}
								//console.debug(personalHistory);
								
							}
						});
						setLoader.prepend(request);
						setLoader.moveToTop(request.id);
					}
				}
			});
			loader.prepend(request);
			loader.moveToTop(request.id);
		}
		
		if(itemCreated){
			//self.currentIndex = findIndex(states);
			self.update();
		}else{
			
		}
	}
	
	this.update = function(){

		//reset
		$(list).removeClass("middle-stream")
			   .removeClass("ready")
			   .removeClass("left-stream")
			   .removeClass("right-stream");
		
		$(container).find(".inner-info-layer").hide();
		$(container).find("li").eq(self.currentIndex).addClass("middle-stream").addClass("ready");
		
		if(self.currentIndex !=4){
			$(".middle-stream").find(".inner-info-layer").show();
		}else{
			$(".middle-stream").find(".inner-info-layer").hide();
		}
		
		var display = $(".middle-stream").find(".with-more-video").css("display");
		if(display == "block"){
			$(".direction-button-wrap").show();
		}else{
			$(".direction-button-wrap").hide();
		}
		
		$(container).find("li").eq(self.currentIndex-1).addClass("left-stream").addClass("ready");
		$(container).find("li").eq(self.currentIndex+1).addClass("right-stream").addClass("ready");
		
		
		var n = towardRight ? self.currentIndex + 2 : self.currentIndex - 2; 
		var k = towardRight ? self.currentIndex - 1 : self.currentIndex + 1; 
		$(container).find("li").eq(n).addClass("ready");
		$(container).find("li").eq(k).addClass("side-animate");
		
		$(container).stop().animate({
			left : startX - (self.currentIndex*(size.w1 + 2))  + "rem"
		}, 900, "easeOutQuart", function(){

			$(container).find("li").removeClass("side-animate");
			//remove extra players and resume players
			$(".flipable").each(function(i,e){
				item = findItemBySetId($(e).attr("id"));
				if($(e).hasClass("ready")){
					item.getReady();
					if($(e).hasClass("middle-stream")){
						
						if(streamingStartTime){
							item.on(streamingStartTime);
							streamingStartTime = false;
						}else{
							item.on();
						}
						
					}else{
						item.off();
						
					}
				}else{
					item.destroyPlayer();
				}
			});
		});
	}

	this.selectDown = function () {
		
		if($(list + ".middle-stream").attr("id") == undefined) return;
		
		var setId = $(list + ".middle-stream").attr("id").split(".")[1];
		for(var i = 0; i<items.length; i++){
			if(items[i].setId == setId){
				items[i].selectDown();
			}
		}
	}

	this.selectUp = function () {
		
		if($(list + ".middle-stream").attr("id") == undefined) return;
		
		var setId = $(list + ".middle-stream").attr("id").split(".")[1];
		for(var i = 0; i<items.length; i++){
			if(items[i].setId == setId){
				items[i].selectUp();
			}
		}
	}
	Array.prototype.move = function(from, to) {
	    this.splice(to, 0, this.splice(from, 1)[0]);
	};
	this.selectRight = function () {
		
		towardRight = true;
		
		items[self.currentIndex].off();
		
		this.currentIndex++;
		var id = $(list).eq(this.currentIndex).attr("id").split(".")[1];
		
		if(this.currentIndex == $(list).length-2){
			
			li = $(container).find("li").eq(0).remove().clone();
			$(container).append(li);
			items.move(0, items.length-1);
			
			relocated = findItemBySetId($(li).attr("id"));
			relocated.target = li;
			
			this.currentIndex = $(list).length - 3;
			$(container).css("left", startX - ((this.currentIndex-1)*(size.w1 + 2))  + "rem");
		}
		//if(this.currentIndex == $(list).length) this.currentIndex = $(list).length - 1;
		$.address.value("streaming/" + id);
		//this.update();
	}

	this.selectLeft = function () {
		
		towardRight = false;
		
		items[self.currentIndex].off();
		
		this.currentIndex--;
		if(this.currentIndex == -1) this.currentIndex = 0;
		
		var id = $(list).eq(this.currentIndex).attr("id").split(".")[1];
		
		if(this.currentIndex == 0){
			li = $(container).find("li").eq($(list).length - 1).remove().clone();
			$(container).prepend(li);
			items.move(items.length-1, 0);
			
			relocated = findItemBySetId($(li).attr("id"));
			relocated.target = li;
			
			this.currentIndex = 1;
			$(container).css("left", startX - (2*(size.w1 + 2))  + "rem");
		}
		
		$.address.value("streaming/" + id);
		//this.update();
	}
	
	this.select = function () {
		
		var li = $(list).eq(this.currentIndex);
		var setId = li.attr("id").split(".")[1];
		if(setId == "cms"){
			//location.href = "/cms/";
			location.href = "http://" + location.host + "/cms/";
			//alert(location.host + "/cms/");
			
		}else if(setId == "settings"){
			
			$(this).trigger("controllOut", ["Settings"]);
			$(document).trigger("showPopup", ["#settings-wrap, #settings-wrap>div"]);
			
		}else if(setId == "guide"){
			
			if(userToken){
				$.address.value("guide");
			}else{
				$(this).trigger("controllOut", ["Sign"]);
				$(document).trigger("showPopup", ["#sign-inup-wrap, #sign-inup-wrap>div, #sign-inup-wrap li:first-child"]);
			}
			
		}else if(setId == "store"){
			
			if(userToken){
				$.address.value("store");
			}else{
				$(this).trigger("controllOut", ["Sign"]);
				$(document).trigger("showPopup", ["#sign-inup-wrap, #sign-inup-wrap>div, #sign-inup-wrap li:first-child"]);
			}
			
		}else if(setId == "sign"){
			
			if(userToken){
				
				items[self.currentIndex].select();
				
			}else{
				$(this).trigger("controllOut", ["Sign"]);
				$(document).trigger("showPopup", ["#sign-inup-wrap, #sign-inup-wrap>div, #sign-inup-wrap li:first-child"]);				
			}
			
		}else if(setId == "my"){
			if(userToken){
				
				items[self.currentIndex].select();
				
			}else{
				$(this).trigger("controllOut", ["Sign"]);
				$(document).trigger("showPopup", ["#sign-inup-wrap, #sign-inup-wrap>div, #sign-inup-wrap li:first-child"]);
			}
		}
		else{
			findItemBySetId($(li).attr("id")).select();
		}
	}
	
	this.togglePlay = function(){
		items[self.currentIndex].togglePlay();
	}

	this.exit = function(){
		// for(var i = 0; i<items.length; i++){
			// items[i].destroyPlayer();
		// }
	}
	onConstructed();
}

Flipable.prototype = new Flipable("Flipable");
Flipable.prototype.constructor = Flipable;
var flipables = [], flipableQueue = [];
var streamingPlayers = [];
var setLoader = new APIRequestLoader(400);
var playbackStartTime = false;
function Flipable(){
	
	var dur = 600;
	var target;
	var self = this;
	var channels, channel;
	var episodes, episode;
	var len = 0;
	var dom;
	var playerId, player = false;
	var request;
	var countDownTimeout, countDownInterval;
	var countDown = "#streaming-layer .countdown-layer";
	var number = countDown + " .countdown-number";
	var nextTitle = countDown + " .next-ch-title";
	var playerReady = false;
	var pdrTimeout;
	var currentTime = 0;
	var playerInterval;
	this.setInfo;
	this.currentIndex = 0;
	this.target;
	this.isReady = false;
	this.isPlaying = false;

	function updateTitle(){
		
		dom = $("li[id='set." + self.setId + "']");
		
		function _try(str){try{return eval(str)}catch(e){return false}}
		
		if(self.setId == "my"){
			if(userToken){
				
				var onReady = function(){
					
					var ch = guideChannels.arr[self.currentIndex];
					var ep = ch.episodes[0];
					
					$(dom).find(".ch-title").html(_try("ch.name"));
					$(dom).find(".ch-which").html("(" + (self.currentIndex+1) + " of "+ guideChannels.length() +")");
					$(dom).find(".ep-title").html(ep.name);
					$(dom).find(".ep-update").html(timeDifference(new Date, ep.timestamp || ep.published));
					$(dom).find(".ch-info-layer").show();
					
					try{
						player.load(ep.programs[0].videoId);
					}catch(e){
						console.warn("videoId not found");
						console.warn(ep);
					}
					
					if(ch.images[0]){
						$(dom).find(".ch-thumb-inner-center>img").attr("src", ch.images[0]);
					}else if(ch.images[1]){
						$(dom).find(".ch-thumb-inner-center>img").attr("src", ch.images[1]);
					}else{
						$(dom).find(".ch-thumb-inner-center>img").attr("src", defaultThumbUrl);
					}
				}
				var fn = function(){
					var ch = guideChannels.arr[self.currentIndex];
					if(ch != undefined){
						if(ch.isReady == false){
							ch.load();
							loader.moveToTop(ch.request.id);
							$(ch).unbind("ready");
							$(ch).bind("ready", function(){
								$(ch).unbind("ready");
								onReady();
							})
						}else{
							onReady();
						}
					}else{
						$(dom).find(".ch-thumb, .info-wrap").hide();
					}
				}
				
				if(channelLineupReady){
					fn();
				}else{
					$(document).bind("channelLineupReady", fn);
				}
				
			}else{
				var val = $.address.value();
				var arr = val.split("/");
				if(arr[arr.length-1] == "my"){
					//$(dom).find(".inner-info-layer").show();
				}
			}
			
		}
		else if(self.setId == "promo"){
			var ch = _try("channels[self.currentIndex]");
			var ep = _try("episodes[self.currentIndex]");
			
			if(!ch) return;
			
			$(dom).find(".ch-title").html(_try("ch.name"));
			$(dom).find(".ch-which").html("(" + (self.currentIndex+1) + " of "+ len +")");
			$(dom).find(".ch-thumb-inner-center img").attr("src", ch.images[0]);
			$(dom).find(".ch-info-layer").show();

			if(promoSet.episode){
				$(dom).find(".ep-title").html(_try("ep.name"));
				$(dom).find(".ep-update").html(timeDifference(new Date, _try("ep.published")));
			}else{
				$(dom).find(".ep-title").html(_try("ep.programNames[0]"));
				$(dom).find(".ep-update").html(timeDifference(new Date, _try("ep.timestamp")));
			}
		}
		else if(self.setId == "history"){
			var ch = _try("channels[self.currentIndex]");
			var ep = _try("ch.episodes[0]");
			
			if(!ch) return;
			
			$(dom).find(".ch-title").html(_try("ch.name"));
			$(dom).find(".ch-which").html("(" + (self.currentIndex+1) + " of "+ len +")");
			$(dom).find(".ep-title").html(_try("ep.name"));
			$(dom).find(".ep-update").html(timeDifference(new Date, _try("ep.published")));
			$(dom).find(".ch-thumb-inner-center img").attr("src", ch.images[0]);
			$(dom).find(".ch-info-layer").show();
		}
		else{
			
			var ch = _try("channels[self.currentIndex]");
			var ep = _try("episodes[self.currentIndex]");
			
			if(!ch) return;
			
			$(dom).find(".ch-title").html(_try("ch.name"));
			$(dom).find(".ch-which").html("(" + (self.currentIndex+1) + " of "+ len +")");
			$(dom).find(".ep-title").html(_try("ep.programNames[0]"));
			$(dom).find(".ep-update").html(timeDifference(new Date, _try("ep.timestamp")));
			$(dom).find(".ch-thumb-inner-center img").attr("src", ch.images[0]);
			$(dom).find(".ch-info-layer").show();
		}
	}
	
	function currentVideoId(){
		
		var url;
		var getId = function(str){
			return str.split("v=").length > 1 ? str.split("v=")[1] : false;
		}

		if(self.setId == "history"){
			var ch = channels[self.currentIndex];
			if(ch.isReady){
				episode = ch.episodes[0];
				return episode.programs[0].videoId;
			}else{
				return false;
			}
		}else if(self.setId == "promo"){
			episode = episodes[self.currentIndex];
			if(episode.url1){
				url = episode.url1;
				if($.isArray(url)){
					return url.length > 1 ? getId(url[1]) : getId(url[0]);
				}
				return getId(url);	
			}else{
				return episode.programs[0].videoId;
			}
		}
		else{
			episode = episodes[self.currentIndex];
			if(episode == undefined) return false;
			url = episode.url1;
			if($.isArray(url)){
				return url.length > 1 ? getId(url[1]) : getId(url[0]);
			}
			return getId(url);	
		}
	}
	
	function nextVideoId(){
		
		episode = nextEpisode();
		if(episode == undefined) return false;
		
		var url = episode.url1;
		var getId = function(str){
			return str.split("v=").length > 1 ? str.split("v=")[1] : false;
		}
		if($.isArray(url)){
			return url.length > 1 ? getId(url[1]) : getId(url[0]);
		}
		return getId(url);
	}
	
	function nextEpisode(){
		if(episode == undefined) return false;
		var nextIndex = self.currentIndex + 1;
		if(nextIndex == episodes.length){
			nextIndex = 0;
		}
		return episodes[nextIndex];
	}
	
	function nextIndex(){
		var nextIndex = self.currentIndex + 1;
		if(nextIndex == episodes.length){
			nextIndex = 0;
		}
		return nextIndex;
	}
	
	function playNextEpisdoe(){
		
		self.selectUp();
	}
	
	function reportPDR(){
		
		var delta = Math.floor((new Date().getTime() - timezero) / 1000);
		var serialized;
		var pdr = getPdr();
		if (!userToken){
			serialized = 'session=' + (timezero / 1000) + '&' + 'time=' + delta + '&' + 'pdr=' + encodeURIComponent(pdr);
		}else{
			serialized = 'user=' + userToken + '&' + 'session=' + Math.floor(timezero / 1000) + '&' + 'time=' + delta + '&' + 'pdr=' + encodeURIComponent(pdr);
		}
	
		$.ajax({
			type: 'POST',
			url: "/playerAPI/pdr",
			data: serialized,
			dataType: 'text',
			success: function(res){ 
			},
			error: function(res){
			}
		});
	}
	
	function getPdr(){
		if(self.setId == "history"){
			return;
		}
		var ep = episodes[self.currentIndex];
		var eid;
		if(ep.programId){
			eid = ep.programId;
		}else if(ep.ytVideoIds){
			eid = "yt" + ep.ytVideoIds[0];
		}else if(ep.id){
			eid = ep.id;
		}
		return "\tw\t" + channels[self.currentIndex].id + "\t" + eid;
	}
	
	function showCountDown(){
		
		$(countDown).show();
		
		try{
			$(nextTitle).html("");
			var ep = nextEpisode();
			if(ep){
				$(nextTitle).html(ep.programName[0]);
			}
		}catch(e){
			
		}
		$(number).html(5);
		
		var count = 5;
		clearInterval(countDownInterval);
		countDownInterval = setInterval(function(){
			count--;
			if(count<0) count = 0;
			$(number).html(count);
		}, 1000);
		
		clearTimeout(countDownTimeout);
		countDownTimeout = setTimeout(function(){
			$(countDown).hide();
			$(nextTitle).html();
			clearInterval(countDownInterval);
			playNextEpisdoe();
		},5500);
	}
	
	this.getCurrentChannel = function(){
		
		if(!channels) return false;
		if(channels.length == 0) return false;
		return channels[this.currentIndex];
	}
	
	this.destroyPlayer = function(){
		
		playerReady = false;
		
		if(player){
			$(player).unbind();
			player.destroy();
			player = false;
			$("#player-" + self.setId).remove();
		}
	}
	
	this.getReady = function(){
		
		if((!userToken) && this.setId == "my") return;
		if(this.setId == "cms" || this.setId == "settings" || this.setId == "guide" || this.setId == "store"){
			return;
		}

		var bool = $(this.target).find(".player-wrap object");
		if(!player || bool.length == 0){

			playerId = "player-" + self.setId;
			$(self.target).find(".player-wrap").append("<div id='" + playerId + "' />");
			
			player = new YTPlayer(playerId);
			streamingPlayers.push(player);
			$(player).one("onPlayerReady", function(){
				playerReady = true;
				
				if(self.setId == "history"){

					var vid = currentVideoId(); 
					if(vid == false){
						var ch = channels[self.currentIndex];
						if(!ch.isReady){
							setLoader.moveToTop(ch.request.id);
							$(ch).one("ready", function(){
								vid = currentVideoId();
								if(vid != false){
									player.loadVideoById(vid);
									player.seekTo(3);
									player.pause();
									player.show();
								}
							})
						}
					}else{
						player.loadVideoById(vid);
						player.seekTo(3);
						player.pause();
						player.show();
					}
				}else{
					player.load(currentVideoId());
					player.seekTo(3);
					player.pause();
					player.show();	
				}

				$(self).trigger("itemReady");
				$(self).trigger("playerReady");
			});
		}
	}
	
	this.on = function(startTime){

		dom = $("li[id='set." + self.setId + "']");
		
		if(player){
			
			clearTimeout(pdrTimeout);
			pdrTimeout = setTimeout(function(){
				reportPDR();
			},10000);
			
			player.play();
			this.isPlaying = true;

			//for sync-ing with playback back-and-forth
			if(startTime){
				player.seekTo(startTime);
			}

			clearInterval(playerInterval);
			playerInterval = setInterval(function(){
				currentTime = player.getCurrentTime();
			}, 1000)

			$(player).unbind("ended");
			$(player).bind("ended", showCountDown);
		}else{
			//console.debug("no player");
		}
		
		if($(dom).find(".with-more-video").length != 0){
			$(dom).find(".inner-info-layer").hide();
		}else{
			$(dom).find(".inner-info-layer").show();
		}
		
		if(!userToken){
			
		}

		reportGA_streaming();
	}
	
	this.off = function(){
		
		clearTimeout(pdrTimeout);
		clearInterval(playerInterval);
		currentTime = 0;
		
		if(player){
			$(player).unbind("ended");
			player.seekTo(3);
			player.pause();
			player.show();
		}
		
		dom = $("li[id='set." + self.setId + "']");
	}
	
	this.togglePlay = function(){
		if(!player) return;
		if(this.isPlaying){
			player.pause();
			this.isPlaying = false;

			var ch = self.getCurrentChannel();
			console.debug(ch);
			$(gaEvents).trigger("paused", [ch.id, ch.name]);

		}else{
			player.play();
			this.isPlaying = true;

			var ch = self.getCurrentChannel();
			$(gaEvents).trigger("resumed", [ch.id, false, ch.name]); 
		}
	}

	//this will be called only once
	this.init = function(target){
		
		this.target = target;
		this.flipable = $(target).hasClass("flipable");
		this.setId = $(target).attr("id").split(".")[1];
		if(this.setId == "my"){
			this.flipable = true;
		}
		if(this.flipable && $(target).attr("id")){
			
			if(this.setId == "my"){
				
				function fn(){
					channels = guideChannels.arr;
					episodes = [];
					for(var i = 0; i<channels.length; i++){
						if(channels[i].lastEpisode){
							episode = channels[i].lastEpisode;
							episodes.push(episode);
						}
					}
					
					len = episodes.length;
					updateTitle();
					$(self).trigger("dataLoaded");
				}
				
				
				this.target = target;
				
				if(channelLineupReady){
					fn();
				}else{
					$(document).bind("channelLineupReady", fn);
				}
				
			}else if(this.setId == "promo"){

				request = new APIRequest({
					url : "/playerAPI/shareInChannelList?v=40&channel=" + promoSet.channel + "&mso=" + mso + rx(),
					success : function(data){
						self.setInfo = getSetInfo(data);
						channels = self.setInfo.channels;
						streamingChannels.append(channels);
						episodes = self.setInfo.episodes;
						len = channels.length;

						for(var i = 0; i<channels.length; i++){
							if(channels[i].id == promoSet.channel){
								self.currentIndex = i;
								break;
							}
						}

						if(promoSet.episode){
							
							$.ajax({
								url : "/playerAPI/channelLineup?channel=" + promoSet.channel + "&v=40&mso=" + mso + rx(),
								success : function(res){
									var lineup = getChannelLineup(res);
									var ch = lineup.eq(0);
									ch.load();
									loader.moveToTop(ch.request.id);
									$(ch).one("ready", function(){

										var eps = ch.episodes;
										episode = eps[0];
										for(i = 0; i<eps.length; i++){
											if(promoSet.episode == eps[i].id){
												episode = eps[i];
												episodes[self.currentIndex] = episode;
												break;
											}
										}

										updateTitle();
										$(self).trigger("dataLoaded");

										$(gaEvents).trigger("promoChannel", [promoSet.channel, promoSet.episode, self.setInfo.name]);
										$(gaEvents).trigger("promoEpisode", [promoSet.channel, promoSet.episode, episode.name]);
										$(gaEvents).trigger("promoProgram", [promoSet.channel, promoSet.episode, "yt" + episode.programs[0].videoId, episode.programs[0].name]);
									});
								}
							});
						}else{

							updateTitle();
							$(self).trigger("dataLoaded");

							var ch = channels[self.currentIndex];
							var ep = episodes[self.currentIndex];

							$(gaEvents).trigger("promoChannel", [promoSet.channel, ch.name]);
							$(gaEvents).trigger("promoEpisode", [promoSet.channel, ep.programId, ep.programNames[0]]);
							$(gaEvents).trigger("promoProgram", [promoSet.channel, ep.programId, ep.ytVideoIds[1].split(";")[0],ep.programNames[0]]);
						}
						
						$("li[id='set.promo']").find(".set-title").html(self.setInfo.set.name);
					}
				});
				setLoader.prepend(request);


			}else if(this.setId == "history"){
				channels = personalHistory;
				var ch = channels[0];
				$(ch).one("ready", function(){
					len = channels.length;
					updateTitle();
				});
			}
			else{
				
				var sid = this.setId;
				if(sid == "my" || sid == "cms" || sid == "guide" || sid == "store" || sid == "settings") return;
				
				request = new APIRequest({
					url : "/playerAPI/setInfo?v=40&set=" + this.setId + "&mso=" + mso + rx(),
					success : function(data){
						self.setInfo = getSetInfo(data);
						channels = self.setInfo.channels;
						streamingChannels.append(channels);
						episodes = self.setInfo.episodes;
						len = channels.length;
						updateTitle();
						
						$(self).trigger("dataLoaded");
					}
				});
				
				setLoader.append(request);
				// $.ajax(request);
				// loader.append(request);
				// loader.moveToTop(request.id);
			}
		}
	}
	
	this.selectUp = function(){
		//if(!this.flipable) return;
		if(dom.find(".with-more-video").length ==0) return;

		this.currentIndex++;
		if(this.currentIndex == len){
			this.currentIndex = 0;
		}
		
		animateDown();

		player.pause();
		player.hide();
		
		clearTimeout(pdrTimeout);
		pdrTimeout = setTimeout(function(){
			reportPDR();
		},10000);
		
		//player.load(currentVideoId());
		
		//my set will load video in updateTitle()
		updateTitle();
		if(this.setId != "my" && this.setId != "history"){
			player.load(currentVideoId());
		}else if(this.setId == "history"){
			var vid = currentVideoId();
			if(vid == false){
				var ch = channels[self.currentIndex];
				if(!ch.isReady){
					setLoader.moveToTop(ch.request.id);
					$(ch).one("ready", function(){
						vid = currentVideoId();
						if(vid != false){
							player.load(vid);
						}
					})
				}
			}else{
				player.load(vid);
			}
		}

		reportGA_streaming();
	}
	
	this.selectDown = function(){

		//if(!this.flipable) return;
		if(dom.find(".with-more-video").length ==0) return;
		
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = len-1;
		};
		animateUp();

		player.pause();
		player.hide();
		
		clearTimeout(pdrTimeout);
		pdrTimeout = setTimeout(function(){
			reportPDR();
		},10000);

		//my set will load video in updateTitle()
		updateTitle();

		if(this.setId != "my" && this.setId != "history"){
			var vid = currentVideoId();
			if(vid != false){
				player.load(currentVideoId());
			}
		}else if(this.setId == "history"){
			var vid = currentVideoId();
			if(vid == false){
				var ch = channels[self.currentIndex];
				if(!ch.isReady){
					loader.moveToTop(ch.request.id);
					$(ch).one("ready", function(){
						vid = currentVideoId();
						if(vid != false){
							player.load(vid);
						}
					})
				}
			}else{
				player.load(vid);
			}
		}

		reportGA_streaming();
	}
	
	this.select = function(){

		channel = channels[self.currentIndex];

		if(this.setId == "history"){
			referer = "streaming";
			$.address.value("playback/" + channel.id);
			$(gaEvents).bind("enter");
		}else{

			episode = episodes[self.currentIndex];
			var epId = episode.programId;

			if(channel.nature == "3" || channel.nature == "4"){
				if(episode.ytVideoIds){
					epId = "yt" + episode.ytVideoIds[0];
				}else if(episode.url1){
					epId = "yt" + episode.url1[0].split("v=")[1];
				}else if(episode.programs){
					epId = episode.programs[0].videoId;
				}
			}else if(epId == undefined){
				epId = episode.id;
			}
			
			var addr = "playback/" + channel.id + "/" + epId;
			
			player.pause();

			if(currentTime > 3){
				playbackStartTime = currentTime - 3;
			}
			
			referer = "streaming";
			$.address.value(addr);
			$(gaEvents).bind("enter");
		}
	}
	
	function reportGA_streaming(){

		var ch = channels[self.currentIndex];
		var ep = episodes[self.currentIndex];

		$(gaEvents).trigger("channelChanged", [ch.id, ch.name]);

		if(ep.currentIndex){
			//ep is NNEpisode
			$(gaEvents).trigger("episodeChanged", [ch.id, ep.id, ep.name]);
			var vid = ep.programs[0].videoId.split(";")[0];
			$(gaEvents).trigger("programChanged", [ch.id, ep.id, vid, ep.programs[0].name]);
		}else{
			
			var i = ep.programNames.length > 1 ? 1 : 0;
			$(gaEvents).trigger("episodeChanged", [ch.id, ep.programId, ep.programNames[i]]);
			var vid = ep.ytVideoIds[i].split(";")[0];
			$(gaEvents).trigger("programChanged", [ch.id, ep.programId, vid, ep.programNames[i]]);
			//ep is Object
		}
	}

	function animateUp() {

		target = $("li[id='set." + self.setId + "']");
		target.find('.video-layer, .ch-info-layer').css({
			'visibility': "hidden"
		});
	
		target.find(".more-video-rect-1").stop().animate({
			width: '95%',
			height: '95%',
			left: '2.5%',
			top: '-4%',
			'border-width': '3px',
			opacity: 0.6
		}, dur, function () {
			$(this).removeAttr("style");
			player.play();
			player.show();
		});
	
		target.find(".more-video-rect-2").stop().animate({
			width: '90%',
			height: '90%',
			left: "5%",
			top: "-7%",
			opacity: 0.4
		}, dur, function () {
			$(this).removeAttr("style");
		});
	
		target.find(".more-video-rect-3").stop().animate({
			width: '85%',
			height: '85%',
			left: "7.5%",
			top: "-10%",
			opacity: 0
		}, dur, function () {
			$(this).removeAttr("style");
		});
	
		target.find(".more-video-rect-4").css({
			"width": "110%",
			"height": "110%",
			"left": "-5%",
			"top": "5%",
			"opacity": 0,
			"z-index": 11
		}).stop().animate({
			width: '100%',
			height: '100%',
			left: "0",
			top: "0",
			'border-width': '0',
			opacity: 1
		}, dur, function () {
			target.find('.video-layer').css({
				'visibility': "visible"
			});
			target.find('.ch-info-layer').css({
				'visibility': "visible"
			});
			$(this).removeAttr("style");
		});
	}
	
	function animateDown() {
	
		target = $("li[id='set." + self.setId + "']");
		target.find('.video-layer, .ch-info-layer').css({
			'visibility': "hidden"
		});
	
		target.find(".more-video-rect-1").stop().animate({
			width: '110%',
			height: '110%',
			left: '-5%',
			top: '5%',
			'border-width': '3px',
			opacity: 0
		}, dur, function () {
			$(this).removeAttr("style");
			player.play();
			player.show();
		});
	
		target.find(".more-video-rect-2").stop().animate({
			width: '100%',
			height: '100%',
			left: 0,
			top: 0,
			'border-width': '0',
			opacity: 1
		}, dur, function () {
			$(this).removeAttr("style");
		});
	
		target.find(".more-video-rect-3").stop().animate({
			width: '95%',
			height: '95%',
			left: "2.5%",
			top: "-4%",
			opacity: 0.6
		}, dur, function () {
			$(this).removeAttr("style");
		});
	
		target.find(".more-video-rect-4").stop().animate({
			width: '90%',
			height: '90%',
			left: "5%",
			top: "-7%",
			opacity: 0.4
		}, dur, function () {
			target.find('.video-layer').css({
				'visibility': "visible"
			});
			target.find('.ch-info-layer').css({
				'visibility': "visible"
			});
			$(this).removeAttr("style");
		});
	}
}

var listener, listeners = [];
function onYouTubePlayerReady(playerId){
	
	var ytPlayer;
	for(i = 0; i<ytplayers.length; i++){
		
		if(ytplayers[i].pid == playerId){
			
			ytPlayer = ytplayers[i];
			ytPlayer.player = ytPlayer.getPlayer();
			ytPlayer.isReady = true;
			$(ytPlayer).trigger("onPlayerReady");
			
			window["onPlayerStateChange" + i] = function(state){
				
				$(ytPlayer).trigger("stateChange", [state]);
				
				switch(state){
					case -1:
						$(ytPlayer).trigger("unstarted");
					break;
					case 0:
						$(ytPlayer).trigger("ended");
					break;
					case 1:
						$(ytPlayer).trigger("playing");
					break;
					case 2:
						$(ytPlayer).trigger("paused");
					break;
					case 3:
						$(ytPlayer).trigger("buffering");
					break;
				}
			}
			
			window["onPlayerError" + i] = function(error){
				$(ytPlayer).trigger("error", [error]);
				switch(error){
					case 2:
						$(ytPlayer).trigger("invalidParameter", [error]);
					break;
					case 100:
						$(ytPlayer).trigger("videoNotFound", [error]);
					break;
					case 101:
					case 150:
						$(ytPlayer).trigger("notAllowed", [error]);
					break;
				}
			}
			ytPlayer.getPlayer().addEventListener("onStateChange", "onPlayerStateChange" + i);
			ytPlayer.getPlayer().addEventListener("onError", "onPlayerError" + i);
		}
	}
}
var ytplayers = [];
function YTPlayer(pid){
	
	var wmode = "transparent";
	//var wmode = "";
	var url = "http://www.youtube.com/apiplayer?version=3&enablejsapi=1&playerapiid=" + pid;
	var params = {
		allowScriptAccess: "always",
		wmode: wmode,
		disablekb: "0"
	};
	var atts = {
		id: pid
	};
	var self = this;
	function onConstructed(){
		
		ytplayers.push(self);
		
		//$(self.getPlayer()).css("left", "1000rem").css("top", 0);
		swfobject.embedSWF(url, pid, "100%", "100%", "8", null, null, params, atts);
		self.player = document.getElementById(pid);
		
		//$(self.player).css("position", "absolute").css("left", "1000rem").css("top", "0");
	}
	function isPlayerValid(){
		//return true;
		if(self.getPlayer() == null){
			//console.debug(self.pid + " is null");
			return false;
		}
		if(!self.getPlayer().hasOwnProperty("playVideo")){
			//console.debug(self.pid + " is reloading"); 
			return false;
		}
		return true;
	}
	this.player  = document.getElementById(pid);
	this.pid = pid;
	this.videoId = false;
	this.isCurrentPlayer = false;
	this.isReady = false;
	this.preloadType = "";
	this.testValid = function(){
		if(self.getPlayer() == null){
			console.debug(self.pid + " is null");
			return false;
		}
		if(typeof self.getPlayer().playVideo != "function"){
			console.debug(self.pid + " is reloading");
			return false;
		}
		return true;
	}
	this.getPlayer = function(){
		return document.getElementById(this.pid);
	}
	this.load = function(vid){
		if(!this.isReady) return;
		if(vid == false){
			console.warn("video id not found");
			return;
		}
		this.videoId = vid;
		this.player = document.getElementById(pid);
		this.getPlayer().loadVideoById(vid);
		this.getPlayer().seekTo(0);
		this.getPlayer().pauseVideo();
	}
	this.loadAndSeekTo = function(vid, sec){
		if(!this.isReady) return;
		if(vid == false){
			console.warn("video id not found");
			return;
		}
		this.videoId = vid;
		this.player = document.getElementById(pid);
		this.getPlayer().loadVideoById(vid);
		this.getPlayer().seekTo(sec);
		this.getPlayer().pauseVideo();
	}
	this.loadVideoById = function(vid){
		if(!this.isReady) return;
		this.videoId = vid;
		this.getPlayer().loadVideoById(vid);
		this.getPlayer().mute();
		$(this.getPlayer()).attr("class", vid);
	}
	this.seekTo = function(sec){
		if(!this.isReady) return;
		this.getPlayer().seekTo(sec);
	}
	this.pause = function(){
		if(!this.isReady) return;
		if(this.getPlayer() == null || !this.getPlayer().pauseVideo) return;
		this.getPlayer().pauseVideo();
		this.getPlayer().mute();
	}
	this.play  = function(){
		if(!this.isReady) return;
		this.getPlayer().setPlaybackQuality(cookie.settings.resolution);
		this.getPlayer().unMute();
		this.getPlayer().playVideo();
		this.show();

		//just mute the noise during development
		//this.getPlayer().mute();
	}
	this.hide = function(){
		$(this.getPlayer()).css("left", "1000rem");
	}
	this.show = function(){
		$(this.getPlayer()).css("left", "0");
	}
	this.getCurrentTime = function(){
		if(!this.isReady) return 0;
		return this.getPlayer().getCurrentTime();
		return 0;
	}
	this.destroy = function(){
		$(this.getPlayer()).unbind();
		$(this.getPlayer()).remove();
	}
	onConstructed();
}

Settings.prototype = new Controllable("Settings");
Settings.prototype.constructor = Settings;
function Settings(){
	
	var self = this;
	var focused, comps, tab, form, buttons;
	this.selectLeft = function(){
		if(focused != buttons){
			tab.selectLeft();
		}else{
			buttons.selectLeft();
		}
	}
	this.selectRight = function(){
		if(focused != buttons){
			tab.selectRight();
		}else{
			buttons.selectRight();
		}
	}
	this.selectUp = function(){
		focused.selectUp();
	}
	this.selectDown = function(){
		focused.selectDown();
	}
	this.select = function(){
		focused.select();
	}
	this.show = function(){
		$("#settings-wrap").show();
	}
	this.hide = function(){
		$("#settings-wrap").hide();
		$("#overlay-layer").hide();
	}
	this.init = function(){
		
		focused = tab;
		tab.currentIndex = 0;
		tab.update();
	}
	this.exit = function(){
		this.hide();
	}
	
	function onSettingsEnd(e){
		$(self).trigger("controllOut", ["StreamingPortal"]);
	}
	
	function onTabChanged(e, index){
		form.currentIndex = index;
		form.onTabChanged();
	}
	
	function onControllIn(e){
		for(var i = 0 ; i<comps.length; i++){
			if(comps[i].name == e.currentTarget.name){
				focused = comps[i];
			}
		}
		focused.init();
	}
	
	function onControllOut(e, targetName){
		var c = e.currentTarget;
		for(var i = 0 ; i<comps.length; i++){
			if(comps[i].name == targetName){
				focused = comps[i];
			}
		}
		if(focused.onFocused){
			focused.onFocused();
		}
		if(focused == buttons){
			focused.init();
		}
	}
	
	function onButtonControllOut(e){
		focused = form;
		form.selectUp();
	}
	
	tab = new SettingsTab();
	form = new SettingsForm();
	buttons = new SettingsButtons();
	comps = [tab, form, buttons];
	
	for(var i = 0; i<comps.length; i++){
		$(comps[i]).bind("controllOut", onControllOut);
		$(comps[i]).bind("controllIn", onControllIn);
		$(comps[i]).bind("indexChanged", onTabChanged);
		$(comps[i]).bind("buttonControllOut", onButtonControllOut);
		$(comps[i]).bind("settingsEnd", onSettingsEnd);
	}
	
	$(buttons).unbind("saveSettings");
	$(buttons).bind("saveSettings", function(){
		form.save();
	});
	
	this.init();
}

SettingsTab.prototype = new Controllable("SettingsTab");
SettingsTab.prototype.constructor = SettingsTab;
function SettingsTab(){
	
	this.currentIndex = 0;
	var sel = "#settings-wrap .tab-selector";
	var self = this;
	this.selectLeft = function(){
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = $(sel + ">ul>li").length-1;
		}
		this.update();
	}
	this.selectRight = function(){
		this.currentIndex++;
		if(this.currentIndex == $(sel + ">ul>li").length){
			this.currentIndex = 0;
		}
		this.update();
	}
	this.selectUp = function(){
		
	}
	this.selectDown = function(){
		$(this).trigger("controllOut", ["SettingsForm"]);
	}
	this.select = function(){
		
	}
	this.update = function(){
		$(sel + " li").removeClass("on").eq(this.currentIndex).addClass("on");
		$(this).trigger("indexChanged", [this.currentIndex]);
	}
	this.show = function(){
		
	}
	this.hide = function(){
		
	}
	this.init = function(){
		
		this.currentIndex = 0;
		$("#settings-wrap .tab-selector li").removeClass("on");
		$("#settings-wrap .tab-selector li").eq(this.currentIndex).addClass("on");
	}
	
	$(sel + " li a").click(function(e){
		$(sel + " li a").each(function(i,el){
			if($(el).html() == $(e.currentTarget).html()){
				self.currentIndex = i;
			}
		});
		self.update();
	});
	
	if(!userToken){
		$(sel + " li").eq(0).remove();
	}
}

SettingsForm.prototype = new Controllable("SettingsForm");
SettingsForm.prototype.constructor = SettingsForm;
function SettingsForm(){
	
	var currentForm, formIndex = 0;
	var self = this;
	this.currentIndex = 0;
	this.selectUp = function(){
		form = forms[this.currentIndex];
		formIndex--;
		if(formIndex == -1){
			$(this).trigger("controllOut", ["SettingsTab"]);
		}
		this.update();
	}
	this.selectDown = function(){
		form = forms[this.currentIndex];
		formIndex++;
		if(formIndex == form.length){
			$(this).trigger("controllOut", ["SettingsButtons"]);
		}
		this.update();
	}
	this.select = function(){
		form = forms[this.currentIndex];
		if(form[formIndex].prop("tagName") == "INPUT"){
			form[formIndex].click();
		}
		else if(form[formIndex].prop("tagName") == "A"){
			form[formIndex].click();
		}
	}
	this.update = function(){
		
		form = forms[this.currentIndex];
		
		for(var j = 0; j<form.length; j++){
			
			form[j].removeClass("on");
			form[j].find("input").blur();
			form[j].blur();
			
			if(formIndex == j){
				form[j].addClass("on");
				if(form[j].hasClass("form-group")){
					form[j].find("input").focus();
				}else{
					form[j].parent().focus();
				}
			}
		}
	}
	this.onFocused = function(){
		formIndex = 0;
		this.update();
	}
	this.onTabChanged = function(){
		$("#settings-wrap .tab-container li").removeClass("on").eq(this.currentIndex).addClass("on");
		formIndex = -1;
		this.update();
	}
	this.show = function(){
		
	}
	this.hide = function(){
		
	}
	this.init = function(){
		formIndex = 0;
		this.update();
	}
	
	this.save = function(){
		
		var theForm = $("#settings-wrap .tab-container>ul>li").eq(this.currentIndex).find("form");
		var id = $(theForm).attr("id");
		var data = theForm.serializeArray();
		var obj = {};
		var find = function(name, arr){
			for(var i = 0; i<arr.length; i++){
				if(arr[i]["name"] == name){
					return arr[i]["value"];
				}
			}
		}
		var validate_password = function(pw){
			if(!pw) return false;
			return pw.length >= 6;
		}
		var buildQuery = function(inputs){
			var q = "user=" + data["user"] + "&key=";
			var keys = [], vals = [];
			for(var i in inputs){
				if(i!="user" && i != "password1"){
					keys.push(i);
					vals.push(inputs[i]);
				}
			}
			q += keys.join(",");
			q += "&value=";
			q += vals.join(",");
			return q;
		}
		switch(id){
			
			case "profile-form" : 
				//setUserProfile
				data = {
					user : userToken,
					name : find("display-name", data),
					oldPassword : cookie.user.password,
					password : find("change-password", data),
					password1 : find("repeat-password", data),
					gender : find("gender", data),
					year : find("birthday", data),
					phone : find("phone", data)
				}
				
				if(data["password"] == ""){
					delete data["password"];
					delete data["password1"];
					delete data["oldPassword"];
				}else{
					if(!validate_password(data["password"])){
						alert("password must contain at least 6 characters!");
						return;
					}
					if(data["password"] != data["password1"]){
						alert("retyped password does not match");
						return;
					}
				}
				
				var q = buildQuery(data);
				$.get("/playerAPI/setUserProfile?" + q, function(res){
					console.debug(res);
					var code = res.split("\t")[0];
					if(code == "0"){
						
						var msg = lang == "zh" ? "設定已成功更新!" : "Settings saved successfully!";
						dialog("custom", msg, streaming);
						
						cookie.user.name = data.name;
						cookie.user.gender = data.gender;
						cookie.user.year = data.year;
						cookie.user.phone = data.phone;
						
						updateCookie();
						
					}else{
						overlayNotice.setNotice('error');
						overlayNotice.show(function(){
							currentComponent = streaming;
						});
						currentComponent = overlayNotice;
					}
				});
				
				
			break;
			case "lang-form" :
				//setUserProfile
				
				if(userToken){
					
					data = {
						"user" : userToken,
						"sphere" : find("region", data),
						"ui-lang" : find("language", data),
					}
					
					var q = buildQuery(data);
					$.get("/playerAPI/setUserProfile?" + q, function(res){
						var code = res.split("\t")[0];
						if(code == "0"){
							
							cookie.settings.sphere = data.sphere;
							cookie.settings["ui-lang"] = data["ui-lang"];
							
							updateCookie();
							
							location.reload();
							
						}else{
							overlayNotice.setNotice('error');
							overlayNotice.show(function(){
								currentComponent = streaming;
							});
							currentComponent = overlayNotice;
						}
					});
					
				}else{
					
					cookie.settings.sphere = find("region", data);
					cookie.settings["ui-lang"] = find("language", data);
					updateCookie();
					location.reload();
				}
				
				
			break;
			case "resolution-form" :
			
			
				if(userToken){
					//setUserPref
					data = {
						user : userToken,
						resolution : find("resolution", data)
					}
					var q = buildQuery(data);
					$.get("/playerAPI/setUserPref?" + q, function(res){
						var code = res.split("\t")[0];
						if(code == "0"){
							
							cookie.settings.resolution = data.resolution;
							updateCookie();
							var msg = lang == "zh" ? "設定已成功更新!" : "Settings saved successfully!";
							dialog("custom", msg, streaming);
							
						}else{
							overlayNotice.setNotice('error');
							overlayNotice.show(function(){
								currentComponent = streaming;
							});
							currentComponent = overlayNotice;
						}
					});
				}else{
					cookie.settings.resolution = find("resolution", data);
					updateCookie();
					var msg = lang == "zh" ? "設定已成功更新!" : "Settings saved successfully!";
					dialog("custom", msg, streaming);
				}
				
			break;
			case "navigation-form" :
			
				
				if(userToken){
					 //setUserPref
					data = {
						user : userToken,
						mode : find("guide_mode", data)
					}
					var q = buildQuery(data);
					$.get("/playerAPI/setUserPref?" + q, function(res){
						var code = res.split("\t")[0];
						if(code == "0"){
							
							cookie.settings.guideMode = data.mode;
							updateCookie();
							var msg = lang == "zh" ? "設定已成功更新!" : "Settings saved successfully!";
							dialog("custom", msg, streaming);
							
						}else{
							overlayNotice.setNotice('error');
							overlayNotice.show(function(){
								currentComponent = streaming;
							});
							currentComponent = overlayNotice;
						}
					});
				}else{
					
					cookie.settings.guideMode = find("guide_mode", data);
					updateCookie();
					var msg = lang == "zh" ? "設定已成功更新!" : "Settings saved successfully!";
					dialog("custom", msg, streaming);
				}
				
			break;
		}
	}
	
	var form;
	var form1 = $("#settings-wrap .tab-container>ul>li").eq(0).find("form");
	var form2 = $("#settings-wrap .tab-container>ul>li").eq(1).find("form");
	var form3 = $("#settings-wrap .tab-container>ul>li").eq(2).find("form");
	var form4 = $("#settings-wrap .tab-container>ul>li").eq(3).find("form"); 
	
	form1.attr("id","profile-form");
	form2.attr("id","lang-form");
	form3.attr("id","resolution-form");
	form4.attr("id","navigation-form");
	
	$([form1, form2, form3, form4]).each(function(){
		$(this).find("input").attr("autocomplete", "off");
	});
	
	var forms = [
		[
			form1.find(".form-group").eq(0).find(".black-button"),
			form1.find(".form-group").eq(1), 
			form1.find(".form-group").eq(2),
			form1.find(".form-group").eq(3),
			form1.find(".form-group").eq(4).find("input").eq(0),
			form1.find(".form-group").eq(4).find("input").eq(1),
			form1.find(".form-group").eq(5),
			form1.find(".form-group").eq(6)
		],
		[
			form2.find(".form-group").eq(1).find("input").eq(0),
			form2.find(".form-group").eq(1).find("input").eq(1),
			form2.find(".form-group").eq(3).find("input").eq(0),
			form2.find(".form-group").eq(3).find("input").eq(1)
		],
		[
			form3.find(".form-group").eq(1).find("input").eq(0),
			form3.find(".form-group").eq(1).find("input").eq(1),
			form3.find(".form-group").eq(1).find("input").eq(2),
			form3.find(".form-group").eq(1).find("input").eq(3)
		],
		[
			form4.find(".form-group").eq(1).find("input").eq(0),
			form4.find(".form-group").eq(1).find("input").eq(1)
		]
	];
	formIndex = -1;
	this.update();
	
	//add id
	$(".form-group").each(function(i,e){
		$(e).attr("id", "form-group-" + i);
	});
	
	//profile
	form1.find("#form-group-9 input").eq(0).val(1);
	form1.find("#form-group-9 input").eq(1).val(0);
	form1.find("#form-group-10 input").attr("name", "birthday");
	form1.find("#form-group-11 input").attr("name", "phone");
	
	//ui-lang
	form2.find("#form-group-13 input").eq(0).val("en");	
	form2.find("#form-group-13 input").eq(1).val("zh");
	
	//region
	form2.find("#form-group-15 input").eq(0).val("en");	
	form2.find("#form-group-15 input").eq(1).val("zh");
	
	//resolution
	form3.find("#form-group-17 input").eq(0).val("default");
	form3.find("#form-group-17 input").eq(1).val("hd1080");
	form3.find("#form-group-17 input").eq(2).val("hd720");	
	form3.find("#form-group-17 input").eq(3).val("large");
	
	//guide mode
	form4.find("#form-group-19 input").eq(0).val(1);
	form4.find("#form-group-19 input").eq(1).val(2);
	
	//enable mouse
	$(".form-group").click(function(e){
		form = forms[self.currentIndex];
		
		var tag, div;
		for(var i = 0; i<form.length; i++){
			
			tag = form[i].get(0).tagName;
			if(tag == "INPUT"){
				if(form[i].parent().hasClass(".form-group")){
					div = form[i].parent();
				}else{
					div = form[i].parent().parent();
				}
			}else if(tag == "DIV"){
				div = form[i];
			}else if(tag == "A"){
				div = form[i].parent().parent();
			}
			if($(e.currentTarget).attr("id") == div.attr("id")){
				formIndex = i;
			}
		}
		self.update();
	});
	
	//wait for cookie being set properly
	//init the form
	setTimeout(function(){

		cookie = $.cookie(cookieName);
		var user = cookie.user;
		var settings = cookie.settings;
		
		if(!userToken){
			//remove profile form
			$("#settings-wrap .tab-container>ul>li").eq(0).remove();
			forms.splice(0,1);
			self.onTabChanged();

		}else{
			
			//set profile form default values
			$("input[name='display-name']").val(user.name);
			$("#form-group-5 .form-elements > span").html(user.email);
			
			$("#gender1, #gender2").attr("checked", false);
			if(user.gender == "0"){
				$("#gender2").attr("checked", true);
				$("label[for=gender2]").click();
			}else{
				$("#gender1").attr("checked", true);
				$("label[for=gender1]").click();
			}
			$("#profile-form input[name=birthday]").val(user.year);
			$("#profile-form input[name=phone]").val(user.phone);
		}
		
		//set language form
		if(settings["ui-lang"] == "en"){
			$("label[for=language1]").click();
		}else{
			$("label[for=language2]").click();
		}
		if(settings["sphere"] == "en"){
			$("label[for=region1]").click();
		}else{
			$("label[for=region2]").click();
		}
		
		//set resolution form
		$("#form-group-17 input").each(function(i,e){
			if($(e).val() == settings.resolution){
				$(e).next().click();
			}
		});
		
		//set navigation form
		$("#form-group-19 input").each(function(i,e){
			if($(e).val() == settings.guideMode){
				$(e).next().click();
			}
		});
		
		
		var signOutBtn = $("#form-group-5 .black-button");
		signOutBtn.click(function(){
					
			removeCookie();
			
			// $.cookie.jason = false;
			// $.cookie("user", null, { expires : -1 });
			// $.cookie.jason = true;
			
			//location.href = "/tv";
			$.get("/playerAPI/signout?user=" + userToken, function(res){
				var code = res.split("\t")[0];
				if(code == "0"){
					
				}else{
					console.info("sign out failed");
				}
				location.reload();
			});
			return false;
		});

	},1500);
	
}

SettingsButtons.prototype = new Controllable("SettingsButtons");
SettingsButtons.prototype.constructor = SettingsButtons;
function SettingsButtons(){
	
	var layer = "#settings-wrap .tab-container .overlay-button-wrap";
	var self = this;
	this.currentIndex = 0;
	this.selectLeft = function(){
		this.currentIndex--;
		this.currentIndex = this.currentIndex == -1 ? 1 : 0;
		this.update();
	}
	this.selectRight = function(){
		this.currentIndex++;
		this.currentIndex = this.currentIndex == 2 ? 0 : 1;
		this.update();
	}
	this.selectUp = function(){
		$("#settings-wrap .tab-container .overlay-button-wrap").removeClass("on");
		$(this).trigger("buttonControllOut");
		$(layer + " a").removeClass("on");
	}
	this.select = function(){
		
		if(this.currentIndex == 0){
			//close
			$(document).trigger("hidePopup");
			$(this).trigger("settingsEnd");
			this.currentIndex = -1;
			this.update();
			
			$("#settings-wrap, #settings-wrap>div").hide();
			
		}else{
			//save
			//alert("設定已儲存成功!");
			$(this).trigger("saveSettings");
		}
	}
	this.update = function(){
		$(layer + " a").removeClass("on").eq(this.currentIndex).addClass("on");
	}
	this.init = function(){
		this.currentIndex = 0;
		this.update();
	}
	
	$(layer + " a").click(function(e){
		
		self.currentIndex = $(layer + " a").index(e.currentTarget);
		
		self.update();
		self.select();
	});
}

Sign.prototype = new Controllable("Sign");
Sign.prototype.constructor = Sign;
function Sign(){
	
	var self = this;
	var focused, comps, tab, form, buttons;
	this.selectLeft = function(){
		focused.selectLeft();
	}
	this.selectRight = function(){
		focused.selectRight();
	}
	this.selectUp = function(){
		focused.selectUp();
	}
	this.selectDown = function(){
		focused.selectDown();
	}
	this.select = function(){
		focused.select();
	}
	this.show = function(){
		$("#settings-wrap").show();
	}
	this.hide = function(){
		$("#settings-wrap").hide();
	}
	this.init = function(){

		focused = tab;
		tab.currentIndex = 0;
		tab.update();

		form.currentIndex = 0;
		form.onTabChanged(); 
	}
	
	tab = new SignTab();
	form = new SignForm();
	buttons = new SignButtons();
	comps = [tab, form, buttons];
	
	
	for(var i = 0; i<comps.length; i++){
		$(comps[i]).unbind();
		$(comps[i]).bind("controllOut", onControllOut);
		$(comps[i]).bind("controllIn", onControllIn);
		$(comps[i]).bind("indexChanged", onTabChanged);
		$(comps[i]).bind("buttonControllOut", onButtonControllOut);
		$(comps[i]).bind("signIn", onSignIn);
		$(comps[i]).bind("signUp", onSignUp);
		$(comps[i]).bind("signEnd", onSignEnd);
	}
	
	this.init();
	
	function onSignIn(e){
		
		var form = $("#sign-inup-wrap .tab-container li").eq(0).find("form");
		var data = form.serialize();
		var arr = form.serializeArray();
		
		$.get("/playerAPI/login?" + data, function(res){
			
			var res = getLoginResponse(res);
			if(res.success){
				
				cookie.user.name = res.name;
				cookie.user.signed = true;
				cookie.user.token = res.token;
				cookie.user.lastLogin = res.lastLogin;
				cookie.user.password = arr[1]["value"];
				
				$.cookie(cookieName, cookie);
				
				$(document).trigger("userLoggedIn");
				$(document).trigger("hidePopup");
				$(self).trigger("controllOut", ["StreamingPortal"]);
				
				$("#sign-inup-wrap").hide();
				
				location.reload();
				
			}else{
				
				var zh = "您輸入的帳號或密碼有誤，請重新輸入!";
				var en = "Log in failed. Please try again";
				
				currentComponent = overlayNotice;
		
				overlayNotice.setNotice('custom');
				overlayNotice.setMode('Close');
				// overlayNotice.setMode('YesNo');
				overlayNotice.show(function(){
					currentComponent = streaming;
				},{
					message : eval(lang),
					closeOverlayLayer : false
				});
			}
		});
	}
	
	function onSignUp(e){
		
		var form = $("#sign-inup-wrap .tab-container li").eq(1).find("form");
		var data = form.serialize();
		var arr = form.serializeArray();
		var obj = form.serializeObject();
		
		if(obj["password"] != obj["verify-password"]){
			
			var zh = "再次輸入的密碼與密碼不符";
			var en = "Verifying password does not match the password";
			
			currentComponent = overlayNotice;
		
			overlayNotice.show(function(){
				currentComponent = buttons;
			},{
				custom : eval(lang)
			});
			return;
		}
		
		$.get("/playerAPI/signup?" + data, function(res){
			
			var res = getSignUpResponse(res);
			if(res.success){
				
				var zh = "註冊成功!";
				var en = "Sign up successfully!";
				
				$(document).trigger("userLoggedIn");
				$(document).trigger("hidePopup");
				$(self).trigger("controllOut", ["StreamingPortal"]);
				$("#sign-inup-wrap").hide();
				alert(eval(lang));
				location.reload();
				
			}else{
				
				var zh = "註冊失敗，請稍後再試一次";
				var en = "Sign up failed. Please try again.";
				alert(eval(lang));
			}
		});
	}
	
	function onSignEnd(e){
		$(self).trigger("controllOut", ["StreamingPortal"]);
	}
	
	function onTabChanged(e, index){
		form.currentIndex = index;
		form.onTabChanged();
	}
	
	function onControllIn(e){
		for(var i = 0 ; i<comps.length; i++){
			if(comps[i].name == e.currentTarget.name){
				focused = comps[i];
			}
		}
		//focused.init();
	}
	
	function onControllOut(e, targetName, formIndex){
		
		for(var i = 0 ; i<comps.length; i++){
			if(comps[i].name == targetName){
				focused = comps[i];
			}
		}
		focused.init(formIndex);
	}
	
	function onButtonControllOut(e){
		focused = form;
		form.selectUp();
	}
}

SignTab.prototype = new Controllable("SignTab");
SignTab.prototype.constructor = SignTab;
function SignTab(){
	
	this.currentIndex = 0;
	var sel = "#sign-inup-wrap .tab-selector li";
	this.selectLeft = function(){
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = 1;
		}
		this.update();
	}
	this.selectRight = function(){
		this.currentIndex++;
		if(this.currentIndex == 2){
			this.currentIndex = 0;
		}
		this.update();
	}
	this.selectUp = function(){
		
	}
	this.selectDown = function(){
		$(this).trigger("controllOut", ["SignForm"]);
	}
	this.select = function(){
		
	}
	this.update = function(){
		$(sel).removeClass("on").eq(this.currentIndex).addClass("on");
		$(this).trigger("indexChanged", [this.currentIndex]);
	}
	this.show = function(){
		
	}
	this.hide = function(){
		
	}
	this.init = function(){
		
		$(sel).find("li").removeClass("on");
		$(sel).find("li").eq(this.currentIndex).addClass("on");
	}
}

SignForm.prototype = new Controllable("SignForm");
SignForm.prototype.constructor = SignForm;
function SignForm(){
	
	var currentForm, formIndex = 0;
	var self = this;
	this.currentIndex = 0;
	this.selectUp = function(){
		form = forms[this.currentIndex];
		formIndex--;
		if(formIndex == -1){
			$(this).trigger("controllOut", ["SignTab"]);
		}
		this.update();
	}
	this.selectDown = function(){
		form = forms[this.currentIndex];
		formIndex++;
		if(formIndex == form.length){
			$(this).trigger("controllOut", ["SignButtons", this.currentIndex]);
		}
		this.update();
	}
	this.select = function(){
		
		form = forms[this.currentIndex];
		
		var group = form[formIndex]; 
		function seamless_exit(url) {
	        window.onbeforeunload = undefined;
	        if (url.match(/^\//))
	            window.location.href = location.protocol + '//' + location.host + url;
	        else
	            window.location.href = url;
	    }
		
		if(group.prop("tagName") == "INPUT"){
			form[formIndex].click();
		}
		//fb sign in
		if(group.find("a").hasClass("facebook-button")){
			
			 removeCookie();
			 $.cookie("fb-return-hash", location.hash);
        	 seamless_exit('/playerAPI/fbLogin');
			
		}//rest password
		else if(this.currentIndex == 0 && formIndex == 3){
			
			var chk_email = forms[0][1].find("input[name='email']").val();
	        function validateEmail(email) { 
			    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
			    return re.test(email);
			} 
	        if (!validateEmail(chk_email)) {
	            var msg = lang == "zh" ? "Email格式錯誤" : "Email format is not valid";
				dialog("custom", msg, streaming);
	            return false;
	        }
	        var gurl = "/playerAPI/forgotpwd?email=" + chk_email + rx() + "&v=32";
	        $.get(gurl, function(result) {
	            var ret = result.split("\t");
	            if (ret[0] === "0") {

	            	var msg = lang == "zh" ? "密碼已寄送到您的郵件信箱。" : "Password has been sent to your mailbox";
	            	dialog("custom", msg, streaming);

					// $(document).trigger("hidePopup");
	    //          	$(self).trigger("signEnd");

	            } else {

	            }
	            
	        });
		}else if(this.currentIndex == 1 && formIndex == 4){
			//terms
			currentComponent = terms;
			terms.show(function(){
				currentComponent = streaming;
			});
		}
		else if(this.currentIndex == 1 && formIndex == 5){
			//privacy
			currentComponent = privacy;
			privacy.show(function(){
				currentComponent = streaming;
			});
		}
	}
	this.update = function(){
		
		form = forms[this.currentIndex];
		for(var j = 0; j<form.length; j++){
			
			form[j].removeClass("on");
			form[j].find(".form-elements").removeClass("on");
			form[j].find("input").blur();
			form[j].blur();
			
			if(formIndex == j){
				form[j].addClass("on");
				form[j].find(".form-elements").addClass("on");
				
				if(form[j].hasClass("form-group")){
					form[j].find("input").focus();
				}else{
					form[j].parent().focus();
				}
			}
		}
	}
	this.onTabChanged = function(){
		$("#sign-inup-wrap .tab-container li").removeAttr("style");
		$("#sign-inup-wrap .tab-container li").removeClass("on").eq(this.currentIndex).addClass("on");
		formIndex = -1;
		this.update();
	}
	this.init = function(){
		formIndex = 0;
		this.update();
	}
	
	var form;
	var form1 = $("#sign-inup-wrap .tab-container>ul>li").eq(0).find("form");
	var form2 = $("#sign-inup-wrap .tab-container>ul>li").eq(1).find("form");
	form1.find(".form-group").removeClass("on");
	form2.find(".form-group").removeClass("on");
	$([form1, form2]).each(function(){
		$(this).find("input").attr("autocomplete", "off");
	});
	var forms = [
		[
			form1.find(".form-group").eq(0), 
			form1.find(".form-group").eq(1),
			form1.find(".form-group").eq(2),
			form1.find(".form-group").eq(3)
		],
		[
			form2.find(".form-group").eq(0), 
			form2.find(".form-group").eq(1),
			form2.find(".form-group").eq(2),
			form2.find(".form-group").eq(3),
			form2.find(".form-group").eq(4).find("a").eq(0),
			form2.find(".form-group").eq(4).find("a").eq(1)
		]
	];

	form1.find(".facebook-button").click(function(){
		self.currentIndex = 0;
		formIndex = 0;
		self.select();
	});

	form2.find(".form-group").eq(4).find("a").eq(0).click(function(){
		formIndex = 4;
		self.select();
	});
	form2.find(".form-group").eq(4).find("a").eq(1).click(function(){
		formIndex = 5;
		self.select();
	});

	formIndex = -1;
	this.update();
}

SignButtons.prototype = new Controllable("SignButtons");
SignButtons.prototype.constructor = SignButtons;
function SignButtons(){
	
	var formIndex = 0;
	var layer = "#sign-inup-wrap .overlay-button-wrap";
	var self = this;
	this.currentIndex = 0;
	this.selectLeft = function(){
		this.currentIndex--;
		this.currentIndex = this.currentIndex == -1 ? 1 : 0;
		this.update();
	}
	this.selectRight = function(){
		this.currentIndex++;
		this.currentIndex = this.currentIndex == 2 ? 0 : 1;
		this.update();
	}
	this.selectUp = function(){
		$("#sign-inup-wrap .tab-container .overlay-button-wrap a").removeClass("on");
		$(this).trigger("buttonControllOut");
	}
	this.select = function(){
		
		if(this.currentIndex == 0){
			//close
			$(document).trigger("hidePopup");
			$(this).trigger("signEnd");
			this.currentIndex = -1;
			this.update();
			
			$("#sign-inup-wrap, #sign-inup-wrap>div, #sign-inup-wrap li:first-child").hide();			
			
		}else{
			//sign in or sign up
			var i = $("#sign-inup-wrap .tab-container li").index($("#sign-inup-wrap .tab-container li.on"));
			if(i == 0){
				//sign in
				$(this).trigger("signIn");
			}else{
				//sign up
				$(this).trigger("signUp");
			}
		}
	}
	this.update = function(){
		$("#sign-inup-wrap .tab-container li.on").find(".overlay-button-wrap a").removeClass("on").eq(this.currentIndex).addClass("on");
	}
	this.init = function(i){
		
		formIndex = i;
		this.currentIndex = 0;
		this.update();
	}
	
	$(layer).find("a").eq(0).click(function(){
		
		//close
		$(document).trigger("hidePopup");
		$(self).trigger("signEnd");
		self.currentIndex = -1;
		self.update();
		
		$("#sign-inup-wrap, #sign-inup-wrap>div, #sign-inup-wrap li:first-child").hide();		
		return false;
	});
	
	$(layer).find("a").eq(1).click(function(){
		var i = $("#sign-inup-wrap .tab-container li").index($("#sign-inup-wrap .tab-container li.on"));
		if(i == 0){
			//sign in
			$(self).trigger("signIn");
		}else{
			//sign up
			$(self).trigger("signUp");
		}
		return false;
	});
}