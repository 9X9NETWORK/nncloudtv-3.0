/* players */

var current_tube = '';

var ytplayer;
var yt_video_id;
var yt_timex = 0;
var yt_previous_state = -2;
var yt_quality = "medium";
var fetch_yt_timex = 0;
var setup_yt_timex = 0;
var fetch_yt_callbacks = {};

var jwplayer;
var jw_video_file = 'nothing.flv';
var jw_timex = 0;
var jw_previous_state = '';
var jw_position = 0;

var fp_player = 'player1';
var fp_preloaded = '';
var fp_next = ''; /* preload for episode level */
var fp_next_timex = 0;
var start_preload = 0;

var fp = {  player1: { file: '', duration: 0, timex: 0, mute: false, loaded: 0 },
            player2: { file: '', duration: 0, timex: 0, mute: false, loaded: 0 }  };

var fp_content = { url: 'http://9x9ui.s3.amazonaws.com/flowplayer.content-3.2.0.swf',
                   html: '', onClick: function() { $("#body").focus(); log ('FP CONTENT CLICK'); },
                   top: 0, left: 0, borderRadius: 0, padding: 0, height: '100%', width: '100%', opacity: 0 };

var language = 'en';
var sitename = '';

var timezero = 0;
var all_programs_fetched = false;
var all_channels_fetched = false;
var piece_count = 0;
var activated = false;
var jingled = false;
var jingle_timex = 0;
var modal_box = false;
var hint_seen = false;
var popup_active = false;
var remembered_pause = false;
var debug_mode = 1;
var user_cursor;
var dir_requires_update = false;
var nopreload = false;
var divlog = false;
var jumpstart_channel = '';
var jumpstart_program = '';
var jumpstarted_channel = ''; /* past tense */
var add_jumpstart_channel = false;
var shared_but_is_now_logged_in = false;
var bandwidth_measurement = 0;
var bw_started = 0;
var user_closed_episode_bubble = false;
var direct_temp_seen = false;

/* player data record */
var pdr = '';
var n_pdr = 0;

var current_program = '';
var current_url = '';

var thumbing = '';
var dragging = false;
var after_confirm = '';
var after_confirm_function = '';

var yn_cursor;
var yn_ifyes;
var yn_ifno;
var yn_saved_state;

var channelgrid = {};
var channels_by_id = {}
var set_titles = {};
var set = 0;

var pool = {};

var programgrid = {};
var program_line = [];
var n_program_line = 0;
var program_cursor = 1;
var program_first = 1;

var ipg_cursor;
var ipg_entry_channel;
var ipg_entry_program;
var ipg_saved_cursor;
var ipg_timex = 0;
var ipg_delayed_stop_timex = 0;
var ipg_preload_timex = 0;
var ipg_mode = '';

/* top left grid position of all clusters */
var top_lefts = [11, 14, 17, 41, 44, 47, 71, 74, 77];

var zoom_page = '';
var zoom_cursor;
var zoom_saved_cursor;
var we_are_zoom = false;
var zoom_channel = '';
var branding_timex = 0;

var delete_mode;
var delete_cursor = 0;

var clips_played = 0;

/* cache this for efficiency */
var loglayer;

/* ugly fixup for temp landing page */
var temp_fixup_counter = 0;
var temp_fixup_timex = 0;

/* browse */
var browser_x;
var browser_y;
var browser_mode = 'category';
var browse_content = {};
var browse_list = {};
var n_browse_list = 0;
var browse_list_first = 1;
var browsables = {};
var browser_cat = 0;
var browser_cat_cursor = 1;
var browser_first_cat = 1;
var max_browse = 20;
var n_browse = 0;
var saved_thumbing = '';
var control_saved_thumbing = '';
var browse_cursor = 1;
var max_programs_in_line = 9;
var cat_query;
var pending_queries = [];

/* timeout for program or channel index */
var osd_timex = 0;

/* workaround for Chrome not firing 'ended' video event */
var fake_timex = 0;

/* timeout for msg-layer */
var msg_timex = 0;
var yt_error_timex = 0;

var dirty_delay;
var dirty_channels = [];
var dirty_timex;

var control_buttons = [ 'btn-replay', 'btn-rewind', 'btn-play', 'btn-forward', 'btn-sg', 'btn-facebook', 'btn-volume-down', 'btn-volume-up' ];
var control_cursor = 2;

var user = '';
var username = '';
var lastlogin = '';
var first_time_user = 0;

/* if we entered via a shared IPG, and have not upconverted */
var via_shared_ipg = false;

/* reduced functionality if there is a valid user but he is visiting a shared ipg */
var readonly_ipg = false;

var root = 'http://9x9ui.s3.amazonaws.com/9x9playerV58/images/';

var language_en =
  {
  hello: 'Hello',
  signin: 'Sign In / Sign Up',
  signout: 'Sign Out',
  resume: 'Resume Watching',
  episodes: 'Episodes',
  episode_lc: 'episode',
  episodes_lc: 'episodes',
  episodeunits: 'episodes',
  sub_lc: 'subscriber',
  subs_lc: 'subscribers',
  updated: 'Updated',
  onemoment: 'One moment...',
  buffering: 'Buffering...',
  contribute: 'Contribute a video Podcast RSS / YouTube Channel / Facebook Page:',
  returningusers: 'Returning Users',
  name: 'Name',
  password: 'Password',
  passverify: 'Verify password',
  email: 'Email',
  login: 'Sign in',
  deletetip: 'Select a channel to delete it',
  findel: 'Exit Delete Mode',
  nochandel: 'No more channels to delete',
  errormov: 'Moving channel error. Please try again.',
  notplaying: 'Nothing was playing',
  noepchan: 'No episodes in this channel',
  thanx: 'Thank you for using 9x9.tv. You have signed out.',
  logfail: 'Sign in failure',
  validmail: 'Please provide a valid email address.',
  passmatch: 'The two passwords you entered do not match.',
  sixchar: 'Please choose a password of at least six characters.',
  signupfail: 'Signup failure',
  suberr: 'Cannot add the channel to Smart Guide now',
  syschan: 'System channel cannot be deleted',
  deletethis: 'Do you want to delete this channel?',
  nochansquare: 'There is no channel in this spot.',
  internal: 'An internal error has occurred',
  playthis: 'Play this channel now?',
  sharing: 'You will be sharing all the channels on your Smart Guide with your Facebook friends. Continue?',
  noeps: 'There are no episodes in this channel',
  addtip: 'Select the empty spot to browse available channels',
  signuptip: 'Sign up and create your own Smart Guide',
  signouttip: 'Sign out',
  startdel: 'Delete channels in your Smart Guide',
  returnipg: 'Return to the channel you were watching before',
  browsetip: 'Browse the categories for available channels',
  addrss: 'Can’t find what you like? Add your favorite video Podcast RSS or Youtube channels!',
  returnsmart: 'Return to Smart Guide',
  rsbubble: 'Return to Smart Guide for more interesting content',
  copypaste: 'Copy and paste video podcast RSS or Youtube channel URL here',
  threecat: 'Please select one to three categories',
  processing: 'We will start processing the channel you contributed right after it is submitted',
  browsecats: 'Browse the categories for available channels',
  watchnow: 'Select this channel to watch it now',
  addsmart: 'Select this channel to add it to your Smart Guide',
  enterwatch: 'Select to watch this channel now, or continue browsing',
  sharedipg: "This is your friend's Smart Guide. You can only edit your own Smart Guide.",
  suredel: 'Are you sure you want to delete this channel?',
  havedel: 'You have deleted channel',
  delmore: 'Delete More Channels',
  chandir: 'Channel Directory',
  delchan: 'Delete Channels',
  subscribed: 'Subscribed',
  entersub: 'Select to subscribe',
  addchannel: 'Add Channel',
  needcat: 'Please select at least one category for this channel',
  needurl: 'Please provide a URL',
  pleasewait: 'Please wait...',
  addrssyt: 'Add YouTube, Facebook, or RSS',
  category: 'Category',
  go: 'Go',
  succpress: 'Successful! Press Enter to watch now',
  hinstr: 'Instruction',
  hwbsg: 'While Browsing Smart Guide',
  hwwe: 'While Watching Episodes',
  hctw: 'Close this window',
  huak: 'Use arrow keys or mouse to navigate',
  hpec: 'Play episodes in the channel selected or add new channels',
  hscp: 'Show playback controls',
  hshow: 'Show episodes in this channel',
  qyes: 'Yes',
  qno: 'No',
  cup: 'Press <span class="enlarge">&uarr;</span> to see your Smart Guide',
  cdown: 'Press <span class="enlarge">&darr;</span> for more episodes',
  chcat: 'Channel category',
  ytvid: 'Video disabled by Youtube',
  eoe: 'End of episodes',
  close: 'Close',
  oneempty: 'You still have one empty channel',
  noempty: 'You have no empty channels!',
  empties: 'You still have %1 empty channels',
  aboutus: 'About Us',
  about1: '9x9 is a cloud based video platform which allows internet content to be discovered and enjoyed through a Smart Guide on Smart TV, Smart Phone and Tablet Devices.',
  about2: 'Discover the magic of the Smart Guide, a 9x9 grid which can be personalized and populated with up to 81 channels to satisfy your everyday online video appetite.',
  about3: 'Watch your favorite podcasts, YouTube channels and other episodic content on 9x9 just like watching TV.',
  about4: '9x9 is based in Santa Clara, California, USA.  We are a bunch of geeks passionate about revolutionizing online video discovery through a human powered network.',
  about5: 'Our investors include venture capitalists, private investors and corporate investors including D-Link.  Contact us at <a href="mailto:feedback@9x9Cloud.tv">feedback@9x9Cloud.tv</a>.',
  newusers: 'New Users',
  signup: 'Sign up',
  successful: 'Successful!',
  failed: 'FAILED',
  hour: 'hour',
  minute: 'minute',
  day: 'day',
  month: 'month',
  year: 'year',
  ago: 'ago',
  hence: 'hence',
  expired: 'This episode has expired and is no longer available. Click OK to visit your Smart Guide where you may select another episode or channel.',
  cinstr: 'Mouse over the control bar to see episodes',
  creplay: 'Play from the beginning',
  crw: 'Rewind',
  cplay: 'Play',
  cpause: 'Pause',
  cff: 'Fast forward',
  cipg: 'Return to Smart Guide',
  cfb: 'Share to Facebook',
  cvolup: 'Volume Up',
  cvoldown: 'Volume Down',
  cmute: 'Mute',
  cunmute: 'Unmute',
  uncaught: 'The system may be experiencing problems. Please try again later.',
  alreadysub: 'You were already subscribed to this channel.',
  pva: 'Your Personal Video Album',
  moresets: 'More Sets',
  addset: 'Add This Set',
  follow: 'Follow These Channels',
  followhint: 'Click to receive the latest episodes from these channels',
  curchan: 'Current Channel',
  plzreg: 'Please register to use this feature.',
  badlanding: 'The URL you have landed on is incorrect. Please visit our SmartGuide.',
  help: 'Help',
  toastfollow: 'Follow this channel?',
  edit: 'Edit',
  done: 'Done',
  addmorech: 'Add More Channels',
  setnum: 'Set %d',
  cannotshare: 'Cannot share this type of channel'
  };

var language_tw =
  {
  hello: '您好',
  signin: '登入 / 註冊',
  signout: '登出',
  resume: '繼續觀看節目',
  episodes: '節目集數',
  episode_lc: '節目集數',
  episodes_lc: '節目集數',
  episodeunits: '個節目',
  sub_lc: '訂閱人數',
  subs_lc: '訂閱人數',
  updated: '已更新',
  onemoment: '稍待片刻...',
  buffering: '載入中...',
  contribute: '請輸入Podcast RSS / Youtube 頻道連結 (www.youtube.com/user/userid):',
  returningusers: '已註冊用戶',
  name: '姓名',
  password: '密碼',
  passverify: '確認密碼',
  email: '電子郵件',
  login: '登入',
  deletetip: '選擇要刪除的頻道',
  findel: '結束刪除頻道',
  nochandel: '所有頻道均已被刪除',
  errormov: '無法搬移頻道, 請稍後再試',
  notplaying: '沒有可繼續觀賞的節目',
  noepchan: '頻道中沒有節目，暫時無法播放',
  thanx: '您已登出，感謝您使用9x9.tv',
  logfail: '登入失敗',
  validmail: '請提出正確電子郵件地址',
  passmatch: '兩次輸入的密碼需吻合',
  sixchar: '密碼長度至少需為六個字元',
  signupfail: '註冊帳號失敗，請重新輸入',
  suberr: '沒有辦法加入Smart Guide，請重新輸入',
  syschan: '系統頻道不得刪除，敬請見諒',
  deletethis: '請問確定要刪除這個頻道嗎？?',
  nochansquare: '空格中沒有頻道',
  internal: '內部錯誤發生，請重新嘗試',
  playthis: '要播放這個頻道嗎？',
  sharing: '您的所有Facebook好友都會看到您的Smart Guide內容。請問是否要繼續？ ',
  noeps: '此頻道中沒有節目，請觀看別的頻道',
  addtip: '選擇空格後，即可挑選新頻道並放入空格',
  signuptip: '註冊即可保證您的Smart Guide更新將會被妥善儲存',
  signouttip: '登出',
  startdel: '選擇此按鈕，開始刪除頻道',
  returnipg: '返回原本觀看的頻道',
  browsetip: '瀏覽頻道分類，挑選新頻道',
  addrss: '找不到喜歡的頻道嗎？您可以加入喜歡的影片Podcast RSS 連結或Youtube頻道網址!',
  returnsmart: '返回Smart Guide',
  rsbubble: '回Smart Guide可觀賞更多有趣內容',
  copypaste: '把Podcast RSS連結或Youtube頻道網址複製貼上',
  threecat: '請選擇一到三個分類',
  processing: '頻道送出後，我們立刻開始處理頻道',
  browsecats: '瀏覽各分類下的頻道',
  watchnow: '選擇頻道之後，即可立即觀看',
  addsmart: '選擇要加入Smart Guide的頻道',
  enterwatch: '選擇頻道後即可開始觀看，也可以瀏覽其它頻道',
  sharedipg: '這是好友分享給您的Smart Guide，您沒有修改它的權限',
  suredel: '請問是否確定要刪除此頻道？',
  havedel: '您已刪除此頻道',
  delmore: '繼續刪除頻道',
  chandir: '頻道目錄',
  delchan: '刪除頻道',
  subscribed: '已訂閱',
  entersub: '選擇即可訂閱',
  addchannel: '新增頻道',
  needcat: '請為此頻道至少挑選一個分類',
  needurl: '請輸入URL',
  pleasewait: '請稍待…',
  addrssyt: '新增 RSS / YouTube',
  category: '頻道分類',
  go: '提交',
  succpress: '訂閱成功! 按"Enter" 鍵即可開始觀看',
  hinstr: '指令',
  hwbsg: '當瀏覽Smart Guide時',
  hwwe: '當觀看影片時',
  hctw: '關閉此視窗',
  huak: '使用方向鍵或鼠標導航',
  hpec: '播放選定頻道的影片或添加新的頻道',
  hscp: '顯示控制台',
  hshow: '播放此頻道的節目',
  qyes: '是',
  qno: '否',
  cup: '按 <span class="enlarge">&uarr;</span> 瀏覽您的Smart Guide',
  cdown: '按 <span class="enlarge">&darr;</span> 觀看其它影片',
  chcat: '頻道分類', 
  ytvid: '此影片被Youtube禁播',
  eoe: '所有節目播映完畢',
  close: '關閉',
  oneempty: '您只剩下一個空位可放頻道。',
  noempty: '您已填滿Smart Guide，請刪除現有頻道之後再新增',
  empties: '您仍剩 %1個空位可放頻道',
  aboutus: '關於我們',
  about1: '9x9 為雲端影片平台，用戶可透過9x9服務在智慧型電視、智慧型手機、平板設備上的Smart Guide發現與觀賞最新最有趣的網路影片內容。',
  about2: 'Smart Guide 共有九九八十一格的空間，可以根據觀眾的興趣填入不同的頻道，滿足所有人不同的影片需求。',
  about3: '您可在9x9上觀賞最喜歡的podcast，Youtube頻道，以及其它系列節目，就像看電視一樣的方便。',
  about4: '9x9位於美國加州Santa Clara。我們是一群對改變網路影片生態極有熱情的科技人，透過發展人機合一的平台， 協助網路使用者獲取更好的觀看經驗。',
  about5: '我們的投資人包括風險投資公司、私人投資者，以及企業投資者，包括D-Link。如欲與我們聯絡，請來信至<a href="mailto:feedback@9x9cloud.tv">feedback@9x9cloud.tv</a>。',
  newusers: '新用戶',
  signup: '註冊',
  successful: '新增成功',
  failed: '新增失敗',
  hour: '小時',
  minute: '分鐘',
  day: '天',
  month: '月',
  year: '年',
  ago: '前',
  hence: '後',
  expired: '本節目已經不存在，請點選OK進入Smart Guide，選擇其它頻道或節目開始觀看。',
  cinstr: '將滑鼠移到這裡即可看到節目列表',
  creplay: '從頭開始播放',
  crw: '回轉',
  cplay: '播放',
  cpause: '暫停',
  cff: '快轉',
  cipg: '回到 Smart Guide',
  cfb: '分享至 Facebook',
  cvolup: '音量大',
  cvoldown: '音量小',
  cmute: '靜音',
  cunmute: '恢復音量',
  uncaught: '系統可能遇到問題。 請稍後再試。',
  alreadysub: '您已經添加過該頻道。',
  pva: '個人專屬影音集合',
  moresets: '更多頻道套餐',
  addset: '新增此頻套餐',
  follow: '關注這些頻道',
  followhint: '點擊即可收到該頻道最新劇集',
  curchan: '當前頻道',
  featchan: '精選頻道',
  plzreg: '註冊後才可以使用該功能',
  badlanding: '您登陆的網址有誤，請查看SmartGuide',
  help: '幫助',
  toastfollow: '關注這個頻道？',
  edit: '編輯',
  done: '完成',
  addmorech: '增加頻道',
  setnum: '套餐 %d',
  cannotshare: '此服務暫不提供'
  };

var translations = language_en;

var landing_pages = {};

$(document).ready (function()
 {
 var now = new Date();
 timezero = now.getTime();
 log ('begin execution');
 init();
 pre_login();
 $(window).resize (function() { elastic(); });
 });

function elastic()
  {
  log ('elastic');
  elastic_innards();
  }

function elastic_innards()
  {
  var newWidth  = $(window).width()  / 16;
  var newHeight = $(window).height() / 16;

  var xtimes = newWidth  / 64 * 100;
  var ytimes = newHeight / 36 * 100;

  $("body").css ("font-size", ((xtimes >= ytimes) ? ytimes : xtimes) + "%");

  var vh = $(window).height();
  var vw = $(window).width();

  var h = document.getElementById ("yt1");
  h.style.width = vw + "px";

  resize_fp();

  ipg_fixup_margin();
  ipg_fixup_middle();

  episode_end_layer_fixup();
  dir_waiting_fixup();
  align_center();

  if (!jingled)
    align_jingle();

  if (thumbing == 'ipg')
    extend_ipg_timex();

  if ($.browser.msie)
    $("#sg-layer, #ch-directory, #msg-layer, #confirm-layer, #yesno-layer, #delete-layer, #hint-layer, #opening, #success-layer").css ({"width": vw, "height": vh});

  var offset = $("#landing").offset();
  $("#branding-holder").css ({ "left": offset.left, "top": offset.top });

  direct_temp_fixup();

  episode_clicks_and_hovers();
  }

function direct_temp_fixup()
  {
  offset = $("#sg-content").offset();
  $("#direct-temp").css ({ "left": offset.left, "top": offset.top });
  }

function episode_end_layer_fixup()
  {
  var wh = $(window).height();
  var eh = $("#epend-layer").height();
  var et = (wh-eh)/2;
  $("#epend-layer").css ("top",et);
  }

function ipg_fixup_margin()
  {
  $("#list-holder").css ("top", ($("#ipg-grid").height() - $("#list-holder").height()) / 2);
  }

function ipg_fixup_middle()
  {
  try { $("#ipg-holder").css ("top", ($(window).height() - $("#ipg-holder").height()) / 2); } catch (error) {};
  try { $("#dir-holder").css ("top", ($(window).height() - $("#dir-holder").height()) / 2); } catch (error) {};
  try { $("#list-holder").css ("top", ($("#ipg-grid").height() - $("#list-holder").height()) / 2); } catch (error) {};
  }

function dir_waiting_fixup()
  {
  var offset = $(".content-panel").offset();
  $("#dir-waiting").css ({ "left": offset.left, "top": offset.top });
  }

function align_center()
  {
  var ww = $(window).width();
  var wh = $(window).height();

  for (var v in { 'ipg-holder':0, 'sg-holder':0, 'dir-holder':0, 'new-holder':0, 'ep-layer':0, 'epend-layer':0, 'waiting':0, 'delete-layer':0 })
    $("#" + v).css ("left", (ww - $("#" + v).width()) / 2);

  $("#buffering").css      ("left", (ww - $("#buffering").width())      / 2);
  $("#confirm-layer").css  ("left", (ww - $("#confirm-layer").width())  / 2);
  $("#signin-layer").css   ("left", (ww - $("#signin-layer").width())   / 2);
  $("#control-layer").css  ("left", (ww - $("#control-layer").width())  / 2);
  $("#yesno-layer").css    ("left", (ww - $("#yesno-layer").width())    / 2);
  $("#ad-layer").css       ("left", (ww - $("#ad-layer").width())       / 2);

  $("#signin-holder").css  ("margin-top",  (wh - $("#signin-holder").height()) / 2);
  $("#msg-holder").css     ("margin-top",  (wh - $("#msg-holder").height() - $("#ep-layer").height()) / 2);
  $(".success-holder").css ("margin-top",  (wh - $(".success-holder").height()) / 2);

  for (var v in { signin:0, hint:0, confirm:0, yesno:0, about:0, 'delete':0, 'sg':0 })
    $("#" + v + "-holder").css ("margin-top",  (wh - $("#" + v + "-holder").height()) / 2);

  if ($.browser.msie && $.browser.version == 7)
    $("#control-layer, #control-bar").css ("width", ww);
  }

function align_jingle()
  {
  var wh = $(window).height();
  $("#splash").css ("margin-top", (wh - $("#splash").height()) / 2);
  }

function set_language (lang)
  {
  language = lang;
  translations = (language == 'zh' || language == 'zh-tw') ? language_tw : language_en;
  solicit();
  $("#resume1").html (translations ['resume']);
  $("#episodes1").html (translations ['episodes'] + ': ');
  $("#episodes2").html (translations ['episodes'] + ':');
  $("#updated1").html (translations ['updated'] + ':');
  $("#contribute").html (translations ['contribute'] + ':');
  $("#returning1").html (translations ['returningusers']);
  $("#pw1").html (translations ['password'] + ':');
  $("#pw2").html (translations ['password'] + ':');
  $("#pwv2").html (translations ['passverify'] + ':');
  $("#email1").html (translations ['email'] + ':');
  $("#email2").html (translations ['email'] + ':');
  $("#name2").html (translations ['name'] + ':');
  $("#loginbtn").html (translations ['login']);

  $("#delrsg").html (translations ['returnsmart']);
  $("#moment1").html (translations ['onemoment']);
  $("#moment2").html (translations ['onemoment']);
  $("#buffering1").html (translations ['buffering']);
  $("#chdirtxt").html (translations ['chandir']);
  $("#edit-or-finish").html (translations ['delchan']);
  $("#rsg1").html (translations ['returnsmart']);
  $("#rsg2").html (translations ['returnsmart']);
  $("#category1").html (translations ['category']);
  $("#close1").html (translations ['close']);
  $("#toast-txt span").html (translations ['toastfollow']);
  $("#edit-txt span").html (translations ['edit']);
  $("#section-title span").html (translations ['curchan']);
  $("#btn-add-channels span").html (translations ['addmorech']);
  $("#btn-about-txt").html (translations ['aboutus']);
  $("#btn-help-txt").html (translations ['help']);
  $("#slogan span").html (translations ['pva']);

  for (var s in { 'hello':0, 'suredel':0, 'havedel':0, 'delmore':0, 'addrssyt':0, 'chcat':0 })
    $("#" + s).html (translations [s]);
  for (var s in { 'hinstr':0, 'hwbsg':0, 'hwwe':0, 'hctw':0, 'huak':0, 'hshow':0, 'qno':0, 'qyes':0 })
    $("#" + s).html (translations [s]);
  for (var s in { 'cup':0, 'cdown':0, 'aboutus':0, 'about1':0, 'about2':0, 'about3':0, 'about4':0, 'about5':0 })
    $("#" + s).html (translations [s]);
  for (var s in { 'newusers':0, 'signup':0, 'cinstr':0, 'rsbubble':0 })
    $("#" + s).html (translations [s]);

  $("#btn-volume-up img").attr ("title", translations ['cvolup']);
  $("#btn-volume-down img").attr ("title", translations ['cvoldown']);
  $("#btn-mute img").attr ("title", translations ['cunmute']);
  $("#btn-facebook img").attr ("title", translations ['cfb']);
  $("#btn-sg img").attr ("title", translations ['cipg']);
  $("#btn-forward img").attr ("title", translations ['cff']);
  $("#btn-pause img").attr ("title", translations ['cpause']);
  $("#btn-play img").attr ("title", translations ['cplay']);
  $("#btn-rewind img").attr ("title", translations ['crw']);
  $("#btn-replay img").attr ("title", translations ['creplay']);

  log ('language information set');
  }

function resize_fp()
  {
  var vh = $(window).height();

  for (var p in { 'player1':'', 'player2':'', 'v':'', 'fp1':'', fp2:'', 'yt1':'' })
    {
    var h = document.getElementById (p);
    h.style.height = vh + "px";
    }
  }

function log (text)
  {
  try
    {
    if (window.console && console.log)
      console.log (text);

    if (!loglayer)
      loglayer = document.getElementById ("log-layer");

    if (divlog)
      loglayer.innerHTML += text + '<br>';

    report ('s', text);
    }
  catch (error)
    {
    }
  }

function log_and_alert (text)
  {
  panic (text);
  }

function panic (text)
  {
  log (text);
  alert (text);
  }

function report (type, arg)
  {
  var delta = Math.floor ((new Date().getTime() - timezero) / 1000);

  pdr += (delta + '\t' + type + '\t' + arg + '\n');

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

  var serialized = 'user=' + user + mso() + '&' + 'session=' + 
           Math.floor (timezero/1000) + '&' + 'time=' + delta + '&' + 'pdr=' + encodeURIComponent (pdr);

  pdr = '';

  $.ajax ({ type: 'POST', url: "playerAPI/pdr", data: serialized, 
              dataType: 'text', success: report_, error: report_error_ });
  }

function report_program()
  {
  if (we_are_zoom)
    report ('w', current_program);
  else
    report ('w', current_program + '\t' + channelgrid [current_channel]['id']);
  }

function init()
  {
  Array.prototype.remove = function (val)
    {
    for (var i = 0; i < this.length; i++)
      {
      if (this [i] == val)
        {
        this.splice (i, 1);
        break;
        }
      }
    }

  setup_ajax_error_handling();

  if (started_from_shared_ipg())
    via_shared_ipg = true;

  /* Initialize FB Javascript SDK */

  FB.init (
    {
    appId: '110847978946712',
    status: true, // check login status
    cookie: true, // enable cookies to allow the server to access the session
    xfbml: true   // parse XFBML
    });

  if ((location+'').match (/preload=off/))
    nopreload = true;

  if ((location+'').match (/divlog=on/))
    divlog = true;

  if ((location+'').match (/ytq=/))
    yt_quality = (location+'').match (/ytq=([^&]+)/)[1];

  $("#fp1, #fp2, #yt1").mousemove (mousemove); // .mouseout (mouseaway);

  $(window).unload (function() { report_submit_(); });
  }

function started_from_shared_ipg()
  {
  var pathname = location.pathname;
  var split = pathname.split ('/');
  return (!shared_but_is_now_logged_in && split.length == 3 && split[2].match(/^[0-9]+$/));
  }

function get_ipg_id()
  {
  var split = location.pathname.split ('/');
  return split[2];
  }

function user_or_ipg()
  {
  return readonly_ipg ? 'ipg=' + get_ipg_id() : 'user=' + user;
  }

function mso()
  {
  return (sitename == '') ? '' : ('&' + 'mso=' + sitename);
  }

function fetch_programs_in (channel)
  {
  log ('obtaining programs for ' + channel);

  var position = channels_by_id [channel];
  var nature = channelgrid [position]['nature'];
  if (nature == '3' || nature == '4' || nature == '5')
    {
    log ('(obtaining: youtube)');
    if (fetch_youtube_or_facebook ('grid', position, "") == false)
      {
      /* already fetched */
      $("#waiting, #dir-waiting").hide();
      if (thumbing == 'ipg-wait')
        thumbing = 'ipg';
      }
    return;
    }

  log ('(obtaining: standard)');
  var query = "playerAPI/programInfo?channel=" + channel + mso() + '&' + user_or_ipg();

  var d = $.get (query, function (data)
    {
    parse_program_data (data);

    $("#waiting, #dir-waiting").hide();
    if (thumbing == 'ipg-wait')
      thumbing = 'ipg';

    if (thumbing == 'ipg')
      {
      ipg_metainfo();
      ipg_sync();
      start_preload_timer();
      }
    });
  }

function fetch_everything()
  {
  all_channels_fetched = false;
  all_programs_fetched = false;

  channelgrid = {};
  programgrid = {};
  channels_by_id = {};

  fetch_channels();
  fetch_programs();
  }

function fetch_programs()
  {
  log ('obtaining programs');

  var query = "playerAPI/programInfo?channel=*" + mso() + '&' + user_or_ipg();

  var d = $.get (query, function (data)
    {
    if (sanity_check_data ('programInfo', data))
      parse_program_data (data);
    else
      log ('*** programInfo: DATA RETURNED BY SERVER FAILS SANITY CHECK');

    all_programs_fetched = true;

    $("#waiting").hide();
    if (thumbing == 'ipg-wait')
      thumbing = 'ipg';

    setTimeout ("update_new_counters()", 0);

    /* we don't use these */
    erase_all_unclean_youtube_channels()
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

    for (var i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');

        if (fields[0] in channels_by_id)
          {
          var position = channels_by_id [fields[0]];
          var nature = channelgrid [position]['nature'];
          if (nature == '3' || nature == '4')
            continue;
          }

        programgrid [fields [1]] = { 'channel': fields[0], 'type': fields[3], 'url1': 'fp:' + fields[8], 
                     'url2': 'fp:' + fields[9], 'url3': 'fp:' + fields[10], 'url4': 'fp:' + fields[11], 
                     'name': fields[2], 'desc': fields [3], 'type': fields[4], 'thumb': fields[6], 
                     'snapshot': fields[7], 'timestamp': fields[12], 'duration': fields[5] };
        }
      }

    log ('finished parsing program data');
    }
  else
    log_and_alert ('server returned nothing');
  }

