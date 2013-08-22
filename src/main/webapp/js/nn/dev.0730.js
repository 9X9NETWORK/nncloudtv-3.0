if (!String.prototype.trim) {
   String.prototype.trim=function(){return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g,'').replace(/\s+/g,' ');};
}

function beforeRender(){
	$("body").hide();
}

function afterRender(){
	
	//clear sample codes from mockup
	$(".guide-group-list>li").each(function(i,e){
		
		$(e).find(".ch-thumb img").remove();
		$(e).find(".guide-thumb img").remove();
		$(e).find(".ch-title").html("");
		$(e).find(".ch-thumb").append("<img />");
		$(e).find(".guide-thumb").append("<img />");
	});
	
	$(".store-item-list li .store-item").remove();
	$(".store-item-preview object").remove();
	
	$("body").show().delay(1).hide().delay(500).fadeIn(500);
}

function getStoreCategory(data){
	
	var obj = [];
	try{
		var arr = data.split("--\n");
		var content = arr[2].trim().split("\n");
		var tmp;
		for(var i = 0; i<content.length; i++){
			tmp = content[i].split("\t");
			obj.push({
				id : tmp[0],
				name : tmp[1],
				count : tmp[2]
			});
		}
	}catch(e){
		
	}
	return obj;
}

function getCategoryInfoMeta(data){
	var arr = data.split("--\n");
	var meta = arr[1].split("\n");
	var obj = {
		id : meta[0].trim().split("\t")[1],
		name : meta[1].trim().split("\t")[1],
		start : meta[2].trim().split("\t")[1],
		count : meta[3].trim().split("\t")[1],
		total : meta[4].trim().split("\t")[1]
	}
	return obj;
}

function getCategoryInfo(data, programInfo){
	
	programInfo = true;
	
	var arr = data.split("--\n");
	var content = arr[3].trim().split("\n");
	var tmp;
	var obj = [];
	var channelArr = [];
	
	//pop the last empty row off
	content.pop();
	
	for(var i = 0; i<content.length; i++){
		tmp = content[i].split("\t");
		obj = {
			grid : tmp[0],
			id : tmp[1],
			name : tmp[2],
			description : tmp[3],
			images : tmp[4].split("|"),
			programCount : tmp[5],
			type : tmp[6],
			status : tmp[7],
			nature : tmp[8],
			youtubeID : tmp[9],
			lastUpdated : tmp[10],
			sorting : tmp[11],
			piwikID : tmp[12],
			lastWatchedEpisode : tmp[13],
			youtubeRealChannelName : tmp[14],
			subscriptionCount : tmp[15],
			viewCount : tmp[16],
			tags : tmp[17].split(","),
			curatorID : tmp[18],
			curatorName : tmp[19],
			curatorDescription : tmp[20],
			curatorImage : tmp[21],
			subscriberIDs : tmp[22].split("|"),
			subscriberImages : tmp[23].split("|"),
			lastEpisodeTitle : tmp[24].split("|")
		};
		channelArr.push(new NNChannel(obj));
	}
	if(programInfo == true && arr.length == 5){
		var episodes = arr[4].split("\n");
		var obj1 = [];
		for(i = 0; i<episodes.length; i++){
			tmp = episodes[i].split("\t");
			if(tmp.length == 15){
				obj1.push(new ProgramItem(tmp));
				if(channelArr[i]){
					channelArr[i].lastEpisode = obj1[i];
				}
			}
		}
	}
	return channelArr;
}

function getSearchCategoryInfo(data){
	var arr = data.split("--\n");
	var merged = arr[3].concat(arr[4].trim())
	var content = merged.split("\n");
	var tmp;
	var obj = [];
	var channelArr = [];
	
	//pop the last empty row off
	content.pop();
	
	for(var i = 0; i<content.length; i++){
		tmp = content[i].split("\t");
		obj = {
			grid : tmp[0],
			id : tmp[1],
			name : tmp[2],
			description : tmp[3],
			images : tmp[4].split("|"),
			programCount : tmp[5],
			type : tmp[6],
			status : tmp[7],
			nature : tmp[8],
			youtubeID : tmp[9],
			lastUpdated : tmp[10],
			sorting : tmp[11],
			piwikID : tmp[12],
			lastWatchedEpisode : tmp[13],
			youtubeRealChannelName : tmp[14],
			subscriptionCount : tmp[15],
			viewCount : tmp[16],
			tags : tmp[17].split(","),
			curatorID : tmp[18],
			curatorName : tmp[19],
			curatorDescription : tmp[20],
			curatorImage : tmp[21],
			subscriberIDs : tmp[22].split("|"),
			subscriberImages : tmp[23].split("|"),
			lastEpisodeTitle : tmp[24].split("|")
		};
		channelArr.push(new NNChannel(obj));
	}
	return channelArr;
}

function getUserProfile(data){
	var arr = data.split("--\n");
	var merged = arr[1];
	var content = merged.split("\n");
	
	var obj = {
		"name" : content[0].split("\t")[1],
		"email" : content[1].split("\t")[1],
		"description" : content[2].split("\t")[1],
		"image" : content[3].split("\t")[1],
		"gender" : content[4].split("\t")[1],
		"year" : content[5].split("\t")[1],
		"sphere" : content[6].split("\t")[1],
		"ui-lang" : content[7].split("\t")[1],
		"phone" : content[8].split("\t")[1]
	}
	
	return obj;
}


