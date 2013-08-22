Playback.prototype = new Controllable("Playback");
Playback.prototype.constructor = Playback;
var playbackPlayers = [];
function Playback(){
	
	var layer = "#playback-layer";
	var holder = "#player-holder .video-player";
	var self = this;
	var loadProgramStarted = false;
	var dataLoaded = false;
	var channelId, episodeId;
	var channelData = [];
	var channels, channel, episodes, episode, program;
	var lineup, lineupIndex, lineupItem;
	var playerCounter = 0;
	var players = [], player;
	var channelIndex = 0;
	var episodeIndex = 0;
	var progressBar = new PlayerProgressBar();
	var osd = new PlayerOSD();
	var interval, timeout;
	var manager = new playerManager();
	var isPlayingTitleCard = false;
	var nid, ncid, pcid;
	var poiTimeout;
	var poiTimeout1;
	var poiTimeouts = [];
	var isPlayingPOI = false;
	var isPlaying = false;
	var poi = new POI();
	var poiCount = 0;
	
	var hint = "";
	
	function onConstructed(){
		
		$(layer).css("z-index", 100);
		$(holder).css("background", "black");
		$(".player-holder").find(".video-layer").remove();
		
		//$(self).bind("programEnd", self.onProgramEnd);
		$("body").prepend("<div id='blackOut' style='background:black; z-index:101; width:100%; height:100%; position:absolute; left:0; top:0; display:none' />");
		$("body").prepend("<div id='video-overlay' style='background:none; z-index:102; width:100%; height:100%; position:absolute; left:0; top:0; display:none' />");
		//$("#playback-layer").append("<div id='poi-layer' class='tv-player' style='z-index:105; display:none' />");
	}
	
	function onChannelReady(){
		
		hideLoading();
		channels.unbindReadyAll();
		channels.setIndex(channel.id);
		
		if(channel.episodes.length == 0){
			console.info("episode not found, going to next channel");
			nextChannel();
			return;
		}
		
		if(episodeId == false || episodeId == undefined){
			
			episode = channel.current();
			if(episode == false){
				console.info("episode not found, going to next channel");
				nextChannel();
				return;
			}
			episodeId = episode.id;
			// $.address.value("playback/" + channelId + "/" + episodeId);
			// return;
		}else{
			episode = channel.byId(episodeId);
			
			//some episodes came from "lastEpisode" is not included in the channel episodes
			//therefore we play the current episode instead
			if(episode == false){
				channel.currentIndex = 0;
				episode = channel.current();
			}
			episodeId = episode.id;
		}
		
		preloadChannel();
		startPlay();
	}
	
	//this is the single entry point when address change 
	function startPlay(){
		
		if(!episode){
			console.warn("episode not found");
			$.address.value(referer);
			return;
		}
		
		if(!episode.programs){
			console.warn("program not found");
			$.address.value(referer);
			return;
		}
		
		$("#blackOut").fadeOut().css("z-index", -1);
		
		program = episode.programs[0];
		progressBar.load(episode);
		
		lineup = episode.lineup;
		lineupIndex = 0;
		
		if(manager.isReady){
			runLineUp();
		}else{
			$(manager).one("playersReady", function(){
				runLineUp();
			});
		}
	}
	
	//make sure vars channel, episode, program ready before calling this function
	function playProgram(program, callback){
		
		isPlayingPOI = false;
		
		updateOsd();
		osd.show();
		isPlaying = true;
		
		try{
			manager.currentPlayer().hide();
		}catch(e){}
		
		manager.play(program.videoId);
		
		$(manager).one("videoEnd", function(){
			isPlaying = false;
			callback();
		});
		
		$(manager).one("error", function(){
			isPlaying = false;
			setTimeout(function(){
				self.selectRight();
			},500);
		});
		
		function _try(str){try{return eval(str)}catch(e){return false}}
		
		$(self).trigger("programChanged", [_try("channel.id"), _try("episode.id"), _try("program.videoId"), _try("program.name")]);
	}
	
	function runLineUp(){
		
		if(lineup == undefined){
			console.warn("lineup not found");
			$.address.value(referer);
			return;
		}
		
		if(lineup.length == 0){
			console.warn("lineup not found");
			$.address.value(referer);
			return;
		}
		
		onProgramChange();
		
		lineupItem = lineup[lineupIndex];
		progressBar.setLineup(lineupItem.position);
		var onLineupItemEnd = function(){
			
			lineupIndex++;
			if(lineupIndex == lineup.length){
				
				pauseProgress();
				$("#progress").css("width", "100%");
				setTimeout(nextEpisode, 200);
				return;
				
			}else{
				
				pauseProgress();
				lineupItem = lineup[lineupIndex];
				progressBar.setLineup(lineupItem.position);
				progressBar.update();
				setTimeout(function(){
					runLineUp();
				},200);
			}
		}
		
		if(lineupItem.type == "titleCard"){
			pauseProgress();
			playTitleCard(lineupItem.content, onLineupItemEnd);
			
		}else{
			
			playProgram(lineupItem.content, onLineupItemEnd);
			startProgress();
			
			var _t = function(str){	try{ return eval(str) != undefined;}catch(e){return false;	}}
			
			var poi;
			clearPoiTimeouts();
			if(lineupItem.poi){
				for(var i = 0; i<lineupItem.poi.length; i++){
					poi = lineupItem.poi[i];
					if(_t("poi.start")){
						poiTimeouts[i] = setTimeout(playPOI, parseInt(poi.start,10)*1000, poi);
					}
				}
			}
		}
		
		nid = findNextVideoId();
		if(nid != false){
			manager.preload(nid);
		}
	}
	
	function clearPoiTimeouts(){
		for(var i = 0; i<poiTimeouts.length; i++){
			clearTimeout(poiTimeouts[i]);
		}
	}
	
	function playPOI(p){
		
		var poi = p
		
		isPlayingPOI = true;
		
		var btns = [];
		var actions = [];
		var types = {
			"1" : "hyperChannel",
			"2" : "shoppingInfo",
			"3" : "tvShowNotice",
			"4" : "poll"
		};
		var duration = parseInt(poi.end, 10) - parseInt(poi.start, 10);
		
		if(!poi.content){
			return;
		}
		
		for(var i = 0; i<poi.content.button.length; i++){
			btns.push(poi.content.button[i].text);
			actions.push(poi.content.button[i].actionUrl);
		}
		
		var obj = {
		  "type": types[poi.type],
		  "displayText": poi.content.message,
		  "buttons": btns,
		  "duration": duration,
		  "onSelected" : function(selectedIndex){
		  	//console.debug("select:" + selectedIndex);
		  	// console.debug(actions[selectedIndex]);
		  	// console.debug(selectedIndex);
		  	location.href = actions[selectedIndex];
		  }
		}
		
		$("#playback-layer #poi-layer").remove();
		$("#playback-layer").poi(obj);
		$("#poi-layer").attr("class","tv-player");
		
		$("#player-holder").stop().animate({
			top : "-2.5rem"
		}).delay(duration*1000).animate({
			top : "0"
		});
		
		clearTimeout(poiTimeout1);
		poiTimeout1 = setTimeout(function(){
			isPlayingPOI = false;
		}, duration*1000);
	}
	
	function prevEpisode(){
		
		$("#progress").width(0);
		manager.pause();
		
		var rs = channel.goPrev();
		if(!rs){
			prevChannel();
			return;
		}
		
		episode = channel.current();
		hint = "ep";
		$.address.value("playback/" + channelId + "/" + episode.id);
	}
	
	function nextEpisode(){
		
		$("#progress").width(0);
		manager.pause();
		
		var rs = channel.goNext();
		if(!rs){
			nextChannel();
			return;
		}
		
		episode = channel.current();
		$.address.value("playback/" + channelId + "/" + episode.id);
		
		$(self).trigger("episodeChanged", [_try("channel.id"), _try("episode.id"), _try("episode.name")]);
	}
	
	function preloadChannel(){
	
		var nextCh = channels.next();
		var prevCh = channels.prev();
		var ep;
		var onChReady = function(ch){
			var ep = ch.current();
			if(ep != false){
				var pro = ep.programs[0];
				if(pro){
					var vid = pro.videoId;
					if(vid){
						manager.preload(vid);
					}
				}
			}
		}
		if(!nextCh.isReady){
			nextCh.load();
			loader.moveToTop(nextCh.request.id);
			$(nextCh).one("ready", function(){
				onChReady(nextCh);
			});
		}else{
			onChReady(nextCh);
		}
		
		if(!prevCh.isReady){
			prevCh.load();
			loader.moveToTop(prevCh.request.id);
			$(prevCh).one("ready", function(){
				onChReady(prevCh);
			});
		}else{
			onChReady(prevCh);
		}
	}
	
	function nextChannel(){
		
		$("#progress").width(0);
		manager.pause();
		
		channels.goNext();
		channel = channels.current();
		episode = channel.current();
		
		if(episode == false && channel.id){
			$.address.value("playback/" + channel.id);
			return;
		}
		
		if(episode == undefined || episode == false){
			channelIndex = 0;
			$.address.value(referer);
			console.debug("episode is undefined");
			console.debug(channel);
			return;
		}
		
		$.address.value("playback/" + _try("channel.id") + "/" + _try("episode.id"));
		
		$(self).trigger("channelChanged", [_try("channel.id", _try("channel.name"))]);
		$(self).trigger("episodeChanged", [_try("channel.id"), _try("episode.id", _try("episode.name"))]);
	}
	
	function prevChannel(){
		
		$("#progress").width(0);
		manager.pause();
		
		channels.goPrev();
		channel = channels.current();
		episode = channel.current();
		
		if(episode == false && channel.id){
			$.address.value("playback/" + channel.id);
			return;
		}
		
		if(episode == undefined){
			channelIndex = 0;
			$.address.value(referer);
			return;
		}
		
		$.address.value("playback/" + _try("channel.id") + "/" + _try("episode.id"));
		
		$(self).trigger("channelChanged", [_try("channel.id", _try("channel.name"))]);
		$(self).trigger("episodeChanged", [_try("channel.id"), _try("episode.id", _try("episode.name"))]);
	}
	
	function _try(str, a){
		try{
			return eval(str);
		}catch(e){
			return typeof a == undefined ? false : a;
		}
	}
	
	function playTitleCard(card, callback){
		
		isPlayingPOI = false;
		$("#titlecard-layer").css("z-index", 1000);
		$("#titlecard-layer").titlecard({
			"text": card["message"],
			"align": card["align"],
			"effect": card["effect"],
			"duration": 60,
			"fontSize": card["size"],
			"fontColor": card["color"],
			"fontStyle": card["style"],
			"fontWeight": card["weight"],
			"backgroundColor": card["bgcolor"]
		}, function(){
			$("#titlecard-layer").css("z-index", -1);
			callback();
		});
	}
	
	function startProgress(){
		
		clearInterval(interval);
		interval = setInterval(function(){
			
			var sec = manager.getCurrentTime();
			progressBar.update(sec);
			
		},500);
	}
	
	function pauseProgress(){
		clearInterval(interval);
	}
	
	function updateOsd(){
		
		osd.setChannel(channel);
		osd.setEpisode(episode);
		osd.setProgram(program);
	}
	
	function findNextVideoId(){
		
		var findNextProgramInLineup = function(line, startIndex){
			var n = startIndex;
			while(n <= line.length - 1){
				if(line[n].type == "program"){
					return line[n].content.videoId;
				}
				n++;
			}
			return false;
		}
		var rs = findNextProgramInLineup(lineup, lineupIndex+1);
		if(rs == false){
			rs = findNextProgramInLineup(channel.next().lineup, 0);
			return rs;
		}
		return rs;
	}
	
	function findChannelById(id){
		
		if((!channelLineup) && (!channelData)) return false;
		
		if(referer == "streaming" || referer == ""){
			channels = channelData
		}else if(referer == "guide"){
			channels = channelLineup
		}
		
		for(var i = 0; i<channels.length; i++){
			if(channels[i].id == id){
				channelIndex = i;
				return channels[i];
			}
		}
		return false;
	}
	
	function findChannelIndexById(id){
		
		if((!channelLineup) && (!channelData)) return -1;
		channels = (userToken && referer != "streaming") ? channelLineup : channelData;
		for(var i = 0; i<channels.length; i++){
			if(channels[i].id == id){
				return i;
			}
		}
		return -1;
	}
	
	function onProgramsLoaded(){
		
		$(self).unbind("programsLoaded");
		
		//set channelIndex & channels in findChannelById()
		channel = findChannelById(channelId);
		
		//shouldn't be false unless channelLineup/channelData is not ready
		if(channel == false){
			self.startLoadChannel();
			return;
		}else{
			if(episodeId == false){
				episode = channel.episodes[0];
				episodeId = episode.id;
				$.address.value("playback/" + channelId + "/" + episodeId);
				return;
			}else{
				for(var i = 0; i<channel.episodes.length; i++){
					if(channel.episodes[i].id == episodeId){
						episode = channel.episodes[i];
						episodeId = episode.id;
					}
				}
			}
		}
		startPlay();
	}
	
	function onProgramChange(){
		
		$("#titlecard-layer").titlecard("cancel");
		$("#titlecard-layer").css("z-index", -1);
		manager.pause();
		manager.currentPlayer().videoId = false;
		osd.show();
	}
	
	this.f1 = function(){
		
		if(!userToken) return;
		
		var isFollowed = guideChannels.byId(channel.id) !== false;
		
		currentComponent = playbackSubscribePreview;
		
		playbackSubscribePreview.show(function(){
			currentComponent = playback;
		},{
			isFollow : !isFollowed,
			channel : channel
		});
		
		if(!isFollowed){
			
			$(popEvents).unbind("follow");
			$(popEvents).bind("follow", function(){
				//do follow
				var grid = getEmptyGridId();
				$.get("/playerAPI/subscribe?user=" + userToken + "&channel=" + channel.id + "&grid=" + grid + "&mso=" + mso, function(res){
					var code = res.split("\t")[0];
					if(code == "0"){
						//訂閱成功
						overlayNotice.setNotice('followed');
						var pos = parseGridPos(grid);
						$(".notice-desc .guide-position").html(pos.join("-"));
						overlayNotice.show(function(){
							currentComponent = playback;
						});
						currentComponent = overlayNotice;
					}else{
						//訂閱失敗
						overlayNotice.setNotice('error');
						overlayNotice.show(function(){
							currentComponent = playback;
						});
						currentComponent = overlayNotice;
					}
				});
			});
		}else{
			
			$(popEvents).unbind();
			$(popEvents).bind("unfollow", function(){
				
				overlayNotice.setNotice('confirmUnfollow');
				overlayNotice.show(function(){
					currentComponent = streaming;
				});
				currentComponent = overlayNotice;
				
				//do unfollow
				$(popEvents).unbind();
				$(popEvents).bind("confirmUnfollow", function(e, ans){
					if(ans){
						$.get("/playerAPI/unsubscribe?user=" + userToken + "&channel=" + channel.id + "&grid=" + channel.grid + "&mso=" + mso, function(res){
							var code = res.split("\t")[0];
							if(code == "0"){
								//取消訂閱成功
								overlayNotice.setNotice('unfollowed');
								overlayNotice.show(function(){
									currentComponent = playback;
								});
								currentComponent = overlayNotice;
								
							}else{
								//取消訂閱失敗
								overlayNotice.setNotice('error');
								overlayNotice.show(function(){
									currentComponent = playback;
								});
								currentComponent = overlayNotice;
							}
						});
					}
				});
			});
		}
		
	}
	
	this.selectLeft = function(){
		
		if(isPlayingPOI){
			poi.selectLeft();
			return;
		}
		
		onProgramChange();
		
		lineupIndex--;
		if(lineupIndex == -1){
			lineupIndex = 0;
			prevEpisode();
			return;
		}
		lineupItem = lineup[lineupIndex];
		progressBar.setLineup(lineupItem.position);
		pauseProgress();
		runLineUp();
	}
	
	this.selectRight = function(){
		
		if(isPlayingPOI){
			poi.selectRight();
			return;
		}
		
		onProgramChange();
		
		lineupIndex++;
		if(lineupIndex == lineup.length){
			lineupIndex = 0;
			nextEpisode();
			return;
		}
		lineupItem = lineup[lineupIndex];
		progressBar.setLineup(lineupItem.position);
		pauseProgress();
		runLineUp();
	}
	
	this.select = function(){
		if(isPlayingPOI){
			poi.select();
			return;
		}else{
			if(isPlaying){
				this.pause();
			}else{
				this.play();
			}
		}
	}
	
	this.selectDown = function(){
		$("#titlecard-layer").titlecard("cancel");
		$("#titlecard-layer").css("z-index", -1);
		prevChannel();
	}
	
	this.selectUp = function(){
		$("#titlecard-layer").titlecard("cancel");
		$("#titlecard-layer").css("z-index", -1);
		nextChannel();
	}
	
	this.togglePlay = function(){
		if(isPlaying){
			this.pause();
		}else{
			this.play();
		}
	}
	
	this.play = function(){
		isPlaying = true;
		manager.currentPlayer().play();
		osd.show();
	}
	
	this.pause = function(){
		isPlaying = false;
		manager.pause();
	}
	
	this.init = function(states){
		
		$("#logo-layer").hide();
		
		//if no channel redirect to 
		if((!states) || states.length == 0){
			$.address.value("streaming");
			return;
		}
		
		referer = cookie.playback.referer;
		
		//add a black background
		$("#blackOut").show().css("z-index", "101");
		//$("#video-overlay").show().css("z-index", "101");
		$("#bg-layer").hide();
		//this overlay is used to prevent keyboard control taken over by flash
		$("#manual-layer").hide();
		
		channelId = states[0];
		episodeId = states.length > 1 ? states[1] : false;
		
		loader.resume();
		
		if(referer == "guide"){
			
			channels = guideChannels;
			channel = channels.byId(channelId);
			if(channel === false){
				console.info("channel not found");
				$.address.value(referer);
			}else{
				if(channel.isReady){
					onChannelReady();
				}else{
					
					showLoading();
					
					loader.moveToTop(channel.request.id);
					$(channel).one("ready", onChannelReady);
				}
			}
				
		}else if(referer == "streaming"){
			
			channels = streamingChannels;
			
			if(channels.length() == 0 || channels.byId(channelId) == false){
				
				showLoading();
				
				$.ajax({
					url : "/playerAPI/channelLineup?channel=" + channelId + "&v=40&programInfo=true" + "&mso=" + mso + rx(),
					success : function(res){
						
						channel = new NNChannel(parseChannel(res));
						channels.append(channel);
						channels.setIndex(channelId);
						channel.load();
						loader.moveToTop(channel.request.id);
						
						$(channel).one("ready", function(){
							onChannelReady();
						});
					}
				});
				
			}else{
				
				channel = channels.byId(channelId);
				
				if(channel.isReady){
					onChannelReady();
				}else{
					
					showLoading();
					
					channel.load();
					loader.moveToTop(channel.request.id);
					$(channel).one("ready", onChannelReady);
				}
			}
		}
	}
	
	this.show = function(){
		
		$(layer).css("left", "0");
	}
	
	this.hide = function(){
		
		$(layer).css("left", "2000px");
	}
	
	this.exit = function(){
		
		$("#logo-layer").show();
		
		this.hide();
		manager.pause();
		pauseProgress();
		
		channel = false;
		episode = false;
		program = false;
		
		$("#titlecard-layer").titlecard("cancel");
		$("#blackOut").hide().css("z-index", -1);
		$("#video-overlay").css("z-index", -1);
		$("#bg-layer").show();
		//this should be added back for GA
		//$(self).trigger("exitPlayback");
	}
	
	onConstructed();
}

