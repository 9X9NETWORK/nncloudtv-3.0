Store.prototype = new Controllable("Store");
Store.prototype.constructor = Store;
function Store(categories, categoryInfo){
	
	var menu, grid, focused, preview, comps, categoryInfo, search, meta;
	var layer = "#store-layer";
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
	this.hide = function(){
		focused = null;
		$("#store-layer").hide();
	}
	this.show = function(){
		$("#store-layer").show();
	}
	this.init = function(state){
		
		if(!userToken){
			$.address.value(referer);
		}
		
		grid.init();
		menu.init();
		preview.init();
		
		focused = menu;
		
		$("#manual-layer").show();
		$("#manual-layer>div").show();
		
		$("#manual-layer>div").eq(0).hide();
		$("#manual-layer>div").eq(3).hide();
		$("#manual-layer>div").eq(4).hide();
		$("#manual-layer>div").eq(5).hide();
		$("#manual-layer>div").eq(6).hide();
	}
	this.exit = function(){
		$(layer).hide();
	}
	
	menu = new StoreMenu();
	grid = new StoreGrid();
	preview = new StorePreview();
	search = new StoreSearch();
	
	comps = [menu, grid, preview, search];
	
	for(var i = 0; i<comps.length; i++){
		
		$(comps[i]).bind("controllOut", onControllOut);
		$(comps[i]).bind("controllIn", onControllIn);
		$(comps[i]).bind("menuClicked", onMenuClicked);
		$(comps[i]).bind("setPreview", onSetPreview);
	}

	$(search).bind("searchClicked", function(){
		menu.currentIndex = -1;
		menu.update();

		grid.currentIndex = -1;
		grid.update();
	})

	$(grid).bind("getNextPage", onGetNextPage);

	function onGetNextPage(e, id, page){
		$.get("/playerAPI/categoryInfo?v=40&lang=" + lang + "&category=" + id + "&programInfo=true" + "&mso=" + mso + rx() + "&start=" + page, function(data){
			categoryInfo = getCategoryInfo(data);
			meta = getCategoryInfoMeta(data);
			grid.appendPage(categoryInfo, meta);
		});
	}
	
	function onSetPreview(e, info){
		
		//console.debug(info);
		preview.setChannel(info);
		preview.loadVideo();
	}
	
	function onMenuClicked(e, id){
		
		showLoading();
		$.get("/playerAPI/categoryInfo?v=40&lang=" + lang + "&category=" + id + "&programInfo=true" + "&mso=" + mso, function(data){
			hideLoading();
			categoryInfo = getCategoryInfo(data);
			meta = getCategoryInfoMeta(data);
			grid.refresh(categoryInfo, meta);
		});
	}
	
	function onControllIn(e){
		for(var i = 0 ; i<comps.length; i++){
			if(comps[i].name == e.currentTarget.name){
				focused = comps[i];
			}
		}
		//focused.init();
	}
	
	function onControllOut(e, targetName){
		
		for(var i = 0 ; i<comps.length; i++){
			if(comps[i].name == targetName){
				focused = comps[i];
			}
		}
		focused.init();
	}
}

StoreSearch.prototype = new Controllable("StoreSearch");
StoreSearch.prototype.constructor = StoreSearch;
function StoreSearch(){
	
	var input = $(".store-search input");
	var self = this;
	this.selectRight = function(){
		$(this).trigger("controllOut", ["StoreGrid"]);
		input.blur();
	}
	this.selectDown = function(){
		$(this).trigger("controllOut", ["StoreMenu"]);
		input.blur();
	}
	this.init = function(){
		console.debug("focus");
		input.focus();
	}

	$(".store-search").bind("click",function(){
		$(self).trigger("searchClicked");
	});
}	

