var device_id;
var timezero = 0;

/* this should be "user" if it is to match 1ft, or "tvuser" if not */
var cookiename = "user";

var pool = {};
var set_pool = {};

var channelgrid = {};
var programgrid = {};

var set_titles = {};
var set_types = {};

var current_channel;
var osd_timex = 0;
var current_tube;

var program_line = [];
var n_program_line = 0;

var mini_player;
var ytmini = [];
var ytmini_video_id = [];
var ytmini_why = [];
var ytmini_previous_state = [-1, -1, -1, -1];
var ytready = [ false, false, false, false ];
var yt_video_id;
var yt_quality = "medium";
var player_mute = false;
var volume_setting = 5 / 7;
var nopreload = false;
var preload = 'on'; /* for 1ft compatibility */
var layers = true;
var device_displayed = "";
var language = 'en';
var locale = 'en';
var sphere = 'en';
var inherited_sphere = false;
var fetch_yt_callbacks = {};
var fetch_yt_timex;
var initialized = false;
var jumpstart_channel;
var on_video_start;

var audio_player = 1;
var audio_stream = [];
var audio_ready = [];
var audio_callback = [];
var audio_url = [];

/* z-index base setting. set to 0 outside store, 100 inside store */
var video_z_base = 0;

var fbtoken;
var brandinfo = '9x9';

var user = '';
var username = '';
var sent_email;
var initial_user;
var n_users = 0;
var known_users = [];

var ipg_cursor = '';

var root = 'http://9x9ui.s3.amazonaws.com/9x9miniV21/images/';
var errorthumb = 'http://9x9ui.s3.amazonaws.com/images/default_channel.png';
var gearthumb = root + 'icon_setting.png';

var wmode = 'transparent';
var thumbing = 'fullscreen';

var force_pairing = false;
var force_update = false;
var became = false;
var after_startup;

var flowplayer;
var fp_content = { url: 'http://9x9ui.s3.amazonaws.com/flowplayer.content-3.2.0.swf',
                   html: '', onClick: function() { $("#body").focus(); log ('FP CONTENT CLICK'); },
                   top: 0, left: 0, borderRadius: 0, padding: 0, height: '100%', width: '100%', opacity: 0 };

/* top left grid position of all clusters */
var top_lefts = [11, 14, 17, 41, 44, 47, 71, 74, 77];

var language_en =
  {
  episode: 'Episode',
  episodes: 'Episodes',
  episode_name: 'Episode',
  episode_lc: 'episode',
  episodes_lc: 'episodes',
  noeps: 'No episodes',
  noepchan: 'No episodes in this channel',
  ch: 'Ch',
  channel: 'Channel',
  channels: 'Channels',
  channel_lc: 'channel',
  channels_lc: 'channels',
  channel_word: 'Channel',
  channel_owner: "'s channels",
  sub: 'Subscriber',
  subs: 'Subscribers',
  sub_lc: 'subscriber',
  subs_lc: 'subscribers',
  updated: 'Updated',
  qyes: 'Yes',
  qno: 'No',
  hour: 'hour',
  minute: 'minute',
  day: 'day',
  month: 'month',
  year: 'year',
  ago: 'ago',
  hence: 'hence',
  onemoment: 'One Moment...',
  remuser: 'Are you sure you want to remove %1 from the user list?',
  nousers: 'No users',
  storehint: '[OK] to add this channel. [9x9] or [M] to Guide. [<][>] to flip set. [^][v] to flip channels in the current set.',
  playerhint: 'Press [9x9] or [M]<br>to go to Guide.',
  acceleration: 'Acceleration',
  chooselang: 'Choose the language in which you want to view 9x9.tv. This will only change the interface.',
  chooseregion: "Choose which region's content you would like to watch. This will only change the video content.",
  welcome: 'Welcome, <span style="color: orange">%1</span>',
  complete: 'Sign up complete.',
  startflipping: 'You may <strong>start flipping!</strong>',
  added: 'Added to your guide successfully!',
  set: 'Set',
  released: 'Remote control has been released',
  regch: 'Content region changed to: %1',
  preloadsav: 'Preload setting saved',
  ytqsav: 'YouTube quality setting saved',
  accsav: 'Hardware acceleration setting saved.',
  reqreload: 'Requires a reload to take effect.',
  langsav: 'Language setting saved',
  plzbrowse: 'Browse our <strong>Channel Store</strong>, add new channels to your personalized <strong>Channel Guide</strong>, and watch your favorite channels just like how you would on a TV!',
  startaddingch: 'Start adding channels',
  channelstore: 'Channel Store',
  run1ft: 'You are going to access the 9x9 PC version website, where you can edit your guide, but keyboard and mouse are needed.',
  subsuccess: 'Subscribed! Channel is in grid %1. Watch now?',
  alreadysub: 'Already subscribed. Channel is in grid %1. Watch now?',
  notconnected: 'Not connected',
  device: 'Device',
  set: 'Set',
  edituserlist: 'Edit User List',
  done: 'Done',
  welc9x9: 'Welcome to 9x9!',
  welc1: 'Sign up to access thousands of curated channels',
  welc2: 'Create your very own personalized channel guide',
  welc3: 'Watch your favorite channels just like watching TV!',
  welc4: "It's free and easy!",
  welc5: 'Sign in or Sign up now!',
  welc6: 'Use iPhone/iPad to remote control 9x9?',
  welc7: 'The name of this device is:',
  save: 'Save',
  cancel: 'Cancel',
  go: 'Go',
  chof: '(Ch %1 of %2)',
  newusers: 'New Users',
  returningusers: 'Returning Users',
  name: 'Name',
  email: 'Email',
  password: 'Password',
  gender: 'Gender',
  birthyear: 'Birth Year',
  s_9x9id: 'Your email',
  s_male: 'Male',
  s_female: 'Female',
  s_minpw: '6-character minimum',
  s_retype: 'Retype Password',
  s_example: 'Example',
  displaylanguage: 'Display Language',
  contentregion: 'Content Region',
  needhelp: 'Need Help?',
  plzvisit: 'Please visit 9x9.tv or email <a href="mailto:feedback@9x9cloud.tv">feedback@9x9cloud.tv</a>',
  signin_only: 'Sign In',
  iaccept: 'I Accept',
  s_disclaim1: 'Clicking I accept means that you agree to the 9x9 service agreement and privacy statement.',
  s_disclaim2: 'You also agree to receive email from 9x9 with service updates, special offers, and survey invitations.',
  s_disclaim3: 'You can unsubscribe at any time.',
  eoe: 'End of episodes'
  };

var language_zh =
  {
  episode: '節目集數',
  episodes: '節目集數',
  episode_name: '節目名稱',
  episode_lc: '集節目',
  episodes_lc: '集節目',
  noeps: '沒有節目',
  noepchan: '頻道無節目',
  ch: '頻道',
  channel: '頻道數',
  channels: '頻道數',
  channel_lc: '個頻道',
  channels_lc: '個頻道',
  channel_word: '頻道',
  channel_owner: '&nbsp;的頻道',
  ch_inside: '含%1個頻道',
  sub: '訂閱人數',
  subs: '訂閱人數',
  sub_lc: '個訂閱者',
  subs_lc: '個訂閱者',
  updated: '更新時間',
  qyes: '是',
  qno: '否',
  hour: '小時',
  minute: '分鐘',
  day: '天',
  month: '月',
  year: '年',
  ago: '前',
  hence: '後',
  onemoment: '稍待片刻...',
  remuser: '確定從常用用戶名單中移除%1？',
  nousers: '無用戶',
  storehint: '[OK] 訂閱此頻道. [9x9] 或 [M] 回頻道表. [<] [>] 換頻道組. [^] [v] 在此頻道組中換頻道.',
  playerhint: '[9x9] 或 [M]<br>回頻道表.',
  acceleration: '硬體加速',
  chooselang: '請選擇您所熟悉的顯示語言。這只會變更介面文字，不會改變提供給您的內容。',
  chooseregion: '請選擇您想觀看哪個文化或地區的內容，這不會影響網站所顯示的語言。',
  welcome: '歡迎, %1',
  complete: '註冊完成,',
  startflipping: '您可以開始使用<strong>遙控器！</strong>',
  added: '訂閱成功！',
  set: '頻道組',
  released: '遙控器已停止遙控。',
  regch: '內容地區變更至: %1',
  preloadsav: '預載節目設定已儲存',
  ytqsav: '解析度設定已儲存',
  accsav: '硬體加速設定已儲存',
  reqreload: '請重新載入以生效。',
  langsav: '語言與地區已儲存',
  plzbrowse: '瀏覽<strong>頻道商店</strong>，並訂閱新頻道至您的<strong>個人頻道表</strong>，即可像看電視一般的享受您最喜愛的頻道！',
  startaddingch: '立刻訂閱頻道',
  channelstore: '頻道商店',
  run1ft: '您即將進入9x9.tv的管理者模式，需要使用鍵盤與滑鼠進行編輯個人頻道表等各項管理功能。',
  subsuccess: '訂閱成功！頻道位於%1。立刻觀看？',
  alreadysub: '已訂閱過此頻道，頻道位於%1。立刻觀看？',
  notconnected: '尚未連線',
  device: '裝置',
  set: '頻道組',
  edituserlist: '編輯用戶名單',
  done: '完成',
  welc9x9: '歡迎來到9x9.tv!',
  welc1: '立刻註冊並觀看千百個策展頻道',
  welc2: '擁有並管理屬於您個人的雲端頻道表',
  welc3: '像看電視一般輕鬆的享受您最愛的頻道!',
  welc4: "免費使用而且非常簡單!",
  welc5: '立即登入或註冊',
  welc6: '使用iPhone或iPad遙控9x9.tv?',
  welc7: '這台裝置的號碼是:',
  save: '儲存',
  cancel: '取消',
  go: '前往',
  chof: '(頻道%2之%1)',
  newusers: '註冊',
  returningusers: '登入',
  name: '暱稱',
  email: 'Email',
  password: '密碼',
  gender: '性別',
  birthyear: '出生年',
  s_9x9id: '您的帳號(email)',
  s_male: '男性',
  s_female: '女性',
  s_minpw: '請輸入至少6位數',
  s_retype: '請再次輸入密碼',
  s_example: '範例',
  displaylanguage: '語言',
  contentregion: '地區',
  needhelp: '需要幫助嗎？',
  plzvisit: '請訪問 9x9.tv 或電郵至 <a href="mailto:feedback@9x9cloud.tv">feedback@9x9cloud.tv</a>',
  signin_only: '登入',
  iaccept: '同意',
  s_disclaim1: '當您按下[同意]時即表示您已同意9x9之服務條款與隱私權政策。',
  s_disclaim2: '您同時也訂閱了9x9關於服務更新、特別獻禮，以及用戶調查邀請等電子報， ',
  s_disclaim3: '但您可以隨時自行取消訂閱。',
  eoe: '頻道已播映完畢'
  };

var translations = language_en;

$(document).ready (function()
  {
  var now = new Date();

  if (timezero == 0)
    timezero = now.getTime();

  log ('ready, timezero=' + timezero);

  $("#bg-layer").css ("zIndex", "10");

  $("#sync-layer, #msg-layer, #sg-layer, #bg-layer, #volume-layer").hide();
  $("#player-layer, #bg-layer").show();

  $("#ch1-video, #ch2-video, #ch3-video").css ("position", "absolute");
  $("#ch1-video, #ch2-video, #ch3-video").html('');

  try { delete_piwik_cookies(); } catch (error) {};

  update_metadata();
  // switch_to_ipg_inner();

  var browser_language;
  if ($.browser.msie)
    browser_language = navigator.userLanguage;
  else
    browser_language = navigator.language;

  if (browser_language.match (/zh/i))
    set_language ('zh');
  else
    set_language ('en');

  if (false && navigator.userAgent.match (/(GoogleTV|Droid Build)/i))
    {
    nopreload = true;
    preload = 'off';
    }

  if ((location+'').match (/layers=(off|no|false)/))
    layers = false;

  /* default users */

  if ((location+'').match (/user=/))
    {
    user = (location+'').match (/user=([^&]+)/)[1];
    log ('user: ' + user);
    }
  else
    {
    var ucookie = getcookie (cookiename);
    if (ucookie)
      {
      user = ucookie;
      log ('user from cookie: ' + user + ', timezero=' + timezero);
      }
    else
      {
      force_pairing = true;
      log ('no user cookie');
      }
    }

  inherit_sphere_from_cookie();
  inherit_video_cookies();

  if ((location+'').match (/preload=(off|no|false)/))
    {
    nopreload = true;
    preload = 'off';
    }

  if ((location+'').match (/ytq=/))
    {
    yt_quality = (location+'').match (/ytq=([^&]+)/)[1];
    log ('youtube quality: ' + yt_quality);
    }

  if ((location+'').match (/wmode=/))
    {
    wmode = (location+'').match (/wmode=([^&]+)/)[1];
    log ('wmode: ' + wmode);
    }

  if ((location+'').match (/view=/))
    {
    jumpstart_channel = (location+'').match (/view=(\d+)/)[1];
    log ('jumpstart channel is: ' + jumpstart_channel);
    }

  if ((location+'').match (/relay=off/))
    $("#relaydiv").hide();
  else
    relay_using_swfobject();

  if (!force_pairing)
    startup();

  document.onkeydown=kp;

  $(window).resize (function() { elastic(); });
  elastic();

  $(window).unload (function() { unload(); });

  retrieve_device_info();

  redraw_ipg();
  setup_piwik();

  $("#msg-layer").css ("z-index", "1000");
  $("#open-description").addClass("ellipsis multiline")
  });

function inherit_sphere_from_cookie()
  {
  if (user != '' && user != 'Guest')
    {
    var sph = getcookie ('sphere-' + user);
    if (sph)
      {
      sphere = sph;
      log ('sphere inherited from cookie: ' + sphere);
      inherited_sphere = true;
      }
    }
  }

function inherit_video_cookies()
  {
  var c = getcookie ('tv-yt-quality');
  if (c)
    yt_quality = c;
  var c = getcookie ('tv-wmode');
  if (c)
    wmode = c;
  var c = getcookie ('tv-preload');
  if (c)
    {
    preload = c;
    nopreload = (preload == 'off');
    }
  }

function elastic()
  {
  var newWidth  = $(window).width()  / 16;
  var newHeight = $(window).height() / 16;

  var xtimes = newWidth  / 64 * 100;
  var ytimes = newHeight / 36 * 100;

  $("body").css ("font-size", ((xtimes >= ytimes) ? ytimes : xtimes) + "%");

  if (thumbing == 'store' || thumbing == 'store-wait')
    match_video_to_store();
  }

function unload()
  {
  relay_post ("QUIT");
  report_submit_();
  }

var program_cursor;
var tvpreview_kickstart = false;
var tvpreview_kickstart_reportflag = false;
var tvpreview_kickstart_player = 1;

var logcount=0;
function log (text)
  {
  try
    {
    logcount++;
    if (window.console && console.log)
      console.log (logcount + ' ' + text);
    report ('s', text);
    }
  catch (error)
    {
    }
  }

function log_and_alert (text)
  {
  log (text);
  alert (text);
  }

var relay_log_data = [];

function relay_log (direction, text)
  {
  relay_log_data.push ({ timestamp: new Date().getTime(), direction: direction, command: text });
  }

var video_log_data = [];

function video_log (forum, id, video_id)
  {
  video_log_data.push ({ timestamp: new Date().getTime(), forum: forum, id: id, video: video_id });
  }

/* player data record */
var pdr = '';
var n_pdr = 0;

function report (type, arg)
  {
  var delta = Math.floor ((new Date().getTime() - timezero) / 1000);

  pdr += ((new Date().getTime() / 1000) + '\t' + type + '\t' + arg + '\n');

  if (++n_pdr >= 200)
    report_submit_();
  }

function report_ (data, textStatus, jqXHR)
  {
  var lines = data.split ('\n');
  var fields = lines[0].split ('\t');
  if (fields [0] != '0')
    log ('[pdr] server error, ignoring: ' + lines [0]);
  else
    log ('[pdr] success');
  }

function report_error_ (jqXHR, textStatus, errorThrown)
  {
  log ('[pdr] error: ' + textStatus);
  }

function report_submit_()
  {
  n_pdr = 0;

  var delta = Math.floor ((new Date().getTime() - timezero) / 1000);

  var serialized;
  if (username == 'Guest')
    {
    serialized = 'device=' + device_id + '&' + 'session=' + 
           (timezero/1000) + '&' + 'time=' + delta + '&' + 'pdr=' + encodeURIComponent (pdr);
    }
  else
    {
    serialized = 'user=' + user + '&' + 'device=' + device_id + '&' + 'session=' + 
           Math.floor (timezero/1000) + '&' + 'time=' + delta + '&' + 'pdr=' + encodeURIComponent (pdr);
    }

  pdr = '';

  $.ajax ({ type: 'POST', url: "/playerAPI/pdr", data: serialized, 
              dataType: 'text', success: report_, error: report_error_ });
  }

function set_language (lang)
  {
  log ('set language: ' + lang);

  language = lang;
  translations = (language == 'zh') ? language_zh : language_en;

  $("#confirm-holder .head span").html (translations ['complete'] + '<br>' + translations ['startflipping']);
  $("#confirm-holder .main span").html (translations ['plzbrowse']);
  $("#btn-start-add span").html (translations ['startaddingch']);
  $("#store-title span").html (translations ['channelstore']);
  $("#open-meta span").eq(2).html (translations ['subs'] + ':');
  $("#open-meta span").eq(5).html (translations ['updated'] + ':');
  $("#device span").eq(0).html (translations ['device'] + ':');
  $("#selected-user .tail").html (translations ['channel_owner']);
  $("#set-titles .head").html (translations ['set'] + ': ');
  $("#ep-info .head").html (translations ['episode_name'] + ': ');
  $("#sync-holder .head span").html (translations ['welc9x9']);
  $("#values li:nth-child(1) span").html (translations ['welc1']);
  $("#values li:nth-child(2) span").html (translations ['welc2']);
  $("#values li:nth-child(3) span").html (translations ['welc3']);
  $("#sync-holder p:nth-child(3) span").html (translations ['welc4']);
  $("#sync-holder p:nth-child(4) span").html (translations ['welc5']);
  $("#sync-holder p:nth-child(5) span").html (translations ['welc6'] + '<br>' + translations ['welc7']);
  $("#btn-settings-save span").html (translations ['save']);
  $("#btn-settings-cancel span").html (translations ['cancel']);

  $("#signin-panel .input-list li:nth-child(1) span").html (translations ['s_9x9id'] + ':')
  $("#signin-panel .input-list li:nth-child(2) span").html (translations ['password'] + ':')
  $("#signin span").html (translations ['returningusers']);
  $("#signup span").html (translations ['newusers']);
  $("#btn-signin span").html (translations ['signin_only']);
  $("#signup-panel .input-list li").eq(0).children ("span").html (translations ['email'] + ':');
  $("#signup-panel .input-list li").eq(1).children ("span").html (translations ['password'] + ':');
  $("#signup-panel .input-list li").eq(2).children ("span").html (translations ['s_retype'] + ':');
  $("#signup-panel .input-list li").eq(3).children ("span").html (translations ['name'] + ':')
  $("#signup-panel .hint").html (translations ['s_minpw']);
  $("#signup-gender :nth-child(1) span").html (translations ['gender'] + ':');
  $("#signup-gender .radio-item").eq(0).children("span").html(translations ['s_male']);
  $("#signup-gender .radio-item").eq(1).children("span").html(translations ['s_female']);
  $("#signup-language :nth-child(1) span").html ('Display Language:');
  $("#signup-region .radio-item").eq(1).children("span").html ('中文');
  $("#birth-input span").html (translations ['birthyear'] + ':');
  $("#signup-language p:nth-child(1) span").html (translations ['displaylanguage']);
  $("#signup-region p:nth-child(1) span").html (translations ['contentregion']);

  $("#btn-signup span").html (translations ['iaccept']);
  $("#signup-panel .term-text span").html (translations ['s_disclaim1'] + translations ['s_disclaim2'] + translations ['s_disclaim3']);
  $("#signup-panel .help-txt span").html (translations ['needhelp'] + ' ' + translations ['plzvisit']);
  $("#entry-switcher .head span").html (translations ['needhelp']);
  $("#entry-switcher .content span").html (translations ['plzvisit']);

  var v = $("#signup-birthyear").val();
  if (v == language_en ['s_example'] + ': 1986' || v == language_zh ['s_example'] + ': 1986')
    $("#signup-birthyear").val (translations ['s_example'] + ': 1986');

  var sh = translations ['storehint'];
  sh = sh.replace ('[OK]', '<p><img src="' + root + 'btn_ok.png" class="icon-btn-ok"><span>');
  sh = sh.replace ('[9x9]', '</span></p><p><img src="' + root + 'btn_9x9.png" class="icon-btn-9x9"><span>');
  sh = sh.replace ('[M]', '<img src="' + root + 'btn_m.png" class="icon-btn-m">');
  sh = sh.replace ('[<]', '</span></p><p><img src="' + root + 'arrow_left.png" class="icon-btn-left">');
  sh = sh.replace ('[>]', '<img src="' + root + 'arrow_right.png" class="icon-btn-right"><span>');
  sh = sh.replace ('[^]', '</span></p><p><img src="' + root + 'arrow_up.png" class="icon-btn-up">');
  sh = sh.replace ('[v]', '<img src="' + root + 'arrow_down.png" class="icon-btn-down"><span>');
  sh = sh + '</span></p>';
  $("#store-hint").html (sh);

  var ph = '<span>' + translations ['playerhint'] + '</span>';
  ph = ph.replace ('[9x9]', '<img src="http://9x9ui.s3.amazonaws.com/9x9miniV21/images/btn_9x9.png" class="icon-btn-9x9">'); 
  ph = ph.replace ('[M]', '<img src="http://9x9ui.s3.amazonaws.com/9x9miniV21/images/btn_m.png" class="icon-btn-m">');
  $("#player-hint").html (ph);

  update_metadata();
  }

function formatted_time (t)
  {
  if (t == '' || isNaN (t) || t == undefined)
    return '--';

  var m = Math.floor (t / 60);
  var s = Math.floor (t) - m * 60;

  return m + ":" + ("0" + s).substring (("0" + s).length - 2);
  }

function fixed_up_program_name (name)
  {
  if (name.match (/YouTube user:? /i))
   name = name.replace (/YouTube user:? /i, '')
  if (name.match (/ channel$/i))
   name = name.replace (/ channel$/i, '')
  return name;
  }

function unique (noampflag)
  {
  var now = new Date();
  var seconds = now.getTime();
  if (noampflag)
    return 'rx=' + seconds;
  else
    return '&' + 'rx=' + seconds;
  }

function retrieve_device_info()
  {
  device_id = getcookie ('device');
  if (!device_id)
    {
    log ('no device id, obtaining a new one');
    var query = '/playerAPI/deviceRegister?' + unique (true);
    if (user && username != 'Guest')
      query += '?user=' + user;
    var d = $.get (query, function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        retrieve_device_info();
      else
        log ('UNABLE TO OBTAIN A DEVICE ID: ' + data);
      });
    return;
    }

  var d = $.get ('/playerAPI/deviceTokenVerify?device=' + device_id + unique(), function (data)
    {
    known_users = [];
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] != '0')
      {
      log ('device token "' + device_id + '" was invalid, removing it');
      deletecookie ("device");
      retrieve_device_info();
      return;
      }
    log ('device token "' + device_id + '" was valid');
    for (var i = 2; i < lines.length; i++)
      {
      if (lines[i] != '')
        {
        var fields = lines[i].split ('\t');
        if (fields.length == 2)
          known_users.push ({ token: fields[0], name: fields[1], email: 'user@domain' });
        else if (fields.length >= 3)
          known_users.push ({ token: fields[0], name: fields[1], email: fields[2] });
        log ('device user: ' + fields[0]);
        }
      }
    ipg_userbox();
    add_to_known_users();
    });
  }

function add_to_known_users()
  {
  if (user == '' || username == 'Guest')
    {
    if (known_users.length > 0)
      {
      /* try to get first user as token */
      user = known_users [0]['token'];
      log ('selected new user token from first available token: ' + user);
      setcookie ('user', user);
      message (translations ['onemoment'], 2000, "reload_page()");
      }
    return;
    }

  for (var u in known_users)
    {
    if (known_users [u]['token'] == user)
      return;
    }
  var d = $.get ('/playerAPI/deviceAddUser?device=' + device_id + '&' + 'user=' + user, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      retrieve_device_info();
    });
  }

function ipg_userbox()
  {
  if (device_displayed != '')
    {
    $("#device-name").html (prettify (device_displayed));
    }
  else
    {
    $("#device-name").html (translations ['notconnected']);
    relay_post ("WHOAMI");
    }

  if (username != '')
    $("#user-name").html (username);
  else
    $("#user-name span").html ('');

  var html = '';
  n_users = 0;
  for (var u in known_users)
    {
    if ('token' in known_users [u])
      {
      html += '<li class="user" id="user-' + known_users[u]['token'] + '"><p><span>' + known_users [u]['name'] + '</span><span class="remove"><img src="' + root + 'icon_remove_off.png" class="off"><img src="' + root + 'icon_remove_on.png" class="on"></span></p></li>';
      n_users++;
      }
    }
  if (html != '')
    {
    html += '<li class="list-control"><p id="btn-edit" class="btn"><span>' + translations ['edituserlist'] + '</span></p></li>';
    $("#user-list").html (html);
    if (user != '' && username != 'Guest')
      $("#user-" + user).addClass ("selected");
    $("#btn-edit").unbind();
    $("#btn-edit").click (edit_userlist);
    }
  else
    {
    $("#user-list").html (translations ['nousers']);
    $("#selected-user").show();
    }

  if (ipg_cursor < 0 && ipg_cursor > -900)
    {
    $("#selected-user").hide();
    $("#user-list").show();
    $("#user-list .user").unbind();
    $("#user-list .user").click (function() { ipg_individual_user_click ($(this).attr("id")); });
    cursor_on (ipg_cursor);
    }
  else
    {
    $("#user-list").hide();
    $("#selected-user").show();
    }
  }

