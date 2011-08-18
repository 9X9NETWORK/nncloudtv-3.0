<!DOCTYPE html>
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="root" value="http://9x9ui.s3.amazonaws.com/9x9playerV68"/>

<!-- $Revision: 1853 $ -->

<!-- FB Sharing meta data -->
<meta name="title" content="${fbName}" />
<meta name="description" content="${fbDescription}" />

<link rel="image_src" href="${fbImg}" />

<link rel="stylesheet" href="${root}/stylesheets/main.css" />
<link rel="stylesheet" href="http://9x9ui.s3.amazonaws.com/contest/contest.css" />

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/jquery-ui.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.8/i18n/jquery-ui-i18n.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/all.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/swfobject.js"></script>
<script type="text/javascript" charset="utf-8" src="${root}/javascripts/jquery.swfobject.1-1-1.min.js"></script>
<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/scripts/flowplayer-3.2.4.min.js"></script>

<script type="text/javascript" charset="utf-8" src="http://9x9ui.s3.amazonaws.com/player11.js"></script>

<script type="text/javascript">
var analytz = false;
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-21595932-1']);
_gaq.push(['_setDomainName', '.9x9.tv']);
_gaq.push(['_trackPageview']);
function analytics()
  {
  if (!analytz)
    {
    log ('submitting analytics');
    (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
      })();
    setTimeout ("_gaq.push(['_trackEvent', 'NoBounce', '10 second ping'])", 10000);
    analytz = true;
    }
  }
</script>

<script type="text/javascript">
var brandinfo = "${brandInfo}";
</script>


<title>9x9.tv</title>

</head>

<body id="body" style="overflow: hidden">

<div id="fp1" style="width: 100%; height: 100%; z-index: 1; visibility: visible; position: absolute; left: 0; top: 0; overflow: hidden;">
  <a href="" style="display:block; width:100%;" id="player1" onClick="noop(this)"></a>

</div>

<div id="fp2" style="width: 100%; height: 100%; z-index: 2; visibility: visible; position: absolute; left: 0; top: 0; overflow: hidden;">
  <a href="" style="display:block; width:100%;" id="player2" onClick="noop(this)"></a>
</div>

<div id="yt1" style="width: 100%; height: 100%; z-index: 1; visibility: visible; position: absolute; left: 0; top: 0; overflow: hidden;">
  <div id="ytapiplayer1">
  </div>
</div>

<div id="yt2" style="width: 100%; height: 100%; z-index: 1; visibility: visible; position: absolute; left: 0; top: 0; overflow: hidden;">
  <div id="ytapiplayer2">
  </div>
</div>

<div id="yt3" style="width: 100%; height: 100%; z-index: 1; visibility: visible; position: absolute; left: 0; top: 0; overflow: hidden;">
  <div id="ytapiplayer3">
  </div>
</div>

<div id="ss" style="width: 100%; height: 100%; z-index: 4; visibility: visible; position: absolute; left: 0; top: 0; overflow: hidden; display: none">
</div>

<div id="blue" style="background: black; width: 100%; height: 100%; display: block; position: absolute; color: white">
</div>

<!--div id="notblue" style="width: 100%; display: none; position: absolute; top: 0; margin: 0; overflow: hidden"-->

  <div id="all-players" style="display: none; padding: 0; display: none">
    <div id="v" style="display: block; padding: 0">
      <video id="vvv" autoplay="false" preload="metadata" loop="false" height="100%" width="100%" volume="0"></video></div>

<div id="jw" style="width: 100%; height: 100%; display: none">
        <embed name="player1" id="player1"
            type="application/x-shockwave-flash"
            pluginspage="http://www.macromedia.com/go/getflashplayer"
            width="100%" height="100%"
            bgcolor="#FFFFFF"
            src="http://9x9ui.s3.amazonaws.com/scripts/player.swf"
            allowfullscreen="true"
            allowscriptaccess="always"
            wmode="transparent"
            flashvars="fullscreen=true&controlbar=none&mute=false&bufferlength=1&allowscriptaccess=always">
        </embed>
</div>
<!--div id="jw2" style="width: 100%; height: 100%">
        <embed name="player2" id="player2"
            type="application/x-shockwave-flash"
            pluginspage="http://www.macromedia.com/go/getflashplayer"
            width="100%" height="100%"
            bgcolor="#FFFFFF"
            src="http://9x9ui.s3.amazonaws.com/scripts/player.swf"
            allowfullscreen="true"
            allowscriptaccess="always"
            wmode="transparent"
            flashvars="fullscreen=true&controlbar=none&mute=false&bufferlength=1&allowscriptaccess=always">
        </embed>

</div-->

  </div>

<div id="ep-layer" style="display: none">
  <img src="${root}/images/ep_panel_off.png" id="ep-panel">
  <div id="ep-tip"></div>
  <div id="ep-container">
    <p id="ep-indicator"><span id="episodes1">Episodes: </span><span id="epNum"></span></p>
    <img src="${root}/images/arrow_right_off.png" id="arrow-right" style="display: none">
    <img src="${root}/images/arrow_left_off.png" id="arrow-left" style="display: none">
    <ul class="ep-list" id="ep-list"></ul>
    <div id="ep-meta"><p><span class="ch-title" id="ep-layer-ch-title"></span> - <span class="ep-title" id="ep-layer-ep-title"></span> - <span class="age" id="ep-age"></span> - <span class="duration" id="ep-length"></span></p></div>
  </div>
</div>

<ul class="set-options"> 
  <li class="on"><img src="${root}/images/icon_check.png" class="icon-check"><span>News</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Music</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Sports</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Movie Trailers</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Lifestyle</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Food & Wine</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Travel</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Local Deal</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Brands</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>My YouTube Favorites</span></li> 
  <li><img src="${root}/images/icon_check.png" class="icon-check"><span>Friends & Family</span></li> 
</ul> 