function fetch_channels()
  {
  log ('obtaining channels');

  var query;

  if (via_shared_ipg)
    query = "playerAPI/loadIpg?ipg=" + get_ipg_id() + mso();
  else
    query = "playerAPI/channelLineup?user=" + user + mso() + '&' + 'setInfo=true';

  var d = $.get (query, function (data)
    {
    var n = 0;
    var conv = {};

    for (var y = 1; y <= 9; y++)
      for (var x = 1; x <= 9; x++)
        conv [++n] = "" + y + "" + x;

    var lines = data.split ('\n');
    log ('number of channels obtained: ' + (lines.length - 3));

    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('server error: ' + lines [0]);
      return;
      }

    var block_start_line = 2;

    if (via_shared_ipg)
      {
      var fields = lines[2].split ('\t');
      jumpstart_channel = fields [0];
      jumpstart_program = fields [1];
      if (fields [10] && fields [10].match (/^http:\/\/www\.youtube\.com\//))
        jumpstart_program = jumpstart_channel + '.' + fields [10].match (/v=([^&]+)/)[1];
      block_start_line = 4;
      }

    if (via_shared_ipg)
      {
      /* hack! this is temporary, until we dispose of loadIpg */
      var i = block_start_line - 1;
      }
    else
      {
      for (var i = block_start_line; i < lines.length && lines [i] != '--'; i++)
        {
        if (lines [i] != '')
          {
          var fields = lines[i].split ('\t');
          if (fields[0] >= 1 && fields[0] <= 9)
            set_titles [top_lefts [parseInt (fields[0]) - 1]] = fields[2];
          }
        }
      }

    for (var j = i + 1; j < lines.length && lines [j] != '--'; j++)
      {
      if (lines [j] != '')
        {
        var fields = lines[j].split ('\t');
        log ("channel line " + j + ": " + conv [fields[0]] + ' = ' + lines [j]);
        channelgrid [conv [fields[0]]] = { 'id': fields[1], 'name': fields[2], 'desc': fields[3], 'thumb': fields[4], 'count': fields[5], 'type': fields[6], 'status': fields[7], 'nature': fields[8], 'extra': fields[9] };
        channels_by_id [fields[1]] = conv [fields[0]];
        }
      else
        log ("ignoring channels line " + j + ": " + lines [j]);
      }

    all_channels_fetched = true;

    if (add_jumpstart_channel)
      {
      add_jumpstart_channel_inner();
      return;
      }

    after_fetch_channels (false);
    });
  }

function after_fetch_channels (ipg_flag)
  {
  log ('after fetch channels');

  log ('----- ipg_flag: ' + ipg_flag);
  log ('----- activated: ' + activated);
  log ('----- shared_but_is_now_logged_in: ' + shared_but_is_now_logged_in);
  log ('----- add_jumpstart_channel: ' + add_jumpstart_channel);
  log ('----- via_shared_ipg: ' + via_shared_ipg);
  log ('----- readonly_ipg: ' + readonly_ipg);

  if (!activated)
    activate();
  else if (ipg_flag || shared_but_is_now_logged_in)
    {
    shared_but_is_now_logged_in = false;
    switch_to_ipg();
    }
  else
    {
    redraw_ipg();
    elastic();
    }

  if (!all_programs_fetched)
    setTimeout ("fetch_programs_piecemeal()", 10000);
  }

function update_new_counters()
  {
  redraw_ipg();
  elastic();

  for (var channel in channelgrid)
    {
    var first = first_program_in (channel);
    channelgrid [channel]['new'] = programs_since (channel, lastlogin);
    }

  redraw_ipg();
  elastic();
  }

function programs_since (channel, timestamp)
  {
  var n = 0;

  if (! (channel in channelgrid))
    return 0;

  var real_channel = channelgrid [channel]['id'];

  for (p in programgrid)
    {
    if (programgrid [p]['channel'] == real_channel)
      {
      if (programgrid [p]['timestamp'] > timestamp)
        n++;
      }
    }

  return n;
  }

function fetch_programs_piecemeal()
  {
  if (!all_programs_fetched)
    {
    log ('fetching programs piecemeal (taking too long)');
    var these = [];
    piece_count = 0;
    for (var ch in channelgrid)
      {
      these.push (channelgrid [ch]['id']);
      if (these.length >= 5)
        {
        fetch_piece (these);
        these = [];
        }
      }
    if (these.length > 0)
      fetch_piece (these);
    }
  }

function fetch_piece (charray)
  {
  var query = "playerAPI/programInfo?channel=" + charray.join (',') + mso() + '&' + user_or_ipg();
  var d = $.get (query, function (data)
    {
    parse_program_data (data);
    piece_count += charray.length;
    var n_channels = 0;
    for (var ch in channelgrid) { n_channels++; }
    if (piece_count == n_channels)
      all_programs_fetched = true;
    });
  pending_queries.push (d);
  }

function abort_pending_queries()
  {
  for (var i in pending_queries)
    {
    try { pending_queries[i].abort(); } catch (error) {};
    }
  pending_queries = [];
  }

function browser_support()
  {
  if (jQuery.browser.msie && !jQuery.browser.version.match (/^[789]/))
    {
    $("#blue").html ('<p>&nbsp;<p>&nbsp;<p>Please use the Chrome browser for this application:<p>&nbsp; &nbsp;<a href="http://www.google.com/chrome">www.google.com/chrome</a><p>');
    return false;
    }
  return true;
  }

function activate()
  {
  log ('activate');

  /* google analytics */
  analytics();

  if (jingled)
    {
    log ('have already jingled');
    $("#opening").hide();
    }

  if (!browser_support())
    return;

  activated = true;
  elastic();

  if (jumpstart_channel != '')
    {
    jumpstart()
    return;
    }

  current_channel = first_channel();
  program_cursor = 1;
  program_first = 1;
  current_program = first_program_in (current_channel);

  enter_channel ('program');

  if (false && location.host == 'ocw.9x9.tv')
    {
    set_titles [11] = "MIT Open Course Ware";
    set_titles [14] = "U-M Open Course Ware";
    set_titles [17] = "Space Science";
    }

  $("#ep-layer").hide();
  document.onkeydown=kp;
  redraw_ipg();

  var path = location.pathname;
  path = path.replace (/^\//, '');

  // if (path in landing_pages)
  if (path != '9x9' && path != '5f' && path != '9x9/' && path != '5f/' && !path.match (/^share\//))
    {
    custom_url (path);
    return;
    }
  else
    {
    switch_to_ipg();
    setTimeout ("direct_landing()", 700);
    }

  finish_activation();
  }

function direct_landing()
  {
  if (!direct_temp_seen && username == 'Guest')
    {
    direct_temp_fixup();

    $("#direct-temp").show();
    $("#ep-layer").hide();
    $("#ep-layer").css ("visibility", "hidden");

    $("#btn-direct-enter").click (direct_enter);
    $("#btn-direct-signup").click (direct_signup);

    direct_temp_fixup();

    temp_fixup_counter = 0;
    temp_fixup_timex = setInterval ("direct_temp_fixup_task()", 100);

    setTimeout ("remove_direct_temp()", 15000);

    direct_temp_seen = true;
    }
  }

function direct_temp_fixup_task()
  {
  direct_temp_fixup();
  if (temp_fixup_counter++ > 200)
    clearTimeout (temp_fixup_timex);
  }

function direct_enter()
  {
  $("#direct-temp").hide();
  clearTimeout (temp_fixup_timex);
  ipg_sync();
  $("#ep-layer").css ("visibility", "visible");
  $("#ep-layer").show();
  }

function direct_signup()
  {
  $("#direct-temp").hide();
  clearTimeout (temp_fixup_timex);
  ipg_sync();
  $("#ep-layer").css ("visibility", "visible");
  $("#ep-layer").show();
  login_screen();
  }

function remove_direct_temp()
  {
  var displayed = $("#direct-temp").css ("display");

  $("#direct-temp").hide();
  clearTimeout (temp_fixup_timex);

  if (displayed == 'block')
    {
    $("#ep-layer").show();
    ipg_sync();
    }

  $("#ep-layer").css ("visibility", "visible");
  }

function finish_activation()
  {
  $("#blue").hide();
  preload_control_images()

  jw_play_nothing();

  $("body").focus();
  check_bandwidth();

  elastic();
  }

function custom_url (path)
  {
  log ('custom url: ' + path);

  var query = "playerAPI/setInfo?landing=" + path + mso();

  var d = $.get (query, function (data)
    {
    log ("RESULT: |" + data + "|");
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields[0] == '0')
      {
      landing_pages [path] = {};
      landing_pages [path]['grid'] = [];
      landing_pages [path]['splashed'] = false;

      for (var i = 2; i < lines.length && lines [i] != '--'; i++)
        {
        log ("First block: " + lines [i]);
        var fields = lines[i].split ('\t');
        if (fields [0] == 'name')
          landing_pages [path]['shortname'] = fields [1];
        if (fields [0] == 'imageUrl')
          landing_pages [path]['logo'] = fields [1];
        if (fields [0] == 'intro')
          landing_pages [path]['motto'] = fields [1];
        }

      for (var j = i + 1; j < lines.length && lines [j] != '--'; j++)
        {
        log ("Second block: " + lines [j]);
        var fields = lines[j].split ('\t');
        if (fields [0] == 'name')
          landing_pages [path]['name'] = fields [1];
        if (fields [0] == 'id')
          landing_pages [path]['setid'] = fields [1];
        if (fields [0] == 'imageUrl')
          landing_pages [path]['setlogo'] = fields [1];
        }

      for (var k = j + 1; k < lines.length; k++)
        {
        log ("Third block: " + lines [k]);
        if (lines [k] != '')
          {
          var fields = lines[k].split ('\t');
          landing_pages [path]['grid'][parseInt (fields [0]) - 1] = fields [1];
          }
        }

      zoom_page = path;
      $("#set-title span").html (landing_pages [path]['name']);
      $("#branding-logo").attr ('src', landing_pages [path]['logo']);
      $("#logo-tzuchi").attr ('src', landing_pages [path]['logo']);
      $(".announcing span").html ("Watch " + landing_pages [path]['shortname'] + " on");
      $("#branding-msg span").html ('<b>' + landing_pages [path]['motto'] + '</b>');
      $("#date span").html ("Friday April 15, 2011");
      if (!channels_in_landing_loaded (zoom_page))
        load_then_zoom()
      else
        switch_to_zoom();
      }
    else
      {
      notice_ok (thumbing, translations ['badlanding'], "switch_to_ipg(); finish_activation()");
      }
    });
  }

function jumpstart()
  {
  if (jumpstart_program in programgrid)
    {
    jumpstart_inner();
    return;
    }

  log ('loading programs in jumpstart channel');
  $("#waiting").show();

  var nature = channelgrid [channels_by_id [jumpstart_channel]]['nature'];

  if (nature != '3' && nature != '4' && nature != '5')
    {
    var query = "playerAPI/programInfo?channel=" + jumpstart_channel + mso() + '&' + user_or_ipg();
    var d = $.get (query, function (data)
      {
      parse_program_data (data);
      $("#waiting").hide();

      if (jumpstart_program in programgrid)
        jumpstart_inner();
      else
        notice_ok (thumbing, translations ['expired'], "switch_to_ipg()");
      });
    }
  else
    {
    ipg_cursor = channels_by_id [jumpstart_channel];
    fetch_youtube_or_facebook ('grid', ipg_cursor, "");
    }
  }

function jumpstart_inner()
  {
  var grid = channels_by_id [jumpstart_channel];

  log ('-- js -- jumpstart_channel: ' + jumpstart_channel);
  log ('-- js -- jumpstart_program: ' + jumpstart_program);
  log ('-- js --  at grid location: ' + grid);
  log ('-- js --    programs there: ' + programs_in_channel (grid));
  log ('-- js --   in program grid: ' + (jumpstart_program in programgrid));

  /* Youtubeisms */
  if ((jumpstart_channel + '.' + jumpstart_program) in programgrid)
    {
    jumpstart_program = jumpstart_channel + '.' + jumpstart_program;
    log ('jumpstart program fixed up to: ' + jumpstart_program);
    }

  if (! (jumpstart_program in programgrid))
    {
    notice_ok (thumbing, translations ['expired'], "switch_to_ipg()");
    return;
    }

  set_channel_and_program (grid, jumpstart_program);
  share_landing();

  physical_stop();

  document.onkeydown=kp;
  redraw_ipg();

  $("#blue").hide();

  preload_control_images()
  $("body").focus();

  // play_program();

  if (false)
    hint();

  if (false && sitename == '5f')
    toast();

  jumpstarted_channel = jumpstart_channel;

  jumpstart_channel = '';
  jumpstart_program = '';

  share_landing();
  }

function share_landing()
  {
  var channel = channelgrid [current_channel];

  /* so that ipg_metainfo() won't blank the channel info */
  if (current_channel in channelgrid)
    ipg_cursor = current_channel;

  $("#ep-sharetitle").html (programgrid [current_program]['name']);
  $("#ep-sharedesc").html (programgrid [current_program]['desc']);

  $("#sg-grid, #control-layer, #ear-left, #ear-right, #sg-bubble, #toast, #ad-layer, #fb-bubble, #set-view, #fb-insert, #branding-elements, #add-ch-elements").hide();
  $("#sg-layer, #ch-view, #landing, #sg-elements, #btn-smart-guide, #ep-layer, #follow-elements, #channel-info").show();

  $("#ep-layer").css ("bottom", "0");
  elastic();

  $("#screenshot").attr ("src", programgrid [current_program]['snapshot']);

  $("#btn-follow").unbind();
  $("#btn-follow").click (share_follow);

  $("#win-preview").unbind();
  $("#win-preview").click (share_play);

  $("#btn-smart-guide").unbind();
  $("#btn-smart-guide").click (switch_to_ipg);

  $("#btn-signin, #btn-about, #btn-help").unbind();

  $("#btn-signin")  .removeClass ("on")  .hover (ipg_btn_hover_in, ipg_btn_hover_out); //  .click (sign_in_or_out);
  $("#btn-about")   .removeClass ("on")  .hover (ipg_btn_hover_in, ipg_btn_hover_out); //  .click (ipg_delete_mode);
  $("#btn-help")    .removeClass ("on")  .hover (ipg_btn_hover_in, ipg_btn_hover_out); //  .click (ipg_resume);

  $("#btn-signin")  .bind ('click', sign_in_or_out);
  $("#btn-about")   .bind ('click', about);

  update_ears();

  var previous = previous_channel_square (current_channel);
  var next = next_channel_square (current_channel);

  if (programs_in_channel (previous) > 0 && programs_in_channel (next) > 0)
    share_landing_ears();
  else
    share_landing_continue (previous, next);

  episode_clicks_and_hovers();

  thumbing = 'sharelanding';
  }

function share_landing_ears()
  {
  log ('turning on share ears');
  update_ears();
  $("#ear-left, #ear-right").show();
  $("#ear-left, #ear-right").unbind();
  $("#ear-left").click (ear_left);
  $("#ear-right").click (ear_right);
  }

function share_landing_continue (previous, next)
  {
  var query = "playerAPI/programInfo?channel=" + channelgrid [previous]['id'] + ',' + channelgrid [next]['id'] + mso();
  var d = $.get (query, function (data)
    {
    parse_program_data (data);
    share_landing_ears();
    });
  }

function share_play()
  {
  $("#sg-layer").hide();
  thumbing = 'program';
  $("#control-layer, #ep-layer").show();
  switch_to_control_layer (true);
  toast();
  play_program();
  }

function share_follow()
  {
  log ('share add more');
  subscribe_button();
  return;

  /* this below was too simple, using previous behavior in subscribe_button */
  if (username != 'Guest')
    {
    log ('logged in already, going to IPG');
    switch_to_ipg();
    }
  else
    login_screen();
  }

function toast()
  {
  $("#toast").show();
  $("#btn-yes").unbind();
  $("#btn-yes").click (subscribe_button);
  $("#toast-close").unbind();
  $("#toast-close").click (close_toast);
  setTimeout ("close_toast()", 16000);
  }

function close_toast()
  {
  $("#toast").hide();
  }

function subscribe_button()
  {
  via_shared_ipg = false;
  shared_but_is_now_logged_in = true;

  $("#toast").hide();

  /* pending programInfo queries interfere with login */
  abort_pending_queries();

  if (readonly_ipg)
    {
    readonly_ipg = false;
    log ('SUBSCRIBE BUTTON HIT, with readonly ipg');
    var uu = getcookie ("user");
    if (uu)
      log ('previously stored user cookie is: ' + uu);
    else
      log ('no previously stored user cookie!');
    add_jumpstart_channel = true;
    login();
    }
  else
    {
    log ('SUBSCRIBE BUTTON HIT, a new user');
    login_screen();
    }
  }

function set_channel_and_program (channel, program)
  {
  log ('set channel (' + channel + ') and program (' + program + ')');

  current_channel = channel;

  /* temporarily first program */
  current_program = first_program_in (channel);
  program_cursor = 1;
  program_first = 1;

  enter_channel ('program');

  /* now the real program */
  current_program = program;

  for (var i = 1; i <= n_program_line; i++)
    {
    if (program_line [i] == current_program)
      {
      log ('program found at: ' + i);
      program_cursor = i;
      redraw_program_line();
      return;
      }
    }

  notice_ok (thumbing, translations ['internal'] + ': Code 27', "");
  }

function channels_in_landing_loaded (page)
  {
  if (page in landing_pages)
    {
    for (var i in landing_pages [page]['grid'])
      {
      var id = landing_pages [page]['grid'][i];
      if (id != '0' && ! (id in pool))
        return false;
      }
    }
  return true;
  }

function load_then_zoom()
  {
  log ("LOAD THEN ZOOM");

  switch_to_zoom();
  thumbing = 'zoom-wait';

  $("#waiting").show();

  var channels = [];
  for (var i in landing_pages [zoom_page]['grid'])
    {
    if (landing_pages [zoom_page]['grid'].hasOwnProperty(i))
      {
      var c = landing_pages [zoom_page]['grid'][i];
      if (c != 0)
        channels.push (c);
      }
    }

  var d = $.get ("playerAPI/channelLineup?channel=" + channels + mso(), function (data)
    {
    var lines = data.split ('\n');
    log ('number of zoom channels obtained: ' + (lines.length - 3));

    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('server error: ' + lines [0]);
      return;
      }

    for (var i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');
        pool [fields[1]] = { 'id': fields[1], 'name': fields[2], 'desc': fields[3], 'thumb': fields[4], 'count': fields[5], 'type': fields[6], 'status': fields[7], 'nature': fields[8], 'extra': fields[9] };
        }
      }

    redraw_zoom();
    zoom_metainfo();

    $("#waiting, #ch-view, #add-ch-elements").hide();
    $("#sg-layer, #landing, #set-view, #branding-elements, #branding-logo, #follow-elements").show();
    elastic();

    thumbing = 'zoom';
    });

  /* fire and forget */
  var query = "playerAPI/programInfo?channel=" + channels + mso();
  var d = $.get (query, function (data)
    {
    parse_program_data (data);
    });
  }

function check_bandwidth()
  {
  var i = new Image();
  bw_started = new Date();
  i.onload = check_bandwidth_completed;
  i.src = 'http://9x9ui.s3.amazonaws.com/bandwidth.jpg?start=' + bw_started.getTime();
  $("#bandwidth").html ("Testing...");
  }

function check_bandwidth_completed()
  {
  var bw_ended = new Date();
  bandwidth_measurement = bw_ended.getTime() - bw_started.getTime();
  $("#bandwidth").html (bandwidth_measurement + "ms");
  if (bandwidth_measurement >= 4000)
    nopreload = true;
  }

function preload_control_images()
  {
  var html = '';

  for (var i in { 'bg_controler':'', 'btn_rewind':'', 'btn_pause':'', 'btn_play':'', 'btn_volume':'', 'btn_close':'', 'btn_on':'', 'btn_off':'', 'btn_facebook':'', 'btn_replay':'' })
    html += '<img src="' + root + i + '.png">';

  $("#preload-control-images").html (html);
  }

function best_url (program)
  {
  var desired;

  if (! (program in programgrid))
    {
    log ('program "' + program + '" not in programgrid!');
    return '';
    }

  if (current_tube == 'jw' || current_tube == 'fp')
    desired = '(mp4|m4v|flv)';

  else if (navigator.userAgent.match (/(GoogleTV|Droid Build)/i))
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

function play_first_program_in (chan)
  {
  program_cursor = 1;
  program_first = 1;

  prepare_channel();

  current_program = first_program_in (chan);
  log ('playing first program in ' + chan + ': ' + current_program);

  play();
  }

function play_first_program_in_real_channel (real_channel)
  {
  program_cursor = 1;
  program_first = 1;

  prepare_channel();

  current_program = first_program_in_real_channel  (real_channel);
  log ('playing first program in real channel ' + real_channel + ': ' + current_program);

  play();
  }

function clear_msg_timex()
  {
  if (msg_timex != 0)
    {
    clearTimeout (msg_timex);
    msg_timex = 0;
    }
  if (yt_error_timex != 0)
    {
    clearTimeout (yt_error_timex);
    yt_error_timex = 0;
    }
  $("#msg-layer").hide();
  $("#epend-layer").hide();
  }

function message (text, duration)
  {
  $("#msg-text").html (text);
  $("#msg-layer").show();

  if (duration > 0)
    msg_timex = setTimeout ("empty_channel_timeout()", duration);
  }

function hide_layers()
  {
  $("#ep-layer").hide();
  $("#control-layer, #ear-left, #ear-right, #sg-bubble, #fb-bubble").hide();
  $("#msg-layer").hide();
  $("#epend-layer").hide();
  }

function end_message (duration)
  {
  $("#buffering").hide();

  if (thumbing != 'program' && thumbing != 'control')
    return;

  log ('end!');
  hide_layers();

  thumbing = 'ipg-wait';

  $("#msg-text").html (translations ['eoe']);
  $("#msg-layer").show();
  elastic();

  setTimeout ((we_are_zoom ? "switch_to_zoom()" : "switch_to_ipg()"), 2500);
  stop_all_players();

  return;
  }

function play()
  {
  clear_msg_timex();

  var url = best_url (current_program);

  if (url == '')
    {
    log ('current program ' + current_program + ' has no URL, assuming empty channel, displaying notice for 3 seconds')
    $("#ep-layer, #fb-bubble").hide();
    end_message (10000);
    return;
    }

  log ('Playing ' + current_program + ': ' + programgrid [current_program]['name'] + ' :: ' + url);

  if (thumbing == 'program' || thumbing == 'control')
    report_program();

  physical_start_play (url);
  turn_on_fb_bubble();

  clips_played++;
  }

function start_play_html5 (url)
  {
  current_tube = 'v1';

  var v = document.getElementById ("vvv");
  v.src = url;

  fake_timex = 0;

  $("#buffering").show();

  // v.addEventListener ('loadstart', function() { loadstart_callback(); }, false);
  v.addEventListener ('play', function () { play_callback(); }, false);
  v.addEventListener ('ended', function () { ended_callback(); }, false);
  v.addEventListener ('timeupdate', function () { update_progress_bar(); }, false);
  v.addEventListener ('pause', function () { pause_callback(); }, false);

  v.addEventListener ('error', function () { notify ("error"); }, false);
  v.addEventListener ('stalled', function () { $("#buffering").show(); notify ("stalled"); }, false);
  v.addEventListener ('waiting', function () { $("#buffering").show(); notify ("waiting"); }, false);
  v.addEventListener ('seeking', function () { notify ("seeking"); }, false);
  v.addEventListener ('seeked', function () { notify ("seeked"); }, false);
  v.addEventListener ('suspend', function () { notify ("suspend"); }, false);
  v.addEventListener ('playing', function () { $("#buffering").hide(); notify ("playing"); }, false);
  v.addEventListener ('abort', function () { notify ("abort"); }, false);
  v.addEventListener ('emptied', function () { notify ("emptied"); }, false);

  try { log ('play'); v.play(); } catch (error) { }

  log ('Playing: ' + url);

  update_bubble();
  }


/* html5 video event callbacks */

function notify (text)
  {
  log ('** video event: ' + text);
  }

function loadstart_callback()
  {
  log ('loadstart callback');
  $("#buffering").show();
  }

function play_callback()
  {
  log ('play callback');
  $("#buffering").hide();
  var v = document.getElementById ("vvv");
  // v.addEventListener ('ended', function () { channel_right(); }, false);
  $("#btn-play").hide();
  $("#btn-pause").show();
  }

function ended_callback()
  {
  if (fake_timex)
    {
    log ('** cleared fake timex');
    clearTimeout (fake_timex);
    fake_timex = 0;
    }

  var type = thumbing;
  if (type == 'control') type = control_saved_thumbing;

  if (type == 'program')
    {
    if (we_are_zoom)
      {
      log ('** ended event fired, but we are zoom');
      notice_ok ('zoom', "Returning you to the 3x3!", "switch_to_zoom()");
      }
    else
      {
      log ('** ended event fired, moving program right (cursor at ' + program_cursor + ')');
      program_right();
      }
    }
  else
    log ('** ended event fired, staying put');
  }

function fake_ended_event()
  {
  fake_timex = 0;
  log ('** ended event not fired, but reached end of video');
  channel_right();
  }

function pause_callback()
  {
  log ('** pause event fired');
  $("#btn-pause").hide();
  $("#btn-play").show();
  }

/* end of html5 video event callbacks */


function empty_channel_timeout()
  {
  clear_msg_timex();
  log ('auto-switching from empty channel');
  channel_right();
  }

function play_program()
  {
  current_program = program_line [program_cursor];
  play();
  }

function update_bubble()
  {
  if (current_channel in channelgrid)
    {
    var channel_name = channelgrid [current_channel]['name'];
    if (channel_name.match (/^\s*$/)) { channel_name = '[no channel name]'; }
    $("#ep-layer-ch-title").html (channel_name);
    }
  else if (we_are_zoom)
    {
    if (zoom_channel in pool)
      {
      var channel = pool [zoom_channel];
      $("#ep-layer-ch-title").html (channel ['name']);
      }
    }
  else
    $("#ep-layer-ch-title").html ('');

  if (current_program in programgrid)
    {
    var program = programgrid [current_program];
    $("#ep-layer-ep-title").html (truncated_name (program ['name']));
    $("#ep-age").html (ageof (program ['timestamp'], true));
    $("#ep-length").html (durationof (program ['duration']));
    $("#epNum").html (n_program_line);
    }
  else
    {
    $("#ep-layer-ep-title").html ('');
    $("#ep-age").html ('');
    $("#ep-length").html ('');
    }

  update_ears();
  }

function update_ears()
  {
  var previous;
  var next;

  if (we_are_zoom)
    {
    previous = zoom_channel_left();
    next = zoom_channel_right();
    }
  else
    {
    previous = channelgrid [previous_channel_square (current_channel)];
    next = channelgrid [next_channel_square (current_channel)];
    }

  if (previous)
    {
    $("#ear-left-img").attr ("src", previous ['thumb']);
    $("#ear-left-name").html (previous ['name']);
    }

  if (next)
    {
    $("#ear-right-img").attr ("src", next ['thumb']);
    $("#ear-right-name").html (next ['name']);
    }
  }

function ear_left()
  {
  $("#ear-left, #ear-right").css ("visibility", "hidden");
  setTimeout ("ears_visible()", 5000);

  if (we_are_zoom)
    {
    zoom_cursor = previous_zoom_channel_square (zoom_cursor);
    log ('EAR LEFT. new zoom cursor: ' + zoom_cursor);
    zoom_play();
    }
  else
    {
    $("#sg-layer").hide();
    channel_left();
    }
  }

function ear_right()
  {
  $("#ear-left, #ear-right").css ("visibility", "hidden");
  setTimeout ("ears_visible()", 5000);

  if (we_are_zoom)
    {
    zoom_cursor = next_zoom_channel_square (zoom_cursor);
    log ('EAR RIGHT. new zoom cursor: ' + zoom_cursor);
    zoom_play();
    }
  else
    {
    $("#sg-layer").hide();
    channel_right();
    }
  }

function ears_visible()
  {
  $("#ear-left, #ear-right").css ("visibility", "visible");
  }

function zoom_channel_left()
  {
  var id = cur;

  var cur = linear (zoom_cursor);
  var grid = landing_pages [zoom_page]['grid'];

  if ((cur - 1) in grid)
    id = grid [cur - 1];
  else
    {
    var trry = grid.length - 1;
    while (trry in grid && grid [trry] == '0')
      trry--;
    id = grid [trry];
    }

  return pool [id];
  }

function zoom_channel_right()
  {
  var id = cur;

  var cur = linear (zoom_cursor);
  var grid = landing_pages [zoom_page]['grid'];

  if ((cur + 1) in grid && grid [cur + 1] != '0')
    id = grid [cur + 1];
  else
    id = grid [0];

  return pool [id];
  }

function prepare_channel()
  {
  program_line = [];

  if (we_are_zoom)
    {
    prepare_real_channel (zoom_channel);
    return;
    }

  log ('prepare channel ' + current_channel);

  if (channelgrid.length == 0)
    {
    log_and_alert ('You have no channels');
    return;
    }

  if (current_channel in channelgrid)
    var real_channel = channelgrid [current_channel]['id'];
  else
    {
    log ('[prepare channel] not in channelgrid: ' + current_channel);
    return;
    }

  if (programs_in_channel (current_channel) < 1)
    {
    log ('no programs in channel');
    return;
    }

  prepare_real_channel (real_channel);
  }

function prepare_real_channel (real_channel)
  {
  program_line = [];

  $("#ep-tip").hide();
  $("#ep-container").show();

  n_program_line = 0;

  for (var p in programgrid)
    {
    if (programgrid [p]['channel'] == real_channel)
      program_line [n_program_line++] = p;
    }

  var channel;
  if (real_channel in channels_by_id)
    {
    var pos = channels_by_id [real_channel];
    channel = channelgrid [pos];
    }
  else if (real_channel in pool)
    {
    channel = pool [real_channel];
    }

  if (channel ['nature'] == '5')
    {
    /* reverse order of position */
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [a]['sort']) - Math.floor (programgrid [b]['sort']) });
    program_line.unshift ('');
    }
  else if (channel ['nature'] == '4')
    {
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [b]['sort']) - Math.floor (programgrid [a]['sort']) });
    program_line.unshift ('');
    }
  else
    {
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [b]['timestamp']) - Math.floor (programgrid [a]['timestamp']) });
    program_line.unshift ('');
    }

  $("#ep-layer").show();
  $("#ep-list").html (ep_html());
  $("#ep-list img").error(function () { $(this).unbind("error").attr("src", root + "error.png"); });
  $("#ep-list .clickable").bind ('click', function() { ep_click ($(this).attr('id')); });

  if (thumbing == 'ipg' && ipg_mode == 'episodes')
    $("#ep-list .clickable").hover (ipg_episode_hover_in, ipg_episode_hover_out);

  update_bubble();
  redraw_program_line();
  }