function edit_userlist()
  {
  if (!$("#user").hasClass ("edit"))
    {
    $("#user").addClass ("edit");
    $("#btn-edit span").html (translations ['done']);
    }
  else
    {
    $("#user").removeClass ("edit");
    $("#btn-edit span").html (translations ['edituserlist']);
    }
  }

function clear_userlist()
  {
  log ('clear userlist');

  for (var u in known_users)
    if ('token' in known_users [u])
      var d = $.get ('/playerAPI/deviceRemoveUser?device=' + device_id + '&' + 'user=' + known_users[u]['token'], function (data)
        {
        var lines = data.split ('\n');
        var fields = lines [0].split ('\t');
        if (fields [0] == '0')
          retrieve_device_info();
        });

  deletecookie (cookiename);
  username = 'Guest';
  user = '';
  relay_post ("RELEASE");
  channelgrid = {};
  if (ipg_cursor < 0)
    ipg_cursor = first_channel();
  reload_page();
  }

function device_remove_user (id)
  {
  id = id.replace (/^user-/, '');
  log ('device remove user: ' + id);

  var cmd = '/playerAPI/deviceRemoveUser?device=' + device_id + '&' + 'user=' + id + unique();
  log ('cmd: ' + cmd);
  var d = $.get (cmd, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines [0].split ('\t');
    if (fields [0] == '0')
      {
      log ('user token ' + id + ' removed from device successfully');
      for (var i in known_users)
        if ('token' in known_users [i] && known_users [i]['token'] == id)
          delete (known_users [i]);
      ipg_userbox();
      if (user == id || known_users.length == 0)
        {
        delete_player_cookies();
        reload_page();
        }
      }
    else
      {
      log ('user token ' + id + ' FAILED TO BE REMOVED FROM DEVICE');
      log ('deviceRemoveUser returned: ' + data);
      }
    });
  }

function delete_player_cookies()
  {
  deletecookie ("user");
  deletecookie ("last_channel_" + user);
  deletecookie ("last_episode_" + user);
  }

function channels_in_guide()
  {
  var count = 0;
  for (var c in channelgrid)
    count++;
  return count;
  }

function first_channel()
  {
  for (var y = 1; y <= 9; y++)
    for (var x = 1; x <= 9; x++)
      {
      if (("" + y + "" + x) in channelgrid)
        return "" + y + "" + x;
      }
  log ("first_channel(): no channels!");
  return '11';
  }

function next_channel_square (channel)
  {
  for (var i = parseInt (channel) + 1; i <= 99; i++)
    {
    if (i in channelgrid)
      return i;
    }

  for (var i = 11; i <= parseInt (channel); i++)
    {
    if (i in channelgrid)
      return i;
    }

  log ("No next channel! (for " + channel + ")")
  }

function previous_channel_square (channel)
  {
  for (var i = parseInt (channel) - 1; i > 10; i--)
    {
    if (i in channelgrid)
      return i;
    }

  for (var i = 99; i >= parseInt (channel); i--)
    {
    if (i in channelgrid)
      return i;
    }

  log ("No previous channel!")
  }

function next_channel_square_setwise (channel)
  {
  var cluster = square_begets_cluster (channel);

  if (channel == cluster [cluster.length-1])
    {
    var current_cluster = which_cluster (channel);

    /* find the next nonempty cluster, return the first occupied square */
    for (var i = current_cluster + 1; i <= 9; i++)
      {
      var corner = top_lefts [i-1];
      if (!cluster_is_empty (corner))
        return first_channel_in_cluster (corner);
      }
    for (var i = 1; i <= current_cluster; i++)
      {
      var corner = top_lefts [i-1];
      if (!cluster_is_empty (corner))
        return first_channel_in_cluster (corner);
      }
    }

  for (var i = 0; i < cluster.length; i++)
    {
    if (cluster [i] == channel)
      return cluster [i+1];
    }
  }

function previous_channel_square_setwise (channel)
  {
  var cluster = square_begets_cluster (channel);

  if (channel == cluster [0])
    {
    var current_cluster = which_cluster (channel);

    /* find the next nonempty cluster, return the first occupied square */
    for (var i = current_cluster - 1; i > 0; i--)
      {
      var corner = top_lefts [i-1];
      if (!cluster_is_empty (corner))
        return last_channel_in_cluster (corner);
      }
    for (var i = 9; i >= current_cluster; i--)
      {
      var corner = top_lefts [i-1];
      if (!cluster_is_empty (corner))
        return last_channel_in_cluster (corner);
      }
    }

  for (var i = 1; i < cluster.length; i++)
    {
    if (cluster [i] == channel)
      return cluster [i-1];
    }
  }

/* return the cluster containing the id, empties removed */
function square_begets_cluster (id)
  {
  var ret = [];

  var cluster = which_cluster (id);
  var corner = top_lefts [cluster-1];

  var ty = Math.floor (corner / 10);
  var tx = corner % 10;

  var channels = '';
  var seq = '';

  for (y = ty; y < ty + 3; y++)
    for (x = tx; x < tx + 3; x++)
       {
       if ((y * 10 + x) in channelgrid)
         ret.push (y * 10 + x);
       }

  return ret;
  }

function which_cluster (pos)
  {
  var y = parseInt (pos / 10);
  var x = pos % 10;

  if (y <= 3)
    set = 1;
  else if (y <= 6)
    set = 4;
  else if (y <= 9)
    set = 7;

  if (x <= 3)
    set += 0;
  else if (x <= 6)
    set += 1;
  else if (x <= 9)
    set += 2;

  return set;
  }

function cluster_is_empty (grid)
  {
  var ty = Math.floor (parseInt (grid) / 10);
  var tx = parseInt (grid) % 10;

  for (y = ty; y < ty + 3; y++)
    for (x = tx; x < tx + 3; x++)
       {
       if ((y * 10 + x) in channelgrid)
         return false;
       }

  return true;
  }

function first_channel_in_cluster (id)
  {
  var cluster = square_begets_cluster (id);
  return cluster [0];
  }

function last_channel_in_cluster (id)
  {
  var cluster = square_begets_cluster (id);
  return cluster [cluster.length-1];
  }

function youtube_of (program)
  {
  if (program in programgrid)
    {
    var pinfo = programgrid [program];
    for (var field in { url1:0, url2:0, url3:0 })
      {
      if (field in pinfo && pinfo [field].match (/\?v=(.*)$/))
        return pinfo [field].match (/\?v=(.*)$/) [1];
      }
    }
  }

function program_lineup (real_channel)
  {
  program_line = [];
  n_program_line = 0;

  for (var p in programgrid)
    {
    if (programgrid [p]['channel'] == real_channel)
      program_line [n_program_line++] = p;
    }

  var channel;
  if (real_channel in pool)
    channel = pool [real_channel];
  else
    log ('**** CHANNEL ' + real_channel + ' IS NOT IN POOL');

  var nature = channel ['nature'];

  if (nature == '5' || nature == '8' || nature == '9' || (nature == '4' && channel ['sortorder'] != '2') || (nature == '6' && channel ['sortorder'] != '2'))
    {
    /* reverse order of position */
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [a]['sort']) - Math.floor (programgrid [b]['sort']) });
    program_line.unshift ('');
    }
  else if ((nature == '4' && channel ['sortorder'] == '2') || (nature == '6' && channel ['sortorder'] == '2'))
    {
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [b]['sort']) - Math.floor (programgrid [a]['sort']) });
    program_line.unshift ('');
    }
  else if (channel ['sortorder'] == '2')
    {
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [a]['timestamp']) - Math.floor (programgrid [b]['timestamp']) });
    program_line.unshift ('');
    }
  else
    {
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [b]['timestamp']) - Math.floor (programgrid [a]['timestamp']) });
    program_line.unshift ('');
    }
  }

function programs_in_real_channel (real_channel)
  {
  var num_programs = 0;

  for (p in programgrid)
    {
    if (programgrid [p]['channel'] == real_channel)
      num_programs++;
    }

  return num_programs;
  }

function ageof (timestamp, flag)
  {
  var age = '';
  var now = new Date();
  var ago_or_hence = 'ago';

  if (timestamp != '' && timestamp != undefined && timestamp != 'null')
    {
    var d = new Date (Math.floor (timestamp));

    var minutes = Math.floor ((now.getTime() - d.getTime()) / 60 / 1000);
    ago_or_hence = minutes < 0 ? translations ['hence'] : translations ['ago'];
    minutes = Math.abs (minutes);

    if (isNaN (minutes))
      return '';

    if (minutes > 59)
      {
      var hours = Math.floor ((minutes + 1) / 60);
      if (hours >= 24)
        {
        var days = Math.floor ((hours + 1) / 24);
        if (days > 30)
          {
          var months = Math.floor ((days + 1) / 30);
          if (months > 12)
            {
            var years = Math.floor ((months + 1) / 12);
            if (language == 'en')
              age = years + (years == 1 ? ' year' : ' years');
            else
              age = years + ' ' + translations ['year'];
            }
          else
            {
            if (language == 'en')
              age = months + (months == 1 ? ' month' : ' months');
            else
              age = months + ' ' + translations ['month'];
            }
          }
        else
          {
          if (language == 'en')
            age = days + (days == 1 ? ' day' : ' days');
          else
            age = days + ' ' + translations ['day'];
          }
        }
      else
        {
        if (language == 'en')
          age = hours + (hours == 1 ? ' hour' : ' hours');
        else
          age = hours + ' ' + translations ['hour'];
        }
      }
    else
      {
      if (language == 'en')
        age = minutes + (minutes == 1 ? ' minute' : ' minutes');
      else
        age = minutes + ' ' + translations ['minute'];
      }
    }
  else
    age = 'long'

  return (flag || age == 'long') ? (age + ' ' + ago_or_hence) : age;
  }

function startup()
  {
  log ('startup');

  var n = 0;
  var conv = {};

  for (var y = 1; y <= 9; y++)
    for (var x = 1; x <= 9; x++)
      conv [++n] = "" + y + "" + x;

  channelgrid = {};

  var d = $.get ("/playerAPI/channelLineup?user=" + user + '&' + 'setInfo=true' + unique(), function (data)
    {
    var lines = data.split ('\n');

    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('server error: ' + lines [0]);
      return;
      }

    for (var i = 2; i < lines.length && lines [i] != '--'; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');
        var cluster = top_lefts [parseInt (fields[0]) -1];
        log ('cluster: ' + cluster + ', type: ' + fields [3]);
        if (fields[0] >= 1 && fields[0] <= 9)
          {
          if (fields [4] == '0')
            {
            /* no restriction */
            set_types [cluster] = 0;
            }
          if (fields [4] == '1')
            {
            /* restricted */
            set_types [cluster] = fields[1];
            }
          else if (fields [4] == '2')
            {
            /* friends & family -- personal */
            set_types [cluster] = -1;
            }
          else if (fields [4] == '3')
            {
            /* my Youtube */
            set_types [cluster] = -2;
            }
          set_titles [cluster] = fields[2];
          }
        }
      }

    channelgrid = {};
    for (var j = i + 1; j < lines.length && lines [j] != '--'; j++)
      {
      if (lines [j] != '')
        {
        var fields = lines[j].split ('\t');
        var channel = { 'id': fields[1], 'name': fields[2], 'desc': fields[3], 'thumb': fields[4], 'count': fields[5], 'type': fields[6], 'status': fields[7], 'nature': fields[8], 'extra': fields[9], 'timestamp': fields[10], 'sortorder': fields[11], 'piwik': fields[12], leftoff: fields[13], curator: fields[14], subscribers: fields[15] };
        var nature = channel ['nature'];
        if (nature == '1' || nature == '3' || nature == '4' || nature == '5' || nature == '6' || nature == '7' || nature == '8' || nature == '9')
          {
          pool [fields[1]] = channel;
          channelgrid [conv [fields[0]]] = channel;
          log ('channel ' + channel ['id'] + ': ' + channel ['name']);
          }
        else
          log ('ignoring channel ' + channel ['id'] + ' of type: ' + channel ['nature']);
        }
      }

    redraw_ipg();
    ipg_cursor = first_channel();
    cursor_on (ipg_cursor);
    $("#user-list").hide();
    update_metadata();
    startup_phase_two();
    });

  fetch_brandinfo();

  if (user)
    {
    if (!initial_user)
      initial_user = user;
    var d = $.get ("/playerAPI/userTokenVerify?token=" + user + unique(), function (data)
      {
      log ('response to userTokenVerify: ' + data);
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        {
        for (var i = 2; i < lines.length; i++)
          {
          var fields = lines[i].split ('\t');
          if (fields[0] == 'name' && username == '')
            {
            log ('USERNAME: ' + fields[1]);
            username = fields[1];
            ipg_userbox();
            }
          if (fields[0] == 'sphere' && !inherited_sphere)
            {
            sphere = fields[1];
            inherited_sphere = true;
            }
          if (fields[0] == 'ui-lang')
            set_language (fields[1]);
          }
        }
      });
    }
  }

function startup_phase_two()
  {
  log ('startup phase two');

  if (!became || !initialized)
    {
    log ('initializing player');

    if (jumpstart_channel)
      current_channel = first_position_with_this_id (jumpstart_channel);
    else
      current_channel = first_channel();

    mini_player = 1;

    for (i = 1; i <= 3; i++)
      ytmini[i] = ytmini_video_id[i] = ytmini_why[i] = undefined;

    tvpreview_kickstart = false;
    tvpreview_yt_init();

    initialized = true;
    }

  if (force_update)
    {
    force_update = false;
    }
  else if (force_pairing)
    {
    force_pairing = false;
    // switch_to_ipg();
    }
  else if (current_channel == undefined)
    {
    log ('no channels!');
    switch_to_ipg();
    }
  else if (false && jumpstart_channel)
    {
    ipg_cursor = current_channel;
    jumpstart_channel = undefined;
    ipg_play();
    // tvpreview_set_channel (jumpstart_channel, undefined, true);
    }
  else if (!became)
    {
    // tvpreview_set_channel (current_channel, undefined, true);
    }

  $("#sync-layer").hide();
  if (thumbing == 'sync')
    {
    log ('was in sync mode!');
    switch_to_ipg_inner();
    }

  if (became)
    relay_post ("REPORT READY");

  if (after_startup)
    {
    var as = after_startup;
    after_startup = undefined;
    log ('after startup: executing: ' + as);
    eval (as)
    }
  else if (!became)
    {
    if (channels_in_guide() < 1)
      {
      log ('no channels in guide, entering store');
      store();
      }
    else
      {
      var ch = getcookie ("last_channel_" + user);
      var ep = getcookie ("last_episode_" + user);
      if (ch)
        {
        log ('last channel: ' + ch + ', last episode: ' + ep);
        ipg_cursor = first_position_with_this_id (ch);
        enter_channel();
        var channel = channelgrid [ipg_cursor];
        log ('ipg cursor: ' + ipg_cursor + ', programs: ' + n_program_line);
        if (channel ['youtube_completed'] && n_program_line < 1)
          {
          log ('most recently watched channel has no programs, entering ipg');
          switch_to_ipg();
          }
        else if (! channel ['youtube_completed'])
          {
          var callback = 'ipg_play_inner ("' + ep + '")';
          if (channel ['nature'] == '3' || channel ['nature'] == '4' || channel ['nature'] == '5')
            {
            if (channel ['youtubed'] && !channel ['youtube_completed'])
              {
              log ('joining channel fetch already in progress');
              fetch_yt_callbacks [ch] += '; ' + callback;
              }
            else
              fetch_programs_in (ch, callback);
            }
          else
            fetch_programs_in (ch, callback);
          }
        else
          ipg_play_inner (ep);
        }
      else
        {
        log ('no saved channel/episode, entering ipg');
        switch_to_ipg();
        }
      }
    }
  }

function fetch_brandinfo()
  {
  log ('fetch_brandinfo');
  var d = $.get ("/playerAPI/brandInfo?mso=" + brandinfo, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      for (var i = 2; i < lines.length; i++)
        {
        var fields = lines[i].split ('\t');
        if (fields[0] == 'fbtoken')
          fbtoken = fields[1];
        else if (fields[0] == 'locale')
          {
          locale = fields[1];
          if (!inherited_sphere)
            {
            if (locale == 'zh' || locale == 'tw')
              sphere = 'zh';
            else
              sphere = 'en';
            }
          }
        }
      }
    });
  }

function first_position_with_this_id (id)
  {
  for (var pos = 11; pos <= 99; pos++)
    if (pos in channelgrid && channelgrid [pos]['id'] == id)
      return pos;
  log ('yipes! real channel not found in channelgrid: ' + id);
  return 0;
  }

function tvpreview_set_real_channel (real_channel, episode_id, reportflag)
  {
  var index = first_position_with_this_id (real_channel);
  if (!index)
    {
    ipg_cursor = 0;
    current_channel = 0;
    tvpreview_set_channel_ (real_channel, episode_id, reportflag)
    }
  else
    tvpreview_set_channel (index, episode_id, reportflag);
  }

function tvpreview_set_channel (index, episode_id, reportflag)
  {
  ipg_cursor = index;
  current_channel = index;
  log ('tvpreview set channel ' + index);
  var real_channel = channelgrid [index]['id'];
  tvpreview_set_channel_ (real_channel, episode_id, reportflag)
  }

function fixup_store_hint()
  {
  if (recommend_set == 0)
    {
    // $("#store-hint span").html ('Press <img src="' + root + 'btn_ok.png" class="icon-btn-ok"> to change region. Press <img src="' + root + 'btn_9x9.png" class="icon-btn-9x9"> or <img src="' + root + 'btn_m.png" class="icon-btn-m"> to Guide.');
    }
  else
    {
    // $("#store-hint span").html ('Press <img src="' + root + 'btn_ok.png" class="icon-btn-ok"> to add this channel. Press <img src="' + root + 'btn_9x9.png" class="icon-btn-9x9"> or <img src="' + root + 'btn_m.png" class="icon-btn-m"> to Guide.');
    }
  }

function tvpreview_set_channel_ (real_channel, episode_id, reportflag)
  {
  program_cursor = 1;
  program_first = 1;

  if (!episode_id)
    episode_id = '';

  fixup_store_hint();

  var channel = pool [real_channel];
  if (!channel)
    {
    log ('tvpreview: channel not in pool: ' + real_channel);
    return;
    }

  log ('tvpreview set channel ' + channel ['id'] + ' :: nature: ' + channel ['nature'] + ', episode: ' + episode_id);

  stop_slideshow();
  $("#sg-layer, #bg-layer").hide();
  $("#player-layer, #bg-layer").show();

  if (programs_in_real_channel (channel ['id']) < 1)
    {
    var nature = channel ['nature'];
    if (nature == '3' || nature == '4' || nature == '5')
      {
      fetch_youtube_or_facebook (channel ['id'], 'tvpreview_set_channel_inner("' + real_channel + '","' + episode_id + '",' + reportflag + ')');
      return;
      }
    else if (nature == '1' || nature == '6' || nature == '7' || nature == '8' || nature == '9')
      {
      standard_channel (channel ['id'], 'tvpreview_set_channel_inner("' + real_channel + '","' + episode_id + '",' + reportflag + ')');
      return;
      }
    }
  else
    tvpreview_set_channel_inner (real_channel, episode_id, reportflag);
  }

function no_episodes()
  {
  remove_msg();
  reset_video_after_dialogue();
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    /* stay in store */
    }
  else if (thumbing == 'fullscreen')
    switch_to_ipg();
  }

function tvpreview_set_channel_inner (real_channel, episode_id, reportflag)
  {
  var channel = pool [real_channel];

  program_lineup (channel ['id']);
  log ('tvpreview_set_channel_inner: ' + episode_id);

  if (thumbing == 'store' || thumbing == 'store-wait')
    store_metadata();

  if (n_program_line < 1)
    {
    $("#msg-layer span").html (translations ['noepchan']);
    $("#msg-layer").show();
    hide_video_for_dialogue();
    if (thumbing == 'store' || thumbing == 'store-wait')
      {
      }
    else
      {
      // $("#player-layer, #store-layer").hide();
      // $("#sg-layer").show();
      }
    setTimeout ("no_episodes()", 3500);
    return;
    }

  if (episode_id != '')
    {
    program_cursor = undefined;
    for (var i = 1; i <= n_program_line; i++)
      {
      if (program_line [i] == episode_id 
             || program_line [i] == (channel ['id'] + '.' + episode_id) || youtube_of (program_line [i]) == episode_id)
        {
        program_cursor = i;
        log ('found ' + episode_id + ' at: ' + i);
        break;
        }
      }
    if (!program_cursor)
      {
      stop_video();
      log ('cannot find episode: ' + episode_id);
      message ('Cannot find this episode!', 6000, "switch_to_ipg()");
      return;
      }
    }
  else
    {
    program_cursor = goto_left_off_point();
    // program_cursor = 1;
    }

  var nature = channel ['nature'];
  if (nature == '1' || nature == '3' || nature == '4' || nature == '5' || nature == '6' || nature == '7' || nature == '8' || nature == '9')
    tvpreview_play_yt (reportflag)
  }

function goto_left_off_point()
  {
  log ('goto left off point');

  if (thumbing == 'store' || thumbing == 'store-wait')
    return 1;

  var ch = channelgrid [ipg_cursor];
  var pc = undefined;

  var leftoff, leftoff_full;

  if ('viewed' in ch)
    {
    leftoff = ch ['viewed'];
    leftoff_full = dot_qualify_program_id (ch ['id'], ch ['viewed'])
    }
  else if ((ch ['sortorder'] == '2' || ch ['sortorder'] == '3') && 'leftoff' in ch && ch ['leftoff'] != '')
    {
    leftoff = ch ['leftoff'];
    leftoff_full = dot_qualify_program_id (ch ['id'], ch ['leftoff'])
    }

  if (leftoff)
    {
    for (var i = 1; i <= n_program_line; i++)
      {
      if (program_line [i] == leftoff_full || youtube_of (program_line [i]) == leftoff)
        {
        pc = i;
        log ('left off episode ' + leftoff + ', at episode position ' + pc);
        break;
        }
      }
    if (!pc)
      log ('left off point not found in episode lineup');
    }

  if (!pc)
    pc = 1;

  log ('program cursor will be set to: ' + pc);
  return pc;
  }

function tvpreview_down()
  {
  log ('tvpreview down, channel is: ' + current_channel);
  tvpreview_set_channel (previous_channel_square_setwise (current_channel), undefined, true);
  }

function tvpreview_up()
  {
  log ('tvpreview up, channel is: ' + current_channel);
  tvpreview_set_channel (next_channel_square_setwise (current_channel), undefined, true);
  }

function tvpreview_left()
  { 
  log ('tvpreview left');
  if (program_cursor > 1)
    {
    program_cursor--;
    if (program_cursor < program_first)
      program_first -= 5;
    tvpreview_play_yt (true);
    }
  else if (program_cursor == 1)
    {
    /* program_cursor = n_program_line;
       tvpreview_play_yt (true); */
    }
  }

function tvpreview_right()
  {
  log ('tvpreview right');
  if (program_cursor < n_program_line)
    {
    program_cursor++;
    if (program_cursor > program_first + 4)
      program_first += 5;
    tvpreview_play_yt (true);
    }
  else if (program_cursor == n_program_line)
    {
    if (thumbing == 'store' || thumbing == 'store-wait')
      store_down();
    else
      {
      /* tvpreview_up(); */
      hide_video_for_dialogue();
      message (translations ['eoe'], 3000, "switch_to_ipg()");
      }
    }
  }

function tvpreview_yt_init()
  {
  log ('setting up youtube mini');

  /* ym0 -- background */
  /* ym1, ym2, ym3 -- video */
  /* ym4 -- mask */
  /* ym5 -- slideshow */
  /* ym6 -- osd */

  var vhtml = '';
  vhtml += '<div id="ym1" style="position: absolute; z-index: 27; height: 100%; width: 100%; top: 0; left: 0; visibility: visible">';
  vhtml += '<div id="ytapimini1"></div></div>';
  vhtml += '<div id="ym2" style="position: absolute; z-index: 25; height: 100%; width: 100%; top: 0; left: 0; visibility: visible">';
  vhtml += '<div id="ytapimini2"></div></div>';
  vhtml += '<div id="ym3" style="position: absolute; z-index: 25; height: 100%; width: 100%; top: 0; left: 0; visibility: visible">';
  vhtml += '<div id="ytapimini3"></div></div>';
  vhtml += '<div id="ym0" style="position: absolute; z-index: 26; height: 100%; width: 100%; top: 0; left: 0; visibility: visible; background-color: black">';
  vhtml += '<div id="ytapimini3"></div></div>';
  vhtml += '<div id="ym4" style="position: absolute; z-index: 28; height: 100%; width: 100%; top: 0; left: 0; display: none; background-color: black"></div>';
  vhtml += '<div id="ym5" style="position: absolute; z-index: 29; height: 100%; width: 100%; top: 0; left: 0; display: none; background-color: black"></div>';
  vhtml += '<div id="ym6" style="position: absolute; z-index: 30; right: 30px; top: 30px; display: none; border-radius: 10px; padding: 20px; text-color: black; font-family: Arial, Helvetica, clean, PMingLiU, sans-serif; background: -webkit-gradient(linear, left top, left bottom, from(#ccc), to(#666)); background: -moz-linear-gradient(top,  #ccc,  #666); text-align: center;"></div>';

  $("#video-layer").html (vhtml);

  if (thumbing == 'store' || thumbing == 'store-wait')
    video_z_base = 100;
  else
    video_z_base = 0;

  fixup_video_z_base();

  /* primary */
  var params = { allowScriptAccess: "always", wmode: wmode, disablekb: "1" };
  var atts = { id: "myytmini1" };
  var url = "http://www.youtube.com/apiplayer?version=3&enablejsapi=1&playerapiid=ytmini1";

  swfobject.embedSWF (url, "ytapimini1", "100%", "100%", "8", null, null, params, atts);

  if (layers)
    {
    /* preload */
    var params = { allowScriptAccess: "always", wmode: wmode, disablekb: "1" };
    var atts = { id: "myytmini2" };
    var url = "http://www.youtube.com/apiplayer?version=3&enablejsapi=1&playerapiid=ytmini2";

    swfobject.embedSWF (url, "ytapimini2", "100%", "100%", "8", null, null, params, atts);

    /* preload */
    var params = { allowScriptAccess: "always", wmode: wmode, disablekb: "1" };
    var atts = { id: "myytmini3" };
    var url = "http://www.youtube.com/apiplayer?version=3&enablejsapi=1&playerapiid=ytmini3";

    swfobject.embedSWF (url, "ytapimini3", "100%", "100%", "8", null, null, params, atts);
    }
  else
    $("#ym2, #ym3").hide();
  }