<div id="sg-layer" style="display: block">
  <div id="sg-holder">
  
    <div id="sg-header">
      <img src="${root}/images/beta.png" id="sg-beta">
      <img src="${root}/images/logo.png" id="sg-logo">
      <p id="slogan"><span>Your Personal Video Album</span></p>  
      <ul id="menu-list">
        <li id="btn-sgt"><img src="${root}/images/icon_sgt.png" id="icon-signin"><span><div id="btn-sg-txt">Smart Guide</div><span class="arrow"></span></span></li>
        <li id="btn-signin"><img src="${root}/images/icon_signin.png" id="icon-signin"><span><div id="btn-signin-txt">Sign in / Sign up</div><span class="arrow"></span></span></li>
        <li id="btn-about"><img src="${root}/images/icon_about.png" id="icon-about"><span><div id="btn-about-text">About Us</div><span class="arrow"></span></span></li>
        <li id="btn-help"><img src="${root}/images/icon_help.png" id="icon-help"><span><div id="btn-help-txt">Help</div><span class="arrow"></span></span></li>
      </ul>
    </div>
    
    <div id="sg-content">
      
      <div id="follow-elements">
        <p class="btn-big" id="btn-follow"><img src="${root}/images/icon_follow.png" id="icon-follow"><span>Follow These Channels</span></p>
        <p id="follow-hint"><span>Click to receive the latest episodes from these channels.</span></p>
      </div>

      <div id="branding-elements">
        <img src="${root}/images/logo_tzuchi.png" id="branding-logo" style="display: none">
      </div>
      
      <div id="sg-elements">
        <p id="sg-title"><span id="sg-user">Guest's</span><span id="btn-smart-guide">Smart Guide<img src="${root}/images/icon_sg.png" id="icon-sg"></span></p>
      </div>

      <div id="add-ch-elements">
        <p class="btn-big" id="btn-add-channels"><img src="${root}/images/icon_add.png" id="icon-add"><span>Add More Channels</span></p>
      </div>

      <div id="landing">

        <div id="set-view">

          <img src="${root}/images/bg_set.png" id="bg-folder">

          <p id="set-title"><span>9x9</span></p>

          <div id="tab-more" class="tab">

            <img src="${root}/images/tab_more_off.png" class="off">

            <img src="${root}/images/tab_more_on.png" class="on">

            <p><span>More Sets</span></p>

          </div>

          <div id="tab-add" class="tab">

            <img src="${root}/images/tab_more_off.png" class="off">

            <img src="${root}/images/tab_more_on.png" class="on">

            <p><span>Add More Sets</span></p>

          </div>

          <ul id="landing-grid">

          </ul>

        </div>

        <div id="ch-view">

          <div id="win-preview"><img src="${root}/images/icon_play.png" class="btn-preview"><img class="screenshot" id="screenshot"></div>

          <p id="fb-insert" style="display: none">

            <img id="fb-photo">

            <span id="fb-friend"></span>

            <span id="fb-recommend"></span>

          </p>
          <p id="ep-share">
            <span id="ep-sharetitle">Episode Title</span>
            <span id="ep-sharedesc">Episode Description</span>
          </p>

        </div>

      </div>


      <div id="sg-grid" class="x9" style="display: block">
        <img src="${root}/images/arrow_right_on.png" id="next-set">
        <img src="${root}/images/arrow_left_on.png" id="prev-set">
        <div id="sg-constrain">
          <div id="slider">
          </div>
        </div>
        <ul id="pagination"><li class="pdot" id="pdot-1"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-2"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-3"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-4"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-5"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-6"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-7"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-8"><img src="${root}/images/icon_pagination.png"></li><li class="pdot" id="pdot-9"><img src="${root}/images/icon_pagination.png"></li></ul>
      </div>

      <div id="btn-edit">
        <img src="${root}/images/btn_edit_off.png" id="bg-off"><img src="${root}/images/btn_edit_on.png" id="bg-on">
        <img src="${root}/images/icon_edit.png" id="icon-edit">
        <p id="edit-txt"><span>Edit</span></p>
      </div>
    
      <div id="channel-info">
        <p id="section-title"><span>Current Channel</span></p>
        <p id="channel-title"><span>Golden Drama</span></p>
        <p id="channel-description"><span>Last installment from my "Making Money from Podcasting" series...</span></p>
        <p>
          <span id="eps-number">Episodes: 12</span><br>
          <span id="updates">Updated: Today</span>
        </p>
      </div>
    </div>


    <div id="add-content">

      <div id="add-col">

        <h2>Add Channels</h2>

        <h3>from your own</h3>

        <ul id="private-list" class="option-list">

          <li id="myFacebook"><span>My Facebook</span></li>

          <li id="myPodcast"><span>My Podcast</span></li>

          <!--<li id="myiTunes"><span>My iTunes</span></li>-->

          <li id="myYouTube"><span>My YouTube</span></li>

          <!--<li id="myTwitter"><span>My Twitter</span></li>-->

        </ul>

        <h3>from 9x9</h3>

        <ul id="public-list" class="option-list">

          <li id="fSets"><span>Featured sets</span></li>

          <li id="fChannels"><span>Featured channels</span></li>

          <!--<li id="rSets"><span>Recommended sets</span></li>-->

          <!--<li id="rChannels"><span>Recommended channels</span></li>-->

          <li id="chDir"><span>Channel directory</span></li>

        </ul>

        <div id="search-box" style="visibility: hidden">

          <input name="" type="text" id="search-field" value="Search">

          <p id="icon-search">

            <img src="${root}/images/icon_search_off.png" id="icon-search-off"><img src="${root}/images/icon_search_on.png" id="icon-search-on">

          </p>

        </div><!-- End search-box -->

        

        <div id="search-filter">

          <ul id="filter-list">

            <li><span class="checkbox"><img src="${root}/images/search_check_off.png" class="search-check-off"><img src="images/search_check_on.png" class="search-check-on"></span><span>search <strong>Sets</strong></span></li>

            <li><span class="checkbox"><img src="${root}/images/search_check_off.png" class="search-check-off"><img src="images/search_check_on.png" class="search-check-on"></span><span>search <strong>Channels</strong></span></li>

            <li><span class="checkbox"><img src="${root}/images/search_check_off.png" class="search-check-off"><img src="images/search_check_on.png" class="search-check-on"></span><span>search <strong>Episodes</strong></span></li>

            <li class="on"><span class="checkbox"><img src="${root}/images/search_check_off.png" class="search-check-off"><img src="${root}/images/search_check_on.png" class="search-check-on"></span><span>search <strong>All</strong></span></li>

          </ul>

        </div><!-- End search-filter -->       

      </div><!-- End add-col -->

      

      <div id="myFacebook-input" class="input-content">

        <h2>Add Facebook Channels</h2>

        <div class="account-connector">

          <p><span>Connect to your Facebook account</span></p>

          <p class="btn-blue"><span>Connect</span></p>

        </div>

        <ul class="spliter"><li class="line"></li><li class="word"><span>OR</span></li><li class="line"></li></ul>

        <div class="url-input">

          <input name="" type="text" class="url-field" value="Enter Facebook Fan Page URL">

        </div>

        <div class="cate-selector">

          <p>Choose category (Please pick 1 to 3 categories):</p>

          <ul class="cate-list">

          </ul>

        </div>

        <div class="tag-input">

          <textarea name="" class="tag-field">Enter tags</textarea>

        </div>

      

      </div><!-- End facebook input -->

      

      <div id="myPodcast-input" class="input-content">

        <h2>Add Podcast Channels</h2>

        <div class="url-input">

          <input name="" type="text" class="url-field" value="Enter Podcast Channel URL">

        </div>

        <div class="cate-selector">

          <p>Choose category (Please pick 1 to 3 categories):</p>

          <ul class="cate-list">

          </ul>

        </div>

        <div class="tag-input">

          <textarea name="" class="tag-field">Enter tags</textarea>

        </div>

      

      </div><!-- End podcast input -->

      

      <div id="myYouTube-input" class="input-content">

        <h2>Add YouTube Channels</h2>

        <div class="url-input">

          <input name="" type="text" class="url-field" value="Enter YouTube Channel or Playlist URL">

        </div>

        <p class="note">* We don't accept URLs for single video</p>

        <div class="cate-selector">

          <p>Choose category (Please pick 1 to 3 categories):</p>

          <ul class="cate-list">

          </ul>

        </div>

        <div class="tag-input">

          <textarea name="" class="tag-field">Enter tags</textarea>

        </div>

      

      </div><!-- End youtube input -->

      

      <div id="submission">

        <ul>

          <li class="btn" id="btn-submit"><span>Submit</span></li><li class="btn" id="btn-return"><span>Return to Smart Guide</span></li>

        </ul>

        <p id="feedback"><img src="${root}/images/loading.gif"><span>Processing...</span></p>

      </div>

      

      <div id="chDir-input" class="input-content">

      

        <div id="dir-waiting">

          <img src="${root}/images/loading.gif">

          <p><span>One moment...</span></p>

         </div>

         

         <div id="ch-waiting">

          <img src="${root}/images/loading.gif">

          <p><span>One moment...</span></p>

         </div>

       

        <h2>Channel Directory</h2>

        <div id="cat-col">

          <ul id="cat-list">

          </ul>

        </div>

        <div id="ch-col">

          <img src="${root}/images/arrow_up.png" id="arrow-up"><img src="${root}/images/arrow_down.png" id="arrow-down">

          <div id="ch-constrain">

          <ul id="ch-list">

          </ul>

          </div>

        </div>

        <div id="preview-col">

          <div id="preview-box">

            <img src="${root}/images/icon_play.png" class="btn-preview">

            <img class="thumb">

          </div>

          <div id="preview-info">

            <p id="ch-title"><span>Where is my home</span></p>

            <p id="ch-meta">

              <span>Set: Da Ai</span>

              <span>Episodes: 12</span>

              <span>Updated: Today</span>

              <span>Curator: Da Ai</span>

            </p>

            <p class="btn-blue" id="btn-follow-cd"><img src="${root}/images/icon_follow.png" class="icon-follow"><span>Follow this channel</span></p>

            <p class="btn" id="btn-return-cd"><span>Return to Smart Guide</span></p>

            <p id="add-feedback"><img src="${root}/images/loading.gif"><span>Processing...</span></p>

          </div>

        </div>

        

      </div><!-- End channel directory -->

      

      <div id="fChannels-input" class="input-content">
        <div id="fch-waiting">
          <img src="${root}/images/loading.gif">

          <p><span>One moment...</span></p>
         </div>
         
        <h2>Add Featured Channels</h2>
        
        <div id="preview-col-fch">
          <div id="preview-box-fch">
            <img src="${root}/images/icon_play.png" class="btn-preview">
            <img src="${root}/thumb/13.jpg" class="thumb">
          </div>

          <div id="preview-info-fch">
            <p id="fch-title"><span>Where is my home</span></p>
            <p id="fch-meta">
              <span>Set: Da Ai</span>
              <span>Episodes: 12</span>
              <span>Updated: Today</span>
              <span>Curator: Da Ai</span>

            </p>
            <p class="btn-blue" id="btn-follow-fch"><img src="${root}/images/icon_follow.png" class="icon-follow"><span>Follow this channel</span></p>
            <p class="btn" id="btn-return-fch"><span>Return to Smart Guide</span></p>
            <p id="fch-feedback"><img src="${root}/images/loading.gif"><span>Processing...</span></p>
          </div>
        </div>
        
        <div id="fch-col">

          <img src="${root}/images/arrow_up.png" id="fch-arrow-up"><img src="${root}/images/arrow_down.png" id="fch-arrow-down">
          <div id="fch-constrain">
            <ul id="fch-list">
              <li class="on"><img src="${root}/thumb/13.jpg" class="thumb"><p><span>Where is my home</span></p></li>
              <li><img src="${root}/thumb/08.jpg" class="thumb"><p><span>I Love You Desiree Elyda</span></p></li>
              <li><img src="${root}/thumb/09.gif" class="thumb"><p><span>Alison Brie Humpday</span></p></li>
              <li><img src="${root}/thumb/14.jpeg" class="thumb"><p><span>Scarlett Johansson's top secret</span></p></li>
              <li><img src="${root}/thumb/20.jpg" class="thumb"><p><span>Bree Olson Wins the INTERNET!</span></p></li>
              <li><img src="${root}/thumb/09.gif" class="thumb"><p><span>Alison Brie Humpday</span></p></li>
              <li><img src="${root}/thumb/14.jpeg" class="thumb"><p><span>Scarlett Johansson's top secret</span></p></li>
              <li><img src="${root}/thumb/20.jpg" class="thumb"><p><span>Bree Olson Wins the INTERNET!</span></p></li>
              <li><img src="${root}/thumb/02.jpg" class="thumb"><p><span>Where is my home</span></p></li>
              <li><img src="${root}/thumb/08.jpg" class="thumb"><p><span>I Love You Desiree Elyda</span></p></li>
              <li><img src="${root}/thumb/09.gif" class="thumb"><p><span>Alison Brie Humpday</span></p></li>
              <li><img src="${root}/thumb/14.jpeg" class="thumb"><p><span>Scarlett Johansson's top secret</span></p></li>
              <li><img src="${root}/thumb/20.jpg" class="thumb"><p><span>Bree Olson Wins the INTERNET!</span></p></li>
              <li><img src="${root}/thumb/09.gif" class="thumb"><p><span>Alison Brie Humpday</span></p></li>
              <li><img src="${root}/thumb/20.jpg" class="thumb"><p><span>Bree Olson Wins the INTERNET!</span></p></li>
              <li><img src="${root}/thumb/02.jpg" class="thumb"><p><span>Where is my home</span></p></li>
              <li><img src="${root}/thumb/08.jpg" class="thumb"><p><span>I Love You Desiree Elyda</span></p></li>
              <li><img src="${root}/thumb/09.gif" class="thumb"><p><span>Alison Brie Humpday</span></p></li>
              <li><img src="${root}/thumb/14.jpeg" class="thumb"><p><span>Scarlett Johansson's top secret</span></p></li>
              <li><img src="${root}/thumb/20.jpg" class="thumb"><p><span>Bree Olson Wins the INTERNET!</span></p></li>
              <li><img src="${root}/thumb/09.gif" class="thumb"><p><span>Alison Brie Humpday</span></p></li>
            </ul>

          </div>
        </div>
        
      </div><!-- End featured channels -->


      <div id="fSets-input" class="input-content">
      
        <div id="fset-waiting">
          <img src="${root}/images/loading.gif">
          <p><span>One moment...</span></p>
        </div>
         
        <h2>Add Featured Sets</h2>
        
        <div id="preview-col-fset">        
          <div id="preview-box-fset">
            <img src="${root}/images/bg_set.png" class="bg-set">
            <p class="set-title"><span>Da Ai TV March Golden Selection</span></p>
            <ul>
              <li class="on"><img src="${root}/thumb/01.jpg" class="thumbnail"><img src="images/icon_play.png" class="btn-preview"></li>
              <li><img src="${root}/thumb/02.jpg" class="thumbnail"><img src="images/icon_play.png" class="btn-preview"></li>
              <li><img src="${root}/thumb/04.jpg" class="thumbnail"><img src="images/icon_play.png" class="btn-preview"></li>
              <li><img src="${root}/thumb/05.jpg" class="thumbnail"><img src="images/icon_play.png" class="btn-preview"></li>
              <li></li>
              <li></li>
              <li></li>
              <li></li>
              <li></li>
            </ul>
            <div id="preview-info-fset">

              <p id="ch-title-on"><span>Where is my home</span></p>
              <p id="fset-meta">
                <span>Episodes: 12</span>
                <span>Updated: Today</span>
                <span>Curator: Da Ai</span>
              </p>
            </div>

            <div id="preview-action-fset">
              <p class="btn-blue" id="btn-follow-fset"><img src="${root}/images/icon_follow.png" class="icon-follow"><span>Follow this set</span></p>
              <p class="btn" id="btn-return-fset"><span>Return to Smart Guide</span></p>
            </div>
            <p id="fset-feedback"><img src="${root}/images/loading.gif"><span>Processing...</span></p>
          </div>
        </div>

        
        <div id="fset-col">
          <img src="${root}/images/arrow_up.png" id="fset-arrow-up"><img src="${root}/images/arrow_down.png" id="fset-arrow-down">
          <div id="fest-constrain">
          <ul id="fset-list">
            <li class="on">
              <div class="fset">
                <img src="${root}/images/bg_fset.png" class="bg-fset">
                <ul class="fset-grid">
                  <li><img src="${root}/thumb/13.jpg"></li>
                  <li><img src="${root}/thumb/14.jpeg"></li>
                  <li><img src="${root}/thumb/15.jpg"></li>
                  <li><img src="${root}/thumb/16.jpg"></li>
                  <li><img src="${root}/thumb/18.jpg"></li>
                  <li><img src="${root}/thumb/20.jpg"></li>
                  <li><img src="${root}/thumb/abc.jpg"></li>
                  <li><img src="${root}/thumb/22.jpg"></li>
                  <li><img src="${root}/thumb/12.jpg"></li>
                </ul>

              </div>
              <p class="fset-title"><span>Da Ai TV March Set</span></p>
            </li>
            
            <li>
              <div class="fset">
                <img src="${root}/images/bg_fset.png" class="bg-fset">
                <ul class="fset-grid">
                  <li><img src="${root}/thumb/13.jpg"></li>
                  <li><img src="${root}/thumb/14.jpeg"></li>
                  <li><img src="${root}/thumb/15.jpg"></li>
                  <li><img src="${root}/thumb/16.jpg"></li>
                  <li><img src="${root}/thumb/18.jpg"></li>
                  <li><img src="${root}/thumb/20.jpg"></li>
                  <li><img src="${root}/thumb/abc.jpg"></li>
                  <li><img src="${root}/thumb/22.jpg"></li>
                  <li><img src="${root}/thumb/12.jpg"></li>
                </ul>

              </div>
              <p class="fset-title"><span>Da Ai TV March Set</span></p>
            </li>
            
            <li>
              <div class="fset">
                <img src="${root}/images/bg_fset.png" class="bg-fset">
                <ul class="fset-grid">
                  <li><img src="${root}/thumb/13.jpg"></li>
                  <li><img src="${root}/thumb/14.jpeg"></li>
                  <li><img src="${root}/thumb/15.jpg"></li>
                  <li><img src="${root}/thumb/16.jpg"></li>
                  <li><img src="${root}/thumb/18.jpg"></li>
                  <li><img src="${root}/thumb/20.jpg"></li>
                  <li><img src="${root}/thumb/abc.jpg"></li>
                  <li><img src="${root}/thumb/22.jpg"></li>
                  <li><img src="${root}/thumb/12.jpg"></li>
                </ul>

              </div>
              <p class="fset-title"><span>Da Ai TV March Set</span></p>
            </li>
            
            <li>
              <div class="fset">
                <img src="${root}/images/bg_fset.png" class="bg-fset">
                <ul class="fset-grid">
                  <li><img src="${root}/thumb/13.jpg"></li>
                  <li><img src="${root}/thumb/14.jpeg"></li>
                  <li><img src="${root}/thumb/15.jpg"></li>
                  <li><img src="${root}/thumb/16.jpg"></li>
                  <li><img src="${root}/thumb/18.jpg"></li>
                  <li><img src="${root}/thumb/20.jpg"></li>
                  <li><img src="${root}/thumb/abc.jpg"></li>
                  <li><img src="${root}/thumb/22.jpg"></li>
                  <li><img src="${root}/thumb/12.jpg"></li>
                </ul>

              </div>
              <p class="fset-title"><span>Da Ai TV March Set</span></p>
            </li>
          </ul>
          </div>
        </div>
        
      </div><!-- End featured sets -->




    </div><!-- End add-content -->





  </div>