function parseChannel(data, programInfo){
	
	var arr = data.split("--\n");
	if(arr.length <2) return {};
	//var content = arr[1].trim().split("\n");
	var channel = arr[1];
	var tmp;
	var channelArr = [];
	
	tmp = channel.split("\t");
	if(tmp.length == 16){
		obj = {
			grid : tmp[0],
			id : tmp[1],
			name : tmp[2],
			description : tmp[3],
			images : tmp[4].split("|"),
			programCount : tmp[5],
			type : tmp[6],
			status : tmp[7],
			nature : tmp[8],
			youtubeID : tmp[9],
			lastUpdated : tmp[10],
			sorting : tmp[11],
			piwikID : tmp[12],
			lastWatchedEpisode : tmp[13],
			youtubeRealChannelName : tmp[14],
			subscriptionCount : tmp[15],
			viewCount : tmp[16]
		};
	}else{
		obj = {
			grid : tmp[0],
			id : tmp[1],
			name : tmp[2],
			description : tmp[3],
			images : tmp[4].split("|"),
			programCount : tmp[5],
			type : tmp[6],
			status : tmp[7],
			nature : tmp[8],
			youtubeID : tmp[9],
			lastUpdated : tmp[10],
			sorting : tmp[11],
			piwikID : tmp[12],
			lastWatchedEpisode : tmp[13],
			youtubeRealChannelName : tmp[14],
			subscriptionCount : tmp[15],
			viewCount : tmp[16],
			tags : tmp[17].split(","),
			curatorID : tmp[18],
			curatorName : tmp[19],
			curatorDescription : tmp[20],
			curatorImage : tmp[21],
			subscriberIDs : tmp[22].split("|"),
			subscriberImages : tmp[23].split("|"),
			lastEpisodeTitle : tmp[24].split("|"),
			poi : tmp[25].split("|")
		};
	}
	return new NNChannel(obj);
}

function getChannelLineup(data, programInfo){
	
	var arr = data.split("--\n");
	
	if(arr[0].split("\t")[0] != "0"){
		return new GuideChannels([]);
	}
	//var content = arr[1].trim().split("\n");
	var channels = arr[1].split("\n");
	if(channels.length == 0 || channels.length == 1){
		return new GuideChannels([]);
	}
	var tmp;
	var channelArr = [];
	var _try = function(str){
		try{
			return eval(str);
		}catch(e){ return ""}
	}
	
	//pop the last empty row off
	if(channels.length > 1){
		channels.pop();
	}
	
	for(var i = 0; i<channels.length; i++){
		tmp = channels[i].split("\t");
		if(tmp.length == 16){
			obj = {
				grid : tmp[0],
				id : tmp[1],
				name : tmp[2],
				description : tmp[3],
				images : tmp[4].split("|"),
				programCount : tmp[5],
				type : tmp[6],
				status : tmp[7],
				nature : tmp[8],
				youtubeID : tmp[9],
				lastUpdated : tmp[10],
				sorting : tmp[11],
				piwikID : tmp[12],
				lastWatchedEpisode : tmp[13],
				youtubeRealChannelName : tmp[14],
				subscriptionCount : tmp[15],
				viewCount : tmp[16]
			};
		}else{
			obj = {
				grid : tmp[0],
				id : tmp[1],
				name : tmp[2],
				description : tmp[3],
				images : tmp[4].split("|"),
				programCount : tmp[5],
				type : tmp[6],
				status : tmp[7],
				nature : tmp[8],
				youtubeID : tmp[9],
				lastUpdated : tmp[10],
				sorting : tmp[11],
				piwikID : tmp[12],
				lastWatchedEpisode : tmp[13],
				youtubeRealChannelName : tmp[14],
				subscriptionCount : tmp[15],
				viewCount : tmp[16],
				tags : tmp[17].split(","),
				curatorID : tmp[18],
				curatorName : tmp[19],
				curatorDescription : tmp[20],
				curatorImage : tmp[21],
				subscriberIDs : tmp[22].split("|"),
				subscriberImages : tmp[23].split("|"),
				lastEpisodeTitle : tmp[24].split("|"),
				poi : _try('tmp[25].split("|")')
			};
		}
		var ch = new NNChannel(obj);
		ch.load();
		channelArr.push(ch);
	}
	
	if(programInfo == true && arr.length == 3){
		var episodes = arr[2].split("\n");
		var obj1 = [];
		for(i = 0; i<episodes.length; i++){
			tmp = episodes[i].split("\t");
			if(tmp.length == 15 || tmp.length == 16){
				obj1 = new ProgramItem(tmp);
				for(var j = 0; j<channelArr.length; j++){
					if(channelArr[j].id == obj1.channelId){
						channelArr[j].lastEpisode = obj1;
					}
				}
			}
		}
	}
	var gch = new GuideChannels(channelArr);
	return gch;
}

function StreamingChannels(arr){
	this.currentIndex = 0;
	this.arr = arr;
	this.append = function(input){
		
		if($.isArray(input)){
			for(var i = 0; i<input.length; i++){
				this.arr.push(input[i]);
			}
		}else{
			this.arr.push(input);
		}
	}
	this.unbindReadyAll = function(){
		for(var i = 0; i<this.arr.length; i++){
			$(this.arr[i]).unbind("ready");
		}
	}
	this.eq = function(i){
		return arr[i];
	}
	this.length = function(){
		return arr.length;
	}
	this.byId = function(id){
		for(var i = 0; i<arr.length; i++){
			if(arr[i].id == id){
				return arr[i];
			}
		}
		return false;
	}
	this.setIndex = function(id){
		for(var i = 0; i<arr.length; i++){
			if(arr[i].id == id){
				this.currentIndex = i;
			}
		}
	}
	this.all = function(){
		return arr;
	}
	this.current = function(){
		return arr[this.currentIndex];
	}
	this.next = function(){
		var i = this.currentIndex + 1;
		if(i == arr.length){
			i = 0; 
		}
		return arr[i];
	}
	this.prev = function(){
		var i = this.currentIndex - 1;
		if(i == -1){
			i = arr.length-1;
		}
		return arr[i];
	}
	this.goNext = function(){
		this.currentIndex++;
		if(this.currentIndex == arr.length){
			this.currentIndex = 0;
			return false;
		}
	}
	this.goPrev = function(){
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = arr.length - 1;
			return false;
		}
	}
}