function hide_all_video()
  {
  log ('hide all video');
  clearInterval (forefront_timex);
  low_video_and_hide();
  for (var i = 1; i <= 3; i++)
    background (i);
  pause_and_mute_all_video();
  for (var i = 1; i <= 3; i++)
    try { ytmini [mini_player].clearVideo(); } catch (error) {};
  }

function low_video_and_hide()
  {
  $("#ym4").show();
  low_video();
  }

function low_video()
  {
  log ('low video');
  video_z_base = 0;
  fixup_video_z_base();
  }

function high_video()
  {
  log ('high video');
  video_z_base = 100;
  fixup_video_z_base();
  }

function fixup_video_z_base (quiet)
  {
  if (!quiet)
    log ('fixup video z base [' + thumbing + ', base is: ' + video_z_base + ']');

  for (var i = 0; i <= 6; i++)
    {
    var z = parseInt ( $("#ym" + i).css ("z-index") );
    if (video_z_base == 100 && z < 100)
      $("#ym" + i).css ("z-index", z + 100);
    else if (video_z_base == 0 && z > 100)
      $("#ym" + i).css ("z-index", z - 100);
    }
  if (video_z_base == 0)
    {
    if (thumbing == 'store' || thumbing == 'store-wait')
      $("#player-layer").css ("z-index", "1");
    else
      $("#player-layer").css ("z-index", "60");
    }
  else
    $("#player-layer").css ("z-index", "60");
  }

function next_channel_to_preload()
  {
  /* logic here differs from 1ft */
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    /* first channel of next set */
    var next_set = store_h_cursor + 1;
    if (next_set >= recommends.length)
      return 0;
    return recommends[next_set]['content'][1];
    }
  else
    return next_channel_square (current_channel);
  }





function forefront (i)
  {
  $("#ym" + i).css ("zIndex", video_z_base + 27);
  if (! $.browser.msie)
    $("#ym" + i).css ("left", "0px");
  }

function background (i)
  {
  $("#ym" + i).css ("zIndex", video_z_base + 25);
  if (! $.browser.msie)
    $("#ym" + i).css ("left", ( -3 * $(window).width() ) + "px");
  }

/* ------------- vv MASS OPERATIONS vv ------------- */

/* stop all videos except the current one */
function stop_other_youtube_videos()
  {
  for (var i = 1; i <= 3; i++)
    {
    if (i != mini_player)
      {
      try { ytmini[i].stopVideo(); } catch (error) {};
      ytmini_video_id [i] = undefined;
      ytmini_why [i] = undefined;
      }
    }
  }

function stop_all_youtube_videos()
  {
  for (var i = 1; i <= 3; i++)
    {
    try { ytmini[i].stopVideo(); } catch (error) {};
    ytmini_video_id [i] = undefined;
    ytmini_why [i] = undefined;
    }
  clearTimeout (awt_timex);
  }

/* stop all audio tracks except the current one */
function stop_other_audio_tracks()
  {
  for (var i = 1; i <= 3; i++)
    if (i != audio_player && audio_stream [i])
      {
      audio_stream [i].stop();
      audio_url [i] = undefined;
      }
  }

function stop_all_audio_tracks()
  {
  for (var i = 1; i <= 3; i++)
    if (audio_stream [i])
      {
      audio_stream [i].stop();
      audio_url [i] = undefined;
      }
  }

function pause_and_mute_all_other_audio()
  {
  for (var i = 1; i <= 3; i++)
    if (i != audio_player && audio_stream [i])
      {
      audio_stream [i].mute();
      audio_stream [i].pause();
      }
  }

function pause_and_mute_all_video()
  {
  for (var i = 1; i <= 3; i++)
    {
    try { ytmini[i].mute(); } catch (error) {};
    try { ytmini[i].pauseVideo(); } catch (error) {};
    }
  }

function pause_and_mute_everything()
  {
  pause_and_mute_all_video();
  for (var i = 1; i <= 3; i++)
    {
    if (audio_stream [i])
      {
      audio_stream[i].pause();
      audio_stream[i].mute();
      }
    }
  }

/* ------------- ^^ MASS OPERATIONS ^^ ------------- */


function tvpreview_play_yt()
  {
  if (thumbing == 'ipg')
    return;

  if (! (program_cursor in program_line))
    {
    log ('store_play_yt: cursor not in program line! program cursor is: ' + program_cursor);
    return;
    }

  var pid = program_line [program_cursor];
  log ('play: ' + pid);

  if (thumbing == 'fullscreen')
    {
    /* to remember where we left off. 10ft only */
    setcookie ("last_channel_" + user, channelgrid [current_channel]['id']);
    setcookie ("last_episode_" + user, pid);
    }

  var program = programgrid [pid];
  if (program ['type'] == '3')
    {
    play_this_audio (pid);
    preload_youtube_videos();
    /* if above line is turned off: */
    /* store_next_random_channel = select_a_random_channel_index(); */
    }
  else
    {
    stop_special_audio();
    play_this_youtube_video (pid);
    }
  }

var awt_timex;

function play_this_youtube_video (pid)
  {
  var url = best_url (pid);
  if (!url.match (/youtube\.com/))
    {
    log ('store_play_yt: "' + pid + '" not a YouTube video');
    stop_all_youtube_videos();
    return;
    }

  var program = programgrid [pid];
  $("#preview-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));
  /* 1ft player only: show_ep_title(); */

  var video_id = url.match (/v=([^&]+)/)[1];

  video_log (thumbing, pid, video_id);

  if (thumbing == 'player' || thumbing == 'player-wait')
    {
    $("#progress-bar").slider ("destroy");
    $("#progress-bar").slider
      ({
          start: function (event, ui) { slider_sliding = true; }, 
          stop:  function (event, ui) { slider_stop (event, ui); slider_sliding = false; }
      });
    }
  else
    $("#progress-bar").slider ("destroy");

  log ('store preview play yt: ' + video_id + ' (playing: ' +
           ytmini_video_id[1] + '/' + ytmini_why[1] + ', ' +
           ytmini_video_id[2] + '/' + ytmini_why[2] + ', ' +
           ytmini_video_id[3] + '/' + ytmini_why[3] + ')');

  var used_preload = false;
  for (var i = 1; i <= 3; i++)
    {
    if (!used_preload && ytmini[i] && ytmini_video_id[i] == video_id)
      {
      $("#ym4").show();
      // setTimeout ("play_youtube_start()", 600); <-- moved this lower
      log ('using preload in ytmini' + i);
      mini_player = i;
      if (thumbing != 'store' && thumbing != 'store-wait')
        try { ytmini[i].seekTo (0); } catch (error) {};
      try { ytmini[i].pauseVideo(); } catch (error) {};
      ytmini_why[i] = 'active';
      if (thumbing != 'store' && thumbing != 'store-wait')
        forefront (i); /* 10ft */
      if (on_video_start) /* 10ft */
        {
        var temp = on_video_start;
        on_video_start = undefined;
        log ('on_video_start callback: ' + temp);
        log ('############################################ on_video_start callback: ' + temp);
        eval (temp);
        }
      if (true) /* 10ft code here differs */
        try { track_episode (channelgrid [ipg_cursor]['id'], video_id); } catch (error) {};
      var youtube_thinks;
      try { youtube_thinks = ytmini[i].getVideoUrl(); } catch (error) {};
      log ('youtube thinks: ' + youtube_thinks);
      /* player_show_yt_quality(); <- 1ft only */
      if (!youtube_thinks || !youtube_thinks.match (/\?/) || youtube_thinks.match (/[\?\&]v=(\&|$)/))
        {
        log ('**** YOUTUBE IS CONFUSED ABOUT URL: ' + youtube_thinks);
        try { ytmini [i].loadVideoById (video_id, 0, yt_quality); } catch (error) {};
        }
      used_preload = true;
      setTimeout ("play_youtube_start()", 600);
      clearTimeout (awt_timex);
      awt_timex = setTimeout ('are_we_there_yet("' + thumbing + '","' + mini_player + '","' + video_id + '")', 7000);
      }
    else
      {
      background (i);
      try { ytmini[i].mute(); } catch (error) {};
      }
    }

  log ('** mini_player: ' + mini_player + ', is: ' + ytmini [mini_player]);

  if (ytmini [mini_player] != undefined)
    {
    tvpreview_kickstart = false;

    if (!used_preload)
      {
      log ('start video (no preload) in slot ' + mini_player + ': ' + video_id);
      try { ytmini [mini_player].loadVideoById (video_id, 0, yt_quality); } catch (error) {};
      if (player_mute)
        { try { ytmini [mini_player].mute(); } catch (error) {}; }
      else
        { try { ytmini [mini_player].unMute(); } catch (error) {}; }
      ytmini_video_id [mini_player] = video_id;
      ytmini_why [mini_player] = 'active';
      if (thumbing != 'store' && thumbing != 'store-wait')
        forefront (mini_player);
      if (on_video_start) /* 10ft */
        {
        var temp = on_video_start;
        on_video_start = undefined;
        log ('on_video_start callback: ' + temp);
        log ('############################################ on_video_start callback: ' + temp);
        eval (temp);
        }
      $("#ym4, #ym5").hide(); /* <-- 10ft only? */
      var youtube_thinks;
      try { youtube_thinks = ytmini[mini_player].getVideoUrl(); } catch (error) { youtube_thinks = 'error: ' + error.message; };
      log ('(no preload) youtube thinks: ' + youtube_thinks);
      /* player_show_yt_quality(); <- 1ft only */
      if (youtube_thinks.match (/^error/))
         {
        log ('ytmini' + mini_player + ' apparently not ready, setting kickstart');
        tvpreview_kickstart = true;
        return;
        }
      else if (!youtube_thinks || !youtube_thinks.match (/\?/) || youtube_thinks.match (/[\?\&]v=(\&|$)/))
        {
        log ('(no preload) **** YOUTUBE IS CONFUSED ABOUT URL: ' + youtube_thinks);
        try { ytmini [mini_player].loadVideoById (video_id, 0, yt_quality); } catch (error) {};
        }
      start_yt_mini_tick();
      if (true) /* 10ft code here differs */
        try { track_episode (channelgrid [ipg_cursor]['id'], video_id); } catch (error) {};
      show_osd();
      if (thumbing == 'store' || thumbing == 'store-wait')
        {
        // try { ytmini [mini_player].playVideo(); } catch (error) {};
        setTimeout ("play_youtube_start()", 600);
        }
      }

    preload_youtube_videos();

    if (thumbing == 'player' || thumbing == 'player-wait')
      {
      /* 1ft only -- 10ft uses 'fullscreen' */
      /* FIX -- must filter out non-volitional PLAY here! or somewhere else, somehow */
      relay_post ('PLAY ' + channelgrid [ipg_cursor]['id'] + ' ' + video_id)
      channelgrid [ipg_cursor]['viewed'] = pid;
      report ('w', channelgrid [ipg_cursor]['id'] + '\t' + video_id);
      }
    if (thumbing == 'fullscreen')
      {
      /* 10ft only */
      channelgrid [ipg_cursor]['viewed'] = pid;
      report ('w', channelgrid [ipg_cursor]['id'] + '\t' + video_id);
      }
    }
  else
    {
    log ('ytmini' + mini_player + ' not ready, setting kickstart');
    tvpreview_kickstart = true;
    }
  }

/* this checks to see if the video we are expecting is actually playing */
function are_we_there_yet (mode, player_id, video_id)
  {
  if ((thumbing == 'store' || thumbing == 'fullscreen') && current_tube != 'au' && thumbing == mode && mini_player == player_id)
    {
    log ('are we there yet? ' + mode + ' ' + player_id + ' ' + video_id);

    var yt_state = -2;
    try { yt_state = ytmini [mini_player].getPlayerState(); } catch (error) {};

    var current_video_id = '';
    try { video_url = ytmini [mini_player].getVideoUrl(); } catch (error) {};
    if (video_url.match (/[\?\&]v=([^\&]*)/))
      current_video_id = video_url.match (/[\?\&]v=([^\&]*)/)[1];

    log ('and presently at: ' + thumbing + ' ' + mini_player + ' ' + current_video_id);

    if (video_id == current_video_id && yt_state == -1)
      {
      log ('YouTube reports ' + video_id + ' as unstarted. starting again');
      try { ytmini [mini_player].loadVideoById (video_id, 0, yt_quality); } catch (error) {};
      // set_ambient_volume();
      }
    else if (video_id == current_video_id)
      log ('are we there yet? all is well!');
    else
      {
      if (yt_state == -1 && current_video_id == ''
             && (ytmini_video_id [mini_player] == video_id || ytmini_video_id [mini_player] == ''))
        {
        log ('YouTube reports it is not playing ' + video_id + ' -- starting it now');
        try { ytmini [mini_player].loadVideoById (video_id, 0, yt_quality); } catch (error) {};
        // set_ambient_volume();
        }
      else
        log ('are we there yet? moved on to another video (' + current_video_id + ')');
      }
    }
  }

function preload_youtube_videos()
  {
  if (preload == 'off')
    return;

  var next_program = parseInt (program_cursor) + 1;
  if (next_program > n_program_line)
    next_program = 1;

  var next_pid = program_line [next_program];
  var next_program = programgrid [next_pid];

  if (next_program ['type'] == '3')
    {
    stop_other_audio_tracks();
    preload_one_audio (next_pid);
    }
  else
    {
    var next_url = best_url (next_pid);
    var next_video_id = next_url.match (/v=([^&]+)/)[1];
    stop_other_youtube_videos();
    if (thumbing != 'store' && thumbing != 'store-wait')
      {
      log ('will try to preload next episode of this channel');
      preload_one_youtube_video (next_video_id, 'next-episode', 0);
      }
    }

  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    /* 10ft only */
    preload_next_set();
    }
  else
    preload_next_channel_first_episode();
  }

function preload_next_set()
  {
  var next_set = store_h_cursor + 1;
  if (next_set >= recommends.length)
    {
    /* next set is 0, which is language settings, and has no preload */
    return;
    }
  cat = recommends [next_set];
  if (cat ['content'].length < 1)
    {
    var query = '/playerAPI/setInfo?set=' + cat ['id'] + '&' + 'lang=' + sphere;
    if (query in rec_cache)
      {
      log ('preloading next set (index ' + next_set + '): using cache for ' + cat ['id']);
      if (parse_set_info_data (next_set, rec_cache [query]) == '0')
        preload_next_channel_first_episode();
      }
    else
      {
      log ('preloading_next_set (index ' + next_set + '): query: ' + query);
      var d = $.get (query, function (data)
        {
        rec_cache [query] = data;
        if (parse_set_info_data (next_set, data) == '0')
          preload_next_channel_first_episode();
        });
      }
    }
  else
    preload_next_channel_first_episode();
  }

function preload_next_channel_first_episode()
  {
  /* preload first episode of next channel */
  var next_channel = next_channel_to_preload();
  if (!next_channel)
    {
    log ('no next channel, no preload');
    return;
    }
  if (! (next_channel in pool))
    {
    log ('next channel not in pool, no preload');
    return;
    }
  var channel = pool [next_channel];
  if (channel ['nature'] == '2')
    {
    log ('next channel (' + next_channel + ') is not YouTube, no preload');
    return;
    }
  log ('will try to preload first episode of next channel ' + channel ['id']);
  if (programs_in_real_channel (channel ['id']) > 0)
    preload_next_channel_first_episode_inner (channel['id']);
  else
    fetch_programs_in (channel['id'], 'preload_next_channel_first_episode_inner("' + channel['id'] + '")');
  }

function preload_next_channel_first_episode_inner (id)
  {
  var channel = pool [id];
  var episode = first_program_in_real_channel (id);
  if (!episode)
    {
    log ('no episodes in ' + id + ' to preload');
    return;
    }

  var program = programgrid [episode];
  if (program ['type'] == '3')
    {
    preload_one_audio (episode);
    return;
    }
  var next_url = best_url (episode);
  var next_video_id;
  if (next_url.match (/v=([^&]+)/))
    next_video_id = next_url.match (/v=([^&]+)/)[1];
  else
    {
    log ("not preloading, this does not seem to be youtube: " + next_url);
    return;
    }

  log ('preload next yt, channel: ' + id + ', youtube: ' + next_video_id);

  var random_start_point = 0;
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    random_start_point = 8 + Math.floor (Math.random() * 12);
    log ('random start point: ' + random_start_point);
    }
  preload_one_youtube_video (next_video_id, 'next-channel', random_start_point)
  }

function preload_one_youtube_video (next_video_id, why, start_point)
  {
  for (var i = 1; i <= 3; i++)
    {
    if (ytmini_video_id[i] == next_video_id)
      {
      log ('preload: video is already in preloads: ' + next_video_id);
      return;
      }
    }

  for (var i = 1; i <= 3; i++)
    {
    if (!ytmini_video_id[i])
      {
      log ('preloading episode: ' + next_video_id);
      background (i);
      try { ytmini[i].loadVideoById (next_video_id, start_point, yt_quality); } catch (error) {};
      if (start_point > 0)
        setTimeout ("pause_preloaded_video (" + i + "," + start_point + ")", 5000);
      else
        try { ytmini[i].pauseVideo(); } catch (error) {};
      try { ytmini[i].mute(); } catch (error) {};
      ytmini_video_id[i] = next_video_id;
      ytmini_why[i] = why;
      return;
      }
    }
  }

function play_this_audio (pid)
  {
  log ('play this audio: ' + pid);

  pause_and_mute_all_video();

  current_tube = 'au';

  var pid = program_line [program_cursor];
  var program = programgrid [pid];
  var url = best_url (pid);
  url = url.replace (/^fp:/, '');

  log ('audio url is: ' + url);

  for (var i = 1; i <= 3; i++)
    {
    if (audio_url [i] == url)
      {
      log ('found preloaded audio program ' + pid + ' in slot: ' + i);
      audio_player = i;
      pause_and_mute_all_other_audio();
      play_special_audio (pid);
      return;
      }
    }

  /* no preload found, so keep the current slot and use it */

  audio_player = 1;
  audio_url [audio_player] = url;

  sm_start (audio_player, pid, url, 'play_special_audio("' + pid + '")');
  }

function preload_one_audio (pid)
  {
  log ('preload audio episode: ' + pid);

  var program = programgrid [pid];
  var url = best_url (pid);
  url = url.replace (/^fp:/, '');

  for (var i = 1; i <= 3; i++)
    {
    if (audio_url [i] == url)
      {
      log ('preload: audio is already in preloads: ' + pid);
      return;
      }
    }

  for (var i = 1; i <= 3; i++)
    {
    if (!audio_url [i])
      {
      log ('preloading audio episode ' + pid + ' in slot: ' + i + ', url: ' + url);
      sm_start (i, pid, url, 'audio_preload_is_ready("' + i + '")');
      return;
      }
    }
  }

function audio_preload_is_ready (slot)
  {
  log ('audio preload is ready: ' + slot);
  if (slot != audio_player && audio_stream [slot])
    {
    audio_stream [slot].mute();
    setTimeout ('audio_preload_is_ready_ii("' + slot + '")', 8000);
    }
  }

function audio_preload_is_ready_ii (slot)
  {
  log ('audio preload is ready ii: ' + slot);
  if (slot != audio_player && audio_stream [slot])
    {
    audio_stream [slot].pause();
    audio_stream [slot].setPosition (0);
    }
  }

function play_youtube_start()
  {
  log ('[* * * * * * * * * * * * play youtube start(' + mini_player + ') * * * * * * * * * * * * * * *]');
  if (thumbing == 'ipg')
    {
    log ('play youtube start: in ipg, stop.');
    return;
    }
  if (current_tube == 'au')
    return;

  for (i = 1; i <= 3; i++)
    if (i != mini_player)
      {
      background (i);
      try { ytmini [i].mute(); } catch (error) {};
      }

  if (player_mute)
    { try { ytmini [mini_player].mute(); } catch (error) {}; }
  else
    { try { ytmini [mini_player].unMute(); } catch (error) {}; }

  var state = -2;
  try { state = ytmini [mini_player].getPlayerState(); } catch (error) {};
  if (state == -1)
    {
    log ('player ' + mini_player + ' still unstarted! load video id: ' + ytmini_video_id [mini_player]);
    try { ytmini [i].loadVideoById (ytmini_video_id [mini_player], 0, yt_quality); } catch (error) {};
    }
 
  try { ytmini [mini_player].playVideo(); } catch (error) {};
  yt_set_volume (volume_setting);
  $("#ym4, #ym5").hide();
  show_osd();
  }

/* not used in 10ft for now */
function pause_preloaded_video (slot, start_point)
  {
  if (slot != mini_player)
    {
    log ('pause preloaded video (slot ' + slot + ') at: ' + start_point);
    try { ytmini[slot].pauseVideo(); } catch (error) {};
    try { ytmini[slot].seekTo (start_point); } catch (error) {};
    }
  }

function show_osd (permanent_flag)
  {
  if (thumbing == 'store' || thumbing == 'store-wait')
    return;

  clearTimeout (osd_timex);

  log ('[' + thumbing + '] show osd');

  if (current_channel != ipg_cursor)
    log ('**ASSERTFAILED*** current_channel (' + current_channel + ') != ipg_cursor (' + ipg_cursor + ')');

  var pid = program_line [program_cursor];
  var program = { name: '', timestamp: 0 };
  if (pid in programgrid)
    program = programgrid [pid];
  var dt = new Date (program ['timestamp']);
  var ch_name = '';

  /* new way */

  var grid = grid_location (ipg_cursor);
  $("#ch-info .head").html (translations ['ch'] + ' <strong>' + grid + '</strong>: ');

  if (ipg_cursor in channelgrid)
    ch_name = channelgrid [ipg_cursor]['name'];

  $("#ch-title").html ('&nbsp;' + ch_name);
  $("#ep-title").html (program ['name']);

  if (program ['timestamp'] != 0)
    $("#ep-age").html (ageof (dt, true));
  else
    $("#ep-age").html ("");

  $("#player-layer").removeClass ("on");
  $("#video-layer").css ({ width: "55em", height: "30.9375em", top: "0.25em", left: "4.5em" });
  $("#osd-layer").show();

  if (!permanent_flag)
    osd_timex = setTimeout ("remove_osd()", 7000);

  /* old way -- silver box */
  var html = '';
  html += '<b style="font-size: 135%">' + ch_name + '</b><br>';
  html += program ['name'];
  html += '<br><span style="font-size: 85%">' + ageof (dt, true) + '</span>';
  $("#ym6").html (html);
  // $("#ym6").show();
  }

function remove_osd()
  {
  clearTimeout (osd_timex);

  log ('[' + thumbing + '] remove osd');

  if (thumbing == 'store' || thumbing == 'store-wait')
    return;

  // old way
  // $("#ym6").hide();

  /* new way */
  $("#player-layer").addClass ("on");
  $("#video-layer").css ({ width: "100%", height: "100%", top: "0", left: "0" });
  $("#osd-layer").hide();
  }

function onYouTubePlayerReady (playerId)
  {
  log ("yt ready, id is: " + playerId);
  if (playerId == 'ytmini1')
    {
    ytmini[1] = document.getElementById ("myytmini1");
    try { ytmini[1].addEventListener ('onStateChange', 'tvpreview_yt_state_1'); } catch (error) {};
    }
  else if (playerId == 'ytmini2')
    {
    ytmini[2] = document.getElementById ("myytmini2");
    try { ytmini[2].addEventListener ('onStateChange', 'tvpreview_yt_state_2'); } catch (error) {};
    }
  else if (playerId == 'ytmini3')
    {
    ytmini[3] = document.getElementById ("myytmini3");
    try { ytmini[3].addEventListener ('onStateChange', 'tvpreview_yt_state_3'); } catch (error) {};
    }

  if (thumbing != 'fullscreen' && thumbing != 'store' && thumbing != 'store-wait')
    return;

  if (tvpreview_kickstart && ('ytmini' + tvpreview_kickstart_player == playerId))
    {
    log ('kickstarting ' + tvpreview_kickstart_player + '!');
    tvpreview_play_yt (tvpreview_kickstart_reportflag);
    }
  }

var ystates = { '-2': 'fail', '-1': 'unstarted', 0: 'ended', 1: 'playing', 2: 'paused', 3: 'buffering', 4: 'four', 5: 'video cued' };

/* YouTube API is botched, require three distinct functions to support multiple players */

function tvpreview_yt_state_1 (state)
  {
  tvpreview_yt_state (1, state);
  }

function tvpreview_yt_state_2 (state)
  {
  tvpreview_yt_state (2, state);
  }

function tvpreview_yt_state_3 (state)
  {
  tvpreview_yt_state (3, state);
  }

function tvpreview_yt_state (id, state)
  {
  // fail (-2), unstarted (-1), ended (0), playing (1), paused (2), buffering (3), video cued (5).

  if (state != ytmini_previous_state [id])
    {
    var video_url = '';
    var video_id = '';
    var star = (id == mini_player) ? '*' : '';
    try { video_url = ytmini [id].getVideoUrl(); } catch (error) {};
    if (video_url.match (/[\?\&]v=([^\&]*)/))
      video_id = ' [' + video_url.match (/[\?\&]v=([^\&]*)/)[1] + ']';
    log ('[' + star + 'tvpreview #' + id + '] yt state: ' + ytmini_previous_state [id] + ':' 
            + ystates [ytmini_previous_state [id]] + ' -> ' + state + ':' + ystates [state] + video_id);
    }

  if (id == mini_player && ytmini_previous_state [id] < 0 && state >= 1 && yt_mini_timex == 0)
    {
    // not needed -- this is for progress bar
    log ('starting ticker');
    start_yt_mini_tick();
    }

  if (state == 0 && (ytmini_previous_state [id] == 1 || ytmini_previous_state [id] == 2 || ytmini_previous_state [id] == 3))
    {
    /* change this as soon as possible */
    ytmini_previous_state [id] = state;
    log ('[tvpreview] yt eof');
    if (id == mini_player)
      {
      log ('auto-flipping to next episode');
      flip_next_episode();
      }
    return;
    }
  else if (state == 0 && ytmini_previous_state [id] == -1)
    {
    /* theoretically impossible, but sometimes happens under IE */
    ytmini_previous_state [id] = state;
    // log ('[tvpreview] yt confused, ignoring this event');
    if (id == mini_player)
      {
      log ('yt confused, but auto-flipping to next episode');
      flip_next_episode();
      }
    return;
    }
  else
    ytmini_previous_state [id] = state;
  }

