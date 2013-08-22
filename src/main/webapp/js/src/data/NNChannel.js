function NNChannel(data){
	
	var self = this;
	var timeout;
	this.channelData;
	this.id;
	this.grid;
	this.data = Array();
	this.currentIndex = 0;
	this.episodes = Array();
	this.isReady = false;
	this.isNature6 = false;
	this.hasNoEpisodes = false;
	this.init = function(){
		this.parseData();
		this.prepareEpisodeData();
	}
	
	this.next = function(){
		
		//next program
		var episode = this.currentEpisode();
		if(episode == false){
			return false;
		}
		var program = this.currentEpisode().next();
		
		//if no next program(already at end) 
		if(program == false){
			this.currentEpisode().currentIndex = 0;
			this.currentIndex++;
			
			//if no next episode
			if(this.currentIndex == this.episodes.length){
				this.currentIndex--;
				return false;
			}
			$(document).trigger("episodeChanged", [this.currentEpisode(), this]);
			return true;
		}
		return true;
	}
	this.prev = function(){
		
		//prev program
		var program = this.currentEpisode().prev();
		
		//if no prev program(already at first) 
		if(program == false){
			this.currentEpisode().currentIndex = 0;
			this.currentIndex--;
			if(this.currentIndex == -1){
				this.currentIndex++;
				return false;
			}
			$(document).trigger("episodeChanged", [this.currentEpisode(), this]);
		}
	}
	
	this.reset = function(){
		this.currentIndex = 0;
		for(var i = 0; i<this.episodes.length; i++){
			this.episodes[i].currentIndex = 0;
		}
	}	
	
	this.parseData = function(){
		for(var key in data){
			this.grid = key;
			this.id = data[key]['id'];
			this.name = data[key]['name'];
			this.channelData = data[key];
			this.timestamp = data[key]["timestamp"];
		}
	}
	this.prepareEpisodeData = function(){
		var ep;
		if(this.channelData["nature"] == 6){
		//if(true){	
			this.isNature6 = true;
			query = "/playerAPI/programInfo?channel=" + this.id + '&' + 'user=' + user + unique() + "&v=32";
			$.get(query, function (data)
			{
				var res = new ProgramInfoResponse(data);
				if(res.success){
					self.data = res.data;
					for(var i = 0; i<self.data.length; i++){
						ep = new NNEpisode(self.data[i], 6);
						self.episodes.push(ep);
					}
					
					if(self.episodes.length == 0){
						self.hasNoEpisodes = true;
					}
					self.isReady = true;
				}else{
					self.isReady = true;
					self.hasNoEpisodes = true;
				}
				//console.debug("n6 ch " + self.id + " is ready, has " + self.episodes.length + "episodes");
			});
		}
		else if(this.channelData["nature"] == "8"){
		//else if(false){	
			query = "/playerAPI/programInfo?channel=" + this.id + unique();
			$.get(query, function (data)
			{
				var res = new ProgramInfoResponse(data);
				if(res.success){
					self.data = res.data;
					for(var i = 0; i<self.data.length; i++){
						ep = new NNEpisode(self.data[i], 8);
						self.episodes.push(ep);
					}
					// console.debug("channel:" + self.name + " length:" + self.episodes.length);
					// console.debug(self.episodes);
					
					if(self.episodes.length == 0){
						self.hasNoEpisodes = true;
					}
					self.isReady = true;
				}else{
					self.isReady = true;
					self.hasNoEpisodes = true;
				}
			});
		}
		else{
			this.episodes = Array();
			
			var tx;
			clearTimeout(tx);
			tx = setTimeout(function(){
				for(var key in programgrid){
					if(programgrid[key].channel == self.id){
						found = true;
						ep = new NNEpisode(programgrid[key]);
						self.episodes.push(ep);
						self.data.push(programgrid[key]);
					}
				}
				//console.debug("n" + this.channelData["nature"] + " ch " + self.id + " " + self.name + " is ready, has " + self.episodes.length + "episodes");
				
				//if(false){
				if(self.channelData["nature"] == 4 && found == false){
				//if(found == false){	
					
					var user = pool[self.id]['extra'];
					//console.debug(this.id + " " + this.name + " " + user);
					self.user = user;
					var url = 'http://gdata.youtube.com/feeds/api/playlists/' + user + '/uploads?v=2' + '&' + 'alt=json-in-script' + '&' + 'start-index=1' + '&' + 'max-results=50' + '&' + 'orderby=position' + '&' + 'prettyprint=true' + '&' + 'callback=NNYouTubeList';
					var script = document.createElement('script');
					script.type = 'text/javascript';
					script.async = true;
					script.id = self.id;
					script.src = url;
					var tag = document.getElementsByTagName('script')[0];
					tag.parentNode.insertBefore(script, tag);
					
				}else{
					self.isReady = true;
					if(self.episodes.length == 0){
						self.hasNoEpisodes = true;
					}
				}
			},500);
			var found = false;
		}
	}
	this.currentEpisode = function(){
		return this.getEpisodeByIndex(this.currentIndex);
	}
	this.nextEpisode = function(){
		return this.getEpisodeByIndex(this.currentIndex+1);
	}
	
	this.getEpisodeByIndex = function(i){
		if(this.episodes.length == 0 || i<0 || i>this.episodes.length-1){
			return false;
		}else{
			return this.episodes[i];
		}
	}
	
	this.currentProgram = function(){
		if(this.currentEpisode() == false){
			return false;
		}
		return this.currentEpisode().currentProgram();
	}
	
	this.nextProgram = function(){
		if(this.currentEpisode() == false){
			return false;
		}
		var ep;
		if(this.isNature6){
			if(this.currentEpisode().nextProgram() == false){
				ep = this.getEpisodeByIndex(this.currentIndex+1);
				if(ep == false){
					return false;
				}
				return ep.currentProgram();
			}
			return this.currentEpisode().nextProgram();
		}
		else{
			if(this.episodes.length>1){
				if(this.currentIndex < this.episodes.length - 2){
					return this.episodes[this.currentIndex+1].programs[0];
				}else{
					return this.episodes[0].programs[0];
				}
			}
			return this.episodes[0].programs[0];
		}
	}
	
	this.prevProgram = function(){
		if(this.isNature6){
			if(this.currentEpisode() == false){
				return false;
			}
			return this.currentEpisode().prevProgram();
		}
		else{
			if(this.episodes.length>1){
				if(this.currentIndex + 1 < this.episodes.length - 1){
					return this.episodes[this.currentIndex+1].programs[0];
				}else{
					return this.episodes[0].programs[0];
				}
			}
			return this.episodes[0].programs[0];
		}
	}
	this.init();
}