function GuideChannels(arr){
	
	this.currentIndex = 0;
	this.arr = arr;
	
	var self = this;
	this.setIndex = function(id){
		for(var i = 0; i<arr.length; i++){
			if(arr[i].id == id){
				this.currentIndex = i;
			}
		}
	}
	this.remove = function(id){
		arr = this.arr;
		for(var i = 0; i<arr.length; i++){
			if(arr[i].id == id){
				arr.splice(i, 1);
				this.arr = arr;
			}
		}
	}
	this.unbindReadyAll = function(){
		for(var i = 0; i<this.arr.length; i++){
			$(this.arr[i]).unbind("ready");
		}
	}
	this.add = function(id){
		var req = new APIRequest({
			url : "/playerAPI/channelLineup?channel=" + id + "&v=40&programInfo=true" + "&mso=" + mso,
			success : function(data){
				self.append(data);
			}
		});
		loader.prepend(req);
	}
	this.eq = function(i){
		return arr[i];
	}
	this.hasGrid = function(grid){
		for(var i = 0; i<arr.length; i++){
			if(arr[i].grid == grid) return true;
		}
		return false;
	}
	this.length = function(){
		return arr.length;
	}
	this.byId = function(id){
		for(var i = 0; i<arr.length; i++){
			if(arr[i].id == id){
				return arr[i];
			}
		}
		return false;
	}
	this.all = function(){
		return arr;
	}
	this.current = function(){
		return arr[this.currentIndex];
	}
	this.next = function(){
		var i = this.currentIndex + 1;
		if(i == arr.length){
			i = 0; 
		}
		return arr[i];
	}
	this.prev = function(){
		var i = this.currentIndex - 1;
		if(i == -1){
			i = arr.length-1;
		}
		return arr[i];
	}
	this.goNext = function(){
		this.currentIndex++;
		if(this.currentIndex == arr.length){
			this.currentIndex = arr.length - 1;
			return false;
		}
		return true;
	}
	this.goPrev = function(){
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = 0;
			return false;
		}
		return true;
	}
	this.append = function(data){
		
		var arr = data.split("--\n");
		if(arr.length <2) return {};
		//var content = arr[1].trim().split("\n");
		var channels = arr[1].split("\n");
		var tmp;
		var channelArr = [];
		
		//pop the last empty row off
		if(channels.length > 1){
			channels.pop();
		}
		
		for(var i = 0; i<channels.length; i++){
			tmp = channels[i].split("\t");
			if(tmp.length == 16){
				obj = {
					grid : tmp[0],
					id : tmp[1],
					name : tmp[2],
					description : tmp[3],
					images : tmp[4].split("|"),
					programCount : tmp[5],
					type : tmp[6],
					status : tmp[7],
					nature : tmp[8],
					youtubeID : tmp[9],
					lastUpdated : tmp[10],
					sorting : tmp[11],
					piwikID : tmp[12],
					lastWatchedEpisode : tmp[13],
					youtubeRealChannelName : tmp[14],
					subscriptionCount : tmp[15],
					viewCount : tmp[16]
				};
			}else{
				obj = {
					grid : tmp[0],
					id : tmp[1],
					name : tmp[2],
					description : tmp[3],
					images : tmp[4].split("|"),
					programCount : tmp[5],
					type : tmp[6],
					status : tmp[7],
					nature : tmp[8],
					youtubeID : tmp[9],
					lastUpdated : tmp[10],
					sorting : tmp[11],
					piwikID : tmp[12],
					lastWatchedEpisode : tmp[13],
					youtubeRealChannelName : tmp[14],
					subscriptionCount : tmp[15],
					viewCount : tmp[16],
					tags : tmp[17].split(","),
					curatorID : tmp[18],
					curatorName : tmp[19],
					curatorDescription : tmp[20],
					curatorImage : tmp[21],
					subscriberIDs : tmp[22].split("|"),
					subscriberImages : tmp[23].split("|"),
					lastEpisodeTitle : tmp[24].split("|"),
					poi : tmp[25].split("|")
				};
			}
			var ch = new NNChannel(obj);
			ch.load();
			self.arr.push(ch);
		}
		
		if(programInfo == true && arr.length == 3){
			var episodes = arr[2].split("\n");
			var obj1 = [];
			for(i = 0; i<episodes.length; i++){
				tmp = episodes[i].split("\t");
				if(tmp.length == 15 || tmp.length == 16){
					obj1 = new ProgramItem(tmp);
					for(var j = 0; j<channelArr.length; j++){
						if(self.arr[j].id == obj1.channelId){
							self.arr[j].lastEpisode = obj1;
						}
					}
				}
			}
		}
	}
}

