Guide.prototype = new Controllable("Guide");
Guide.prototype.constructor = Guide;
function Guide(){
	
	var GUIDEMODE = {
		"THREEBYTHREE" : 1,
		"NINEBYNONE" : 2
	}
	var currentGrid;
	var grid;
	var self = this;
	var rightObj = {
		"display" : "block",
		"width" : "24.4rem",
		"height" : "21rem",
		"left" : "20rem",
		"top" : "4rem",
		"z-index" : 10
	}
	var leftObj = {
		"display" : "block",
		"width" : "24.4rem",
		"height" : "21rem",
		"left" : 0,
		"top" : "4rem",
		"z-index" : 10
	}
	var onObj = {
		"display" : "block",
		"width" : "30.8rem",
		"height" : "26.5rem",
		"left" : "6.5rem",
		"top" : 0,
		"z-index" : 20
	}
	var chThumbs = ".ch-thumb";
	var guideThumbs = ".guide-thumb";
	var chTitles = ".ch-title";
	var layer = "#guide-layer";
	var guideSel = layer + " #guide-group-wrap";
	var grids = guideSel + " .guide-group-list";
	var gridTp;
	
	this.mode = GUIDEMODE.THREEBYTHREE;
	this.currentIndex = 0;
	this.leftIndex = 0;
	this.rightIndex = 2;
	this.grids = Array();
	this.dataLoaded = false;
	
	this.selectLeft = function(){
		currentGrid.selectLeft();
	}
	this.selectRight = function(){
		currentGrid.selectRight();
	}
	this.selectUp = function(){
		currentGrid.selectUp();
	}
	this.selectDown = function(){
		currentGrid.selectDown();
	}
	this.select = function(){
		currentGrid.select();
	}
	this.hide = function(){
		$("#guide-layer").hide();
	}
	this.show = function(){
		$("#guide-layer").show();
	}	
	this.update = function(){
		
	}
	this.setMode = function(mode){
		
		$("#guide-mode-wrap a.black-button").removeClass("on");
		if(mode == 1){
			self.mode = 1;
			$("#guide-group-wrap").attr("class", "mode3x3");

			$(".guide-group-list>li").removeClass("left").removeClass("on").removeClass("right");
			
			var o = self.currentIndex;
			var l = self.currentIndex - 1 < 0 ? 8 : self.currentIndex - 1;
			var r = self.currentIndex + 1 == 9 ? 0 : self.currentIndex + 1;

			$(".guide-group-list>li").eq(l).addClass("left").show().animate(leftObj, 0);
			$(".guide-group-list>li").eq(o).addClass("on").show().animate(onObj, 0);
			$(".guide-group-list>li").eq(r).addClass("right").show().animate(rightObj, 0);
			
			$("#guide-mode-wrap a.black-button").eq(0).addClass("on");

		}else if(mode == 2){
			self.mode = 2;
			$("#guide-group-wrap").attr("class", "mode9x9");
			$("#guide-group-wrap .guide-group-list li").removeAttr("style");
			$("#guide-mode-wrap a.black-button").eq(1).addClass("on");
		}
		initDragNDrop();
	}
	this.init = function(){
		
		if(!userToken){
			$.address.value("streaming");
		}
		
		cookie.playback.referer = "guide";
		referer = "streaming";
		updateCookie();
		
		$("#manual-layer").show();
		$("#manual-layer>div").show();
		
		$("#manual-layer>div").eq(4).hide();
		$("#manual-layer>div").eq(5).hide();
		
		cookie = $.cookie(cookieName);
		if(cookie.settings.guideMode == "2"){
			$("#guide-mode-wrap a.black-button").eq(1).click();
		}else{
			$("#guide-mode-wrap a.black-button").eq(0).click();
		}
	}
	this.exit = function(){
		$(layer).hide();
	}
	
	this.f1 = function(){
		
		var channel = currentGrid.channel();
		var title = currentGrid.getGridName();
		var pos = currentGrid.gridIndex + 1;
		console.debug(title);
		
		if(channel != undefined){
			
			guideShortCut.show(function(){
				currentComponent = guide;
			},{
				isFollow : false,
				channel : channel,
				title : title,
				mso : mso
			});
			currentComponent = guideShortCut;
			
			$(popEvents).one("saveTitle", function(e, title){
				$.ajax({
					url : "/playerAPI/setSetInfo?user=" + userToken + "&v=40&" + "&mso=" + mso + rx() + "&pos=" + pos + "&name=" + title,
					success : function(res){
						var code = res.split("\t")[0];
						if(code == 0){
							//success
							var msg = lang == "zh" ? "標題修改成功!" : "Group Name Saved Successfully";
							dialog("custom", msg, guide);

							$(".guide-group-list>li").eq(pos-1).find(".guide-group-title").html(title);
							
						}else{
							dialog("error", guide);
						}
					}
				});
			});
			
			//Email Sharing Button
			$(popEvents).unbind("emailSharing");
			$(popEvents).bind("emailSharing", function(){
				
				subscribePreview.hide();
				emailSharing.show(function(){
					currentComponent = guide;
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
							dialog("custom", msg, guide);
						}else if(code == 110){
							var msg = lang == "zh" ? "驗證碼錯誤" : "Captcha does not match";
							dialog("custom", msg, guide);
						}else{
							dialog("error", guide);
						}
					},
					error : function(res){
						console.warn(res);
						dialog("error", guide);
					}
				});
				//currentComponent = subscribePreview;
				currentComponent = guide;
			});
			
			//FB Sharing Button
			$(popEvents).unbind("fbSharing");
			$(popEvents).bind("fbSharing", function(){
				currentComponent = guide;
				facebookShareChannel(channel, function(){
					currentComponent = guide;
				});
			});
			
			$(popEvents).one("unfollow", function(e){
				
				overlayNotice.setNotice('confirmUnfollow');
				overlayNotice.show(function(){
					currentComponent = guide;
				});
				currentComponent = overlayNotice;
				
				//do unfollow
				$(popEvents).unbind();
				$(popEvents).bind("confirmUnfollow", function(e, ans){
					if(ans){
						var request = new APIRequest({
							url : "/playerAPI/unsubscribe?user=" + userToken + "&channel=" + channel.id + "&grid=" + channel.grid + "&mso=" + mso,
							success : function(res){
								var code = res.split("\t")[0];
								if(code == "0"){
									//取消訂閱成功
									overlayNotice.setNotice('unfollowed');
									overlayNotice.show(function(){
										currentComponent = guide;
									});
									currentComponent = overlayNotice;
									guideChannels.remove(channel.id);

									location.reload();
									
								}else{
									//取消訂閱失敗
									overlayNotice.setNotice('error');
									overlayNotice.show(function(){
										currentComponent = guide;
									});
									currentComponent = overlayNotice;
								}
							} 
						});
						loader.prepend(request);
					}else{
						currentComponent = guide;
						overlayNotice.exit();
					}
				});
				
			});
		}
	}
	
	function createGrid(){

		var item, pos = [];
		
		for(var i = 0; i<guideChannels.length(); i++){
			
			item = guideChannels.eq(i);
			pos = parseGridPos(item.grid);
			guideChannels.eq(i).pos = pos;
			
			var li = $(grids + ">li").eq(pos[0]-1).find("ul>li").eq(pos[1]-1);
			$(li).data("channel", item);
			$(li).attr("id", item.id);
			$(li).addClass("followed");
			
			var gridStuffings = $(".guide-group-list>li").find("li").eq(0).clone().html();
			li.html("");
			li.append(gridStuffings);
			
			li.find(".ch-thumb>div").html("<img src='"+item.images[0]+"' />");
			//li.find(".ch-thumb img").hide();
			
			li.find(".guide-thumb").html("<img src='"+item.images[1]+"' />");

			if(item.images[1] == undefined){
				li.find(".guide-thumb").html("<img src='http://9x9ui.s3.amazonaws.com/tv4.0/img/invalid_img.jpg' />");
			}
			//li.find(".guide-thumb img").hide();
			
			li.find(".ch-title").html(item.name);
			
			li.find(".guide-item-del").click(function(){
				
				var li = $(this).parent().parent();
				var ch = li.attr("id");
				$.ajax({
					url : "/playerAPI/unsubscribe?user=" + userToken + "&channel=" + ch,
					success : function(res){
						var code = res.split("\t")[0];
						if(code == 0){
							li.removeAttr("id").removeAttr("class").html("");
							li.removeData("channel");
							
							overlayNotice.setNotice('unfollowed');
							overlayNotice.show(function(){
								currentComponent = guide;
								$("#overlay-layer").hide();
							});
							currentComponent = overlayNotice;
							guideChannels.remove(ch);
							
						}else{
							
							overlayNotice.setNotice('error');
							overlayNotice.show(function(){
								currentComponent = guide;
							});
							currentComponent = overlayNotice;
						}
					}
				});
				return false;
			});
		}
		
		var cg = 1;
		var g;
		$(grids + ">li").each(function(){
			$(this).find("ul>li").each(function(j,t){

				g = parseGridPos(cg) + "";
				g = g.split(",");
				g = parseInt(((g[0] - 1) * 9),10) + parseInt(g[1],10);
				$(t).data("grid", g);
				cg++;

				if(!$(this).hasClass("followed")){
					$(this).html("");
				}
			});
		});
		
		$(".guide-group-list>li").removeAttr("class");

		if(self.mode == 1){
			$(".guide-group-list>li").eq(8).addClass("left").show().animate(leftObj, 0);
			$(".guide-group-list>li").eq(0).addClass("on middle").show().animate(onObj, 0);
			$(".guide-group-list>li").eq(1).addClass("right").show().animate(rightObj, 0);

		}else{
			$(".guide-group-list>li").removeAttr("on").removeAttr("left").removeAttr("right").removeAttr("style");
		}
		
		initDragNDrop();
	}

	var dragged, draggedContainer, draggedChannel, draggedId, draggedGrid, dropped, droppedId, droppedChannel, droppedGrid, overDiv;
	function initDragNDrop(){

		
		$(".guide-ch-list li").droppable({
			accept: ".guide-channel-item",
			activeClass: "ui-state-hover",
			hoverClass: "ui-state-active",
			over : function(e, ui){
				if(overDiv){
					overDiv.stop().animate({
						"left" : 0,
						"top" : 0
					}, 200);
					overDiv.css("z-index",0);
				}

				overDiv = $(this).find("div").eq(0);

				if(overDiv.length > 0){
					overDiv.stop().animate({
						"left" : -overDiv.parent().offset().left + draggedContainer.offset().left,
						"top" : -overDiv.parent().offset().top + draggedContainer.offset().top
					}, 300);
					overDiv.css("z-index",10);
				}
				
			},
			out : function(e, ui){
				overDiv.css("left", 0).css("top", 0);
			},
			drop: function( e, ui ) {

				if(overDiv){
					overDiv.css("left", 0).css("top", 0);
				}

				var li = this;

				droppedId = $(this).attr("id");

				dropped = $(this).find("div").eq(0).clone();
				droppedChannel = guideChannels.byId(droppedId);
				droppedGrid = $(this).data("grid");

				$(this).html("");
				$(this).append(dragged);

				if(draggedId != undefined){
					$(this).attr("id", draggedId);
				}

				$(dragged).css("left",0).css("top",0);

				//move droped channel to dragged location
				draggedContainer.append(dropped);

				if(droppedId == undefined){

					//moving to an empty grid

					draggedChannel.grid = droppedGrid;

					$(this).data("channel", draggedChannel);

					draggedContainer.removeData("channel");
					draggedContainer.removeClass("followed");
					draggedContainer.removeAttr("id");
					
					$.ajax({
						url : "/playerAPI/moveChannel?user=" + userToken + "&grid1=" + draggedGrid + "&grid2=" + droppedGrid,
						success : function(res){
							
						}
					});


				}else{

					//exchange

					$(draggedContainer).attr("id", droppedId);

					var draggedData = draggedChannel
					var droppedData = $(this).data("channel");

					draggedChannel.grid = $(this).data("grid");
					droppedData.grid = draggedGrid;

					$(this).data("channel", draggedChannel);
					draggedContainer.data("channel", droppedData);

					$.ajax({
						url : "/playerAPI/moveChannel?user=" + userToken + "&grid1=" + draggedGrid + "&grid2=" + droppedGrid,
						success : function(res){
							$.ajax({
								url : "/playerAPI/unsubscribe?user=" + userToken + "&channel=" + droppedId,
								success : function(){
									$.ajax({
										url : "/playerAPI/subscribe?user=" + userToken + "&grid=" + draggedGrid + "&channel=" + droppedId ,
										success : function(res){
											
										}
									});
								}
							});
						}
					});
				}

				return false;
			}
		});

		
		$(".guide-channel-item").draggable({ 
			revert: true,
			stack : ".guide-channel-item",
			start : function(e){
				
				$(this).parent().droppable("destroy");
				$(this).addClass("drag");
				dragged = this;
				draggedContainer = $(this).parent();
				draggedId = $(this).parent().attr("id");
				draggedChannel = guideChannels.byId(draggedId);
				draggedGrid = draggedChannel.grid;
			},
			stop : function(e){
				$(this).removeClass("drag");
				initDragNDrop();
			}
		});

		if(self.mode == 1){
			$(".guide-group-list>li:not(.on)").find("li").droppable().droppable("destroy").droppable( { } );
		}
	}

	function onConstructed(){
		
		$(layer + " .ch-position").html("");
		$(layer + " .ch-title").html("");
		$(layer + " .ep-amount").html("");
		$(layer + " .ch-desc").html("");
		
		$(".guide-group-list>li").removeAttr("style");
		
		if(channelLineupReady){
			fn();
		}else{
			$(document).bind("channelLineupReady", fn);
		}
		
		function fn(){
			
			$(".guide-group-list>li").each(function(i,e){
				grid = new GuideGrid(i, e, self);
				$(grid).bind("controllOut", onControllOut);
				self.grids.push(grid);
				
				// $(e).find(".ch-thumb").remove();
				// $(e).find(".guide-thumb").remove();
				// $(e).find(".ch-title").html("");
			});
			
			createGrid();
			currentGrid = self.grids[self.currentIndex];
			currentGrid.currentIndex = 0;
			currentGrid.update();
			
			$("#guide-mode-wrap a.black-button").click(toggleMode);
			
			cookie = $.cookie(cookieName);
			if(cookie.settings.guideMode == "2"){
				$("#guide-mode-wrap a.black-button").eq(1).click();
			}
		}
	}
	
	function toggleMode(e){
		
		$("#guide-mode-wrap a.black-button").removeClass("on");
		$(e.currentTarget).addClass("on");
		var mode = $("#guide-mode-wrap a.on i").attr("class");
		if(mode == "icon-mode3x3"){
			self.mode = 1;
			$("#guide-group-wrap").attr("class", "mode3x3");

			$(".guide-group-list>li").removeClass("left").removeClass("on").removeClass("right");

			var o = self.currentIndex;
			var l = self.currentIndex - 1 < 0 ? 8 : self.currentIndex - 1;
			var r = self.currentIndex + 1 == 9 ? 0 : self.currentIndex + 1;

			$(".guide-group-list>li").eq(l).addClass("left").show().animate(leftObj, 0);
			$(".guide-group-list>li").eq(o).addClass("on").show().animate(onObj, 0);
			$(".guide-group-list>li").eq(r).addClass("right").show().animate(rightObj, 0);

		}else{
			self.mode = 2;
			$("#guide-group-wrap").attr("class", "mode9x9");
			$("#guide-group-wrap .guide-group-list li").removeAttr("style");

		}
	}
	
	function onControllOut(e, dir, index){
		
		var grid;
		if(self.mode == 1){
			
			if(index%3 == 2){
			
				self.currentIndex++;
				if(self.currentIndex == 9){
					self.currentIndex = 0;
				}
				self.leftIndex = self.currentIndex - 1;
				if(self.leftIndex == -1) self.leftIndex = 8;
				self.rightIndex = self.currentIndex + 1;
				if(self.rightIndex == 9) self.rightIndex = 0;
				
				grid = $(".guide-group-list>li").eq(self.rightIndex);
				grid.css("left", "27rem").css("top", "4rem").css("width", "22rem").css("height", "21rem");
				
				swap();
				
			}else{
				
				self.currentIndex--;
				if(self.currentIndex == -1){
					self.currentIndex = 8;
				}
				self.leftIndex = self.currentIndex - 1;
				if(self.leftIndex == -1) self.leftIndex = 8;
				self.rightIndex = self.currentIndex + 1;
				if(self.rightIndex == 9) self.rightIndex = 0;
				
				grid = $(".guide-group-list>li").eq(self.leftIndex);
				grid.css("left", "-5rem").css("top", "4rem").css("width", "22rem").css("height", "21rem");
				
				swap();
			}
			
			currentGrid = self.grids[self.currentIndex];
			currentGrid.currentIndex = 0;
			currentGrid.update();
			
			//update indicator
			$(".guide-slide-point>ul>li").removeClass("on").eq(self.currentIndex).addClass("on");

			initDragNDrop();
			
		}else{
			
			if(dir == "right"){
				
				self.currentIndex++;
				if(self.currentIndex == 9){
					self.currentIndex = 0;
				}
				currentGrid = self.grids[self.currentIndex];
				currentGrid.currentIndex = index - 2;
				currentGrid.update();
				
			}else if(dir == "left"){
				
				self.currentIndex--;
				if(self.currentIndex == -1){
					self.currentIndex = 8;
				}
				currentGrid = self.grids[self.currentIndex];
				currentGrid.currentIndex = index + 2;
				currentGrid.update();
				
			}else if(dir == "up"){
				
				if(self.currentIndex > 2){
					self.currentIndex -= 3;
				}else{
					self.currentIndex += 6;
				}
				currentGrid = self.grids[self.currentIndex];
				currentGrid.currentIndex = index + 6;
				currentGrid.update();
				
			}else if(dir == "down"){
				
				if(self.currentIndex < 6){
					self.currentIndex += 3;
				}else{
					self.currentIndex -= 6;
				}
				currentGrid = self.grids[self.currentIndex];
				currentGrid.currentIndex = index - 6;
				currentGrid.update();
			}
		}
	}
	
	function swap(){
		
		$(".guide-group-list>li").removeAttr("class").hide();
		
		var grid = $(".guide-group-list>li").eq(self.currentIndex);
		grid.show().css("z-index", 20).addClass("on").stop().animate(onObj, 700);
		
		grid = $(".guide-group-list>li").eq(self.leftIndex);
		grid.show().css("z-index", 10).addClass("left").stop().animate(leftObj, 700);
		
		grid = $(".guide-group-list>li").eq(self.rightIndex);
		grid.show().css("z-index", 10).addClass("right").stop().animate(rightObj, 700);
	}
	
	onConstructed();
}