</div>

<div id="tvch-layer"> 
  <div id="tvch-holder"> 
    <div id="tvch-header"> 
      <h2><span>News Channels</span></h2> 
      <img src="${root}/images/arrow_up.png" id="tvch-arrow-up"> 
    </div> 
    <div id="tvch-container"> 
      <ul id="tvch-list"> 
        <li class="on"> 
          <p class="tvch-thumb"><img src="${root}/thumb/03.png" class="thumb"><img src="${root}/images/icon_play.png" class="btn-tvpreview"></p> 
          <p class="tvch-title"><span>ABC News</span></p> 
          <p class="tvch-description"><span>ABC's Jason Ryan (@jasonryanabc) Just days after the perjury trial began, the federal judge overseeing...</span></p> 
        </li> 
        <li> 
          <p class="tvch-thumb"><img src="${root}/thumb/02.jpg" class="thumb"><img src="${root}/images/icon_play.png" class="btn-tvpreview"></p> 
          <p class="tvch-title"><span>CNN News Tonight World Wide</span></p> 
          <p class="tvch-description"><span>CNN's Jason Ryan (@jasonryanabc) Just days after the perjury trial began, the federal judge overseeing...</span></p> 
        </li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
        <li></li> 
      </ul> 
    </div> 
    <div id="tvch-footer"> 
      <img src="${root}/images/arrow_down.png" id="tvch-arrow-down"> 
    </div> 
  </div> 
