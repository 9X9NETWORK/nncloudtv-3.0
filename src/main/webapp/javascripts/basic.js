// JavaScript Document
$(document).ready( function(){    		
		
  $("#program-save").click(function() {
     imageUrl = $("#imageUrl").val();
     intro = $("#intro").val();
     name = $("#name").val();
     pid = $("#pid").val();
     url = "/show/edit/" + pid;
     $.post(url, {imageUrl : imageUrl, intro : intro, name : name}, 
     	 function(data) {});     	             
  });
  		
  $("#btn-signout").click(function() {
    $(this).attr("href","index.html");
  });
                                                                                                                
  $("#channel-selector").change(function () {
     currentChannel = $("#channel-selector").val();  		  
     newUrl = "/show/create/" + currentChannel;
     $("#btn-newshow").attr("href", newUrl); 		  
     $(".constrain").append('<p class="clip-loading">Loading...</p>');
     $(".clip-loading").show();
     $(".clip-list").empty();
     url = "/show/clips/" + $("#channel-selector").val();
     $.get(url, function(data) {       
       $(".clip-list").fadeIn('slow');
       $("#clips").html(data);                     
       GetListWidth("onshow-list");
       GetListWidth("offshow-list");
     })  
    
  });
                                                   
  //!!!!! merge with swfupload/file_handler
  $("#btn-show-url-save").click(function() {
      url = "/show/create/" + $("#cid").val();                    
	  if ($("#action").html() != "create") {
		  url = "/show/edit/" + $("#pid").val();
	  }
      file = $("#s1-url").val();
  	  if ($("#action").html() == "create") {  
	      $.post(url, {fileUrl : file}, function(data) {
	          swfu.setButtonDisabled(true);
		      img_swfu.setButtonDisabled(false);
	          $("#name").val(data.filename);                    
	          $("#btn-show-url-save").removeAttr("href");
	          $("#program-save").removeClass("disable");
		      $("#program-save").removeAttr('disabled');	    	   
	          $("#pid").val(data.pid);
	      }, "json");    		                                             
  	  } else {
  		  imageUrl = $("#imageUrl").val();
  		  intro = $("#intro").val();
  		  name = $("#name").val();
	      $.post(url, {fileUrl : file, name : name, imageUrl : imageUrl, intro : intro}, function(data) {
	      }, "json");    		                                             
  		  
  	  }
  });                             
  
  $("#shows-content .clip-list li").mousedown(function() {
      $(this).addClass("grab");
  }).mouseup(function() {                                                                              
      $(this).removeClass("grab");
  });
  
  // IPG
  SetOnlistPos();
  
  $("#on-list img, #off-list img").mouseover(function() {
     url = '/channel/detail/' + $(this).parent().attr('id');
     $(this).load(url, function(data) {
         $('.info-bubble').html(data);
         dh = $("#ipg-content").width()/2;
         dv = $("#ipg-content").height()/2;
         loc = $(this).offset();                 
         h = loc.left;
         v = loc.top;
         l = loc.left + 77;
         r = loc.left - 325;
         t = loc.top - 45;
         b = loc.top - 125;
        if (h < dh) {
           if (v < dv) {
             $(".info-bubble").fadeIn("fast").css({"left":l, "top":t});
             $(".info-bubble").removeClass().addClass("info-bubble").addClass("q1");
           } else {
             $(".info-bubble").fadeIn("fast").css({"left":l, "top":b});
             $(".info-bubble").removeClass().addClass("info-bubble").addClass("q3");
           }
        } else {
           if (v < dv) {
             $(".info-bubble").fadeIn("fast").css({"left":r, "top":t});
             $(".info-bubble").removeClass().addClass("info-bubble").addClass("q2");
           } else {
             $(".info-bubble").fadeIn("fast").css({"left":r, "top":b});
             $(".info-bubble").removeClass().addClass("info-bubble").addClass("q4");
           }
       }       
   })
  }).mouseout(function() {
     $(".info-bubble").hide();
  });
    
  $(".info-bubble").mouseover(function() {
     $(this).show();
  }).mouseout(function() {
     $(this).hide();
  });
      
  function SetOnlistPos() {
    if ($("#off-list").css("display")=="block") {
         w = ($("#ipg-content").width()-$("#on-list").width()-$("#off-list").width()-2)/2 ;
      $("#on-list").css("margin-left",w);
    } else {
      w = ($("#ipg-content").width()-$("#on-list").width())/2 ;
      $("#on-list").css("margin-left",w);
    }
  }
  
  $("#btn-toggle").toggle(function () {
     $(this).removeClass("on").text("Show Off-Air Channels");
     $("#off-list").hide();
     SetOnlistPos();
     },function() {
        $(this).addClass("on").text("Hide Off-Air Channels");
        $("#off-list").show();
        SetOnlistPos();
  });  
  
  // Upload button mouse over //
  $("#btn-upload").mouseover(function() {
     $(this).attr("src","images/btn_upload_hover.png");
  }).mouseout(function() {
     $(this).attr("src","images/btn_upload.png");
  }); 
  
  // Form Inputs Status Change //
  $(".form .textfield").focus(function() {
     $(".form .guide").hide();
     $(this).parents("span.textfieldbox").siblings(".guide").show();
     $(this).parents("span.textfieldbox").siblings(".mandatory-bubble").remove();
     $(this).parents("span.textfieldbox").css("background-position","0 -100px");
  }).blur(function() {
     $(this).parents("span.textfieldbox").css("background-position","0 0");
  });

  $(".form .textarea").click(function() {
    $(".form .guide").hide();
    $(this).parents("span.textareabox").siblings(".guide").show();
    $(this).parents("span.textareabox").siblings(".mandatory-bubble").remove();
    $(this).parents("span.textareabox").css("background-position","0 -200px");
  }).blur(function() {
    $(this).parents("span.textareabox").css("background-position","0 0");
  });
  
  // Info form Validation //
  function ValidateInfo() {
     if ($("#c-name").val() == "") {
       $("#c-name").parents(".form li").append("<div class='mandatory-bubble'></div>");
       $(".mandatory-bubble").fadeIn("slow");
       validate = "flase";
     } else {
       validate = "true";
     } 
     if ($("#c-intro").val() == "") {
       $("#c-intro").parents(".form li").append("<div class='mandatory-bubble'></div>");
       $(".mandatory-bubble").fadeIn("slow");
       validate = "flase";
     }else {
       validate = "true";
     } 
     return validate;
  }
  
  $("#btn-createShow").click(function() {
    ValidateInfo();
    if (validate == "true") {
       $(this).attr("href","create_show.html");
    }
  });
  
  // Publish button //  
  function PublishOn() {
    $("#btn-publish").attr("src","images/btn_publish.png");
    $("#btn-publish").mouseover(function() {
      $(this).attr("src","images/btn_publish_hover.png");
    }).mouseout(function() {
      $(this).attr("src","images/btn_publish.png");
    }).click(function() {
      ValidatePublish();
    });
  }
  
  // Publish Validation //
  function ValidatePublish() {
     if ($("#s-title").val() == "") {
       $("#s-title").parents(".form li").append("<div class='mandatory-bubble'></div>");
       $(".mandatory-bubble").fadeIn("slow");
       validate = "flase";
     } else {
       validate = "true";
     }  
     return validate;
  }
  
  // Shows Channel Option list //
  $(".titlebarL .selected").click(function() {
     $(this).addClass("on");
     $(".option-list").show();
  });
  $(".titlebarL .option-list li").click(function() {
     text = $(this).text();
     $(".titlebarL .selected").text(text).removeClass("on");
     $(".option-list").hide();
  });
  
  // Create Channel //
  $("#newchannel-content #btn-upload").click(function() {
    $("#newchannel-content .thumb").attr("src","thumb/20.jpg");
  });
  
  // Edit Channel //  
  $("#btn-ok").parents("li").hide();
  
  var box;                                      
  $("#btn-deleteChannel").click(function() {              
     $("#mask, .confirm-box").show();
     $("#message").html("If you delete the channel, all associated shows and subscribers will be removed from your vMSO line up.<span class='linebreak'>Continue?</span>");
     AlignCenter(".confirm-box");
     box = "deleteChannel";
  });                               
  
  $("#btn-takeOffAir").click(function() {                                                              
    $("#mask, .confirm-box").show();
    btnText = jQuery.trim($("#btn-takeOffAir").html());
    if (btnText == "Take Off-Air" ) {        	
      $("#message").html("Taking the channel off-air will remove it from your IPG. Existing subscribers can still view this channel. Do you want to take the channel off-air?");
    } else {
      $("#message").html("Taking the channel on-air will put it back to your IPG. Do you want to take the channel on-air?"); 	    
    }    
    AlignCenter(".confirm-box");
    box = "takeOffAir";
  });
    
  $("#btn-yes").click(function() {            
    if (box == "deleteChannel") {
       url = "/channel/delete/" + $(".control").attr('id');
       $.get(url, function(data) {
          // !!! if 200/ok, then
          $("#message").html("Channel has been deleted.");
       })     
    }
    if (box == "takeOffAir") {
        id = $(".control").attr('id');
        url = '/channel/onoff/' + id;
        btnText = jQuery.trim($("#btn-takeOffAir").html());     
        if (btnText == "Take Off-Air" ) {          
           $.post(url, { isPublic: false }, 
        	function(data) {           	  
        	   $("#message").html("You can find this channel in the off-air section.");
        	   $("#btn-takeOffAir").html("Take On-Air");
        	})
        } else {
           $.post(url, { isPublic: true }, 
        	function(data) {           	  
        	   $("#message").html("You can find this channel in the on-air section.");
        	   $("#btn-takeOffAir").html("Take Off-Air");
        	})        	
        }
           	   
    }
    $("#btn-yes, #btn-no").parents("li").hide();
    $("#btn-ok").parents("li").show();
  });
  
  $("#btn-no").click(function() {
    $("#mask, .confirm-box").hide();
  });
  
  $("#btn-ok").click(function() {     		  
     $("#mask, .confirm-box").hide();
     $("#btn-yes, #btn-no").parents("li").show();
     $("#btn-ok").parents("li").hide();
     if (box != "takeOffAir") {
       $(this).attr("href","/channel/list");
     }
  });
  
  $("#btn-boxclose").click(function() {
     $("#mask, .confirm-box").hide();
  });
  
  $("#btn-saveChannel").mouseover(function() {
     $(this).attr("src","images/btn_save_hover.png");
  }).mouseout(function() {
     $(this).attr("src","images/btn_save.png");
  });
  
  
  $("#btn-saveEditChannel, #btn-cancel").click(function() {
    $(this).attr("href","/channel/list");
  });
  
  $(window).resize(function(){ 
    SetOnlistPos();
    GetListWidth("onshow-list");
    GetListWidth("offshow-list");
    SetMask();
  });
                                                                                
  // Onshow List / Offshow List Width //
  GetListWidth("onshow-list");
  GetListWidth("offshow-list");
  
  function GetListWidth(list) {
    Num = $("#"+list+" .clip-list li").size();
    w = 115*Num;
    $("#"+list+" .clip-list").css("width",w);
  }
  
  // Onshow list / Offshow List Check box //  
  $("#shows-content .check").live("click",GetChecked);  
     function GetChecked() {
       $(this).toggleClass("checked");
       list = $(this).parents(".show-list").attr("id");
       ListCheck(list);
     }
  
     function CheckAll(list) {
        i = $("#"+list + " .check" ).size();
        if (i == 0) {                                                             
           $("#"+list+" .btn-all").parents("li").addClass("disable");
           $("#"+list+" .action").addClass("disable");
        } else {
          $("#"+list+" .btn-all").parents("li").removeClass("disable");
        }
     }
  
     function ListCheck(list) {
        i = $("#"+list + " .checked").size();
        if (i > 0) {
           $("#"+list + " .action").removeClass("disable");                                
        } else {
           $("#"+list + " .action").addClass("disable");
        }
     }
  
    $(".btn-all").live("click", function() {
      if( !$(this).parents("li").hasClass("disable")) {                       
        list = $(this).parents(".show-list").attr("id");
        $("#"+list+" .check").addClass("checked");
        ListCheck(list);                                                  
      }                                                      
  });
    
  $(".btn-delete").live("click", function() {             
    list = $(this).parents(".show-list").attr("id"); //onshow-list
    
    if( !$(this).parents(".action").hasClass("disable")) {                  
      programs = "";            
      $("#"+list+" .checked").parents("li").each(function() {
      	 programs = programs + $(this).attr("id") + ","	      
      });       
      programs = programs.substring(0, programs.length - 1);            
      url = "/show/delete/" + programs;      
      $.get(url, function(data) {
         $("#"+list+" .checked").parents("li").remove();
     });
    }                                                     
    GetListWidth(list);                         
    CheckAll(list);
  });                                                
  
  var shows = [];
                                                                             
  $(".btn-movedown").live("click", function() {                                                    
    if( !$(this).parents(".action").hasClass("disable")) {
       shows = [];                                               
       Num = $("#onshow-list .checked").size();         
       target = $("#onshow-list .checked").siblings("img").attr("src");
       shows.push(target);
       programs = "";            
       $("#"+list+" .checked").parents("li").each(function() {
       	 programs = programs + $(this).attr("id") + ","	           
       });       
       programs = programs.substring(0, programs.length - 1);            
       url = "/show/onoff/" + programs;
       data = "";       
       $.post(url, { isPublic: false}, function(data) {
       });
       if (data == "") {
          $("#"+list+" .checked").parents("li").each(function() {
    	     $(this).find('a').toggleClass("checked", false);          
    	     $("#offshow-list .clip-list").append($(this));	      
          });                                                                          
       }
       GetListWidth("offshow-list");                                      
       GetListWidth("onshow-list");
       CheckAll("offshow-list");
       CheckAll("onshow-list");
    }
  });
  
  $(".btn-moveup").live("click", function() {
    if( !$(this).parents(".action").hasClass("disable")) {
       shows = [];
       Num = $("#offshow-list .checked").size();
       target = $("#offshow-list .checked").siblings("img").attr("src");
       shows.push(target);

       programs = "";
       $("#"+list+" .checked").parents("li").each(function() {
       	 programs = programs + $(this).attr("id") + ","	           
       });       
       programs = programs.substring(0, programs.length - 1);            
       url = "/show/onoff/" + programs;       
       data = ""
       $.post(url, { isPublic: true}, function(data) {       		       
       });
       if (data == "") {
         $("#"+list+" .checked").parents("li").each(function() {
    	    $(this).find('a').toggleClass("checked", false);          
    	    $("#onshow-list .clip-list").append($(this));	      
         });
       }
       GetListWidth("offshow-list");
       GetListWidth("onshow-list");
       CheckAll("offshow-list");
       CheckAll("onshow-list");
    }
  });
  
  // Onshow list / Offshow List Play button //
  $("#popup-close").click(function() {
    $("#mask").hide();
    $(".popup").hide();
  });
  
  $(".play").click(function() {
    $("#mask").show();
    $(".popup").show();
    AlignCenter(".popup");
    AlignCenter(".confirm-box");
  });
  
  function AlignCenter(obj) {
    ww = $(window).width();
    wh = $(window).height();
    pw = $(obj).width();
    ph = $(obj).height();
    l = (ww-pw)/2;
    /* t = (wh-ph)/2;*/
    t = 125;
    $(obj).css({"left":l,"top":t});
  }
    
  //Create Shows //
/*  
  var sid=1;
  $("#btn-moreshow").click(function() {
    sid= sid + 1;
    $(".showbox").append('<div class="show" id="s'+sid+'"><form id="showinfo-form"><div class="showinfo"><p class="show-title"><span class="tabNum">1</span>Fill in show information</p><ul class="form"><li><label for="show title">Show title:<span class="star">*</span></label><span class="textfieldbox"><input id="s'+sid+'-title" type="text" class="textfield"/></span></li><li><label for="show description">Show description:</label><span class="textareabox"><textarea id="s'+sid+'-description" cols="10" rows="6" class="textarea"></textarea></span></li><li><a href="javascript:;" class="btn btn-saveInfo">Save</a></li></ul></div></form><form id="showthumb-form"><div class="showthumb disable"><p class="show-title"><span class="tabNum">2</span>Upload show icon</p><ul class="thumbnail"><li><label for="show-icon">Show icon:</label></li><li><img src="images/thumb_noImage.jpg" class="thumb"> <span class="guide">Recommended upload size under 2MB</span></li><li class="btn-line"><img src="images/btn_upload_disable.png" class="btn-uploadThumb" disabled="disabled"><a href="javascript:;" class="btn-cancelThumb">Cancel</a></li></ul></div></form><form id="showupload-form"><div class="showupload disable"><p class="show-title"><span class="tabNum">3</span>Upload show</p><ul class="form"><li><label for="show url">Show URL:</label><span class="textfieldbox"><input id="s'+sid+'-url" type="text" class="textfield" disabled="disabled"/></span> </li><li class="btn-line"><img src="images/btn_upload_disable.png" class="btn-uploadShow"><a href="javascript:;" class="btn-cancelShow">Cancel</a></li></ul><p class="uploading"></p><p class="result"><span class="exciting">Complete!</span><br>The uploaded show is in the Off-Air Show area now.</p></div></form></div>');
  });
*/  
  $(".showinfo .textfield").focus(function() {
     $(this).parents("li").append("<div class='show-bubble'>30 English or 15 Chinese characters only.</div>");
     $(this).parents(".textfieldbox").siblings(".show-bubble").fadeIn("slow");
     $(".showinfo .mandatory-bubble").remove();
  }).blur(function() {
     $(this).parents(".textfieldbox").siblings(".show-bubble").remove();
  });
  
  $(".showinfo .textarea").focus(function() {
     $(this).parents("li").append("<div class='show-bubble'>80 English or 40 Chinese characters only.</div>");
     $(this).parents(".textareabox").siblings(".show-bubble").fadeIn("slow");
     $(".showinfo .mandatory-bubble").remove();
  }).blur(function() {
     $(this).parents(".textareabox").siblings(".show-bubble").remove();
  });
  
  $("#s1-title").focus(function() {
     $(this).parents("li").append("<div class='show-bubble'>30 English or 15 Chinese characters only.</div>");
     $(this).parents(".textfieldbox").siblings(".show-bubble").fadeIn("slow");
     $(".showinfo .mandatory-bubble").remove();
  }).blur(function() {
     $(this).parents(".textfieldbox").siblings(".show-bubble").remove();
  });
  
  
  function ShowThumbSwitch() {
     if ($("#s1 .showthumb").hasClass("disable")) {
       $("#s1 .btn-uploadThumb").attr("src","btn_upload_disable.png");
       $("#s1 .btn-uploadThumb").mouseover(function() {
          $(this).attr("src","images/btn_upload_disable.png");
       }).mouseout(function() {
          $(this).attr("src","images/btn_upload_disable.png");
       });
     } else {
       $("#s1 .btn-uploadThumb").attr("src","images/btn_upload.png");
       $("#s1 .btn-uploadThumb").mouseover(function() {
       $(this).attr("src","images/btn_upload_hover.png");
    }).mouseout(function() {
       $(this).attr("src","images/btn_upload.png");
    });
  }
  }
  
  function ShowUploadSwitch() {
     if ($("#s1 .showupload").hasClass("disable")) {
     	$("#btn-uploadShow").attr("src","btn_upload_disable.png");
        $("#btn-uploadShow").mouseover(function() {
        $(this).attr("src","images/btn_upload_disable.png");
      }).mouseout(function() {
        $(this).attr("src","images/btn_upload_disable.png");
      });
    } else {
       $("#s1 .btn-uploadShow").attr("src","images/btn_upload.png");
       $("#s1 .btn-uploadShow").mouseover(function() {
          $(this).attr("src","images/btn_upload_hover.png");
       }).mouseout(function() {
          $(this).attr("src","images/btn_upload.png");
      });
    }
  }
  
  function ValidateShow() {
     if ($("#s1-title").val() == "") {
       $("#s1-title").parents(".form li").append("<div class='mandatory-bubble'></div>");
       $(".mandatory-bubble").fadeIn("slow");
       validate = "flase";
     } else {
       validate = "true";
     }  
     return validate;
  }
  
  $(".btn-saveInfo").mouseover(function() {
     $(this).attr("src","images/btn_save_hover.png");
  }).mouseout(function() {
     $(this).attr("src","images/btn_save.png");
  });
   
  $("#s1 .btn-saveInfo").click(function() {
     ValidateShow();
     if (validate == "true") {
       $("#s1 .showthumb").removeClass("disable");
       $("#s1 .btn-uploadThumb").attr("disabled","");
       ShowThumbSwitch();
     }
  });
  
  $("#s1 .btn-uploadThumb").click(function() {
     $("#s1 .showthumb .thumb").attr("src","thumb/22.jpg");
     $("#s1 .showupload").removeClass("disable");
     $("#s1-url").attr("disabled","");
     $("#s1 .btn-uploadShow").attr("disabled","");
     ShowUploadSwitch();
  });

  $("#s1-url").focus(function() {
     $(this).parents("span.textfieldbox").css("background-position","0 -100px");
  }).blur(function() {
     $(this).parents("span.textfieldbox").css("background-position","0 0");
  });
  

  /*
  $("#s1 .btn-uploadShow").click(function() {
    if (!$("#s1 .showupload").hasClass("disable")) {
      $("#s1 .uploading").show().delay(1000).fadeOut();
      $("#s1 .result").delay(1500).fadeIn();
    }
  });
  */
 
  // Embed //
  $(".form .dimensionfield").focus(function() {
     $(this).parents("span.dimensionbox").css("background-position","0 -100px");
  }).blur(function() {                           
     $(this).parents("span.dimensionbox").css("background-position","0 0");
  });
  
  $("#btn-generalCode").click(function() {
     $("#embed-code").parents(".textareabox").css("background-position","0 -200px");
  }).blur(function() {
     $("#embed-code").parents(".textareabox").css("background-position","0 0");
  });
  
  // Account //
  $("#btn-account").click(function() {                     
    $(this).attr("href","account.html");                  
  });
  
  $("#account-content .check").click(function() {
    $(this).toggleClass("checked");
  });
  
  $("#btn-saveAccount").mouseover(function() {
     $(this).attr("src","images/btn_save_hover.png");
  }).mouseout(function() {
     $(this).attr("src","images/btn_save.png");
  }).click(function() {
     $("#mask, .confirm-box").show();
     $("#message").html("Your change has been saved successfully!");
     AlignCenter(".confirm-box");
  });
                         
  $("#btn-AccountOK").click(function() {
     $("#mask, .confirm-box").hide();              
  });
  
  SetMask();                             
  function SetMask() {  
    h = $(document).height();
	$("#mask").css("height",h);
  }                              

});
