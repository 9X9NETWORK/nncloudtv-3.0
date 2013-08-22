function NNProgressBar(){
	
	var self = this;
	var timeout;
	var programs;
	var titleCards;
	var interval;
	
	function onDragStop(){
		
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
	
	this.episode;
	this.duration = 0;
	this.seconds = 0;
	this.load = function(episode){
		
		this.destroy();
		
		programs = episode.programs;
		titleCards = episode.titleCards;
		this.episode = episode;
		this.duration = episode.duration;
		
		this.build();
		this.start();
	}
	
	this.setSeconds = function(sec){
		
		this.seconds = sec;
		this.update();
	}
	
	this.pause = function(){
		clearInterval(interval);
	}
	
	this.start = function(){
		clearInterval(interval);
		interval = setInterval(function(){
			self.seconds += 0.5;
			self.update();
		},500);
	}
	
	this.update = function(){
		var percent = (this.seconds/this.duration)*100;
		$("#progress").width(percent + "%");
		$("#knob").css("left", (this.seconds/this.duration) * $("#bar").width() - ($("#knob").width()/2) );
	}
	
	this.build = function(){
		
		if(!programs) return;
		if(programs.length == 0){
			return;
		}
		var li = $('<li style="left: 0%;"><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_sub_ep.png"></li>');
		var point;
		var lineups = this.episode.lineup;
		var lineup, type;
		
		for(var i = 0; i<lineups.length; i++){
			lineup = lineups[i];
			type = lineup.type == "titleCard" ? "titleCard" : "video";
			point = li.clone().addClass(type).css("left", (lineup.position/this.duration)*100 + "%");
			if(type == "titleCard"){
				point.css("width", (lineup.duration/this.duration)*100 + "%");
			}
			$("#sub-episode-points").append(point);
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
	
	this.destroy = function(){
		
		this.duration = 0;
		this.seconds = 0;
		programs = Array();
		$("#sub-episode-points li").remove();
		clearTimeout(timeout);
		clearInterval(interval);
	}
}