function NNChannel(data){
	
	this.isReady = false;
	this.currentIndex = 0;
	var self = this;
	function _try(str){
		try{
			return eval(str);
		}catch(e){
			return false;
		}
	}
	function onConstructed(){
		
		for(var i in data){
			self[i] = data[i];
		}
	}
	onConstructed();
	
	this.load = function(){
		var fn = function(){
			self.isReady = true;
			$(self).trigger("ready");
		}
		if(!self.nature) return;
		switch(self.nature){
			case "6":
			case "8":
			case "11":
				q = "/playerAPI/programInfo?channel=" + self.id + "&v=40" + "&mso=" + mso;
				
				obj = {
					url : q,
					context : self,
					success : function(res){
						this.episodes = getProgramInfo(res);
						//update count with actual number of episodes
						this.programCount = this.episodes.length;
						fn();							
					},
					error : function(){
						this.episodes = [];
						this.programCount = 0;
						fn();
					}
				}
			break;
			case "4":
				q = "http://gdata.youtube.com/feeds/api/playlists/" + self.youtubeID + "?v=2&alt=json&start-index=1&max-results=50&orderby=position";
				obj = {
					url : q,
					dataType : "json",
					context : self,
					success : function(res){
						this.episodes = getYouTubePlayList(res, this.id);
						//update count with actual number of episodes
						this.programCount = this.episodes.length;
						fn();
					},
					error : function(){
						this.episodes = [];
						this.programCount = 0;
						fn();
					}
				}
			break;
			case "3":
				q = "http://gdata.youtube.com/feeds/api/users/" + self.youtubeID + "/uploads?v=2&alt=json&start-index=1&max-results=50&orderby=published";
				obj = {
					url : q,
					dataType : "json",
					context : self,
					success : function(res){
						this.episodes = getYouTubeUsers(res, this.id);
						//update count with actual number of episodes
						this.programCount = this.episodes.length;
						fn();
					},
					error : function(){
						this.episodes = [];
						this.programCount = 0;
						fn();
					}
				}
			break;
			default:
				
				this.episodes = [];
				
			break;
		}
		self.request = new APIRequest(obj);
		loader.append(self.request);
	}
	
	this.current = function(){
		if(!self.episodes) return false;
		return self.episodes[self.currentIndex];
	}
	
	this.next = function(){
		if(!self.episodes) return false;
		var i = this.currentIndex+1;
		if(i == this.episodes.length){
			i = 0;
		}
		return this.episodes[i];
	}
	
	this.prev = function(){
		if(!self.episodes) return false;
		var i = this.currentIndex-1;
		if(i == -1){
			i = this.episodes.length-1;
		}
		return this.episodes[i];
	}
	
	this.byId = function(id){
		for(var i = 0; i<this.episodes.length; i++){
			if(this.episodes[i].id == id){
				return this.episodes[i];
			}
		}
		return false;
	}
	
	this.goNext = function(){
		this.currentIndex++;
		if(this.currentIndex == this.episodes.length){
			this.currentIndex = 0;
			return false;
		}
		return this;
	}
	
	this.goPrev = function(){
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = this.episodes.length - 1;
			return false;
		}
		return this;
	}
}

function getLoginResponse(data){
	
	var arr = data.split("--\n");
	
	if(arr[0].split("\t")[0] == "0"){
		var content = arr[1].trim();
		var tmp;
		tmp = content.split("\n");
		var obj = {};
		var tmp1;
		
		obj.success = true;
		for(var i = 0; i<tmp.length; i++){
			tmp1 = tmp[i].split("\t");
			obj[tmp1[0]] = tmp1[1];
		}
		return obj;
	}else{
		return {success : false}
	}
}

function getPromoSet(data){
	// var arr = data.split("--/n");
	// var sets = arr[1].trim().split("\n");
	// var tmp;
	// var obj = {}
	// try{
		// tmp = sets
	// }catch(e){}
	// return 
}

function getPortal(data){
	
	var arr = data.split("--\n");
	var sets = arr[1].trim().split("\n");
	// var channels = arr[2].trim().split("\n");
	// var episodes = arr[3].trim().split("\n");
	var tmp;
	var obj = {
		sets : [],
		channels : [],
		episodes : []
	};
	
	//pop the last empty row off
	
	for(var i = 0; i<sets.length; i++){
		try{
			tmp = sets[i].split("\t");
			obj.sets.push({
				id : tmp[0],
				name : tmp[1],
				description : tmp[2],
				images : tmp[3].split("|"),
				channelCount : tmp[4]
			});
		}catch(e){}
	}
	// for(i = 0; i<channels.length; i++){
		// try{
			// tmp = channels[i].split("\t");
			// obj.channels.push({
				// grid : tmp[0],
				// id : tmp[1],
				// name : tmp[2],
				// description : tmp[3],
				// images : tmp[4].split("|"),
				// programCount : tmp[5],
				// type : tmp[6],
				// status : tmp[7],
				// nature : tmp[8],
				// youtubeID : tmp[9],
				// lastUpdated : tmp[10],
				// sorting : tmp[11],
				// piwikID : tmp[12],
				// lastWatchedEpisode : tmp[13],
				// youtubeRealChannelName : tmp[14],
				// subscriptionCount : tmp[15],
				// viewCount : tmp[16],
				// tags : tmp[17].split(","),
				// curatorID : tmp[18],
				// curatorName : tmp[19],
				// curatorDescription : tmp[20],
				// curatorImage : tmp[21],
				// subscriberIDs : tmp[22].split("|"),
				// subscriberImages : tmp[23].split("|"),
				// lastEpisodeTitle : tmp[24].split("|")
			// });
		// }catch(e){}
	// }
	// for(i = 0; i<episodes.length; i++){
// 		
		// try{
			// tmp = episodes[i].split("\t");
			// obj.episodes.push({
				// channelId : tmp[0],
				// programId : tmp[1],
				// programNames : tmp[2] ? tmp[2].split("|") : "",
				// descriptions : tmp[3] ? tmp[3].split("|") : "",
				// programTypes : tmp[4] ? tmp[4].split("|") : "",
				// durations : tmp[5].split("|"),
				// thumbnailUrls : tmp[6].split("|"),
				// largeThumbnailUrls : tmp[7].split("|"),
				// url1 : tmp[8].split("|"),
				// url2 : "",
				// url3 : "",
				// url4 : "",
				// timestamp : tmp[12],
				// reserved : "",
				// titlecard : tmp[14]
			// });
		// }catch(e){}
	// }
	return obj;
}

