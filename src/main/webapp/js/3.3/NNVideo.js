function NNProgram(data){
	
	this.init = function(){
		this.data = data;
		this.name = data["name"];
		this.thumb = data["thumb"];
		this.type = data["type"];
		this.url = data["url"];
		this.videoId = data["videoId"];
		this.startSeconds = data["startSeconds"];
		this.endSeconds = data["endSeconds"];
		this.titleCards = data["titleCards"];
		this.duration = this.endSeconds - this.startSeconds;
		this.position = 0;
	}
	this.init();
}