var yt_mini_timex;
var ystates = { '-2': 'fail', '-1': 'unstarted', 0: 'ended', 1: 'playing', 2: 'paused', 3: 'buffering', 4: 'four', 5: 'video cued' };

function start_yt_mini_tick()
  {
  clearInterval (yt_mini_timex);
  last_tick_offset = undefined;
  yt_mini_timex = setInterval ("yt_mini_tick()", 3000);
  }

var last_tick_offset;

function yt_mini_tick()
  {
  var state = -2;
  try { state = ytmini [mini_player].getPlayerState(); } catch (error) {};

  if (state == -2 || state == -1 || state == 0)
    {
    log ('yt_mini_tick, STATE IS: ' + state + ' (' + ystates [state] + ')');
    clearTimeout (yt_mini_timex);
    yt_mini_timex = 0;
    return;
    }

  if (ytmini [mini_player] && ytmini [mini_player].getCurrentTime)
    {
    var offset = ytmini [mini_player].getCurrentTime();
    if (offset != last_tick_offset)
      relay_post ("REPORT TICK " + offset);
    last_tick_offset = offset;
    }
  }

function flip_next_episode()
  {
  log ('flip next episode');
  if (thumbing == 'store' || thumbing == 'store-wait')
    on_video_start = "move_to_forefront()";
  tvpreview_right();
  }

function fetch_programs_in (channel, callback)
  {
  log ('obtaining programs for ' + channel);

  var nature = pool [channel]['nature'];
  if (nature == '3' || nature == '4' || nature == '5')
    {
    log ('(obtaining: youtube)');
    if (fetch_youtube_or_facebook (channel, callback) == false)
      {
      /* already fetched */
      }
    return;
    }

  log ('(obtaining: standard)');
  var query = "/playerAPI/programInfo?channel=" + channel + '&' + 'user=' + user + unique();

  var d = $.get (query, function (data)
    {
    parse_program_data (data);
    if (callback) eval (callback);
    });
  }

function fetch_youtube_or_facebook (channel_id, callback)
  {
  var channel = pool [channel_id];

  if (!channel)
    {
    log ('fetch_youtube_or_facebook: channel "' + channel_id + '" does not exist!');
    return;
    }

  var was_youtubed = ('youtubed' in channel);
  log ('was youtubed: ' + was_youtubed);

  channel ['youtubed'] = true;
  var nature = channel ['nature'];

  if (nature == '3' || nature == '4' || nature == '5')
    {
    if (!was_youtubed)
      {
      fetch_yt_callbacks [channel_id] = callback;
      if (nature == '3')
        fetch_youtube_channel (channel_id);
      else if (nature == '4')
        fetch_youtube_playlist (channel_id);
      else if (nature == '5')
        fetch_facebook_playlist (channel_id);
      return true;
      }
    }

  return false;
  }

var fbdata;
var fbfeed;
var fbindex = {};

function fetch_facebook_playlist (channel_id)
  {
  var fbusername = pool [channel_id]['extra'];

  var fields = fbusername.split ('/');
  fbusername = fields [fields.length - 1]; 

  log ("FETCHING FACEBOOK PLAYLIST: " + fbusername);

  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://graph.facebook.com/' + fbusername + '&' + 'callback=fb_fetched_1';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
  }

function fb_fetched_1 (data)
  {
  clearTimeout (fetch_yt_timex);
  fbdata = data;

  var channel;

  if ('link' in fbdata)
    {
    var link = fbdata ['link'].toLowerCase();

    link = link.replace (/^https:/, 'http:')
    var link2 = link;

    if (link2.match (/^https?:\/\/www\.facebook/))
      link2 = link2.replace (/^https?:\/\/www\.facebook/, 'http://facebook');
    else if (link2.match (/^https?:\/\/facebook/))
      link2 = link2.replace (/^https?:\/\/facebook/, 'http://www.facebook');

    /* first, check the pool, it must receive precedence */
    for (var c in pool)
      {
      log (pool [c]['extra'].toLowerCase() + ' VS ' + link + ' OR ' + link2);
      if (pool [c]['extra'].toLowerCase() == link || pool [c]['extra'].toLowerCase() == link2)
        {
        log ('facebook fetched "' + fbdata ['link'] + '" channel info for pool channel: ' + c);
        channel = pool [c];
        break;
        }
      }

    if (!channel)
      {
      log ("******** unable to determine where Facebook channel goes: " + fbdata ['link']);
      $("#waiting").hide();
      switch_to_ipg();
      return;
      }

    channel ['fbid'] = fbdata ['id'];
    }
  else
    {
    log ('******** link not in fbdata!');
    $("#waiting").hide();
    notice_ok (thumbing, "An error on Facebook has occurred", "switch_to_ipg()");
    return;
    }

  if ('description' in fbdata)
    {
    channel ['desc'] = fbdata['description'].substring (0, 140);
    // ipg_metainfo();
    }
  else if ('company_overview' in fbdata)
    {
    channel ['desc'] = fbdata['company_overview'].substring (0, 140);
    // ipg_metainfo();
    }

  load_fb_feed (fbdata ['id']);
  fbindex [fbdata['id']] = fbdata['username'];
  }

function load_fb_feed (id)
  {
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  if (fbtoken)
    y.src = 'https://graph.facebook.com/' + id + '/feed' + '&' + 'callback=fb_fetched_2' + '&' + 'limit=200' + '&' + 'access_token=' + fbtoken;
  else
    y.src = 'http://graph.facebook.com/' + id + '/feed' + '&' + 'callback=fb_fetched_2' + '&' + 'limit=200';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
  }

function fb_fetched_2 (data)
  {
  var id;
  var channel;

  clearTimeout (fetch_yt_timex);
  fbfeed = data;

  if (fbfeed && 'data' in fbfeed && 0 in fbfeed['data'])
    {
    id = fbfeed['data'][0]['id'];
    log ('id scraped from feed: ' + id);
    }
  else
    {
    $("#waiting, #dir-waiting").hide();
    if (thumbing == 'ipg-wait')
      thumbing = 'ipg';
    return;
    }

  if (id && id.match (/(\d+)_(\d+)/))
    {
    var facebook_feed_id = id.match (/(\d+)_(\d+)/)[1];
    log ("facebook feed id: " + facebook_feed_id);

    for (var c in pool)
      {
      if (pool [c]['fbid'] == facebook_feed_id)
        {
        log ('feed found in pool channel: ' + c);
        channel = pool [c];
        break;
        }
      }
    }

  if (channel)
    real_channel = channel ['id'];
  else
    {
    log ('feed "' + facebook_feed_id + '" was not found in available channels');
    return;
    }

  var count = 0;

  if (id)
    for (var i in fbfeed['data'])
      {
      var link = fbfeed['data'][i]['link'];

      if (link && link.match ('(youtube\.com|youtu\.be)'))
        {
        var url = link;
        var title = fbfeed['data'][i]['name'];
        var thumb = fbfeed['data'][i]['picture'];
        var ts = 0;
        var duration = 0;

        var video_id = '';
        if (url.match (/\?v=/))
          video_id = url.match (/\?v=(...........)/)[1];
        else if (url.match (/\byoutu\.be\//))
          video_id = url.match (/\byoutu\.be\/(...........)/)[1];
        if (video_id == '')
          {
          log ('no video id! url="' + url + '" (probably a channel or playlist)');
          continue;
          }

        var dtime = fbfeed['data'][i]['updated_time'];
        // '2001-05-11T07:32:50+0000'
        dtime = dtime.substring (0, 19);
        if ($.browser.msie)
          dtime = ie_rearrange_date (dtime);
        var timestamp = new Date (dtime);
        var fbmessage = fbfeed['data'][i]['message'];
        var fbid = fbfeed['data'][i]['from']['id'];
        var fbfrom = fbfeed['data'][i]['from']['name'];
 
        log ('url scraped: ' + url + ', video id: ' + video_id + ', count: ' + count);

        count++;

        var program_id = real_channel + '.' + video_id;

        /* ignore older duplicates */
        if (program_id in programgrid)
          continue;

        var link = 'http://www.youtube.com/watch?v=' + video_id;

        programgrid [program_id] = { 'channel': real_channel, 'url1': 'fp:' + link, 
                                     'url2': '', 'url3': '', 'url4': '', 'name': title, 'desc': '', 'type': '',
                                     'thumb': thumb, 'snapshot': thumb, 'timestamp': timestamp, 'duration': -1,
                                     'fbmessage': fbmessage, 'fbid': fbid, 'fbfrom': fbfrom, 'sort': count };
        }
      else if (link)
        {
        /* not a youtube link */
        }

      // if we want the duration of the video:
      // http://gdata.youtube.com/feeds/api/videos/{video_id}?v=2&alt=json-in-script&callback=fubar
      }

  log ('youtube programs fetched for ' + channel ['id'] + ': ' + count);
  fetch_youtube_fin (channel ['id']);
  }

function fetch_youtube_playlist (channel_id)
  {
  var ytusername = pool [channel_id]['extra'];

  log ("FETCHING YOUTUBE PLAYLIST: " + ytusername);
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://gdata.youtube.com/feeds/api/playlists/' + ytusername + '?v=2' + '&' + 'alt=json-in-script' + '&' + 'format=5' + '&' + 'start-index=1' + '&' + 'max-results=50' + '&' + 'orderby=position' + '&' + 'prettyprint=true' + '&' + 'callback=yt_fetched';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);

  fetch_yt_timex = setTimeout ("fetch_youtube_fin(" + channel_id + ")", 30000);
  }

function fetch_youtube_channel (channel_id)
  {
  var ytusername = pool [channel_id]['extra'];

  if (ytusername && ytusername.match (/\//))
    ytusername = pool [channel_id]['extra'].match (/\/user\/([^\/]*)/)[1];

  log ("FETCHING YOUTUBE CHANNEL: " + ytusername);
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://gdata.youtube.com/feeds/api/users/' + ytusername + '/uploads?v=2' + '&' + 'alt=json-in-script' + '&' + 'format=5' + '&' + 'start-index=1' + '&' + 'max-results=50' + '&' + 'orderby=published' + '&' + 'prettyprint=true' + '&' + 'callback=yt_fetched';

  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);

  fetch_yt_timex = setTimeout ('fetch_youtube_fin("' + channel_id + '")', 30000);
  }

var feed;
var entry;
var yyprogram;
function yt_fetched (data)
  {
  var channel;
  var now = new Date();

  if (data && data.feed)
    {
    feed = data.feed;

    var name = feed.author[0].name.$t;
    name = name.toLowerCase();

    // playlists are different
    // "tag:youtube.com,2008:playlist:45f1353372bc22eb"
    var ytid = feed.id.$t;
    if (ytid.match (/playlist:/))
      name = ytid.match (/playlist:(.*)$/)[1];

    /* first, check the pool, it must receive precedence */
    for (var c in pool)
      {
      if (pool [c]['extra'] == name)
        {
        log ('youtube fetched "' + name + '" channel info for pool channel: ' + c);
        channel = pool [c];
        break;
        }
      }

    if (!channel)
      {
      log ("******** unable to determine where YouTube channel goes: " + name);
      return;
      }

    var entries = feed.entry || [];

    for (var i = 0; i < entries.length; i++)
      {
      entry = entries[i];
      var id = entry.id.$t;
      var title = entry.title.$t;
      var updated = entry.updated.$t;

      var video_id = entry.media$group.yt$videoid.$t;
      var duration = entry.media$group.yt$duration.seconds;

      var dtime = entry.media$group.yt$uploaded.$t;
      if ($.browser.msie)
        dtime = ie_rearrange_date (dtime);
      var timestamp = new Date (dtime);
 
      duration = formatted_time (duration);

      log ("YOUTUBE " + video_id + " EPISODE: " + title);

      var thumb = entry.media$group.media$thumbnail[1]['url'];

      var ts = timestamp.getTime();
      if (ts == undefined || isNaN (ts) || ts == Infinity)
        ts = now.getTime();

      var program_id = channel ['id'] + '.' + video_id;
      programgrid [program_id] = { 'channel': channel ['id'], 'url1': 'fp:http://www.youtube.com/watch?v=' + video_id, 
                                   'url2': '', 'url3': '', 'url4': '', 'name': title, 'desc': '', 'type': '',
                                   'thumb': thumb, 'snapshot': thumb, 'timestamp': ts, 'duration': duration, 'sort': i+1 };
      }

    log ('youtube programs fetched for ' + channel ['id'] + ': ' + entries.length);
    fetch_youtube_fin (channel ['id']);
    }
  else
    log ('***** youtube fetch, data was incomplete ****');
  }

function fetch_youtube_fin (channel_id)
  {
  clearTimeout (fetch_yt_timex);
  log ('fetch youtube fin, channel_id: ' + channel_id);

  if (pool [channel_id]['youtube_completed'])
    {
    /* this is a stray timeout, discard it */
    log ('fetch youtube fin: discarding this transaction');
    return;
    }

  pool [channel_id]['youtubed'] = true;
  pool [channel_id]['youtube_completed'] = true;

  if (channel_id in fetch_yt_callbacks)
    {
    log ("CALLBACK1: " + fetch_yt_callbacks [channel_id]);
    eval (fetch_yt_callbacks [channel_id]);
    }

  update_metadata();
  }

function ie_rearrange_date (dt)
  {
  /* IE does not understand the YouTube date format. Transform:
     2011-03-03T04:04:01.000Z -> 3 March 2011 04:04:01 */
 
  var months = { 1: 'January', 2: 'February', 3: 'March', 4: 'April', 5: 'May', 6: 'June',
                 7: 'July', 8: 'August', 9: 'September', 10: 'October', 11: 'November', 12: 'December' };
 
  var mo = months [Math.floor (dt.substring(5,7))];
 
  return (Math.floor (dt.substring(8,10)) + ' ' + mo + ' ' + dt.substring(0,4) + ' ' + dt.substring (11,19));
  }

function best_url (program)
  {
  var desired;

  if (! (program in programgrid))
    {
    log ('program "' + program + '" not in programgrid!');
    return '';
    }

  desired = '(mp4|m4v|flv)';

  if (navigator.userAgent.match (/(GoogleTV|Droid Build)/i))
    desired = '(mp4|m4v)';

  else if (navigator.userAgent.match (/(Opera|Firefox)/))
    desired = 'webm';

  else if (navigator.userAgent.match (/(Safari|Chrome)/))
    desired = '(mp4|m4v)';

  var ext = new RegExp ('\.' + desired + '$');

  if (programgrid [program]['url1'].match (ext))
    {
    return programgrid [program]['url1'];
    }
  else if (programgrid [program]['url2'].match (ext))
    {
    return programgrid [program]['url2'];
    }
  else if (programgrid [program]['url3'].match (ext))
    {
    return programgrid [program]['url3'];
    }
  else if (programgrid [program]['url4'].match (ext))
    {
    return programgrid [program]['url4'];
    }
  else
    {
    for (var f in { url1:'', url2:'', url3:'', url4:'' })
      {
      var p = programgrid [program][f];
      if (! (p.match (/^(|null|jw:null|jw:|fp:null|fp:)$/)))
        return p;
      }
    return '';
    }
  }

function first_program_in_real_channel (real_channel)
  {
  var programs = [];
  var n_programs = 0;

  for (p in programgrid)
    {
    if (programgrid [p]['channel'] == real_channel)
      programs [n_programs++] = p;
    }

  if (programs.length < 1)
    {
    log ('No programs in real channel: ' + real_channel);
    return 0;
    }

  var channel = pool [real_channel];
  var nature = channel ['nature'];

  // unshift here is to match what is in program_line

  if (nature == '5' || nature == '8' || nature == '9' || (nature == '4' && channel ['sortorder'] != '2') || (nature == '6' && channel ['sortorder'] != '2'))
    {
    /* reverse order of position */
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [a]['sort']) - Math.floor (programgrid [b]['sort']) });
    programs.unshift ('');
    }
  else if ((nature == '4' && channel ['sortorder'] == '2') || (nature == '6' && channel ['sortorder'] == '2'))
    {
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [b]['sort']) - Math.floor (programgrid [a]['sort']) });
    programs.unshift ('');
    }
  else if (channel ['sortorder'] == '2')
    {
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [a]['timestamp']) - Math.floor (programgrid [b]['timestamp']) });
    programs.unshift ('');
    }
  else
    {
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [b]['timestamp']) - Math.floor (programgrid [a]['timestamp']) });
    programs.unshift ('');
    }

  return programs [1];
  }

function kp (e)
  {
  var ev = e || window.event;
  log ('[' + thumbing + '] ' + ev.type + " keycode=" + ev.keyCode);

  var special_codes = { 8:0, 116:0, 121:0, 167:0, 168:0, 169:0, 170:0, 171:0, 172:0 };

  /* let backspace filter through to forms */
  if (ev.keyCode == 8 && (thumbing == 'signin' || thumbing == 'diagnostics' || thumbing == 'ask'))
    {
    keypress (ev.keyCode);
    return;
    }

  /* prevent F5 from refreshing */ 
  if (ev.keyCode in special_codes)
    {
    if (ev.preventDefault)
      {
      ev.preventDefault();
      ev.stopPropagation();
      }
    else
      {
      ev.keyCode = 0;
      ev.returnValue = false;
      ev.cancelBubble = true;
      }
    }

  keypress (ev.keyCode);
  }
    
function keypress (keycode)
  { 
  if ((thumbing == 'signin' || thumbing == 'diagnostics') && keycode != 27)
    return;

  if (keycode == 'store-wait')
    play_error_noise();

  switch (keycode)
    {
    case 32:
      /* space */
      pause_video();
      break;

    case 88:
      /* x */
      stop_video();
      if (thumbing == 'store')
        flip_next_episode();
      break;

    case 83:
      /* s */
      break;

    case 37:
      /* left arrow */
    case 75:
      /* k -- RC "program pre" */
      if (thumbing == 'ipg')
        ipg_left();
      else if (thumbing == 'ask')
        ask_left();
      else if (thumbing == 'store')
        store_left();
      else if (thumbing == 'fullscreen')
        tvpreview_left();
      else if (thumbing == 'settings')
        settings_left();
      break;

    case 39:
      /* right arrow */
    case 76:
      /* l -- RC "program next" */
      if (thumbing == 'ipg')
        ipg_right();
      else if (thumbing == 'ask')
        ask_right();
      else if (thumbing == 'store')
        store_right();
      else if (thumbing == 'fullscreen')
        tvpreview_right();
      else if (thumbing == 'settings')
        settings_right();
      break;

    case 38:
    case 33:
      /* up arrow */
    case 78:
      /* n -- RC "next" */
      if (thumbing == 'ipg')
        ipg_up();
      else if (thumbing == 'store')
        store_up();
      else if (thumbing == 'fullscreen')
        tvpreview_up();
      else if (thumbing == 'settings')
        settings_up();
      break;

    case 40:
    case 34:
      /* down arrow */
    case 86:
      /* v -- RC "pre" */
      if (thumbing == 'ipg')
        ipg_down();
      else if (thumbing == 'store')
        store_down();
      else if (thumbing == 'fullscreen')
        tvpreview_down();
      else if (thumbing == 'settings')
        settings_down();
      break;

    case 81:
      /* q - quit */
      log ('close attempt');
      window.close();
      try
        {
        var f = getflash ("relay");
        f.close_window();
        }
      catch (error)
        {
        log ('error closing window: ' + error);
        }
      // window.open('','_parent',''); 
      // window.close();
      // window.location.href = "javascript:window.opener=self; window.close()";
      break;

    case 84:
      /* t -- diagnostics */
      diagnostics();
      break;

    case 187:
    case 107:
    case 61:
      /* = -- RC "vol up" */
      yt_volume_up();
      break;

    case 189:
    case 109:
      /* - -- RC "vol down" */
      yt_volume_down();
      break;

    case 85:
      /* u -- RC "mute" */
      yt_mute_toggle();
      break;

    case 27:
      /* ESC */
      if (thumbing == 'diagnostics' || $("#help-layer").css ("display") == 'block')
        {
        diagnostics_close();
        switch_to_ipg();
        }
      else if (thumbing == 'tutorial' || $("#tutorial-layer").css ("display") == 'block')
        {
        $("#tutorial-layer").hide();
        thumbing = tutorial_saved_thumbing;
        }
      else if (thumbing == 'confirm')
        store();
      else if (thumbing == 'signin')
        signin_close();
      else if (thumbing == 'ask')
        ask_enter (3);
      else if (thumbing == 'settings')
        settings_close();
      else
        switch_to_ipg();
      break;

    case 17:
      /* ctrl key */
      break;

    case 36:
      /* home */
    case 77:
      /* M */
    case 119:
      /* remote control 9x9 key */
    case 72:
      /* h */
    case 73:
      /* i */
    case 220:
      /* \ */
      switch_to_ipg();
      break;

    case 46:
      /* del */
      break;

    case 13:
      /* CR */
    case 80:
      /* play */
    case 121:
      /* ok */
      if (thumbing == 'ipg')
        ipg_play();
      else if (thumbing == 'ask')
        ask_enter (yesno_cursor);
      else if (thumbing == 'store')
        store_ok();
      else if (thumbing == 'tutorial')
        tutorial_ok();
      else if (thumbing == 'sync')
        signin_page();
      else if (thumbing == 'confirm')
        store();
      else if (thumbing == 'settings')
        settings_ok();
      else if ((keycode == 13 || keycode == 121) && thumbing == 'fullscreen')
        pause_video();
      else
        play_video();
      break;

    default:
      // alert ("KEY " + keycode);
      break;
    }
  }

function pause_video()
  {
  if (current_tube == 'au')
    {
    if (audio_stream [audio_player] && audio_stream [audio_player].paused)
      {
      audio_resume();
      remove_osd();
      }
    else
      {
      audio_pause();
      show_osd (true);
      }
    }
  try
    {
    if (ytmini[mini_player].getPlayerState() == 2)
      {
      ytmini[mini_player].playVideo();
      remove_osd();
      }
    else
      {
      ytmini[mini_player].pauseVideo();
      show_osd (true);
      }
    }
  catch (error)
    {
    };
  }

function play_video()
  {
  try { ytmini[mini_player].playVideo(); } catch (error) {};
  }

function stop_video()
  {
  // log ('stop video');

  for (var i = 1; i <= 3; i++)
    {
    try { ytmini [i].stopVideo(); } catch (error) {};
    ytmini_video_id[i] = undefined;
    ytmini_why[i] = undefined;
    }

  $("#ym4").show();
  $("#ym5, #ym6").hide();

  stop_slideshow();
  }

var ssdata;

function fetch_slideshow (episode_id)
  {
  log ("FETCHING SLIDESHOW: " + episode_id);

  ssdata = undefined;

  var url = best_url (episode_id);
  url = url.replace (/^fp:/, '');

  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  // y.src = 'http://slides.teltel.com/slideshow/get_slides.php?id=' + episode_id + '&' + 'callback=slideshow_fetched';
  y.src = url + '&' + 'callback=slideshow_fetched';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
  }

function slideshow_fetched (data)
  {
  log ('slideshow fetched');
  ssdata = data;
  play_slideshow();
  }

function start_play_slideshow (url)
  {
  log ('play slideshow: ' + url);
  $("#ym4").show();
  show_osd();
  var pid = program_line [program_cursor];
  fetch_slideshow (pid);
  }

var n_slides = 0;
var current_slide = 1;
var ss_timex = 0;

function play_slideshow()
  {
  $("#ym5").html ('');

  for (var i = 1; i <= 3; i++)
    try { ytmini[i].mute(); } catch (error) {};

  clearTimeout (ss_timex);
  ss_timex = 0;

  n_slides = ssdata['slides'].length;
  log ('play_slideshow, ' + n_slides + ' slides');

  current_slide = 1;

  if (n_slides < 1)
    {
    tvpreview_right();
    return;
    }

  redraw_slideshow_html();

  if (flowplayer)
  flowplayer ("audio", "http://releases.flowplayer.org/swf/flowplayer-3.2.7.swf",
    {
    plugins: { controls: null, content: fp_content },
    // plugins: { controls: { fullscreen: false, height: 30, autoHide: false } },
    clip: { autoPlay: false, onBeforeBegin: function() { $f("player").close(); } },
    key: ['#@02568d345b51c565a77', '#@094b173a0a25655269e', '#@8b75f0276f3becc2f3f', '#@5d96176a271ed2e10e8']
    });

  $("#ym5").show();

  $("#slide-1").fadeIn();
  try { flowplayer("audio").play(); } catch (error) {};

  ss_timex = setTimeout ("next_slide()", 5000);
  }

function redraw_slideshow_html()
  { 
  var n = 0;
  var html = '';

  html += '<div id="audio" style="display:block;width:750px;height:30px;visibility:hidden" href="http://9x9ui.s3.amazonaws.com/chillout-long.mp3"></div>';

  var aspect = $(window).width() / $(window).height();

  for (var i = 0; i < ssdata['slides'].length; i++)
    {
    n++;
    var slide_aspect = ssdata['slides'][i]['x'] / ssdata['slides'][i]['y'];

    var rsz;
    if (slide_aspect > aspect)
      rsz = ' style="display: block; width: 100%; height: auto; margin-left: auto; margin-right: auto;" ';
    else
      rsz = ' style="display: block; width: auto; height: 100%; margin-left: auto; margin-right: auto;" ';

    var display = (n == current_slide) ? "block" : "none";

    html += '<div id="slide-' + n + '" class="slide" style="display: ' + display + '; position: absolute; top: 0; left: 0; width: 100%; height: 100%"><img src="' + ssdata['slides'][i]['url'] + '" ' + rsz + '></div>';
    }

  $("#ym5").html (html);
  }