function getSetInfo(data){
	
	var arr = data.split("--\n");
	var tmp;
	var obj = {
		brand : {},
		set : {},
		channels : [],
		episodes : []
	};
	var brand = arr[1].split("\n");
	var sets = arr[2].split("\n");
	var channels = arr[3].split("\n");
	var episodes = arr[4].split("\n");
	var _try = function(str){try{return eval(str);}catch(e){return "";}}
	
	for(var i = 0; i<brand.length; i++){
		tmp = brand[i].split("\t");
		if(tmp[0] != "" && tmp.length == 2){
			obj["brand"][tmp[0]] = tmp[1];
		}
	}
	
	for(var i = 0; i<sets.length; i++){
		tmp = sets[i].split("\t");
		if(tmp[0] != "" && tmp.length == 2){
			obj["set"][tmp[0]] = tmp[1];
		}
	}
	
	if(channels.length > 0){
		for(i = 0; i<channels.length; i++){
			try{
				tmp = channels[i].split("\t");
				var ch = new NNChannel({
					grid : tmp[0],
					id : tmp[1],
					name : tmp[2],
					description : tmp[3],
					images : tmp[4].split("|"),
					programCount : tmp[5],
					type : tmp[6],
					status : tmp[7],
					nature : tmp[8],
					youtubeID : tmp[9],
					lastUpdated : tmp[10],
					sorting : tmp[11],
					piwikID : tmp[12],
					lastWatchedEpisode : tmp[13],
					youtubeRealChannelName : tmp[14],
					subscriptionCount : tmp[15],
					viewCount : tmp[16],
					tags : tmp[17].split(","),
					curatorID : tmp[18],
					curatorName : tmp[19],
					curatorDescription : tmp[20],
					curatorImage : tmp[21],
					subscriberIDs : tmp[22].split("|"),
					subscriberImages : tmp[23].split("|"),
					lastEpisodeTitle : tmp[24].split("|")
				});
				obj.channels.push(ch);
			}catch(e){}
		}
	}
	
	var cidInEp = [];
	if(episodes.length > 0){
		for(i = 0; i<episodes.length; i++){
			try{
				tmp = episodes[i].split("\t");
				
				//get youtube ids from url1
				var urls = tmp[8].split("|");
				var ids = [], vid;
				for(var j = 0; j<urls.length; j++){
					vid = urls[j].split("v=")[1];
					ids.push(vid);
				}
				cidInEp.push(tmp[0]);
				obj.episodes.push({
					channelId : tmp[0],
					programId : tmp[1],
					programNames : tmp[2] ? tmp[2].split("|") : "",
					descriptions : tmp[3] ? tmp[3].split("|") : "",
					programTypes : tmp[4] ? tmp[4].split("|") : "",
					durations : tmp[5].split("|"),
					thumbnailUrls : tmp[6].split("|"),
					largeThumbnailUrls : tmp[7].split("|"),
					url1 : tmp[8].split("|"),
					url2 : "",
					url3 : "",
					url4 : "",
					ytVideoIds : ids,
					timestamp : tmp[12],
					reserved : "",
					titlecard : tmp[14]
				});
			}catch(e){}
		}
	}

	function notIn(id, arr){
		for(var p = 0; p<arr.length; p++){
			if(arr[p] == id){
				return false;
			}
		}
		return true;
	}

	//remove channels that have no episodes
	for(i = obj.channels.length-1; i>-1; i--){
		if(notIn(obj.channels[i].id, cidInEp)){
			obj.channels.splice(i,1);
		}
	}
	
	return obj;
}

function getPersonalHistory(data){
	
	var arr = data.split("--\n");
	var tmp;
	var channels = arr[1].split("\n");
	var arr = [];
	var _try = function(str){try{return eval(str);}catch(e){return "";}}
	
	if(channels.length > 0){
		for(i = 0; i<channels.length; i++){
			try{
				tmp = channels[i].split("\t");
				var ch = new NNChannel({
					grid : tmp[0],
					id : tmp[1],
					name : tmp[2],
					description : tmp[3],
					images : tmp[4].split("|"),
					programCount : tmp[5],
					type : tmp[6],
					status : tmp[7],
					nature : tmp[8],
					youtubeID : tmp[9],
					lastUpdated : tmp[10],
					sorting : tmp[11],
					piwikID : tmp[12],
					lastWatchedEpisode : tmp[13],
					youtubeRealChannelName : tmp[14],
					subscriptionCount : tmp[15],
					viewCount : tmp[16],
					tags : tmp[17].split(","),
					curatorID : tmp[18],
					curatorName : tmp[19],
					curatorDescription : tmp[20],
					curatorImage : tmp[21],
					subscriberIDs : tmp[22].split("|"),
					subscriberImages : tmp[23].split("|"),
					lastEpisodeTitle : tmp[24].split("|")
				});
				arr.push(ch);
			}catch(e){}
		}
	}
	
	return arr;
}

function getSignUpResponse(data){
	var arr = data.split("\n--\n");
	
	if(arr[0].split("\t")[0] == "0"){
		var content = arr[1];
		var tmp;
		tmp = content.split("\n");
		var obj = {
			"success" : true
		};
		var tmp1;
		for(var i = 0; i<tmp.length; i++){
			tmp1 = tmp[i].split("\t");
			obj[tmp1[0]] = tmp1[1];
		}
		return obj;
	}else{
		return {
			success : false,
			reason : arr[0].split("\t")[1]
		}
	}
}

function TitleCard(data){
	
	data = data.split("\n");
	var key, val, p;
	for(var i = 0; i<data.length; i++){
		p = data[i].split(":");
		
		if(p.length == 2){
			
			key = p[0].trim();
			val = p[1].trim();
			
			if(key == "type"){
				this.type = val;
			}
			if(key == "subepisode"){
				this.subepisode = val;
			}
			if(key == "message"){
				this.message = val;
			}
			if(key == "duration"){
				this.duration = val;
			}
			if(key == "style"){
				this.style = val;
			}
			if(key == "size"){
				this.size = val;
			}
			if(key == "color"){
				this.color = val;
			}
			if(key == "effect"){
				this.effect = val;
			}
			if(key == "align"){
				this.align = val;
			}
			if(key == "bgcolor"){
				this.bgcolor = val;
			}
			if(key == "bgimage"){
				this.bgimage = val;
			}
			if(key == "weight"){
				this.weight = val;
			}
		}
	}
}