</div> 

<div id="tvpreview-layer" style="display:block;"> 
  <div id="tvpreview-holder"> 
    <div id="tvpreview-header"> 
      <h2><span class="index">News Channels (<span id="chNum">10</span>) :</span><span class="title">Select a Channel</span></h2> 
      <ul class="navigation"> 
        <li class="btn-store"><img src="${root}/images/icon_store.png" class="icon-store"><span>Channel Store</span></li> 
        <li class="btn-home"><img src="${root}/images/icon_home.png" class="icon-home"><span>Return to 9x9 View</span></li> 
      </ul> 
      <ul id="tvpreview-lang-setting"> 
        <li><span id="tvpreview-program-lang" class="lang-selected">English programs</span><img src="${root}/images/icon_downarrow.png" class="icon-downarrow"> 
          <ul class="lang-options"> 
            <li id="tvp-lang-en"><img src="${root}/images/icon_check.png" class="icon-check"><span>English programs</span></li> 
            <li id="tvp-lang-zh"><img src="${root}/images/icon_check.png" class="icon-check"><span>中文節目</span></li> 
          </ul> 
        </li> 
      </ul> 
    </div> 
    <div id="tvpreview-container"> 
      <div id="tvch-cards"> 
        <div id="current-card"> 
          <img src="" class="tvch-thumb"> 
          <p class="tvch-title"><span>Channel: </span><span class="title">ABC News</span></p> 
          <p class="tvch-description"><span></span></p> 
          <p class="tvch-author"><span>Author: XXX YYY</span></p> 
        </div> 
        <div id="next1-card"><p class="tvch-title"><span>Channel: </span><span class="title"></span></p></div> 
        <div id="next2-card"><p class="tvch-title"><span>Channel: </span><span class="title"></span></p></div> 
        <div id="next3-card"><p class="tvch-title"><span>Channel: </span><span class="title"></span></p></div> 
      </div> 
      <div id="tvpreview-control"> 
        <p class="btn" id="tri-left"><img src="${root}/images/tri_left.png"></p> 
        <p class="btn" id="tri-right"><img src="${root}/images/tri_right.png"></p> 
        <p class="btn" id="tri-up"><img src="${root}/images/tri_up.png"></p> 
        <p class="btn" id="tri-down"><img src="${root}/images/tri_down.png"></p> 
        <p class="btn" id="btn-plus"><img src="${root}/images/icon_plus.png"></p> 
        <p class="txt" id="txt-left"><span>Prev<br>Episode</span></p> 
        <p class="txt" id="txt-right"><span>Next<br>Episode</span></p> 
        <p class="txt" id="txt-up"><span>Prev Channel</span></p> 
        <p class="txt" id="txt-down"><span>Next Channel</span></p> 
      </div> 
      <div id="tvpreview-win"> 
        <p class="btn-blue"><img src="${root}/images/icon_plus.png"><span>Add</span></p> 
        <div id="tvpreview-video"></div> 
        <img src="${root}/images/ep_panel_on.png" id="tvep-panel"> 
        <p id="tv-arrow-right"><img src="${root}/images/arrow_right_off.png" id="tv-arrow-right-off"><img src="${root}/images/arrow_right_on.png" id="tv-arrow-right-on"></p> 
        <p id="tv-arrow-left"><img src="${root}/images/arrow_left_off.png" id="tv-arrow-left-off"><img src="${root}/images/arrow_left_on.png" id="tv-arrow-left-on"></p> 
        <ul class="tvep-list"></ul> 
        <div id="tvep-meta"><p><span class="tvep-title"></span> - <span class="age"></span></p></div>
      </div> 
    </div> 
  </div> 