StoreMenu.prototype = new Controllable("StoreMenu");
StoreMenu.prototype.constructor = StoreMenu;
function StoreMenu(){
	
	var container = ".store-catalogue-list";
	var sel = ".store-catalogue-list li";
	var categories, category;
	var self = this;
	this.length = (sel).length;
	this.currentIndex = 0;
	this.dataLoaded = false;
	
	function slide(n){
		
		
		var li = $(sel).eq(self.currentIndex);
		var bottom = li.offset().top + li.height();
		var top = $(".search-wrap").offset().top + $(".search-wrap").height() + li.height(); 
		var distance = $(".search-wrap").offset().top - $("#manual-layer").offset().top;
		
		if(bottom >= $("#manual-layer").offset().top){
			$(container).animate({
				top : distance/2
			},200);
			return;
		}else if(li.offset().top <= top){
			
			var t = $(container).css("top").replace("px", "");
			distance = $(".search-wrap").offset().top - $("#manual-layer").offset().top;
			t = parseInt(t,10) + Math.abs(distance/2);
			
			if(t>10) t = 10;
			
			$(container).animate({
				top : t
			},200);
		}
	}
	
	this.selectUp = function(){
		this.currentIndex--;
		if(this.currentIndex <= -1){
			$(sel).removeClass("on");
			$(sel).eq(0).addClass("selected");
			$(this).trigger("controllOut", ["StoreSearch"]);
			this.currentIndex = -1;
			return;
		}
		slide();
		this.select();
	}
	this.selectDown = function(){
		this.currentIndex++;
		if(this.currentIndex == categories.length){
			this.currentIndex = categories.length - 1;
			return;
		}
		slide();
		this.select();
	}
	
	this.selectRight = function(){
		$(sel).removeClass("on");
		$(sel).eq(this.currentIndex).addClass("selected");
		$(this).trigger("controllOut", ["StoreGrid"]);
	}
	var timeout;
	this.select = function(){
		this.update();
		
		clearTimeout(timeout);
		timeout = setTimeout(function(){
			$(".store-catalogue-list li a").eq(self.currentIndex).click();
		},400);
	}
	this.update = function(){
		$(sel).removeClass("on");
		$(sel).eq(this.currentIndex).addClass("on");
	}
	this.init = function(){
		
		if(!this.dataLoaded){
			
			showLoading();
			
			var request = new APIRequest({
				url : "/playerAPI/category?v=40&lang=" + lang + "&mso=" + mso,
				success : function(data){
					categories = getStoreCategory(data);
					for(var i = 0; i<categories.length; i++){
						$(".store-catalogue-list").append('<li><a id="cate-'+categories[i].id+'" href="#">' + categories[i].name + '</a></li>');
					}
					$(".store-catalogue-list>li>a").click(function(e){
						var id = $(e.currentTarget).attr("id").split("-")[1];
						$(self).trigger("menuClicked", [id]);
						self.currentIndex = $(sel).index($(e.currentTarget).parent());
						self.update();
						return false;
					});
					
					$(".store-catalogue-list>li>a").eq(0).click();
					self.dataLoaded = true;
					self.update();
				}
			});
			loader.append(request);
			loader.moveToTop(request.id);
		}
		
		$(sel).each(function(i,e){
			if($(e).hasClass("selected")){
				$(e).removeClass("selected");
				$(e).addClass("on");
				self.currentIndex = i;
			}
		});
	}
	
	this.update();
	
	var self = this;
	
	//$(".scroll_container").mCustomScrollbar();
	$(".store-catalogue-list li").remove();
	
	// $(".store-catalogue-list").css("display","block");
	// $(".store-catalogue-list").css("width", "100%");
}