GuideGrid.prototype = new Controllable("GuideGrid");
GuideGrid.prototype.constructor = GuideGrid;
function GuideGrid(index, element, parent){
	
	this.gridIndex = index;
	this.currentIndex = -1;
	var ns = $("#guide-layer");
	var sel = ".guide-group-list>li";
	var element = $(sel).eq(this.gridIndex);
	var infoLayer = "#guide-ch-info-wrap";
	var self = this;
	
	function updateInfo(channel){
		if(!channel){
			var pos = (self.gridIndex + 1) + "-" + (self.currentIndex+1);
			$(infoLayer + " .ch-position").html("Channel " + pos);
			$(infoLayer + " .ch-title").html("");
			$(infoLayer + " .ep-amount").html("");
			$(infoLayer + " .ch-desc").html("");
		}else{
			channel.pos = parseGridPos(channel.grid);
			var pos = (channel.pos[0]) + "-" + (channel.pos[1]);
			$(infoLayer + " .ch-position").html("Channel " + pos);
			$(infoLayer + " .ch-title").html(channel.name);
			$(infoLayer + " .ep-amount").html("Episodes : " + channel.programCount);
			$(infoLayer + " .ch-desc").html(channel.description);
		}
	}

	this.getGridName = function(){
		return $(element).find(" .guide-group-title").html();
	}
	
	this.selectLeft = function(){
		if(this.currentIndex == -1) return;
		if(this.currentIndex%3 == 0){
			$(this).trigger("controllOut", ["left", this.currentIndex]);
			this.currentIndex = -1;
			element.find("li").removeClass("on");
			return;
		}
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = 0;
		}
		this.update();
	}
	
	this.selectRight = function(){
		if(this.currentIndex == -1) return;
		if(this.currentIndex%3 == 2){
			$(this).trigger("controllOut", ["right", this.currentIndex]);
			this.currentIndex = -1;
			element.find("li").removeClass("on");
			return;
		}
		this.currentIndex++;
		if(this.currentIndex >= 8){
			this.currentIndex = 8
		}
		this.update();
	}
	
	this.selectUp = function(){
		if(this.currentIndex == -1) return;
		if(this.currentIndex > 2){
			this.currentIndex -= 3;
		}else{
			if(parent.mode == 2){
				$(this).trigger("controllOut", ["up", this.currentIndex]);
				this.currentIndex = -1;
				element.find("li").removeClass("on");
				return;
			}
		}
		this.update()
	}
	
	this.selectDown = function(){
		if(this.currentIndex == -1) return;
		if(this.currentIndex < 6){
			this.currentIndex += 3;
		}else{
			if(parent.mode == 2){
				$(this).trigger("controllOut", ["down", this.currentIndex]);
				this.currentIndex = -1;
				element.find("li").removeClass("on");
				return;
			}
		}
		this.update();
	}
	
	this.select = function(){
		var channel = element.find("li").eq(this.currentIndex).data("channel");
		if(channel){
			if(channel.id){
				referer = "guide";
				$.address.value("playback/" + channel.id);
			}
		}
		if(this.currentIndex == -1) return;
	}
	
	this.channel = function(){
		return element.find("li").eq(this.currentIndex).data("channel");
	}
	
	this.update = function(){
		
		element.find("li").removeClass("on");
		if(this.currentIndex != -1){
			element.find("li").eq(this.currentIndex).addClass("on");
		}
		var channel = element.find("li").eq(this.currentIndex).data("channel");
		updateInfo(channel);
	}
	
	this.hide = function(){
		$(element).hide();
	}
	
	this.show = function(){
		$(element).show();
		this.init();
	}
	
	this.init = function(){
	}
	
	$(element).find("li").removeClass("on");
	$(sel).eq(this.gridIndex).find("li").bind("click", function(e){
		self.currentIndex = $(sel + " li").index($(e.currentTarget));
		self.select();
		return false;
	});
}