</div> 

<div id="store-layer"> 
  <div id="store-holder"> 
    <div id="store-header"> 
      <h2><span>Channel Store</span></h2> 
      <ul class="navigation"> 
        <li class="btn-home"><img src="${root}/images/icon_home.png" class="icon-home"><span>Return to 9x9 View</span></li> 
      </ul> 
      <ul id="store-lang-setting"> 
        <li><span id="store-program-lang" class="lang-selected">English programs</span><img src="${root}/images/icon_downarrow.png" class="icon-downarrow"> 
          <ul class="lang-options"> 
            <li id="store-lang-en"><img src="${root}/images/icon_check.png" class="icon-check"><span>English programs</span></li> 
            <li id="store-lang-zh"><img src="${root}/images/icon_check.png" class="icon-check"><span>中文節目</span></li> 
          </ul> 
        </li> 
      </ul> 
    </div> 
    <div id="store-container"> 
      <img src="${root}/images/arrow_up.png" id="store-arrow-up"> 
      <img src="${root}/images/arrow_down.png" id="store-arrow-down"> 
      <h3><span id="store-cat-1"></span></h3> 
      <ul class="store-list" id="store-list-1"> 
      </ul> 
      <h3><span id="store-cat-2"></span></h3> 
      <ul class="store-list" id="store-list-2"> 
      </ul> 
      <h3><span id="store-cat-3"></span></h3> 
      <ul class="store-list" id="store-list-3"> 
      </ul> 
      <img src="${root}/images/tvch_shadow.png" id="store-shadow"></div> 
    <div id="store-footer"></div> 
  </div> 
</div>

<div id="ad-layer" style="display: none">
  <img src="${root}/images/ep_panel_on.png" id="ad-panel">
  <p id="ad-title"><span>Featured Channels</span></p>
  <div id="featured-set">
    <img src="${root}/images/bg_featured_set.png" id="bg-featured-set">
    <ul id="featured-list">
      <li><img src="${root}/thumb/13.jpg"></li>
      <li><img src="${root}/thumb/14.jpeg"></li>
      <li><img src="${root}/thumb/15.jpg"></li>
      <li><img src="${root}/thumb/16.jpg"></li>
      <li><img src="${root}/thumb/18.jpg"></li>
      <li><img src="${root}/thumb/20.jpg"></li>
      <li><img src="${root}/thumb/abc.jpg"></li>
      <li><img src="${root}/thumb/22.jpg"></li>
      <li><img src="${root}/thumb/12.jpg"></li>
    </ul>
  </div>

  <p id="featured-meta">
    <span id="featured-title">BHG Production</span><br>
    <span id="featured-brief">BHG Production is  well known in the media field. It shares 9 most watched channel to all 9x9 users..</span>
  </p>
  <div id="btn-add-featured"><img src="${root}/images/btn_add_featured_off.png" class="off"><img src="${root}/images/btn_add_featured_on.png" class="on"><p><span>Add This Set</span></p></div>
</div>