StoreGrid.prototype = new Controllable("StoreGrid");
StoreGrid.prototype.constructor = StoreGrid;
function StoreGrid(){
	
	var sel = ".store-item-list li";
	var container = ".store-item-list";
	var row = 0;
	var lastRow = 0;
	var totalRows = Math.floor($(sel).length/3);
	var self = this;
	var categoryInfo;
	var searchInput = ".store-search input";
	var categoryId;
	var itemCount = 20;
	var itemTotal = 0;
	var totalPages = 0;

	this.page = 0;

	function onConstructed(){
		self.currentIndex = 0;
		self.row = 0;
		self.lastRow = 0;
		$(".store-item-list").animate({
			"top" : "0"
		},700,"easeOutExpo");
		$(sel).remove();
	}
	
	this.length = $(sel).length;
	this.currentIndex = -1;
	this.selectLeft = function(){
		if(this.currentIndex%3 == 0){
			$(this).trigger("controllOut", ["StoreMenu"]);
			$(sel).removeClass("on");
			return;
		}
		var i = this.currentIndex-1;
		i = Math.max(0, i);
		this.currentIndex = i;
		this.update();
	}
	this.selectRight = function(){
		var i = this.currentIndex+1;
		i = Math.min(this.length-1, i);
		this.currentIndex = i;
		this.update();
	}
	this.selectUp = function(){
		if(this.currentIndex >= 3){
			this.currentIndex -= 3;
		}
		this.update();
	}
	this.selectDown = function(){
		
		if(this.currentIndex < this.length-3){
			this.currentIndex += 3;
		}else if(this.currentIndex == this.length - 3){
			this.currentIndex += 2;
		}else if(this.currentIndex == this.length - 2){
			this.currentIndex += 1;
		}
		this.update();
	}
	this.select = function(){
		
		$(self).trigger("controllOut", ["StorePreview"]);
		$(self).trigger("setPreview", [categoryInfo[self.currentIndex]]);
		$(document).trigger("showPopup", [".store-item-preview, .overlay-container"]);
		
		$(".overlay-container").css("top", ($(window).height() - $(".overlay-container").height())/2);
		$(".overlay-container").css("left", ($(window).width() - $(".overlay-container").width())/2);
		$(".overlay-container").css("margin-left",0).css("margin-top",0);
		setTimeout(function(){
			$(".overlay-container").css("top", ($(window).height() - $(".overlay-container").height())/2);
			$(".overlay-container").css("left", ($(window).width() - $(".overlay-container").width())/2);	
		}, 500);
	}
	this.init = function(){
		this.currentIndex = 0;
		this.update();
	}
	this.update = function(){
	
		$(sel).removeAttr("class");
		$(sel).eq(this.currentIndex).addClass("on");
		
		this.lastRow = this.row;
		var i = this.currentIndex;
		if(i<0) i = 0;
		this.row = Math.floor(i/3);
		
		//row change 
		if(this.row>this.lastRow){
			//downward
			if(this.row > 1){
				$(container).stop().animate({
					"top" : (0-(this.row-1)*11.6)+"rem"
				},700,"easeOutExpo");
			}else if(this.row ==0){
				$(container).stop().animate({
					"top" : "0rem"
				},700,"easeOutExpo");
			}

			if(this.row >= (totalRows - 2)){
				if(this.page < (totalPages)){
					this.page++;
					$(this).trigger("getNextPage", [categoryId, this.page*itemCount])
				}
			}

		}else if(this.row < this.lastRow){
			//upward
			if(this.row != 0){
				$(container).stop().animate({
					"top" : (-(this.row-1)*11.6)+"rem"
				},700,"easeOutExpo");
			}else{
				$(container).stop().animate({
					"top" : "0rem"
				},700,"easeOutExpo");
			}
		}
	}
	
	var tmp = '<li>  <a href="#" class="store-item">   <figure class="ch-thumb">    <img style="display:none" src="{img1}" alt="">   </figure>   <figure class="store-thumb">    <img  style="display:none" src="{img2}">   </figure>   <span class="ch-title">{chName}</span>  </a> </li>'
	this.refresh = function(data, meta){
		if(!meta){
			meta = {};
			meta.total = 1;
			meta.id = "search";
		}
		categoryInfo = data;
		totalPages = Math.floor(parseInt(meta.total,10)/itemCount);
		itemTotal = meta.total;
		totalRows = Math.floor(data.length/3);
		categoryId = meta.id;

		this.page = 0;
		this.currentIndex = -1;
		this.length = data.length;
		
		var li;
		$(".store-item-list li").remove();
		for(var i = 0; i<data.length; i++){
			li = tmp.replace("{img1}", data[i].images[0]);
			if(data[i].images.length > 2){
				li = li.replace("{img2}", data[i].images[1]);
			}
			li = li.replace("{chName}", data[i].name);
			$(".store-item-list").append(li);
			$(".store-item-list li").eq(i).find("img").bind("load", function(){
				$(this).fadeIn();
			});
		}
		addEvents();
		hideLoading();
		this.update();
	}

	this.appendPage = function(data, meta){

		categoryInfo = categoryInfo.concat(data);
		totalRows += Math.floor(data.length/3);
		this.length += data.length;
		
		var start = $(".store-item-list li").length;
		var li;
		for(var i = 0; i<data.length; i++){
			li = tmp.replace("{img1}", data[i].images[0]);
			if(data[i].images.length > 2){
				li = li.replace("{img2}", data[i].images[1]);
			}
			li = li.replace("{chName}", data[i].name);
			$(".store-item-list").append(li);
			$(".store-item-list li").eq(start + i).find("img").bind("load", function(){
				$(this).fadeIn();
			});
		}
		addEvents();
	}
	
	this.search = function(){
		
		var key = $(".store-search input").val();
		key = key.trim();
		
		if(key.length>1){
			showLoading();
			$.get("/playerAPI/search?text="+key+"&v=40" + "&mso=" + mso, function(data){
				var result = getSearchCategoryInfo(data);
				self.refresh(result);
				$(searchInput).blur();
			});
		}
	}
	//$(".store-item-list li").remove();
	
	var timeout;
	var self = this;
	
	function addEvents(){
		
		$(sel + " .store-item").unbind();
		$(sel + " .store-item").click(function(e){
			self.currentIndex = $(sel).index($(e.currentTarget).parent());
			self.update();
			self.select();
			return false;
		});
		
		$(".store-search input").unbind();
		$(".store-search input").keydown(function(e){
			var code = e.keyCode;
			if(code == 13){
				self.search();
				return false;
			}
		});
	}
	
	onConstructed();
}



