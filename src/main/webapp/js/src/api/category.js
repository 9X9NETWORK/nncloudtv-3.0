

//lang "zh" or "us";
function CategoryList(data){
	var arr, tmp;
	try{
		arr = Array();
		data = data.trim();
		arr = data.split("\n--\n");
		this.success = arr[0].split("\t")[1] == "SUCCESS";
		this.items = [];
		if(arr.length>2){
			arr = arr[2].split("\n");
			if(arr[arr.length-1] == "--"){
				arr.pop();
			}
			for(var i = 0; i<arr.length; i++){
				tmp = arr[i].trim().split("\t");
				item = {
					id : tmp[0],						
					name : tmp[1],
					count : tmp[2],
					lang : tmp[3]
				};
				this.items.push(item);
			}
		}
	}catch(e){
		this.success = false;
		console.error("CategoryList::parse error");
	}
	console.log(this);
}