function next_slide()
  {
  clearTimeout (ss_timex);
  ss_timex = 0;

  if (current_slide == n_slides)
    {
    if ($("#ym5").css ("display") == 'block')
      ss_timex = setTimeout ("slideshow_ended()", 7000);
    return;
    }

  // $("#slide-" + current_slide).css ("opacity", "0");
  $("#slide-" + current_slide).fadeOut();

  current_slide++;
  log ('next slide: ' + current_slide);

  // $("#slide-" + current_slide).css ("opacity", "1");
  // $("#slide-" + current_slide).css ("left", ($(window).width() - $("#slide-" + current_slide + " img").width()) / 2);
  $("#slide-" + current_slide).fadeIn();

  if ($("#ym5").css ("display") == 'block')
    ss_timex = setTimeout ("next_slide()", 7000);
  }

function slideshow_ended()
  {
  thumbing = 'fullscreen';
  clearTimeout (ss_timex);
  ss_timex = 0;
  log ('slideshow ended');
  stop_slideshow_audio();
  $("#ym4").show();
  $("#ym5").hide();
  tvpreview_right();
  }

function stop_slideshow()
  {
  var old_ss_timex = ss_timex;
  clearTimeout (ss_timex);
  ss_timex = 0;

  if (thumbing == 'slideshow')
    {
    log ('stop slideshow');
    stop_slideshow_audio();
    $("#ym4").show();
    $("#ym5").hide();
    thumbing = 'fullscreen';
    }
  }

function stop_slideshow_audio()
  {
  try { flowplayer("audio").stop(); } catch (error) {};
  }

/* ------------------------------------------------------ vv AUDIO vv ------------------------------------------------------ */


// var slide_container = '#ss';  /* for 1ft */
var slide_container = '#ym5'; /* for 10ft */

sm_start_timex = [];

function sm_start (slot, id, url, callback)
  {
  if (slot in sm_start_timex)
    clearTimeout (sm_start_timex [slot]);

  log ('sm_start :: slot: ' + slot + ', id: ' + id + ', url: ' + url);

  if (audio_stream [slot])
    {
    audio_stream [slot].destruct();
    audio_stream [slot] = undefined;
    }

  audio_url [slot] = url;
  audio_ready [slot] = false;
  audio_callback [slot] = callback;

  audio_stream [slot] = soundManager.createSound
    ({
    id: 'slot-' + slot,
    url: url,
    autoLoad: true,
    autoPlay: false,
    stream: true,
    onload: function()
      {
      log ('sm: sound loaded: ' + this.sID + ', callback: ' + callback);
      audio_ready [this.sID] = true;
      eval (audio_callback [slot]);
      },
    onfinish: function()
      {
      log ('sm: audio ' + this.sID + ' ended');
      if (this.sID == 'slot-' + audio_player)
        audio_ended();
      },
    volume: 50
    });

  sm_start_timex [slot] = setTimeout ('sm_start_timeout("' + slot + '")', 1250);
  }

function sm_start_timeout (slot)
  {
  // if (current_tube == 'au' && audio_player == slot)
  if (current_tube == 'au')
    {
    if (audio_ready [slot] == false)
      {
      log ('slot ' + slot + ' :: playState: ' + audio_stream [slot].playState + ', readyState: ' + audio_stream [slot].readyState + ', position: ' + audio_stream [slot].position);
      if (!audio_stream [slot].position)
        {
        log ('have not received load event from sound manager (slot ' + slot + '), firing anyway: ' + audio_callback [slot]);
        eval (audio_callback [slot]);
        audio_callback [slot] = undefined;
        }
      }
    }
  }

function audio_stop()
  {
  if (audio_stream [audio_player])
    audio_stream [audio_player].stop();
  }

var au_position;

function play_special_audio (pid)
  {
  log ('play special audio');

  $(slide_container).html ('');
  $(slide_container).hide();

  var program = programgrid [pid];

  var html = '';
  html += '<div id="slide-container"></div>';

  $(slide_container).html (html);
  $(slide_container).show();

  var img = new Image();
  img.onload = function()
    {
    var aspect = $(window).width() / $(window).height();
    var slide_aspect = this.width / this.height;
  
    var rsz;
    if (slide_aspect > aspect)
      rsz = ' style="display: block; width: 100%; height: auto; margin-left: auto; margin-right: auto;" ';
    else
      rsz = ' style="display: block; width: auto; height: 100%; margin-left: auto; margin-right: auto;" ';

    var html = '';
    html += '<div id="slide" style="display: none; position: absolute; top: 0; left: 0; width: 100%; height: 100%">';
    html += '<img src="' + program ['snapshot'] + '" ' + rsz + '></div>';

    $("#slide-container").html (html);
    $("#slide").fadeIn (300);
    };

  /* trigger the above code to fixup after dimensions are known */
  img.src = program ['snapshot'];

  $(slide_container).show();

  log ('audio' + audio_player + ' play');
  try { audio_stream [audio_player].play(); } catch (error) {};

  /* set the pause/play button */
  audio_onresume_inner();

  adjust_audio_mute();

  show_osd(); /* 10ft only */
  }

var au_duration;
var audio_tick_timex;

function audio_onstart()
  {
  // au_duration = parseInt (this.getClip().fullDuration, 10) * 1000;
  audio_tick_timex = setInterval ("audio_tick()", 333);
  adjust_audio_mute();
  }

function audio_tick()
  {
  update_yt_mini_progress_bar();
  /* cancel ticking if player stopped */
  // if (flowplayer ("audio" + audio_player).getState() == 1)
  //   clearInterval (audio_tick_timex);
  }

function audio_ended()
  {
  $(slide_container).html ('');
  $(slide_container).hide();
  audio_stop();
  current_tube = '';
  log ('audio ended: auto-flipping to next episode');
  flip_next_episode();
  }

function audio_pause()
  {
  log ('audio pause');
  if (audio_stream [audio_player])
    audio_stream [audio_player].pause();
  fixup_audio_pause_button();
  }

function audio_resume()
  {
  log ('audio resume');
  if (audio_stream [audio_player])
    audio_stream [audio_player].resume();
  fixup_audio_pause_button();
  }

function adjust_audio_mute()
  {
  if (current_tube == 'au')
    {
    if (player_mute)
      audio_mute();
    else
      audio_unmute();
    }
  }

function audio_mute()
  {
  log ('audio mute');
  if (audio_stream [audio_player])
    audio_stream [audio_player].mute();
  }

function audio_unmute()
  {
  log ('audio unmute');
  if (audio_stream [audio_player])
    audio_stream [audio_player].unmute();
  }

function audio_onpause()
  {
  var id = this.id();
  log ('audio "' + id + '" onPause');
  $("#btn-preview-play").removeClass ("on");
  $("#btn-play").hide();
  $("#btn-pause").show();
  }

function audio_onresume()
  {
  var id = this.id();
  log ('audio "' + id + '" onResume');
  audio_onresume_inner();
  }

function audio_onresume_inner()
  {
  $("#btn-preview-play").addClass ("on");
  $("#btn-pause").hide();
  $("#btn-play").show();
  }

function stop_special_audio()
  {
  $(slide_container).html ('');
  $(slide_container).hide();
  audio_stop();
  current_tube = '';
  }

/* 1ft only */
function fixup_audio_pause_button()
  {
  }

/* ------------------------------------------------------ ^^ AUDIO ^^ ------------------------------------------------------ */

var au_duration;
var audio_tick_timex;

function audio_onstart()
  {
  var id = this.id();
  log ('audio onstart: ' + id);
  log ('audio fulldur: ' + this.getClip().fullDuration);
  au_duration = parseInt (this.getClip().fullDuration, 10) * 1000;
  audio_tick_timex = setInterval ("audio_tick()", 333);
  if (current_tube == 'au')
    adjust_audio_mute();
  else
    try { flowplayer (id).mute(); } catch (error) {};
  }

function audio_tick()
  {
  /* update_yt_mini_progress_bar(); */
  /* cancel ticking if player stopped */
  if (flowplayer ("audio" + audio_player).getState() == 1)
    clearInterval (audio_tick_timex);
  }

function audio_onpause()
  {
  log ('audio onPause');
  $("#btn-preview-play").removeClass ("on");
  $("#btn-play").hide();
  $("#btn-pause").show();
  }

function audio_onresume()
  {
  log ('audio onResume');
  $("#btn-preview-play").addClass ("on");
  $("#btn-pause").hide();
  $("#btn-play").show();
  }

function redraw_slideshow_html()
  { 
  var n = 0;
  var html = '';

  html += '<div id="audio" style="display:block;width:750px;height:30px;visibility:hidden" href="http://9x9ui.s3.amazonaws.com/chillout-long.mp3"></div>';

  var aspect = $(window).width() / $(window).height();

  for (var i = 0; i < ssdata['slides'].length; i++)
    {
    n++;
    var slide_aspect = ssdata['slides'][i]['x'] / ssdata['slides'][i]['y'];

    var rsz;
    if (slide_aspect > aspect)
      rsz = ' style="display: block; width: 100%; height: auto; margin-left: auto; margin-right: auto;" ';
    else
      rsz = ' style="display: block; width: auto; height: 100%; margin-left: auto; margin-right: auto;" ';

    var display = (n == current_slide) ? "block" : "none";

    html += '<div id="slide-' + n + '" class="slide" style="display: ' + display + '; position: absolute; top: 0; left: 0; width: 100%; height: 100%"><img src="' + ssdata['slides'][i]['url'] + '" ' + rsz + '></div>';
    }

  $("#ym5").html (html);
  }

var saved_thumbing;
function standard_channel (id, callback)
  {
  saved_thumbing = thumbing;
  // thumbing = 'wait';
  var query = "/playerAPI/programInfo?channel=" + id + unique();

  log ('fetch programInfo: channel=' + id);
  var d = $.get (query, function (data)
    {
    parse_program_data (data);
    // thumbing = saved_thumbing;
    if (callback) eval (callback);
    });
  }

function parse_program_data (data)
  {
  var lines = data.split ('\n');

  log ('number of programs obtained: ' + (lines.length - 3));

  if (lines.length > 0)
    {
    var fields = lines[0].split ('\t');
    if (fields [0] == '701')
      {
      if (debug_mode)
        log_and_alert ('server error: ' + lines [0]);
      return;
      }
    else if (fields [0] != '0')
      {
      log_and_alert ('server error: ' + lines [0]);
      return;
      }

    var count = 0;
    for (var i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');
        var channel_id = fields [0];

        if (channel_id in pool)
          {
          var nature = pool [channel_id]['nature'];
          if (nature == '3' || nature == '4' || nature == '5')
            continue;
          }

        programgrid [fields [1]] = { 'channel': channel_id, 'type': fields[3], 'url1': 'fp:' + fields[8], 
                     'url2': 'fp:' + fields[9], 'url3': 'fp:' + fields[10], 'url4': 'fp:' + fields[11], 
                     'name': fields[2], 'desc': fields [3], 'type': fields[4], 'thumb': fields[6], 
                     'snapshot': fields[7], 'timestamp': fields[12], 'duration': fields[5], 'sort': ++count };
        }
      }

    log ('finished parsing program data');
    }
  else
    log_and_alert ('server returned nothing');
  }

function getcookie (id)
  {
  /* log ('getcookie: ' + document.cookie); */

  var fields = document.cookie.split (/; */);

  for (var i in fields)
    {
    try
      {
      var kv = fields[i].split ('=');
      if (kv [0] == id)
        return kv [1];
      }
    catch (err)
      {
      // this catch is necessary because of a bug in Google TV
      log ('some error occurred: ' + err.description);
      }
    }

  return undefined;
  }

function deletecookie (id)
  {
  var expired = new Date (new Date().getTime() - 3600);
  document.cookie = id + "=; expires=" + expired.toGMTString(); 
  }

function setcookie (id, value)
  {
  var expires = new Date (new Date().getTime() + (3600 * 24 * 365));
  document.cookie = id + "=" + value + '; expires=' + expires.toGMTString();
  }

function delete_piwik_cookies()
  {
  if (document.cookie.length > 28000)
    {
    log ('delete_piwik_cookies: ' + document.cookie);
    var fields = document.cookie.split (/; */);
    for (var i in fields)
      {
      var kv = fields[i].split ('=');
      if (kv[0].match (/_pk_/))
        try
          {
          log ('want to delete cookie: ' + kv[0]);
          deletecookie (kv[0]);
          }
        catch (error)
          {
          };
      }
    }
  }

function switch_to_ipg()
  {
  switch_to_ipg_inner();
  relay_post ("REPORT IPG");
  relay_post_position();
  }

function switch_to_ipg_inner()
  {
  log ('ipg');
  $("body").css ("cursor", "auto");
  thumbing = 'ipg';
  redraw_ipg();
  $("#sg-layer, #bg-layer").show();
  $("#sync-layer, #player-layer, #store-layer, #help-layer, #tutorial-layer, #yesno-layer, #settings-layer").hide();
  /* above used to turn off msg-layer, but there is a requirement that after signing, it stays on for a bit */
  $("#sg-grid li").removeClass ("on");
  ipg_buttons();
  if (parseInt (ipg_cursor) < 11 || parseInt (ipg_cursor) > 99)
    ipg_cursor = first_channel();
  $("#btn-store, #btn-help, #btn-pc, #btn-pref").removeClass ("on");
  cursor_on (ipg_cursor);
  update_metadata();
  ipg_userbox();
  stop_video();
  stop_all_audio_tracks();
  for (var i = 1; i <= 10; i++)
    setTimeout ("delayed_stop_video()", i * 500);
  }

function ipg_buttons()
  {
  $("#btn-store").unbind();
  $("#btn-store").click (store);
  $("#btn-help").unbind();
  $("#btn-help").click (tutorial);
  $("#btn-pc").unbind();
  $("#btn-pc").click (ask_permission_to_run_one_foot);
  $("#btn-pref").unbind();
  $("#btn-pref").click (settings);
  $("#user").click (ipg_user_click);
  }

function delayed_stop_video()
  {
  if (thumbing == 'ipg')
    {
    stop_video();
    stop_all_audio_tracks();
    }
  }

function ipg_user_click()
  {
  if ($("#user-list").css ("display") == "none")
    ipg_user_click_inner();
  }

function ipg_user_click_inner()
  {
  cursor_off (ipg_cursor);
  ipg_cursor = -1;
  $("#user").removeClass ("edit");
  ipg_userbox();
  cursor_on (ipg_cursor);
  enter_channel();
  update_metadata();
  }

function grid_location (n)
  {
  var cluster = which_cluster (n);
  var corner = top_lefts [cluster-1];
  var pos;

  if (n >= corner && n <= corner + 2)
    pos = n - corner + 1;
  else if (n >= corner + 10 && n <= corner + 12)
    pos = n - corner - 6;
  else if (n >= corner + 20 && n <= corner + 22)
    pos = n - corner - 13;

  // sensible way:
  // return Math.floor (parseInt (n) / 10) + '-' + (parseInt (n) % 10);

  // silly way:
  return cluster + '-' + pos;
  }

function update_metadata()
  {
  if (parseInt (ipg_cursor) >= 11 & parseInt (ipg_cursor) <= 99)
    {
    var grid = grid_location (ipg_cursor);
    $("#channel-info .section-title").html ('<span>' + translations ['channel_word'] + '</span><span id="ch-index">' + grid + '</span>');
    }
  else
    $("#channel-info .section-title").html ('');

  if (ipg_cursor in channelgrid)
    {
    var channel = channelgrid [ipg_cursor];
    $("#channel-title span").html (channel ['name']);
    $("#channel-description span").html (channel ['desc']);
    $("#eps-number").show();
    var n_programs = programs_in_real_channel (channel ['id']);
    if (n_programs > 0)
      $("#eps-number").html (translations ['episodes'] + ': ' + n_programs);
    else if ('youtube_completed' in channel)
      $("#eps-number").html ("No Episodes");
    else
      $("#eps-number").hide();

    $("#updates").html ('');
    if (1 in program_line)
      {
      var pid = dot_qualify_program_id (channel ['id'], program_line [1]);
      if (pid in programgrid)
        {
        var program = programgrid [pid];
         var dt = new Date (program ['timestamp']);
        $("#updates").html (translations ['updated'] + ': ' + ageof (dt, true));
        } 
      }
    }
  else
    {
    $("#channel-title span").html ('');
    $("#channel-description span").html ('');
    $("#eps-number").html ('');
    $("#updates").html ('');
    }

  ipg_userbox();
  }

function updated_date (channel_id)
  {
  if (1 in program_line)
    {
    var pid = dot_qualify_program_id (channel_id, program_line [1]);
    if (pid in programgrid)
      {
      var program = programgrid [pid];
      var dt = new Date (program ['timestamp']);
      return ageof (dt, true);
      } 
    }
  return '';
  }

function dot_qualify_program_id (ch, ep)
  {
  if ((ch + '.' + ep) in programgrid)
    {
    log ('program id fixed up to: ' + ch + '.' + ep);
    return ch + '.' + ep;
    }
  return ep;
  }

function enter_channel()
  {
  if (ipg_cursor in channelgrid)
    {
    var channel = channelgrid [ipg_cursor];
    program_lineup (channel ['id']);
    }
  else
    program_line = [];
  }

function ipg_up()
  {
  log ('ipg-up');
  cursor_off (ipg_cursor);
  if (ipg_cursor < 0)
    {
    if (ipg_cursor < -1 && ipg_cursor > -900)
      ipg_cursor++;
    }
  else if (parseInt (ipg_cursor) < 20)
    ipg_cursor = -900;
  else if (parseInt (ipg_cursor) > 20)
    ipg_cursor = parseInt (ipg_cursor) - 10;
  cursor_on (ipg_cursor);
  if (ipg_cursor > 0)
    enter_channel();
  update_metadata();
  if (ipg_cursor > 0)
    relay_post_position();
  }

function ipg_down()
  {
  log ('ipg-down');
  cursor_off (ipg_cursor);
  if (ipg_cursor < 0)
    {
    if (ipg_cursor <= -900)
      ipg_cursor = 19;
    else
      {
      if (Math.abs (ipg_cursor) <= n_users)
        {
        ipg_cursor--;
        cursor_on (ipg_cursor);
        }
      return;
      }
    }
  else if (parseInt (ipg_cursor) >= 11 && parseInt (ipg_cursor) < 90)
    ipg_cursor = parseInt (ipg_cursor) + 10;
  cursor_on (ipg_cursor);
  enter_channel();
  update_metadata();
  relay_post_position();
  }

function ipg_left()
  {
  log ('ipg-left');
  cursor_off (ipg_cursor);
  // if (parseInt (ipg_cursor) == 11)
    // {
    // old logic// ipg_cursor = 99;
    // }
  if (ipg_cursor < 0)
    {
    if (ipg_cursor <= -900)
      {
      if (ipg_cursor != -900)
        ipg_cursor++;
      }
    }
  else if (parseInt (ipg_cursor) % 10 == 1)
    {
    //old logic// ipg_cursor = parseInt (ipg_cursor) - 2; /* 41 -> 39 */
    ipg_user_click_inner();
    return;
    }
  else
    ipg_cursor = parseInt (ipg_cursor) - 1;
  cursor_on (ipg_cursor);
  if (ipg_cursor > 0)
    enter_channel();
  update_metadata();
  if (ipg_cursor > 0)
    relay_post_position();
  }

function ipg_right()
  {
  log ('ipg-right');
  cursor_off (ipg_cursor);
  if (ipg_cursor < 0)
    {
    if (ipg_cursor <= -900)
      {
      if (ipg_cursor != -903)
        ipg_cursor--;
      }
    else
      ipg_cursor = first_channel();
    }
  else if (parseInt (ipg_cursor) == 99)
    ipg_cursor = 11;
  else if (parseInt (ipg_cursor) % 10 == 9)
    ipg_cursor = parseInt (ipg_cursor) + 2; /* 39 -> 41 */
  else
    ipg_cursor = parseInt (ipg_cursor) + 1;
  cursor_on (ipg_cursor);
  if (ipg_cursor > 0)
    enter_channel();
  update_metadata();
  if (ipg_cursor > 0)
    relay_post_position();
  }

function ask_to_remove (remov_user, remov_username)
  {
  var q = '<span>' + translations ['remuser'] + '</span>';
  q = q.replace ('%1', '</span><span id="removee">' + remov_username + '</span><span>');
  ask (q, translations ['qyes'], translations ['qno'], 'device_remove_user("' + remov_user + '")', "", "", 2);
  }

function ipg_play()
  {
  log ('ipg-play: ' + ipg_cursor);
  if (ipg_cursor < 0)
    { 
    if (ipg_cursor == -903)
      ask_permission_to_run_one_foot();
    else if (ipg_cursor == -902)
      tutorial();
    else if (ipg_cursor == -901)
      settings();
    else if (ipg_cursor == -900)
      store();
    else if (Math.abs (ipg_cursor) <= n_users)
      {
      var id = $("#user-list li:nth-child(" + Math.abs (ipg_cursor) + ")").attr ("id");
      ipg_individual_user_click (id);
      }
    else
      edit_userlist();
    return;
    }
  else if (ipg_cursor in channelgrid)
    {
    stop_video();
    var channel = channelgrid [ipg_cursor];
    program_lineup (channel ['id']);
    if (channel ['youtubed'] && n_program_line < 1)
      {
      log ('ipg_play: no episodes!');
      message (translations ['noepchan'], 1250);
      return;
      }
    else
      ipg_play_inner (undefined);
    }
  else
    {
    if (username == 'Guest')
      message ('Please visit <span style="color: orange">www.9x9.tv</span> to configure!', 5000);
    else
      store();
    }
  }

function ipg_play_inner (episode_id)
  {
  $("#sg-layer, #bg-layer, #store-layer").hide();
  thumbing = 'fullscreen';
  low_video();
  $("body").css ("cursor", "none");
  tvpreview_set_channel (ipg_cursor, episode_id, true);
  }

function ipg_individual_user_click (id)
  {
  log ('ipg individual user click: ' + id);
  // var future_username = $(id + " span").html();
  var future_username = $("#" + id + " span:nth-child(1)").html();
  if ($("#user").hasClass ("edit"))
    {
    ask_to_remove (id, future_username);
    }
  else
    {
    var future_user = id.replace (/^user-/, '');
    $("#user-list").hide();
    update_metadata();
    become (future_user, future_username, true);
    }
  }

function ask_permission_to_run_one_foot()
  {
  ask (translations ['run1ft'], translations ['go'], translations ['cancel'], "run_one_foot()", "", "", 2);
  }

var yesno_cursor;
var on_yesno_yes;
var on_yesno_no;
var on_yesno_cancel;
var yesno_save_thumbing;

function ask (question, yes, no, ifyes, ifno, ifcancel, defbutton)
  {
  yesno_save_thumbing = thumbing;
  thumbing = 'ask';

  if (yesno_save_thumbing == 'store' || yesno_save_thumbing == 'store-wait')
    hide_video_for_dialogue();

  $("#question").html (question);

  $("#btn-yesno-yes span").html (yes);
  $("#btn-yesno-no span").html (no);

  on_yesno_yes = ifyes;
  on_yesno_no = ifno;
  on_yesno_cancel = ifcancel;

  $("#yesno-layer").show();

  $("#btn-yesno-yes").unbind();
  $("#btn-yesno-yes").click (function() { ask_enter (1); });

  $("#btn-yesno-no").unbind();
  $("#btn-yesno-no").click (function() { ask_enter (2); });

  yesno_cursor = defbutton;
  $("#yesno-holder .btn").removeClass ("on");

  if (yesno_cursor == 1)
    $("#btn-yesno-yes").addClass ("on");
  else if (yesno_cursor == 2)
    $("#btn-yesno-no").addClass ("on");
  }

function hide_video_for_dialogue()
  {
  hide_all_video();
  pause_and_mute_everything();
  }

function reset_video_after_dialogue()
  {
  if (yesno_save_thumbing == 'store' || yesno_save_thumbing == 'store-wait')
    {
    match_video_to_store();
    forefront (mini_player);
    if (!player_mute)
      {
      if (current_tube == 'au')
        audio_unmute();
      else
        { try { ytmini [mini_player].unMute(); } catch (error) {}; }
      }
    if (current_tube == 'au')
      audio_resume();
    else
      { try { ytmini [mini_player].playVideo(); } catch (error) {}; }
    $("#ym4").hide();
    }
  }

function ask_enter (cursor)
  {
  reset_video_after_dialogue();
  if (cursor == 1)
    {
    $("#yesno-layer").hide();
    thumbing = yesno_save_thumbing;
    eval (on_yesno_yes);
    }
  else if (cursor == 2)
    {
    $("#yesno-layer").hide();
    thumbing = yesno_save_thumbing;
    eval (on_yesno_no);
    }
  else if (cursor == 3)
    {
    $("#yesno-layer").hide();
    thumbing = yesno_save_thumbing;
    eval (on_yesno_cancel);
    }
  }

function ask_left()
  {
  if (yesno_cursor == 2)
    {
    yesno_cursor = 1;
    $("#btn-yesno-no").removeClass ("on");
    $("#btn-yesno-yes").addClass ("on");
    }
  }

function ask_right()
  {
  if (yesno_cursor == 1)
    {
    yesno_cursor = 2;
    $("#btn-yesno-yes").removeClass ("on");
    $("#btn-yesno-no").addClass ("on");
    }
  }

function run_external_store()
  {
  window.location.href = location.protocol + '//' + location.host + '/store?position=' + ipg_cursor;
  }

function run_one_foot()
  {
  window.location.href = location.protocol + '//' + location.host;
  }

/* top left grid position of all clusters */
var top_lefts = [11, 14, 17, 41, 44, 47, 71, 74, 77];

