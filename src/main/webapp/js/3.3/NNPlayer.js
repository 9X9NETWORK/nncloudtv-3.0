
// -1 (unstarted)
// 0 (ended)
// 1 (playing)
// 2 (paused)
// 3 (buffering)
// 5 (video cued).

function onPlayerStateChange0(s){onPlayerStateChange(0,s)}
function onPlayerStateChange1(s){onPlayerStateChange(1,s)}
function onPlayerStateChange2(s){onPlayerStateChange(2,s)}
function onPlayerStateChange3(s){onPlayerStateChange(3,s)}

function onPlayerStateChange(i, state){
	
	if(!nnPlayer) return;
	
	if(thumbing != "fullscreen") return;
	
	var player = $(".yt").eq(i).getPlayer();
	if(state == -1){
		if(!nnPlayer.onVideoStopped) return;
		nnPlayer.onVideoStopped(player);
	}
	if(state == 0){
		if(!nnPlayer.onVideoEnd) return;
		nnPlayer.onVideoEnd(player);
	}else if(state == 1){
		if(!nnPlayer.onVideoStart) return;
		nnPlayer.onVideoStart(player);
	}else if(state == 2){
		//nnPlayer.onVideoPaused(player);
	}
}

function pauseVideo(player){
	if(player){
		if(player.pauseVideo){
			player.pauseVideo();
		}
	}
}

function loadVideoById(program, player){
	if(!program) return false;
	if(!player) return false;
	if(!player.loadVideoById) return false;
	
	player.loadVideoById({
		"videoId" : program.videoId,
		"startSeconds" : program.startSeconds,
		"endSeconds" : program.endSeconds
	});
}

function mutePlayer(player){
	if(player){
		if(player.mute){
			player.mute();
		}
	}
}


function stopVideo(player){
	if(player){
		if(player.stopVideo){
			player.stopVideo();
		}
	}
	//console.debug("stop video failed");
}

function playVideo(player){
	if(player){
		if(player.playVideo){
			player.playVideo();
		}
	}
	//console.debug("play video failed");
}

function addEventListener(p, e, fn){
	if(p){
		if(p.addEventListener){
			p.addEventListener(e, fn);
			return;
		}else{
			//console.debug("player is not ready");
		}
	}
	//console.debug("add listener failed");
}

function getPlayerState(player){
	if(player){
		if(player.getPlayerState){
			return player.getPlayerState();
		}
	}
	return false;
}

function getVideoUrl(player){
	if(player){
		if(player.getVideoUrl){
			return player.getVideoUrl();
		}
	}
	return false;
}

function seekTo(player, sec){
	if(player){
		if(player.seekTo){
			player.seekTo(sec);
		}
	}
}

function setVolume(player, vol){
	if(player){
		if(player.setVolume){
			player.setVolume(vol);
		}
	}
}

function getCurrentTime(player){
	if(player){
		if(player.getCurrentTime){
			return player.getCurrentTime();
		}
	}
	return 0;
}