function playerManager(){
	
	var players = [], player;
	var currentPlayer;
	var counter = 0;
	var holder = "#player-holder .video-layer";
	var self = this;
	var max = 11;
	
	this.isReady = false;
	function onConstructed(){
		
		var readyCount = 0;
		for(var i = 0; i<4; i++){
			player = createPlayer();
			$(player).bind("onPlayerReady", function(e){
				$(player).bind("onPlayerReady");
				this.isReady = true;
				readyCount++;
				if(readyCount == 4){
					$(self).trigger("playersReady");
					self.isReady = true;
				}
			});
			players.push(player);
			player.hide();
		}
	}
	
	function createPlayer(){
		counter++;
		var id = "pbplayer-" + counter;
		var div = $("<div class='ytPlayer' id='"+id+"'/>");
		$(holder).append(div);
		$("#" + id).data("count", counter);
		var p = new YTPlayer(id);
		p.hide();
		return p;
	}
	
	function resetPlayers(){
		for(var i = 0; i<players.length; i++){
			players[i].isCurrentPlayer = false;
			players[i].pause(); 
		}
	}
	
	function findLoadedPlayer(vid){
		for(var i = 0; i<players.length; i++){
			if(players[i].videoId == vid){
				return players[i];
			}
		}
		return false;
	}
	
	function getEmptyPlayer(){
		
		//empty player is player with video id set to false
		for(var i = 0; i<players.length; i++){
			if(players[i].videoId == false){
				return players[i];
			}
		}
		return false;
		//if not found return any player as along as it's not the current player
		for(i = 0; i<players.length; i++){
			if(players[i].isCurrentPlayer == false){
				return players[i];
				break;
			}
		}
		return players[0];
	}
	
	function clearOldestPlayer(){
		var pl;
		var n = 0;
		for(var i = 0; i<players.length-1; i++){
			pl = players[i];
			if(!pl.isCurrentPlayer){
				n++;
				pl.destroy();
				players.splice(i,1);
				if(n == 4){
					break;
				}
			}
		}
		console.debug(players);
	}
	
	this.onPlayersReady = function(){
		players[0].isCurrentPlayer = true;
		self.isReady = true;
	}
	
	this.getCurrentTime = function(){
		if(this.currentPlayer().getCurrentTime){
			return this.currentPlayer().getCurrentTime();
		}
		return 0;
	}
	
	this.play = function(vid){
		
		//find player if is preloaded
		try{
			this.currentPlayer().hide();
		}catch(e){}
		
		this.pause();
		
		player = findLoadedPlayer(vid);
		
		if(!player){
			//not found
			var fn = function(){
				
				player.videoId = vid;
				resetPlayers();
				player.isCurrentPlayer = true;
				
				$(player).unbind();
				player.loadVideoById(vid);
				player.play();
				player.show();
			}
			
			player = getEmptyPlayer();
			if(player == false){
			
				player = createPlayer();
				player.hide();
				$(player).one("onPlayerReady", function(e){
					this.isReady = true;
					fn();
				});
				players.push(player);
				if(players.length > max){
					clearOldestPlayer();
				}
			}else{
				fn();
			}
			
		}else{
			//found
			resetPlayers();
			player.isCurrentPlayer = true;
			
			player.play();
			player.show();
			
			//should release players here if found player is preload channel
			//1. how to know we get here from next/prev channel()
			//2. how to know which players are preloaded channels, when one of which is now current player
		}
		$(player).one("ended", function(){
			$(self).trigger("videoEnd");
		});
		
		$(player).one("error", function(e, code){
			console.info("video error, code:" + code);
			$(self).trigger("error");
		});
	}
	
	this.pause = function(){
		if(this.currentPlayer()){
			this.currentPlayer().pause();
		}
	}
	
	this.preload = function(vid){
		
		for(var i = 0; i<players.length; i++){
			if(players[i].videoId == vid){
				//return;
			}
		}
		
		var fn = function(){
			player.videoId = vid;
			player.isCurrentPlayer = false;
			
			player.loadVideoById(vid);
			player.pause();
			player.hide();
		}
		var player = getEmptyPlayer();
		
		if(player == false){
			
			//console.debug("no empty player");
			if(players.length > max){
				clearOldestPlayer();
			}
			
			player = createPlayer();
			player.hide();
			$(player).one("onPlayerReady", function(e){
				this.isReady = true;
				fn();
			});
			players.push(player);
			
		}else{
			player.hide();
			fn();
		}
	}
	
	this.currentPlayer = function(){
		for(var i = 0; i<players.length; i++){
			if(players[i].isCurrentPlayer){
				return players[i];
			}
		}
		return false;
	}
	onConstructed();
}