function redraw_ipg()
  {
  var html = "";
  
  var bad_thumbnail = '<img src="' + errorthumb + '" class="thumbnail">';

  for (var i in top_lefts)
    {
    if (!top_lefts.hasOwnProperty (i))
      continue;

    var ty = Math.floor (top_lefts[i] / 10);
    var tx = top_lefts[i] % 10;

    //var title = "Set #%d";
    //title = title.replace ('%d', parseInt (i) + 1);
    var title = "Untitled";
    if (top_lefts [i] in set_titles)
      title = set_titles [top_lefts [i]];

    html += '<div id="box-' + top_lefts[i] + '" class="on">';
    html += '<img src="' + root + 'bg_set.png" class="bg-set">';
    html += '<p class="set-title" id="title-' + top_lefts[i] + '"><span>' + title + '</span></p>';
    html += '<ul>';

    var play = '<img src="' + root + 'icon_play.png" class="btn-play">';

    var stuff = play;

    for (y = ty; y < ty + 3; y++)
      for (x = tx; x < tx + 3; x++)
         {
         var yx = y * 10 + x;
         if (yx in channelgrid)
           {
           var channel = channelgrid [yx];
           var thumb = channel ['thumb'];
           if (thumb == '' || thumb == 'null' || thumb == 'false')
             thumb = errorthumb;
           html += '<li class="clickable" id="ipg-' + yx + '"><img src="' + thumb + '" class="thumbnail">' + stuff + '</li>';
           }
         else
           html += '<li class="clickable" id="ipg-' + yx + '"><img src="' + root + 'add_channel.png" class="add-ch"></li>';
         }

    html += '</ul>';
    html += '</div>';
    }

  $("#slider").html (html);

  $("#sg-grid .clickable").unbind();
  $("#sg-grid .clickable").click (function() { ipg_click ($(this).attr("id")); });
  }

function ipg_click (cursor)
  {
  cursor = cursor.replace (/^ipg-/, '');
  log ('ipg click: ' + cursor);
  if (cursor != ipg_cursor)
    {
    cursor_off (ipg_cursor);
    ipg_cursor = cursor;
    cursor_on (ipg_cursor);
    enter_channel();
    update_metadata();
    relay_post_position();
    }
  else
    ipg_play();
  }

function cursor_on (cursor)
  {
  if (ipg_cursor < 0 && ipg_cursor > -900)
    {
    $("#user-list li, #btn-edit").removeClass ("on");
    if (Math.abs (ipg_cursor) > n_users)
      $("#btn-edit").addClass ("on");
    else
      $("#user-list li:nth-child(" + Math.abs (ipg_cursor) + ")").addClass ("on");
    }
  else if (ipg_cursor <= -900)
    {
    $("#btn-store, #btn-help, #btn-pc").removeClass ("on");
    if (ipg_cursor == -900)
      $("#btn-store").addClass ("on");
    else if (ipg_cursor == -901)
      $("#btn-pref").addClass ("on");
    else if (ipg_cursor == -902)
      $("#btn-help").addClass ("on");
    else if (ipg_cursor == -903)
      $("#btn-pc").addClass ("on");
    }
  else
    $("#ipg-" + cursor).addClass ("on");
  }

function cursor_off (cursor)
  {
  if (ipg_cursor <= -900)
    {
    if (ipg_cursor == -900)
      $("#btn-store").removeClass ("on");
    else if (ipg_cursor == -901)
      $("#btn-pref").removeClass ("on");
    else if (ipg_cursor == -902)
      $("#btn-help").removeClass ("on");
    else if (ipg_cursor == -903)
      $("#btn-pc").removeClass ("on");
    }
  else
    $("#ipg-" + cursor).removeClass ("on");
  }

function mini_pause()
  {
  var program = programgrid [program_line [program_cursor]];
  if (program ['type'] == '3')
    audio_pause();
  else
    try { ytmini [mini_player].pauseVideo(); } catch (error) {};
  }

function mini_resume()
  {
  var program = programgrid [program_line [program_cursor]];
  if (program ['type'] == '3')
    audio_resume();
  else
    try { ytmini [mini_player].playVideo(); } catch (error) {};
  }

function seek (offset)
  {
  try { ytmini [mini_player].seekTo (offset); } catch (error) {};
  }

function yt_mute_toggle()
  {
  if (player_mute)
    {
    player_mute = false;
    try { ytmini [mini_player].unMute(); } catch (error) {};
    }
  else
    {
    player_mute = true;
    try { ytmini [mini_player].mute(); } catch (error) {};
    }
  }

function yt_volume()
  {
  var volume = 1;
  if (ytmini [mini_player])
    try { volume = ytmini [mini_player].getVolume() / 100; } catch (error) {};
  return volume;
  }

function yt_set_volume (volume)
  {
  if (thumbing == 'fullscreen' || thumbing == 'store' || thumbing == 'store-wait')
    {
    log ('set volume: ' + volume);
    if (volume > 1)
      volume = 1;
    else if (volume < 0)
      volume = 0;
    volume_setting = volume;
    if (ytmini [mini_player])
      try { ytmini [mini_player].setVolume (100 * volume); } catch (error) {};
    }
  }

var volume_bars = 10;

function yt_volume_up()
  {
  if (thumbing == 'fullscreen' || thumbing == 'store' || thumbing == 'store-wait')
    {
    var volume = yt_volume();
    volume += 1/volume_bars;
    if (volume > 1.0) volume = 1.0;
    yt_set_volume (volume);
    yt_render_volume();
    }
  }

function yt_volume_down()
  {
  if (thumbing == 'fullscreen' || thumbing == 'store' || thumbing == 'store-wait')
    {
    var volume = yt_volume();
    volume -= 1/volume_bars;
    if (volume < 0) volume = 0;
    yt_set_volume (volume);
    yt_render_volume();
    }
  }

var yt_volume_timex;

function yt_render_volume()
  {
  clearTimeout (yt_volume_timex);

  var bars = Math.round (yt_volume() * volume_bars);

  var html = '';
  // for (var i = volume_bars; i >= 1; i--)
  for (var i = 1; i <= volume_bars; i++)
    {
    if (i > bars)
      html += '<li class="volbar" id="vol-' + i + '"></li>'
    else
      html += '<li class="volbar on" id="vol-' + i + '"></li>';
    }
  $("#volume-bars").html (html);

  if (thumbing == 'fullscreen')
    {
    $("#volume-layer").show();

    $("#volume-layer").animate ({ bottom: "0" }, 500);
    $("#player-layer").animate ({ top: "-2.5em" }, 500);

    $("#volume-bars .volbar").unbind();
    $("#volume-bars .volbar").click (yt_volume_click);

    yt_volume_timex = setTimeout ("remove_volume_layer()", 1200);
    }
  else if (thumbing == 'store' || thumbing == 'store-wait')
    {
    $("#volume-layer").show();
    yt_volume_timex = setTimeout ("remove_volume_layer()", 1200);
    }
  else
    $("#volume-layer").hide();
  }

function yt_volume_click()
  {
  log ('yt volume click');
  }

function remove_volume_layer()
  {
  if (thumbing == 'fullscreen')
    {
    $("#volume-layer").animate ({ bottom: "-2.5em" }, 500, function() { $("#volume-layer").hide(); });
    $("#player-layer").animate ({ top: "0" }, 500);
    }
  else
    $("#volume-layer").hide();
  }

var relay_started = false;

function which_relay()
  {
  var which;

  if (location.host == 'www.9x9.tv' || location.host == '9x9.tv')
    which = "relay-prod.9x9.tv";
  else if (location.host == 'dev.9x9.tv')
    which = "relay-dev.9x9.tv";
  else if (location.host == 'demo.9x9.tv')
    which = "relay-demo.9x9.tv";
  else if (location.host == 'alpha.9x9.tv')
    which = "relay-alpha.9x9.tv";
  else if (location.host == 'puppy.9x9.tv')
    which = "relay-puppy.9x9.tv";
  else if (location.host == 'moveout.9x9.tv')
    which = "relay-moveout.9x9.tv";
  else if (location.host.match (/jetty\d+.9x9.tv/))
    which = "relay-prod.9x9.tv";

  return which;
  }

function which_relay_port()
  {
  if (location.host == 'moveout.9x9.tv')
    return "910";
  else
    return "909";
  }

var relay_load_timex;

function relay_using_swfobject()
  {
  $("#relaydiv").html ('<div id="relayinside"></div>');
  $("#relaydiv").show();

  // $("#msg-layer span").html ('One Moment...');
  // $("#msg-layer").show();

  var params = { allowScriptAccess: "always", wmode: "transparent" };
  var atts = { id: "relayy" };
  var url = "http://" + which_relay() + "/relay4.swf";

  //swfobject.embedSWF (url, "relayinside", "100%", "100%", "8", null, null, params, atts);
  swfobject.embedSWF (url, "relayinside", "500px", "500px", "8", null, null, params, atts);

  log ('embedded');

  /* hack */
  /* nopreload = true; */

  relay_load_timex = setTimeout ("relay_loaded_timeout()", 15000);
  }

function relay_loaded_timeout()
  {
  if (!relay_started)
    {
    log ('retrying relay');
    relay_using_swfobject();
    }
  }

function getflash (name)
  {
  try { if (relayy) return relayy; } catch (error) {};
  if (document.getElementById ("relayy"))
    return document.getElementById ("relayy");
  }

var last_pong;
var relay_cx_timex;

function relay_loaded()
  {
  clearTimeout (relay_cx_timex);
  log ('relay loaded, requesting connect');
  var f = getflash ("relay");
  if (f && f.start)
    f.start (which_relay(), which_relay_port());
  else
    log ('unable to get a handle to relay flash application');
  relay_cx_timex = setTimeout ('log ("relay connection failed!"); relay_loaded()', 15000);
  $("#relaydiv").css ("visibility", "hidden");
  }

var ping_timex;

function relay_loaded_and_ready()
  {
  clearTimeout (relay_cx_timex);

  relay_started = true;

  if (navigator.userAgent.match (/GoogleTV/i))
    relay_post ("DEVICE Google-TV");
  else
    relay_post ("DEVICE TV");

  relay_post ("SESSION " + device_id + ' ' + (timezero / 1000));
  relay_post ("TIMESTAMP " + (new Date().getTime() / 1000));

  relay_post ("WHOAMI");

  if (user != '')
    relay_post ("RENDERER " + user + ' ' + encodeURIComponent (username));
  else
    relay_post ("RENDERER nobody");

  relay_post ("TIMESTAMP " + (new Date().getTime() / 1000));

  ping_timex = setInterval ("ping_relay()", 10000);
  }

function ping_relay()
  {
  relay_post ("PING");
  var since = new Date().getTime() - last_pong;
  if (since > 30000)
    {
    last_pong = new Date().getTime();
    log ('last pong: ' + since + ' ago');

    $("#msg-layer span").html ('Connection timed out');
    $("#msg-layer").show();

    $("#relaydiv").hide();
    setTimeout ("relay_reconnect()", 500);
    }
  else if (since > 15000)
    log ('warning: last PONG received ' + since + ' ago');
  }

function remove_connected()
  {
  if ($("#msg-layer span").html() == 'Connected')
    $("#msg-layer").hide();
  }

function relay_receive (s)
  {
  var lines = s.split ('\n');
  for (var i = 0; i < lines.length; i++)
    if (lines [i] != '')
      {
      if (lines[i].match (/^PONG/))
        last_pong = new Date().getTime();
      else
        {
        log ('[' + thumbing + '] relay received: ' + lines[i]);
        execute_relay_command (lines[i]);
        }
      }
  }

function relay_error (s)
  {
  log ("relay_error: " + s);
  }

function relay_disconnected()
  {
  log ('relay disconnected');
  relay_started = false;
  clearInterval (ping_timex);

  $("#msg-layer span").html ('Disconnected');
  $("#msg-layer").show();

  $("#relaydiv").hide();
  setTimeout ("relay_reconnect()", 500);
  }

function relay_reconnect()
  {
  log ('attempt reconnect');
  $("#msg-layer, #sync-layer").hide();
  relay_using_swfobject();
  }

function relay_error (s)
  {
  log ('relay error: ' + s);
  }

function execute_relay_command (s)
  {
  var fields = s.split (/\s+/);
  var cmd = fields [0];
  var args = fields [1];

  if (cmd == 'PONG')
    {
    /* noisy, do nothing */
    }
  else if (cmd == 'IPG')
    {
    log ("RELAY :: IPG");
    stop_video();
    switch_to_ipg_inner();
    return;
    if (thumbing != 'ipg')
      {
      // $("#sg-grid").removeClass("x3").addClass("x9");
      // $("#prev-set, #next-set").hide();
      back_to_smart_guide();
      switch_to_ipg_inner();
      }
    else
      back_to_smart_guide();
    }
  else if (cmd == 'IPG-POSITION')
    {
    log ("RELAY :: IPG-POSITION " + args);
    cursor_off (ipg_cursor);
    ipg_cursor = args;
    cursor_on (ipg_cursor);
    update_metadata();
    }
  else if (cmd == 'PLAY')
    {
    log ("RELAY :: PLAY " + fields[1] + ' ' + fields[2]);
    relay_play (fields[1], fields[2]);
    }
  else if (cmd == 'PAUSE')
    {
    mini_pause();
    }
  else if (cmd == 'RESUME')
    {
    mini_resume();
    }
  else if (cmd == 'SEEK')
    {
    seek (args);
    }
  else if (cmd == 'UPDATE')
    {
    log ("RELAY :: UPDATE");
    force_update = true;
    startup();
    }
  else if (cmd == '3x3')
    {
    log ("RELAY :: 3x3");
    stop_video();
    switch_to_ipg();
    return;
    }
  else if (cmd == 'NODE')
    {
    if (args == 'START')
      {
      nodes_incoming = [];
      }
    else if (args == 'END')
      {
      nodes = nodes_incoming;
      nodes_incoming = [];
      redraw_help_p1();
      }
    else if (args.match (/(\d+):/))
      {
      var slot = parseInt (args.match (/(\d+):/) [1]);
      nodes_incoming [slot] = { name: fields[1], type: fields[2], ip: fields[3], devtype: fields[4] };
      }
    }
  else if (cmd == 'YOUARE')
    {
    device_displayed = args;
    log ("RELAY :: YOUARE " + device_displayed);
    ipg_userbox();

    if ($("#help-layer").css ("display") == 'block')
      {
      help_p1_pings();
      }
    else if ((force_pairing && username == '') || username == 'Guest')
      {
      log ('sync: please initiate account binding');
      var html = '';
      // html += '<p><span>Please initiate account binding.<br>The name of this device is:</span></p>';
      // html += '<p><span class="device-name">' + device_displayed + '</span></p>';
      // html += '<p><span class="note">Need help? Please visit 9x9.tv or email feedback@9x9cloud.tv</span></p>';
      // $("#sync-holder").html (html);
      $("#msg-layer, #help-layer").hide();
      $("#sync-layer, #sg-layer, #bg-layer").show();
      $("#sync-holder .device-name span").html (prettify (device_displayed));
      $("#sync-holder .btn-hilite").unbind();
      $("#sync-holder .btn-hilite").click (signin_page);
      thumbing = 'sync';
      }
    }
  else if (cmd == 'REPORT')
    {
    if (fields[2] == 'ACK' && fields[1].match (/(\d+):/))
      {
      var slot = parseInt (fields[1].match (/(\d+):/) [1]);
      help_p1_ack (slot);
      }
    }
  else if (cmd == 'RELEASE')
    {
    log ("RELAY :: RELEASE");
    device_displayed = device_displayed.replace (/^\+/, '')
    // user = initial_user;
    // username = ''
    ipg_userbox();
    relay_post ("RENDERER " + user + ' ' + encodeURIComponent (username));
    message (translations ['released'], 6000);
    // var d = $.get ("/playerAPI/userTokenVerify?token=" + user, function (data)
    //  {
    //  log ('response to userTokenVerify: ' + data);
    //  startup();
    //  });
    }
  else if (cmd == 'BECOME')
    {
    log ("RELAY :: BECOME " + args);
    if (fields.length > 2)
      username = decodeURIComponent (fields [2]);
    else
      username = args;
    user = args;
    become (user, username, false);
    }
  else if (cmd == 'TELL')
    {
    relay_post (s);
    }
  else if (cmd == 'KEY')
    {
    relay_key (args);
    }

  relay_log ("received", s);
  }

function relay_key (keyname)
  {
  var keytable = 
    {
    'SPACE': 32,
    'LEFT': 37,
    'RIGHT': 39,
    'UP': 38,
    'DOWN': 40,
    'VOL+': 107,
    'VOL-': 109,
    'MUTE': 85,
    'HOME': 36,
    'BACK': 8,
    'ESC': 27,
    'DEL': 46,
    'CR': 13,
    'PLAY': 80,
    'OK': 121
    };

  if (keyname.match (/^#/))
    {
    keyname = keyname.replace (/^#/, '');
    keypress (parseInt (keyname));
    }
  else if (keyname in keytable)
    {
    keypress (keytable [keyname]);
    }
  else 
    {
    var code = keyname.charCodeAt (0);
    if (code >= 97 && code <= 122)
      {
      keypress (code - 32);
      }
    else if (code >= 48 && code <= 57)
      {
      keypress (code - 48)
      }
    }
  }

function become (incoming_user, incoming_username, force_cookie_flag)
  {
  log ('become :: user:' + incoming_user + ' username:' + username);
  user = incoming_user;
  inherit_sphere_from_cookie();
  username = incoming_username;
  ipg_userbox();
  relay_post ("RENDERER " + user + ' ' + encodeURIComponent (username));
  $("#sg-user").html (username);
  $("#sync-layer, #signin-layer").hide();
  message (translations ['welcome'].replace ('%1', username), 6000);
  became = true;
  /* do this a bit later */
  setTimeout ("add_to_known_users()", 2000);
  var d = $.get ("/playerAPI/userTokenVerify?token=" + user + unique(), function (data)
    {
    log ('response to userTokenVerify: ' + data);
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      /* always set cookie now */
      if (true || force_cookie_flag || !getcookie (cookiename))
        setcookie (cookiename, user);
      for (var i = 2; i < lines.length; i++)
        {
        var fields = lines[i].split ('\t');
        if (fields[0] == 'sphere' && !inherited_sphere)
          sphere = fields[1];
        if (fields[0] == 'name')
          username = fields[1];
        if (fields[0] == 'ui-lang')
          set_language (fields[1]);
        }
      $("#user-list").hide();
      update_metadata();
      startup();
      }
    else
      {
      $("#msg-layer span").html ('Cannot verify this user! Please try again.');
      $("#msg-layer").show();
      setTimeout ("remove_msg(); reload_page()", 6000);
      }
    });
  }

function reload_page()
  {
  log ('reload page! bye');
  window.location.href = window.location.href;
  }

function remove_sync()
  {
  $("#sync-layer").hide();
  }

function remove_msg()
  {
  $("#msg-layer").hide();
  }

function relay_post (s)
  {
  if (relay_started && s && s != '')
    {
    if (s != 'PING')
      log ('relay post: ' + s);
    var f = getflash ("relay");
    if (f && f.post)
      {
      f.post (s + '\n');
      if (s != 'PING')
        relay_log ("sent", s);
      }
    else
      relay_log ("send failed", s);
    }
  }

function relay_post_position()
  {
  relay_post ("REPORT IPG-POSITION " + ipg_cursor);
  }

function relay_play (channel, episode)
  {
  log ('relay play: ' + channel + ' ' + episode);
  $("#sg-layer, #bg-layer").hide();
  thumbing = 'fullscreen';
  low_video();
  tvpreview_set_real_channel (channel, episode, false);
  }

var piwik_installed = false;

function piwik_host()
  {
  var host = (document.location.protocol == 'https:') ? "https://" : "http://";

  if (location.host == 'puppy.9x9.tv')
    host += 'dev.piwik.9x9.tv';
  else if (location.host == 'alpha.9x9.tv')
    host += 'alpha.piwik.9x9.tv';
  else if (location.host == 'qa.9x9.tv')
    host += 'qa.piwik.9x9.tv';
  else if (location.host == 'demo.9x9.tv')
    host += 'demo.piwik.9x9.tv';
  else if (location.host == 'www.9x9.tv' || location.host == '9x9.tv')
    host += 'piwik.9x9.tv';

  host += '/';
  return host;
  }

function setup_piwik()
  {
  if (!piwik_installed)
    {
    var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
    y.src = piwik_host() + "piwik.js";
    var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
    piwik_installed = true;
    }
  }

function reverse_engineer_program_id (episode_id)
  {
  for (var i = 1; i <= n_program_line; i++)
    {
    if (episode_id == youtube_of (program_line [i]))
      return program_line [i];
    }
  }

function track_episode (channel_id, episode_id)
  {
  var channel = pool [channel_id];
  var has_piwik = false;
  try { if (Piwik) has_piwik = true; } catch (error) {};
  if (has_piwik && 'piwik' in channel)
    {
    var full_episode_id = episode_id;
    if ((channel_id + '.' + episode_id) in programgrid)
      full_episode_id = channel_id + '.' + episode_id;
    var programinfo = programgrid [full_episode_id];
    if (!programinfo)
      {
      var pid = reverse_engineer_program_id (episode_id);
      if (pid)
        programinfo = programgrid [pid];
      }
    if (!programinfo)
      {
      log ("track_episode: can't locate program: " + full_episode_id);
      return;
      }
    var idSite = channel ['piwik'];
    var pageTitle = programinfo ['name'] + ' (' + episode_id + ')';
    var pageUrl = pageTitle;
    log ('track :: channel=' + channel_id + ' episode=' + episode_id + ' piwik=' + idSite);
    var tracker = Piwik.getTracker();
    if (tracker)
      {
      tracker.setTrackerUrl (piwik_host() + 'piwik.php');
      tracker.setSiteId (idSite);
      tracker.setDocumentTitle (pageTitle);
      tracker.setCustomUrl (pageUrl);
      tracker.trackPageView();
      }
    else
      log ("can't get Piwik tracker");
    }
  else if (!has_piwik)
    log ('not tracking (no Piwik seems available)');
  else if (! ('piwik' in channel))
    log ('not tracking (no Piwik ID for this channel)');
  }

var nodes = [];
var nodes_incoming = [];
var diagnostics_saved_thumbing;

function diagnostics()
  {
  try { state(); } catch (error) {}; /* record this first */
  diagnostics_saved_thumbing = thumbing;
  thumbing = 'diagnostics';
  try { ytmini [mini_player].pauseVideo(); } catch (error) {};
  $("#p1 span").html ('Sync');
  $("#p2 span").html ('Relay Commands');
  $("#p3 span").html ('Videos');
  $("#p4 span").html ('Report a Bug');
  $("#help-layer").show();
  $("#btn-help-close").unbind();
  $("#btn-help-close").click (diagnostics_close);
  $("#help-tabs li").unbind();
  $("#help-tabs li").click (function() { help_tab ($(this).attr ("id")); });
  help_tab ("p1");
  try { ytmini [mini_player].pauseVideo(); } catch (error) {};
  }

function diagnostics_close()
  {
  $("#help-layer, #msg-layer").hide();
  thumbing = diagnostics_saved_thumbing;
  }

function help_tab (tab)
  {
  $("#help-tabs li").removeClass ("on");
  $("#" + tab).addClass ("on");
  $("#help-holder .input-panel").hide();
  $("#" + tab + "-panel").show();
  eval ("help_" + tab + "()");
  }

function help_p1()
  {
  relay_post ('NODES');
  }

function redraw_help_p1()
  {
  log ('redraw help p1');
  var html = '<span style="color: white">This device is: <span id="thisdevice" style="color: orange; font-size: 1em">[Wait...]</span></span><br>\n';
  html += '<span style="color: white">User token: <span style="color: orange; font-size: 1em">' + user + '</span></span><br>';
  html += '<span style="color: white">Device token: <span style="color: orange; font-size: 1em">' + device_id + '</span></span><br>';

  html += '<table border="1" columns="6">';
  for (var n in nodes)
    {
    if (nodes.hasOwnProperty (n) && 'name' in nodes [n])
      {
      var node = nodes [n];
      html += '<tr id="node-' + n + '">';
      html += '<td><span>' + node ['name']    + '</span></td>';
      html += '<td><span>' + node ['type']    + '</span></td>';
      html += '<td><span>' + node ['ip']      + '</span></td>';
      html += '<td><span>' + node ['devtype'] + '</span></td>';
      html += '<td><span>' + 'Unverified'     + '</span></td>';
      html += '</tr>';
      }
    }
  html += '</table>';

  html += '<br>';

  $("#p1-panel .list").html (html);
  scrollbar ("#p1-content", "#p1-list", "#p1-slider");

  relay_post ("WHOAMI");
  }

function help_p1_pings()
  {
  log ('help p1 pings');
  $("#thisdevice").html (device_displayed);
  for (var n in nodes)
    {
    if (nodes.hasOwnProperty (n) && 'name' in nodes [n])
      {
      var node = nodes [n];
      relay_post ('TELL ' + node ['name'] + ' TELL ' + device_displayed + ' REPORT ' + node ['name'] + ' ACK');
      $("#node-" + n + " td:nth-child(5) span").html ("Contacting...");
      if (node ['name'] == device_displayed)
        { $("#node-" + n + " td span").css ({ "font-weight": "bold", "color": "orange" }); }
      }
    }
  }

function help_p1_ack (node)
  {
  var node_data = nodes [node];
  $("#node-" + node + " td:nth-child(5) span").html ("Live!");
  if (node_data ['name'] == device_displayed)
    { $("#node-" + node + " td span").css ({ "font-weight": "bold", "color": "orange" }); }
  }

function help_p2()
  {
  var html = '<div class="block"><p>';
  for (var i in relay_log_data)
    {
    if (relay_log_data.hasOwnProperty (i))
      {
      var when = new Date (relay_log_data [i]['timestamp']);
      var hh = ("00" + "" + when.getHours()).slice (-2);
      var mm = ("00" + "" + when.getMinutes()).slice (-2);
      var ss = ("00" + "" + when.getSeconds()).slice (-2);
      var fulltime = hh + ':' + mm + ':' + ss;
      html += '<span>';
      html += fulltime + ' ' + relay_log_data [i]['direction'] + ' ' + relay_log_data [i]['command'];
      html += '</span><br>';
      }
    }
  html += '</p></div>';
  $("#p2-panel .list").html (html);
  scrollbar ("#p2-content", "#p2-list", "#p2-slider");
  }

function help_p3()
  {
  var html = '<div class="block"><p>';
  for (var i in video_log_data)
    {
    if (video_log_data.hasOwnProperty (i))
      {
      var when = new Date (relay_log_data [i]['timestamp']);
      var hh = ("00" + "" + when.getHours()).slice (-2);
      var mm = ("00" + "" + when.getMinutes()).slice (-2);
      var ss = ("00" + "" + when.getSeconds()).slice (-2);
      var fulltime = hh + ':' + mm + ':' + ss;
      html += '<span>' + fulltime + ' ' + video_log_data [i]['forum'] + ' ';
      html += video_log_data [i]['id'] + ' ' + video_log_data [i]['video'];
      html += ' <a href="http://www.youtube.com/watch?v=' + video_log_data [i]['video'] + '" target="_blank">[youtube]</a>';
      html += '</span><br>';
      }
    }
  html += '</p></div>';
  $("#p3-panel .list").html (html);
  scrollbar ("#p3-content", "#p3-list", "#p3-slider");
  }

function help_p4()
  {
  $("#btn-report").unbind();
  $("#btn-report").click (function()
    {
    submit_user_report ($("#problem-input .textfield").val());
    });
  }

function submit_user_report (comment)
  {
  var session = Math.floor (timezero/1000);

  var query;
  if (username == 'Guest')
    query = '/playerAPI/userReport?device=' + device_id + '&' + 'session=' + session + '&' + 'comment=' + comment;
  else
    query = '/playerAPI/userReport?device=' + device_id + '&' + 'user=' + user + '&' + 'session=' + session + '&' + 'comment=' + comment;

  log ('submitting user bug report');

  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields[0] == '0')
      {
      var response = 'Thank you for your report.';
      for (var i = 2; i < lines.length; i++)
        {
        var fields = lines[i].split ('\t');
        if (fields[0] == 'id')
          response = 'Thank you. Your ticket number is: <span style="color: orange">' + fields[1] + '</span>';
        }
      $("#msg-layer span").html (response);
      $("#msg-layer").show();
      $("#help-layer").hide();
      /* flush this just in case */
      report_submit_();
      }
    else
      {
      $("#msg-layer span").html ('Problem submitting your report: ' + fields[1]);
      $("#msg-layer").show();
      setTimeout ("remove_msg()", 3000);
      }
    });
  }