function NNYouTubeList(data){
	//console.debug(data.feed.title);
	
	var id = data.feed.id.$t;
	var channelName = data.feed.title.$t;
	id = id.split(":");
	var user = id[id.length-1];
	var chs = nnChannels.channels;
	var ch, entry;
	for(var i = 0; i<chs.length; i++){
		
		ch = chs[i];
		//if(channel.name == channelName){
		if(ch.user == user){
			NNCreateEpisode(ch, data);		
		}
	}
}

function NNCreateEpisode(ch, data){
	
	var arr = data.feed.entry || [];
	var item;
	for (var i = 0; i < arr.length; i++)
	{
		var id = title = updated = video_id = dtime = timestamp = thumb = ts = program_id = timestamp = "";
		var obj = {};
		var duration = 0;
		
		item = arr[i];
		
		id = item.id.$t;
		title = item.title.$t;
		
		// updated = item.updated.$t;
		video_id = item.media$group.yt$videoid.$t;
		dtime = item.media$group.yt$uploaded.$t;
		timestamp = new Date(dtime);
		ts = timestamp.getTime();
		program_id = ch.id + '.' + video_id;
		
		if (ts == undefined || isNaN(ts) || ts == Infinity) ts = now.getTime();
		if(item.media$group.yt$duration) duration = item.media$group.yt$duration.seconds;
		if(item.media$group.media$thumbnail){
			if(item.media$group.media$thumbnail.thumb){
				thumb = item.media$group.media$thumbnail.thumb[1]['url'];
			}
		}
 				
		obj = {
			'channel': ch.id,
			'channelId': ch.id,
			'url1': 'fp:http://www.youtube.com/watch?v=' + video_id,
			'url2': '',
			'url3': '',
			'url4': '',
			'name': title,
			'desc': '',
			'type': '',
			'thumb': thumb,
			'snapshot': thumb,
			'timestamp': ts,
			'duration': duration,
			'sort': i + 1
		};
		var ep = new NNEpisode(obj, 4);
		ch.episodes.push(ep);
	}
	ch.isReady = true;
}