<div id="ch-directory">
  <div id="dir-holder">

  <div id="dir-header">
    <img src="${root}/images/logo.png" id="dir-logo">
    <p id="chdirtxt">Channel Directory</p>  
  </div>

  <div id="main-panel">
    <ul>
      <!--<li id="featured"><p>Featured</p><span class="arrow">&raquo;</span></li>-->
      <li id="main-1" class="selected"><p id="category1">Category</p><span class="arrow">&raquo;</span></li>

      <!--<li id="most"><p>Most subscribed</p><span class="arrow">&raquo;</span></li>
      <li id="search"><p>Search</p><span class="arrow">&raquo;</span></li>-->
      <li id="main-2"><p id="addrssyt">Add RSS / YouTube</p><span class="arrow">&raquo;</span></li>
    </ul>
    <div class="btn" id="btn-returnIPG" onclick="browse_to_ipg()"><span id="rsg1">Return to Smart Guide</span></div>
  </div>
    <div class="br-panel" id="category-panel">
    <div class="sub-panel">
      <p class="page-up"><img src="${root}/images/arrow_up.png"></p>
      <div class="sub-holder">
        <ul id="ch-catlist"></ul>
      </div>
      <p class="page-down"><img src="${root}/images/arrow_down.png"></p>
    </div>
    <div class="content-panel">
      <p class="page-up" id="content-up" onclick="browse_content_up()"><img src="${root}/images/arrow_up.png"></p>
      <div class="content-holder" style="display: block">
        <ul id="content-list"></ul>
      </div>
      <p id="ch-vacancy"></p>
      <!--a href="javascript:;" class="btn" id="btn-subscribeAll">Subscribe all</a-->
      <p class="page-down" id="content-down" onclick="browse_content_down()"><img src="${root}/images/arrow_down.png"></p>
    </div>
  </div>
  
  <div class="op-panel" id="search-panel">
    <div class="input-area">
      <label for="search input">Enter search term:</label>
        <ul class="search-input">
          <li class="textfieldbox"><input name="" type="text" class="textfield"></li>
          <li><a href="javascript:;" class="btn">Go</a></li>
        </ul>
    </div>
    <p class="page-up"><img src="${root}/images/arrow_up.png"></p>
    <p class="page-down"><img src="${root}/images/arrow_down.png"></p>
  </div>

  <div class="op-panel" id="add-panel">
    <div class="input-area">
      <label for="RSS/YouTube input" id="contribute">Contribute a Podcast / YouTube URL:</label>
      <ul class="url-input">
        <li class="textfieldbox" id="submit-url-box"><input name="" type="text" class="textfield" id="submit-url" onfocus="document.getElementById('submit-url').select();"></li>
      </ul>
    </div>
    <div class="cate-selector">
      <p id="chcat">Channel category:</p>
      <ul class="cate-list" id="cate-list"></ul>
    </div>
    <div id="feedback" class="success"><img src="${root}/images/loading.gif"><p></p></div>
    <a href="javascript:submit_throw()" class="btn" id="add-go"><span>Go</span></a>
  </div>
  </div>
</div>

<div id="yt-layer"> 
  <div id="yt-holder"> 
    <div id="yt-header"> 
      <h2><span>Add My YouTube Favorites</span></h2> 
      <ul class="navigation"> 
        <li class="btn-store"><img src="${root}/images/icon_store.png" class="icon-store"><span>Channel Store</span></li> 
        <li class="btn-home"><img src="${root}/images/icon_home.png" class="icon-home"><span>Return to 9x9 View</span></li> 
      </ul> 
    </div> 
    <div id="yt-container"> 
        <div id="yt-url"> 
          <div class="url-input"> 
            <input name="" type="text" class="url-field" value="Enter YouTube Channel or Playlist URL"> 
          </div> 
          <p class="note">* We don't accept URLs for single video</p> 
        </div> 
        <div id="yt-chName"> 
          <div class="url-input yt-input"> 
            <input name="" type="text" class="url-field" value="Enter Channel Name"> 
          </div> 
        </div> 
        <div id="yt-authorName">         
          <div class="url-input yt-input"> 
            <input name="" type="text" class="url-field" value="Enter Author Name"> 
          </div> 
        </div> 
        <div id="yt-lang"> 
          <ul id="yt-lang-setting"> 
            <li><span id="yt-program-lang" class="lang-selected">Select program language</span><img src="${root}/images/icon_downarrow.png" class="icon-downarrow"> 
              <ul class="lang-options"> 
                <li id="yt-lang-en"><img src="${root}/images/icon_check.png" class="icon-check"><span>English programs</span></li> 
                <li id="yt-lang-zh"><img src="${root}/images/icon_check.png" class="icon-check"><span>中文節目</span></li> 
              </ul> 
            </li> 
           </ul> 
        </div> 
        <div id="yt-description"> 
          <textarea name="" class="description-field">Enter Channel Description</textarea> 
        </div> 
        <div class="cate-selector" id="yt-cate"> 
          <p>Choose category:</p> 
          <ul class="cate-list"> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>News</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Music</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Sports</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Movie Trailers</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Lifestyle</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Food & Wine</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Travel</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Local Deals</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Brands</span></li> 
            <li><img src="${root}/images/check_off.png" class="check-off"><img src="${root}/images/check_on.png" class="check-on"><span>Others</span></li> 
          </ul> 
        </div> 
        <div id="yt-submission"> 
          <ul> 
            <li class="btn" id="btn-add-yt"><span>Add</span></li><li class="btn" id="btn-cancel-yt"><span>Cancel</span></li> 
          </ul> 
          <p id="yt-feedback"><img src="${root}/images/loading.gif"><span>Processing...</span></p> 
        </div> 
    </div>   
  </div> 
</div> 

<div id="signin-layer">

  <div id="signin-holder">

    <div id="btn-winclose"><img src="${root}/images/btn_winclose.png"></div>

    <ul id="tab-list">

      <li id="login" class="on"><h2>Returning Users</h2></li>

      <li id="signup"><h2>New Users</h2></li>

    </ul>

    <div id="login-panel" class="input-panel">

      <ul class="input-list">

        <li>

          <span>Email:</span>

          <p class="signin-input">

            <input type="text" class="signin-field" id="L-email">

          </p>

        </li>

        <li>

          <span>Password:</span>

          <p class="signin-input">

            <input type="password" class="signin-field" id="L-password">

          </p>

        </li>

        <li><a class="btn" id="btn-login"><span>Log in</span></a></li>

      </ul>

    </div>

    <div id="signup-panel" class="input-panel">

      <ul class="input-list">

        <li>

          <span>Name:</span>

          <p class="signin-input">

            <input type="text" class="signin-field" id="S-name">

          </p>

        </li>

        <li>

          <span>Email:</span>

          <p class="signin-input">

            <input type="text" class="signin-field" id="S-email">

          </p>

        </li>

        <li>

          <span>Password:</span>

          <p class="signin-input">

            <input type="password" class="signin-field" id="S-password">

          </p>

        </li>

        <li>

          <span>Password verify:</span>

          <p class="signin-input">

            <input type="password" class="signin-field" id="S-password2">

          </p>

        </li>

        <li><a class="btn" id="btn-signup"><span>Sign up</span></a></li>

      </ul>

       

      <div id="encouragement">

        <p><span>Sign up to have a personalized Smart Guide, and to curate your own channels from YouTube or Facebook.</span></p>

        <div id="signup-triger">

          <p><img src="${root}/images/icon_tip.png" id="triger-icon"><span id="triger-txt">Learn more</span></p>

          <p id="signup-tip"><span>To curate your own channels from YouTube or Facebook, simply copy and paste your YouTube channel, YouTube playlist or Facebook page URL to the Smart Guide.</span><span class="arrow"></span></p>

        </div>

      </div>

    </div>

  </div>

