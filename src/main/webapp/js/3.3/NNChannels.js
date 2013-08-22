function NNChannels(data, current_channel){
	
	var self = this;
	var interval;
	var readyCount = 0;
	var allChannelsReady = false;
	this.data = data;
	this.channelArray = Array();
	this.currentIndex = 0;
	this.channels = Array();
	this.isInited = false;
	this.init = function(){
		
		var i = 0;
		var ch;
		for(var key in this.data){
			obj = {}
			obj[key] = this.data[key];
			this.channelArray.push(obj);
			ch = new NNChannel(obj);
			this.channels.push(ch);
			if(key == current_channel){
				this.currentIndex = i;
			}
			i++;
		}
		startWatch();
	}
	
	this.setChannelIndex = function(ch){
		var i = 0;
		for(var key in this.data){
			if(key == ch){
				this.currentIndex = i;
			}
			i++;
		}
	}
	
	function startWatch(){
		
		allChannelsReady = false;
		clearInterval(interval);
		interval = setInterval(function(){
			
			readyCount = 0;
			for(var i = 0; i<self.channels.length; i++){
				if(self.channels[i].isReady){
					readyCount++;
				}else{
					//console.debug(self.channels[i].id);
				}
			}
			if(readyCount == self.channels.length){
				
				clearInterval(interval);
				allChannelsReady = true;
				
				if(!self.goAheadPlaying){
					self.onChannelsReady();
				}
			}
			
		}, 500);
		
		setTimeout(function(){
			clearInterval(interval);
			if(!allChannelsReady){
				self.onChannelsReady();
				self.goAheadPlaying = true;
				console.debug("some channels could not be loaded");
			}
		}, 3000);
		
		//give it up after 30 secs
		setTimeout(function(){
			clearInterval(interval);
		},30000);
	}
	
	this.onChannelsReady = function(){
	}
	
	this.currentChannel = function(){
		return this.channels[this.currentIndex];
	};
	
	this.next = function(){
		
		//reset current channel
		this.currentChannel().reset();
		
		this.currentIndex++;
		if(this.currentIndex == this.channels.length){
			this.currentIndex = 0;
		}
		
		$(document).trigger("episodeChanged", [this.currentChannel().currentEpisode()]);
		$(document).trigger("channelChanged", [this.currentChannel()]);
	}
	
	this.prev = function(){
		
		//reset current channel
		this.currentChannel().reset();
		
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = this.channels.length - 1;
		}
		
		$(document).trigger("episodeChanged", [this.currentChannel().currentEpisode()]);
		$(document).trigger("channelChanged", [this.currentChannel()]);
	}
	
	this.prevChannel = function(){
		if(this.currentIndex == 0){
			return this.channels[this.channels.length-1];
		}
		return this.channels[this.currentIndex-1];
	};
	this.nextChannel = function(){
		if(this.currentIndex == this.channelArray.length - 1){
			return this.channels[0];
		}
		return this.channels[this.currentIndex+1];
	};
	this.init();
}