StorePreview.prototype = new Controllable("StorePreview");
StorePreview.prototype.constructor = StorePreview;
function StorePreview(){
	
	var sel = ".store-item-preview";
	var overlay = $(".overlay-bg");
	var video = $(sel).find(".video-layer object");
	var self = this;
	var pid = "store-preview-player";
	var player;
	var yIndex = 0, xIndex = 1;
	var followBtn = $(sel).find(".follow-button");
	var isFollowed = false;
	var btn = {
		"share" : $(sel).find(".mail-sharing-btn"),
		"fb" : $(sel).find(".facebook-button"),
		"follow" : $(sel).find("a.follow-button"),
		"close" : $(sel + " .overlay-button-wrap .black-button")
	}
	var input = $(sel).find(".sharing-wrap input").eq(0);
	var map = [
		[btn.close, btn.follow],
		[input],
		[btn.fb, btn.share]
	]
	video.remove();
	this.channel;
	function onConstructed(){
		
		$(sel).find(".video-layer").append("<div id='store-preview-player'/>");
		for(var i in btn){
			$(btn[i]).removeClass("on");
		}
		$(btn[0]).addClass("on");
		
		btn.fb.click(function(){
			self.fb();
			return false;
		});
		
		btn.share.click(function(){
			self.share();
			return false;
		});
		
		btn.close.click(function(){
			self.close();
			return false;
		});
		
		btn.follow.click(function(){
			self.subscribe();
			return false;
		});
	}


	function clear(){
		$(map).each(function(i,e){
			$(e).each(function(){
				$(this).removeClass("on");
			});
		})
	}
	
	this.selectLeft = function(){
		if(yIndex != 1){
			clear();
			xIndex = 0;
			map[yIndex][xIndex].addClass("on");
		}
		return false;
	}
	this.selectRight = function(){
		
		if(yIndex != 1){
			clear();
			xIndex = 1;
			map[yIndex][xIndex].addClass("on");
		}
		return false;
	}
	this.selectUp = function(){
		yIndex++;
		if(yIndex > 2) yIndex = 2;

		clear();
		if(yIndex == 1){
			input.addClass("on");
			input.each(function(){
				this.select();
			});
		}else{
			input.blur();
			xIndex = 0;
			map[yIndex][xIndex].addClass("on");
		}
	}
	this.selectDown = function(){
		yIndex--;
		if(yIndex <= 0) yIndex = 0;

		clear();
		if(yIndex == 1){
			input.addClass("on");
			input.each(function(){
				this.select();
			});
		}else{
			input.blur();
			xIndex = 0;
			map[yIndex][xIndex].addClass("on");
		}
	}
	this.close = function(){
		
		if(player){
			player.pause();
		}
		
		$(this).trigger("controllOut", ["StoreGrid"]);
		$(document).trigger("hidePopup");
		$(sel).find(".bottom").find("a").removeClass("on");
		$("#" + pid).remove();
	}
	this.setChannel = function(channel){
		
		this.channel = channel;
		isFollowed = guideChannels.byId(this.channel.id) !== false;
		
		$(".store-item-preview .ch-thumb img").attr("src", channel.images[0]);
		$(".store-item-preview .ch-title").html(channel.name);
		$(".store-item-preview .ch-desc").html(channel.curatorDescription);
		$(".store-item-preview .sharing-wrap input").val("www.9x9.tv/view?ch="+channel.id);
		

		var str = lang == "zh" ? "訂閱" : "Follow";
		var str1 = lang == "zh" ? "取消訂閱" : "Unfollow";
		if(!isFollowed){
			followBtn.html('<i class="icon-follow"></i>' + str);
		}else{
			followBtn.html('<i class="icon-follow"></i>' + str1);
		}
		followBtn.addClass("on");
	}
	this.select = function(){
		
		if(yIndex == 0){
			if(xIndex == 0){
				
				this.close();
				
			}else if(xIndex == 1){
				
				this.subscribe();
			}
			
		}else if(yIndex == 2){
			if(xIndex == 0){
				//facebook
				btn.fb.click();
			}else{
				//email
				btn.share.click();
			}
		}
	}
	this.fb = function(){

		facebookShareChannel(this.channel, function(){
			currentComponent = store;
		});
	}
	this.share = function(){

		//Email Sharing Button
		self.close();

		setTimeout(function(){
			emailSharing.show(function(){
				currentComponent = store;
			},{
				userToken : userToken
			});
			currentComponent = emailSharing;
		}, 500);

		//On Email Sharing Sumbmit
		$(popEvents).one("emailSubmit", function(e, obj){

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
						dialog("custom", msg, store);
					}else if(code == 110){
						var msg = lang == "zh" ? "驗證碼錯誤" : "Captcha does not match";
						dialog("custom", msg, store);
					}else{
						dialog("error", store);
					}
				},
				error : function(res){
					console.warn(res);
					dialog("error", store);
				}
			});
			//currentComponent = subscribePreview;
			currentComponent = store;
		});
	}
	this.subscribe = function(){

		if(userToken){

			if(isFollowed){
				
				this.close();
				
				//$(".overlay-container").css("top", ($(window).height() - $(".overlay-container").height())/2);
				setTimeout(function(){
					
					overlayNotice.setNotice('confirmUnfollow');
					overlayNotice.show(function(){
						currentComponent = stroe;
					});
					currentComponent = overlayNotice;
					
					//do unfollow
					$(popEvents).unbind();
					$(popEvents).bind("confirmUnfollow", function(e, ans){
						if(ans){
							$.get("/playerAPI/unsubscribe?user=" + userToken + "&channel=" + self.channel.id + "&grid=" + self.channel.grid + "&mso=" + mso, function(res){
								var code = res.split("\t")[0];
								if(code == "0"){
									//取消訂閱成功
									overlayNotice.setNotice('unfollowed');
									overlayNotice.show(function(){
										currentComponent = store;
									});
									currentComponent = overlayNotice;
									
									location.reload(); 
									
								}else{
									//取消訂閱失敗
									overlayNotice.setNotice('error');
									overlayNotice.show(function(){
										currentComponent = store;
									});
									currentComponent = overlayNotice;
								}
							});
						}else{
							overlayNotice.hide();
							currentComponent = store;
						}
					});
					
				},400);
				
			}else{
				
				var grid = getEmptyGridId();
				if(grid){
					this.close();
					$.get("/playerAPI/subscribe?user=" + userToken + "&channel=" + this.channel.id + "&grid=" + grid + "&mso=" + mso, function(res){
						var code = res.split("\t")[0];
						$(sel).hide();
						
						if(code == "0"){
							
							//訂閱成功
							overlayNotice.setNotice('followed');
							var pos = parseGridPos(grid);
							$(".notice-desc .guide-position").html(pos.join("-"));
							overlayNotice.show(function(){
								currentComponent = store;
							});
							currentComponent = overlayNotice;
							
							location.reload();
							
						}else{
							overlayNotice.setNotice('error');
							overlayNotice.show(function(){
								currentComponent = store;
							});
							currentComponent = overlayNotice;
						}
					});
				}else{
					alert("Your guide is full.");
				}
			}
		}
	}
	
	this.init = function(){
		
		xIndex = 1;
		yIndex = 0;
	}
	
	this.loadVideo = function(){
		
		var vid;
		if(this.channel.lastEpisode){
			vid = this.channel.lastEpisode.url1[0].split("v=")[1];
			fn();
		}else{
			
			this.channel.load();
			loader.moveToTop(this.channel.request.id);
			
			$(this.channel).bind("ready", function(){
				if(this.episodes.length > 0){
					if(this.nature == 6){
						vid = this.episodes[0].programs[0].videoId;
					}else{
						vid = this.episodes[0].data.videoId;
					}
					fn();
				}else{
					alert("Sorry, there are currently no episodes in this channel!");
				}
			});
		}
		
		function fn(){
			$(sel).find(".video-layer").append("<div id='store-preview-player'/>");
			player = new YTPlayer(pid);
			$(player).bind("onPlayerReady", function(){
				player.load(vid);
				player.play();
			});
		}
	}
	
	onConstructed();
}














