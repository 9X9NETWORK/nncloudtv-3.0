(function($){
	
	var layers;
	var methods = 
	{
		init:function()
		{
		},
	};
	
	$.fn.NNPlayer = function( method ) 
	{
	    if ( methods[method] ) 
	    {
	      return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
	    } 
	    else if ( typeof method === 'object' || ! method ) 
	    {
	      return methods.init.apply( this, arguments );
	    } 
	    else 
	    {
	      $.error( '' );
	    }   
	};
	
    return $();
})(jQuery);
function NNProgressBar(data){
	
}
