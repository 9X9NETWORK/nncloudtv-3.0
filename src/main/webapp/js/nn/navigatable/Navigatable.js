/**
 * @author <a href="mailto:yifan.9x9@gmail.com">Yi Fan Liao</a>
 */

/**
 * @function
 * @param {string} name Object name.
 * @param {string} domElement DOM element selector string.
 * @param {object} options Options.
 */
function Navigatable(name, domElement, options){

	var self = this;

	this.name = name;
	this.currentIndex = 0;
	this.context = null;

	this.domElement = domElement;
	this.$domElement = $(domElement);

	if (!!options) {
		this.upNode = options.upNode || null;
		this.bottomNode = options.bottomNode || null;
		this.leftNode = options.leftNode || null;
		this.rightNode = options.rightNode || null;

		this.onSelected = options.onSelected || function(){};
		this.onFocus = options.onFocus || this.onFocus;
		this.onBlur = options.onBlur || this.onBlur;
		this.onExit = options.onExit || this.onExit;
		this.onOpen = options.onOpen || this.onOpen;
		this.onClose = options.onClose || this.onClose;

		this.parentNode = options.parentNode || null;
	}
	this.childNodes = [];

	this.focusedNode = null;

	this.$domElement.on('mouseup', function(){
		if (!!self.parentNode) {
			self.parentNode.focusedNode = self;
			self.onSelected();
		}
	});
}

Navigatable.prototype.selectLeft = function(){
	if (!!this.focusedNode.leftNode) {
		this.focusedNode.onBlur();
		this.focusedNode.leftNode.rightNode = this.focusedNode;
		this.focusedNode = this.focusedNode.leftNode;
		this.focusedNode.onFocus();
	}
};

Navigatable.prototype.selectRight = function(){
	if (!!this.focusedNode.rightNode) {
		this.focusedNode.onBlur();
		this.focusedNode.rightNode.leftNode = this.focusedNode;
		this.focusedNode = this.focusedNode.rightNode;
		this.focusedNode.onFocus();
	}
};

Navigatable.prototype.selectUp = function(){
	if (!!this.focusedNode.upNode) {
		this.focusedNode.onBlur();
		this.focusedNode.upNode.bottomNode = this.focusedNode;
		this.focusedNode = this.focusedNode.upNode;
		this.focusedNode.onFocus();
	}
};

Navigatable.prototype.selectDown = function(){
	if (!!this.focusedNode.bottomNode) {
		this.focusedNode.onBlur();
		this.focusedNode.bottomNode.upNode = this.focusedNode;
		this.focusedNode = this.focusedNode.bottomNode;
		this.focusedNode.onFocus();
	}
};

Navigatable.prototype.select = function(){
	this.focusedNode.onBlur();
	this.focusedNode.onSelected();
	return this;
};

Navigatable.prototype.hide = function(){
	var $parent = this.$domElement.parent();
	// $parent.hide();
	$parent.parent().hide();
	this.onClose();
};

Navigatable.prototype.show = function(onExitCallback){
	var $parent = this.$domElement.parent();
	$parent.parent().show();
	this.onOpen();
	$parent.css('left', 'calc(50% - '+$parent.width()/2+'px)');
	$parent.css('top', 'calc(50% - '+$parent.height()/2+'px)');
	
	$(".overlay-bg").fadeIn(500);

	if (typeof onExitCallback === 'function') {
		this.onExitCallback = onExitCallback;
	}
	else if (!!onExitCallback){
		throw 'Argument is not a function.';
	}
};

Navigatable.prototype.exit = function(option){
	
	this.hide();
	$(".overlay-bg").fadeOut(500);
	
	if (typeof this.onExit === 'function') {
		this.onExit(option);
	}

	if (typeof this.onExitCallback === 'function') {
		this.onExitCallback(option);
	}
};

Navigatable.prototype.init = function(onExitCallback){
	if (typeof onExitCallback === 'function') {
		this.onExitCallback = onExitCallback;
	}
	else if (!!onExitCallback){
		throw 'Argument is not a function.';
	}
};

// Runs on mouse clicked enter pressed.
Navigatable.prototype.onSelected = function() {};

Navigatable.prototype.onFocus = function() {
	this.$domElement.addClass('on');
	this.$domElement.focus();
};

Navigatable.prototype.onBlur = function() {
	this.$domElement.removeClass('on');
	this.$domElement.blur();
};

Navigatable.prototype.onOpen = function(){
	this.$domElement.show();
};

Navigatable.prototype.onClose = function(){
	this.$domElement.hide();
};

Navigatable.prototype.onExit = function(){};

Navigatable.prototype.onExitCallback = function(){};

Navigatable.prototype.constructor = Navigatable;