function enter_channel (mode)
  {
  $("#epend-layer").hide();
  prepare_channel();
  redraw_program_line();
  $("#ep-meta").hide();
  $(".ep-list .age").show();
  $("#ep-layer").show();
  thumbing = mode;
  reset_osd_timex();
  }

function ep_html()
  {
  var html = '';
  var now = new Date();

  var bad_thumbnail = root + 'error.png';

  for (var i = program_first; i <= n_program_line && i < program_first + max_programs_in_line; i++)
    {
    if (i in program_line)
      {
      var program = programgrid [program_line [i]];

      var age = ageof (program ['timestamp'], false);
      var duration = durationof (program ['duration']);

      var classes = (i == program_cursor) ? 'on clickable' : 'clickable';
      var bg_ep = (i == program_cursor) ? 'bg_ep_off.png' : 'bg_ep_off.png'

      var thumbnail = program ['thumb']
      if (thumbnail == '' || thumbnail == 'null' || thumbnail == 'false')
        thumbnail = bad_thumbnail;

      html += '<li class="' + classes + '" id="p-li-' + i + '"><img src="' + root + 'bg_ep_off.png" class="ep-off" id="ep-off-' + i + '"><img src="' + root + 'bg_ep_on.png" class="ep-on" id="ep-on-' + i + '"><img src="' + thumbnail + '" class="thumbnail"><p class="age"><span>' + age + '</span></p><p class="duration"><span>' + duration + '</span></p></li>'
      }
    }

  return html;
  }

function durationof (duration)
  {
  if (duration == -1)
    return '';

  if (duration == '' || duration == 'null' || duration == undefined || duration == Infinity)
    return '0:00';

  if (duration.match (/^00:\d\d:\d\d/))
    duration = duration.replace (/^00:/, '');

  if (duration.match (/\.\d\d$/))
    duration = duration.replace (/\.\d\d$/, '');

  return duration;
  }

function ageof (timestamp, flag)
  {
  var age = '';
  var now = new Date();
  var ago_or_hence = translations ['ago'];

  if (timestamp != '')
    {
    var d = new Date (Math.floor (timestamp));

    var minutes = Math.floor ((now.getTime() - d.getTime()) / 60 / 1000);
    ago_or_hence = minutes < 0 ? translations ['hence'] : translations ['ago'];
    minutes = Math.abs (minutes);

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

  panic ("No next channel! (for " + channel + ")")
  }

function next_zoom_channel_square (cursor)
  {
  for (var i = parseInt (cursor) + 1; i <= 33; i++)
    {
    var index = linear (i);
    if (index >= 0)
      {
      var channel_id = landing_pages [zoom_page]['grid'][index];
      if (channel_id in pool)
        return i;
      }
    }

  for (var i = 11; i <= parseInt (cursor); i++)
    {
    var index = linear (i);
    if (index >= 0)
      {
      var channel_id = landing_pages [zoom_page]['grid'][index];
      if (channel_id in pool)
        return i;
      }
    }

  return -1;
  }

function next_free_square (channel)
  {
  for (var i = parseInt (channel) + 1; i <= 99; i++)
    {
    if (((i % 10) != 0) && ! (i in channelgrid))
      return i;
    }

  for (var i = 11; i <= parseInt (channel); i++)
    {
    if (((i % 10) != 0) && ! (i in channelgrid))
      return i;
    }

  return 0;
  }

function up_channel_square (channel)
  {
  var column = parseInt (channel) % 10;

  for (var row = Math.floor (parseInt (channel) / 10) - 1; row >= 1; row--)
    {
    for (var c = 0; c <= 8; c++)
      {
      var cursor = row + '' + (column + c);
      if (cursor in channelgrid)
        return cursor;

      var cursor = row + '' + (column - c);
      if (cursor in channelgrid)
        return cursor;
      }
    }

  return -1;
  }

function down_channel_square (channel)
  {
  var column = parseInt (channel) % 10;

  for (var row = Math.floor (parseInt (channel) / 10) + 1; row <= 9; row++)
    {
    for (var c = 0; c <= 8; c++)
      {
      var cursor = row + '' + (column + c);
      if (cursor in channelgrid)
        return cursor;

      var cursor = row + '' + (column - c);
      if (cursor in channelgrid)
        return cursor;
      }
    }

  return -1;
  }

function programs_in_channel (channel)
  {
  if (channel in channelgrid)
    {
    var real_channel = channelgrid [channel]['id'];
    return programs_in_real_channel (real_channel);
    }
  return 0;
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

  panic ("No previous channel!")
  }

function previous_zoom_channel_square (cursor)
  {
  for (var i = parseInt (cursor) - 1; i > 10; i--)
    {
    var index = linear (i);
    if (index >= 0)
      {
      var channel_id = landing_pages [zoom_page]['grid'][index];
      if (channel_id in pool)
        return i;
      }
    }

  for (var i = 33; i >= parseInt (cursor); i--)
    {
    var index = linear (i);
    if (index >= 0)
      {
      var channel_id = landing_pages [zoom_page]['grid'][index];
      if (channel_id in pool)
        return i;
      }
    }

  return -1;
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
  }

function first_empty_channel()
  {
  for (var y = 1; y <= 9; y++)
    for (var x = 1; x <= 9; x++)
      {
      if (! (("" + y + "" + x) in channelgrid))
        return "" + y + "" + x;
      }
  log ("no empty channels!");
  }

function first_program_in (channel)
  {
  if (! (channel in channelgrid))
    {
    log ('channel ' + channel + ' not in channelgrid');
    return 0;
    }

  var real_channel = channelgrid [channel]['id'];
  return first_program_in_real_channel (real_channel);
  }

function first_program_in_real_channel (real_channel)
  {
  foo(); /* god, why */

  var programs = [];
  var n_programs = 0;

  for (p in programgrid)
    {
    if (programgrid [p]['channel'] == real_channel)
      programs [n_programs++] = p;
    }

  if (programs.length < 1)
    {
    log ('No programs in channel: ' + real_channel);
    return 0;
    }

  var channel;
  if (real_channel in channels_by_id)
    {
    var pos = channels_by_id [real_channel];
    channel = channelgrid [pos];
    }
  else if (real_channel in pool)
    {
    channel = pool [real_channel];
    }

  // unshift here is to match what is in program_line

  if (channel ['nature'] == '5')
    {
    /* reverse order of position */
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [a]['sort']) - Math.floor (programgrid [b]['sort']) });
    programs.unshift ('');
    }
  else if (channel ['nature'] == '4')
    {
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [b]['sort']) - Math.floor (programgrid [a]['sort']) });
    programs.unshift ('');
    }
  else
    {
    programs = programs.sort (function (a,b) { return Math.floor (programgrid [b]['timestamp']) - Math.floor (programgrid [a]['timestamp']) });
    programs.unshift ('');
    }

  return programs [1];
  }

function escape()
  {
  var layer;

  log ('escape!');
  $("#log-layer").hide();

  if (thumbing == 'browse-wait' || thumbing == 'ipg-wait')
    return;

  if (modal_box)
    {
    close_box ('');
    return;
    }

  if (thumbing == 'browse')
    {
    log ('browse return to ipg');
    $("#ch-directory").hide();
    thumbing = 'ipg';
    ipg_sync();
    return;
    }

  if (thumbing == 'ipg')
    {
    if (ipg_mode == 'episodes')
      {
      ipg_exit_episode_mode();
      return;
      }

    if (ipg_mode == 'edit')
      {
      ipg_exit_delete_mode();
      return;
      }

    if (! (ipg_cursor in channelgrid))
      {
      log ('not on a channel');
      return;
      }

    try
      {
      clearTimeout (ipg_timex);
      clearTimeout (ipg_delayed_stop_timex);
      }
    catch (error)
      {
      }

    $("#sg-layer, #ch-directory").hide();

    ipg_play();
    return;
    }

  if (thumbing == 'confirm')
    {
    notice_completed();
    return;
    }

  if (thumbing == 'yes-or-no')
    {
    yn_enter (2);
    return;
    }

  if (thumbing == 'delete')
    {
    if (delete_mode == 'step1')
      {
      $("#delete-layer, #mask").hide();
      thumbing = 'ipg';
      }
    else if (delete_mode == 'step2')
      ipg_exit_delete_mode();
    return;
    }

  if (thumbing == 'user')
    {
    $("#body").focus();
    $("#signin-layer").hide();
    if ($("#ch-view").css ('display') == 'block')
      thumbing = 'sharelanding';
    else
      switch_to_ipg();
    return;
    }

  if (thumbing == 'zoom')
    {
    return;
    }

  if (thumbing == 'control' || thumbing == 'program')
    {
    exit_control_layer();
    return;
    }

  switch (thumbing)
    {
    case 'program': layer = $("#ep-layer");
                    break;

    case 'channel': return;

    case 'ipg':     layer = $("#sg-layer");
                    break;

    case 'user':    layer = $("#signin-layer");
                    break;

    case 'browse':  layer = $("#ch-directory");
                    break;

    case 'control': layer = $("#control-layer");
                    break;

    case 'end':     return;
    }

  layer.css ("display", layer.css ("display") == "block" ? "none" : "block");

  if (thumbing == 'ipg')
    {
    try
      {
      clearTimeout (ipg_timex);
      clearTimeout (ipg_delayed_stop_timex);
      }
    catch (error)
      {
      }
    }

  if (thumbing == 'ipg' || thumbing == 'user')
    { /* resume(); */ }

  if (thumbing == 'user' || thumbing == 'browse')
    {
    thumbing = saved_thumbing;
    if (thumbing == 'control')
      $("#control-layer, #ear-left, #ear-right").show();
    }

  else if (thumbing == 'ipg' || thumbing == 'control')
    {
    thumbing = 'program';
    prepare_channel();
    }

  $("#mask").hide();
  $("#msg-layer").hide();
  $("#epend-layer").hide();

  if (thumbing == 'program')
    {
    if (layer.css ("display") == "none")
      clear_osd_timex();
    }
  }

function notice_completed()
  {
  log ('confirm esc, setting: ' + after_confirm);
  $("#confirm-layer").hide();
  thumbing = after_confirm;
  if (after_confirm_function)
    eval (after_confirm_function);
  }

function mousemove()
  {
  if (thumbing == 'program')
    {
    switch_to_control_layer (false);
    report ('e', 'control-bar');
    }
  }

function mouseaway()
  {
  if (thumbing == 'program' || thumbing == 'control')
    exit_control_layer();
  }

function kp (e)
  {
  var ev = e || window.event;
  log ('[' + thumbing + '] ' + ev.type + " keycode=" + ev.keyCode);

  keypress (ev.keyCode);
  }

function keypress (keycode)
  {
  if (!jingled)
   return;

  /* if in rss field entry and down key, exit field */

  if (document.activeElement.id == 'podcastRSS' && keycode == 40)
    document.getElementById ('podcastRSS').blur();

  /* entering a form */
  if (thumbing == 'user' && keycode != 27 && keycode != 37 && keycode != 38 && keycode != 39 && keycode != 40)
    return;

  /* special case, channel browser + navigation key */
  if (thumbing == 'browse' && keycode != 27 && keycode != 37 && keycode != 38 && keycode != 39 && keycode != 40 && keycode != 13 && keycode != 33 && keycode != 34)
    return;

  if (thumbing == 'ipg')
    extend_ipg_timex();

  report ('k', keycode);

  if (modal_box)
    {
    if (keycode == 13 || keycode == 121 || keycode == 27)
      close_box ('');
    return;
    }

  /* ensure osd is up */

  if (keycode == 37 || keycode == 39 || keycode == 38 || keycode == 40)
    {
    if (thumbing == 'program')
      {
      if ($("#ep-layer").css ('display') == 'none')
        {
        log ('program osd was off');
        extend_ep_layer();
        return;
        }
      else
        reset_osd_timex();
      }
    }

  switch (keycode)
    {
    case 27:
      /* esc */
      if (thumbing == 'confirm')
        notice_completed();
      else if (thumbing == 'ipg' && (ipg_mode == 'episodes' || ipg_mode == 'edit'))
        escape();
      else if (thumbing == 'ipg' && clips_played == 0)
        {
        /* do nothing */
        }
      else
        escape();
      break;

    case 32:
      /* space */
    case 178:
      /* google TV play/pause */
      if (thumbing == 'program' || thumbing == 'control')
        pause();
      break;

    case 13:
      /* enter */
    case 121:
      /* F10, remote control "OK" */
      if (thumbing == 'ipg')
        ipg_play();
      else if (thumbing == 'zoom')
        zoom_enter();
      else if (thumbing == 'browse')
        browse_enter();
      else if (thumbing == 'program')
        switch_to_control_layer (false);
      else if (thumbing == 'control')
        control_enter();
      else if (thumbing == 'confirm')
        escape();
      else if (thumbing == 'delete')
        delete_enter (delete_cursor);
      else if (thumbing == 'yes-or-no')
        yn_enter (yn_cursor);
      break;

    case 37:
      /* left arrow */
      if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        channel_left();
        enter_channel ('program');
        }
      else if (thumbing == 'program')
        program_left();
      else if (thumbing == 'ipg')
        ipg_left();
      else if (thumbing == 'zoom')
        zoom_left();
      else if (thumbing == 'control')
        control_left();
      else if (thumbing == 'browse')
        browse_left();
      else if (thumbing == 'user')
        user_left();
      else if (thumbing == 'delete')
        delete_left();
      else if (thumbing == 'yes-or-no')
        yn_left();
      break;

    case 39:
      /* right arrow */
      if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        channel_right();
        enter_channel ('program');
        }
      else if (thumbing == 'program')
        program_right();
      else if (thumbing == 'ipg')
        ipg_right();
      else if (thumbing == 'zoom')
        zoom_right();
      else if (thumbing == 'control')
        control_right();
      else if (thumbing == 'browse')
        browse_right();
      else if (thumbing == 'user')
        user_right();
      else if (thumbing == 'delete')
        delete_right();
      else if (thumbing == 'yes-or-no')
        yn_right();
      break;

    case 38:
      /* up arrow */
      if (thumbing == 'program')
        {
        if (we_are_zoom)
          switch_to_zoom();
        else
          switch_to_ipg();
        }
      else if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        switch_to_ipg();
        }
      else if (thumbing == 'control')
        control_up();
      else if (thumbing == 'browse')
        browse_up();
      else if (thumbing == 'ipg')
        ipg_up();
      else if (thumbing == 'zoom')
        zoom_up();
      else if (thumbing == 'user')
        user_up()
      break;

    case 40:
      /* down arrow */
      if (thumbing == 'control' || thumbing == 'program')
        control_down();
      else if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        enter_channel ('program');
        thumbing = 'program';
        }
      else if (thumbing == 'browse')
        browse_down();
      else if (thumbing == 'ipg')
        ipg_down();
      else if (thumbing == 'zoom')
        zoom_down();
      else if (thumbing == 'user')
        user_down()
      break;

    case 33:
      /* PgUp */
      /* remote control channel up */
      if (thumbing == 'browse')
        browse_page_up();
      else if (thumbing == 'program' || thumbing == 'control')
        ear_right();
      break;

    case 34:
      /* PgDn */
      /* remote control channel down */
      if (thumbing == 'browse')
        browse_page_down();
      else if (thumbing == 'program' || thumbing == 'control')
        ear_left();
      break;

    case 45:
      /* Ins */
      break;

    case 8:
      /* Backspace */
    case 68:
      /* D */
    case 46:
      /* Del */
      if (thumbing == 'ipg')
        delete_yn();
      break;

    case 82:
      /* R */
      if (thumbing == 'ipg')
        {
        redraw_ipg();
        elastic();
        }
      else if (thumbing == 'program')
        prepare_channel();
      break;

    case 49:
    case 50:
    case 51:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
      /* 1, 2, 3... */
      break;

    case 71:
      /* G */
      break;

    case 79:
      /* O */
      if (divlog)
        $("#log-layer").show();
      break;

    case 66:
      /* B */
      break;

    case 67:
      /* C */
      break;

    case 73:
      /* I */
      switch_to_ipg();
      break;

    case 85:
      /* U */
      break;

    case 87:
      /* W */
      break;

    case 88:
      /* X */
      physical_stop();
      break;
    }
  }

function dump_configuration_to_log()
  {
  log ('PROGRAMS');
  for (var p in programgrid)
    {
    var program = programgrid [p];
    log ('#' + p + ' ch:' + program ['channel'] + ' grid:' + channels_by_id [program ['channel']] + ' ' + program ['name'] + ' time:' + program ['timestamp'] + ' url: ' + best_url (p))
    }
  }

function clear_osd_timex()
  {
  if (osd_timex != 0)
    {
    clearTimeout (osd_timex);
    osd_timex = 0;
    }
  }

function reset_osd_timex()
  {
  clear_osd_timex();
  osd_timex = setTimeout ("osd_timex_expired()", 6000);
  }

function osd_timex_expired()
  {
  osd_timex = 0;
  log ('osd timex expired');
  if (thumbing != 'ipg' && thumbing != 'sharelanding')
    $("#ep-layer, #control-layer, #ear-left, #ear-right, #sg-bubble").hide();
  if (thumbing == 'control')
    thumbing = 'program';
  }

function extend_ep_layer()
  {
  // $("#ep-layer").show();
  switch_to_control_layer (true);
  elastic();
  reset_osd_timex();
  }

function delayed_video_stop()
  {
  log ('delayed video stop: ' + thumbing);
  if ((thumbing == 'ipg' || thumbing == 'browse' || thumbing == 'browse-wait') && current_tube == 'fp')
    {
    try { log ('flowplayer state: ' + flowplayer(fp_player).getState()); } catch (error) {};
    physical_stop();
    }
  }

function add_more_channels()
  {
  if (ipg_cursor in channelgrid)
    {
    var fec = first_empty_channel();
    if (fec)
      {
      cursor_off (ipg_cursor);
      ipg_cursor = fec;
      cursor_on (ipg_cursor);
      }
    else
      {
      notice_ok (thumbing, "No place for a new channel!", "");
      return;
      }
    }
  browse();
  }

function foo()
  {
  $("#sg-holder").css ("left", ($(window).width() - $("#sg-holder").width()) / 2);
  $("#sg-holder").css ("margin-top", ($(window).height() - $("#sg-holder").height()) / 2);
  }

function switch_to_ipg()
  {
  log ('ipg');
  report ('e', 'ipg');

  clear_msg_timex();
  clear_osd_timex()

  // setInterval ("foo()", 200);

  physical_stop();
  ipg_delayed_stop_timex = setTimeout ("delayed_video_stop()", 5000);

  we_are_zoom = false;

  if (current_channel in channelgrid)
    ipg_cursor = current_channel;

  if (! (ipg_cursor in channelgrid))
    ipg_cursor = first_channel();

  if (! (ipg_cursor in channelgrid))
    ipg_cursor = '11';

  ipg_entry_channel = ipg_cursor;
  if (program_cursor in program_line)
    ipg_entry_program = program_line [program_cursor];
  else
    ipg_entry_program = '';

  redraw_ipg();

  $("#btn-signin, #btn-about, #btn-help").unbind();

  $("#btn-signin")  .removeClass ("on")  .hover (ipg_btn_hover_in, ipg_btn_hover_out); //  .click (sign_in_or_out);
  $("#btn-about")   .removeClass ("on")  .hover (ipg_btn_hover_in, ipg_btn_hover_out); //  .click (ipg_delete_mode);
  $("#btn-help")    .removeClass ("on")  .hover (ipg_btn_hover_in, ipg_btn_hover_out); //  .click (ipg_resume);

  $("#btn-signin")  .bind ('click', sign_in_or_out);
  // $("#btn-resume")  .bind ('click', ipg_resume);
  $("#btn-about")   .bind ('click', about);

  $("#btn-edit").show();
  $("#btn-edit").unbind();
  $("#btn-edit").click (ipg_delete_mode);

  $("#btn-add-channels").unbind();
  $("#btn-add-channels").click (add_more_channels);

  stop_preload();
  $("#buffering").hide();

  $("#control-layer, #ear-left, #ear-right, #sg-bubble, #toast, #ad-layer, #fb-bubble").hide();
  $("#ch-directory").hide();

  $("#ep-layer").css ("bottom", "0");

  $("#landing, #branding-elements, #follow-elements").hide();
  // foo();
  $("#sg-layer, #sg-grid, #sg-elements, #add-ch-elements, #btn-smart-guide").show().css("display","block");

  // $("#sg-grid").removeClass("x3").addClass("x9").css("display","block");

  foo();
  log ("IPG ELEMENTALS");
  log ("ipg is " + $("#sg-layer").css ("display"));

  elastic();
  extend_ipg_timex();
  thumbing = 'ipg';
  start_preload_timer();
  ipg_sync();

  // if (first_time_user == 1 || sitename == '5f')
  //   hint();

  $("#ep-list .clickable").removeClass ("on");
  }

function switch_to_zoom()
  {
  log ('zoom');
  report ('e', 'zoom');

  clear_msg_timex();
  clear_osd_timex()

  physical_stop();
  ipg_delayed_stop_timex = setTimeout ("delayed_video_stop()", 5000);

  zoom_cursor = '11';

  if (!landing_pages [zoom_page]['splashed'])
    {
    $("#branding-temp").show();
    branding_timex = setTimeout ("hide_branding_temp()", 15000);
    $("#btn-watch").unbind();
    $("#btn-watch").click (hide_branding_temp);
    landing_pages [zoom_page]['splashed'] = true;
    }

  redraw_zoom();
  zoom_cursor_on (zoom_cursor);
  zoom_metainfo();

  $("#btn-signin, #btn-about, #btn-help").unbind();
  $("#btn-signin, #btn-about, #btn-help").removeClass ("on").hover (ipg_btn_hover_in, ipg_btn_hover_out);

  $("#btn-signin")  .bind ('click', sign_in_or_out);
  // $("#btn-edit")    .bind ('click', ipg_delete_mode);
  // $("#btn-resume")  .bind ('click', ipg_resume);
  $("#btn-about")   .bind ('click', about);

  stop_preload();
  $("#buffering").hide();

  $("#control-layer, #ear-left, #ear-right, #sg-bubble, #toast, #ipg-layer, #ep-layer, #sg-elements, #sg-grid, #fb-bubble, #btn-edit, #ch-view, #add-ch-elements").hide();
log ("SG GRID HIDE ***************************");
  $("#ch-directory").hide();

  $("#sg-layer, #branding-elements, #branding-logo, #follow-elements, #sg-elements, #btn-smart-guide, #landing, #set-view").show();

  $("#btn-smart-guide").unbind();
  $("#btn-smart-guide").click (switch_to_ipg);

  elastic();
  extend_ipg_timex();
  thumbing = 'zoom';

  $("#btn-follow").unbind();
  $("#btn-follow").click (follow_these);

  $("#ep-layer").css ("bottom", "0");
  }

function hide_branding_temp()
  {
  $("#branding-temp").hide();
  clearTimeout (branding_timex);
  }

function follow_these()
  {
  log ("FOLLOW THESE");

  var channels = [];
  var grid_cluster = [];
  var empties = first_empty_cluster();

  if (empties.length == 0)
    {
    notice_ok (thumbing, "No place for a new 3x3!", "");
    return;
    }

  log ('empties: ' + empties);
  var cluster = top_left_to_server (empties[0]);
  var set_id = landing_pages [zoom_page]['setid'];

  $("#waiting").show();
  thumbing = 'zoom-wait';

  var cmd = "playerAPI/subscribe?user=" + user + mso() + '&' + "pos=" + cluster + '&' + "set=" + set_id;
  var d = $.get (cmd, function (data)
    {
    $("#waiting").hide();
    thumbing = 'zoom';

    log ('subscribe raw result: ' + data);
    var fields = data.split ('\t');
    if (fields [0] == '0')
      {
      fetch_channels();
      $(".success-holder").css ("margin-top",  ($(window).height()- $(".success-holder").height()) / 2);
      $("#success-data").html (channels.length + ' ' + landing_pages [zoom_page]['shortname']);
      $("#success-goto").unbind();
      $("#success-goto").click (follow_these_ok);
      $("#success-layer").show();
      $(".success-holder").css ("margin-top",  ($(window).height()- $(".success-holder").height()) / 2);
      // notice_ok (thumbing, "Subscription successful!", "switch_to_ipg()");
      set_titles [empties[0]] = landing_pages [zoom_page]['name'];
      }
    else
      notice_ok (thumbing, translations ['suberr'] + ': ' + fields [1], "");
    });
  }

function OLD_follow_these()
  {
  log ("FOLLOW THESE");

  var channels = [];
  var grid_cluster = [];
  var empties = first_empty_cluster();

  if (empties.length == 0)
    {
    notice_ok (thumbing, "No place for a new 3x3!", "");
    return;
    }

  for (var i in landing_pages [zoom_page]['grid'])
    {
    if (landing_pages [zoom_page]['grid'].hasOwnProperty(i))
      {
      var c = landing_pages [zoom_page]['grid'][i];
      if (c != '0')
        {
        if (c in channels_by_id)
          {
          log (c + " is already subscribed!");
          notice_ok (thumbing, "You already have this set in your Smart Guide!", "switch_to_ipg()");
          return;
          }
        else
          {
          channels.push (c);
          grid_cluster.push (server_grid (empties [i]));
          }
        }
      }
    }

  $("#waiting").show();
  thumbing = 'zoom-wait';

  var cmd = "playerAPI/subscribe?user=" + user + mso() + '&' + "channel=" + channels + '&' + "grid=" + grid_cluster;
  var d = $.get (cmd, function (data)
    {
    $("#waiting").hide();
    thumbing = 'zoom';

    log ('subscribe raw result: ' + data);
    var fields = data.split ('\t');
    if (fields [0] == '0')
      {
      fetch_channels();
      $(".success-holder").css ("margin-top",  ($(window).height()- $(".success-holder").height()) / 2);
      $("#success-data").html (channels.length + ' ' + landing_pages [zoom_page]['shortname']);
      $("#success-goto").unbind();
      $("#success-goto").click (follow_these_ok);
      $("#success-layer").show();
      $(".success-holder").css ("margin-top",  ($(window).height()- $(".success-holder").height()) / 2);
      // notice_ok (thumbing, "Subscription successful!", "switch_to_ipg()");
      set_titles [empties[0]] = landing_pages [zoom_page]['name'];
      }
    else
      notice_ok (thumbing, translations ['suberr'] + ': ' + fields [1], "");
    });
  }

function follow_these_ok()
  {
  $("#success-layer").hide();
  switch_to_ipg();
  }