function PlayerProgressBar(){
	
	var self = this;
	var interval, timeout, duration, seconds;
	var layer = "#video-ctrl";
	var knob, bar, titleCardPoints, subEpisodePoints;
	var episode, programs, titleCards;
	
	function onDragStop(){
		
		return;
		var percent = $("#knob").offset().left / $("#bar").width();
		self.seconds = percent * self.duration;
		var lineups = self.episode.lineup;
		
		for(var i = lineups.length-1; i>-1; i--){
			lineup = lineups[i];
			if(lineup.position < self.seconds){
				$(document).trigger("knobDrapped", [lineup, i, self.seconds - lineup.position]);
				self.start();
				break;
			}
		}
	}
	
	function onConstructed(){
		self.destroy();
	}
	
	function build(){
		
		if(programs.length == 0 || !programs){
			console.error("progressBar:: no programs");
			console.info(episode);
			return;
		}
		
		//var li = $('<li style="left: 0%;"><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_sub_ep.png"></li>');
		var point;
		var lineups = episode.lineup;
		var lineup, type;
		
		for(var i = 0; i<lineups.length; i++){
			lineup = lineups[i];
			type = lineup.type == "titleCard" ? "titleCard" : "program";
			
			point = $("<li/>").addClass(type).css("left", (lineup.position/duration)*100 + "%");
			
			if(type == "titleCard"){
				point.css("width", (lineup.duration/duration)*100 + "%");
				$("#title-card-points").append(point);
			}else{
				if(i != 0){
					$("#sub-episode-points").append(point);
				}
			}
		}
		
		return;
		$("#bar #knob").draggable({
			axis : "x",
			stop : onDragStop, 
			start : function(){
				clearInterval(interval);
			}
		});
	}
	
	
	this.load = function(ep){
		
		this.destroy();
		
		//console.debug(ep);
		episode = ep
		programs = episode.programs;
		titleCards = episode.titleCards;
		duration = episode.duration;
		this.lineupPosition = 0;
		
		build();
	}
	
	this.setLineup = function(pos){
		this.lineupPosition = pos;
		this.update(0);
	}
	
	this.update = function(sec){
		//console.debug(sec, duration, this.lineupPosition);
		var percent = ((this.lineupPosition + sec)/duration)*100;
		$("#progress").width(percent + "%");
	}
	
	this.destroy = function(){
		
		episode = null;
		programs = [];
		titleCards = [];
		duration = 0;
		this.lineupPosition = 0 
		
		knob = $(layer).find(".knob");
		bar = $(layer).find("#progress");
		titleCardPoints = $(layer).find("#title-card-points li");
		subEpisodePoints = $(layer).find("#sub-episode-points li");
		 
		//knob.css("left", -(knob.width())/2);
		bar.width(0);
		titleCardPoints.remove();
		subEpisodePoints.remove();
	}
	
	onConstructed();
}

