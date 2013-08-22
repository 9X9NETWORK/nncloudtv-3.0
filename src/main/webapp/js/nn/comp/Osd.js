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