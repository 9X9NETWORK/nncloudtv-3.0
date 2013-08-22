function Controllable(name){
	this.name = name;
	this.currentIndex = 0;
	this.context = null;
}
Controllable.prototype.selectLeft = function(){
	
}
Controllable.prototype.selectRight = function(){
	
}
Controllable.prototype.selectUp = function(){
	
}
Controllable.prototype.selectDown = function(){
	
}
Controllable.prototype.select = function(){
	
}
Controllable.prototype.hide = function(){
	
}
Controllable.prototype.show = function(){
	
}

Controllable.prototype.exit = function(){
	
}

Controllable.prototype.init = function(){
	
}

Controllable.prototype.f1 = function(){
	
}

Controllable.prototype.f6 = function(){
	
}

Controllable.prototype.constructor = Controllable;


function PopUpManager(){
	
	this.init = function(){
		$("#overlay-layer>div").hide();
		$("#overlay-layer>div>div").hide();
	}
	this.init();
}