function PlayerOSD(){
	
	var layer = "#osd-layer";
	var timeout;
	var self = this;
	
	this.setChannel = function(channel){
		
		if(channel.pos){
			$("#osd-wrap .ch-pos").html((channel.pos[0]) + "-" + (channel.pos[1]));
		}else{
			$("#osd-wrap .ch-pos").html("");
		}
		
		$(layer + " .ch-title").html(channel.name);
		
		var span = $("<span>");
		span.addClass("updated");
		span.html(timeDifference(new Date(), channel.timestamp));
		$(layer + " .s-ep-title .updated").remove();
		$(layer + " .s-ep-title").append(span);
	}
	this.setEpisode = function(episode){
		
		$(layer + " .ep-title").html(episode.name);
		$("#osd-wrap h1 .updated").html(timeDifference(new Date(), episode.published));
	}
	this.setProgram = function(program){
		
		$(layer + " .sub-ep-title").html(program.name);
		$("#osd-holder .ch-thumb img").attr("src", program.thumb); 
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
}

POI.prototype = new Controllable("POI");
POI.prototype.constructor = POI;
function POI(){
	
	this.selectLeft = function(){
		$("#poi-layer").poi("selectLeft");
	}
	this.selectRight = function(){
		$("#poi-layer").poi("selectRight");
	}
	this.select = function(i){
		$("#poi-layer").poi("select");
	}
}