function NNPlayer(){
	
	var interval;
	var timeout;
	var layers = $(".yt");
	var self = this;
	
	this.volume = 100;
	this.currentPlayer;
	this.nextPlayer;
	this.prevChPlayer;
	this.nextChPlayer;
	this.isPlaying = false;
	this.isAtLast = false;
	this.isPlayerTitleCard = false;
	this.isPlayingEndTitleCard = false;
	this.titleCard = new NNTitleCard();
	
	this.onVideoStart = function(){};
	this.init = function(channel){
		
		$.prototype.getPlayer = function(){
			return document.getElementById($(this).find("object").attr("id"));
		}
		
		// try{
			// layers.eq(0).getPlayer().stopVideo();
			// layers.eq(1).getPlayer().stopVideo();
			// layers.eq(2).getPlayer().stopVideo();
			// layers.eq(3).getPlayer().stopVideo();
		// }catch(e){}
		
		this.channel = channel;
		this.initPlayers();
	}
	
	this.destroy = function(){
		
	}
	
	this.initPlayers = function(){
		
		layers.each(function(i,e){
			var player = $(e).getPlayer();
			//addEventListener(player, 'onStateChange', 'onPlayerStateChange'+i);
		});
	}
	
	this.load = function(currentProgram, nextProgram, prevChProgram, nextChProgram){
		
		this.isAtLast = false;
		$("#titlecard-layer").titlecard("cancel");
		layers.find("object").removeAttr("class");
		
		stopVideo(layers.eq(0).css("z-index", 27).find("object").addClass("current").parent().getPlayer());
		stopVideo(layers.eq(1).css("z-index", 25).find("object").addClass("next").parent().getPlayer());
		stopVideo(layers.eq(2).css("z-index", 25).find("object").addClass("prev_channel").parent().getPlayer());
		stopVideo(layers.eq(3).css("z-index", 25).find("object").addClass("next_channel").parent().getPlayer());
		
		clearTimeout(timeout);
		timeout = setTimeout(function(){
			
			var player = layers.eq(0).getPlayer();
			addEventListener(player, 'onStateChange', 'onPlayerStateChange0');
			//player.addEventListener('onStateChange', 'onPlayerStateChange0');
			
			player = layers.eq(1).getPlayer();
			addEventListener(player, 'onStateChange', 'onPlayerStateChange1');
			//player.addEventListener('onStateChange', 'onPlayerStateChange1');
			
			player = layers.eq(2).getPlayer();
			addEventListener(player, 'onStateChange', 'onPlayerStateChange2');
			//player.addEventListener('onStateChange', 'onPlayerStateChange2');
			
			player = layers.eq(3).getPlayer();
			addEventListener(player, 'onStateChange', 'onPlayerStateChange3');
			//player.addEventListener('onStateChange', 'onPlayerStateChange3');
			
			player = layers.eq(1).getPlayer();
			loadVideoById(nextProgram, player);
			pauseVideo(player);
			layers.eq(1).data("program", nextProgram);
			
			player = layers.eq(2).getPlayer();
			loadVideoById(prevChProgram, player);
			pauseVideo(player);
			layers.eq(2).data("program", prevChProgram);
			
			player = layers.eq(3).getPlayer();
			loadVideoById(nextChProgram, player);
			pauseVideo(player);
			layers.eq(3).data("program", nextChProgram);
			
			//first one last
			player = layers.eq(0).getPlayer(); 
			loadVideoById(currentProgram, player);
			layers.eq(0).data("program", currentProgram);
			self.playProgram(player);
			
		},500);
	}
	
	this.currentTime = function(){
		return this.player().getCurrentTime();
	}
	
	this.skipTitleCard = function(){
		
		if(!this.isPlayingTitleCard){
			return;
		}
		this.titleCard.cancel();
		this.isPlayingTitleCard = false;
		
		if(this.isPlayingEndTitleCard){
			$(self).trigger("programEnd");
			$(document).trigger("endTitleCardEnd");
		}else{
			$(document).trigger("beginTitleCardEnd");
			var player = $(".current").parent().getPlayer();
			player.playVideo();
		}
		this.isPlayingEndTitleCard = false;
	}
	
	this.playProgram = function(player){
		
		var program = $(player).parent().data("program");
		if(program == false || program == undefined){
			$(document).trigger("noProgramFound");
			return;
		}
		
		var card = program.titleCards["begin"];
		var self = this;
		var onEndTitleCardEnds = function(){
			self.isPlayingTitleCard = false;
			$(document).trigger("endTitleCardEnd");
			$(document).trigger("programEnd");
		}
		var playProgramVideo = function(){
			
			self.isPlayingTitleCard = false;
			$(document).trigger("beginTitleCardEnd");
			
			/*
			 * get current time to know if the player is already playing
			 * if the player is already playing, it means we need to reload the
			 * player for the "prev" action
			 */
			var time = getCurrentTime(player);
			if(time > 0){
				
				stopVideo(player);
				setVolume(player, self.volume);
				var rs = loadVideoById(program, player);
				self.onVideoStart = function(){
					$(document).trigger("videoStart",[program]);
					self.isPlaying = true;
					self.onVideoStart = function(){}
				}
			}else{
				/**
				 * if the player is not already playing, 
				 * play the paused player 
				 */
				playVideo(player);
				setVolume(player, self.volume);
				self.isPlaying = true;
				$(document).trigger("videoStart",[program]);
			}
			
			self.onVideoEnd = function(player){
				
				stopVideo(player);
				self.isPlaying = false;
				card = program.titleCards["end"];
				
				if(card !== false){
					self.isPlayingTitleCard = true;
					self.isPlayingEndTitleCard = true;
					this.titleCard.play(card, onEndTitleCardEnds);
					$(document).trigger("endTitleCardStart");
				}else{
					$(document).trigger("programEnd");
				}
			}
		}
		
		if(card != false){
			self.titleCard.play(card, playProgramVideo);
			self.isPlayingTitleCard = true;
			self.isPlaying = false;
			$(document).trigger("beginTitleCardStart",[program]);
		}else{
			self.isPlayingTitleCard = false;
			playProgramVideo();
		}
	}
	
	this.toggle = function(){
		
		if(this.player() == null) return;
		
		if(this.isPlaying){
			pauseVideo(this.player());
			this.isPlaying = false;
		}else{
			if(this.isPlayingTitleCard){
				
				self.titleCard.cancel();
				this.player().pauseVideo();
				this.isPlaying = false;
			
			}else{
				playVideo(this.player());
				this.isPlaying = true;
			}
		}
	}
	
	this.nextChannel = function(nextProgram, prevChProgram, nextChProgram){
		
		this.isAtLast = false;
		this.titleCard.cancel();
		
		var out_player = document.getElementById($("object.current").attr("id"));
		var in_player = document.getElementById($("object.next_channel").attr("id"));
		var player;
		
		stopVideo(out_player);
		this.playProgram(in_player);
		$(".yt object").attr("class", "");
		$(in_player).attr("class", "current").parent().css("z-index", 27);
		
		//preload next program
		player = document.getElementById($(".yt object:not(.current)").eq(0).attr("id"));
		loadVideoById(nextProgram, player);
		pauseVideo(player);
		
		$(player).parent().data("program", nextProgram);
		$(player).attr("class", "next").parent().css("z-index", 25);
		
		//preload next channel program
		player = document.getElementById($(".yt object:not(.current)").eq(1).attr("id"));
		loadVideoById(prevChProgram, player);
		pauseVideo(player);
		$(player).parent().data("program", prevChProgram);
		$(player).attr("class", "prev_channel").parent().css("z-index", 25);
		
		//preload prev channel program
		player = document.getElementById($(".yt object:not(.current)").eq(2).attr("id"));
		loadVideoById(nextChProgram, player);
		pauseVideo(player);
		$(player).parent().data("program", nextChProgram);
		$(player).attr("class", "next_channel").parent().css("z-index", 25);
	}
	
	this.prevChannel = function(nextProgram, prevChProgram, nextChProgram){
		
		this.isAtLast = false;
		this.titleCard.cancel();
		
		var out_player = document.getElementById($("object.current").attr("id"));
		var in_player = document.getElementById($("object.prev_channel").attr("id"));
		var player;
		
		stopVideo(out_player);
		
		this.playProgram(in_player);
		
		$(".yt object").attr("class", "");
		$(in_player).attr("class", "current").parent().css("z-index", 27);
		
		player = document.getElementById($(".yt object:not(.current)").eq(0).attr("id"));
		loadVideoById(nextProgram, player);
		pauseVideo(player);
		$(player).parent().data("program", nextProgram);
		$(player).attr("class", "next").parent().css("z-index", 25);
		
		player = document.getElementById($(".yt object:not(.current)").eq(1).attr("id"));
		loadVideoById(prevChProgram, player);
		pauseVideo(player);
		$(player).parent().data("program", prevChProgram);
		$(player).attr("class", "prev_channel").parent().css("z-index", 25);
		
		player = document.getElementById($(".yt object:not(.current)").eq(2).attr("id"));
		loadVideoById(nextChProgram, player);
		pauseVideo(player);
		$(player).parent().data("program", nextChProgram);
		$(player).attr("class", "next_channel").parent().css("z-index", 25);
	}
	
	this.nextProgram = function(nextProgram){
		
		this.titleCard.cancel();
		
		var out_player = document.getElementById($("object.current").attr("id"));
		var in_player = document.getElementById($("object.next").attr("id"));
		var program;
		
		loadVideoById(nextProgram, out_player);
		$(out_player).parent().data("program", nextProgram);
		pauseVideo(out_player);
		$(out_player).attr("class", "next").parent().css("z-index", 25);
		
		
		this.playProgram(in_player);
		$(in_player).attr("class", "current").parent().css("z-index", 27);
	}
	
	this.prevProgram = function(program, nextProgram, startSecond){
		
		if(!startSecond){
			startSecond = 0;
		}
		
		this.isAtLast = false;
		this.titleCard.cancel();
		
		var player = document.getElementById($("object.current").attr("id"));
		$(player).parent().data("program", program);
		this.playProgram(player);
		if(startSecond != 0){
			seekTo(player, startSecond);
		}
		
		player = document.getElementById($("object.next").attr("id"));
		if(!player){
			player = $(".yt object:not(.current, .next_ch, .prev_ch)").eq(0).parent().getPlayer();
			$(player).addClass("next");
		}
		
		loadVideoById(nextProgram, player);
		$(player).parent().data("program", nextProgram);
		
		pauseVideo(player);
	}
	
	this.lastProgram = function(){
		
		this.titleCard.cancel();
		
		var player = document.getElementById($("object.current").attr("id"));
		stopVideo(player);
		$(player).attr("class", "").parent().css("z-index", 25);
		
		player = document.getElementById($("object.next").attr("id"));
		this.playProgram(player);
		
		$(player).attr("class", "current").parent().css("z-index", 27);
		this.isAtLast = true;
	}
	
	this.playVideo = function(){
		
	}
	
	this.pauseVideo = function(){
		this.player().stopVideo();
	}
	
	this.stopVideo = function(){
		this.player().stopVideo();
	}
	
	this.seekTo = function(seconds){
		
	}
	
	this.progress = function(){
		
	}
	
	this.player = function(){
		return $("object.current").parent().getPlayer();
	}
	
	this.volumeUp = function(){
		this.volume += 10;
		this.volume = Math.min(this.volume, 100);
		this.player().setVolume(this.volume);
		this.updateVolumeBar();
	}
	
	this.volumeDown = function(){
		this.volume -= 10;
		this.volume = Math.max(this.volume, 0);
		this.player().setVolume(this.volume);
		this.updateVolumeBar();
	}
	
	this.updateVolumeBar = function(){
		var v = Math.floor(this.volume/10);
		$("#volume-bars li").removeClass("on");
		$("#volume-bars li:lt("+v+")").addClass("on");
	}
	
	this.testMode = function(){
		
		clearInterval(interval);
		interval = setInterval(function(){
			$("#ym0").hide();
			$("#ym1").css("width","50%").css("height", "50%").css("z-index", 27);
			$("#ym7").css("width","50%").css("height", "50%").css("right",0).css("left", "").css("top", 0).css("z-index", 27);
			$("#ym3").css("width","50%").css("height", "50%").css("left", 0).css("bottom", 0).css("top", "").css("z-index", 27);
			$("#ym2").css("width","50%").css("height", "50%").css("right", 0).css("left", "").css("bottom", 0).css("top", "").css("z-index", 27);
		}, 100);
		setTimeout(function(){
			clearInterval(interval);
		}, 1000);
	}
	
	this.standardMode = function(){
		$("#ym1").css("right", "").css("bottom", "").css("left", 0).css("top", 0);
		$("#ym7").css("z-index", 25).css("right", "").css("bottom", "").css("left", 0).css("top", 0);
		$("#ym3").css("z-index", 25).css("right", "").css("bottom", "").css("left", 0).css("top", 0);
		$("#ym2").css("z-index", 25).css("right", "").css("bottom", "").css("left", 0).css("top", 0);
	}
	
	this.exit = function(){
		
		stopVideo(layers.eq(0).getPlayer());
		stopVideo(layers.eq(1).getPlayer());
		stopVideo(layers.eq(2).getPlayer());
		stopVideo(layers.eq(3).getPlayer());
		$("#player-layer").hide();
	}
	
	this.init();
}