</div>


<div id="browse" style="display: none; z-index: 999"></div>

<div id="preload-control-images" style="display: none"></div>

<div id="control-layer" style="display: block;">
    <img src="${root}/images/bg_controler.png" id="controler-bg">
    <ul id="control-bar">
      <li id="play-time">00:00 / 00:00</li>
      <li id="progress-bar">
        <p id="loaded" style="width: 100%"></p>
        <p id="played"></p>
      </li>
      <li class="divider"></li>

      <li id="instruction"><span id="cinstr">Mouse over the control bar to see episodes.</span></li>
      <li id="btn-volume-up" class="cpclick"><img src="${root}/images/btn_volume_up.png" title="Volume Up"></li>
      <li id="volume-constrain" class="on">
        <ul id="volume-bars">
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
        </ul>
      </li>
      <li id="btn-volume-down" class="cpclick"><img src="${root}/images/btn_volume_down.png" title="Volume Down"></li>
      <!--li id="btn-mute"><img src="${root}/images/btn_mute.png" title="Mute"></li-->
      <li class="divider-r">
      <li id="btn-facebook" class="cpclick"><img src="${root}/images/btn_facebook.png" title="Share to Facebook"></li>
      <li id="btn-sg" class="cpclick"><img src="${root}/images/btn_sg.png" title="Return to Smart Guide "></li>
      <li class="divider-r">
      <li id="btn-forward" class="cpclick"><img src="${root}/images/btn_forward.png" title="Fast forward" ></li>
      <li id="btn-pause" class="cpclick"><img src="${root}/images/btn_pause.png" title="Pause"></li>
      <li id="btn-play" class="cpclick on"><img src="${root}/images/btn_play.png" title="Play"></li>
      <li id="btn-rewind" class="cpclick"><img src="${root}/images/btn_rewind.png" title="Rewind"></li>
      <li id="btn-replay" class="cpclick"><img src="${root}/images/btn_replay.png" title="Play from beginning"></li>
      <li class="divider-r"></li>
    </ul>
</div>

<div id="delete-layer">
  <div class="delete-holder" id="delete-holder">
    <p id="step1"><span id="suredel">Are you sure you want to delete</span> "<span id="delete-title-1"></span>"?</p>
    <p id="step2"><span id="havedel">You have deleted channel</span> "<span id="delete-title-2"></span>".</p>
    <div class="actions"><a class="btn" id="btn-delYes" onclick="delete_enter(1)"><span>Yes</span></a>
    <a class="btn on" id="btn-delNo" onclick="delete_enter(2)"><span>No</span></a>
    <a class="btn on" id="btn-returnSG" onclick="delete_enter(1)"><span id="delrsg">Return to Smart Guide</span></a>
    <a class="btn" id="btn-delMore" onclick="delete_enter(2)"><span id="delmore">Delete More Channels</span></a></div>
  </div>
</div>

<div id="confirm-layer">
  <div class="confirm-holder" id="confirm-holder" style="z-index: 70">
    <p id="confirm-text"></p>
    <a class="btn on" id="btn-cfclose"><span id="close1">Close</span></a>
  </div>
</div>

<div id="waiting">
  <div class="waiting-holder">
    <img src="${root}/images/loading.gif">
    <p id="moment1">One moment...</p>

  </div>
</div>

<div id="buffering">
  <div class="waiting-holder">
    <img src="${root}/images/loading.gif">
    <p id="buffering1">Buffering...</p>
  </div>
</div>

<div id="dir-waiting">
  <div class="waiting-holder">
    <img src="${root}/images/loading.gif">
    <p id="moment2">One moment...</p>
  </div>
</div>

<div id="msg-layer" style="display: none">
  <div class="msg-holder" id="#msg-holder">
    <p id="msg-text">No episodes in this channel</p>
  </div>
</div>

<div id="ear-left">
  <p id="left-off">
    <span>CH -</span>
  </p>
  <div id="left-on">
    <p class="range"><span class="align"><span class="txt" id="ear-left-name"></span></span></p>
    <img class="preview" id="ear-left-img">
    <p class="ch"><span>CH -</span></p> 
  </div>
</div>

<div id="ear-right">
  <p id="right-off">
    <span>CH +</span>
  </p>
  <div id="right-on">
    <p class="range"><span class="align"><span class="txt" id="ear-right-name"></span></span></p>
    <img class="preview" id="ear-right-img">
    <p class="ch"><span>CH +</span></p> 
  </div>
</div>

<div id="epend-layer" style="display: none">
  <div id="go-up">Press <span class="enlarge">&uarr;</span> to go to the IPG</div>
  <div id="go-down">Press <span class="enlarge">&darr;</span> to see all episodes</div>
  <div id="go-left"><img src="" id="left-tease">Press <span class="enlarge">&larr;</span> to watch previous channel</div>
  <div id="go-right"><img src="" id="right-tease">Press <span class="enlarge">&rarr;</span> to watch next channel</div>
</div>

<div id="branding-temp" style="display: none">

  <div id="branding-holder">

    <p id="date"><span>Friday April 1, 2011</span></p>

    <p class="announcing"><span>Watch Da Ai on</span><img src="${root}/images/logo_about.png"></p>

    <p class="wording"><span>Channels now playing</span></p>

    <img src="${root}/images/logo_tzuchi.png" id="logo-tzuchi">

    <p id="branding-msg"><span>善的接力，清流的堅持<br>看大愛，行大愛</span></p>

    <ul id="temp-channels">

    </ul>

    <p class="pushing"><span>Organize your favorite Da Ai channels into a personalized program guide!</span></p>

    <p class="btn-blue" id="btn-watch"><span>Watch Now</span></p>

  </div>

</div>


<div id="direct-temp">
  <img src="${root}/images/bg_direct.png" id="bg-direct">
  <div id="direct-content">
    <img src="${root}/images/logo_about.png" id="direct-logo">
    <h1><span>9x9 is your personal video album</span></h1>
    <p class="description"><span>All of your favorite videos in one place. Like videos from your podcasts, YouTube channels, Facebook and Twitter. Plus discover video albums created for your unique lifestyle. You feed all these videos as TV channels into a personalized program guide you create and personalize.</span></p>
    <p class="btn-blue" id="btn-direct-enter"><span>Watch it Now</span></p>
    <p class="promotion"><span>Personalize your video album and<br>save it to your <strong>Smart Guide</strong></span></p>
    <p class="btn-blue" id="btn-direct-signup"><span>Create Account</span></p>
  </div>