function check_for_empty_clusters()
  {
  log ('checking for empty clusters');
  for (var i in top_lefts)
    {
    var corner = top_lefts [i];
    var pos = top_left_to_server (corner);

    if (cluster_is_empty (corner) && (corner in set_titles) && set_titles [corner] != 'Set ' + pos)
      {
      log ('blanking out title of cluster #' + pos);

      set_titles [corner] = "Set " + pos;
      redraw_ipg();

      var d = $.get ("playerAPI/setSetInfo?user=" + user + '&' + 'pos=' + pos + '&' + 'name=Set%20' + pos + mso(), function (data)
        {
        /* ignore result */
        });
      }
    }
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

function first_empty_cluster()
  {
  var cluster;

  for (var i in top_lefts)
    {
    cluster = [];

    var ty = Math.floor (top_lefts[i] / 10);
    var tx = top_lefts[i] % 10;

    for (y = ty; y < ty + 3; y++)
      for (x = tx; x < tx + 3; x++)
         {
         if (! ((y * 10 + x) in channelgrid))
           cluster.push (y * 10 + x);
         }

    if (cluster.length == 9)
      return cluster;
    }

  return [];
  }

function top_left_to_server (pos)
  {
  for (var i in top_lefts)
    {
    if (pos == top_lefts [i])
      return parseInt (i)+1;
    }
  return -1;
  }

function about()
  {
  modal_box = true;
  $("#about-layer").show();
  $("#btn-closeAbout").unbind();
  $("#btn-closeAbout").hover (hover_in, hover_out);
  $("#btn-closeAbout").click (close_about);
  elastic();
  }

function close_about()
  {
  close_box ('#about-layer');
  }

function help()
  {
  hint();
  }

function hint()
  {
  if (!hint_seen)
    {
    modal_box = true;
    hint_seen = true;
    $("#hint-layer").show();
    $("#btn-closeHint").addClass ("on");
    modal_box = true;
    elastic();
    first_time_user = 2;
    }
  }

function close_hint()
  {
  close_box ('#hint-layer');
  }

function close_box (box)
  {
  var n_open = 0;
  var n_closed = 0;

  var closables = { '#hint-layer':0, '#about-layer':0 };

  log ('close box: "' + box + '"');

  if (box == '')
    {
    /* close only the topmost closable */
    for (var v in closables)
      {
      if (n_closed == 0 && $(v).css ("display") == 'block')
        {
        $(v).hide();
        n_closed++;
        }
      }
    }
  else if ($(box).css ("display") == 'block')
    {
    $(box).hide();
    n_closed++;
    }

  /* how many closables remain open */
  for (var v in closables)
    {
    if ($(v).css ("display") == 'block')
      n_open++;
    }

  modal_box = (n_open > 0);
  }

function ipg_idle()
  {
  ipg_timex = 0;
  // if (thumbing == 'ipg') switch_to_whats_new();
  }

function extend_ipg_timex()
  {
  if (ipg_timex)
    clearTimeout (ipg_timex);
  ipg_timex = setTimeout ("ipg_idle()", 65000);
  }

function redraw_ipg()
  {
  var html = "";
  
  var bad_thumbnail = '<img src="' + root + 'error.png" class="thumbnail">';
  var add_channel = (language == 'zh' || language == 'zh-tw') ? 'add_channel_cn.png' : 'add_channel.png';

  for (var i in top_lefts)
    {
    if (!top_lefts.hasOwnProperty (i))
      continue;

    var ty = Math.floor (top_lefts[i] / 10);
    var tx = top_lefts[i] % 10;

    var title = translations ['setnum'];
    title = title.replace ('%d', parseInt (i) + 1);
    if (top_lefts [i] in set_titles)
      title = set_titles [top_lefts [i]];

    html += '<div id="box-' + top_lefts[i] + '">';
    html += '<img src="' + root + 'bg_set.png" class="bg-set">';
    html += '<div id="title-click-' + top_lefts[i] + '" class="title-click"><p class="set-title" id="title-' + top_lefts[i] + '"><span>' + title + '</span></p>';
    html += '<p class="zoom"><img src="' + root + 'icon_zoomin.png" class="icon-zoomin"><img src="' + root + 'icon_zoomout.png" class="icon-zoomout"></p></div>';
    html += '<ul>';

    // <li class="on"><img src="thumb/01.jpg" class="thumbnail"><img src="images/icon_move.png" class="icon-move"><p class="btn-del-ch"><img src="images/btn_delete_off.png" class="off"><img src="images/btn_delete_on.png" class="on"></p><img src="images/icon_play.png" class="btn-preview"></li>^M

    var mov = '<img src="' + root + 'icon_move.png" class="icon-move">';
    var deloff = '<p class="btn-del-ch"><img src="' + root + 'btn_delete_off.png" class="off">';
    var delon = '<img src="' + root + 'btn_delete_on.png" class="on"></p>';
    var play = '<img src="' + root + 'icon_play.png" class="btn-preview">';

    var stuff = mov + deloff + delon + play;

    for (y = ty; y < ty + 3; y++)
      for (x = tx; x < tx + 3; x++)
         {
         var yx = y * 10 + x;
         if (yx in channelgrid)
           {
           var channel = channelgrid [yx];
           var thumb = channel ['thumb'];
           if (thumb == '' || thumb == 'null' || thumb == 'false')
             thumb = root + 'error.png';

           var hasnew = channel ['updated'] > lastlogin; // channel ['new'] contains new count
           html += '<li class="clickable draggable" id="ipg-' + yx + '"><img src="' + thumb + '" class="thumbnail">' + stuff + '</li>';
           }
         else
           html += '<li class="clickable droppable" id="ipg-' + yx + '"><img src="' + root + add_channel + '" class="add-ch"></li>';
         }

    html += '</ul>';
    html += '</div>';
    }

  $("#slider").html (html);
  foo();

  $("#slider .clickable img").error(function () { $(this).unbind("error").attr("src", root + "error.png"); });
  $("#slider .clickable").unbind();
  $("#slider .clickable").bind ('click', function () { ipg_click ($(this).attr ('id')); });
  $("#slider .clickable").hover (hover_in, hover_out);
  $("#slider .title-click").bind ('click', function() { title_click ($(this).attr ('id')); });

  if (ipg_mode == 'edit')
    setup_draggables();

  if (ipg_cursor > 0)
    cursor_on (ipg_cursor);

  ipg_metainfo();

  ipg_fixup_margin();
  ipg_fixup_middle();

  if (thumbing == 'zoom')
    redraw_zoom(); /* FIX */
  }

function setup_draggables()
  {
  $(function()
    {
    remove_draggables();
    $("#slider .draggable").draggable({ zIndex: 9999, opacity: 1 });
    $("#slider .droppable").droppable
         ({
             revert: "invalid",
             activate: function() { if (!dragging) { dragging = true; log ('dragstart'); } },
             deactivate: function(event,ui) { setTimeout ('drag_cleanup("' + ui.draggable.attr('id') + '")', 200); },
             accept: ".draggable",
             activeClass: "ui-state-hover",
             hoverClass: "ui-state-active",
             drop: function (event, ui)
                      { setTimeout ('move_channel ("' + ui.draggable.attr('id') + '", "' + $(this).attr('id') + '")', 20); }
         });
    });
  }

function remove_draggables()
  {
  $("#slider .draggable").draggable ("destroy");
  $("#slider .droppable").droppable ("destroy");
  }

function OLD_redraw_ipg()
  {
  for (var y = 1; y <= 9; y++)
    {
    html += '<ul class="ipg-list" id="row-' + y + '">';

    for (var x = 1; x <= 9; x++)
      {
      var yx = y * 10 + x;
      if (yx in channelgrid)
        {
        var channel = channelgrid [yx]

        var thumb = channel ['thumb'];

        var newbox = '';

        if (('new' in channel) && channel ['new'] > 0)
          newbox = '<div class="reddot"><p class="newNum"><span>' + channel ['new'] + '</span></p><img src="' + root + 'icon_reddot_off.png">';

        if (thumb == '' || thumb == 'null' || thumb == 'false')
          html += '<li class="clickable draggable" id="ipg-' + yx + '">' + bad_thumbnail + '</li>';
        else
          html += '<li class="clickable draggable" id="ipg-' + yx + '"><img src="' + channelgrid [yx]['thumb'] + '" class="thumbnail">' + newbox + '</li>';
        }
      else
        html += '<li class="clickable droppable" id="ipg-' + yx + '"><img src="' + root + add_channel + '" class="add-ch"></li>';
      }

    html += '</ul>';
    }

  $("#list-holder").html (html);
  $("#list-holder img").error(function () { $(this).unbind("error").attr("src", root + "error.png"); });
  $("#list-holder .clickable").bind ('click', function () { ipg_click ($(this).attr ('id')); });
  $("#list-holder .clickable").hover (hover_in, hover_out);

  $(function()
    {
    $( "#list-holder .draggable" ).draggable({ zIndex: 9999, opacity: 1 });
    $( "#list-holder .droppable" ).droppable
         ({
             revert: "invalid",
             activate: function() { if (!dragging) { dragging = true; log ('dragstart'); } },
             deactivate: function() { setTimeout ("drag_cleanup()", 200); },
             accept: ".draggable",
             activeClass: "ui-state-hover",
             hoverClass: "ui-state-active",
             drop: function (event, ui)
                      { setTimeout ('move_channel ("' + ui.draggable.attr('id') + '", "' + $(this).attr('id') + '")', 20); }
         });
    });

  if (ipg_cursor > 0)
    cursor_on (ipg_cursor);

  ipg_metainfo();

  ipg_fixup_margin();
  ipg_fixup_middle();

  redraw_zoom(); /* FIX */
  }

function redraw_zoom()
  {
  var html = "";
  var temp_html = "";

  var temp_count = 0;
 
  var bad_thumbnail = '<img src="' + root + 'error.png" class="thumbnail">';
  var play_overlay_icon  = '<img src="' + root + 'icon_play.png" class="btn-preview">';

  log ('redraw_zoom: zoom_page is: ' + zoom_page + ', grid: ' + landing_pages[zoom_page]['grid']);

  if (zoom_page != '')
    {
    for (var y = 1; y <= 3; y++)
      {
      for (var x = 1; x <= 3; x++)
        {
        var yx = y * 10 + x;
        var linear = ((y-1) * 3) + (x-1);

        if (linear in landing_pages [zoom_page]['grid'])
          {
          var id = landing_pages [zoom_page]['grid'][linear];
          if (id == '0')
            {
            html += '<li id="zoom-' + yx + '"></li>';
            }
          else if (id in pool)
            {
            var channel = pool [id];
            var thumb = channel ['thumb'];

            if (thumb == '' || thumb == 'null' || thumb == 'false')
              {
              html += '<li id="zoom-' + yx + '">' + bad_thumbnail + play_overlay_icon + '</li>';
              temp_html += '<li></li>';
              }
            else
              {
              html += '<li id="zoom-' + yx + '"><img src="' + thumb + '" class="thumbnail">' + play_overlay_icon + '</li>';
              if (++temp_count <= 4)
                {
                temp_html += '<li><img src="' + thumb + '"><span class="chName">' + channel ['name'] + '</span>';
                temp_html += '<span class="divider">-</span>';
                temp_html += '<span class="epNum">' + channel ['count'] + '</span>';
                temp_html += '<span>' + translations ['episodeunits'] + '</span></li>';
                }
              }
            }
          else
            {
            html += '<li id="zoom-' + yx + '">' + bad_thumbnail + '</li>';
            }
          }
        else
          html += '<li id="zoom-' + yx + '"></li>';
        }
      }

    html += '</ul>';
    }

  log ("setting landing grid: " + html);
  $("#landing-grid").html (html);

  $("#landing-grid li").unbind();
  $("#landing-grid li").hover (hover_in, hover_out);
  $("#landing-grid li").bind ('click', function () { zoom_click ($(this).attr ('id')); });

  zoom_cursor_on (zoom_cursor);

  // if (!landing_pages [zoom_page]['splashed'])
  $("#temp-channels").html (temp_html);
  }

function cursor_on (cursor)
  {
  // $("#ipg-" + cursor).addClass ((ipg_mode == 'edit') ? "editcursor" : "on");
  $("#ipg-" + cursor).addClass ("on");
  if (cursor in channelgrid && ('new' in channelgrid [cursor]) && channelgrid [cursor]['new'] > 0)
    $("#dot-" + cursor).attr ("src", root + "icon_reddot_on.png");
  }

function cursor_off (cursor)
  {
  // $("#ipg-" + cursor).removeClass ((ipg_mode == 'edit') ? "editcursor" : "on");
  $("#ipg-" + cursor).removeClass ("on");
  if (cursor in channelgrid && ('new' in channelgrid [cursor]) && channelgrid [cursor]['new'] > 0)
    $("#dot-" + cursor).attr ("src", root + "icon_reddot_off.png");
  }

function zoom_cursor_on (cursor)
  {
  $("#zoom-" + cursor).addClass ("on");
  }

function zoom_cursor_off (cursor)
  {
  $("#zoom-" + cursor).removeClass ("on");
  }

function drag_cleanup (id)
  {
  if (dragging)
    {
    dragging = false;
    log ("drag_cleanup, square: " + id);
    id = id.replace (/^ipg-/, '');
    /* if this is an accidental move, adopt this position as the new cursor */
    if (ipg_mode != 'edit' || id in channelgrid)
      ipg_cursor = id;
    redraw_ipg();
    elastic();
    ipg_sync();
    }
  }

function move_channel (src, dst)
  {
  dragging = false;

  stop_preload();

  src = src.replace (/^ipg-/, '');
  dst = dst.replace (/^ipg-/, '');

  log ('MOVE CHANNEL: ' + src + ' TO ' + dst + ' (id: ' + channelgrid [src]['id'] + ')');

  $("#waiting").show();

  var query = 'playerAPI/moveChannel?user=' + user + mso() + '&' +
      'grid1=' + server_grid (src) + '&' + 'grid2=' + server_grid (dst);

  var d = $.get (query, function (data)
    {
    $("#waiting").hide();

    log ('moveChannel raw result: ' + data);
    var fields = data.split ('\t');

    if (fields[0] == '0')
      {
      channelgrid [dst] = channelgrid [src];
      channels_by_id [channelgrid [dst]['id']] = dst;

      ipg_cursor = dst;

      delete (channelgrid [src]);
      }
    else
      {
      notice_ok (thumbing, translations ['errormov'] + ': ' + fields [1], "");
      }

    redraw_ipg();
    elastic();

    ipg_sync();
    start_preload_timer();
    });
  }

function fetch_youtube_or_facebook (why, cursor, callback)
  {
  var channel;

  if (why == 'grid')
    channel = channelgrid [cursor];
  else
    channel = pool [cursor];

  var was_youtubed = 'youtubed' in channel;
log ('was youtubed: ' + was_youtubed);
  channel ['youtubed'] = true;

  if (channel ['nature'] == '3')
    {
    if (!was_youtubed)
      {
      fetch_yt_callbacks [channel['id']] = callback;
      fetch_youtube_channel (why, cursor);
      return true;
      }
    }
  else if (channel ['nature'] == '4')
    {
    if (!was_youtubed)
      {
      fetch_yt_callbacks [channel['id']] = callback;
      fetch_youtube_playlist (why, cursor);
      return true;
      }
    }
  else if (channel ['nature'] == '5')
    {
    if (!was_youtubed)
      {
      fetch_yt_callbacks [channel['id']] = callback;
      fetch_facebook_playlist (why, cursor);
      return true;
      }
    }

  return false;
  }

var fbdata;
var fbfeed;
var fbindex = {};
var fbwhy;
var fbgrid;

function fetch_facebook_playlist (why, grid)
  {
  fbwhy = why;
  fbgrid = grid;

  if (why == 'grid')
    var username = channelgrid [grid]['extra'];
  else
    var username = pool [grid]['extra'];

  metainfo_wait();

  var fields = username.split ('/');
  username = fields [fields.length - 1]; 

  log ("FETCHING FACEBOOK PLAYLIST: " + username);

  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://graph.facebook.com/' + username + '&' + 'callback=fb_fetched_1';
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

    /* first, check the pool, it must receive precedence */
    for (var c in pool)
      {
      if (pool [c]['extra'].toLowerCase() == link)
        {
        log ('facebook fetched "' + fbdata ['link'] + '" channel info for pool channel: ' + c);
        channel = pool [c];
        break;
        }
      }

    /* failing that, check the channel grid */
    if (!channel)
      for (var c in channelgrid)
        {
        if (channelgrid [c]['extra'].toLowerCase() == link)
          {
          log ('facebook fetched "' + fbdata ['link'] + '" channel info for grid: ' + c);
          channel = channelgrid [c];
          break;
          }
        }

    if (!channel)
      log ("******** unable to determine where channel goes: " + fbdata ['link']);
    else
      channel ['fbid'] = fbdata ['id'];
    }
  else
    log ('link not in fbdata!');

  if ('description' in fbdata)
    {
    if (channel)
      channel ['desc'] = fbdata['description'].substring (0, 140);
    else
      channelgrid [ipg_cursor]['desc'] = fbdata['description'].substring (0, 140);
    ipg_metainfo();
    }
  else if ('company_overview' in fbdata)
    {
    if (channel)
      channel ['desc'] = fbdata['company_overview'].substring (0, 140);
    else
      channelgrid [ipg_cursor]['desc'] = fbdata['company_overview'].substring (0, 140);
    ipg_metainfo();
    }

  load_fb_feed (fbdata ['id']);
  fbindex [fbdata['id']] = fbdata['username'];
  }

function load_fb_feed (id)
  {
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://graph.facebook.com/' + id + '/feed' + '&' + 'callback=fb_fetched_2' + '&' + 'limit=60';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
  }

function fb_fetched_2 (data)
  {
  var id;
  var channel;
  var real_channel = channelgrid [ipg_cursor]['id'];

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

    /* first, check the pool, it must receive precedence */
    for (var c in pool)
      {
      if (pool [c]['fbid'] == facebook_feed_id)
        {
        log ('feed found in pool channel: ' + c);
        channel = pool [c];
        break;
        }
      }

    if (!channel)
      for (var c in channelgrid)
        {
        if (channelgrid [c]['fbid'] == facebook_feed_id)
          {
          log ('feed found in grid channel: ' + c);
          channel = channelgrid [c];
          break;
          }
        }
    }

  if (channel)
    real_channel = channel ['id'];
  else
    log ('feed "' + facebook_feed_id + '" was not found in available channels');

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

  var channel = channels_by_id [real_channel];

  if (fbwhy == 'grid')
    {
    log ('youtube programs fetched for ' + channel + '(fbgrid ' + fbgrid + '): ' + count);
    fetch_youtube_fin (fbwhy, fbgrid);
    }
  else
    {
    log ('youtube programs fetched for ' + channel + '(not in grid): ' + count);
    fetch_youtube_fin (fbwhy, channel);
    }
  }

function fetch_youtube_playlist (why, grid)
  {
  if (why == 'grid')
    var username = channelgrid [grid]['extra'];
  else
    var username = pool [grid]['extra'];

  metainfo_wait();
  log ("FETCHING YOUTUBE PLAYLIST: " + username);
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://gdata.youtube.com/feeds/api/playlists/' + username + '?v=2' + '&' + 'alt=json-in-script' + '&' + 'format=5' + '&' + 'orderby=position' + '&' + 'start-index=1' + '&' + 'max-results=50' + '&' + 'callback=yt_fetched';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);

  fetch_yt_timex = setTimeout ("fetch_youtube_fin(" + why + "," + grid + ")", 30000);
  }

function fetch_youtube_channel (why, grid)
  {
  if (why == 'grid')
    var username = channelgrid [grid]['extra'];
  else
    var username = pool [grid]['extra'];

  if (username.match (/\//))
    username = channelgrid [grid]['extra'].match (/\/user\/([^\/]*)/)[1];

  metainfo_wait();
  log ("FETCHING YOUTUBE CHANNEL: " + username);
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://gdata.youtube.com/feeds/api/users/' + username + '/uploads?v=2' + '&' + 'alt=json-in-script' + '&' + 'format=5' + '&' + 'orderby=published' + '&' + 'start-index=1' + '&' + 'max-results=50' + '&' + 'callback=yt_fetched';

  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);

  fetch_yt_timex = setTimeout ('fetch_youtube_fin("' + why + '","' + grid + '")', 30000);
  }

var feed;
var entry;
var yyprogram;
function yt_fetched (data)
  {
  var why = '';
  var channel = '';
  var grid = '';

  var now = new Date();

  if (data && data.feed)
    {
    feed = data.feed;

    var name = feed.author[0].name.$t;

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
        grid = '';
        why = 'zoom';
        channel = c;
        break;
        }
      }

    /* failing that, check the channel grid */
    if (why == '')
      for (var c in channelgrid)
        {
        if (channelgrid [c]['extra'] == name)
          {
          log ('youtube fetched "' + name + '" channel info for grid: ' + c);
          grid = c;
          why = 'grid';
          channel = channelgrid [grid]['id'];
          break;
          }
        }

    if (why == '')
      log ("******** unable to determine where channel goes: " + name);

    var entries = feed.entry || [];

    erase_programs_in (channel);

    for (var i = 0; i < entries.length; i++)
      {
      entry = entries[i];
      var id = entry.id.$t;
      var title = entry.title.$t;
      var updated = entry.updated.$t;

      // if (! ("media$content" in entry.media$group))
      //   continue;

      // var url = entry.media$group.media$content[0]['url'];
      var video_id = entry.media$group.yt$videoid.$t;
      var duration = entry.media$group.yt$duration.seconds;
      // var duration = entry.media$group.media$content[0]['duration'];

      var dtime = entry.media$group.yt$uploaded.$t;
      if ($.browser.msie)
        dtime = ie_rearrange_date (dtime);
      var timestamp = new Date (dtime);
 
      duration = formatted_time (duration);

      // var video_id = '';
      // if (url.match (/\/v\//))
      //   video_id = url.match (/\/v\/(...........)/)[1];

      log ("YOUTUBE " + video_id + " EPISODE: " + title);

      var thumb = entry.media$group.media$thumbnail[1]['url'];

      var ts = timestamp.getTime();
      if (ts == undefined || isNaN (ts) || ts == Infinity)
        ts = now.getTime();

      var program_id = channel + '.' + video_id;
      programgrid [program_id] = { 'channel': channel, 'url1': 'fp:http://www.youtube.com/watch?v=' + video_id, 
                                   'url2': '', 'url3': '', 'url4': '', 'name': title, 'desc': '', 'type': '',
                                   'thumb': thumb, 'snapshot': thumb, 'timestamp': ts, 'duration': duration, 'sort': i+1 };
      }

    if (why == 'grid')
      {
      log ('youtube programs fetched for ' + channel + '(grid ' + grid + '): ' + entries.length);
      fetch_youtube_fin (why, grid);
      }
    else
      {
      log ('youtube programs fetched for ' + channel + '(not in grid): ' + entries.length);
      fetch_youtube_fin (why, channel);
      }
    }
  else
    log ('***** youtube fetch, data was incomplete ****');
  }

