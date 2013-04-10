<!DOCTYPE HTML>
<html lang="en-US">
<head>
	<meta charset="UTF-8">
	<title>9x9 Playback</title>
	<script src="http://9x9ui.s3.amazonaws.com/scripts/swfobject.js" type="text/javascript" charset="utf-8"></script>
	<!-- <script src="http://code.jquery.com/jquery-1.8.8.min.js"></script> -->
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/jquery-ui.js" type="text/javascript" charset="utf-8"></script>
	<script src="https://9x9ui.s3.amazonaws.com/9x9miniV23j/javascripts/jquery.titlecard.js" type="text/javascript" charset="utf-8"></script>
	<script src="http://9x9ui.s3.amazonaws.com/playback/NNComp.js" type="text/javascript" charset="utf-8"></script>
	<script src="http://9x9ui.s3.amazonaws.com/playback/main.js" type="text/javascript" charset="utf-8"></script>
	
	<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/playback/main.css" type="text/css" media="screen" title="no title" charset="utf-8"/>
</head>
<body>
	<div id="player-layer">
  
  <div id="player-holder">
    <div id="pause-layer">
      <p class="btn-hilite" id="btn-resume"><span>Resume playing</span></p>
      <p id="pause-hint"><span>Press <img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_9x9.png" class="icon-btn-9x9"> or <img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_m.png" class="icon-btn-m"> to go to Guide.</span></p>
    </div>
    <div id="video-layer">
    	
    </div>
  </div>
</div>

<div id="osd-layer">
	<div id="vline"></div>
	<div id="osd-holder">
	  <div id="video-ctrl">
		<div id="bar">
			<ul id="sub-episode-points">
				<li><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_sub_ep.png" alt="" /></li>
				<li><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_sub_ep.png" alt="" /></li>
				<li><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_sub_ep.png" alt="" /></li>
				<li><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_sub_ep.png" alt="" /></li>
			</ul>
			<ul id="title-card-points">
				<li></li>
			</ul>
			<div id="progress"></div>
			<img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_knob.png" alt="" id="knob" />
		</div>
	</div>	
	  <div id="osd-wrap">
	  	<img src="" />
	  	<div id="titles">
	  		<h1>
	      		<span class="ch-pos"></span>
	      		<span class="ch-title"></span>
	      		<span class="arrow">></span>
	      		<span class="ep-title"></span> 
	      		<span class="updated"></span>
	      	</h1>
	      	<h2 class="s-ep-title"><span class="updated"></span></h2>
	  	</div>
	  </div>
	  <!-- <p id="ch-info"><span class="head">Ch <strong>1-1</strong>: </span><span id="ch-title">TEDtalks</span></p>
	  <p id="ep-info"><span class="head">Episode: </span><span id="ep-title">Louie Schwartzberg's TEDtalk on "Gratitude" ~ Enjoy ;) [HD]</span><span class="dash"> &#8212; </span><span id="ep-age">1 month ago</span></p> -->
	  <p id="player-hint">
	  	<span>Press 
	  		<img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_9x9.png" class="icon-btn-9x9"> or 
	  		<img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_m.png" class="icon-btn-9x9"><br>to go to Guide.
	  	</span>
	  </p>
	</div>
	<div id="volume-layer">
	  <div id="volume-holder">
	    <p id="volume-down"><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_volume_down.png" title="Volume Down"></p>
	    <ul id="volume-bars">
	    	<li class="on"></li>
	    	<li class="on"></li>
	    	<li class="on"></li>
	    	<li class="on"></li>
	    	<li></li>
	    	<li></li>
	    	<li></li>
	    	<li></li>
	    	<li></li>
	    	<li></li>
	    </ul>
	    <p id="volume-up"><img src="http://9x9ui.s3.amazonaws.com/9x9miniV23j/images/btn_volume_up.png" title="Volume Up"></p>
	  </div>
	</div>
</div>
</body>
</html>