var error_noise;

function sound_effect_setup()
  {
  if (!error_noise)
    {
    error_noise = soundManager.createSound (
      {
      id: 'errorbeep',
      url: 'http://9x9ui.s3.amazonaws.com/click-high.mp3',
      autoLoad: true,
      autoPlay: false,
      stream: true,
      onload: function()
        {
        log ('sound_effect_setup: sound loaded: ' + this.sID);
        },
      volume: 50
      });
    }
  }

function play_error_noise()
  {
  if (error_noise)
    try { error_noise.play(); } catch (error) {};
  }

function store()
  {
  if (username == 'Guest' || username == '')
    return;

  clearTimeout (ch_next_timex);
  clearTimeout (ch_prev_timex);

  sound_effect_setup();

  $("#ch1").removeClass().addClass("i1");
  $("#ch2").removeClass().addClass("i2");
  $("#ch3").removeClass().addClass("i3");

  log ('store');
  proposed_sphere = sphere;
  log ('[store] proposed sphere set to: ' + proposed_sphere);
  if (recommend_set == 0)
    recommend_set = store_h_cursor = 1;
  thumbing = 'store-wait';
  store_load_recommended();
  thumbing = 'store';
  $("#sg-layer, #osd-layer, #confirm-layer").hide();
  $("#store-layer").show();
  video_z_base = 100;
  fixup_video_z_base();
  match_video_to_store();
  }

var slr_cache = {};
var recommends;

/* what language the in-memory store data is in */
var store_language;

/* what the user might change language to */
var proposed_sphere;

function store_load_recommended()
  {
  on_video_start = "move_to_forefront()";

  if (recommends && sphere == store_language)
    {
    /* been here before */
    redraw_store_recommended();
    reset_store();
    recommended (store_h_cursor);
    return;
    }

  var language_used = sphere;

  recommends = [ { name: 'language', thumb: gearthumb, count: 0 } ];

  var query = '/playerAPI/listRecommended?' + 'lang=' + language_used;

  if (query in slr_cache)
    {
    store_load_recommended_inner (slr_cache [query], language_used);
    return;
    }

  var d = $.get (query, function (data)
    {
    slr_cache [query] = data;
    store_load_recommended_inner (data, language_used);
    });
  }

function store_load_recommended_inner (data, language)
  {
  var lines = data.split ('\n');
  for (var i = 2; i < lines.length && lines[i] != '' && lines[i] != '--'; i++)
    {
    var fields = lines[i].split ('\t');
    recommends [i-1] = { id: fields[0], name: fields[1], desc: fields[2], thumb: fields[3], count: fields[4], content: [] };
    }
  redraw_store_recommended();
  reset_store();
  /* recommended (openset + 1); */
  recommended (store_h_cursor, "store_load_recommended_callback()");
  /* this step must be done after recommended */
  append_all_categories();
  store_language = language;
  }

var forefront_timex;
var previous_mini_player;

function move_to_forefront()
  {
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    if (recommend_set in recommends && 'content' in recommends [recommend_set] && recommends [recommend_set]['content'].length > 0)
      forefront (mini_player);
    previous_mini_player = mini_player;
    clearInterval (forefront_timex);
    forefront_timex = setInterval ("move_to_forefront_poll()", 250);
    }
  }

function move_to_forefront_poll()
  {
  if (thumbing != 'store' && thumbing != 'store-wait')
    {
    clearInterval (forefront_timex);
    }
  else
    {
    if (mini_player != previous_mini_player)
      {
      log ('############################## mini player change detected! ' + previous_mini_player + ' -> ' + mini_player);
      previous_mini_player = mini_player;
      // clearInterval (forefront_timex);
      }
    if (recommend_set in recommends && 'content' in recommends [recommend_set] && recommends [recommend_set]['content'].length > 0)
      {
      forefront (mini_player);
      if (thumbing != 'store-wait' && !$("#open-set").hasClass ("freeze"))
        match_video_to_store (true);
      }
    }
  }

function redraw_store_recommended (going_left_flag)
  {
  var html = '';
  for (var i = store_h_cursor - 2; i <= store_h_cursor + 3; i++)
    {
    var r;
    if (i == 0 || i == recommends.length)
      {
      /* language change */
      r = { thumb: gearthumb, name: 'Language', count: 0 };
      }
    else if (i < 0)
      r = recommends [recommends.length + i];
    else if (i >= recommends.length)
      r = recommends [i - recommends.length];
    else
      r = recommends [i];

    if (!r)
      r = { thumb: errorthumb, name: '', count: 0 };

    var plural = r['count'] + ' ' + ( (r['count'] == 1) ? translations ['channel'] : translations ['channels'] );
    if (i > store_h_cursor || (going_left_flag && i == store_h_cursor))
      plural += ' inside';
    if (language == 'zh')
      {
      /* always */
      plural = translations ['ch_inside'];
      plural = plural.replace ('%1', r['count']);
      }
    if (r['name'] == 'Language') plural = '';

    html += '<li id="sr-' + i + '">\n';
    if (r['name'] == 'Language')
      {
      html += '<img src="' + root + 'icon_setting.png" class="icon-setting">';
      html += '<p class="set-title"><span>Region</span></p>';
      }
    else
      {
      html += '<img src="' + root + 'bg_chs.png" class="bg-chs">\n';
      html += '<img src="' + r['thumb'] + '" class="thumbnail">\n';
      html += '<img src="' + root + 'set_front.png" class="set-front">\n';
      html += '<img src="' + root + 'set_back.png" class="set-back">\n';
      html += '<p class="set-title"><span>' + r['name'] + '</span></p>\n';
      html += '<p class="ch-amount"><span>' + plural + '</span></p>\n';
      }
    html += '</li>\n';
    }

  $("#set-list").html (html);
  }

var slc_cache = {};

function append_all_categories()
  {
  log ('append all categories');

  var query = '/playerAPI/category?category=0&flatten=true&lang=' + sphere;

  if (query in slc_cache)
    {
    log ('using cache for top categories');
    append_all_categories_inner (slc_cache [query]);
    }
  else
    {
    log ('category query: ' + query);
    var d = $.get (query, function (data)
      {
      slc_cache [query] = data;
log ('cache "' + query + '" to |' + data + '|');
      append_all_categories_inner (data);
      });
    }
  }

function append_all_categories_inner (data)
  {
  var block = 0;
  var lines = data.split ('\n');
  log ('append all categories inner, lines: ' + lines.length);
  var seen = {};
  for (var i = 1; i < lines.length; i++)
    {
    if (lines [i] == '--')
      block++;
    if (block >= 3)
      break;
    }
  for (var j = i; j < lines.length; j++)
    {
    if (lines[j] != '--' && lines[j] != '')
      {
      var fields = lines[j].split ('\t');
      if (! (fields[0] in seen))
        {
        recommends [recommends.length] = 
           { id: fields[0], name: fields[1], desc: '', thumb: errorthumb, count: fields[2], content: [] };
        log ("CATEGORY: " + fields[1]);
        seen [fields[0]] = true;
        }
      }
    }
  }

var recommend_set;
var recommend_cursor;

/* Store Slider */
var storeset;
var setlist;
var setNum;
var openset;
var expand_timex;

var store_h_cursor = 1; 

function reset_store()
  {
  storeset = 1;
  openset = storeset + 1;
  setlist = $("#set-list li");
  setNum = $(setlist).size();
log ('reset_store: setNum=' + setNum);
  var distance = "-" + 14.5 * (storeset) + "em"; /* was: 0 */
  $("#set-list").css("left",distance);
  $(setlist).removeClass("on").removeClass("used");
  $("#open-set").show();
  hide_all_video();
  ch_expand();
  $(setlist[openset]).addClass("on");
  set_used_set();
  set_ellipsis();
  /* recommended (openset + 1); */
  $("#ch1-video, #ch2-video, #ch3-video").html('');
  }

function soft_reset_store()
  {
  storeset = 1;
  openset = storeset + 1;
  setlist = $("#set-list li");
  setNum = $(setlist).size();
log ('soft reset_store: store_h_cursor=' + store_h_cursor);
  var distance = "-" + 14.5*(storeset) + "em"; /* was: 0 */
  $("#set-list").css("left",distance);
  $(setlist).removeClass("on").removeClass("used");
  $(setlist[openset]).addClass("on");
  set_used_set();
  set_ellipsis();
  if (store_h_cursor != 0)
    $("#ch1-video, #ch2-video, #ch3-video").html('');
  }
 
function set_ellipsis()
  {
  $(".ellipsis").ellipsis();
  }
 
function set_used_set()
  {
  for (var i = storeset; i >= 0; i--)
    $(setlist[i]).addClass("used"); 
  }
  
function store_move (storeset)
  {
  hide_all_video();
  program_line = [];
  ch_shrink();
  distance = "-" + 14.5*(storeset) + "em";
  $("#set-list").animate ({ left: distance }, 400, "easeInOutExpo");
  $(setlist).removeClass("on").removeClass("used");
  openset = storeset + 1;
  $(setlist[openset]).addClass("on");
  set_used_set();
  $("#open-set").hide();
  expand_timex = setTimeout ("store_move_inner()", 500);
  proposed_sphere = sphere;
  log ('[store move] proposed sphere set to: ' + proposed_sphere);
  recommended (store_h_cursor);
  }

function store_move_inner()
  {
  $("#open-set").show();
  ch_expand();
  redraw_store_recommended();
  soft_reset_store();
  store_metadata();
  $("#open-set").removeClass ("freeze"); 
  }

function store_metadata()
  {
  if (recommend_set == 0)
    {
    $("#set-name").html ('Settings');
    $("#ch-name").html ('Region Setting');
    $("#ch-order, #open-subscriber, #open-age").html ('');
    $("#open-meta").css ("visibility", "hidden");
    }
  else if (recommend_set in recommends)
    {
    $("#open-meta").css ("visibility", "visible");
    var rec = recommends [recommend_set];
    $("#set-name").html (rec ['name']);
    if ('content' in rec && recommend_cursor in rec ['content'])
      {
      var real_channel = rec ['content'][recommend_cursor];
      var channel = pool [real_channel];
      $("#ch-name").html (channel ['name']);
      $("#open-description span").html (channel ['desc']);
      var n_eps = programs_in_real_channel (real_channel);
      var plural = (n_eps == 1) ? translations ['episode_lc'] : translations ['episodes_lc'];
      $("#open-epi").html (n_eps + ' ' + plural);
      var chof = translations ['chof'];
      chof = chof.replace ('%1', recommend_cursor);
      chof = chof.replace ('%2', rec['content'].length-1);
      $("#ch-order").html (chof);
      $("#open-subscriber").html (channel ['subscribers']);
      $("#open-age").html (updated_date (channel ['id']));
      }
    else
      {
      $("#ch-name, #open-description span, #ch-order, #open-subscriber, #open-age").html ('');
      $("#open-epi").html (translations ['noeps']);
      }
    }
  set_ellipsis();
  fixup_store_hint();
  }

function ch_expand()
  {
  log ('ch_expand');
  $(setlist[openset]).children("p.et-title").hide();
  $(setlist[openset]).children("p.ch-amount").hide();
  $("#ch1").addClass("f1", 300, function()
    {
    $(this).removeClass("i1");
    // ch_expand_completion();
    });	
  $("#ch2").addClass("f2", 300, function()
    {
    $(".f2 .thumbnail").fadeIn(300);
    $(this).removeClass("i2");
    });
  $("#ch3").addClass("f3", 300, function()
    {
    $(".f3 .thumbnail").fadeIn(300);
    $(this).removeClass("i3");
    });
  store_metadata();
  $(".open-txt").delay(300).fadeIn(300);
  setTimeout ("ch_expand_completion()", 300);
  }

function ch_expand_completion()
  {
  log ('ch expand completion');
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    high_video();
    match_video_to_store();
    // forefront (mini_player);
    move_to_forefront();
    soft_reset_store();
    store_metadata();
    }
  }

function ch_shrink()
  {
  log ('ch_shrink');

  $(setlist[openset]).children("p.et-title").show();
  $(setlist[openset]).children("p.ch-amount").show();

  $("#ch1").removeClass().addClass("i1");
  $("#ch2").removeClass().addClass("i2");
  $("#ch3").removeClass().addClass("i3");

  $(".open-txt").hide();
  }

function ch_prev()
  {
  $(".open-txt").hide();
  // low_video_and_hide(); /* TMP */
  hide_all_video();
  $("#open-set .thumbnail").hide();
  if ($("#open-set div").hasClass("f1"))
    {
    $(".f1").removeClass().addClass("b1");
    $(".f2").removeClass().addClass("b2");
    $(".f3").removeClass().addClass("b3");
    }
  $(".b3").removeClass("b3").addClass("b0");
  $(".b0").addClass("b1",600, function()
    {
    $(this).removeClass("b0");
    });
  $(".b1").addClass("b2",600, function()
    {
    $(this).removeClass("b1");
    $(".b2 .thumbnail").fadeIn(300);
    });
  $(".b2").addClass("b3",600, function()
    {
    $(this).removeClass("b2");
    $(".b3 .thumbnail").fadeIn(300);
    });
  $("#ch4").addClass("b3").addClass("b4", 600, function()
    {
    $(this).removeClass();
    });
  fixup_store_hint();
  $(".open-txt").delay(600).fadeIn(300);
  ch_prev_preview();
  }

var ch_prev_timex;

function ch_prev_preview()
  {
  ch_prev_timex = setTimeout (function()
    {
    // ch_prev_pos();
    // $(".b2 div").append('<img src="" class="thumbnail">').fadeIn(300);
    // $(".b3 div").append('<img src="" class="thumbnail">').fadeIn(300);
    $(".open-txt").fadeIn (300);
    $("#open-set").removeClass ("freeze");
    store_down_inner();
    }, 900);
  }

function ch_next()
  {
  $(".open-txt").hide();
  // low_video_and_hide(); /* TMP */
  hide_all_video();
  pause_and_mute_all_video();
  $("#open-set .thumbnail").remove();
  if ($("#open-set div").hasClass("b1"))
    {
    $(".b1").removeClass().addClass("f1");
    $(".b2").removeClass().addClass("f2");
    $(".b3").removeClass().addClass("f3");
    }
  $(".f1").removeClass().addClass("f4");
  $(".f4").addClass("f3",600, function()
    {
    $(this).removeClass("f4");
    $(".f3 .thumbnail").fadeIn(300);
    });
  $(".f2").addClass("f1",600, function()
    {
    $(this).removeClass("f2");
    });
  $(".f3").addClass("f2",600, function()
    {
    $(this).removeClass("f3");
    $(".f2 .thumbnail").fadeIn(300);
    });
  $("#ch4").addClass("f1").addClass("f0", 600, function()
    {
    $(this).removeClass();
    });
  $(".open-txt").delay(600).fadeIn(300);
  ch_next_preview();
  }

var ch_next_timex;

function ch_next_preview()
  {
  ch_next_timex = setTimeout (function()
    {
    // ch_next_pos();
    // $(".f2 div").append('<img src="" class="thumbnail">').fadeIn(300);
    // $(".f3 div").append('<img src="" class="thumbnail">').fadeIn(300);
    $(".open-txt").fadeIn (300);
    $("#open-set").removeClass ("freeze");
    store_up_inner();
    }, 900);
  }

function store_right()
  {
  if (!$("#open-set").hasClass ("freeze"))
    {
    log ('store right');
    if (storeset < setNum - 2)
      {
      storeset++;
      store_h_cursor++;
      if (store_h_cursor >= recommends.length)
        store_h_cursor = 0;
      $("#open-set").addClass ("freeze"); 
      store_move (storeset);
      }
    }
  else
    {
    log ('store right: too fast!');
    play_error_noise();
    }
  }

function store_left()
  {
  if (!$("#open-set").hasClass ("freeze"))
    {
    log ('store left');
    if (storeset > 0)
      {
      storeset--;
      store_h_cursor--;
      if (store_h_cursor < 0)
        store_h_cursor = recommends.length - 1;
      $("#open-set").addClass ("freeze"); 
      store_move (storeset);
      }
    }
  else
    {
    log ('store left: too fast!');
    play_error_noise();
    }
  }

function store_picture (level, image)
  {
  var video = "#" + $(level).attr ("id") + "-video";
  $(video).html ('<img src="' + root + image + '" height="100%" width="100%">');
  $("#video-layer").css ("left", ( -3 * $(window).width() ) + "px");
  }

function show_language_as_is()
  {
  log ('show language as is: ' + proposed_sphere);
  if (recommend_set == 0)
    {
    store_picture (".f1, .b1, .i1", "setting_" + proposed_sphere + ".jpg");
    var other_sphere = (proposed_sphere == 'en') ? 'zh' : 'en';
    store_picture (".f2, .b2, .i2", "setting_" + other_sphere + ".jpg");
    store_picture (".f3, .b3, .i3", "setting_" + other_sphere + ".jpg");
    }
  else
    log ('show_language_as_is: no longer on set 0');
  store_metadata();
  pause_and_mute_everything();
  }

function store_down()
  {
  if (!$("#open-set").hasClass ("freeze"))
    {
    log ('store down');
    $("#open-set").addClass ("freeze"); 
    ch_prev();
    }
  else
    {
    log ('store down: too fast!');
    play_error_noise();
    }
  }

function store_down_inner()
  {
  log ('store down inner');

  if (recommend_set == 0)
    {
    recommend_cursor--;
    proposed_sphere = (proposed_sphere == 'en') ? 'zh' : 'en';
    log ('[store down inner] proposed sphere set to: ' + proposed_sphere);
    show_language_as_is();
    setTimeout ("show_language_as_is()", 500);
    return;
    }
  $("#ch1-video, #ch2-video, #ch3-video").html('');
  high_video(); /* TMP */
  match_video_to_store();
  var channels_in_set = recommends [recommend_set]['content'].length - 1;
  on_video_start = "move_to_forefront()";
  if (recommend_cursor > 1)
    recommend_channel (recommend_cursor - 1);
  else
    recommend_channel (channels_in_set);
  }

function store_up()
  {
  if (!$("#open-set").hasClass ("freeze"))
    {
    log ('store up');
    $("#open-set").addClass ("freeze"); 
    ch_next();
    }
  else
    {
    log ('store up: too fast!');
    play_error_noise();
    }
  }

function store_up_inner()
  {
  log ('store up inner');
  if (recommend_set == 0)
    {
    recommend_cursor++;
    proposed_sphere = (proposed_sphere == 'en') ? 'zh' : 'en';
    log ('[store up inner] proposed sphere set to: ' + proposed_sphere);
    show_language_as_is();
    setTimeout ("show_language_as_is()", 500);
    return;
    }
  $("#ch1-video, #ch2-video, #ch3-video").html('');
  high_video(); /* TMP */
  match_video_to_store();
  var channels_in_set = recommends [recommend_set]['content'].length - 1;
  on_video_start = "move_to_forefront()";
  if (recommend_cursor < channels_in_set)
    recommend_channel (recommend_cursor + 1);
  else
    recommend_channel (1);
  }

var rec_cache = {};

function recommended (index)
  {
  if (! (index in recommends))
    {
    log ('recommended: ' + index + ' failed (not in recommended index)');
    return;
    }

  var cat = recommends [index];
  log ('recommended: ' + index + ', name: ' + cat ['name'] + ', set: ' + cat ['id']);

  if (index == 0)
    {
    log ("INDEX ZERO");
    $("#ch1").removeClass().addClass("i1");
    $("#ch2").removeClass().addClass("i2");
    $("#ch3").removeClass().addClass("i3");
    /* language settings kept at zero */
    recommend_set = index;
    show_language_as_is();
    store_metadata();
    return;
    }

  thumbing = 'store-wait';

  var query = '/playerAPI/setInfo?set=' + cat ['id'] + '&' + 'lang=' + sphere;
  if (query in rec_cache)
    {
    log ('recommended: using cache for ' + cat ['id']);
    recommended_inner (index, rec_cache [query]);
    return;
    }

  log ('recommended: query: ' + query);
  var d = $.get (query, function (data)
    {
    rec_cache [query] = data;
    recommended_inner (index, data);
    });
  }

function recommended_inner (index, data)
  {
  thumbing = 'store';
  if (parse_set_info_data (index, data) == '0')
    {
    recommend_set = index;
    if (recommends [index]['content'].length == 0)
      no_channels_in_this_set();
    else 
      recommend_channel (1);
    }
  }

function no_channels_in_this_set()
  {
  clearInterval (forefront_timex);
  on_video_start = undefined;
  for (var i = 1; i <= 3; i++)
    background (i);
  message ('No channels in this set!', 2000);
  }

function parse_set_info_data (index, data)
  {
  var cat = recommends [index];
  var lines = data.split ('\n');
  var fields = lines[0].split ('\t');
  var result = fields[0];
  log ('parse set info (' + index + '): ' + cat ['id'] + ', result: ' + result);
  if (result == '0')
    {
    var block = 0;
    var count = 0;
    for (var i = 0; i < lines.length; i++)
      {
      if (lines[i] == '')
        continue;
      if (lines[i] == '--')
        {
        block++;
        continue;
        }
      var fields = lines[i].split ('\t');
      if (fields[0] == 'piwik')
        {
        set_pool [cat['id']] = {};
        set_pool [cat['id']]['piwik'] = fields[1];
        }

      if (block == 3)
        {
        var channel = { 'id': fields[1], 'name': fields[2], 'desc': fields[3], 'thumb': fields[4], 'count': fields[5], 'type': fields[6], 'status': fields[7], 'nature': fields[8], 'extra': fields[9], 'timestamp': fields[10], 'sortorder': fields[11], 'piwik': fields[12], leftoff: fields[13], curator: fields[14], subscribers: fields[15] };
        if (parseInt (channel ['nature']) >= 3 && parseInt (channel ['nature']) <= 9)
          {
          recommends [index]['content'][++count] = channel ['id'];
          pool [channel['id']] = channel;
          }
        log ('channel ' + channel['id'] + ': ' + channel['name'] + ' nature: ' + channel['nature']);
        }
      }
    if (cat ['count'] != count)
      {
      /* count is out of date, update in place */
      cat ['count'] = count;
      }
    }
  return result;
  }

function recommend_channel (index)
  {
  recommend_cursor = index;
  var real_channel = recommends [recommend_set]['content'][recommend_cursor];
  program_lineup (real_channel);
  store_metadata();
  // match_video_to_store();
  tvpreview_set_real_channel (real_channel, undefined, false);
  }

var mdiff;
function match_video_to_store (quiet)
  {
  if (!quiet)
    log ('match video to store');

  video_z_base = 100;
  fixup_video_z_base (quiet);

  if (!quiet)
    {
    log ('#ch1: ' + $("#ch1").attr("class"));
    log ('#ch2: ' + $("#ch2").attr("class"));
    log ('#ch3: ' + $("#ch3").attr("class"));
    }

  var video;
  if ($(".f1").length)
    video = "#" + $(".f1").attr ("id") + "-video";
  else if ($(".b1").length)
    video = "#" + $(".b1").attr ("id") + "-video";
  else
    {
    if (!quiet)
      log ("match video to store: NO ACTIVE VIDEO");
    return;
    }

  if (!quiet)
    log ("match video to store: " + video);

  $(video).css ('position', 'absolute');

  var factor = 1.0; // 0.98
  var w = $(video).width() * factor;
  var h = $(video).height() * factor;

  // var s_margin = ($(window).width() - $("#store-holder").width()) / 2;
  // var p_margin = ($(window).width() - $("#player-holder").width()) / 2;

  player_holder_fixup();
$("#player-holder").css ({top: 0, left: 0, width: "100%", height: "100%", margin: "0 0 0 0"})

  mdiff = ( $("#store-holder").width() - $("#player-holder").width() ) / 2;

  // var left = mdiff + parseInt ( $(video).parent().css ("left") ) + parseInt ( $("#open-set").css ("left") );

  var holder_inset = (parseInt ($(window).width()) - parseInt ($("#store-holder").width()))/2
  var video_inset = ( parseInt ($(video).parent().parent().width()) - parseInt ($(video).width()) ) / 2;
  var left = holder_inset + parseInt ( $("#store-constrain").css ("left") ) + parseInt ( $("#open-set").css ("left") ) 
               + video_inset;

  var top = parseInt ($("#store-constrain").css("top")) + parseInt ($(".f1").css("top"));

  // var top = 1.2 * parseInt ( $(video).css ("top") ) + parseInt ( $("#open-set").css ("top") ) + parseInt ( $("#store-constrain").css ("top") );
  // var left = 1.2 * parseInt ( $(video).css ("left") ) + parseInt ( $("#open-set").css ("left") ) + parseInt ( $("#store-constrain").css ("left") );

  // these don't work right
  // var top = $("#ch1").offset().top;
  // var left = $("#ch1").offset().left;

  // if (!$(video).width || $(video).height)
  //   {
  //   log ("can't determine dimensions of: " + video);
  //   return;
  //   }

  /* TESTING */
  top = $(video).offset().top;
  left = $(video).offset().left;

  w = $(video).width();
  h = $(video).height();

  if ((thumbing == 'store' || thumbing == 'store-wait') && recommend_set == 0)
    {
    /* want offscreen */
    $("#video-layer").css ("left", ( -3 * $(window).width() ) + "px");
    }
  else
    $("#video-layer").css({ width: w, height: h, top: top, left: left });

$("#player-holder").css ({top: 0, left: 0, width: "100%", height: "100%", margin: "0 0 0 0"})
  // player_holder_fixup();
  }