function fetch_youtube_fin (why, grid)
  {
  clearTimeout (fetch_yt_timex);
  log ('fetch youtube fin');
  foo();

  if (why == 'grid')
    {
    channelgrid [grid]['youtubed'] = true;
    ipg_sync();
    ipg_metainfo();
    }
  else
    {
    if (grid)
      {
      pool [grid]['youtubed'] = true;
      zoom_metainfo();
      }
    }

  $("#waiting, #dir-waiting").hide();

  if (thumbing == 'ipg-wait')
    thumbing = 'ipg';
  else if (thumbing == 'zoom-wait')
    thumbing = 'zoom';

  if (why == 'grid')
    {
    if (jumpstart_channel != '')
      {
      log ('youtube fetch (' + grid + ') completed, continuing jumpstart');
      jumpstart_inner();
      }
    else
      log ('jumpstart not in progress, proceeding');

    if (channelgrid [grid]['id'] in fetch_yt_callbacks)
      {
      log ("CALLBACK1: " + fetch_yt_callbacks [channelgrid [grid]['id']]);
      eval (fetch_yt_callbacks [channelgrid [grid]['id']]);
      }
    }
  else if (why == 'zoom')
    {
    if (grid in fetch_yt_callbacks)
      eval (fetch_yt_callbacks [grid]);
    }
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
 
function erase_programs_in (channel)
  {
  for (var p in programgrid)
    {
    if (programgrid [p]['channel'] == channel)
      delete (programgrid [p]);
    }
  }

function erase_all_unclean_youtube_channels()
  {
  for (var c in channelgrid)
    {
    var ch = channelgrid [c];
    var id = ch ['id'];
    var youtubed = (('youtubed' in ch) || ((id in pool) && ('youtubed' in pool [id])));
    if ((ch ['nature'] == 3 || ch ['nature'] == 4) && !youtubed)
      erase_programs_in (id)
    }

  for (var id in pool)
    {
    var ch = pool [id];
    var youtubed = (('youtubed' in ch) || ((id in channels_by_id) && ('youtubed' in channelgrid [channels_by_id[id]])));
    if ((ch ['nature'] == 3 || ch ['nature'] == 4) && !youtubed)
      erase_programs_in (id);
    }
  }

function linear (cursor)
  {
  // Calculating this way does not take into account invalid square cursor locations
  // var y = Math.floor (parseInt (cursor) / 10);
  // var x = zoom_cursor % 10;
  // return ((y-1) * 3) + (x-1);

  var conv = { 11:0, 12:1, 13:2, 21:3, 22:4, 23:5, 31:6, 32:7, 33:8 };
  return (cursor in conv) ? conv [cursor] : -1;
  }

function zoom_metainfo()
  {
  if (thumbing != 'zoom' && thumbing != 'zoom-wait')
    return;

  if (zoom_page in landing_pages)
    {
    var id = landing_pages [zoom_page]['grid'][linear(zoom_cursor)];
    if (id != '0')
      {
      if (id in pool)
        {
        var channel = pool [id];
        $("#channel-title span").html (channel ['name']);
        $("#channel-description span").html (channel ['desc']);
        $("#eps-number").html (translations ['episodes'] + ': ' + channel ['count']);
        $("#updates").html (translations ['updated'] + ': ' + "Today");
        $("#channel-info").show();

        if (fetch_youtube_or_facebook ('zoom', id, ""))
          return;
        }
      else
        log ('zoom_metainfo: channel not in pool: ' + id);
      }
    else
      {
      $("#channel-info").hide();
      }
    }
  }

function ipg_metainfo()
  {
  if (ipg_cursor in channelgrid)
    {
    var thumbnail = channelgrid [ipg_cursor]['thumb'];

    if (thumbnail == '' || thumbnail == 'null' || thumbnail == 'false')
      thumbnail = root + 'error.png'

    var name = channelgrid [ipg_cursor]['name'];
    if (name == '')
      name = '[no title]';

    $("#ch-thumb-img").attr ("src", thumbnail);
    $("#ch-name").html ('<p>' + name + '</p>');
    $("#ep-name").html ('');

    /* new */
    $("#channel-title span").html (name);

    var desc = channelgrid [ipg_cursor]['desc'];
    if (desc == undefined || desc == 'null')
      desc = '';

    $("#description").html ('<p>' + desc + '</p>');

    /* new */
    $("#channel-description span").html (desc);

    var n_eps = programs_in_channel (ipg_cursor);
    var display_eps = n_eps;

    if (fetch_youtube_or_facebook ('grid', ipg_cursor, ""))
      return;

    if (channelgrid [ipg_cursor]['count'] == undefined)
      {
      /* brackets quietly indicate a data inconsistency */
      if (debug_mode)
        display_eps = '[' + n_eps + ']';
      }
    else if (n_eps != channelgrid [ipg_cursor]['count'])
      {
      if (debug_mode)
        display_eps = channelgrid [ipg_cursor]['count'] + ' [' + n_eps + ']';

      if (ipg_mode != 'edit')
        {
        if (! ('refetched' in channelgrid [ipg_cursor]))
          {
          channelgrid [ipg_cursor]['refetched'] = true;
          metainfo_wait();
          fetch_programs_in (channelgrid [ipg_cursor]['id']);
          }
        }
      }
    else
      display_eps = n_eps;

    if (n_eps > 0)
      {
      var first = first_program_in (ipg_cursor);
      $("#update-date").html (ageof (programgrid [first]['timestamp'], true));
      $("#update").show();
      /* new */
      $("#updates").html (translations ['updated'] + ': ' + ageof (programgrid [first]['timestamp'], true));
      $("#updates").show();
      }
    else
      {
      $("#update").hide();
      $("#updates").hide();
      }

    $("#ch-episodes").html (display_eps);
    $("#ep-number").show();

    $("#eps-number").html (translations ['episodes'] + ': ' + display_eps);
    $("#eps-number").show();

    $("#channel-info").show();
    }
  else
    {
    if (ipg_cursor < 0)
      {
      $("#ch-thumb-img").attr ("src", "");
      $("#ch-name").html ('<p></p>');
      $("#ep-name").html ('<p></p>');
      }
    else
      {
      $("#ch-thumb-img").attr ("src", "http://9x9ui.s3.amazonaws.com/images/default_channel.png");
      if (readonly_ipg)
        {
        $("#ch-name").html ('<p></p>');
        $("#ep-name").html ('<p></p>');
        }
      else
        {
        $("#ch-name").html ('<p>' + translations ['addchannel'] + '</p>');
        $("#ep-name").html ('<p>' + translations ['addtip'] + '</p>');
        }
      }

    $("#description").html ('<p></p>');
    $("#ep-number").hide();
    $("#update").hide();

    $("#channel-info").hide();
    }

  zoom_metainfo();
  }

function metainfo_wait()
  {
  if (thumbing == 'browse' || thumbing == 'browse-wait')
    $("#dir-waiting").show();
  else
    $("#waiting").show();

  if (thumbing == 'ipg')
    thumbing = 'ipg-wait';
  else if (thumbing == 'zoom')
    thumbing = 'zoom-wait';
  }

function stop_preload()
  {
  clearTimeout (ipg_preload_timex);

  if (fp_preloaded == 'yt')
    {
    try { ytplayer.stopVideo(); } catch (error) {};
    try { ytplayer.unMute(); } catch (error) {};
    log ('cleared preload: ' + fp_preloaded);
    }
  else if (fp_preloaded != '')
    {
    fp [fp_preloaded]['mute'] = false;

    flowplayer (fp_preloaded).stop();
    flowplayer (fp_preloaded).unmute();

    log ('cleared preload: ' + fp_preloaded);
    }

  fp_preloaded = '';
  $("#preload").html ('None');

  if (fp_next)
    {
    try { flowplayer (fp_next).stop(); } catch (error) {};
    try { flowplayer (fp_next).unmute(); } catch (error) {};
    fp_next = '';
    }

  clearTimeout (fp_next_timex);
  }

function start_preload_timer()
  {
  if (nopreload)
    return;

  if (thumbing == 'ipg' && ipg_cursor in channelgrid)
    {
    ipg_preload_timex = setTimeout ("preload_this_square()", 1000);
    $("#preload").html ('Timer...');
    }
  }

function preload_this_square()
  {
  if (thumbing == 'ipg' && ipg_cursor in channelgrid)
    ipg_preload (ipg_cursor);
  }

function ipg_delete_mode()
  {
  if (ipg_mode == 'edit')
    {
    log ('clicked on delete button, exiting');
    ipg_exit_delete_mode();
    return;
    }

  if (! (ipg_cursor in channelgrid))
    {
    cursor_off (ipg_cursor);
    ipg_cursor = first_channel();
    cursor_on (ipg_cursor);
    }

  log ('enter ipg delete mode');
  ipg_mode = 'edit';

  $("#sg-grid").addClass ("editable");
  $("#edit-txt span").text (translations ['done']);

  setup_draggables();
  ipg_sync();
  }

function OLD_ipg_delete_mode()
  {
  if (ipg_mode == 'edit')
    {
    log ('clicked on delete button, exiting');
    ipg_exit_delete_mode();
    return;
    }

  log ('enter ipg delete mode');
  ipg_mode = 'edit';
  $("#edit-or-finish").html (translations ['findel']);

  $("#ipg-grid").addClass ("deletemode");

  if (parseInt (ipg_cursor) >= 11 && parseInt (ipg_cursor) <= 99)
    {
    ipg_saved_cursor = ipg_cursor;
    $("#ipg-" + ipg_cursor).removeClass ("on");
    }
  else
    {
    // if (ipg_cursor != -2)
    //  $("#btn-edit").removeClass ("on");
    }

  if (! (ipg_cursor in channelgrid))
    ipg_cursor = first_channel();

  if (ipg_cursor in channelgrid)
    {
    $("#btn-edit").removeClass ("on");
    $("#ipg-" + ipg_cursor).addClass ("editcursor");
    }
  else
    {
    notice_ok (thumbing, translations ['nochandel'], "ipg_exit_delete_mode()");
    }

  ipg_sync();
  }

function ipg_exit_delete_mode()
  {
  log ('exit ipg edit mode');
  ipg_mode = 'tip';

  thumbing = 'ipg';

  $("#delete-layer, #mask").hide();

  $("#sg-grid").removeClass ("editable");
  $("#edit-txt span").text (translations ['edit']);

  remove_draggables();

  tip ('');
  ipg_metainfo();
  ipg_sync();
  }

function OLD_ipg_exit_delete_mode()
  {
  log ('exit ipg edit mode');

  ipg_mode = 'tip';
  $("#edit-or-finish").html (translations ['delchan']);
  $("#ipg-grid").removeClass ("deletemode");

  $("#delete-layer, #mask").hide();

  $("#list-holder .clickable").removeClass ("editcursor");

  if (parseInt (ipg_cursor) >= 11 || parseInt (ipg_cursor) <= 99)
    cursor_on (ipg_cursor);

  thumbing = 'ipg';

  tip ('');
  ipg_metainfo();
  ipg_sync();
  }

function ipg_exit_episode_mode()
  {
  log ('ipg mode: episodes -> tip');
  $("#ipg-content").removeClass ("fade");
  ipg_program_tip();
  ipg_mode = 'tip';
  }

function tip (text)
  {
  $("#ep-container").hide();
  $("#ep-tip").html ('<p>' + text + '</p>');
  $("#ep-tip").show();
  }

function ipg_sync()
  {
  if ($("#direct-temp").css ("display") != 'none')
    return;

  if (thumbing == 'zoom' || thumbing == 'zoom-wait')
    {
    zoom_sync();
    return;
    }

  log ('ipg_sync, thumbing: ' + thumbing);

  if (thumbing == 'browse' || thumbing == 'browse-wait')
    return;

  if (ipg_mode == 'edit')
    {
    tip (translations ['deletetip']);
    }
  else if (ipg_cursor in channelgrid)
    {
    if (programs_in_channel (ipg_cursor) < 1)
      {
      tip (translations ['noeps']);
      return;
      }
    ipg_set_channel (ipg_cursor);
    ipg_program_index();
    episode_clicks_and_hovers();
    }
  else if (ipg_cursor < 0)
    {
    ipg_btn_tip (ipg_cursor);
    }
  else
    {
    tip (translations ['addtip']);
    ipg_program_tip();
    }

  /* mostly stay in tip mode now */
  if (ipg_mode != 'edit')
    ipg_mode = 'tip';
  }

function zoom_sync()
  {
  zoom_channel = landing_pages [zoom_page]['grid'][linear(zoom_cursor)];
  if (zoom_channel > 0)
    {
    ipg_program_index();
    episode_clicks_and_hovers();
    }
  else
    {
    tip ('');
    ipg_program_tip();
    }
  }

function episode_clicks_and_hovers()
  {
  $("#ep-list .clickable").unbind();
  $("#ep-list .clickable").bind ('click', function() { ep_click ($(this).attr('id')); });
  $("#ep-list .clickable").hover (ipg_episode_hover_in, ipg_episode_hover_out);
  $("#arrow-left, #arrow-right").unbind();
  $("#arrow-left, #arrow-right").hover (arrow_hover_in, arrow_hover_out);
  $("#arrow-left, #arrow-right").click (arrow_click);
  }

function arrow_click()
  {
  var id = $(this).attr ("id");

  if (id == 'arrow-left')
    {
    log ('ARROW-LEFT');
    program_first -= 9;
    if (program_first < 1)
      program_first = 1;
    }
  else if (id == 'arrow-right')
    {
    log ('ARROW-RIGHT');
    program_first += 9;
    if (program_first > n_program_line)
      program_first = n_program_line;
    }

  program_cursor = program_first;
  redraw_program_line();

  $("#ep-list").html (ep_html());
  $("#ep-list img").error(function () { $(this).unbind("error").attr("src", root + "error.png"); });

  if (thumbing == 'ipg')
    $("#ep-list .clickable").removeClass ("on");

  episode_clicks_and_hovers();

  report ('m', id + ' [' + thumbing + '] ' + id);

  if (thumbing == 'program')
    {
    current_program = program_line [program_cursor];
    play_program();
    }
  }

function arrow_hover_in()
  {
  var id = $(this).attr ("id");
  if (id == 'arrow-left')
    $("#arrow-left").attr ("src", root + 'arrow_left_on.png');
  else if (id == 'arrow-right')
    $("#arrow-right").attr ("src", root + 'arrow_right_on.png');
  }

function arrow_hover_out()
  {
  var id = $(this).attr ("id");
  if (id == 'arrow-left')
    $("#arrow-left").attr ("src", root + 'arrow_left_off.png');
  else if (id == 'arrow-right')
    $("#arrow-right").attr ("src", root + 'arrow_right_off.png');
  }

function ipg_right()
  {
  log ("IPG RIGHT: old ipg cursor: " + ipg_cursor);

  if (ipg_mode == 'episodes')
    {
    physical_mute();
    program_right();
    return;
    }

  if (ipg_cursor < 0)
    {
    if (ipg_cursor == -1)
      {
      $("#btn-signin").removeClass ("on");
      $("#btn-about").addClass ("on");
      ipg_cursor = -2;
      }
    else if (ipg_cursor == -2)
      {
      $("#btn-about").removeClass ("on");
      $("#btn-help").addClass ("on");
      ipg_cursor = -3;
      }
    ipg_sync();
    return;
    }

  cursor_off (ipg_cursor);

  if ($("#sg-grid").hasClass ("x3"))
    ipg_cursor = ipg_3x3_right();
  else if (ipg_mode == 'edit')
    {
    log ('edit, ipg right, cursor: ' + ipg_cursor + ', next: ' + next_channel_square (parseInt (ipg_cursor)));
    ipg_cursor = next_channel_square (ipg_cursor);
    }
  else if (parseInt (ipg_cursor) == 99)
    ipg_cursor = 11;
  else if (parseInt (ipg_cursor) % 10 == 9)
    ipg_cursor = parseInt (ipg_cursor) + 2; /* 39 -> 41 */
  else
    ipg_cursor = parseInt (ipg_cursor) + 1;

  log ("new ipg cursor: " + ipg_cursor);

  cursor_on (ipg_cursor);
  ipg_metainfo();

  stop_preload();
  start_preload_timer();

  ipg_sync();
  }

function ipg_3x3_right()
  {
  var top_left = top_lefts [set];

  if (ipg_cursor == top_left + 22)
    return top_left;
  else if (ipg_cursor == top_left + 2 || ipg_cursor == top_left + 12)
    return parseInt (ipg_cursor) + 8;
  else
    return parseInt (ipg_cursor) + 1;
  }

function zoom_right()
  {
  log ("ZOOM RIGHT: old zoom cursor: " + zoom_cursor);

  if (zoom_cursor < 0)
    {
    if (zoom_cursor == -1)
      {
      $("#btn-signin").removeClass ("on");
      $("#btn-edit").addClass ("on");
      zoom_cursor = -2;
      }
    else if (zoom_cursor == -2)
      {
      $("#btn-edit").removeClass ("on");
      $("#btn-resume").addClass ("on");
      zoom_cursor = -3;
      }
    else if (zoom_cursor == -3)
      {
      $("#btn-resume").removeClass ("on");
      $("#btn-about").addClass ("on");
      zoom_cursor = -4;
      }
    return;
    }

  zoom_cursor_off (zoom_cursor);

  if (parseInt (zoom_cursor) == 33)
    zoom_cursor = 11;
  else if (parseInt (zoom_cursor) % 10 == 3)
    zoom_cursor = parseInt (zoom_cursor) + 8; /* 33 -> 41 */
  else
    zoom_cursor = parseInt (zoom_cursor) + 1;

  log ("new zoom cursor: " + zoom_cursor);

  zoom_cursor_on (zoom_cursor);
  zoom_metainfo();
  }

function ipg_left()
  {
  log ("IPG LEFT: old ipg cursor: " + ipg_cursor);

  if (ipg_mode == 'episodes')
    {
    program_left();
    return;
    }

  if (ipg_cursor < 0)
    {
    if (ipg_cursor == -3)
      {
      $("#btn-help").removeClass ("on");
      $("#btn-about").addClass ("on");
      ipg_cursor = -2;
      }
    else if (ipg_cursor == -2)
      {
      $("#btn-about").removeClass ("on");
      $("#btn-signin").addClass ("on");
      ipg_cursor = -1;
      }
    ipg_sync();
    return;
    }

  cursor_off (ipg_cursor);

  if ($("#sg-grid").hasClass ("x3"))
    ipg_cursor = ipg_3x3_left();
  else if (ipg_mode == 'edit')
    ipg_cursor = previous_channel_square (ipg_cursor);
  else if (parseInt (ipg_cursor) == 11)
    ipg_cursor = 99;
  else if (parseInt (ipg_cursor) % 10 == 1)
    ipg_cursor = parseInt (ipg_cursor) - 2; /* 41 -> 39 */
  else
    ipg_cursor = parseInt (ipg_cursor) - 1;

  log ("new ipg cursor: " + ipg_cursor);

  cursor_on (ipg_cursor);
  ipg_metainfo();

  stop_preload();
  start_preload_timer();

  ipg_sync();
  }

function zoom_left()
  {
  log ("ZOOM LEFT: old zoom cursor: " + zoom_cursor);

  if (zoom_cursor < 0)
    {
    if (zoom_cursor == -4)
      {
      $("#btn-about").removeClass ("on");
      $("#btn-resume").addClass ("on");
      zoom_cursor = -3;
      }
    else if (zoom_cursor == -3)
      {
      $("#btn-resume").removeClass ("on");
      $("#btn-edit").addClass ("on");
      zoom_cursor = -2;
      }
    else if (zoom_cursor == -2)
      {
      $("#btn-edit").removeClass ("on");
      $("#btn-signin").addClass ("on");
      zoom_cursor = -1;
      }
    return;
    }

  zoom_cursor_off (zoom_cursor);

  if (parseInt (zoom_cursor) == 11)
    zoom_cursor = 33;
  else if (parseInt (zoom_cursor) % 10 == 1)
    zoom_cursor = parseInt (zoom_cursor) - 8; /* 31 -> 23 */
  else
    zoom_cursor = parseInt (zoom_cursor) - 1;

  log ("new zoom cursor: " + zoom_cursor);

  zoom_cursor_on (zoom_cursor);
  zoom_metainfo();
  }

function ipg_3x3_left()
  {
  var top_left = top_lefts [set];

  if (ipg_cursor == top_left)
    return top_left + 22;
  else if (ipg_cursor == top_left + 10 || ipg_cursor == top_left + 20)
    return parseInt (ipg_cursor) - 8;
  else
    return parseInt (ipg_cursor) - 1;
  }

function ipg_up()
  {
  log ("IPG UP: old ipg cursor: " + ipg_cursor);

  if (ipg_mode == 'episodes')
    {
    ipg_exit_episode_mode();
    return;
    }

  if (parseInt (ipg_cursor) < 0)
    {
    return;
    }
  else if ($("#sg-grid").hasClass ("x3"))
    {
    ipg_3x3_up();
    }
  else if (ipg_mode == 'edit')
    {
    cursor_off (ipg_cursor);
    ipg_cursor = up_channel_square (ipg_cursor);
    if (ipg_cursor in channelgrid)
      cursor_on (ipg_cursor);
    else
      {
      $("#btn-signin").addClass ("on");
      ipg_cursor = -1;
      }
    }
  else if (parseInt (ipg_cursor) >= 11 && parseInt (ipg_cursor) <= 19)
    {
    cursor_off (ipg_cursor);
    $("#btn-signin").addClass ("on");
    ipg_saved_cursor = ipg_cursor;
    ipg_cursor = -1;
    }
  else if (parseInt (ipg_cursor) > 20)
    {
    cursor_off (ipg_cursor);
    ipg_cursor = parseInt (ipg_cursor) - 10;
    cursor_on (ipg_cursor);
    }

  log ("new ipg cursor: " + ipg_cursor);
  ipg_metainfo();

  stop_preload();

  if (ipg_cursor > 0)
    start_preload_timer();

  ipg_sync();
  }

function ipg_3x3_up()
  {
  var top_left = top_lefts [set];

  if (Math.floor ((parseInt (ipg_cursor) / 10)) == Math.floor (top_left / 10))
    {
    /* eventually navigate to buttons -- later */
    }
  else
    {
    cursor_off (ipg_cursor);
    ipg_cursor = parseInt (ipg_cursor) - 10;
    cursor_on (ipg_cursor);
    }
  }

function zoom_up()
  {
  log ("ZOOM UP: old zoom cursor: " + zoom_cursor);

  if (parseInt (zoom_cursor) < 0)
    {
    return;
    }
  else if (false && parseInt (zoom_cursor) >= 11 && parseInt (zoom_cursor) <= 13)
    {
    zoom_cursor_off (zoom_cursor);
    $("#btn-signin").addClass ("on");
    zoom_saved_cursor = zoom_cursor;
    zoom_cursor = -1;
    }
  else if (parseInt (zoom_cursor) > 20)
    {
    zoom_cursor_off (zoom_cursor);
    zoom_cursor = parseInt (zoom_cursor) - 10;
    zoom_cursor_on (zoom_cursor);
    }

  log ("new zoom cursor: " + zoom_cursor);
  zoom_metainfo();
  }

function ipg_down()
  {
  log ("IPG DOWN: old ipg cursor: " + ipg_cursor);

  if (ipg_mode == 'episodes')
    return;

  if (ipg_cursor < 0)
    {
    $("#btn-signin, #btn-about, #btn-help").removeClass ("on");
    if (ipg_mode == 'edit')
      ipg_cursor = first_channel();
    else
      ipg_cursor = ipg_saved_cursor;
    }
  else if ($("#sg-grid").hasClass ("x3"))
    {
    ipg_3x3_down();
    }
  else if (ipg_cursor > 90)
    {
    /* bottom row */
    }
  else if (ipg_mode == 'edit')
    {
    cursor_off (ipg_cursor);
    var possible = down_channel_square (ipg_cursor);
    if (possible in channelgrid)
      ipg_cursor = possible;
    }
  else
    {
    cursor_off (ipg_cursor);
    ipg_cursor = parseInt (ipg_cursor) + 10;
    }

  log ("new ipg cursor: " + ipg_cursor);

  cursor_on (ipg_cursor);
  ipg_metainfo();

  stop_preload();
  start_preload_timer();

  ipg_sync();
  }

function ipg_3x3_down()
  {
  var top_left = top_lefts [set];

  if (Math.floor ((parseInt (ipg_cursor) / 10)) == (Math.floor (top_left / 10) + 2))
    {
    /* bottom row */
    }
  else
    {
    cursor_off (ipg_cursor);
    ipg_cursor = parseInt (ipg_cursor) + 10;
    }
  }

function zoom_down()
  {
  log ("ZOOM DOWN: old zoom cursor: " + zoom_cursor);

  if (zoom_cursor < 0)
    {
    $("#btn-signin, #btn-edit, #btn-resume, #btn-about").removeClass ("on");
    zoom_cursor = zoom_saved_cursor;
    }
  else if (zoom_cursor > 30)
    {
    /* bottom row */
    }
  else
    {
    zoom_cursor_off (zoom_cursor);
    zoom_cursor = parseInt (zoom_cursor) + 10;
    }

  log ("new zoom cursor: " + zoom_cursor);

  zoom_cursor_on (zoom_cursor);
  zoom_metainfo();
  }

function zoom_enter()
  {
  log ("ZOOM ENTER");
  zoom_play();
  }

function zoom_play()
  {
  log ('ZOOM PLAY, cursor:  ' + zoom_cursor);

  we_are_zoom = true;
  zoom_channel = landing_pages [zoom_page]['grid'][linear(zoom_cursor)];

  if (fetch_youtube_or_facebook ('zoom', zoom_channel, "zoom_play_inner()"))
    {
    thumbing = 'zoom-play-wait';
    return;
    }

  zoom_play_inner();
  }

function zoom_play_inner()
  {
  log ('zoom_play_inner');

  $("#sg-layer, #ad-layer").hide();
  switch_to_control_layer (true);

  current_channel = '';
  current_program = first_program_in_real_channel (zoom_channel);

  enter_channel ('program');
  update_bubble();

  report_program();

  play_first_program_in_real_channel (zoom_channel);
  }

function zoom_click (id)
  {
  if (modal_box)
    return;

  if (thumbing == 'zoom')
    {
    id = id.replace (/^zoom-/, '');
    log ('zoom_click: ' + id);

    var previous_cursor = zoom_cursor;

    zoom_cursor = id;
    report ('m', 'zoom-click [' + thumbing + '] ' + id);

    if (zoom_cursor != previous_cursor)
      {
      zoom_cursor_off (previous_cursor);
      zoom_cursor_on (id);
      zoom_metainfo();
      }
    else
      {
      zoom_play();
      }
    }
  }

function ipg_resume()
  {
  /* this may have received focus */
  $('#ipg-return-btn').blur();

  if (clips_played == 0 || current_tube == '' || ipg_entry_program == '' || ! (ipg_entry_channel in channelgrid) || programs_in_channel (ipg_entry_channel) < 1)
    {
    notice_ok (thumbing, translations ['notplaying'], "");
    return
    }

  log ('ipg resume: ' + ipg_entry_channel);

  clearTimeout (ipg_timex);
  clearTimeout (ipg_delayed_stop_timex);

  stop_preload();
  stop_all_players();

  ipg_cursor = ipg_entry_channel;
  ipg_sync();

  set_channel_and_program (ipg_entry_channel, ipg_entry_program);

  $("#sg-layer").hide();
  play_program();
  }

function ipg_set_channel (grid)
  {
  var saved = thumbing;

  program_cursor = 1;
  program_first = 1;

  current_program = first_program_in (grid);

  current_channel = grid;
  enter_channel (thumbing);

  /* and then yucky fixups (temporary) */
  if (saved == 'ipg')
    {
    thumbing = 'ipg';
    $("#sg-layer").show().css("display","block");
    }

  clear_osd_timex();
  }

function ipg_program_tip()
  {
  $("#ep-container").hide();
  $("#ep-tip").show();
  $("#ep-layer").show();
  $("#ipg-content").removeClass ("fade");
  $("#ep-panel").attr ("src", root + 'ep_panel_off.png');

  if (ipg_mode != 'edit')
    ipg_mode = 'tip';

  if (ipg_cursor in channelgrid && !channelgrid [ipg_cursor]['thumbcache'])
    {
    channelgrid [ipg_cursor]['thumbcache'] = 1;
    setTimeout ("ipg_episode_thumbs(" + ipg_cursor + ")", 400);
    }
  }

function ipg_episode_thumbs (id)
  {
  if (thumbing == 'ipg' && ipg_cursor == id)
    {
    if (channelgrid [id]['thumbcache'] == 2)
      {
      /* thumbs presently being loaded */
      return;
      }

    channelgrid [id]['thumbcache'] = 2;

    log ('preload episode thumbs');
    ipg_set_channel (id);

    for (var p in program_line)
      {
      if (program_line [p] in programgrid)
        {
        var image = new Image();
        image.src = programgrid [program_line[p]]['thumb'];
        }
      }
    }
  }

function ipg_program_index()
  {
  $("#ep-list").html (ep_html());
  $("#ep-list li").removeClass ("on");
  $("#ep-layer").show();
  $("#ep-tip").hide();
  $("#ep-container").show();
  $("#ep-panel").attr ("src", root + 'ep_panel_on.png');
  }

function title_click (id)
  {
  id = id.replace (/^title-click-/, '');
  log ('title_click: ' + id);

  if (ipg_mode == 'edit')
    ipg_exit_delete_mode();

  if ($("#sg-grid").hasClass ("x3"))
    {
    back_to_smart_guide();
    return;
    }

  for (var i in top_lefts)
    {
    if (id == top_lefts [i])
      {
      $("#sg-grid").removeClass("x9").addClass("x3");
      $("#btn-edit").hide();
      log ('set now ' + i);
      set = i;
      SetMove (set);
      CheckSet();
      $("#btn-smart-guide").unbind();
      $("#btn-smart-guide").click (function() 
        {
        back_to_smart_guide();
        });
      $("#next-set").unbind();
      $("#next-set").click  (function()
        {
	if (set < 8 ) 
          SetMove (++set);
        });
      $("#prev-set").unbind();
      $("#prev-set").click (function()
        {
        if (set > 0)
	  SetMove (--set); 
        });
      $("#pagination .pdot").bind ('click', function() { pdot_click ($(this).attr ('id')); });
      return;
      }
    }
  }

function back_to_smart_guide()
  {
        Reset();
	$("#prev-set, #next-set").hide();
        $("#sg-grid").removeClass("x3").addClass("x9");
        $("#btn-edit").show();
        $("#btn-edit").unbind();
        $("#btn-edit").click (ipg_delete_mode);
        $("#btn-smart-guide").unbind();
  }

function pdot_click (id)
  {
  id = id.replace (/^pdot-/, '');
  log ('pdot_click: ' + id);
  set = id - 1;
  SetMove (set);
  }

function ipg_click (id)
  {
  if (dragging)
    {
    log ('eating apparent false click');
    return;
    }

  if (modal_box)
    return;

  if (thumbing == 'ipg')
    {
    id = id.replace (/^ipg-/, '');
    log ('ipg_click: ' + id);

    var previous_cursor = ipg_cursor;

    if (ipg_mode == 'edit' && ! (id in channelgrid))
      {
      log ('no channel here');
      return;
      }

    ipg_cursor = id;
    ipg_sync();

    report ('m', 'ipg-click [' + thumbing + '] ' + id);

    if (ipg_cursor != previous_cursor)
      {
      cursor_off (previous_cursor);
      cursor_on (id);
      $("#ipg-" + previous_cursor).removeClass ("on");

      ipg_metainfo();
      current_program = first_program_in (id);
      update_bubble();

      stop_preload();
      start_preload_timer();
      }
    else
      {
      log ('PLAY. ipg cursor: ' + ipg_cursor + ', id: ' + id);
      ipg_play();
      }
    }
  }

function ipg_play()
  {
  log ('ipg play: ' + ipg_cursor);

  if ($("#hint-layer").css ("display") == 'block')
    {
    close_box ('#hint-layer');
    return;
    }

  if ($("#about-layer").css ("display") == 'block')
    {
    close_box ('#about-layer');
    return;
    }

  if (ipg_cursor < 0)
    {
    if (ipg_cursor == -1)
      {
      sign_in_or_out();
      }
    else if (ipg_cursor == -2)
      {
      // ipg_delete_mode();
      about();
      }
    else if (ipg_cursor == -3)
      {
      // ipg_resume();
      help();
      }

    clearTimeout (ipg_timex);
    return;
    }

  if (ipg_mode == 'edit')
    {
    ipg_delete_channel();
    return;
    }

  if (! (ipg_cursor in channelgrid))
    {
    if (readonly_ipg)
      {
      log_and_alert ('You cannot add channels while viewing a shared IPG');
      return;
      }
    clearTimeout (ipg_timex);
    browse();
    return;
    }

  if (programs_in_channel (ipg_cursor) < 1)
    {
    notice_ok (thumbing, translations ['noepchan'], "");
    return;
    }

  clearTimeout (ipg_timex);
  clearTimeout (ipg_delayed_stop_timex);

  $(".ep-list .age").hide();
  $("#ep-meta").show();

  if (ipg_mode == 'episodes' && program_cursor != 1)
    {
    /* program is already started or in process of starting */
    /* note that since this navigation is disabled in the ipg, won't use this */
    log ('using preload');
    fp_preloaded = '';
    physical_seek (0);
    physical_unmute();
    physical_play();
    unhide_player (current_tube);
    $("#sg-layer").hide();
    switch_to_control_layer (true);
    thumbing = 'program';
    stop_all_other_players();
    report_program();
    episode_clicks_and_hovers();
    return;
    }

  if (current_tube == '')
    current_tube = 'fp';

  if (fp_preloaded != '')
    {
    ipg_preload_play();
    return;
    }

  ipg_play_grid (ipg_cursor);
  }

function ipg_play_grid (cursor)
  {
  $("#sg-layer").hide();
  switch_to_control_layer (true);

  current_channel = cursor;
  current_program = first_program_in (cursor);

  enter_channel ('program');
  update_bubble();

  report_program();

  play_first_program_in (current_channel);
  }

function ipg_delete_channel()
  {
  if (ipg_cursor in channelgrid)
    {
    $("#delete-layer p, #delete-layer .btn").hide();
    $("#btn-delFinish, #step1, #btn-delYes, #btn-delNo").show();
    $("#btn-delYes").removeClass ("on");
    $("#btn-delNo").addClass ("on");
    delete_mode = 'step1';
    delete_cursor = 2;
    $("#delete-title-1, #delete-title-2").html (channelgrid [ipg_cursor]['name']);
    thumbing = 'delete';
    $("#delete-layer, #mask").show();
    elastic();
    }
  }

function delete_left()
  {
  if (delete_mode == 'step1')
    {
    if (delete_cursor == 2)
      {
      delete_cursor = 1;
      $("#btn-delYes").addClass ("on");
      $("#btn-delNo").removeClass ("on");
      }
    }
  else if (delete_mode == 'step2')
    {
    if (delete_cursor == 2)
      {
      delete_cursor = 1;
      $("#btn-returnSG").addClass ("on");
      $("#btn-delMore").removeClass ("on");
      }
    }
  }

function delete_right()
  {
  if (delete_mode == 'step1')
    {
    if (delete_cursor == 1)
      {
      delete_cursor = 2;
      $("#btn-delYes").removeClass ("on");
      $("#btn-delNo").addClass ("on");
      }
    }
  else if (delete_mode == 'step2')
    {
    if (delete_cursor == 1)
      {
      delete_cursor = 2;
      $("#btn-returnSG").removeClass ("on");
      $("#btn-delMore").addClass ("on");
      }
    }
  }

function delete_enter (cursor)
  {
  if (delete_mode == 'step1')
    {
    if (cursor == 1)
      {
      unsubscribe_channel();
      }
    else if (cursor == 2)
      {
      $("#delete-layer, #mask").hide();
      thumbing = 'ipg';
      }
    }
  else if (delete_mode == 'step2')
    {
    if (cursor == 1)
      {
      ipg_exit_delete_mode();
      }
    else if (cursor == 2)
      {
      $("#delete-layer, #mask").hide();
      thumbing = 'ipg';
      }
    }
  }

function channel_right()
  {
  current_channel = next_channel_square (current_channel);
  log ('outer: current channel is: ' + current_channel);

  if (fetch_youtube_or_facebook ('grid', current_channel, "channel_right_inner(" + current_channel + ")"))
    return;

  channel_right_inner (current_channel);
  }

function channel_right_inner (channel)
  {
  current_channel = channel;
  log ('inner: current channel is: ' + current_channel);
  enter_channel ('program');
  play_first_program_in (current_channel);
  }

function channel_left()
  {
  current_channel = previous_channel_square (current_channel);

  if (fetch_youtube_or_facebook ('grid', current_channel, "channel_left_inner(" + current_channel + ")"))
    return;

  channel_left_inner (current_channel);
  }

function channel_left_inner (channel)
  {
  current_channel = channel;
  enter_channel ('program');
  play_first_program_in (current_channel);
  }

function ep_click (id)
  {
  if (thumbing == 'program' || thumbing == 'control' || thumbing == 'ipg' || thumbing == 'sharelanding')
    {
    id = id.replace (/^p-li-/, '');
    log ('ep_click: ' + id);
    program_cursor = id;
    redraw_program_line();
    report ('m', 'episode-click [' + thumbing + '] ' + id);
    physical_stop();
    if (thumbing == 'sharelanding')
      toast();
    if (thumbing == 'ipg' || thumbing == 'sharelanding')
      {
      thumbing = 'program';
      $("#sg-layer").hide();
      switch_to_control_layer (true);
      if (program_cursor == 1)
        {
        ipg_play()
        return;
        }
      else
        stop_preload();
      }
    if (tube() == 'fp' || tube() == 'yt' || tube() == '') play_program();
    }
  }

function ipg_episode_hover_in()
  {
  if (thumbing == 'program' || thumbing == 'control')
    $("#ep-list .clickable").removeClass ("on");

  $(this).addClass ("hover");

  var id = $(this).attr('id').replace (/^p-li-/, '');

  var program = programgrid [program_line [id]];

  $("#ep-layer-ep-title").html (truncated_name (program ['name']));
  $("#ep-age").html (ageof (program ['timestamp'], true));
  $("#ep-length").html (durationof (program ['duration']));

  $(".ep-list .age").hide();
  $("#ep-meta").show();

  if (thumbing == 'program' || thumbing == 'control')
    reset_osd_timex();
  }

function ipg_episode_hover_out()
  {
  $(this).removeClass ("hover");

  var id = $(this).attr('id').replace (/^p-li-/, '');

  var program = programgrid [program_line [program_cursor]];

  $("#ep-layer-ep-title").html (truncated_name (program ['name']));
  $("#ep-age").html (ageof (program ['timestamp'], true));
  $("#ep-length").html (durationof (program ['duration']));

  if (thumbing == 'ipg' || thumbing == 'ipg-wait')
    {
    $("#ep-meta").hide();
    $(".ep-list .age").show();
    }

  if (thumbing == 'program' || thumbing == 'control')
    {
    $("#p-li-" + program_cursor).addClass ("on");
    reset_osd_timex();
    }
  }

function truncated_name (name)
  {
  return (name.length > 60) ? (name.substring (0, 57) + '...') : name;
  }

function ipg_btn_hover_in()
  {
  var id = $(this).attr ("id");
  var cursor;

  if (id == "btn-signin")
    cursor = -1;
  else if (id == "btn-about")
    cursor = -2;
  else if (id == "btn-help")
    cursor = -3;

  $(this).addClass ("hover");
  ipg_btn_tip (cursor);
  }

function ipg_btn_hover_out()
  {
  $(this).removeClass ("hover");
  /* might have exited IPG via this button */
  if (thumbing == 'ipg' || thumbing == 'sharelanding')
    ipg_sync();
  else if (thumbing == 'zoom')
    tip('');
  }

function ipg_btn_tip (cursor)
  {
  if (cursor == -1)
    {
    if (username == 'Guest' || username == '')
      tip (translations ['signuptip'])
    else
      tip (translations ['signouttip']);
    }
  else if (cursor == -2)
    tip (translations ['aboutus']);
  else if (cursor == -3)
    tip (translations ['help']);

  ipg_program_tip();
  }

function preload_is_valid (program)
  {
  if (current_tube == 'fp' && fp_next != '')
    {
    var url = best_url (program);
    url = url.replace (/^fp:/, '');
    return fp [fp_next]['file'] == url;
    }
  else
    return false;
  }

function program_right()
  {
  log ('program right');

  if (program_cursor < n_program_line)
    {
    program_cursor++;
    redraw_program_line();
    update_bubble();
    physical_stop();

    if (preload_is_valid (program_line [program_cursor]))
      {
      log ('valid episode preload detected, using it');
      fp_player = fp_next;
      fp_next = '';
      $("#played").css ("width", '0%');
      physical_seek (0);
      physical_unmute();
      physical_play();
      unhide_player ("fp");
      return;
      }
    else
      log ('no valid episode preload detected');

    if (tube() == 'fp' || tube() == 'yt') play_program();
    }
  else
    {
    physical_stop();
    end_message (0);
    }
  }

function program_left()
  {
  log ('program left');

  if (program_cursor > 1)
    program_cursor--;
  else
    return;

  redraw_program_line();
  play_program();
  }

function redraw_program_line()
  {
  log ('redraw program line');

  while (program_cursor < program_first)
    {
    --program_first;
    $("#ep-list").html (ep_html());
    $("#ep-list img").error(function () { $(this).unbind("error").attr("src", root + "error.png"); });
    }

  while (program_cursor >= program_first + max_programs_in_line)
    {
    ++program_first;
    $("#ep-list").html (ep_html());
    $("#ep-list img").error(function () { $(this).unbind("error").attr("src", root + "error.png"); });
    }

  if (thumbing != 'ipg' && thumbing != 'ipg-wait')
    for (var i = program_first; i <= n_program_line && i < program_first + max_programs_in_line; i++)
      {
      if (i == program_cursor)
        {
        if (! $("#p-li-" + i).hasClass ("on"))
          {
          $("#p-li-" + i).addClass ("on");
          }
        }
      else
        {
        if ($("#p-li-" + i).hasClass ("on"))
          {
          $("#p-li-" + i).removeClass ("on");
          }
        }
      }

  if (program_first != 1)
    $("#arrow-left").show();
  else
    $("#arrow-left").hide();

  if (program_first + max_programs_in_line <= n_program_line)
    $("#arrow-right").show();
  else
    $("#arrow-right").hide();

  episode_clicks_and_hovers();
  }

function setup_ajax_error_handling()
  {
  $.ajaxSetup ({ error: function (x, e)
    {
    $("#buffering").hide();
    $("#msg-layer").hide();

    if (x.status == 0)
      {
      log ('** ERROR ** No network! **');
      }
    else if (x.status == 404)
      {
      log_and_alert ('404 Not Found');
      }
    else if (x.status == 500)
      {
      log_and_alert (translations ['uncaught'] + ' Code 500');
      }
    else if (e == 'timeout')
      {
      log_and_alert ('Network request timed out!');
      }
    else
      {
      log_and_alert ('Unknown error: ' + x.responseText);
      }
    }});
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

function getcookie (id)
  {
  log ('getcookie: ' + document.cookie);

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

function sign_in_or_out()
  {
  if (thumbing != 'ipg' && thumbing != 'sharelanding')
    return;

  if (username != 'Guest' && username != '')
    {
    var d = $.get ("playerAPI/signout?user=" + user + mso(), function (data)
      {
      var lines = data.split ('\n');

      var fields = lines[0].split ('\t');
      if (fields [0] != '0')
        {
        log ('[signout] server error: ' + lines [0]);
        return;
        }

      log ('after signout, cookie is: ' + document.cookie);

      notice_ok ('ipg', translations ['thanx'], "login()");
      });
    }
  else
    login_screen();
  }

function login_screen()
  {
  /* this may have received focus */
  $('#ipg-signin-btn').blur()

  stop_preload();
  stop_all_players();

  saved_thumbing = thumbing;
  thumbing = 'user';

  $("#signin-layer").show();

  $("#btn-winclose").unbind();
  $("#btn-winclose").hover (hover_in, hover_out);
  $("#btn-winclose").click (escape);

  elastic();

  $("#signin-layer .textfield").focus (user_focus);
  $("#signin-layer .textfield").blur (user_blur);

  user_cursor = 'S-name';
  $("#S-name").focus();
  }

function user_up()
  {
  var old_cursor = user_cursor;
  var new_cursor = user_cursor;

  log ('user up: ' + old_cursor);

  if (user_cursor == 'L-password')
    new_cursor = 'L-email';
  else if (user_cursor == 'S-password2')
    new_cursor = 'S-password';
  else if (user_cursor == 'S-password')
    new_cursor = 'S-email';
  else if (user_cursor == 'S-email')
    new_cursor = 'S-name';
  else if (user_cursor == 'S-button')
    {
    new_cursor = 'S-password2';
    $("#S-button").removeClass ("on");
    }
  else if (user_cursor == 'L-button')
    {
    new_cursor = 'L-password';
    $("#L-button").removeClass ("on");
    }

  if (new_cursor != '' && new_cursor != old_cursor)
    {
    $("#" + new_cursor).focus();
    $("#" + old_cursor).blur();
    }
  }

function user_down()
  {
  var old_cursor = user_cursor;
  var new_cursor = user_cursor;

  log ('user down: ' + old_cursor);

  if (user_cursor == 'L-email')
    new_cursor = 'L-password';
  else if (user_cursor == 'S-name')
    new_cursor = 'S-email';
  else if (user_cursor == 'S-email')
    new_cursor = 'S-password';
  else if (user_cursor == 'S-password')
    new_cursor = 'S-password2';
  else if (user_cursor == 'S-password2')
    {
    new_cursor = 'S-button';
    $("#S-button").addClass ("on");
    user_cursor = new_cursor;
    }
  else if (user_cursor == 'L-password')
    {
    new_cursor = 'L-button';
    user_cursor = new_cursor;
    $("#L-button").addClass ("on");
    }

  if (new_cursor != '' && new_cursor != old_cursor)
    {
    $("#" + new_cursor).focus();
    $("#" + old_cursor).blur();
    }
  }

function user_left()
  {
  var old_cursor = user_cursor;
  var new_cursor = user_cursor;

  if (user_cursor == 'S-name')
    new_cursor = 'L-email';
  else if (user_cursor == 'S-email')
    new_cursor = 'L-password';
  else if (user_cursor == 'S-password')
    new_cursor = 'L-email';
  else if (user_cursor == 'S-password2')
    new_cursor = 'L-email';
  else if (user_cursor == 'S-button')
    new_cursor = 'L-email';

  if (new_cursor != '' && new_cursor != old_cursor)
    {
    $("#" + new_cursor).focus();
    $("#" + old_cursor).blur();
    }
  }

function user_right()
  {
  var old_cursor = user_cursor;
  var new_cursor = user_cursor;

  if (user_cursor == 'L-email')
    new_cursor = 'S-name';
  else if (user_cursor == 'L-password')
    new_cursor = 'S-email';
  else if (user_cursor == 'L-button')
    new_cursor = 'S-name';

  if (new_cursor != '' && new_cursor != old_cursor)
    {
    $("#" + new_cursor).focus();
    $("#" + old_cursor).blur();
    }
  }

function user_focus()
  {
  var id = $(this).attr("id");

  $(this).parent(".textfieldbox").addClass("on");
  log ('user focus: ' + id);

  user_cursor = id;

  if (id != 'S-button')
    $("#S-button").removeClass ("on");
  if (id != 'L-button')
    $("#L-button").removeClass ("on");
  }

function user_blur()
  {
  $(this).parent(".textfieldbox").removeClass("on");
  log ('user blur: ' + $(this).attr("id"));
  }

function submit_login()
  {
  var things = [];
  var params = { 'L-email': 'email', 'L-password': 'password' };

  // this is broken in earlier Opera, appears to be Javascript implementation bug
  for (var p in params)
    {
    var v = $('#' + p).val();
    log ("value1: " + v);
    v = encodeURIComponent (v);
    log ("value2: " + v);
    things.push ( params [p] + '=' + v );
    }

  var serialized = things.join ('&') + mso();
  log ('login: ' + serialized);
  
  $("#waiting").show();

  $.post ("playerAPI/login", serialized, function (data)
    {
    $("#waiting").hide();

    log ('login raw data: ' + data);

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == "0")
      {
      for (var i = 2; i < lines.length; i++)
        {
        fields = lines [i].split ('\t');
        if (fields [0] == 'token')
          user = fields [1];
        else if (fields [0] == 'name')
          username = fields [1];
        }

      $("#user, #sg-user").html (username);
      log ('[explicit login] welcome ' + username + ', AKA ' + user);
      solicit();
      report ('u', 'login ' + user + ' ' + username);

      if (readonly_ipg)
        {
        /* this user has now upconverted */
        readonly_ipg = false;
        via_shared_ipg = false;
        }

      /* wipe out the current guest account program+channel data */
      channelgrid = {};
      programgrid = {};
      channels_by_id = {};
      escape();

      resume();
      activated = false;

      fetch_everything();
      }
    else
      {
      notice_ok ('user', translations ['logfail'] + ': ' + fields [1], "error_login_fail()");
      }
    })
  }

function notice_ok (whatnext, text, afterfunction)
  {
  after_confirm = whatnext;
  after_confirm_function = afterfunction;
  thumbing = 'confirm';
  $("#btn-cfclose").unbind();
  $("#btn-cfclose").click (notice_completed);
  $("#confirm-text").html (text);
  $("#confirm-layer").show();
  elastic();
  log ('NOTICE: ' + text);
  }

function error_login_fail()
  {
  $("#L-email").focus();
  user_cursor = 'L-email';
  }

function submit_signup()
  {
  var things = [];
  var params = { 'S-name': 'name', 'S-email': 'email', 'S-password': 'password' };

  // this is broken in earlier Opera, appears to be Javascript implementation bug
  for (var p in params)
    {
    var v = $('#' + p).val();
    v = encodeURIComponent (v);
    things.push ( params [p] + '=' + v );
    }

  if (! $("#S-email").val().match (/\@/))
    {
    notice_ok ('user', translations ['validmail'], "error_bad_email()");
    return;
    }

  if ($("#S-password").val() != $("#S-password2").val())
    {
    notice_ok ('user', translations ['passmatch'], "error_password()");
    return;
    }

  if ($("#S-password").val().length < 6)
    {
    notice_ok ('user', translations ['sixchar'], "error_password()");
    return;
    }

  var serialized = things.join ('&') + '&' + 'user=' + user + mso();
  log ('signup: ' + serialized);

  $("#waiting").show();

  $.post ("playerAPI/signup", serialized, function (data)
    {
    $("#waiting").hide();

    log ('signup response: ' + data);

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == "0")
      {
      for (var i = 2; i < lines.length; i++)
        {
        fields = lines [i].split ('\t');
        if (fields [0] == 'token')
          user = fields [1];
        else if (fields [0] == 'name')
          username = fields [1];
        }

      $("#user, #sg-user").html (username);
      log ('[login via signup] welcome ' + username + ', AKA ' + user);
      solicit();

      report ('u', 'signup ' + user + ' ' + username);

      if (readonly_ipg)
        {
        /* this user has now upconverted */
        readonly_ipg = false;
        via_shared_ipg = false;
        }

      /* wipe out the current guest account program+channel data */
      channelgrid = {};
      programgrid = {};
      channels_by_id = {};
      escape();

      fetch_everything();
      }
    else
      {
      if (fields[1])
        notice_ok ('user', translations ['signupfail'] + ': ' + fields [1], "error_signup_fail()");
      else
        notice_ok ('user', translations ['signupfail'], "error_signup_fail()");
      }
    });
  }

function error_signup_fail()
  {
  $("#S-name").focus();
  user_cursor = 'S-name';
  }

function error_password()
  {
  $("#S-password").val('');
  $("#S-password2").val('');
  user_cursor = 'S-password';
  $("#S-password").focus();
  }

function error_bad_email()
  {
  $("#S-email").val('');
  user_cursor = 'S-email';
  $("#S-email").focus();
  }

function feedback (success, text)
  {
  $("#feedback").addClass (success ? "success" : "fail");
  $("#feedback").removeClass (success ? "fail" : "success");
  $("#feedback p").text (text);
  $("#feedback").show();
  }

function submit_throw()
  {
  if ($("#podcastRSS") == '')
    {
    log ('blank podcastRSS submitted, ignoring');
    return;
    }

  if (username == 'Guest' || username == '')
    {
    feedback (false, 'You must be logged in!');
    return;
    }

  /* always called from IPG, use ipg_cursor */
  var categories = '';

  $("#cate-list .selected").each (function()
    {
    var id = $(this).attr("id").replace (/^addcat-/, '');
    if (categories != '')
      { categories += ','; }
    categories += browsables [id]['category'];
    });

  if (categories == '')
    {
    feedback (false, translations ['needcat']);
    return;
    }

  var url = encodeURIComponent ($("#submit-url").val());

  if (url == '')
    {
    feedback (false, translations ['needurl']);
    return;
    }

  var position = ipg_cursor;
  if (position in channelgrid)
    {
    position = next_free_square (position);
    if (!position)
      {
      log_and_alert ('no free squares');
      return;
      }
    }

  // $("#throw").serialize()
  var serialized =  'url=' + url + '&' + 'user=' + user + mso() + '&' + 'grid=' + server_grid (position) + '&' + 'langCode=' + language + '&' + 'category=' + categories;
  log ('throw: ' + serialized);

  feedback (true, translations ['pleasewait']);
  $("#feedback img").show();

  $.post ("playerAPI/channelSubmit", serialized, function (data)
    {
    $("#feedback img").hide();
    sanity_check_data ('podcastSubmit', data);
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields [0] == "0")
      {
      feedback (true, translations ['successful']);
      dir_requires_update = true;

      log ('channelSubmit successful, returned: ' + data);
      fields = lines[2].split('\t');

      if (url.match (/^http:\/\/(www.)?facebook\.com\//))
        channelgrid [position] = { 'id': fields[0], 'name': fields[1], 'thumb': fields[2], 'nature': '5', 'extra': url };
      else
        channelgrid [position] = { 'id': fields[0], 'name': fields[1], 'thumb': fields[2] };

      channels_by_id [fields[0]] = position;

      redraw_ipg();
      ipg_sync();

      add_dirty_channel (fields[0]);

      report ('c', 'throw ' + position + ' ' + fields[0]);
      }
    else
      {
      feedback (false, translations ['failed'] + ': ' + fields[1]);
      }
    $("#podcastRSS").html ('');
    })
  }

/* podcast channels submitted by the user, which must be polled */

function add_dirty_channel (channel)
  {
  if (dirty_timex)
    clearTimeout (dirty_timex);

  dirty_delay = 15;
  dirty_channels.push (channel);

  log ('next dirty check: ' + dirty_delay + ' seconds');
  dirty_timex = setTimeout ("dirty()", dirty_delay * 1000);
  }

function dirty()
  {
  log ('dirty!');

  dirty_timex = 0;
  var channels = dirty_channels.join();

  if (channels == '')
    {
    log ('dirty(): no dirty channels!');
    return;
    }

  var cmd = "playerAPI/programInfo?channel=" + channels + '&' + "user=" + user + mso();

  var d = $.get (cmd, function (data)
    {
    parse_program_data (data);

    /* once program data is returned, remove those channels from dirty list */

    var lines = data.split ('\n');
    for (var i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');
        dirty_channels.remove (fields[0]);
        }
      }
    });

  fetch_channels();
  redraw_ipg();

  if (dirty_channels.length > 0)
    {
    dirty_delay += 10;
    log ('next dirty check: ' + dirty_delay + ' seconds');
    dirty_timex = setTimeout ("dirty()", dirty_delay * 1000);
    }
  }

function pre_login()
  {
  log ('pre_login');

  var d = $.get ("playerAPI/brandInfo?mso=" + brandinfo, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      for (var i = 2; i < lines.length; i++)
        {
        var fields = lines[i].split ('\t');
        if (fields[0] == 'logoUrl')
          {
          log ('logo: ' + fields[1]);
          for (var v in { 'logo':0, 'sg-logo':0, 'dir-logo':0, 'logo3':0 })
            $("#" + v).attr ('src', fields[1]);
          }
        else if (fields[0] == 'jingleUrl')
          {
          log ('jingle: ' + fields[1]);
          elastic();
          $("#blue").hide();
          $("#opening").css ("display", "block");

          $("#splash").flash({ swf: fields[1], width: "100%", height: "100%", wmode: 'transparent' });
          align_jingle();

          /* temporary */
          jingle_timex = setTimeout ("jingle_completed()", 7000);
          }
        else if (fields[0] == 'preferredLangCode')
          {
          log ('language: ' + fields[1]);
          set_language (fields[1]);
          }
        else if (fields[0] == 'debug')
          {
          log ('debug: ' + fields[1]);
          debug_mode = parseInt (fields[1]) != 0;
          if (debug_mode)
            $("#preloading, #bandwidthing").show();
          else
            $("#preloading, #bandwidthing").hide();
          }
        else if (fields [0] == 'brandInfoCounter')
          {
          log ('counter: ' + fields[1]);
          $("#nowserving").html (fields[1]);
          }
        else if (fields[0] == 'title')
          {
          document.title = fields[1];
          }
        else if (fields[0] == 'name')
          {
          sitename = fields[1];
          }
        }
      login();
      }
    else
      {
      alert ('[brandInfo] failure!');
      }
    });
  }