function createTitleCards(data){
	
	data = urldecode(data.trim());
	var a = data.split("--");
	var cards = Array();
	for(var i = 0; i<a.length; i++){
		a[i].trim();
		if(a[i].length>1){
			cards.push(new TitleCard(a[i]));
		}
	}
	return cards;
}

function getProgramInfo(data){
	
	var arr = [], ep;
	var res = new ProgramInfoResponse(data);
	if(res.success){
		for(var i = 0; i<res.data.length; i++){
			ep = new NNEpisode(res.data[i]);
			arr.push(ep);
		}
	}
	return arr;
}

function ProgramInfoResponse(data){
	
	var arr, result, tmp, card, item;
	arr = Array();
	arr = data.split("--\n");
	if(arr.length != 2){
		console.info("ProgramInfoResponse Error");
		console.info(arr);
		return false;
	}
	this.success = arr[0].split("\t")[0] == "0";
	this.data = Array();
	arr = arr[1].split("\n");
	
	for(var i = 0; i<arr.length; i++){
		tmp = arr[i].split("\t");
		if(tmp.length > 2 && (tmp.length == 15 || tmp.length == 16)){
			item = new ProgramItem(tmp);
			this.data.push(item);
		}
	}
}

function ProgramItem(data){
	
	this.channelId = data[0];
	this.programId = data[1]; 
	this.programName = data[2].split("|");
	this.description = data[3].split("|");
	this.programType = data[4].split("|");
	this.duration = data[5].split("|");
	this.programThumbnailUrl = data[6].split("|");
	this.programLargeThumbnailUrl = data[7].split("|");
	this.url1 = data[8].split("|");
	this.url2 = data[9].split("|");
	this.url3 = data[10].split("|");
	this.url4 = data[11].split("|");
	this.published = data[12];
	this.reversed = data[13];
	this.titleCard =  createTitleCards(data[14]);
	if(data[15]){
		this.poi = getPOI(data[15]);
	}else{
		this.poi = false;
	}
}

function getPOI(data){
	var arr = data.split("|");
	var item, items, obj = {};
	var _try = function(a, b){ try{	return eval(a);	}catch(e){ return b ? b : false;}};
	var rs = []
	for(var i = 0; i<arr.length; i++){
		item = arr[i];
		items = item.split(";");
		obj = {
			"subEpisodePos" : _try("items[0]"),
			"start" : _try("items[1]", 0),
			"end" : _try("items[2]", 0),
			"type" : _try("items[3]"),
			"content" : _try("JSON.parse(urldecode(items[4]))")
		}
		rs.push(obj);
	}
	return rs;
}

function getYouTubePlayList(data, channel){
	var eps = parseYouTubeData(data, channel);
	//console.debug(eps);
	return eps;
}

function getYouTubeUsers(data, channel){
	var eps = parseYouTubeData(data, channel);
	return eps;
}

function parseYouTubeData(data, channel){
	
	var channel;
	var now = new Date();
	var arr = [];
	if (data && data.feed)
	{
		feed = data.feed;

		var name = feed.author[0].name.$t;
		var name1 = feed.author[0].uri.$t.split("/");
		name1 = name1[name1.length-1];
		
		// name1 = name1.toLowerCase();
		// name = name.toLowerCase();

		// playlists are different
		// "tag:youtube.com,2008:playlist:45f1353372bc22eb"
		var ytid = feed.id.$t;
		if (ytid.match(/playlist:/)) name = ytid.match(/playlist:(.*)$/)[1];
		
		// if(ytid == "tag:youtube.com,2008:user:cowboy731216:uploads"){
			// console.debug("found ma");
		// }

		var entries = feed.entry || [];
		var entry;
		for (var i = 0; i < entries.length; i++)
		{
			entry = entries[i];
			
			//entry.media$group.yt$duration sometimes unavailable
			if(entry.media$group.yt$duration){
				
				var video_id = entry.media$group.yt$videoid.$t;
				var id = "yt" + video_id;
				var title = entry.title.$t;
				var updated = entry.updated.$t;
				var duration = entry.media$group.yt$duration.seconds;
				var dtime = entry.media$group.yt$uploaded.$t;
				var timestamp = new Date(dtime);
				var thumb = entry.media$group.media$thumbnail[1]['url'];
				var ts = timestamp.getTime();
				if (ts == undefined || isNaN(ts) || ts == Infinity) ts = now.getTime();
				
				var program_id = channel + '.' + video_id;
				var obj = {
					'id': id,
					'url1': ['http://www.youtube.com/watch?v=' + video_id],
					'url2': '',
					'url3': '',
					'url4': '',
					'name': title,
					'programName': [title],
					'desc': '',
					'type': '',
					'programThumbnailUrl': [thumb],
					'snapshot': thumb,
					'published': ts,
					'duration': [duration],
					'durations' : [duration],
					'sort': i + 1,
					'videoId' : video_id
				};
				var episode = new YTEpisode(obj);
				arr.push(episode);
			}
				
			
			try{
				
			}catch(e){
				
			}
		}
	}
	return arr;
}

