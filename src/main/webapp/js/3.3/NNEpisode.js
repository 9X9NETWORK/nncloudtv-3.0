function NNEpisode(data, nature){
	this.id;
	this.name;
	this.data = data;
	this.programs = Array();
	this.currentIndex = 0;
	this.lineupIndex = 0;
	this.thumb;
	this.duration = 0;
	this.durations = Array();
	this.positions = Array();
	this.lineup = Array();
	this.init = function(){
		
		if(nature == 8){
			this.parseN8Data();
		}else if(nature == 6){
			this.parseN6Data();
		}else if(nature == 4){
			this.parseN4Data();
		}
		else{
			this.parseData();
		}
		
		if(!data["titleCard"]){
			this.titleCards = Array();
		}else{
			this.titleCards = data["titleCard"];
		}
		
		this.getDuration();
		this.lineUpDurations();
	}
	
	this.next = function(){
		this.currentIndex++;
		if(this.currentIndex == this.programs.length){
			this.currentIndex = 0;
			return false;
		}
		$(document).trigger("programChanged", [this.currentProgram()]);
		return true;
	}
	
	this.prev = function(){
		this.currentIndex--;
		if(this.currentIndex == -1){
			this.currentIndex = 0;
			return false;
		}
		$(document).trigger("programChanged", [this.currentProgram()]);
		return this.currentProgram();
	}
	
	this.reset = function(){
		this.currentIndex = 0;
	}
	
	this.findProgramPosition = function(program){
		for(var i = 0; i<this.lineup.length; i++){
			if(this.lineup[i].content == program){
				return this.lineup[i].position;
			}
		}
	}
	
	this.currentProgram = function(){
		return this.getProgramByIndex(this.currentIndex);
	}
	this.nextProgram = function(){
		return this.getProgramByIndex(this.currentIndex+1);
	}
	this.getProgramByIndex = function(i){
		if(this.programs.length == 0 || i<0 || i> this.programs.length-1){
			return false;
		}else{
			return this.programs[i];
		}
	}
	this.getDuration = function(){
		for(var i = 0; i<this.programs.length; i++){
			this.duration += this.programs[i].duration;
		}
		for(i = 0; i<this.titleCards.length; i++){
			this.duration += parseInt(this.titleCards[i]["duration"],10);
		}
	}
	this.parseData = function(){
		
		var ep, te;
		var len = 0;
		if(data["channelId"]){
			this.id = data["programId"];
			if(data["programName"]){
				this.name = data["programName"][0];
				len = data["programName"].length;
			}else{
				this.name = data["name"];
			}
			if(data["programThumbnailUrl"]){
				this.thumb = data["programThumbnailUrl"][0];
			}else{
				this.thumb = data["thumb"];
			}
			this.published = parseInt(data["published"],10);
			for(var i = 1; i<len; i++){
				pg = new NNProgram({
					name : data["programName"][i],
					thumb : data["programThumbnailUrl"][i],
					type : data["programType"][i],
					url : data["url1"][i],
					videoId : data["url1"][i].slice(
						data["url1"][i].indexOf("=")+1,
						data["url1"][i].indexOf(";")
					),
					startSeconds : parseInt(data["url1"][i].split(";")[1]),
					endSeconds : parseInt(data["url1"][i].split(";")[2]),
					titleCards : this.getTitleCards(i)
				});
				this.programs.push(pg);
			}
		}else{
			this.id = false;
			this.name = data["name"];
			this.thumb = data["thumb"];
			this.published = parseInt(data["timestamp"],10);
			
			pg = new NNProgram({
				name : data["name"],
				thumb : data["thumb"],
				type : false,
				url : data["url1"],
				videoId : data["url1"].split("=")[1],
				startSeconds : 0,
				endSeconds : this.toSeconds(data["duration"]),
				titleCards : {
					"begin" : false,
					"end" : false
				}
			});
			this.programs.push(pg);
		}
	}
	this.parseN6Data = function(){
		var ep, te;
		var len = 0;
		if(data["channelId"]){
			this.id = data["programId"];
			this.thumb = data["programThumbnailUrl"][0];
			this.published = parseInt(data["published"],10);
			this.name = data["programName"][0];
			var len = data["programName"].length;
			for(var i = 1; i<len; i++){
				
				if(data["url1"][i]){
					// console.debug(i);
					// console.debug(data["programName"]);
					// console.debug(data["url1"]);
					var vid = data["url1"][i].split("v=")[1];
					vid = vid.split(";")[0];
					pg = new NNProgram({
						name : data["programName"][i],
						thumb : data["programThumbnailUrl"][i],
						type : data["programType"][i],
						url : data["url1"][i],
						videoId : vid,
						startSeconds : parseInt(data["url1"][i].split(";")[1]),
						endSeconds : parseInt(data["url1"][i].split(";")[2]),
						titleCards : this.getTitleCards(i)
					});
					this.programs.push(pg);
				}
			}
		}else{
			this.id = false;
			this.name = data["name"];
			this.thumb = data["thumb"];
			this.published = parseInt(data["timestamp"],10);
			
			pg = new NNProgram({
				name : data["name"],
				thumb : data["thumb"],
				type : false,
				url : data["url1"],
				videoId : data["url1"].split("=")[1],
				startSeconds : 0,
				endSeconds : this.toSeconds(data["duration"]),
				titleCards : {
					"begin" : false,
					"end" : false
				}
			});
			this.programs.push(pg);
		}
	}
	this.parseN8Data = function(){
		var ep, te;
		var len = 0;
		
		if(data["channelId"]){
			this.id = data["programId"];
			this.name = data["programName"][0];
			
			if(!data["programThumbnailUrl"]){
				//console.debug(data);
			}
			this.thumb = data["programThumbnailUrl"][0];
			this.published = parseInt(data["published"],10);
			var len = data["programName"].length;
			for(var i = 0; i<len; i++){
				if(data["programName"][i] != ""){
					var vid = data["url1"][i].split("v=")[1];
					vid = vid.split(";")[0];
					pg = new NNProgram({
						name : data["programName"][i],
						thumb : data["programThumbnailUrl"][i],
						type : data["programType"][i],
						url : data["url1"][i],
						videoId : vid,
						startSeconds : parseInt(data["url1"][i].split(";")[1]),
						endSeconds : parseInt(data["url1"][i].split(";")[2]),
						titleCards : this.getTitleCards(i)
					});
					this.programs.push(pg);
				}
			}
		}else{
			this.id = false;
			this.name = data["name"];
			this.thumb = data["thumb"];
			this.published = parseInt(data["timestamp"],10);
			
			pg = new NNProgram({
				name : data["name"],
				thumb : data["thumb"],
				type : false,
				url : data["url1"],
				videoId : data["url1"].split("=")[1],
				startSeconds : 0,
				endSeconds : this.toSeconds(data["duration"]),
				titleCards : {
					"begin" : false,
					"end" : false
				}
			});
			this.programs.push(pg);
		}
	}
	
	this.parseN4Data = function(){
		var ep, te;
		var len = 0;
		
		if(data["channelId"]){
			this.id = data["episodeId"];
			this.name = data["name"];
			this.thumb = data["thumb"][0];
			this.published = parseInt(data["timestamp"],10);
			pg = new NNProgram({
				name : data["name"],
				thumb : data["thumb"],
				type : data["type"],
				url : data["url1"],
				videoId : data["url1"].split("v=")[1],
				startSeconds : 0,
				endSeconds : parseInt(data["duration"],10),
				titleCards : this.getTitleCards(i)
			});
			this.programs.push(pg);
			
		}else{
			this.id = false;
			this.name = data["name"];
			this.thumb = data["thumb"];
			this.published = parseInt(data["timestamp"],10);
			
			pg = new NNProgram({
				name : data["name"],
				thumb : data["thumb"],
				type : false,
				url : data["url1"],
				videoId : data["url1"].split("=")[1],
				startSeconds : 0,
				endSeconds : this.toSeconds(data["duration"]),
				titleCards : {
					"begin" : false,
					"end" : false
				}
			});
			this.programs.push(pg);
		}
	}
	
	this.toSeconds = function(str){
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
	this.getTitleCards = function(index){
		
		var cards = {
			"begin" : false,
			"end" : false
		};
		if(!data["titleCard"]) return cards;
		
		for(var i = 0; i<data["titleCard"].length; i++){
			if(data["titleCard"][i]["subepisode"] == index){
				if(data["titleCard"][i]["type"] == "begin"){
					cards["begin"] = data["titleCard"][i];
				}else{
					cards["end"] = data["titleCard"][i];
				}
			}
		}
		return cards;
	}
	this.lineUpDurations = function(){
		
		var len = this.programs.length*3;
		var n = 0;
		var positions = Array();
		
		/** expand the array to fit title cards, 
		 * videos are in the middle of every 3 items
		 */
		for(i = 0; i<len; i++){
			if(i%3 == 1){
				j = Math.ceil(i/3) -1;
				this.durations[i] = parseInt(this.programs[j].duration,10);
				this.lineup[i] = {
					type : "program",
					position : 0,
					duration : this.durations[i],
					content : this.programs[j]
				};
			}else{
				j = Math.floor(i/3);
				this.durations[i] = parseInt(0,10);
				this.lineup[i] = {
					type : "",
					position : 0,
					duration : 0,
					content : null
				};
			}
			positions[i] = 0;
		}
		
		var begin, end, card, p;
		for(i = 0; i<this.titleCards.length; i++){
			card = this.titleCards[i];
			n = parseInt(card.subepisode,10) -1;
			begin = n * 3;
			end = begin + 2;
			if(card.type == "begin"){
				n = begin;
			}else{
				n = end;
			}
			this.durations[n] = parseInt(card.duration,10);
			this.lineup[n] = {
				type : "titleCard",
				position : 0,
				duration : this.durations[n],
				content : card
			};
		}
		
		//remove 0 durations
		for(i = this.durations.length-1; i>-1; i--){
			if(this.durations[i] == 0 || isNaN(this.durations[i])){
				this.durations.splice(i,1);
				this.lineup.splice(i,1);
			}
		}
		
		p = 0;
		if(this.durations.length>1){
			for(i = 0; i<this.durations.length; i++){
				this.positions.push(p);
				this.lineup[i].position = p;
				p += this.durations[i]; 
			}
		}
	}
	this.init();
}