function jingle_completed()
  {
  clearTimeout (jingle_timex);

  log ('jingle completed');

  if (activated)
    $("#opening").hide();

  jingled = true;
  }

function solicit()
  {
  if (username == 'Guest' || username == '')
    {
    $("#solicit").html (translations ['signin']);
    $("#btn-signin-txt").html (translations ['signin']);
    }
  else
    {
    $("#solicit").html (translations ['signout']);
    $("#btn-signin-txt").html (translations ['signout']);
    }
  }

function login()
  {
  log ('login')
  var u = getcookie ("user");

  if (u)
    {
    log ('user cookie exists, checking');

    var d = $.get ("playerAPI/userTokenVerify?token=" + u + mso(), function (data)
      {
      sanity_check_data ('userTokenVerify', data);
      log ('response to userTokenVerify: ' + data);

      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');

      if (fields[0] == '0')
        {
        proceed_with_valid_login ('login via cookie', lines);
        }
      else
        {
        if (debug_mode)
          log_and_alert ('User token was not valid');
        else
          log ('user token was not valid');
        login();
        }
      });
    }
  else
    {
    if (first_time_user < 2)
      first_time_user = 1;

    if (via_shared_ipg)
      log ('jumpstarting from this ipg: ' + get_ipg_id());

    /* first, try the veal */

    var gcookie = getcookie ("guest");

    if (gcookie)
      {
      var d = $.get ("playerAPI/userTokenVerify?token=" + gcookie + mso(), function (data)
        {
        log ('response to userTokenVerify: ' + data);

        var lines = data.split ('\n');
        var fields = lines[0].split ('\t');

        if (fields[0] == '0')
          {
          proceed_with_valid_login ('login via saved guest cookie', lines);
          }
        else
          {
          log ('error occurred verifying guest token "' + gcookie + '": ' + fields[0]);
          become_a_guest();
          }
        });
      }
    else
      become_a_guest();
    }
  }

function proceed_with_valid_login (how, lines)
  {
  log ('user token was valid');
  first_time_user = 2;

  for (var i = 2; i < lines.length; i++)
    {
    fields = lines [i].split ('\t');
    if (fields [0] == 'token')
      user = fields [1];
    else if (fields [0] == 'name')
      username = fields [1];
    else if (fields [0] == 'lastLogin')
      {
      var one_day_ago = new Date().getTime() - (1000 * 60 * 60 * 24);
      lastlogin = parseInt (fields [1]);
      if (lastlogin < one_day_ago)
        {
        log ('lastlogin too old, using 24 hours');
        lastlogin = one_day_ago;
        }
      }
    }

  $("#user, #sg-user").html (username + "'s");
  solicit();

  log ('[' + how + '] welcome ' + username + ', AKA ' + user);

  if (via_shared_ipg && username != 'Guest')
    {
    log ('setting readonly_ipg');
    readonly_ipg = true;
    }

  fetch_everything();
  }

function become_a_guest()
  {
  log ('user cookie does not exist, obtaining one');

  args = via_shared_ipg ? ('?ipg=' + get_ipg_id() + mso()) : ('?mso=' + sitename);

  var d = $.get ("playerAPI/guestRegister" + args, function (data)
    {
    log ('response to guestRegister: ' + data);
    var u = getcookie ("user");

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == '0')
      {
      if (u)
        log ('user cookie now exists');
      else
        log ('no "user" cookie, but login was successful')

      proceed_with_valid_login ('guest login', lines);
      }
    else if (u)
      {
      log ('guest register failed, but user cookie now exists');
      user = u;
      username = u;
      $("#user, #sg-user").html (username);
      solicit();
      via_shared_ipg = false;
      fetch_everything();
      }
    else
      panic ("was not able to get a user cookie");
    });
  }

function calculate_empties()
  {
  var n = 0;

  for (var y = 1; y <= 9; y++)
    for (var x = 1; x <= 9; x++)
      if (! (["" + y + "" + x] in channelgrid))
        n++;

  var text;

  if (n == 1)
    text = translations ['oneempty'];
  else if (n == 81)
    text = translations ['noempty'];
  else
    text = translations ['empties'].replace (/\%1/, n);

  $("#ch-vacancy").html (text);
  }

function browse()
  {
  log ('channel directory browse');
  report ('e', 'channel-directory');

  saved_thumbing = thumbing;
  thumbing = 'browse-wait';
  browsables = [];
  browser_cat_cursor = 1;
  browser_first_cat = 1;

  $("#waiting").show();

  browser_mode = 'category';
  $("#main-1").addClass ("selected");
  $("#main-2").removeClass ("selected");

  $("#ep-tip").html ('<p></p>');

  var d = $.get ("playerAPI/categoryBrowse?langCode=en" + mso(), function (data)
    {
    $("#waiting").hide();
    sanity_check_data ('categoryBrowse', data);

    // calculate_empties();

    $("#add-panel").hide();
    $("#category-panel").show();
    $("#ch-directory").show();
    elastic();

    thumbing = 'browse';

    var html = '';
    var add_html = '';

    var lines = data.split ('\n');

    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('[categoryBrowse] server error: ' + lines [0]);
      return;
      }

    n_browse = 0;
    for (var i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        n_browse++;
        var fields = lines[i].split ('\t');
        var xclass = (n_browse == 1) ? ' class="selected"' : '';
        var count = (fields[2] == 0) ? '' : ' (' + fields[2] + ')';
        if (n_browse <= max_browse)
          html += '<li id="cat-' + n_browse + '"' + xclass + '><p>' + fields[1] + count + '</p><span class="arrow">&raquo;</span></li>';
        add_html += '<li id="addcat-' + n_browse + '"><img id="img-addcat-' + n_browse + '" src="' + root + 'check_off.png"><span>' + fields[1] + '</span></li>';
        browsables [n_browse] = { category: fields[0], name: fields[1], count: fields[2] };
        }
      }

    $("#ch-catlist").html (html);
    $("#cate-list").html (add_html);

    $("#main-panel li").bind ('click', function () { browse_click (1, $(this).attr ('id')); });
    $("#ch-catlist li").bind ('click', function () { browse_click (2, $(this).attr ('id')); });
    $("#cate-list  li").bind ('click', function () { browse_click (5, $(this).attr ('id')); });

    $("#main-panel li, #ch-catlist li, cate-list li").hover (hover_in, hover_out);
    $("#btn-returnIPG").hover (hover_in, hover_out);

    browse_set_cursor (2, 1);

    stop_all_players();
    });
  }

function redraw_browser_categories()
  {
  var html = '';

  if (browser_y < browser_first_cat)
    browser_first_cat = browser_y;

  while (browser_y >= browser_first_cat + max_browse)
    browser_first_cat++;

  for (var i = browser_first_cat; i <= n_browse && i < browser_first_cat + max_browse; i++)
    {
    var xclass = (browser_y == i) ? ' class="selected"' : '';
    var count = (fields[2] == 0) ? '' : ' (' + fields[2] + ')';
    html += '<li id="cat-' + i + '"' + xclass + '><p>' + browsables[i]['name'] + count + '<span class="arrow">&raquo;</span></li>';
    }

  $("#ch-catlist").html (html);
  $("#ch-catlist li").bind ('click', function () { browse_click (2, $(this).attr ('id')); });
  $("#ch-catlist li").hover (hover_in, hover_out);

  if (browser_x == 1)
    $("#cat-" + browser_y).addClass ("on");
  }

function hover_in()
  {
  $(this).addClass ("hover");
  }

function hover_out()
  {
  $(this).removeClass ("hover");
  }

function browse_set_cursor (x, y)
  {
  log ('browse [' + browser_mode + '] set cursor: ' + x + ', ' + y);

  if (!browser_x || !browser_y)
    {
    browser_x = x;
    browser_y = y;
    browse_category (browsables [browser_y]['category']);
    return;
    }

  if ((browser_x && x != browser_x) || (browser_y && y != browser_y))
    {
    if (browser_x == 1)
      {
      if (browser_y == 3)
        $("#btn-returnIPG").removeClass ("on");
      else
        $("#main-" + browser_y).removeClass ("on");
      }

    else if (browser_mode == 'category')
      {
      if (browser_x == 2)
        $("#cat-" + browser_y).removeClass ("on");

      else if (browser_x == 3)
        $("#content-" + browser_y).removeClass ("on");
      }

    else if (browser_mode == 'add')
      {
      if (browser_x == 2)
        {
        if (browser_y == 1)
          {
          $("#submit-url-box").removeClass ("on");
          $("#submit-url").blur();
          }
        }
      else if (browser_x == 3)
        $("#addcat-" + browser_y).removeClass ("on");

      else if (browser_x == 4)
        $("#add-go").removeClass ("on");
      }
    }

  if (x == 1)
    {
    if (y == 3)
      $("#btn-returnIPG").addClass ("on");
    else
      {
      $("#main-" + y).addClass ("on");
      $("#main-1, #main-2").removeClass ("selected");
      $("#main-" + y).addClass ("selected");
      }
    }
  else if (browser_mode == 'category')
    {
    if (x == 2)
      $("#cat-" + y).addClass ("on");

    else if (x == 3)
      $("#content-" + y).addClass ("on");
    }
  else if (browser_mode == 'add')
    {
    if (x == 2)
      {
      $("#submit-url-box").addClass ("on");
      $("#submit-url").focus();
      // $("#submit-url").select();
      document.getElementById("submit-url").focus();
      document.getElementById("submit-url").select();
      }
    else if (x == 3)
      $("#addcat-" + y).addClass ("on");

    else if (x == 4)
      $("#add-go").addClass ("on");
    }

  log ('setting new cursor: ' + x + ', ' + y);

  browser_x = x;
  browser_y = y;

  if (browser_x == 1)
    {
    if (browser_y == 1)
      {
      $("#add-panel").hide();
      $("#category-panel").show();
      browser_mode = 'category';
      }
    else if (browser_y == 2)
      {
      $("#feedback, #feedback img").hide();
      $("#add-go").removeClass ("on");
      $("#category-panel").hide();
      $("#add-panel").show();
      $("#submit-url").focus (submit_focus);
      $(".btn").hover (hover_in, hover_out);
      $("#submit-url").val ("http://");
      browser_mode = 'add';
      }
    }

  if (browser_mode == 'category' && browser_x == 2 && browser_cat != browsables [browser_y]['category'])
    {
    if (browser_y < browser_first_cat || browser_y >= browser_first_cat + max_browse)
      redraw_browser_categories();

    log ('  selected category, cursor: ' + browser_cat_cursor + ', new: ' + browser_y);
    $("#cat-" + browser_cat_cursor).removeClass ("selected");
    browser_cat_cursor = browser_y;
    $("#cat-" + browser_cat_cursor).addClass ("selected");
    browse_category (browsables [browser_y]['category']);
    }

  if (browser_x == 1)
    {
    if (browser_y == 1)
      tip (translations ['browsetip']);
    else if (browser_y == 2)
      tip (translations ['addrss']);
    else if (browser_y == 3)
      tip (translations ['returnsmart']);
    }

  if (browser_mode == 'add')
    {
    if (browser_x == 2 && browser_y == 1)
      tip (translations ['copypaste']);
    else if (browser_x == 3)
      tip (translations ['threecat']);
    else if (browser_x == 4)
      tip (translations ['processing']);
    }

  if (browser_mode == 'category')
    {
    if (browser_x == 2)
      tip (translations ['browsecats']);

    else if (browser_x == 3)
      {
      if (browser_y < browse_list_first || browser_y >= browse_list_first + 8)
        {
        log ("CONTENT OUT OF RANGE! REDRAW!");
        dir_requires_update = false;
        redraw_browse_content();
        }

      var content = browse_list [parseInt (browse_list_first) + parseInt (browser_y) - 1];

      if (content['id'] in channels_by_id)
        tip (translations ['watchnow']);
      else
        tip (translations ['addsmart']);
      }
    }

  if (dir_requires_update)
    {
    dir_requires_update = false;
    redraw_browse_content();
    }
  }

function submit_focus()
  {
  if (! $("#submit-url-box").hasClass ("on"))
    {
    $("#submit-url-box").addClass ("on");
    browse_set_cursor (2, 1);
    }
  }

function submit_blur()
  {
  $("#submit-url-box").removeClass ("on");
  }

function browse_left()
  {
  if (browser_x > 1)
    {
    if (browser_x == 2)
      {
      switch (browser_mode)
        {
        case 'category': browse_set_cursor (1, 1);
                         break;

        case 'add':      browse_set_cursor (1, 2);
                         break;
        }
      }
    else if (browser_mode == 'add')
      {
      if (browser_x == 2)
        browse_set_cursor (1, 2);
      else if (browser_x == 3)
        {
        if ((parseInt (browser_y) % 4) == 1)
          browse_set_cursor (1, 2);
        else
          browse_set_cursor (browser_x, parseInt (browser_y) - 1);
        }
      else if (browser_x == 4)
        browse_set_cursor (1, 2);
      }
    else if (browser_mode == 'category')
      {
      if ((parseInt (browser_y) % 2) == 1)
        browse_set_cursor (parseInt (browser_x) - 1, browser_cat_cursor);
      else
        browse_set_cursor (browser_x, parseInt (browser_y) - 1);
      }
    }
  }

function browse_right()
  {
  if (browser_x < 4)
    {
    if (browser_mode == 'add')
      {
      if (browser_x == 1)
        {
        browse_set_cursor (parseInt (browser_x) + 1, 1);
        }
      else if (browser_x == 2)
        {
        /* do nothing */
        }
      else if (browser_x == 3)
        {
        if (browser_y < n_browse)
          browse_set_cursor (browser_x, parseInt (browser_y) + 1);
        }
      }
    else if (browser_mode == 'category')
      {
      if (browser_x == 1)
        browse_set_cursor (parseInt (browser_x) + 1, browser_cat_cursor);
      else if (browser_x == 2)
        {
        if (n_browse_list > 0)
          browse_set_cursor (parseInt (browser_x) + 1, 1);
        }
      else if (browser_x == 3)
        {
        if (browser_y < n_browse_list)
          browse_set_cursor (browser_x, 1 + parseInt (browser_y));
        }
      }
    }
  }

function browse_up()
  {
  if (browser_mode == 'add')
    {
    if (browser_x == 1 && browser_y > 1)
      {
      browse_set_cursor (browser_x, parseInt (browser_y) - 1);
      }
    else if (browser_x == 3)
      {
      if (browser_y <= 4)
        {
        /* top row */
        browse_set_cursor (2, 1);
        }
      else
        browse_set_cursor (3, parseInt (browser_y) - 4);
      }
    else if (browser_x == 4)
      {
      var new_y = n_browse;
      while ((new_y % 4) != 1) new_y--;
      browse_set_cursor (3, new_y);
      }
    }
  else if (browser_mode == 'category')
    {
    if (browser_x == 1 || browser_x == 2)
      {
      if (browser_y > 1)
        browse_set_cursor (browser_x, parseInt (browser_y) - 1);
      }
    else if (browser_x == 3)
      {
      if (browser_y > 2)
        browse_set_cursor (browser_x, parseInt (browser_y) - 2);
      }
    }
  }

