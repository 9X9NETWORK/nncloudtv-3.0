var arr, result, tmp, card, item;
function ProgramInfoResponse(data){
	
	try{
		arr = Array();
		data = data.trim();
		arr = data.split("\r\n--\r\n");
		this.success = arr[0].split("\t")[1] == "SUCCESS";
		this.data = Array();
		arr = arr[1].split("\r\n");
		for(var i = 0; i<arr.length; i++){
			tmp = arr[i].trim().split("\t");
			item = new ProgramItem(tmp);
			this.data.push(item);
		}
	}catch(e){
		console.log("ProgramInfoResponse::parse error");
	}
}


function ProgramItem(data){
	
	if(data.length>0) this.channelId = data[0];
	if(data.length>1) this.programId = data[1]; 
	if(data.length>2) this.programName = data[2].split("|");
	if(data.length>3) this.description = data[3].split("|");
	if(data.length>4) this.programType = data[4].split("|");
	if(data.length>5) this.duration = data[5].split("|");
	if(data.length>6) this.programThumbnailUrl = data[6].split("|");
	if(data.length>7) this.programLargeThumbnailUrl = data[7].split("|");
	if(data.length>8) this.url1 = data[8].split("|");
	if(data.length>9) this.url2 = data[9].split("|");
	if(data.length>10) this.url3 = data[10].split("|");
	if(data.length>11) this.url4 = data[11].split("|");
	if(data.length>12) this.published = data[12];
	if(data.length>13) this.reversed = data[13];
	if(data.length>14) this.titleCard =  createTitleCards(data[14]);
}

function TitleCard(data){
	
	data = data.split("\n");
	var key, val, p;
	for(var i = 0; i<data.length; i++){
		p = data[i].split(":");
		
		if(p.length == 2){
			key = p[0].trim();
			val = p[1].trim();
			
			console.debug(key);
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


function urldecode(str) {
   return decodeURIComponent((str+'').replace(/\+/g, '%20'));
}