</div>

<div id="success-layer">
  <div class="success-holder" id="success-holder">
    <p class="greeting"><span>Congratulations!</span></p>
    <p><span>You have just added a set of <span id="success-data">4 Da Ai</span> channels to your <strong>Smart Guide</strong>.<br>Your Smart Guide is the place for you to add more channels and personalize your channel line-up.</span></p>
    <ul class="action-list">
      <li><a class="btn on" id="success-goto"><span>Go to my Smart Guide</span></a></li>
    </ul>
  </div>
</div>

<div id="hint-layer" style="display: none">
  <div class="hint-holder" id="hint-holder">
    <p id="hint-title"><span id="hinstr">Instruction</span></p>
    <div id="sg-hint">
      <p class="section-title"><span id="hwbsg">While Browsing Smart Guide</span></p>
      <ul class="hints-list">
        <li><img src="${root}/images/arrow_keys.png" class="key-arrows"><span id="huak">Use arrow keys or mouse to navigate</span></li>
        <li><img src="${root}/images/enter_key.png" class="key-enter"><span id="hpec">Play episodes in the channel selected or add new channels</span></li>
      </ul>
    </div>
    <div id="ep-hint">
      <p class="section-title"><span id="hwwe">While Watching Episodes</span></p>
      <ul class="hints-list">
        <li><img src="${root}/images/enter_key.png" class="key-enter"><span id="hscp">Show control panel</span></li>
        <li><img src="${root}/images/up_key.png" class="key-up"><span id="rsg2">Return to Smart Guide</span></li>
        <li><img src="${root}/images/left_key.png" class="key-left"><img src="http://9x9ui.s3.amazonaws.com/9x9playerV44/images/right_key.png" class="key-right"><span id="hshow">Show episodes in this channel</span></li>
      </ul>
    </div>
    <div id="hint-bottom">
      <p id="hint-remove" style="display: none"><img src="${root}/images/check_off.png" class="checkbox"><span>Don't show me this again</span></p>
      <p class="btn" id="btn-closeHint" onclick="close_hint()"><span id="hctw">Close this window</span></p>
    </div>
  </div>
</div>

<div id="about-layer">
  <div class="about-holder" id="about-holder">
    <p><span id="about1">9x9 is a cloud based video platform which allows internet content to be discovered and enjoyed through a Smart Guide on Smart TV, Smart Phone and Tablet Devices.</span></p>
    <p><span id="about2">Discover the magic of the Smart Guide, a 9x9 grid which can be personalized and populated with up to 81 channels to satisfy your everyday online video appetite.</span></p>
    <p><span id="about3">Watch your favorite podcasts, YouTube channels and other episodic content on 9x9 just like watching TV.</span></p>
    <p><span id="about4">9x9 is based in Santa Clara, California, USA.  We are a bunch of geeks passionate about revolutionizing online video discovery through a human powered network.</span></p>
    <p><span id="about5">Our investors include venture capitalists, private investors and corporate investors including D-Link.  Contact us at <a href="mailto:feedback@9x9Cloud.tv">feedback@9x9Cloud.tv</a>.</span></p>
    <div id="btn-closeAbout"><img src="${root}/images/btn_winclose.png"></div>
    <img src="${root}/images/logo_about.png" id="about-logo">
  </div>
</div>

<div id="sg-bubble"><img src="${root}/images/bg_bubble.png"><div id="btn-bubble-del"><img src="${root}/images/btn_delete_off.png" class="off"><img src="${root}/images/btn_delete_on.png" class="on"></div><p><span id="rsbubble">Return to Smart Guide for more interesting content</span></p></div>

<div id="fb-bubble">

  <div id="fb-holder">

    <img src="" id="fb-picture">

    <span id="fb-name"></span>

    <span id="fb-comment"></span>

  </div>

</div>


<!--div id="btn-subscribe" style="z-index: 300; display: none"><img src="${root}/images/btn_subscribe_off.png" class="off"><img src="${root}/images/btn_subscribe_on.png" class="on"></div-->

<div id="toast">
  <img src="${root}/images/bg_toast.gif" id="bg-toast">
  <div id="toast-close">
    <img src="${root}/images/btn_delete_off.png" id="toast-close-off">
    <img src="${root}/images/btn_delete_on.png" id="toast-close-on">
  </div>
  <p id="toast-txt"><span>Follow this channel?</span></p>
  <div id="btn-holder">
    <p id="btn-yes" class="btn-blue"><span>Yes</span></p>
  </div>
</div>

<div id="opening" style="display: block; z-index: 999">
  <div class="opening-holder" id="splash"></div>
</div>

<div id="yesno-layer">
  <div class="yesno-holder" id="yesno-holder">
    <p id="question"></p>
    <ul class="action-list">
      <li><a href="javascript:yn_enter(1)" class="btn" id="yesno-btn-yes"><span id="qyes">Yes</span></a></li>
      <li><a href="javascript:yn_enter(2)" class="btn" id="yesno-btn-no"><span id="qno">No</span></a></li>
    </ul>
  </div>
</div>

<div id="rename-layer">
  <div class="rename-holder">
    <p id="rename-input">
      <input type="text" id="rename-field" value="Enter a new title for the set">
    </p>
    <ul class="action-list">
      <li><a class="btn" id="btn-rename-save"><span>Save</span></a></li>
      <li><a class="btn" id="btn-rename-cancel"><span>Cancel</span></a></li>
    </ul>
  </div>
</div>

<div id="tribtn-layer">
  <div class="tribtn-holder">
    <p>This set has been added to your Smart Guide!</p>
    <div class="actions">
      <a class="btn" id="btn-watchSet"><span>Watch this set</span></a>
      <a class="btn" id="btn-toFset"><span>Look at more featured sets</span></a>
      <a class="btn" id="btn-toSG"><span>Return to Smart Guide</span></a>
    </div>
  </div>
 </div>

<div id="log-layer" style="position: absolute; left: 0; top: 0; height: 100%; width: 100%; background: white; color: black; text-align: left; padding: 20px; overflow: scroll; z-index: 9999; display: none"></div>

<div id="mask"></div>

<div id="fb-root"></div>

<div id="relaydiv" style="z-index: 1; position: absolute; top: 0px; left: 0px">
<object id="relay" width=481 height=86>
<param name=movie value="relay.swf">
<embed play=false swliveconnect="true" name="relay" 
src="http://50.17.15.33/relay.swf" quality=high bgcolor=#FFFFFF 
width=500 height=500 type="application/x-shockwave-flash" allowScriptAccess="always" allowScripting="on" wmode="transparent">
</embed>
</object>
</div>

<!--/div-->
</body>
</html>