function browse_down()
  {
  if (browser_x == 1 && browser_y >= 3)
    {
    /* last menu item */
    }
  else if (browser_mode == 'add')
    {
    if (browser_x == 1)
      {
      browse_set_cursor (1, parseInt (browser_y) + 1);
      }
    else if (browser_x == 2)
      {
      browse_set_cursor (3, 1);
      }
    else if (browser_x == 3)
      {
      if (parseInt (browser_y) + 4 <= n_browse)
        browse_set_cursor (3, parseInt (browser_y) + 4);
      else
        browse_set_cursor (4, 1);
      }
    }
  else if (browser_mode == 'category')
    {
    if (browser_x == 2 && browser_y >= n_browse)
      {
      /* at last category */
      }
    else if (browser_x == 3)
      {
      if (browser_y + 2 <= n_browse_list)
        browse_set_cursor (browser_x, parseInt (browser_y) + 2);
      else if (browser_y + 1 <= n_browse_list)
        browse_set_cursor (browser_x, parseInt (browser_y) + 1);
      }
    else
      {
      browse_set_cursor (browser_x, parseInt (browser_y) + 1);
      }
    }
  }

function browse_page_up()
  {
  if (browser_mode == 'category' && browser_x == 3)
    browse_content_up();
  }

function browse_content_up()
  {
  browse_list_first -= 8;
  if (browse_list_first < 1)
    browse_list_first = 1;
  if (browser_x == 3)
    {
    browser_y -= 8;
    if (browser_y < 1)
      browser_y = 1;
    }
  redraw_browse_content();
  }

function browse_page_down()
  {
  if (browser_mode == 'category' && browser_x == 3)
    browse_content_down();
  }

function browse_content_down()
  {
  if (browse_list_first + 8 <= n_browse_list)
    {
    browse_list_first += 8;
    if (browser_x == 3)
      {
      browser_y += 8;
      if (browser_y > n_browse_list)
        browser_y = n_browse_list;
      }
    redraw_browse_content();
    }
  }

function browse_click (column, id)
  {
  log ('browse click :: ' + column + ', ' + id);
  report ('m', 'browse-click [' + thumbing + '] ' + column + ' ' + id);

  if (column == 1)
    {
    id = id.replace (/^main-/, '');
    browse_set_cursor (1, id);
    }
  if (column == 2)
    {
    id = id.replace (/^cat-/, '');
    browse_set_cursor (2, id);
    $("#cat-" + id).addClass ("selected");
    }
  else if (column == 3)
    {
    id = id.replace (/^content-/, '');
    if ($("#content-" + id).hasClass ("on"))
      {
      log ('wants to subscribe to: ' + $("#content-" + id).attr ("data-id"));
      browse_accept ($("#content-" + id).attr ("data-id"));
      }
    else
      {
      /* move cursor here */
      browse_set_cursor (3, id);
      }
    }
  else if (column == 5)
    {
    browse_add_checkbox (id);
    }
  }

function browse_add_checkbox (id)
  {
  if ($("#" + id).hasClass ("selected"))
    {
    $("#" + id).removeClass ("selected");
    $("#img-" + id).attr ('src', root + 'check_off.png');
    }
  else
    {
    $("#" + id).addClass ("selected");
    $("#img-" + id).attr ('src', root + 'check_on.png');
    }
  }

function browse_category (category_id)
  {
  dir_waiting_fixup()
  $("#dir-waiting").show();

  $("#content-list").html ('');
  $("#ch-vacancy").hide();

  browser_cat = category_id;

  try { cat_query.abort(); } catch (error) {};

  var query = "playerAPI/channelBrowse?category=" + category_id + mso();

  cat_query = $.get (query, function (data)
    {
    $("#dir-waiting").hide();
    sanity_check_data ('channelBrowse', data);

    // 0=sequence-number 1=channel-id 2=channel-name 3=thumbnail 4=count

    var lines = data.split ('\n');

    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('[channelBrowse] server error: ' + lines [0]);
      return;
      }

    var category = parseInt (lines[2]);

    if (category != browser_cat)
      {
      log ('ignoring obsolete information for category: ' + category + ' (category is now ' + browser_cat + ')');
      return;
      }

    log ('received channels for category: ' + category);

    browse_content = {};
    browse_list = {};
    n_browse_list = 0;
    browse_list_first = 1;

    for (var i = 4; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');
        if (parseInt (fields[1]) > 0)
          {
          n_browse_list++;

          var name = fields[2];
          if (language == 'zh' || language == 'zh-tw')
            {
            if (name.length > 12)
              name = name.substring (0, 12) + '...';
            }
          else
            {
            if (name.length > 25)
              name = name.substring (0, 22) + '...';
            }

          browse_content [fields[1]] = { 'id': fields[1], 'name': name, 'thumb': fields[3], 'count': fields[4], 'subscribers': fields[5] };
          browse_list [n_browse_list] = { 'id': fields[1], 'name': name, 'thumb': fields[3], 'count': fields[4], 'subscribers': fields[5] };
          }
        }
      }

    redraw_browse_content();
    });
  }

function redraw_browse_content()
  {
  var html = '';

  if (browser_mode == 'category' && browser_x == 3)
    {
    if (browser_y < browse_list_first)
      {
      browse_list_first = browser_y;
      if ((browse_list_first % 2) != 1)
        browse_list_first--;
      }

    while (browser_y >= browse_list_first + 8)
      browse_list_first += 2;
    }

  for (var i = browse_list_first; i <= n_browse_list && i < browse_list_first + 8; i++)
    {
    var content = browse_list [i];

    html += '<li id="content-' + i + '" data-id="' + content['id'] + '"><img src=' + content['thumb'] + ' class="thumbnail">';
    html += '<p class="chdir-title">' + content['name'] + '</p>';

    var eps = content['count'] + ' ' + ((content['count'] == 1) ? translations ['episode_lc'] : translations ['episodes_lc']);
    var subs = content['subscribers'] + ' ' + ((content['subscribers'] == 1) ? translations ['sub_lc'] : translations ['subs_lc']);

    html += '<p class="chdir-meta">' + eps + '<br>' + subs + '</p>';
    html += '<div class="msgbar">';

    if (content['id'] in channels_by_id)
      html += '<p class="status">' + translations ['subscribed'] + '</p></div></li>';
    else
      html += '<p class="tip">' + translations ['entersub'] + '</p></div></li>';
    }

  $("#content-list").html (html);

  if (browse_list_first > 1)
    $("#content-up").show();
  else
    $("#content-up").hide();

  if (browse_list_first + 8 < n_browse_list)
    $("#content-down").show();
  else
    $("#content-down").hide();

  $("#content-list li").bind ('click', function () { browse_click (3, $(this).attr ('id')); });
  $("#content-list li").hover (hover_in, hover_out);

  browse_set_cursor (browser_x, browser_y);

  if (browser_mode == 'category' && browser_x == 3)
    $("#content-" + browser_y).addClass ("on");

  calculate_empties();

  if (n_browse_list > 0)
    $("#ch-vacancy").show();
  else
    $("#ch-vacancy").hide();
  }

function browse_to_ipg()
  {
  log ('click: return to ipg');
  escape();
  }

function browse_enter()
  {
  if (browser_x == 1 && browser_y == 3)
    {
    escape();
    }
  else if (browser_mode == 'category')
    {
    var id = $("#content-" + browser_y).attr ("data-id");
    if (id)
      browse_accept (id);
    }
  else if (browser_mode == 'add')
    {
    if (browser_x == 3)
      browse_add_checkbox ("addcat-" + browser_y);
    }
  }

function browse_play (channel)
  {
  $("#ch-directory").hide();
  thumbing = 'ipg';
  ipg_cursor = channels_by_id [channel];
  ipg_sync();
  ipg_play_grid (ipg_cursor);
  }

function browse_accept (channel)
  {
  if (channel in channels_by_id)
    {
    yes_or_no (translations ['playthis'], "browse_play(" + channel + ")", "", 2);
    }
  else
    {
    var position = ipg_cursor
    if (position in channelgrid)
      {
      position = next_free_square (ipg_cursor);
      if (!position)
        {
        log_and_alert ('no free squares');
        return;
        }
      }

    report ('c', 'subscribe [' + thumbing + '] ' + channel + ' ' + position);
    log ('subscribe: ' + channel +  ' (at ' + server_grid (position) + ')');

    thumbing = 'browse-wait';
    $("#dir-waiting").show();

    var channel_info = {};
    if (channel in browse_content)
      channel_info = browse_content [channel];
    else
      {
      /* race condition, data is bad */
      return;
      }

    var cmd = "playerAPI/subscribe?user=" + user + mso() + '&' + "channel=" + channel + '&' + "grid=" + server_grid (position);
    var d = $.get (cmd, function (data)
      {
      $("#dir-waiting").hide();
      thumbing = 'browse';

      log ('subscribe raw result: ' + data);
      var fields = data.split ('\t');
      if (fields [0] == '0')
        continue_acceptance (position, channel_info);
      else
        notice_ok ('browse', translations ['suberr'] + ': ' + fields [1], "");
      });
    }
  }

function continue_acceptance (position, channel_info)
  {
  log ('accepting new channel ' + channel_info ['id'] + ' in grid location: ' + position);
  stop_preload();

  $("#content-" + browser_y + " .msgbar").html ('<p class="successful">' + translations ['succpress'] + '</p>');
  tip (translations ['enterwatch']);

  /* insert channel */

  channelgrid [position] = { 'id': channel_info ['id'], 'name': channel_info ['name'], 'thumb': channel_info ['thumb'], 'count': channel_info ['count'] };
  channels_by_id [channel_info ['id']] = position;

  redraw_ipg();
  elastic();
  calculate_empties();

  dir_requires_update = true;

  thumbing = 'browse';

  /* obtain programs */

  log ('obtaining programs for: ' + channel_info ['id']);

  var cmd = "playerAPI/programInfo?channel=" + channel_info ['id'] + mso();

  var d = $.get (cmd, function (data)
    {
    sanity_check_data ('programInfo', data);
    parse_program_data (data);
    redraw_ipg();
    elastic();
    });
  }

function add_jumpstart_channel_inner()
  {
  add_jumpstart_channel = false;

  /* use 10, to permit the first channel, 11, to be used */
  position = next_free_square (10);
  if (!position)
    {
    log_and_alert ('no free squares');
    return;
    }

  if (username == 'Guest')
    {
    login_screen();
    return;
    }

  if (jumpstarted_channel in channels_by_id)
    {
    current_channel = channels_by_id [jumpstarted_channel];
    log ('ALREADY SUBSCRIBED TO: ' + jumpstarted_channel + ', in grid location: ' + current_channel);
    notice_ok (thumbing, translations ['alreadysub'], "after_fetch_channels (true)");
    return;
    }

  log ('subscribing to jumpstart channel: ' + jumpstarted_channel);

  var cmd = "playerAPI/subscribe?user=" + user + mso() + '&' + "channel=" + jumpstarted_channel + '&' + "grid=" + server_grid (position);
  var d = $.get (cmd, function (data)
    {
    $("#waiting").hide();
    thumbing = 'ipg-wait';
    current_channel = position;
    log ('subscribe raw result: ' + data);
    var fields = data.split ('\t');
    if (fields [0] == '0')
      {
      $("#waiting").show();
      fetch_channels();
      }
    else
      {
      notice_ok (thumbing, translations ['suberr'] + ': ' + fields [1], "");
      after_fetch_channels (true);
      }
    });
  }

function unsubscribe_channel()
  {
  if (readonly_ipg)
    {
    notice_ok (thumbing, translations ['sharedipg'], "");
    if (ipg_mode == 'edit')
      ipg_exit_delete_mode();
    return;
    }

  if (ipg_cursor in channelgrid)
    {
    var grid = server_grid (ipg_cursor);
    var channel = channelgrid [ipg_cursor]['id'];

    if (channelgrid [ipg_cursor]['type'] == '2')
      {
      notice_ok (thumbing, translations ['syschan'], "");
      return;
      }

    stop_preload();

    $("#delete-layer").hide();
    $("#waiting").show();

    var cmd = "playerAPI/unsubscribe?user=" + user + mso() + '&' + "channel=" + channel + '&' + "grid=" + grid;
    var d = $.get (cmd, function (data)
      {
      dir_requires_update = true;
      delete (channelgrid [ipg_cursor]);
      delete (channels_by_id [channel]);
      redraw_ipg();
      elastic();

      report ('c', 'unsubscribe [' + thumbing + '] ' + channel + ' ' + ipg_cursor);

      $("#waiting").hide();

      if (thumbing == 'delete')
        {
        cursor_off (ipg_cursor);
        ipg_cursor = next_channel_square (ipg_cursor);
        cursor_on (ipg_cursor);
        $("#step1, #delete-layer p, #delete-layer .btn").hide();
        $("#step2, #btn-returnSG, #btn-delMore").show();
        $("#btn-returnSG").addClass ("on");
        $("#btn-delMore").removeClass ("on");
        $("#delete-layer").show();
        delete_mode = 'step2';
        delete_cursor = 1;
        ipg_sync();
        }

      ipg_metainfo();

      check_for_empty_clusters();
      });
    }
  }

function sanity_check_data (what, data)
  {
  log ('sanity check ' + what);

  if (data.match (/\!DOCTYPE/))
    {
    log ('sanity check: a !DOCTYPE was found in results from ' + what + ' API');
    return false;
    }

  var lines = data.split ('\n');

  if (lines.length > 9 && lines [0] == '' && lines [1] == '')
    {
    log_and_alert ('very bad data returned from ' + what + ' API');
    return false;
    }

  return true;
  }

function tube()
  {
  /* will be more complicated when there is preloading */
  return current_tube;
  }

function force_pause()
  {
  remembered_pause = physical_is_paused();
  if (!remembered_pause)
    pause();
  }

function resume()
  {
  log ('resume');
  if (remembered_pause != physical_is_paused())
    {
    pause();
    remembered_pause = physical_is_paused();
    }
  }

function pause()
  {
  if (physical_is_paused())
    {
    physical_play();
    $("#btn-play").hide();
    $("#btn-pause").show();
    }
  else
    {
    physical_pause();
    $("#btn-pause").hide();
    $("#btn-play").show();
    }
  }

function unhide_player (player)
  {
  log ('unhide: ' + player);

  switch (player)
    {
    case "jw":

      $("#v, #fp1, #fp2").hide();
      $("#jw2").show();
      break;

    case "fp":

      $("#v, jw2, #yt1").hide();

      if (fp_player == 'player1')
        {
        $("#fp1").css ("z-index", "2");
        $("#fp2").css ("z-index", "1");
        }
      else if (fp_player == 'player2')
        {
        $("#fp2").css ("z-index", "2");
        $("#fp1").css ("z-index", "1");
        }

      $("#fp1, #fp2").css ("display", "block");

      break;

    case "yt":

      $("#v, #fp1, #fp2").hide();
      $("#yt1").show();
      $("#yt1").css ("visibility", "visible");
      break;
    }
  }

function physical_start_play (url)
  {
  if (url.match (/youtube\.com/))
    start_play_yt (url);

  else if (url.match (/^http:/))
    start_play_html5 (url);

  else if (url.match (/^jw:/))
    start_play_jw (url);

  else if (url.match (/^fp:/))
    start_play_fp (url);

  update_bubble();
  $("#btn-play").hide();
  $("#btn-pause").show();
  }

function start_play_yt (url)
  {
  yt_video_id = url.match (/v=([^&]+)/)[1];
  log ('YouTube video: ' + yt_video_id);
  setup_yt();
  }

function start_play_jw (url)
  {
  jw_position = 0;
  current_tube = 'jw';

  // ugh! don't know actual url until player is chosen
  url = best_url (current_program);

  jw_video_file = url.replace (/^jw:/, '');

  log ('setting up JW player, video file is: ' + jw_video_file);
  unhide_player ("jw");

  if (jwplayer)
    jw_play();
  else
    jw_timex = setInterval ("retry_jw_start()", 50);
  }

function retry_jw_start()
  {
  if (jwplayer)
    {
    clearTimeout (jw_timex);
    jw_play();
    }
  }

function jw_play()
  {
  log ("jw STOP");
  // jwplayer.sendEvent ('STOP');
  physical_stop();
  log ("jw LOAD " + jw_video_file);
  jwplayer.sendEvent ('LOAD', jw_video_file)
  log ("jw PLAY");
  jwplayer.sendEvent ('PLAY');
  jw_previous_state = '';
  jwplayer.addModelListener ('TIME', 'jw_progress' );
  jwplayer.addModelListener ('STATE', "jw_state_change()" );
  }

function jw_play_nothing()
  {
return;
  jw_video_file = "nothing.flv";
  log ("jw LOAD " + jw_video_file);
  jwplayer.sendEvent ('LOAD', jw_video_file)
  }

function physical_stop()
  {
  switch (tube())
    {
    case "jw": log ('jw STOP');
               try { jwplayer.removeModelListener ('STATE'); } catch (error) {};
               try { jwplayer.sendEvent ('STOP'); } catch (error) {};
               break;

    case "fp": log ('fp ' + fp_player + ' STOP');
               if (flowplayer)
                 try { flowplayer (fp_player).stop(); } catch (error) {};
               break;

    case "yt": log ('yt STOP');
               if (ytplayer)
                 try { ytplayer.stopVideo(); } catch (error) {};
               break;

    }
  }

function physical_mute()
  {
  switch (tube())
    {
    case "jw": break;

    case "fp": log ('fp MUTE');
               if (flowplayer)
                 try { flowplayer (fp_player).mute(); } catch (error) {};
               fp [fp_player]['mute'] = true;
               break;

    case "yt": log ('yt STOP');
               if (ytplayer)
                 try { ytplayer.mute(); } catch (error) {};
               break;
    }
  }

function physical_unmute()
  {
  switch (tube())
    {
    case "jw": break;

    case "fp": log ('fp UNMUTE');
               if (flowplayer)
                 try { flowplayer (fp_player).unmute(); } catch (error) {};
               fp [fp_player]['mute'] = false;
               break;

    case "yt": log ('yt STOP');
               if (ytplayer)
                 try { ytplayer.unMute(); } catch (error) {};
               break;
    }
  }

function jw_state_change()
  {
  var state = jwplayer.getConfig()['state'];
  var previous = jw_previous_state;

  jw_previous_state = state;

  log ('jwplayer state is: ' + state + ', previous state was: ' + previous);

  if (state == 'COMPLETED' && previous != 'COMPLETED')
    {
    log ('jw now completed');
    physical_stop();
    ended_callback();
    }

  if (state == 'IDLE' && previous != 'IDLE')
    {
    log ('jw now idle');
    log ("jw STOP");
    physical_stop();
    ended_callback();
    }

  else if (state == 'BUFFERING')
    {
    // $("#buffering").show();
    }

  else if (state == 'PLAYING')
    {
    // $("#buffering").hide();
    }
  }

function jw_progress (event)
  {
  jw_position = event ['position'];
  update_progress_bar();
  }

function preload_yt (program)
  {
  var url = best_url (program);
  fp_preloaded = 'yt';
  start_play_yt (url)
  }

function ipg_preload (grid)
  {
  log ('preload: grid ' + grid)

  if (programs_in_channel (grid) < 1)
    {
    log ('no programs in channel ' + grid + ' to preload');
    $("#preload").html ('No programs');
    return;
    }

  var program = first_program_in (grid);

  if (best_url (program).match (/youtube\.com/))
    {
    current_tube = 'yt';
    preload_yt (program);
    $("#preload").html ('Start YT...');
    return;
    }

  if (current_tube != 'fp')
    {
    log ('preload: flowplayer was not active');
    current_tube = 'fp';
    unhide_player ("fp");
    fp_player = 'player1';
    }

  fp_preloaded = fp_player == 'player1' ? 'player2' : 'player1';

  if (fp_preloaded == 'player1')
    {
    $("#fp1").css ("z-index", "2");
    $("#fp2").css ("z-index", "1");
    try { flowplayer ("player2").stop(); } catch (error) {};
    }
  else
    {
    $("#fp1").css ("z-index", "1");
    $("#fp2").css ("z-index", "2");
    try { flowplayer ("player1").stop(); } catch (error) {};
    }

  fp_unload (fp_preloaded);

  try { log ('flowplayer preload state: ' + flowplayer (fp_preloaded).getState()); } catch (error) {};

  fp [fp_preloaded]['duration'] = 0;
  fp [fp_preloaded]['loaded'] = 0;

  log ('RUN FLOWPLAYER: ' + fp_preloaded + ' [ipg preload]');
  flowplayer (fp_preloaded,
      {src: 'http://9x9ui.s3.amazonaws.com/scripts/flowplayer.commercial-3.2.5.swf', wmode: 'transparent', allowfullscreen: 'false', allowscriptaccess: 'always' }, 
      { canvas: { backgroundColor: '#000000', backgroundGradient: 'none', linkUrl: '' },
      clip: { onFinish: fp_ended, onStart: fp_onstart, onBegin: fp_onbegin, bufferLength: 1, autoPlay: true, scaling: 'fit', onBufferEmpty: fp_buffering, onBufferFull: fp_notbuffering }, 
      plugins: { controls: null, content: fp_content },
      play: null, onBeforeKeypress: fpkp, onLoad: fp_onpreload,
      onError: function (in_code, in_msg) { log ("ERROR! " + in_code + " TEXT: " + in_msg); },
      key: ['#@02568d345b51c565a77', '#@094b173a0a25655269e', '#@8b75f0276f3becc2f3f', '#@5d96176a271ed2e10e8']  });

  start_preload = new Date();
  $("#preload").html ('Starting...');

  var url = best_url (program);
  url = url.replace (/^fp:/, '');

  fp [fp_preloaded]['file'] = url;
  fp [fp_preloaded]['mute'] = true;
  }

function fp_unload (id)
  {
  log ('want to unload: ' + id);
  try { flowplayer (id).onBeforeUnload (function() { return true; }); } catch (error) {};
  try { flowplayer (id).unload(); } catch (error) {};
  }

function fp_buffering()
  {
  var id = this.id();
  log ('fp ' + id + ' buffering')
  if (id == fp_player)
    $("#buffering").show();
  }

function fp_notbuffering()
  {
  var id = this.id();
  log ('fp ' + id + ' no longer buffering')

  if (id == fp_player)
    $("#buffering").hide();
  }

function fp_onpreload()
  {
  var url = fp [fp_preloaded]['file'];

  log ('onpreload ' + fp_preloaded + ' url: ' + url);
  $("#preload").html ('Waiting...');

  // flowplayer (fp_preloaded).stop();
  flowplayer (fp_preloaded).mute();

  flowplayer (fp_preloaded).play (url);
  flowplayer (fp_preloaded).mute();
  }

function yt_tick()
  {
  if (tube() == "yt")
    update_progress_bar();

  if (thumbing == 'program' && !popup_active)
    $("#body").focus();

  /* cancel ticking if player stopped */

  var state = -2;
  try { state = ytplayer.getPlayerState(); } catch (error) {};

  if (state == -2 || state == -1 || state == 0)
    {
    log ('yt_tick, STATE IS: ' + state);
    clearTimeout (yt_timex);
    }
  }

function stop_all_other_players()
  {
  if (current_tube != 'fp' || fp_player != 'player1')
    try { flowplayer ('player1').stop(); } catch (error) {};

  if (current_tube != 'fp' || fp_player != 'player2')
    try { flowplayer ('player2').stop(); } catch (error) {};

  if (current_tube != 'yt')
    try { ytplayer.stopVideo(); } catch (error) {};
  }

function stop_all_players()
  {
  try { flowplayer ('player1').stop(); } catch (error) {};
  try { flowplayer ('player2').stop(); } catch (error) {};
  try { ytplayer.stopVideo(); } catch (error) {};
  }

function ipg_preload_play()
  {
  if (fp_preloaded == '')
    {
    log ('no preload running');
    return;
    }

  clearTimeout (ipg_timex);
  clearTimeout (ipg_delayed_stop_timex);

  $("#played").css ("width", '0%');

  if (fp_preloaded == 'yt')
    {
    log ('starting preloaded YouTube video');
    fp_preloaded = '';
    current_tube = 'yt';
    try { ytplayer.seekTo (0); } catch (error) {};
    try { ytplayer.unMute(); } catch (error) {};
    $("#yt1").css ("visibility", "visible");
    try { ytplayer.playVideo(); } catch (error) {};
    clearTimeout (yt_timex);
    yt_timex = setInterval ("yt_tick()", 333);
    unhide_player ("yt");
    }
  else
    {
    current_tube = 'fp';
    fp_player = fp_preloaded;
    fp_preloaded = '';

    log ('PRELOAD PLAY: ' + fp_player);

    try { log (fp_player + ' pre1state: ' + flowplayer (fp_player).getState()); } catch (error) {};

    try { flowplayer (fp_player).seek (0); } catch (error) {};

    try { log (fp_player + ' pre2state: ' + flowplayer (fp_player).getState()); } catch (error) {};

    unhide_player ("fp");

    try { log (fp_player + ' pre3state: ' + flowplayer (fp_player).getState()); } catch (error) {};

    fp [fp_player]['mute'] = false;
    try { flowplayer (fp_player).unmute(); } catch (error) {};

    try { log (fp_player + ' pre4state: ' + flowplayer (fp_player).getState()); } catch (error) {};
    }

  stop_all_other_players();

  current_channel = ipg_cursor;
  current_program = first_program_in (ipg_cursor);

  enter_channel ('program');
  update_bubble();
  $("#sg-layer").hide();
  switch_to_control_layer (true);

  program_cursor = 1;
  program_first = 1;

  enter_channel ('program');
  clips_played++;

  report_program();

  try
    {
    if (tube() == 'yt')
      log ('EXIT PRELOAD PLAY, player ' + fp_player);
    else
      log ('EXIT PRELOAD PLAY, player ' + fp_player + ', state ' + flowplayer (fp_player).getState());
    }
  catch (error)
    {
    log ('EXIT PRELOAD PLAY, state unknown');
    }

  if (tube() == 'fp')
    {
    try
      {
      var state = flowplayer (fp_player).getState();
      if (state == -1)
        {
        log ('*** flowplayer was unloaded, trying over');
        $("#buffering").show();
        stop_preload();
        ipg_play();
        }
      else if (state == 1)
        {
        log ('*** flowplayer was unexpectedly idle, restarting with: ' + fp [fp_player]['file']);
        $("#buffering").show();
        flowplayer (fp_player).play (fp [fp_player]['file']);
        }
      else if (state == 2)
        {
        $("#buffering").show();
        }
      }
    catch (error)
      {
      }
    }

  if (current_tube == 'fp')
    {
    thumbing = 'program';
    fp_next_timex = setTimeout ("fp_preload_next(" + program_cursor + ")", 500);
    }
  }

function start_play_fp (url)
  {
  fp_player = 'player1';
  current_tube = 'fp';

  // ugh! don't know actual url until player is chosen
  var url = best_url (current_program);

  url  = url.replace (/^fp:/, '');
  fp [fp_player]['file'] = url;

  unhide_player ("fp");
  log ("FP STREAM: " + url);

  fp_unload (fp_player);

  $("#played").css ("width", '0%');
  $("#buffering").show();

  fp [fp_player]['duration'] = 0;
  fp [fp_player]['loaded'] = 0;

  log ('RUN FLOWPLAYER: ' + fp_player + ' [normal]');
  flowplayer (fp_player,
      {src: 'http://9x9ui.s3.amazonaws.com/scripts/flowplayer.commercial-3.2.5.swf', wmode: 'transparent', allowfullscreen: 'false', allowscriptaccess: 'always' }, 
      { canvas: { backgroundColor: '#000000', backgroundGradient: 'none', linkUrl: '' },
      clip: { onFinish: fp_ended, onStart: fp_onstart, bufferLength: 1, autoPlay: true, scaling: 'fit', onBufferEmpty: fp_buffering, onBufferFull: fp_notbuffering }, 
      plugins: { controls: null, content: fp_content },
      play: null, onBeforeKeypress: fpkp, onLoad: fp_onload,
      key: ['#@02568d345b51c565a77', '#@094b173a0a25655269e', '#@8b75f0276f3becc2f3f', '#@5d96176a271ed2e10e8'] });

  /* hack */
  if (thumbing != 'ipg' || ipg_mode != 'episodes')
    fp [fp_player]['mute'] = false;

  stop_all_other_players();
  }

function fp_onload()
  {
  var id = this.id();
  log ('fp onload: ' + id);
  if (fp [id]['loaded'] != 0)
    {
    log ('WHOAH! ' + id + ' already loaded ' + fp[id]['loaded'] + ' times!');
    state();
    return;
    }
  fp[id]['loaded']++;
  unhide_player ("fp");

  flowplayer (id).onBeforeLoad (function() { return false; });
  flowplayer (id).onBeforeUnload (function() { log ('**** unload attempt: ' + id); return false; });

  flowplayer (id).unmute();
  flowplayer (id).play (fp [id]['file']);
  }

function fp_onbeforeload()
  {
  return false;
  }

function fpkp()
  {
  log ('fpkp');
  return false;
  }

function fp_onbegin()
  {
  var id = this.id();
  log ('fp ' + id + ' onbegin')

  if (fp [id]['mute'])
    {
    try { flowplayer (id).mute(); } catch (error) {};
    }
  else
    {
    try { flowplayer (id).unmute(); } catch (error) {};
    }

  var now = new Date();
  var waited = Math.round ((now.getTime() - start_preload.getTime()) / 100) / 10;
  $("#preload").html ('Preloaded ' + waited + 's');
  $("#buffering").hide();
  }

function fp_onstart()
  {
  var id = this.id();
  log ('fp ' + id + ' onstart (fp_player is: ' + fp_player + ')')

  var fd = parseInt (this.getClip().fullDuration, 10);
  fp [id]['duration'] = fd * 1000;

  if (fp [id]['mute'])
    { try { flowplayer (id).mute(); } catch (error) {}; }
  else
    { try { flowplayer (id).unmute(); } catch (error) {}; }

  /* flowplayer provides no progress/tick event */

  var cmd = 'fp_tick("' + id + '")';
  fp [id]['timex'] = setInterval (cmd, 333);

  if (id == fp_player)
    update_progress_bar();
  else
    {
    if (id == 'player1')
      {
      $("#fp1").css ("z-index", "1");
      $("#fp2").css ("z-index", "2");
      }
    else
      {
      $("#fp1").css ("z-index", "2");
      $("#fp2").css ("z-index", "1");
      }
    }

  if (id == fp_player)
    {
    fp_next = '';
    log ('fp_next cleared');
    fp_next_timex = setTimeout ("fp_preload_next(" + program_cursor + ")", 500);
    }
  }