function player_holder_fixup()
  {
  /* ugly fixup required, is Flash messing this up? */
  $("#player-holder").css ({ left: "50%", top: "50%", width: "50%", height: "100%", margin: "-18em 0 0 -32em", 'background-color': "transparent" });
  }

function server_grid (coord)
  {
  var n = 0;
  var conv = {};

  for (var y = 1; y <= 9; y++)
    for (var x = 1; x <= 9; x++)
      conv [y * 10 + x] = ++n;

  if (! (coord in conv))
    {
    log ('server_grid: coordinate error for ' + coord);
    return 0;
    }

  return conv [coord];
  }

function first_empty_channel()
  {
  for (var set = 0; set <= 8; set++)
    {
    var corner = top_lefts [set];
    for (var dy = 0; dy <= 2; dy++)
      for (var dx = 0; dx <= 2; dx++)
        {
        var possible = parseInt (corner) + 10*dy + dx;
        if (! (possible in channelgrid))
          return possible;
        }
    }
  log ("no empty channels!");
  return undefined;
  }

function message (text, duration, callback)
  {
  $("#msg-layer span").html (text);
  $("#msg-layer").show();
  if (callback)
    setTimeout ("remove_msg(); " + callback, duration);
  else
    setTimeout ("remove_msg()", duration);
  }

function store_ok()
  {
  if (!$("#open-set").hasClass ("freeze"))
    {
    if (recommend_set == 0)
      {
      thumbing = 'store-wait';
      sphere = proposed_sphere;
      log ('setting region to: ' + sphere);
      // message ('Content region changed to: ' + sphere, 3000);
      $("#msg-layer span").html (translations ['regch'].replace ('%1', sphere.toUpperCase()));
      $("#msg-layer").show();
      setTimeout ("after_content_region_changed()", 3000);
      setcookie ("sphere-" + user, sphere);
      }
    else
      {
      var real_channel = recommends [recommend_set]['content'][recommend_cursor];
      add_channel (real_channel);
      }
    }
  else
    {
    log ('store_ok: too fast!');
    play_error_noise();
    }
  }

function after_content_region_changed()
  {
  $("#msg-layer").hide();
  store();
  }

function add_channel (real_channel)
  {
  var position;

  log ('add channel: ' + real_channel);

  var position = first_position_with_this_id (real_channel);
  if (position > 0)
    {
    var grid = grid_location (position);
    ask (translations ['alreadysub'].replace ('%1', grid), 
            translations ['qyes'], translations ['qno'], 'watch_now("' + position + '")', "", "", 1);
    return;
    }

  position = ipg_cursor;
  if (!position || position in channelgrid)
    {
    position = first_empty_channel();
    if (!position)
      {
      log_and_alert ('no free squares');
      return;
      }
    }

  if (parseInt (position) == 0 || server_grid (position) == undefined)
    {
    log ('Position is zero!');
    return;
    }

  log ('subscribe: ' + real_channel +  ' at ' + position + '(ipg) = ' + server_grid (position) + '(server)');

  var cmd = "/playerAPI/subscribe?user=" + user + '&' + "channel=" + real_channel + '&' + "grid=" + server_grid (position);

  thumbing = 'store-wait';
  var d = $.get (cmd, function (data)
    {
    thumbing = 'store';
    log ('subscribe raw result: ' + data);
    var fields = data.split ('\t');
    if (fields [0] == '0')
      {
      continue_acceptance (position, real_channel);
      }
    else
      log_and_alert ('subscription error: ' + fields [1]);
    });
  }

function continue_acceptance (position, real_channel)
  {
  var channel_info = pool [real_channel];
  log ('accepting new channel ' + channel_info ['id'] + ' in grid location: ' + position);

  /* insert channel */

  var channel;
  if (channel_info ['id'] in pool)
    channel = pool [channel_info ['id']];
  else
    {
    log ('odd: channel ' + channel_info ['id'] + ' was not in pool');
    channel = channel_info;
    }
  channelgrid [position] = channel;

  ipg_cursor = position;

  redraw_ipg();
  elastic();

  dir_requires_update = true;

  /* obtain programs, if podcast  */

  if (channel ['nature'] != '3' && channel ['nature'] != '4' && channel ['nature'] != '5')
    {
    log ('obtaining programs for: ' + channel_info ['id']);
    var cmd = "/playerAPI/programInfo?channel=" + channel_info ['id'] + unique();
    var d = $.get (cmd, function (data)
      {
      parse_program_data (data);
      redraw_ipg();
      elastic();
      ask_watch_now (position);
      });
    return;
    }

  ask_watch_now (position);
  }

function ask_watch_now (position)
  {
  var grid = grid_location (position);
  ask (translations ['subsuccess'].replace ('%1', grid), translations ['qyes'], translations ['qno'], 'watch_now("' + position + '")', "", "", 1);
  }

function watch_now (position)
  {
  if (position in channelgrid)
    {
    ipg_cursor = position;
    $("#store-layer").hide();
    ipg_play();
    }
  else
    log_and_alert ("Position " + position + " not in channel grid!");
  }

function tutorial()
  {
  if ($("#tutorial-holder").html().match (/^\s*$/))
    {
    var query = '/playerAPI/staticContent?key=10ft-tutorial&lang=' + language;
    var d = $.get (query, function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        {
        data = data.replace (lines[0] + '\n' + lines[1] + '\n', '');
        data = data.replace (/src="images\//gi, 'src="' + root);
        $("#tutorial-holder").html (data);
        tutorial_inner();
        }
      });
    }
  else
    tutorial_inner();
  }

var tutorial_saved_thumbing;

function tutorial_inner()
  {
  tutorial_saved_thumbing = thumbing;
  thumbing = 'tutorial';

  $("#store-page, #player-page, #contact-page").hide();
  $("#tutorial-layer, #guide-page").show();

  $("#guide-page .btn").unbind();
  $("#guide-page .btn").click (function()
    {
    $("#guide-page").hide();
    $("#store-page").show();
    });
  
  $("#store-page .btn").unbind();
  $("#store-page .btn").click (function()
    {
    $("#store-page").hide();
    $("#player-page").show();
    });
  
  $("#player-page .btn").unbind();
  $("#player-page .btn").click (function()
    {
    $("#player-page").hide();
    $("#contact-page").show();
    });
  
  $("#contact-page .btn").unbind();
  $("#contact-page .btn").click (function()
    {
    $("#tutorial-layer").hide();
    thumbing = tutorial_saved_thumbing;
    });
  }

function tutorial_ok()
  {
  if ($("#guide-page").css ("display") == 'block')
    {
    $("#guide-page").hide();
    $("#store-page").show();
    }
  else if ($("#store-page").css ("display") == 'block')
    {
    $("#store-page").hide();
    $("#player-page").show();
    }
  else if ($("#player-page").css ("display") == 'block')
    {
    $("#player-page").hide();
    $("#contact-page").show();
    }
  else if ($("#contact-page").css ("display") == 'block')
    {
    $("#tutorial-layer").hide();
    thumbing = tutorial_saved_thumbing;
    }
  }

function signin_page()
  {
  log ('signin');

  thumbing = 'signin';

  $("#sync-layer").hide();
  $("#signin-layer").show();

  $("#signin").unbind();
  $("#signin").click (signin_panel);

  $("#signup").unbind();
  $("#signup").click (signup_panel);

  $("#btn-signin-close").unbind();
  $("#btn-signin-close").click (signin_close);

  $("#signup-birthyear").val (translations ['s_example'] + ': 1986');

  signup_panel();

  /* why does this need a delay?? */
  setTimeout ('set_language ("' + language + '")', 0);
  }

function signin_panel()
  {
  log ('signin panel');
  $("#signin-tabs li").removeClass ("on");
  $("#signin").addClass ("on");
  $("#signup-panel").hide();
  $("#signin-panel").show();
  $("#btn-signin").unbind();
  $("#btn-signin").click (submit_signin);
  $("#signup-region .radio-item, #signup-language .radio-item").removeClass ("on");
  }

function signup_panel()
  {
  log ('signup panel');
  $("#signin-tabs li").removeClass ("on");
  $("#signup").addClass ("on");
  $("#signin-panel").hide();
  $("#signup-panel").show();
  $("#btn-signup").unbind();
  $("#btn-signup").click (submit_signup);

  $("#signup-region .radio-item, #signup-language .radio-item").removeClass ("on");
  if (language == 'zh')
    $("#signup-language .radio-item").eq(1).addClass ("on");
  else
    $("#signup-language .radio-item").eq(0).addClass ("on");
  if (locale == 'zh' || sphere == 'zh')
    $("#signup-region .radio-item").eq(1).addClass ("on");
  else
    $("#signup-region .radio-item").eq(0).addClass ("on");

  for (var i in { '#signup-gender':0, '#signup-region':0, '#signup-language':0 })
    {
    $(i + ' .radio-item').unbind();
    $(i + ' .radio-item').click (function()
      {
      $(this).siblings().removeClass ("on");
      $(this).addClass ("on");
      });
    }
  }

function signin_close()
  {
  $("#signin-layer").hide();
  $("#sync-layer").show();
  thumbing = 'sync';
  }

function submit_signin()
  {
  var things = [];

  log ('submit signin');

  /* remember this in case user has no name defined */
  sent_email = $("#return-email").val();

  things.push ('email=' + encodeURIComponent ($("#return-email").val()));
  things.push ('password=' + encodeURIComponent ($("#signin-password").val()));

  var serialized = things.join ('&');
  
  $.post ("/playerAPI/login", serialized, function (data)
    {
    log ('login raw data: ' + data);

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == "0")
      {
      $("#signin-layer").hide();

      for (var i = 2; i < lines.length; i++)
        {
        fields = lines [i].split ('\t');
        if (fields [0] == 'token')
          user = fields [1];
        else if (fields [0] == 'name')
          username = fields [1];
        }

      if (!username || username == '')
        {
        username = sent_email;
        if (username.match (/\@/))
          username = username.split (/\@/)[0];
        }

      log ('[explicit login] welcome ' + username + ', AKA ' + user);
      after_startup = "submit_signin_inner()";
      add_to_known_users();
      inherit_sphere_from_cookie();
      startup();
      }
    else
      alert ("bad signin: " + fields[1]);
    })
  }

function submit_signin_inner()
  {
  $("#msg-layer span").html (translations ['welcome'].replace ('%1', username));
  $("#msg-layer").show();
  setTimeout ("remove_msg()", 4000);
  switch_to_ipg();
  }

function submit_signup()
  {
  var things = [];

  var gender = $("#signup-gender .radio-item:eq(1)").hasClass ("on") ? '1' : '0';
  sphere = $("#signup-region .radio-item:eq(1)").hasClass ("on") ? 'zh' : 'en';      /* set the global variable */
  language = $("#signup-language .radio-item:eq(1)").hasClass ("on") ? 'zh' : 'en';  /* set the global variable */
  var year = $("#signup-birthyear").val();

  set_language (language);

  sent_email = $("#signup-email").val();

  things.push ('name=' + encodeURIComponent ($("#signup-name").val()));
  things.push ('email=' + encodeURIComponent ($("#signup-email").val()));
  things.push ('gender=' + gender);
  things.push ('sphere=' + sphere);
  things.push ('ui-lang=' + language);

  if (year.match (/^\d+$/))
    things.push ('year=' + encodeURIComponent (year));

  things.push ('password=' + encodeURIComponent ($("#signup-password").val()));

  if (! $("#signup-email").val().match (/\@/))
    {
    alert ("invalid email");
    return;
    }

  if ($("#signup-password").val() != $("#signup-password2").val())
    {
    alert ("two passwords do not match");
    return;
    }

  if ($("#signup-password").val().length < 6)
    {
    alert ("password too short");
    return;
    }

  var serialized = things.join ('&'); // + '&' + 'user=' + user;
  log ('signup: ' + serialized);

  $.post ("/playerAPI/signup", serialized, function (data)
    {
    log ('signup response: ' + data);

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == "0")
      {
      for (var i = 2; i < lines.length; i++)
        {
        fields = lines [i].split ('\t');
        if (fields [0] == 'token')
          {
          user = fields [1];
          // relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));
          }
        else if (fields [0] == 'name')
          username = fields [1];
        }

      if (!username || username == '')
        {
        username = sent_email;
        if (username.match (/\@/))
          username = username.split (/\@/)[0];
        }

      log ('[login via signup] welcome ' + username + ', AKA ' + user);
      $("#signin-layer").hide();
      setcookie (cookiename, user);
      after_startup = "so_now_youve_signed_up()";
      add_to_known_users();
      inherit_sphere_from_cookie();
      startup();
      }
    else
      alert ('signup failure: ' + fields [1]);
    });
  }

function so_now_youve_signed_up()
  {
  thumbing = 'confirm';
  $("#confirm-layer").show();
  $("#btn-start-add").unbind();
  $("#btn-start-add").click (store);
  }

function state()
  {
  var yt_state = -2;

  var states = { '-2': 'fail', '-1': 'unstarted', '0': 'ended', '1': 'playing', '2': 'paused', '3': 'buffering', '5': 'cued' };

  try
    {
    var channel = '[?]';
    if (thumbing == 'store' || thumbing == 'store-wait')
      channel = recommends[recommend_set]['content'][recommend_cursor]
    else
      channel = channelgrid [ipg_cursor]['id'];
    var nature = pool [channel]['nature'];
    var episode = program_line [program_cursor];
    log ('channel: ' + channel + ' (type ' + nature + ')');
    log ('episode: ' + episode + ' (type "' + programgrid [episode]['type'] + '")');
    }
  catch (error)
    {
    }

  for (var i = 1; i <= 3; i++)
    {
    var video_url = '';
    var video_id = '[no video id]';
    var star = (i == mini_player && current_tube != 'au') ? '*' : ' ';
    try { yt_state = ytmini [i].getPlayerState(); } catch (error) {};
    try { video_url = ytmini [i].getVideoUrl(); } catch (error) {};
    if (video_url.match (/[\?\&]v=([^\&]*)/))
      video_id = video_url.match (/[\?\&]v=([^\&]*)/)[1];
    var offset = '';
    if (ytmini [mini_player] && ytmini [mini_player].getCurrentTime)
      offset = '[position:' + ytmini [mini_player].getCurrentTime() + ']';
    log (star + ' youtube #' + i + ' state ' + yt_state + ': ' + states [yt_state] + ' ' + video_id + ' ' + offset);
    }

  for (var i = 1; i <= 3; i++)
    {
    if (audio_stream [i])
      {
      var star = (i == audio_player && current_tube == 'au') ? '*' : ' ';
      log (star + ' audio' + i + ' playState=' + audio_stream[i].playState + 
             ' isBuffering=' + audio_stream[i].isBuffering + ' muted=' + audio_stream[i].muted +
             ' paused=' + audio_stream[i].paused + ' position=' + audio_stream[i].position + ' url=' + audio_stream[i].url);
      }
    }
  }

var y_cursor;

function settings()
  {
  thumbing = 'settings';
  log ('settings');
  $("#settings-layer").show();
  preload_settings();
  $("#preload").unbind();
  $("#preload").click (preload_settings);
  $("#resolution").unbind();
  $("#resolution").click (resolution_settings);
  $("#acceleration").unbind();
  $("#acceleration").click (acceleration_settings);
  $("#language").unbind();
  $("#language").click (language_settings);
  $("#btn-settings-close, #btn-settings-cancel").unbind();
  $("#btn-settings-close, #btn-settings-cancel").click (settings_close);
  $("#btn-settings-save").unbind();
  $("#btn-settings-save").click (settings_save);
  }

function preload_settings()
  {
  log ('preload settings');
  $("#settings-tabs li").removeClass ("on hilite");
  $("#preload").addClass ("on hilite");
  $("#settings-holder .input-panel").hide();
  $("#preload-panel").show();
  $("#preload-panel .radio-item").unbind();
  $("#preload-panel .radio-item").click (settings_click);
  $("#preload-panel .radio-item").removeClass ("on");
  if (preload == 'off')
    $("#preload-panel .radio-item").eq (0).addClass ("on");
  else
    $("#preload-panel .radio-item").eq (1).addClass ("on");
  y_cursor = 0;
  redraw_settings();
  }

function resolution_settings()
  {
  log ('resolution settings');
  $("#settings-tabs li").removeClass ("on hilite");
  $("#resolution").addClass ("on hilite");
  $("#settings-holder .input-panel").hide();
  $("#resolution-panel").show();
  $("#resolution-panel .radio-item").unbind();
  $("#resolution-panel .radio-item").click (settings_click);
  var rezz = { 'hd1080': 1, 'hd720': 2, 'medium': 3, 'low': 4 };
  $("#resolution-panel .radio-item").removeClass ("on");
  $("#resolution-panel .radio-item").eq (rezz [yt_quality] - 1).addClass ("on");
  y_cursor = 0;
  redraw_settings();
  }

function acceleration_settings()
  {
  log ('acceleration settings');
  $("#settings-tabs li").removeClass ("on hilite");
  $("#acceleration").addClass ("on hilite");
  $("#settings-holder .input-panel").hide();
  $("#acceleration-panel").show();
  $("#acceleration-panel .radio-item").unbind();
  $("#acceleration-panel .radio-item").click (settings_click);
  $("#acceleration-panel .radio-item").removeClass ("on");
  if (wmode == 'transparent')
    $("#acceleration-panel .radio-item").eq (0).addClass ("on");
  else
    $("#acceleration-panel .radio-item").eq (1).addClass ("on");
  y_cursor = 0;
  redraw_settings();
  }

function language_settings()
  {
  log ('language settings');
  $("#settings-tabs li").removeClass ("on hilite");
  $("#language").addClass ("on hilite");
  $("#settings-holder .input-panel").hide();
  $("#language-panel").show();
  $("#language-panel .radio-item").unbind();
  $("#language-panel .radio-item").click (settings_click);
  $("#language-panel .radio-item").removeClass ("on");
  if (language == 'zh')
    $("#language-panel .radio-item").eq (1).addClass ("on");
  else
    $("#language-panel .radio-item").eq (0).addClass ("on");
  if (sphere == 'zh')
    $("#language-panel .radio-item").eq (3).addClass ("on");
  else
    $("#language-panel .radio-item").eq (2).addClass ("on");
  y_cursor = 0;
  redraw_settings();
  }

function settings_up()
  {
  log ('settings up');
  if (y_cursor > 0)
    {
    y_cursor--;
    redraw_settings();
    }
  }

function settings_down()
  {
  log ('settings down');
  var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
  if (y_cursor < 2 || ((panel == 'resolution' || panel == 'language') && y_cursor < 4))
    {
    y_cursor++;
    redraw_settings();
    }
  else if ((panel != 'resolution' && panel != 'language' && y_cursor == 2) 
             || ((panel == 'resolution' || panel == 'language') && y_cursor == 4))
    {
    y_cursor++;
    $("#" + panel + "-panel .radio-item").removeClass ("hilite");
    $("#settings-layer .action-list .btn").removeClass ("on");
    $("#btn-settings-save").addClass ("on");
    }
  }

function settings_left()
  {
  log ('settings left');
  var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
  if ((panel != 'resolution' && panel != 'language' && y_cursor == 3)
         || ((panel == 'resolution' || panel == 'language') && y_cursor == 5))
    {
    settings_swap_buttons();
    }
  else
    {
    if (panel == 'preload')
      language_settings();
    else if (panel == 'resolution')
      preload_settings();
    else if (panel == 'acceleration')
      resolution_settings();
    else if (panel == 'language')
      acceleration_settings();
    }
  }

function settings_right()
  {
  log ('settings right');
  var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
  if ((panel != 'resolution' && panel != 'language' && y_cursor == 3)
         || ((panel == 'resolution' || panel == 'language') && y_cursor == 5))
    {
    settings_swap_buttons();
    }
  else
    {
    if (panel == 'preload')
      resolution_settings();
    else if (panel == 'resolution')
      acceleration_settings();
    else if (panel == 'acceleration')
      language_settings();
    else if (panel == 'language')
      preload_settings();
    }
  }

function settings_swap_buttons()
  {
  var was = $("#btn-settings-save").hasClass ("on");
  $("#settings-layer .action-list .btn").removeClass ("on");
  if (was)
    $("#btn-settings-cancel").addClass ("on");
  else
    $("#btn-settings-save").addClass ("on");
  }

function settings_ok()
  {
  log ('settings ok: ' + y_cursor);
  if (y_cursor > 0)
    {
    var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
    if (y_cursor <= 2 || ((panel == 'resolution' || panel == 'language') && y_cursor <= 4))
      {
      log ('settings soft update');
      if (panel == 'language')
        {
        if (y_cursor <= 2)
          $("#l1, #l2").removeClass ("on");
        else
          $("#l3, #l4").removeClass ("on");
        }
      else
        $("#" + panel + "-panel .radio-item").removeClass ("on");
      $("#" + panel + "-panel .radio-item").eq (y_cursor-1).addClass ("on");
      }
    else if ((panel != 'resolution' && panel != 'language' && y_cursor) == 3 
               || ((panel == 'resolution' || panel == 'language') && y_cursor == 5))
      {
      log ('settings save');
      if ($("#btn-settings-save").hasClass ("on"))
        {
        settings_save();
        }
      else
        {
        log ('settings cancel');
        settings_close();
        }
      }
    }
  }

function settings_click()
  {
  var id = $(this).attr ("id");
  var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
  log ('settings click panel: ' + panel + ' id: ' + id);
  var y = parseInt (id.replace (/^./, '')) - 1;
  if (panel == 'language')
    {
    if (id == 'l1' || id == 'l2')
      $("#l1, #l2").removeClass ("on");
    else
      $("#l3, #l4").removeClass ("on");
    }
  else
    $("#" + panel + "-panel .radio-item").removeClass ("on");
  $("#" + panel + "-panel .radio-item").eq (y).addClass("on");
  }

function settings_save()
  {
  var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
  if (panel == 'preload')
    {
    nopreload = $("#preload-panel .radio-item").eq (0).hasClass ("on");
    preload = nopreload ? 'off' : 'on';
    log ('preload set to: ' + preload);
    setcookie ('tv-preload', preload);
    message (translations ['preloadsav'], 3000, "settings_close()");
    }
  else if (panel == 'resolution')
    {
    var rezz = { 1: 'hd1080', 2: 'hd720', 3: 'medium', 4: 'low' };
    var y = $("#resolution-panel .radio-item.on").attr ("id").replace (/^./, '');
    yt_quality = rezz [y];
    setcookie ('tv-yt-quality', yt_quality);
    message (translations ['ytqsav'], 3000, "settings_close()");
    }
  else if (panel == 'acceleration')
    {
    wmode = $("#acceleration-panel .radio-item").eq (0).hasClass ("on") ? "transparent" : "gpu";
    log ('wmode set to: ' + wmode);
    setcookie ('tv-wmode', wmode);
    $("#settings-layer").hide();
    var q = translations ['accsav'] + ' ' + translations ['reqreload'];
    ask (q, "Reload", "Later", 'reload_page()', "settings_close()", "settings_close()", 1);
    }
  else if (panel == 'language')
    {
    sphere = $("#language-panel .radio-item").eq (3).hasClass ("on") ? 'zh' : 'en';
    language = $("#language-panel .radio-item").eq (1).hasClass ("on") ? 'zh' : 'en';
    set_language (language);
    save_language_setting();
    message (translations ['langsav'], 3000, "settings_close()");
    }
  }

function save_language_setting()
  {
  if (username != 'Guest')
    var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' 
          + 'key=ui-lang' + '&' + 'value=' + language + '&' + 'key=sphere' + '&' + 'value=' + sphere, function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields [0] != '0')
        alert ('Error saving language: ' + fields[1]);
      else
        log ('languaged saved to server successfully');
      });
  }

function settings_close()
  {
  log ('settings close');
  thumbing = 'ipg';
  $("#settings-layer").hide();
  }

function redraw_settings()
  {
  var panel = $("#preload.on, #acceleration.on, #resolution.on, #language.on").attr ("id")
  if (y_cursor > 0)
    {
    $("#settings-tabs li").removeClass ("hilite");
    $("#" + panel + "-panel .radio-item").removeClass ("hilite");
    $("#" + panel + "-panel .radio-item").eq (y_cursor-1).addClass ("hilite");
    }
  else
    {
    $("#" + panel + "-panel .radio-item").removeClass ("hilite");
    $("#" + panel).addClass ("hilite");
    }
  $("#settings-layer .action-list .btn").removeClass ("on");
  }

/* relay identifiers -- change 6:TV into TV-6 */
function prettify (s)
  {
  if (s.match (/^\d+:[A-Z]/))
    {
    var num = s.match (/^(\d+):[A-Z]/)[1];
    s = s.replace (/^\d+:/, '');
    s = s + '-' + num;
    }
  return s;
  }