function NNTitleCard(){
	
	var self = this;
	this.isPlaying = false;
	this.play = function(card, callback){
		this.isPlaying = true;
		$("#titlecard-layer").titlecard({
			"text": card["message"],
			"align": card["align"],
			"effect": card["effect"],
			"duration": card["duration"],
			"fontSize": card["size"],
			"fontColor": card["color"],
			"fontStyle": card["style"],
			"fontWeight": card["weight"],
			"backgroundColor": card["bgcolor"]
		}, function(){
			this.isPlaying = false;
			$(this).trigger("end");
			callback();
		});
	}	
	
	this.cancel = function(){
		$(this).trigger("end");
		$("#titlecard-layer").titlecard("cancel");
	}
}

function NNOSD(channel){
	
	var layer = $("#osd-layer");
	var timeout;
	var self = this;
	
	this.channel;
	this.init = function(){
		this.channel = channel;
	}
	this.setChannel = function(channel){
		this.channel = channel;
	}
	this.updateInfo = function(program){
		
		var tmp = this.channel.grid + "";
		var episode = this.channel.currentEpisode();
		tmp = tmp.split("");
		$(".ch-pos").html(tmp[0] + "-" + tmp[1]);
		
		$(".ch-title").html(this.channel.name);
		$("#osd-wrap>img:first-child").attr("src", program.thumb); 
		$(".ep-title").html(episode.name);
		$(".s-ep-title").html(program.name);
		
		$("#osd-wrap h1 .updated").html(timeDifference(new Date(), episode.published));
		
		var span = $("<span>");
		span.addClass("updated");
		span.html(timeDifference(new Date(), this.channel.timestamp));
		$(".s-ep-title .updated").remove();
		$(".s-ep-title").append(span);
	}
	
	this.show = function(){
		this.hide();
		clearTimeout(timeout);
		timeout = setTimeout(function(){
			self.hide();
		}, 5000);
		
		$("#player-holder").stop().animate(
		{
			top: -$("#osd-layer").height()
		}, 500);
		$("#osd-layer").show().stop().animate(
		{
			bottom: "0em"
		}, 500);
		$("#video-ctrl").stop().show();
	}
	
	this.hide = function(){
		$("#player-holder").stop().animate(
		{
			top: "0em"
		}, 500);
		$("#osd-layer").stop().animate(
		{
			bottom: -$("#osd-layer").height() - 20
		}, 500);
	}
	this.init();
}