function fp_preload_next (cursor)
  {
  if (nopreload)
    return;

  log ('preload episode at cursor: ' + cursor);

  if (cursor != program_cursor)
    {
    log ('no preload: have moved on');
    return;
    }

  if (fp_next != '')
    {
    log ('no preload: already preloading in ' + fp_next);
    return;
    }

  if (thumbing != 'program')
    {
    log ('no preload: not in program mode');
    return;
    }


  if (program_cursor < n_program_line)
    {
    var next_program = program_line [program_cursor + 1];
    fp_next = (fp_player == 'player1') ? 'player2' : 'player1';

    log ('fp_next is: ' + fp_next + ', fp_player is: ' + fp_player);
    fp_unload (fp_next);

    if (fp_next == 'player1')
      {
      $("#fp2").css ("z-index", "2");
      $("#fp1").css ("z-index", "1");
      }
    else if (fp_next == 'player2')
      {
      $("#fp1").css ("z-index", "2");
      $("#fp2").css ("z-index", "1");
      }

    var url = best_url (next_program);
    url  = url.replace (/^fp:/, '');

    fp [fp_next]['file'] = url;
    fp [fp_next]['mute'] = true;

    log ('Episode preload: ' + url + ' in: ' + fp_next);
    fp [fp_next]['duration'] = 0;
    fp [fp_next]['loaded'] = 0;
    /* state(); */

    log ('RUN FLOWPLAYER: ' + fp_next + ' [episode preload]');
    flowplayer (fp_next,
          {src: 'http://9x9ui.s3.amazonaws.com/scripts/flowplayer.commercial-3.2.5.swf', wmode: 'transparent', allowfullscreen: 'false', allowscriptaccess: 'always' }, 
          { canvas: { backgroundColor: '#000000', backgroundGradient: 'none', linkUrl: '' },
          clip: { onFinish: fp_ended, onStart: fp_onstart, bufferLength: 1, autoPlay: true, scaling: 'fit', onBufferEmpty: fp_buffering, onBufferFull: fp_notbuffering }, 
          plugins: { controls: null, content: fp_content },
          play: null, onBeforeKeypress: fpkp, onLoad: fp_next_onload,
          key: ['#@02568d345b51c565a77', '#@094b173a0a25655269e', '#@8b75f0276f3becc2f3f', '#@5d96176a271ed2e10e8'] });
    }
  }

function fp_next_onload()
  {
  var id = this.id();

  log ('fp_next_onload: ' + id);
  //unhide_player ("fp");

  flowplayer (id).onBeforeLoad (function() { return false; });
  flowplayer (id).onBeforeUnload (function() { log ('**** [next] unload attempt: ' + id); return false; });

  flowplayer (id).mute();
  flowplayer (id).play (fp [id]['file']);
  }

function fp_ended()
  {
  var id = this.id();
  log ('fp ' + id + ' ended');

  if (id == fp_player)
    ended_callback();

  clearTimeout (fp [id]['timex']);
  }

function fp_tick (id)
  {
  if (id == fp_player)
    update_progress_bar();

  /* cancel ticking if player stopped */

  if (flowplayer (id).getState() == 1)
    clearTimeout (fp [id]['timex']);

  if (thumbing == 'program' && !popup_active)
    $("#body").focus();
  }

function setup_yt()
  {
  unhide_player ("yt");

  current_tube = 'yt';

  if (!ytplayer)
    {
    log ('setting up youtube');

    var params = { allowScriptAccess: "always", wmode: "transparent", disablekb: "1" };
    var atts = { id: "myytplayer" };
    var url = "http://www.youtube.com/apiplayer?version=3&enablejsapi=1";

    swfobject.embedSWF (url, "ytapiplayer", "100%", "100%", "8", null, null, params, atts);

    setup_yt_timex = setTimeout ("setup_yt_timeout()", 30000);
    }
  else
    play_yt();
  }

function setup_yt_timeout()
  {
  var pathname = location.pathname;
  if (thumbing != 'ipg' && $("#yt1").css ("display") != 'none' && pathname.match (/share/))
    {
    notice_ok (thumbing, "A temporary problem was encountered trying to start the video.", "switch_to_ipg()");
    switch_to_ipg();
    }
  }

function onYouTubePlayerReady (playerId)
  {
  clearTimeout (setup_yt_timex);
  ytplayer = document.getElementById ("myytplayer");
  log ("yt ready, id is: " + playerId);
  try { ytplayer.setSize ($(window).width(), $(window).height()) } catch (error) {};
  play_yt();
  }

function play_yt()
  {
  if (ytplayer && yt_video_id)
    {
    log ('yt PLAY');

    if (fp_preloaded == 'yt')
      { try { ytplayer.mute(); } catch (error) {}; }
    else
      {
      try { ytplayer.unMute(); } catch (error) {};
      clearTimeout (yt_timex);
      yt_timex = setInterval ("yt_tick()", 333);
      }

    $("#yt1").css ("visibility", "visible");

    try { ytplayer.addEventListener ('onStateChange', 'yt_state'); } catch (error) {};
    try { ytplayer.addEventListener ('onError', 'yt_error'); } catch (error) {};

    /* yt_quality can be: small | medium | large | hd720 | hd1080 */
    try { ytplayer.loadVideoById (yt_video_id, 0, yt_quality); } catch (error) {};
    }
  else
    alert ("ytplayer not ready");
  }

function yt_state (state)
  {
  // fail (-2), unstarted (-1), ended (0), playing (1), paused (2), buffering (3), video cued (5).

  if (state != yt_previous_state)
    log ('yt state: ' + state);

  if (fp_preloaded == 'yt' && state == 1)
    {
    log ('yt preloaded, setting visibility to hidden');
    ytplayer.pauseVideo();
    $("#yt1").css ("visibility", "hidden");
    $("#preload").html ('YT Preloaded');
    }

  if (fp_preloaded != 'yt')
    {
    if ((state == 1 || state == 2 || state == 3) && yt_previous_state != state)
      {
      log ('restarting yt timex');
      clearTimeout (yt_timex);
      yt_timex = setInterval ("yt_tick()", 333);
      }
    else if (state == 0 && (yt_previous_state == 1 || yt_previous_state == 2 || yt_previous_state == 3))
      {
      /* change this as soon as possible */
      yt_previous_state = state;
      log ('yt eof');
      ended_callback();
      return;
      }
    else if (state == 0 && yt_previous_state == -1)
      {
      /* theoretically impossible, but sometimes happens under IE */
      yt_previous_state = state;
      log ('yt confused, ignoring this event');
      // setTimeout ('yt_confusion()', 2000);
      // ended_callback();
      return;
      }
    }

  yt_previous_state = state;
  }

function yt_confusion()
  {
  log ('yt_confusion: attempting to reload: ' + yt_video_id);
  ytplayer.loadVideoById (yt_video_id);
  }

function yt_error (code)
  {
  var errors = { 100: 'Video not found', 101: 'Embedding not allowed', 150: 'Video not found' };
  var errtext = translations ['ytvid'] + ': Code ' + code;

  if (yt_error_timex != 0)
    {
    log ('** YOUTUBE ERROR ' + code + ', BUT ALREADY PROCESSING AN ERROR **');
    return;
    }

  if (fp_preloaded != 'yt' && (thumbing == 'program' || thumbing == 'control'))
    {
    log ('** YOUTUBE ERROR ** ' + errtext);

    $("#msg-text").html (errtext);
    $("#msg-layer").show();
    elastic();

    /* unload the chromeless player, or bad things happen */
    // $("#yt1").css ("display", "none");
    // ytplayer = undefined;

    yt_error_timex = setTimeout ("yt_error_timeout()", 3500);

    notify_server_bad_program();
    }
  else
    log ('** YOUTUBE ERROR ** ' + errtext);
  }

function yt_error_timeout()
  {
  clear_msg_timex();
  program_right();
  }

function notify_server_bad_program()
  {
  var current_program = program_line [program_cursor];
  var query = "playerAPI/programRemove?user=" + user + mso() + '&' + 'program=' + current_program;

  var d = $.get (query, function (data)
    {
    log ('programRemove returned: ' + data);
    });
  }

function physical_seek (offset)
  {
  switch (tube())
    {
    case "fp":
      if (flowplayer)
        flowplayer (fp_player).seek (offset);
      break;

    case "yt":
      if (ytplayer)
        try { ytplayer.seekTo (offset); } catch (error) {};
      break;
    }
  }

function physical_offset()
  {
  switch (tube())
    {
    case "yt":

      if (ytplayer && ytplayer.getCurrentTime)
        return ytplayer.getCurrentTime();
      else
        return 0;

    case "jw":

      if (jwplayer)
        return jw_position;
      else
        return 0;

    case "fp":

      if (flowplayer)
        {
        var t = flowplayer (fp_player).getTime();
        if (t == undefined || t == NaN || t == Infinity)
          return 0;
        else
          return t;
        }
      else
        return 0;

    case "v1":

      var video = document.getElementById ("vvv");
      return video.currentTime;
    }
  }

function physical_length()
  {
  switch (tube())
    {
    case "yt":

      var duration = 1;
      if (ytplayer && ytplayer.getDuration)
        try { duration = ytplayer.getDuration(); } catch (error) {};
      return duration;

    case "jw":

      if (jwplayer)
        return jwplayer.getPlaylist()[0]['duration'];
      else
        return 1;

    case "fp":

      if (flowplayer && fp[fp_player]['duration'])
        return fp [fp_player]['duration'] / 1000;
      else
        return 1;

    case "v1": 

      var video = document.getElementById ("vvv");
      return video.duration;
    }
  }

function physical_pause()
  {
  switch (tube())
    {
    case "yt":

      if (ytplayer)
        ytplayer.pauseVideo()
      break;

    case "jw":

      if (jwplayer)
        jwplayer.sendEvent ("PLAY", "false");
      break;

    case "fp":

       if (flowplayer)
         try { flowplayer (fp_player).pause(); } catch (error) {};
       break;

    case "v1": var video = document.getElementById ("vvv");
               video.pause();
               break;
    }
  }

function physical_play()
  {
  switch (tube())
    {
    case "yt":

      if (ytplayer)
        ytplayer.playVideo()
      break;

    case "jw":

      if (jwplayer)
        jwplayer.sendEvent ("PLAY", "true");
      break;

    case "fp":

      if (flowplayer)
        try { flowplayer (fp_player).play(); } catch (error) {};
      break;

    case "v1":

      var video = document.getElementById ("vvv");
      video.play();
      break;
    }
  }

function physical_is_paused()
  {
  switch (tube())
    {
    case "yt":

      if (ytplayer)
        return ytplayer.getPlayerState() == 2;
      else
        return false;

    case "jw":

      if (jwplayer)
        return jwplayer.getConfig()['state'] == 'PAUSED';
      else
        return false;

    case "fp":

      if (flowplayer)
        {
        try
          {
          return flowplayer (fp_player).isPaused();
          }
        catch (error)
          {
          return false;
          }
        }
      else
        return false;

      break;

    case "v1":

      var video = document.getElementById ("vvv");
      return video.paused;
    }
  }

function physical_replay()
  {
  switch (tube())
    {
    case "yt":

      if (ytplayer && ytplayer.seekTo)
        {
        ytplayer.seekTo (0, false);
        ytplayer.playVideo();
        }
      break;

    case "jw":

      if (jwplayer && jwplayer.sendEvent)
        {
        jwplayer.sendEvent ("SEEK", "0");
        jwplayer.sendEvent ("PLAY", "true");
        }
      break;

    case "fp":

      if (flowplayer)
        {
        flowplayer (fp_player).seek (0);
        flowplayer (fp_player).resume();
        }
      break;

    default:

      var video = document.getElementById ("vvv");
      video.currentTime = 0;
      video.play();

      break;
    }
  }

function physical_volume()
  {
  switch (tube())
    {
    case "fp":

      if (flowplayer)
        {
        try
          {
          return flowplayer (fp_player).getVolume() / 100;
          }
        catch (error)
          {
          return 0.5;
          }
        }
      else
        return false;

    case "yt":
      if (ytplayer && ytplayer.getVolume)
        return ytplayer.getVolume() / 100;
      else
        return 1;
    }
  }

function physical_set_volume (volume)
  {
  if (volume > 1)
    volume = 1;
  else if (volume < 0)
    volume = 0;

  switch (tube())
    {
    case "fp":

      if (flowplayer)
        try { flowplayer (fp_player).setVolume (volume * 100); } catch (error) {};
      break;

    case "yt":

      if (ytplayer && ytplayer.setVolume)
        ytplayer.setVolume (100 * volume);

      break;
    }
  }

function update_progress_bar()
  {
  var pct = 100 * physical_offset() / physical_length();

  if (pct >= 0)
    $("#played").css ("width", pct + '%');

  var o1 = physical_offset();
  var o2 = physical_length();

  var t1 = formatted_time (physical_offset());
  var t2 = formatted_time (physical_length());

  $("#play-time").html (t1 + " / " + t2);

  var diff = o2 - o1;
  if (diff > 0 && diff < 1)
    log ('diff: ' + diff);

  if (o2 - o1 < 0.2 && tube() == 'v1' && !physical_is_paused() && !fake_timex)
    {
    log ('end of video reached');
    fake_timex = setTimeout ("fake_ended_event()", 200);
    }
  }

function formatted_time (t)
  {
  if (t == '' || t == NaN || t == undefined)
    return '--';

  var m = Math.floor (t / 60);
  var s = Math.floor (t) - m * 60;

  return m + ":" + ("0" + s).substring (("0" + s).length - 2);
  }

function turn_on_fb_bubble()
  {
  $("#fb-bubble").hide();

  current_program = program_line [program_cursor];

  if ('fbmessage' in programgrid [current_program])
    {
    $("#fb-name").html (programgrid [current_program]['fbfrom']);
    $("#fb-comment").html (programgrid [current_program]['fbmessage']);

    var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
    y.src = 'http://graph.facebook.com/' + programgrid [current_program]['fbid'] + '&' + 'callback=fb_bubble_callback';
    var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
    }
  }

function fb_bubble_callback (data)
  {
  log ('fb bubble callback');
  if (data)
    {
    if ('picture' in data)
      {
      $("#fb-picture").attr ("src", data['picture']);
      $("#fb-picture").show();
      }
    else
      {
      $("#fb-picture").hide();
      $("#fb-picture").attr ("src", "");
      }
    $("#fb-bubble").show();
    current_program = program_line [program_cursor];
    setTimeout ('turn_off_fb_bubble("' + current_program + '")', 11000);
    }
  }

function turn_off_fb_bubble (program)
  {
  current_program = program_line [program_cursor];
  if (program == current_program)
    {
    log ('turn off fb bubble');
    $("#fb-bubble").hide();
    }
  }

function exit_control_layer()
  {
  log ('decontrol');
  if (thumbing == 'control' || thumbing == 'program')
    {
    thumbing = 'program';
    $("#control-layer, #ear-left, #ear-right, #ep-layer, #sg-bubble").hide();
    clear_osd_timex();
    // prepare_channel();
    }
  }

function switch_to_control_layer (epflag)
  {
  log ('control layer');

  control_cursor = 2;

  if (epflag)
    {
    $("#ep-layer").show();
    episode_clicks_and_hovers();
    }
  else
    $("#ep-layer").hide();

  /* do this twice */
  control_redraw();

  if (epflag)
    $("#ep-layer").css ("bottom", "1.75em");
  $("#control-layer, #ear-left, #ear-right").show();

  $("#control-layer, #ear-left, #ear-right").unbind();
  $("#control-layer").mousemove (show_eps);
  $("#ear-left").click (ear_left);
  $("#ear-right").click (ear_right);

  $("#sg-layer").hide();

  control_saved_thumbing = thumbing;
  thumbing = 'control';

  control_volume();
  control_redraw();

  $(".cpclick").unbind();
  $(".cpclick").click (control_click);
  $(".cpclick").hover (control_hover_in, control_hover_out);

  reset_osd_timex();

  if (epflag)
    $(".cpclick").removeClass ("on");

  if (user_closed_episode_bubble)
    $("#sg-bubble").hide();
  else
    show_sg_bubble();
  }

function show_eps()
  {
  $("#ep-layer").css ("bottom", "1.75em");
  $("#ep-layer").show();

  episode_clicks_and_hovers();

  report ('e', 'episode-bar');
  }

function control_volume()
  {
  var bars = Math.round (physical_volume() * 7);

  var html = '';
  // for (var i = 7; i >= 1; i--)
  for (var i = 1; i <= 7; i++)
    {
    if (i > bars)
      html += '<li class="volbar" id="vol-' + i + '"></li>'
    else
      html += '<li class="volbar on" id="vol-' + i + '"></li>';
    }

  $("#volume-bars").html (html);
  $("#volume-bars .volbar").unbind();
  $("#volume-bars .volbar").click (volume_click);

  reset_osd_timex();
  }

function volume_click()
  {
  var id = $(this).attr ("id").replace (/^vol-/, '');
  log ('volume click: ' + id);
  physical_set_volume (id / 7.0);
  control_volume();
  }

function control_left()
  {
  if ($("#ep-layer").css ("display") != 'none')
    {
    program_left();
    $("#sg-bubble").hide();
    return;
    }

  $(".cpclick").removeClass ("on");

  if (control_cursor > 0)
   control_cursor--;
  else
   control_cursor = control_buttons.length - 1;

  control_redraw();
  $("#sg-bubble").hide();

  reset_osd_timex();
  }

function control_right()
  {
  if ($("#ep-layer").css ("display") != 'none')
    {
    program_right();
    $("#sg-bubble").hide();
    return;
    }

  $(".cpclick").removeClass ("on");

  if (control_cursor < control_buttons.length - 1)
    control_cursor++;
  else
    control_cursor = 0;

  control_redraw();
  $("#sg-bubble").hide();

  reset_osd_timex();
  }

function control_redraw()
  {
  $(".cpclick").removeClass ("on");
  $('#' + control_buttons [control_cursor]).addClass ("on");

  if (control_buttons [control_cursor] == 'btn-play')
    $('#btn-pause').addClass ("on");

  if (physical_is_paused())
    {
    $("#btn-pause").hide();
    $("#btn-play").show();
    }
  else
    {
    $("#btn-play").hide();
    $("#btn-pause").show();
    }
  }

function control_up()
  {
  if ($("#ep-layer").css ("display") == 'none')
    switch_to_control_layer (true);
  else if (we_are_zoom)
    switch_to_zoom();
  else
    switch_to_ipg();
  }

function control_down()
  {
  if ($("#ep-layer").css ("display") != 'none')
    switch_to_control_layer (false);
  }

function volume_up()
  {
  var volume = physical_volume();
  volume += 1/7;
  if (volume > 1.0) volume = 1.0;
  physical_set_volume (volume);
  control_volume();
  }

function volume_down()
  {
  var volume = physical_volume();
  volume -= 1/7;
  if (volume < 0) volume = 0;
  physical_set_volume (volume);
  control_volume();
  }

function hide_sg_bubble()
  {
  $("#sg-bubble").hide();
  user_closed_episode_bubble = true;
  }

function control_hover_in()
  {
  $(".cpclick").removeClass ("on");
  $(this).addClass ("hover");
  reset_osd_timex();

  if (false)
    if ($(this).attr ("id") == 'btn-sg')
      show_sg_bubble();
  }

function control_hover_out()
  {
  $(this).removeClass ("hover");
  $('#' + control_buttons [control_cursor]).addClass ("on");
  reset_osd_timex();
  }

function show_sg_bubble()
  {
  return; /* turn off this, per Lily 20-May-2011 */
  $("#sg-bubble").show();
  $("#btn-bubble-del").unbind();
  $("#btn-bubble-del").click (hide_sg_bubble);
  }

function control_click()
  {
  var id = $(this).attr("id");
  log ('control click: ' + id);

  $(".cpclick").removeClass ("on");

  report ('m', 'control-click [' + thumbing + '] ' + id);

  if (id == 'btn-pause')
    id = 'btn-play';

  reset_osd_timex();

  for (var i in control_buttons)
    {
    if (control_buttons [i] == id)
      {
      control_cursor = i;
      $('#' + control_buttons [control_cursor]).addClass ("on");
      control_enter();
      return;
      }
    }
  }

function control_enter()
  {
  switch (control_buttons [control_cursor])
    {
    case 'btn-close':       escape();
                            /* return to avoid timex reset */
                            return;

    case 'btn-play':
    case 'btn-pause':       pause();
                            break;

    case 'btn-signin':      login_screen();
                            break;

    case 'btn-replay':      physical_replay();
                            break;

    case 'btn-facebook':    switch_to_facebook();
                            break;

    case 'btn-rewind':      rewind();
                            break;

    case 'btn-forward':     fast_forward();
                            break;

    case 'btn-sg':          if (we_are_zoom)
                              switch_to_zoom();
                            else
                              switch_to_ipg();
                            break;

    case 'btn-volume-up':   volume_up();
                            break;

    case 'btn-volume-down': volume_down();
                            break;
    }

  reset_osd_timex();
  }

function yes_or_no (question, ifyes, ifno, defaultanswer)
  {
  $("#qyes").html (translations ['qyes']);
  $("#qno").html (translations ['qno']);
  a_or_b (question, ifyes, ifno, defaultanswer)
  }

function a_or_b (question, ifyes, ifno, defaultanswer)
  {
  log ('yn saving state: ' + thumbing);
  yn_saved_state = thumbing;

  yn_ifyes = ifyes;
  yn_ifno = ifno;

  $("#question").html (question);
  log ('QUESTION: ' + question);

  yn_cursor = defaultanswer;
  if (defaultanswer == 1)
    {
    $("#yesno-btn-no").removeClass ("on");
    $("#yesno-btn-yes").addClass ("on");
    }
  else
    {
    $("#yesno-btn-yes").removeClass ("on");
    $("#yesno-btn-no").addClass ("on");
    }

  thumbing = 'yes-or-no';

  $("#yesno-layer").show();
  elastic();
  }

function delete_yn()
  {
  if (ipg_cursor in channelgrid)
    {
    if (channelgrid [ipg_cursor]['type'] == '2')
      {
      notice_ok (thumbing, translations ['syschan'], "");
      return;
      }
    yes_or_no (translations ['deletethis'], "delete_yes()", "delete_no()", 2);
    }
  else
    notice_ok (thumbing, translations ['nochansquare'], "");
  }

function delete_yes()
  {
  unsubscribe_channel();
  thumbing = 'ipg';
  }

function delete_no()
  {
  thumbing = 'ipg';
  }

function switch_to_facebook()
  {
  if (ipg_cursor in channelgrid)
    {
    if (channelgrid [ipg_cursor]['nature'] == '5')
      {
      notice_ok (thumbing, translations ['cannotshare'], "");
      return;
      }
    }

  if (we_are_zoom)
    notice_ok (thumbing, translations ['plzreg'], "");
  else
    {
    $("#ep-layer, #control-layer, #ear-left, #ear-right, #sg-bubble").hide();
    yes_or_no (translations ['sharing'], "fb_yes()", "fb_no()", 2);
    }
  }

function fb_yes()
  {
  var months = { 0: 'January', 1: 'February', 2: 'March', 3: 'April', 4: 'May', 5: 'June',
                 6: 'July', 7: 'August', 8: 'September', 9: 'October', 10: 'November', 11: 'December' };

  var now = new Date();
  var stringdate = months [now.getMonth()] + ' ' + now.getDate() + ', ' + now.getFullYear();

  var channel = channelgrid [current_channel]['id'];
  current_program = program_line [program_cursor];
  var thumb = programgrid [current_program]['thumb'];
  var name = 'My 9x9 Channel Guide: ' + stringdate;
  var desc = 'My 9x9 Channel Guide. Easily browse your favorite video Podcasts on the 9x9 Player! Podcasts automatically download and update for you, bringing up to 81 channels of new videos daily.';

  name = programgrid [current_program]['name'];
  desc = programgrid [current_program]['desc'];

  var host = location.host;

  if (sitename == '5f')
    host = host.replace ('9x9.tv', '5f.tv');

  var pid = current_program;
  if (pid.match (/^\d+\./))
    pid = pid.match (/^\d+\.(.*)$/)[1];

  var query = "playerAPI/saveIpg?user=" + user + mso() + '&' + 'channel=' + channel + '&' + 'program=' + pid;
  log ("SAVEIPG: " + query);
  $("#waiting").show();

  var d = $.get (query, function (data)
    {
    log ('saveIpg returned: ' + data);
    $("#waiting").hide();

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    var link = location.protocol + '//' + host + '/share/' + lines[2];
    if (link.match ('//share/'))
      link = link.replace ('//share/', '/share/');

    log ('share link: ' + link);

    var parms = { method: 'feed', name: name, link: link, picture: thumb, description: desc };

    if (sitename == '5f')
      {
      // parms ['actions'] = [ { name: '有沒有抽全台第一台iPad2的八卦？', link: location.protocol + '//' + location.host + '/' + sitename + '/' + 'sharetowin.html' } ];
      }

    if (fields[0] == "0")
      {
      popup_active = true;
      FB.ui (parms, function (response)
        {
        popup_active = false;
        $("#body").focus();
        if (response && response.post_id)
          { log ('published as: ' + response.post_id); } else { log ('not published'); }
        });
      }

    $("#ep-layer, #control-layer, #ear-left, #ear-right").show();
    });
  }

function fb_no()
  {
  $("#ep-layer, #control-layer, #ear-left, #ear-right").show();
  }

function yn_left()
  {
  if (yn_cursor == 2)
    {
    yn_cursor = 1;
    $("#yesno-btn-no").removeClass ("on");
    $("#yesno-btn-yes").addClass ("on");
    }
  }

function yn_right()
  {
  if (yn_cursor == 1)
    {
    yn_cursor = 2;
    $("#yesno-btn-yes").removeClass ("on");
    $("#yesno-btn-no").addClass ("on");
    }
  }

function yn_enter (button)
  {
  log ('yn_enter: ' + button);

  $("#yesno-layer").hide();
  thumbing = yn_saved_state;

  if (button == 1)
    eval (yn_ifyes);
  else if (button == 2)
    eval (yn_ifno);

  if (thumbing == 'yes-or-no')
    {
    notice_ok ('ipg', translations ['internal'] + ': Code 22', "");
    switch_to_ipg();
    }
  }

function rewind()
  {
  var offset = physical_offset();
  var duration = physical_length();

  log ('rewind, offset is ' + offset + ', duration is ' + duration);

  offset -= 10;

  if (offset < 0)
    offset = 0;

  log ('seeking ' + offset);
  physical_seek (offset);
  }

function fast_forward()
  {
  var offset = physical_offset();
  var duration = physical_length();

  log ('fast forward, offset is ' + offset + ', duration is ' + duration);

  offset += 10;

  if (offset > duration)
    offset = duration;

  log ('seeking ' + offset);
  physical_seek (offset);
  }

function state()
  {
  if (tube() == "yt")
    {
    yt_player_state();
    }
  else if (flowplayer)
    {
    log ('current player: ' + fp_player + ', preloaded: ' + fp_preloaded);
    fp_player_state ('player1');
    fp_player_state ('player2');
    }
  else
    log ('flowplayer is not active!');

  log ('layers :: fp1: ' + $("#fp1").css ("display") + '/z' + $("#fp1").css ("z-index") + ' ' + $("#fp1").css ("visibility") + ', fp2: ' + 
                           $("#fp2").css ("display") + '/z' + $("#fp2").css ("z-index") + ' ' + $("#fp2").css ("visibility") + ', yt1: ' +
                           $("#yt1").css ("display") + ' ' + $("#yt1").css ("visibility"));
  return '';
  }

function yt_player_state()
  {
  var yt_state = -2;
  var states = { '-2': 'fail', '-1': 'unstarted', '0': 'ended', '1': 'playing', '2': 'paused', '3': 'buffering', '5': 'cued' };

  try { yt_state = ytplayer.getPlayerState(); } catch (error) {};
  log ('youtube state: ' + states [yt_state]);
  }

function fp_player_state (player)
  {
  var fp_state = -2;

  var star = (player == fp_player ? '* ' : '  ') + player + ' ';
  var states = { '-2': 'none', '-1': 'unloaded', '0': 'loaded', '1': 'unstarted', '2': 'buffering', '3': 'playing', '4': 'paused', '5': 'ended' };

  try { fp_state = flowplayer (player).getState(); } catch (error) {};

  log (star + 'flowplayer state: ' + fp_state + ' == "' + states [fp_state] + '"');
  log (star + 'url: ' + fp [player]['file']);

  return ''
  }

function playerReady (thePlayer)
  {
  return;
  log ('jw player ready: ' + thePlayer.id);
  jwplayer = document.getElementById (thePlayer.id);
  jwplayer.sendEvent ('LOAD', 'nothing.flv');
  }

function noop (e)
  {
  log ('video mouse down');
  /* undo the pause damage done by flowplayer */
  // flowplayer (fp_player).pause();
  }









  $("#btn-smart-guide").click(function() {
	Reset();
	$("#sg-grid").removeClass("x3").addClass("x9");
  });
  
  $(".set-title").click(function() {
    $("#sg-grid").removeClass("x9").addClass("x3");
	set = $(".set-title").index(this);
	SetMove(set);
  });
  
  
  // Slide //
  
  CheckSet();
  
  function Reset() {
    set = 0;
	$("#slider").css("left","0");
	$("#next-set, #prev-set").hide();
  }
  
  function SetMove(set) {
    distance = "-" + 36*(set) + "em";
    $("#slider").animate({"left":distance}, 500, CheckSet);
    reset_set_cursor();
  }
  
  $("#next-set").click(function() {
	if (set < 8 ) {
	  set = set +1;
      SetMove(set);
	} 							
  });
  
  $("#prev-set").click(function() {
    if (set > 0 ) {
	  set = set - 1;
	  SetMove(set); 
	} 
  });
  
  function CheckSet() {
    if (set == 0) {
      $("#prev-set").hide();
      $("#next-set").show();
    } else if (set == 8) {
	  $("#prev-set").show();
	  $("#next-set").hide();
	} else {
	  $("#prev-set").show();
	  $("#next-set").show();
	}
	Pagination();
  }
  
  function Pagination() {
	p = $("#pagination li");
    $(p).removeClass("on");
	$(p[set]).addClass("on");
  }

function reset_set_cursor()
  {
  var top_left = top_lefts [set];

  var new_ipg_cursor = ipg_cursor;

  var inthebox = [0, 1, 2, 10, 11, 12, 20, 22, 23];

  for (var i in inthebox)
    if (parseInt (ipg_cursor) == top_left + inthebox [i])
      return;

  cursor_off (ipg_cursor);
  ipg_cursor = top_left;
  cursor_on (ipg_cursor);

  ipg_metainfo();
  ipg_sync();
  }