function ApiQueue(){
	
	var queue = [];
	var interval;
	var isProcessing = false;
	var self = this;
	var max = 3;
	var counter = 0;
	this.append = function(api, callback, dataType){
		if(!dataType) dataType = "html";
		queue.push({
			api : api,
			callback : callback,
			dataType : dataType
		});
		if(!isProcessing){
			callNext();
			isProcessing = true;
		}
	}
	// this.prepend = function(api, callback){
		// queue.unshift({
			// api : api,
			// callback : callback
		// });
		// if(!isProcessing){
			// callNext();
			// isProcessing = true;
		// }
	// }
	function callNext(){
		var fn = function(res){
			queue[0].callback(res);
			queue.shift();
			if(queue.length == 0){
				isProcessing = false;
				$(self).trigger("completed");
			}else{
				callNext();
			}
		}
		$.ajax({
			url : queue[0].api,
			success : fn,
			dataType : queue[0].dataType,
			error : function(){
				console.warn(queue[0]);
				fn();
			}
		});
	}
}

function urldecode(str) {
   return decodeURIComponent((str+'').replace(/\+/g, '%20'));
}

function YTEpisode(data){
	var ep, te;
	var len = 0;
	var len = 1;
	
	this.data = data;
	this.programs = Array();
	this.currentIndex = 0;
	this.duration = 0;
	this.durations = Array();
	this.positions = Array();
	this.lineup = Array();
	this.titleCards = [];
	
	
	this.thumb = data["programThumbnailUrl"][0];
	this.published = parseInt(data["published"],10);
	this.name = data["programName"][0]; 
	this.durations = data["duration"];
	this.duration = data["duration"][0];
	this.id = data["id"];
	
	var sp = data["url1"][0].split("v=");
	var vid = sp[1];
	var sp1 = vid.split(";"); 
	vid = sp1[0];
	
	if(sp1.length > 2){
		sp1.shift();
	}else{
		sp1 = [0, this.duration];
	}
	
	pg = {
		id : "yt" + vid,
		name : data["programName"][0],
		thumb : data["programThumbnailUrl"][0],
		url : data["url1"][0],
		videoId : vid,
		startSeconds : parseInt(sp1[0]),
		endSeconds : parseInt(sp1[1]),
		titleCards : {
			"begin" : false,
			"end" : false
		},
		duration : this.duration
	}
	this.programs.push(pg);
	
	this.lineup = [{
		"type" : "program",
		"duration" : this.duration,
		"position" : 0,
		"content" : pg
	}]
}

function NNEpisode(data, channel){
	
	var ep, te;
	var len = 0;
	var len = 1;
	var _try = function(a, b){ try{	return eval(a);	}catch(e){ return b ? b : false;}};
	this.id;
	this.name;
	this.data = data;
	this.programs = Array();
	this.currentIndex = 0;
	this.thumb;
	this.durations = Array();
	this.positions = Array();
	this.lineup = Array();
	this.thumb = data["programThumbnailUrl"][0];
	this.published = parseInt(data["published"],10);
	this.name = data["programName"][0]; 
	this.durations = data["duration"];
	this.duration = this.durations[0];
	
	//will add title card durations to this
	if(this.duration == "" || this.duration == 0){
		this.duration = 60;
		for(var i = 0; i<this.durations.length; i++){
			this.durations[i] = 60;
		}
	}
	
	this.id = _try('data["programId"]', '');
	
	if($.isArray(data["url1"]) && data["url1"][0] == ""){
		
		data["programName"].shift();
		data["url1"].shift();
		data["programType"].shift();
		data["programThumbnailUrl"].shift();
		this.durations.shift();
		
		len = data["url1"].length;
	}
	
	for(var i = 0; i<len; i++){
		
		var sp = data["url1"][i].split("v=");
		var vid = sp[1];
		var sp1 = vid.split(";");
		var l = sp1.length;
		 
		vid = sp1[0];
		
		if(sp1.length > 2){
			sp1.shift();
		}else{
			sp1 = [0, this.durations[i]];
		}
		
		pg = {
			id : "ep" + vid,
			name : data["programName"][i],
			thumb : data["programThumbnailUrl"][i],
			url : data["url1"][i],
			videoId : vid,
			startSeconds : parseInt(sp1[0]),
			endSeconds : parseInt(sp1[1]),
			titleCards : getTitleCards(data, i),
			duration : this.durations[i]
		}
		this.programs.push(pg);
	}
	lineupPrograms(this);
	
	var poi = data["poi"] == undefined ? [] : data["poi"];
	var pos, n = 0;
	var isNum = function (num) {return (num >=0 || num < 0);}
	
	
	for(i = 0; i<poi.length; i++){
		pos = parseInt(poi[i].subEpisodePos, 10);
		n = 0;
		if(isNum(pos)){
			pos-=1;
			for(var j = 0; j<this.lineup.length; j++){
				if(this.lineup[j].type == "program"){
					if(n == pos){
						this.lineup[j].poi.push(poi[i]);
					}
					n++;
				}
			}
		}
	}
}

function lineupPrograms(ref){
	
	var len = ref.programs.length*3;
	var n = 0;
	var positions = Array();
	
	/** expand the array to fit title cards, 
	 * videos are in the middle of every 3 items */
	for(i = 0; i<len; i++){
		if(i%3 == 1){
			j = Math.ceil(i/3) -1;
			ref.lineup[i] = {
				type : "program",
				position : 0,
				duration : ref.durations[j],
				content : ref.programs[j],
				poi : []
			};
		}else{
			j = Math.floor(i/3);
			ref.lineup[i] = {
				type : "",
				position : 0,
				duration : 0,
				content : null,
				poi : []
			};
		}
		positions[i] = 0;
	}
	
	ref.titleCards = [];
	for(i = 0; i<ref.programs.length; i++){
		if(ref.programs[i].titleCards.begin){
			ref.titleCards.push(ref.programs[i].titleCards.begin);
		}
		if(ref.programs[i].titleCards.end){
			ref.titleCards.push(ref.programs[i].titleCards.end);
		}
	}
	
	var begin, end, card, p;
	for(i = 0; i<ref.titleCards.length; i++){
		card = ref.titleCards[i];
		if(card.subepisode){
			n = parseInt(card.subepisode, 10) -1;
			begin = n * 3;
			end = begin + 2;
			if(card.type == "begin"){
				n = begin;
			}else{
				n = end;
			}
			ref.lineup[n] = {
				type : "titleCard",
				position : 0,
				duration : parseInt(card.duration,10),
				content : card
			};
		}
	}
	
	//remove 0 durations
	for(i = ref.lineup.length-1; i>-1; i--){
		if(ref.lineup[i].duration == 0){
			ref.lineup.splice(i,1);
		}
	}
	
	p = 0;
	if(ref.lineup.length>1){
		for(i = 0; i<ref.lineup.length; i++){
			ref.positions.push(p);
			ref.lineup[i].position = p;
			p += parseInt(ref.lineup[i].duration,10);
		}
	}
}

function getTitleCards(data, index){
	
	var cards = {
		"begin" : false,
		"end" : false
	};
	if(!data["titleCard"]){
		return cards;
	}
	
	for(var i = 0; i<data["titleCard"].length; i++){
		var j = parseInt(data["titleCard"][i]["subepisode"], 10) - 1;
		if(j == index){
			if(data["titleCard"][i]["type"] == "begin"){
				cards["begin"] = data["titleCard"][i];
			}else{
				cards["end"] = data["titleCard"][i];
			}
		}
	}
	return cards;
}

function toSeconds(str){
	if(!str) return 0;
	if(str == 0) return 0;
	var arr = str.split(":");
	for(var i = 0; i<arr.length; i++){
		arr[i] = parseInt(arr[i], 10);
	}
	if(arr.length ==  1){
		return arr[0];
	}
	if(arr.length == 2){
		return arr[0]*60 + arr[1];
	}else if(arr.length == 3){
		return arr[0]*3600 + arr[1]*60 + arr[2]
	}
}


var requestCounter = 1;
function APIRequest(obj){
	for(var i in obj){
		this[i] = obj[i];
	}
	var self = this;
	function onConstructed(){
		self.id = requestCounter;
		requestCounter++;
	}
	onConstructed();
}
Array.prototype.move = function (old_index, new_index) {
    while (old_index < 0) {
        old_index += this.length;
    }
    while (new_index < 0) {
        new_index += this.length;
    }
    if (new_index >= this.length) {
        var k = new_index - this.length;
        while ((k--) + 1) {
            this.push(undefined);
        }
    }
    this.splice(new_index, 0, this.splice(old_index, 1)[0]);
};

function APIRequestLoader(sec){
	
	if(!sec) sec = 1500;
	
	var queue = [];
	var interval;
	var self = this;
	var max = 3;
	var counter = 0;
	var delay = sec;
	var isProcessing = false;
	var paused = false;
	
	this.append = function(req){
		queue.push(req);
		
		if(!isProcessing && !paused){
			this.start();
		}
	}

	
	this.prepend = function(req){
		queue.unshift(req);
		
		if(!isProcessing && !paused){
			this.start();
		}
	}
	
	this.moveToTop = function(id){
		for(var i = 0; i<queue.length; i++){
			if(queue[i].id == id){
				queue.move(i, 0);
				break;
			}
		}
		callNext();
	}
	
	this.pause = function(){
		this.stop();
		paused = true;
	}
	
	this.resume = function(){
		this.start();
		paused = false;
	}	
	
	this.start = function(){
		
		if(queue.length == 0) return;
		
		isProcessing = true;
		clearInterval(interval);
		interval = setInterval(callNext, delay);
	}
	
	this.stop = function(){
		isProcessing = false;
		clearInterval(interval);
	}
	
	function callNext(){
		
		if(queue.length == 0){
			return
		}
			
		var action = queue[0];
		// action.success = function(data) {
			// console.debug('ajax success by loader ');
			// console.debug(data);
		// };
		$.ajax(action);
		
		queue.shift();
		if(queue.length == 0){
			self.stop();
			$(self).trigger("completed");
		}
	}
}

String.prototype.toHHMMSS = function () {
    var sec_num = parseInt(this, 10); // don't forget the second parm
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    if (hours   < 10) {hours   = "0"+hours;}
    if (minutes < 10) {minutes = "0"+minutes;}
    if (seconds < 10) {seconds = "0"+seconds;}
    var time    = hours+':'+minutes+':'+seconds;
    return time;
}
// var arr = data.split("--\n");
// var episodes = arr[1].split("\n");
// var arr = [];

// if(episodes.length > 0){
	// for(i = 0; i<episodes.length; i++){
		// try{
// 				
			// tmp = episodes[i].split("\t");
// 				
			// //get youtube ids from url1
			// var urls = tmp[8].split("|");
			// var ids = [], vid, a;
			// for(var j = 0; j<urls.length; j++){
				// a = urls[j].split("v=");
				// if(a.length>1){
					// vid = a[1];
					// ids.push(vid);
				// }
			// }
			// arr.push({
				// channelId : tmp[0],
				// programId : tmp[1],
				// programNames : tmp[2] ? tmp[2].split("|") : "",
				// descriptions : tmp[3] ? tmp[3].split("|") : "",
				// programTypes : tmp[4] ? tmp[4].split("|") : "",
				// durations : tmp[5].split("|"),
				// thumbnailUrls : tmp[6].split("|"),
				// largeThumbnailUrls : tmp[7].split("|"),
				// url1 : tmp[8].split("|"),
				// url2 : "",
				// url3 : "",
				// url4 : "",
				// ytVideoIds : ids,
				// timestamp : tmp[12],
				// reserved : "",
				// titlecard : tmp[14]
			// });
		// }catch(e){
// 				
			// console.warn(episodes[i]);
			// return [];
		// }
	// }
// }
