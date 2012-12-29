/* TODO: track_without_piwik update to segments */
/* TODO: preload with segments */

/* players */

var current_tube = '';
var wmode = 'transparent';
var nofacebook = false;
var read_only;

/* main youtube player */
var yt_kickstart = false;
var yt_kickstart_playflag = false;
var yt_kickstart_id;
var tvpreview_kickstart = false;

var yt_timex = 0;
var yt_mini_timex = 0;
var fetch_yt_timex = 0;
var fetch_yt_callbacks = {};

/* preview player */
var mini_player;
var ytmini = [];
var ytmini_video_id = [];
var ytmini_why = [];
var ytmini_previous_state = [-1, -1, -1, -1];

var player_mute = false;
var player_mute_override;
var user_has_set_muting = false;
var ambient_volume;

var audio_player = 1;
var audio_stream = [];
var audio_ready = [];
var audio_callback = [];
var audio_url = [];

var fp_player = 'player1';
var fp_preloaded = '';
var fp_next = ''; /* preload for episode level */
var fp_next_timex = 0;
var start_preload = 0;

/* a list of volitionally selected channel id's */
var player_history_ids = [];

var shopping_cart = 0;

/* settings */
var preload = 'on';
var yt_quality = "medium";
var locale = 'en';

var errorthumb = "http://9x9ui.s3.amazonaws.com/images/default_channel.png";

var fp = {  player1: { file: '', duration: 0, timex: 0, mute: false, loaded: 0 },
            player2: { file: '', duration: 0, timex: 0, mute: false, loaded: 0 }  };

var fp_content = { url: 'http://9x9ui.s3.amazonaws.com/flowplayer.content-3.2.0.swf',
                   html: '', onClick: function() { $("#body").focus(); log ('FP CONTENT CLICK'); },
                   top: 0, left: 0, borderRadius: 0, padding: 0, height: '100%', width: '100%', opacity: 0 };

var language = 'en';
var sphere = 'en';
var sitename = '';
var who = []

var timezero = 0;
var all_programs_fetched = false;
var all_channels_fetched = false;
var piece_count = 0;
var activated = false;
var jingled = false;
var jingle_timex = 0;
var store_landing_jail = false;
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
var custom_but_is_now_logged_in = false;
var follow_set_id = '';
var follow_state = '';
var force_ipg_cursor = '';
var shared_but_is_now_logged_in = false;
var bandwidth_measurement = 0;
var bw_started = 0;
var user_closed_episode_bubble = false;
var fbtoken;
var last_captcha;
var suppress_success_dialog = false;

/* player data record */
var pdr = '';
var n_pdr = 0;

var current_program = '';
var current_channel = '';
var current_url = '';

var thumbing = '';
var dragging = false;
var after_confirm = '';
var after_confirm_function = '';

var do_this_after_fetch_channels;

var yn_cursor;
var yn_ifyes;
var yn_ifno;
var yn_saved_state;

var channelgrid = {};
var set_titles = {};
var set_ids = {};
var set_types = {};
var set = 0;

/* cache kept by language:categoryid */
var store_cache = {};

var pool = {};
var set_pool = {};

var programgrid = {};
var program_line = [];
var n_program_line = 0;
var program_cursor = 1;
var program_first = 1;

var ipg_cursor;
var last_sent_ipg_cursor;
var ipg_entry_channel;
var ipg_entry_program;
var ipg_saved_cursor;
var ipg_timex = 0;
var ipg_delayed_stop_timex = 0;
var ipg_preload_timex = 0;
var ipg_mode = '';

/* top left grid position of all clusters */
var top_lefts = [11, 14, 17, 41, 44, 47, 71, 74, 77];

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
var max_browse = 18;
var n_browse = 0;
var saved_thumbing = '';
var browse_cursor = 1;
var max_programs_in_line = 9;
var cat_query;
var pending_queries = [];
var categories_by_id = {};

/* workaround for Chrome not firing 'ended' video event */
var fake_timex = 0;

/* timeout for msg-layer */
var msg_timex = 0;
var yt_error_timex = 0;

var dirty_delay;
var dirty_channels = [];
var dirty_timex;

var user = '';
var username = '';
var userid;
var curatorid;
var lastlogin = '';
var first_time_user = 0;
var device_id;
var known_users = [];
var iam;

var rez9x9 = { 'hd1080': '1080p', 'hd720': '720p', 'large': '480p', 'medium': '360p', 'small': '240p' };

var fpstates = { '-1': 'unloaded', 0: 'loaded', 1: 'unstarted', 2: 'buffering', 3: 'playing', 4: 'paused', 5: 'ended' };

/* if we entered via a share, and have not upconverted */
var via_share = false;
var via_share_path = '';

/* to allow things to be displayed before we are ready */
var the_very_first_time = true;

var root = 'http://9x9ui.s3.amazonaws.com/9x9playerV68a/images/';
//var nroot = 'http://9x9ui.s3.amazonaws.com/9x9playerV104/images/';
var nroot = 'http://9x9ui.s3.amazonaws.com/mock40/images/';
var no_thumbnail_image = 'http://9x9ui.s3.amazonaws.com/no-thumbnail-image.jpg';

var language_en =
  {
  hello: 'Hello',
  signin: 'Sign In / Sign Up',
  signin_only: 'Sign In',
  signup_only: 'Sign Up',
  signout: 'Sign Out',
  resume: 'Resume Watching',
  episode: 'Episode',
  episodes: 'Episodes',
  episode_lc: 'episode',
  episodes_lc: 'episodes',
  episodeunits: 'episodes',
  epdesc: 'Episode Description',
  curcom: "Curator's Comment",
  channel: 'Channel',
  channels: ' Channels',
  channel_lc: 'channel',
  channels_lc: 'channels',
  nchannel: 'Channel',
  nchannels: 'Channels',
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
  tooken: 'This email has been used',
  suberr: 'Cannot add the channel to Smart Guide now',
  syschan: 'System channel cannot be deleted',
  deletethis: 'Do you want to delete this channel?',
  nochansquare: 'There is no channel in this spot.',
  internal: 'An internal error has occurred',
  playthis: 'Play this channel now?',
  sharing: 'You will be sharing this channel and episode with your Facebook friends. Continue?',
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
  nmo: 'No more episodes',
  close: 'Close',
  oneempty: 'You still have one empty channel',
  noempty: 'You have no empty channels!',
  empties: 'You still have %1 empty channels',
  aboutus: 'About Us',
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
  pva: 'Your Personal Channel Browser',
  fad: 'Flip and Discover',
  moresets: 'More Sets',
  addset: 'Add This Set',
  follow: 'Follow These Channels',
  followhint: 'Click to receive the latest episodes from these channels',
  curchan: 'Current Channel',
  plzreg: 'Please register to use this feature.',
  badlanding: 'The URL you have landed on is incorrect. Please visit our SmartGuide.',
  toastfollow: 'Follow this channel?',
  _edit: 'Edit',
  done: 'Done',
  addmorech: 'Add More Channels',
  setnum: 'Set %d',
  cannotshare: 'Cannot share this type of channel',
  alreadysmart: 'You are already in the Smart Guide',
  deleteset: 'Do you want to delete this set?',
  liveedit: 'Cannot edit a live set',
  followthis: 'Follow This Channel',
  emptyset: 'There is no set here',
  allfull: 'No place for a new channel!',
  newtitle: 'Enter a new title for the set',
  hi: 'Hi, %1',
  store: 'Store',
  guide: 'Guide',
  player: 'Player',
  curator: 'Curator',
  curators: 'Curators',
  ncurator: 'Curator',
  ncurators: 'Curators',
  help: 'Help',
  search: 'Search',
  company: 'Company',
  partner: 'Partner',
  blog: 'Blog',
  free: 'FREE',
  watchmore: 'Click here to watch more episodes in this channel',
  showepisodes: 'Show episodes',
  hidemore: 'Hide',
  channelstore: 'Channel Store',
  recommended: 'Recommended Sets',
  directory: 'Directory',
  addyourown: 'Add Your Own',
  noaccept: "We don't accept URLs for single videos",
  settings: 'Settings',
  sh_general: 'General',
  sh_sharing: 'Sharing',
  sh_preload: 'Preload',
  sh_resolution: 'Resolution',
  gender: 'Gender',
  birthyear: 'Birth Year',
  language: 'Language',
  _region: 'Region',
  vquality: "Set 9x9's default playback resolution at",
  tips: 'Tips',
  tip1: 'Drag and drop channel icons to arrange channels',
  tip2: 'Move cursor near the top-left corner of channel icons to delete channels',
  tip3: 'Move cursor over channel icons to see channel descriptions',
  chcount: 'channels in your 9x9 Guide',
  backtostore: 'Back to Store',
  previewing: 'Previewing %1 channel in %2 set',
  pvc: 'Previewing Channel',
  pv1: 'Previewing',
  pv2: 'channel in',
  pv3: 'set',
  watching: 'You are watching %1 channel',
  prevch: 'Prev Ch',
  nextch: 'Next Ch',
  tt_sort: 'Sort',
  tt_share: 'Share',
  tt_sync: 'Sync to 10-Foot-Player',
  tt_fs: 'Full Screen',
  tt_chinfo: 'Channel Info',
  tt_soundon: 'Sound On',
  tt_soundoff: 'Sound Off',
  s_9x9id: 'Your email',
  s_forgot: 'Forgot your password?',
  s_create: 'Create an Account',
  s_pitch0: 'New to 9x9?',
  s_pitch1: 'Tired of searching for videos? Access thousands of curated channels and create your very own programming guide!',
  s_pitch2: "It's free and easy!",
  s_minpw: '6-character minimum',
  s_retype: 'Retype Password',
  s_male: 'Male',
  s_female: 'Female',
  s_captcha1: 'Word Verification',
  s_captcha2: 'Type the characters in the picture below',
  s_disclaim1: 'Clicking I accept means that you agree to the 9x9 service agreement and privacy statement.',
  s_disclaim2: 'You also agree to receive email from 9x9 with service updates, special offers, and survey invitations.',
  s_disclaim3: 'You can unsubscribe at any time.',
  forum: 'Forum',
  addyt: 'Add YouTube Channels to Your Guide',
  enterurl: 'Enter YouTube Channel or Playlist URL:',
  addthischannel: 'Add This Channel',
  tutorial: 'New User Tutorial',
  faq: 'FAQ',
  diagnostics: 'Diagnostics',
  contactus: 'Contact Us',
  legal: 'Legal',
  problems: 'Report a bug',
  problemo: "Did you encounter a problem? Please describe the problem and we'll fix it!",
  t_general: 'General',
  t_preload: 'Preload',
  t_rez: 'Resolution',
  addsucc: 'Added successfully!',
  resfound1: 'channel found',
  resfound2: 'channels found',
  iaccept: 'I accept',
  s_example: 'Example',
  b_watchnow: 'Watch now',
  b_guide: 'Go to guide',
  b_store: 'Stay in store',
  b_emailmore: 'Email more friends',
  nodesc: 'No description available',
  renameset: 'You can change the name of this 3x3 channel set',
  success: 'Success!',
  signed1: 'Welcome to 9x9! Now you can access plenitudes of curated channels!',
  signed2: 'Browse our Channel Store, add new channels to your personalized progamming guide and watch your favorite channels just like how you would on a TV!',
  b_cont: 'Continue',
  b_startadding: 'Start adding channels',
  changessaved: 'Changes saved',
  errupdate: 'Error updating your settings',
  nochanges: "You haven't changed anything",
  enteremail: 'Enter email address',
  entermessage: 'Enter message',
  preload0: "I want to set 9x9's preloading capacity to:",
  preload1: 'Turn off smooth channel flipping for faster buffering',
  preload2: 'Turn on smooth channel flipping for faster browsing',
  promo1: '<span>Flip through the channels and add your favorites to your Guide!</span>',
  promo2: '<span>Official 9x9 application hits the App Store!</span><img src="' + nroot + 'app_store_badge.png" id="app-store">',
  save: 'Save',
  uploaded: 'Uploaded',
  epsortby: 'Episodes sort by:',
  fromnewest: 'From Newest to Oldest',
  fromleftoff: 'From Left off to Newest',
  conferr: 'Error saving your configuration',
  nochanguide: 'No channels in your guide!',
  badcaptcha: 'Oops! Looks like the verification word was entered incorrectly. Please try again.',
  badpass: "Email and password don't match, please try again",
  alreadyin: 'This channel is already in your Guide',
  mustlogin: 'You must be logged in to use this feature',
  trynextep: 'Try next episode',
  nextchan: 'Next channel',
  readonly: 'The system is under maintenance, and has limited functionality. Please try again later.',

  home: 'Home',
  guide: 'Guide',
  browse: 'Browse',
  search: 'Search',
  signinup: 'Sign in/up',
  mypage: 'My page',
  studio: 'Studio',
  settings: 'Settings',
  logout: 'Logout',
  featcur: 'Featured Curators',
  language: 'Language',
  region: 'Region',
  trending: 'Trending',
  trendstories: 'Trending Stories',
  recommended: 'Recommended',
  youmaylike: 'You May Like',
  featured: 'Featured',
  hottest: 'Hottest',
  follow: 'Follow',
  followthischannel: 'Follow this channel',
  unfollow: 'Unfollow',
  location: 'Location',
  favorite: 'Favorite',
  unfavorite: 'Unfavorite',
  imfollowing: "I'm Following",
  isfollowing: 'Channels %1 is following',
  followbychannel: "Sorted by channel number",
  followbyupdate: "Sorted by update time",
  mostpop: 'The most popular tags:',
  sortedby: 'Sorted by:',
  updatetime: 'Update Time',
  mostsub: 'Most Subscribed',
  alphabetical: 'Alphabetical',
  yoursub: 'Your Subscriptions',
  view: 'View',
  views: 'Views',
  follower: 'Follower',
  followers: 'Followers',
  morerec: 'More Recommendations',
  curatorby: 'by',
  curatorfrom: 'From',
  curatoron: 'on',
  clipfrom: 'From',
  taiwan: 'Taiwan',
  usa: 'USA',
  email: 'E-mail',
  password: 'Password',
  repeatpassword: 'Repeat Password',
  yourname: 'Your Name',
  createmyaccount: 'Create My Account',
  thiswillbe: 'This will be your name as other 9x9 users see you.',
  signfb: 'Sign in with Facebook',
  agreement: 'I have read and accepted the User Agreement and Privacy Policy',
  createanewchannel: 'Create a<br>new channel',
  edit: 'Edit',
  prev: 'Prev',
  next: 'Next',
  resultsfor: 'Results for:',
  mytopchannel: 'My Top Channel',
  settingsabout: 'About',
  uploadimage: 'Upload',
  aboutimage: 'Image',
  changepassword: 'Change Password',
  saveprofile: 'Save Profile',
  a_aboutus: 'About us',
  a_help: 'Help',
  a_report: 'Feedback',
  a_termspolicy: 'Terms & Policy',
  a_contactus: 'Contact us',
  a_partners: 'Partners',
  a_curators: 'Curators',
  a_press: 'Press',
  a_contest: 'Curation Contest',
  full: 'Your guide is already full. Please delete channels to add this one.',
  oldpassword: 'Old Password',
  newpassword: 'New Password',
  newpasswordverify: 'Repeat New Password',
  cancel: 'Cancel',
  followingme: 'Following Me',
  mypageurl: "My Page's URL",
  plzaccept: 'Please accept the agreements below.',
  addfav: 'Added to your Favorites',
  remfav: 'Removed from your Favorites',
  hasbeenadded: 'This channel has been added to your Guide.',
  hasbeenremoved: 'This channel has been removed from your Guide.',
  myc: 'Manage your channels',
  nowplaying: 'Now Playing',
  usersfav: "%1's Favorite"
  };

var language_tw =
  {
  hello: 'Hello',
  signin: '登入 / 註冊',
  signin_only: '登入',
  signup_only: '註冊',
  signout: '登出',
  resume: '繼續上次觀看',
  _episode: '節目集數',
  _episodes: '節目集數',
  episode: '個節目',
  episodes: '個節目',
  _episode_lc: '節目',
  _episodes_lc: '節目',
  episode_lc: '個節目',
  episodes_lc: '個節目',
  episodeunits: '個節目',
  epdesc: 'Episode Description',
  curcom: "Curator's Comment",
  channel: '頻道',
  channels: '頻道',
  channel_lc: '頻道',
  channels_lc: '頻道',
  nchannel: '個頻道',
  nchannels: '個頻道',
  sub_lc: '訂閱人數',
  subs_lc: '訂閱人數',
  updated: '更新時間',
  onemoment: '稍待片刻...',
  buffering: '載入中...',
  contribute: '貼上YouTube頻道/播放清單URL',
  returningusers: '登入',
  name: '暱稱',
  password: '密碼',
  passverify: '確認密碼',
  email: 'Email',
  login: '登入',
  deletetip: '選擇刪除頻道',
  findel: '結束刪除',
  nochandel: '已刪除所有頻道',
  errormov: '無法搬移，請稍後再試',
  notplaying: '無節目可播放',
  noepchan: '頻道無節目',
  thanx: '已登出，記得常回來9x9.tv！',
  logfail: '登入失敗',
  validmail: '請輸入正確的Email帳號',
  _passmatch: '輸入的密碼需相同',
  passmatch: '輸入密碼不一致，請重新輸入',
  sixchar: '密碼長度至少6個字元',
  signupfail: '註冊失敗，請再試一次',
  tooken: '此信箱已被使用',
  suberr: '無法訂閱，請稍候再試',
  syschan: '系統頻道不可刪除',
  deletethis: '確定刪除此頻道？',
  nochansquare: '空格中沒有頻道',
  internal: '內部錯誤，請重新嘗試',
  playthis: '立刻播放此頻道？',
  sharing: '確定分享您的頻道表至Facebook塗鴉牆？',
  noeps: '頻道無節目，請觀看其它頻道',
  addtip: '從空格可進入頻道商店訂閱新頻道',
  signuptip: '註冊後，您的頻道表將被妥善儲存',
  signouttip: '登出',
  startdel: '點擊並開始刪除頻道',
  returnipg: '返回上次觀看頻道',
  browsetip: '瀏覽並訂閱新頻道',
  addrss: '找不到喜歡的頻道嗎？貼上您喜愛的YouTube頻道/播放清單URL以訂閱更多！',
  returnsmart: '回我的頻道表',
  rsbubble: '回我的頻道表',
  copypaste: '請貼上YouTube頻道/播放清單URL ',
  threecat: '請選擇一到三個分類',
  processing: '系統處理中',
  browsecats: '瀏覽並訂閱新頻道',
  watchnow: '立即觀看',
  addsmart: '訂閱至我的頻道表',
  enterwatch: '立即觀看，或繼續瀏覽',
  suredel: '確定刪除頻道？',
  havedel: '頻道已刪除',
  delmore: '繼續刪除頻道',
  chandir: '頻道網菜單',
  delchan: '刪除頻道',
  subscribed: '已訂閱',
  entersub: '訂閱',
  addchannel: '訂閱頻道',
  needcat: '選擇分類',
  needurl: '輸入URL',
  pleasewait: '請稍候…',
  addrssyt: '訂閱YouTube頻道',
  category: '頻道分類',
  go: '送出',
  succpress: '訂閱成功！按"Enter"鍵開始觀看',
  hinstr: '操作提示',
  hwbsg: '瀏覽頻道表時',
  hwwe: '觀看影片時',
  hctw: '關閉視窗',
  huak: '使用鍵盤或滑鼠操作',
  hpec: '播放頻道或訂閱更多',
  hscp: '顯示控制列',
  hshow: '顯示節目列',
  qyes: '是',
  qno: '否',
  cup: '按 <span class="enlarge">&uarr;</span> 瀏覽我的頻道表',
  cdown: '按 <span class="enlarge">&darr;</span> 觀看更多節目',
  chcat: '頻道分類', 
  ytvid: '此節目已從來源移除',
  eoe: '頻道已播映完畢',
  nmo: '頻道已播映完畢',
  close: '關閉',
  oneempty: '您只剩一個頻道空格。',
  noempty: '您的頻道表已滿，刪除頻道後才能訂閱',
  empties: '您還剩 1% 個頻道空格',
  _aboutus: '關於9x9',
  aboutus: '關於我們',
  newusers: '註冊',
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
  expired: '此節目已不存在。',
  cinstr: '游標至此展開節目列表',
  creplay: '重新播放',
  crw: '回轉',
  cplay: '播放',
  cpause: '暫停',
  cff: '快轉',
  cipg: '回我的頻道表',
  cfb: '分享至Facebook',
  cvolup: '音量大',
  cvoldown: '音量小',
  cmute: '靜音',
  cunmute: '恢復音量',
  uncaught: '系統有誤，請稍後再試。',
  alreadysub: '您已訂閱過此頻道',
  pva: '個人影音瀏覽器',
  fad: 'Flip and Discover',
  moresets: '更多頻道網',
  addset: '訂閱頻道網',
  _follow: '訂閱所有頻道',
  followhint: '點擊收看所有頻道的最新節目',
  curchan: '目前頻道',
  featchan: '精選頻道',
  plzreg: '登入後才能使用此功能',
  badlanding: '您進入的網址有誤，請回首頁繼續瀏覽',
  help: '幫助',
  toastfollow: '訂閱頻道？',
  _edit: '編輯',
  done: '完成',
  addmorech: '訂閱更多',
  setnum: '頻道網',
  cannotshare: '此服務暫不提供',
  alreadysmart: '您已位於頻道表',
  deleteset: '確定移除此頻道網？',
  liveedit: 'Cannot edit a live set',
  followthis: '訂閱頻道',
  emptyset: '此無頻道網',
  allfull: 'Guide已滿，無法訂閱新頻道！',
  newtitle: '重新命名此頻道網',
  hi: 'Hi, %1',
  store: '頻道商店',
  guide: '我的頻道',
  player: '頻道瀏覽器',
  curator: '策展人',
  curators: '策展人',
  ncurator: '個策展人',
  ncurators: '個策展人',
  help: '幫助',
  search: '搜尋',
  company: '關於9x9',
  spartner: '合作夥伴',
  blog: '部落格',
  free: 'FREE',
  watchmore: '觀看更多此頻道的節目',
  showepisodes: 'Show episodes',
  hidemore: '隱藏',
  channelstore: '頻道商店',
  recommended: '精選頻道網',
  directory: '頻道網菜單',
  addyourown: '訂閱YouTube',
  noaccept: '無法訂閱單一影片的URL',
  settings: '設定',
  sh_general: '一般設定',
  sh_sharing: '活動分享',
  sh_preload: '預載節目',
  sh_resolution: '解析度',
  gender: '性別',
  birthyear: '出生年',
  language: '語言',
  _region: '地區',
  vquality: '預設播放解析度為',
  tips: '小提示',
  tip1: '拖曳圖示即可搬移頻道',
  tip2: '滑鼠移到頻道圖示左上角可刪除頻道',
  tip3: '滑鼠移到頻道圖示上可觀看頻道資訊',
  chcount: '個頻道已訂閱',
  backtostore: '回頻道商店',
  previewing: '正在預覽 %1 頻道於 %2 頻道網',
  pvc: '預覽頻道',
  pv1: '正在預覽',
  pv2: '頻道於',
  pv3: '頻道網',
  watching: '您正在收看 %1 頻道',
  prevch: '上個頻道',
  nextch: '下個頻道',
  tt_sort: '節目排序',
  tt_share: '分享',
  tt_sync: '同步至電視版瀏覽器',
  tt_fs: '全螢幕',
  tt_chinfo: '頻道資訊',
  tt_soundon: '播放聲音',
  tt_soundoff: '靜音',
  s_9x9id: '您的帳號(email)',
  s_forgot: '忘記密碼?',
  s_create: '建立帳戶',
  s_pitch0: '9x9新手?',
  s_pitch1: '厭倦在網路上到處搜尋好看影片嗎？立刻瀏覽上千精心編輯的影視頻道，並建立您個人的雲端電視頻道表！',
  s_pitch2: '現在免費而且超簡單！',
  s_minpw: '請輸入至少6位數',
  s_retype: '請再次輸入密碼',
  s_male: '男性',
  s_female: '女性',
  s_captcha1: '字詞驗證',
  s_captcha2: '請輸入下圖顯示的字詞',
  s_disclaim1: '當您按下[同意]時即表示您已同意9x9之服務條款與隱私權政策。',
  s_disclaim2: '您同時也訂閱了9x9關於服務更新、特別獻禮，以及用戶調查邀請等電子報， ',
  s_disclaim3: '但您可以隨時自行取消訂閱。',
  forum: '討論區',
  addyt: '訂閱YouTube頻道',
  enterurl: '貼上YouTube頻道/播放清單URL:',
  addthischannel: '訂閱頻道',
  tutorial: '新手指南',
  faq: '常見問題',
  diagnostics: '問題診斷',
  contactus: '聯絡我們',
  legal: '服務條款',
  problems: '回報問題',
  problemo: "請在此描述您的問題，我們將盡快改善。",
  t_general: '一般設定',
  t_preload: '預載節目',
  t_rez: '解析度',
  addsucc: '新增成功!',
  resfound1: '個頻道',
  resfound2: '個頻道',
  iaccept: '同意',
  s_example: '範例',
  b_watchnow: '立刻觀看',
  b_guide: '我的頻道表',
  b_store: '頻道商店',
  b_emailmore: 'Email更多朋友',
  nodesc: '無頻道介紹',
  renameset: '編輯此3x3頻道網的名稱',
  success: '註冊完成！',
  signed1: '歡迎使用9x9！您現在可以享受千百個精選影視頻道。',
  signed2: '立刻瀏覽頻道商店，訂閱您最喜愛的頻道，並像看電視般的欣賞您的個人雲端頻道表！',
  b_cont: '繼續',
  b_startadding: '開始訂閱頻道',
  changessaved: '變更已儲存',
  nochanges: "您沒有變更任何資料",
  enteremail: '請輸入Email',
  entermessage: '請輸入訊息',
  preload0: "設定預先載入下個節目，提高快速瀏覽多個節目的順暢度：",
  preload1: '關閉預載功能(無法快速瀏覽節目，但正在觀看節目的播放速度較佳)',
  preload2: '開啟預載功能(可快速瀏覽節目，但正在觀看節目的播放速度較慢)',
  promo1: '<span>輕鬆瀏覽您喜愛的頻道，並訂閱至您的個人頻道表中。</span>',
  promo2: '<span>Official 9x9 application hits the App Store!</span><img src="' + nroot + 'app_store_badge.png" id="app-store">',
  save: '儲存',
  uploaded: '更新時間',
  epsortby: '節目排序：',
  fromnewest: '最新到最舊',
  fromleftoff: '上次觀看到最新',
  conferr: 'Error saving your configuration',
  nochanguide: 'No channels in your guide!',
  badcaptcha: 'Oops! Looks like the verification word was entered incorrectly. Please try again.',
  badpass: '信箱或密碼輸入不正確，請重新輸入',
  alreadyin: '這個頻道已經在你的頻道表中',
  mustlogin: 'You must be logged in to use this feature',
  trynextep: '看下一個節目',
  nextchan: '換另一個頻道',
  readonly: '系統正在維護中,因此只開放少數的功能,請稍後再試',

  home: '首頁',
  guide: '頻道表',
  browse: '探索',
  search: '搜尋',
  signinup: '登入/註冊',
  mypage: '我的頁面',
  studio: '編輯台',
  settings: '設定',
  logout: '登出',
  featcur: '精選策展人',
  language: '語言',
  region: '區域',
  trending: '趨勢',
  trendstories: '影音趨勢',
  recommended: '推薦頻道',
  youmaylike: '為您推薦',
  featured: '精選頻道',
  hottest: '熱門頻道',
  follow: '訂閱',
  followthischannel: '訂閱',
  unfollow: '取消',
  location: '所在地',
  _favorite: '喜愛,愛好',
  favorite: '喜歡',
  unfavorite: '取消喜歡',
  _imfollowing: '已訂閱(頻道)',
  imfollowing: '我的訂閱',
  isfollowing: '我的訂閱',
  followbychannel: "定頻模式",
  followbyupdate: "動態模式",
  mostpop: '熱門標籤',
  sortedby: '排序：',
  updatetime: '更新時間',
  mostsub: '最多訂閱',
  alphabetical: '字母順序',
  yoursub: '您訂閱的頻道',
  view: '觀看次數',
  views: '觀看次數',
  follower: '個粉絲',
  followers: '個粉絲',
  morerec: '更多推薦頻道',
  curatorby: '來自',
  curatorfrom: '來自',
  curatoron: '於',
  clipfrom: '上傳的',
  taiwan: '台灣',
  usa: '美國',
  email: '信箱',
  password: '密碼',
  repeatpassword: '再次輸入密碼',
  _yourname: '你的姓名',
  yourname: '用戶名稱',
  createmyaccount: '送出',
  _thiswillbe: '這將是9x9的其他用戶看到你的用戶名。',
  thiswillbe: '創建新用戶名稱',
  signfb: '使用FaceBook登入',
  agreement: '我已讀完並同意遵守隱私權政策與使用條款',
  createanewchannel: '創建頻道',
  edit: '管理',
  prev: '上頁',
  next: '下頁',
  resultsfor: '的搜尋結果',
  mytopchannel: '主打頻道',
  settingsabout: '關於我',
  uploadimage: '上傳照片',
  upload: '上傳',
  aboutimage: '照片',
  changepassword: '更改密碼',
  saveprofile: '儲存',
  a_aboutus: '關於我們',
  a_help: '使用說明',
  a_report: '問題回報',
  a_termspolicy: '政策條款',
  a_contactus: '聯繫我們',
  a_partners: '合作夥伴',
  a_curators: '創建頻道',
  a_press: '活動紀錄',
  a_contest: '頻道策展大賽',
  full: '你的頻道表已經滿了,請刪除現存頻道以加入新頻道',
  oldpassword: '舊密碼',
  newpassword: '新密碼',
  newpasswordverify: '再次輸入新密碼',
  cancel: '取消',
  followingme: '個粉絲',
  mypageurl: '專頁連結',
  plzaccept: '請接受以下的隱私權政策與使用條款',
  addfav: '此頻道已經添加到您喜歡的頻道表',
  remfav: '此頻道已從您喜歡的頻道表中刪除',
  hasbeenadded: '此頻道已經添加到您的頻道表',
  hasbeenremoved: '此頻道已從您的頻道表中刪除',
  myc: '管理你的頻道',
  nowplaying: '正在播放',
  usersfav: "%1 喜歡的頻道"
  };

var translations = language_en;

var category_en_to_zh =
  {
  'All': '全部',
  'Animals & Pets': '寵物與動物',
  'Art & Design': '藝術設計',
  'Autos & Vehicles': '動力機械',
  'Cartoons & Animation': '卡通動畫',
  'Comedy': '搞笑小品',
  'Fashion, Food & Living': '時尚美食與生活',
  'Gaming': '電玩遊戲',
  'How-To': 'DIY教學',
  'Education & Lectures': '教育講座',
  'Music': '音樂',
  'News': '新聞',
  'Nonprofits & Faith': '宗教與非營利',
  'People, Blogs & Shorts': '人物與網誌',
  'Science': '科學知識',
  'Sports & Health': '運動與健康',
  'Tech & Apps': '科技3C',
  'TV & Film': '電視與電影',
  'Others': '其他'
   };

var category_zh_to_en = {};

var landing_pages = {};

$(document).ready (function()
 {
 var now = new Date();
 timezero = now.getTime();
 log ('begin execution');
 init();
 pre_login();
 $(window).resize (function() { elastic(); after_elastic(); });
 });

function elastic()
  {
  log ('elastic');
  elastic_innards();
  }

function elastic_innards()
  {
  var newWidth = $(window).width() / 16 ;
  var oriWidth = 64;
  var xtimes = newWidth / oriWidth * 100;

  var newHeight = $(window).height() / 16;
  var oriHeight = 39;
  var ytimes = newHeight / oriHeight * 100;
	
  var times = (xtimes >= ytimes) ? (ytimes + "%") : (xtimes + "%");
  $("body").css("font-size", times);

  if (thumbing == 'player')
    {
    if (player_size == 'fullscreen')
      player_fullscreen();
    else if (player_size == 'minimize')
      player_minimize();
    }

  adjust_sliders();
  }

/* these are executed ONLY if a .resize was called, and not merely elastic(). Since redraw_layer_if_possible
   may contain some very expensive operations, call it at most once per second */

var elastic_timex;

function after_elastic()
  {
  clearTimeout (elastic_timex);
  elastic_timex = setTimeout ("redraw_layer_if_possible()", 1000);
  }

function redraw_layer_if_possible()
  {
  if (thumbing == 'home')
    home();
  else if (thumbing == 'curator')
    curation (current_curator_page);
  else if (thumbing == 'about')
    developer ($("#developer-menu li h1.on").parent().attr("data-doc"));
  else if (thumbing == 'guide')
    guide();
  else if (thumbing == 'browse')
    browse_category (current_browse_index);
  else if (thumbing == 'search')
    perform_search (last_search_term);
  }

function adjust_sliders()
  {
  if (thumbing == 'browse')
    {
    $("#browse-constrain").css ({ height: $(window).height() - $("#browse-main").offset().top });
    $("#browse-slider, #browse-constrain").css ({ height: $("#browse-constrain").height() - 15 });
    }
  }

function OLD_elastic_innards()
  {
  if (player_size == 'fullscreen')
    player_fullscreen();
  else if (player_size == 'minimize')
    player_minimize();
  return;

  var newWidth  = $(window).width()  / 16;
  var newHeight = $(window).height() / 16;

  var xtimes = newWidth  / 64 * 100;
  var ytimes = newHeight / 36 * 100;

  $("body").css ("font-size", ((xtimes >= ytimes) ? ytimes : xtimes) + "%");

  var vh = $(window).height();
  var vw = $(window).width();

  // var h = document.getElementById ("yt1");
  // h.style.width = vw + "px";

  if ($.browser.msie)
    $("#sg-layer, #ch-directory, #msg-layer, #confirm-layer, #yesno-layer, #delete-layer, #opening, #success-layer").css ({"width": vw, "height": vh});

  episode_clicks_and_hovers();

  if (current_tube == 'ss')
    redraw_slideshow_html();

  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    if (store_tab == 'recommended')
      scrollbar ("#recommended-content", "#recommended-list", "#recommended-slider");
    else if (store_tab == 'category')
      scrollbar ("#category-content", "#category-list", "#category-slider");
    }

  flip_bubble_pos();
  if (thumbing == 'store' || thumbing == 'store-wait')
    calculate_flip_bubble_offsets();
  }

function set_language (lang)
  {
  language = lang;
  yt_submit_language = lang;
  store_language = lang;

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
  $("#waiting-layer p").html (translations ['onemoment']);
  $("#pool-waiting p span").html (translations ['onemoment']);
  $("#preview-waiting p span").html (translations ['onemoment']);
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
  $("#slogan span").html (translations ['fad']);
  $("#email-holder li:nth-child(3) span:nth-child(1)").html (translations ['s_captcha1'])
  $("#preview-promo p span").html (translations ['promo1']);
  $("#btn-confirm-close span").html (translations ['close']);

  $("#btn-company span").html (translations ['company']);
  $("#btn-blog span").html (translations ['blog']);
  $("#btn-forum span").html (translations ['forum']);
  $("#store span").html (translations ['store']);
  $("#guide span").eq(0).html (translations ['guide']);
  $("#player span").html (translations ['player']);
  $("#curate span").html (translations ['curator']);
  $("#help span").html (translations ['help']);
  $("#ep-show").html (translations ['showepisodes']);
  $("#store-holder h2 span").html (translations ['channelstore']);
  // $("#recommended span").html (translations ['recommended']);
  $("#category span").html (translations ['directory']);
  $("#yourown span").html (translations ['addyourown']);
  $("#yt-note span").html ("* " + translations ['noaccept']);
  $("#btn-add-yt span").html (translations ['addthischannel']);
  $("#btn-player2store span").html ('&' + 'lt; ' + translations ['backtostore']);
  $("#btn-guide2store span").html ('&' + 'lt; ' + translations ['backtostore']);
  $("#play-next span").html (translations ['nextch']);
  $("#play-prev span").html (translations ['prevch']);
  $("#player-ep-meta .meta-head").html (translations ['episode'] + ':');
  // $("#preview-ep-meta .meta-head").html (translations ['episode'] + ':');
  $("#channel-info .head span").html (translations ['curchan'] + ':');
  $("#channel-info .ch-epNum span:nth-child(1)").html (translations ['episode'] + ':');
  $("#channel-info .ch-epNum span:nth-child(2)").html (translations ['updated'] + ':');
  $("#channel-info .ch-updated span:nth-child(1)").html (translations ['updated'] + ':');
  $("#guide-index span:nth-child(2)").html (translations ['chcount']);
  $("#guide-tip .head").html (translations ['tips'] + ':');
  $("#guide-tip span:nth-child(3)").html (translations ['tip1']);
  $("#btn-share img:nth-child(1)").attr ("title", translations ['tt_share']);
  $("#btn-sort img:nth-child(1)").attr ("title", translations ['tt_sort']);
  $("#btn-sync img:nth-child(1)").attr ("title", translations ['tt_sync']);
  $("#btn-full img").attr ("title", translations ['tt_fs']);
  $("#entry-switcher p:nth-child(1) span").html (translations ['s_pitch0']);
  $("#entry-switcher p:nth-child(2) span").html (translations ['s_pitch1']);
  $("#entry-switcher p:nth-child(3) span").html (translations ['s_pitch2'])
  $("#btn-create-account span").html (translations ['s_create']);
  $("#btn-settings span").html (translations ['settings']);
  $("#btn-signout span").html (translations ['signout']);
  $("#btn-sign span").html (translations ['signin']);
  $("#general span").html (translations ['t_general']);
  $("#preload span").html (translations ['t_preload']);
  $("#resolution span").html (translations ['t_rez']);
  $("#report span").html (translations ['problems']);
  $("#addsucc").html (translations ['addsucc']);
  $("#yt-intro span").html (translations ['enterurl']);
  $("#yt-note span").html ('* ' + translations ['noaccept']);
  $("#yourown-content h3 span").html (translations ['addyt']);
  $("#signin-panel .input-list li:nth-child(1) span").html (translations ['s_9x9id'] + ':')
  $("#signin-panel .input-list li:nth-child(2) span").html (translations ['password'] + ':')
  $("#signin span").html (translations ['returningusers']);
  $("#signup span").html (translations ['newusers']);
  $("#btn-signin span").html (translations ['signin_only']);
  $("#signup-panel .input-list li:nth-child(1) span").html (translations ['email'] + ':');
  $("#signup-panel .input-list li:nth-child(2) span:nth-child(1)").html (translations ['password'] + ':');
  $("#signup-panel .input-list li:nth-child(3) span").html (translations ['s_retype'] + ':');
  $("#signup-panel .input-list li:nth-child(4) span").html (translations ['name'] + ':');
  $("#signup-panel .hint").html (translations ['s_minpw']);
  $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(1) span").html (translations ['gender'] + ':');
  $("#signup-panel .input-list-right li:nth-child(1) .radio-item:nth-child(2) span").html (translations ['s_male']);
  $("#signup-panel .input-list-right li:nth-child(1) .radio-item:nth-child(3) span").html (translations ['s_female']);
  $("#birth-input span").html (translations ['birthyear'] + ':');
  $("#btn-signup span").html (translations ['iaccept']);
  $("#rename-holder .instruction span").html (translations ['renameset']);
  $("#preload-panel .head span").html (translations ['preload0']);
  $("#preload-off .explanation").html (translations ['preload1']);
  $("#preload-on .explanation").html (translations ['preload2']);
  $("#general-panel .input-list li:nth-child(1) .head span").html (translations ['name'] + ':');
  $("#general-panel .input-list li:nth-child(2) .head span").html (translations ['email'] + ':');
  $("#general-panel .input-list li:nth-child(3) .head span").html (translations ['password'] + ':');
  $("#general-panel .input-list li:nth-child(4) .head span").html (translations ['gender'] + ':');
  $("#general-panel .input-list li:nth-child(5) .head span").html (translations ['birthyear'] + ':');
  $("#general-panel .input-list li:nth-child(6) .head span").html (translations ['language'] + ':');
  $("#general-panel .input-list li:nth-child(7) .head span").html (translations ['region'] + ':');
  $("#resolution-panel .input-list .head span").html (translations ['vquality'] + ':');
  $("#btn-general-save span").html (translations ['save']);
  $("#btn-preload-save span").html (translations ['save']);
  $("#btn-resolution-save span").html (translations ['save']);
  $(".term-text span").html (translations ['s_disclaim1'] + ' ' + translations ['s_disclaim2'] + ' ' + translations ['s_disclaim3']);
  // $("#ep-uploaded .head span").html (translations ['uploaded']);
  $("#ep-description .head span").html (translations ['epdesc'] + ':');
  $("#tutorial span").html (translations ['tutorial']);
  $("#sync span").html (translations ['diagnostics']);
  $("#faq span").html (translations ['faq']);
  $("#about span").html (translations ['aboutus']);
  $("#contact span").html (translations ['contactus']);
  $("#legal span").html (translations ['legal']);
  $("#sort-dropdown li:nth-child(1) span").html (translations ['epsortby']);
  $("#sort-dropdown li:nth-child(2) span").html (translations ['fromnewest']);
  $("#sort-dropdown li:nth-child(3) span").html (translations ['fromleftoff']);

  /* this one is tricky */
  var was = $("#player-index span:nth-child(2)").html();
  var text = translations ['watching'];
  text = text.replace ('%1', '</span><span class="ch-title">' + was + '</span><span>');
  $("#player-index").html ('<span>' + text + '</span>');

  for (var s in { 'hello':0, 'suredel':0, 'havedel':0, 'delmore':0, 'addrssyt':0, 'chcat':0 })
    $("#" + s).html (translations [s]);
  for (var s in { 'hinstr':0, 'hwbsg':0, 'hwwe':0, 'hctw':0, 'huak':0, 'hshow':0, 'qno':0, 'qyes':0 })
    $("#" + s).html (translations [s]);
  for (var s in { 'cup':0, 'cdown':0, 'aboutus':0 })
    $("#" + s).html (translations [s]);
  for (var s in { 'newusers':0, 'cinstr':0, 'rsbubble':0 }) // removed 'signup':0
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

  $("#search-field").val (translations ['search']);

  $("#preview-index span:nth-child(1)").html (translations ['pvc'] + ': ');

  /* 3.2 */
  $("#browse span").html (translations ['browse']);
  $("#home span").html (translations ['home']);
  $("#guide span").html (translations ['guide']);
  $("#mypage").html (translations ['mypage']);
  $("#studio").html (translations ['studio']);
  $("#settings").html (translations ['settings']);
  $("#logout").html (translations ['logout']);
  $("#curator-list #list-title").html (translations ['featcur']);
  $("#guide-trending-banner").html (translations ['trendstories']);
  $("#guide-recommended-banner").html (translations ['recommended']);
  $("#guide-imfollowing-banner").html (translations ['imfollowing']);
  $("#home-trending-banner").html (translations ['trendstories']);
  $("#sort-list .head").html (translations ['sortedby']);
  $("#sort-by-update").html (translations ['updatetime']);
  $("#sort-by-sub").html (translations ['mostsub']);
  $("#sort-by-alpha").html (translations ['alphabetical']);
  $("#tag-area #tag-head").html (translations ['mostpop']);
  $("#selected-developer").html (translations ['aboutus']);
  $("#signin-btn").html (translations ['signinup']);
  $("#player-ch-info .favorite li span").eq(0).html (translations ['favorite']);
  $("#curator-title span").html (translations ['curator'] + ':');
  if (language == 'zh')
    $("#video-source").html (translations ['curatoron'] + ' <span>YouTube</span> ' + translations ['upload']);
  else
    $("#video-source").html (translations ['curatoron'] + ' <span>YouTube</span>');
  $("#search-input").val (translations ['search']);
  $("#settings-content #title").eq(0).html (translations ['settings']);
  $("#settings-layer .settings-bar .settings-h1").html (translations ['settings']);
  $("#settings-layer #left li .title").eq(0).html (translations ['email']);
  $("#settings-layer #left li .title").eq(1).html (translations ['yourname']);
  $("#settings-layer #left li .title").eq(2).html (translations ['password']);
  $("#settings-layer #left li .title").eq(3).html (translations ['settingsabout']);
  $("#settings-panel-change li .hint").eq(0).html (translations ['oldpassword'])
  $("#settings-panel-change li .hint").eq(1).html (translations ['newpassword'])
  $("#settings-panel-change li .hint").eq(2).html (translations ['newpasswordverify'])
  $("#btn-save-profile .btn-gray-middle").html (translations ['saveprofile']);
  $("#settings-layer #right .title").html (translations ['aboutimage']);
  $("#btn-change-password .btn-gray-middle").html (translations ['changepassword']);
  $("#btn-change-return-password .btn-white-middle").html (translations ['changepassword']);
  $("#btn-cancel-password .btn-white-middle").html (translations ['cancel']);
  $("#curator-main .manage-tip .tool-text").html (translations ['myc']);
  $("#hottest .manage-tip .tool-text").html (translations ['hottest']);
  $("#featured .manage-tip .tool-text").html (translations ['featured']);
  $("#recommended .manage-tip .tool-text").html (translations ['recommended']);

  $("#btn-home-sign-in .btn-white-middle").html (translations ['signin']);
  $("#return-email, #signup-email").val (translations ['email']);
  $("#signup-name").val (translations ['yourname']);
  $("#return-password-hint").html (translations ['password']);
  $("#signup-password-hint").html (translations ['password']);
  $("#signup-password-hint2").html (translations ['repeatpassword']);
  $("#btn-home-create-account .btn-white-middle").html (translations ['createmyaccount']);
  $("#signin-panel-signup .create-p").html (translations ['thiswillbe']);
  $("#btn-home-sign-in-fb .btn-fb-middle").html (translations ['signfb']);
  $("#signup-checkbox-txt").html (translations ['agreement']);

  $("#curator-activity li .item").eq(0).html (translations ['nchannels']);
  $("#curator-activity li .item").eq(1).html (translations ['imfollowing']);
  $("#curator-activity li .item").eq(2).html (translations ['followingme']);
  $("#curator-page b").html (translations ['mypageurl']);

  /* about dropdown */
  $("#developer-dropdown li").eq(0).html (translations ['a_aboutus']);
  $("#developer-dropdown li").eq(1).html (translations ['a_help']);
  $("#developer-dropdown li").eq(2).html (translations ['a_report']);
  $("#developer-dropdown li").eq(3).html (translations ['a_termspolicy']);
  $("#developer-dropdown li").eq(4).html (translations ['a_contactus']);
  $("#developer-dropdown li").eq(5).html (translations ['a_partners']);
  $("#developer-dropdown li").eq(6).html (translations ['a_curators']);
  $("#developer-dropdown li").eq(7).html (translations ['a_press']);
  $("#developer-dropdown li").eq(8).html (translations ['a_contest']);
  if (language == 'zh')
    {
    $("#developer-dropdown li").eq(7).show();
    $("#developer-dropdown li").eq(8).show();
    }
  else
    {
    $("#developer-dropdown li").eq(7).hide();
    $("#developer-dropdown li").eq(8).hide();
    }
  /* TEMP FIX */
  $("#developer-dropdown li").eq(8).attr ("data-doc", "v-contest");

  $("#signin-layer .signinpic").attr ("src", nroot + ((language == 'zh') ? 'sign_in_zh.png' : 'sign_in.png'));
  $("#signin-layer .signuppic").attr ("src", nroot + ((language == 'zh') ? 'sign_up_zh.png' : 'sign_up.png'));

  if (true)
    {
    var html = '';
    var p_ch_source = $("#ch-source span").text();
    var p_video_source = $("#video-source span").text();
    var p_curator_source = $("#curator-source span").text();

    /* Note that the last two elements must be listed in reverse order, not what you expect:
       "Floating to the right reverses the order of the elements. This is the expected behavior */

    if (language == 'zh')
      {
      html  = '<p id="ch-source"><span>' + p_ch_source + '</span></p>';
      html += '<p id="video-source">' + translations ['curatoron'] + ' <span>' + p_video_source + '</span> ' + translations ['upload'] + '</p>';
      html += '<p id="curator-source"><span>' + p_curator_source + '</span></p>';
      }
    else
      {
      // html =  '<p id="ch-source">' + translations ['clipfrom'] + ' <span>' + p_ch_source + '</span></p>';
      html =  '<p id="ch-source"><span>' + p_ch_source + '</span></p>';
      html += '<p id="video-source">' + translations ['curatoron'] + ' <span>' + p_video_source + '</span></p>';
      html += '<p id="curator-source">' + translations ['curatorby'] + ' <span>' + p_curator_source + '</span></p>';
      }
    $("#player-ep-source").html (html);
    }

  if (home_stack_name == 'hottest')
    $("#home-type").html (translations ['hottest']);
  else if (home_stack_name == 'featured')
    $("#home-type").html (translations ['featured']);
  else if (home_stack_name == 'recommended')
    $("#home-type").html (translations ['recommended']);

  if (username == 'Guest')
    $("#followings-wrap h1").html (translations ['morerec'])
  else
    $("#followings-wrap h1").html (translations ['imfollowing'] + ' (<span id="home-follow-count">' + channels_in_guide() + '</span>/72)');

  if (thumbing == 'home')
    set_language_for_home_page();

  translate_top_level_categories();
  footer_locale();
  header();
  redraw_subscribe();
  if (thumbing == 'player')
    {
    player_column_title();
    redraw_player_column();
    }
  }

function footer_locale()
  {
  if (false && username == 'Guest')
    {
    $("#selected-sitelocation").html (translations ['region']);
    $("#selected-sitelang").html (translations ['language']);
    }
  else
    {
    $("#selected-sitelocation").html ((sphere == 'zh') ? translations ['taiwan'] : translations ['usa']);
    $("#selected-sitelang").html ((language == 'zh') ? '中文' : 'English');
    }

  $("#sitelocation-dropdown li").eq(0).text (translations ['usa']);
  $("#sitelocation-dropdown li").eq(1).text (translations ['taiwan']);

  $("#sitelang-dropdown li").eq(0).text ('English');
  $("#sitelang-dropdown li").eq(1).text ('中文');
  }

function set_language_for_home_page()
  {
  home_subscriptions();
  var id = $("#trending-stories-right li.on").attr ("id").replace (/^trending-/, '');
  home_trending_inner (id);
  redraw_home_right_column (home_stack_name);
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

var relay_log_data = [];

function relay_log (direction, text)
  {
  if (text != 'PING')
    relay_log_data.push ({ timestamp: new Date().getTime(), direction: direction, command: text });
  }

var video_log_data = [];

function video_log (forum, id, video_id)
  {
  video_log_data.push ({ timestamp: new Date().getTime(), forum: forum, id: id, video: video_id });
  }

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

function report_program()
  {
  if (! (current_channel in channelgrid))
    report ('w', '\t' + current_program);
  else
    report ('w', channelgrid [current_channel]['id'] + '\t' + current_program);
  }

function init()
  {
  if ($.browser.msie && jQuery.browser.version.match (/^[78]/))
    {
    nopreload = true;
    wmode = 'gpu';
    }

  if ((location+'').match (/relay=off/))
    {
    norelay = true;
    $("#relaydiv").hide();
    }

  if ((location+'').match (/wmode=/))
    {
    wmode = (location+'').match (/wmode=([^&]+)/)[1];
    log ('wmode: ' + wmode);
    }

  if ((location+'').match (/nofacebook/))
    {
    nofacebook = true;
    log ('facebook access off');
    }

  $("#captcha-input, #buffering, #sg-bubble").hide();

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

  if (started_from_share())
    via_share = true;

  /* temporary fix -- delete piwik cookies */
  try { delete_piwik_cookies(); } catch (error) {};

  /* Initialize FB Javascript SDK */

  if (FB && !nofacebook)
    FB.init (
      {
      appId: '110847978946712',
      // appId: '193650000771534',
      status: true, // check login status
      cookie: true, // enable cookies to allow the server to access the session
      xfbml: true   // parse XFBML
      });

  if ((location+'').match (/preload=(off|no|false)/))
    {
    nopreload = true;
    preload = 'off';
    }

  if ((location+'').match (/divlog=on/))
    divlog = true;

  if ((location+'').match (/ytq=/))
    yt_quality = (location+'').match (/ytq=([^&]+)/)[1];

  // $("#yt1, #yt2, #yt3, #ss").mousemove (mousemove); // .mouseout (mouseaway);

  $(window).unload (function() { unload(); });

  $("#ch-directory").hide();
  $("#ep-uploaded").hide();
  $("#player-ep-meta").show();

  var cover = document.createElement('div');
  cover.setAttribute('id', 'cover');
  var body = document.getElementsByTagName ('body')[0];
  body.parentNode.insertBefore (cover, body);

  $("#cover").css ({ zIndex: 5, height: "100%", width: "100%", visibility: "visible", position: "absolute", left: "0", top: "0", overflow: "hidden", backgroundColor: "#dedede", display: "none" });

  var undercover = document.createElement('div');
  undercover.setAttribute('id', 'undercover');
  var body = document.getElementsByTagName ('body')[0];
  body.parentNode.insertBefore (undercover, body);

  $("#undercover").css ({ zIndex: 2, height: "100%", width: "100%", visibility: "visible", position: "absolute", left: "0", top: "0", overflow: "hidden", backgroundColor: "#dedede", display: "none" });
  $("#undercover").show();

  $("#index-ch-title").html ('');

  setup_piwik();
  relay_using_swfobject();
  retrieve_device_info();

  /* z-index of this must be higher than the full screen video! */
  $("#confirm-layer").css ("z-index", "66666");

  /* for dan */
  // if ((location+'').match (/\/9x9$/))
  //  $("#store-layer").show();
  elastic();

  if ('hashchange' in $(window))
    $(window).hashchange (hash_changed);
  else
    log ("UNABLE TO SET HASH CHANGE EVENT");

  window.onbeforeunload = function()
    { 
    return "Exit 9x9.tv ...?";
    };

  $("#player-layer, #search-layer, #curator-layer").hide();

  $("#waiting-layer img").attr ("src", "http://9x9ui.s3.amazonaws.com/9x9playerV104/images/loading.gif");

  /* reverse index this temporary translation table */
  for (var n in category_en_to_zh)
    category_zh_to_en [category_en_to_zh [n]] = n;

  if ($("#popmessage-home").length == 0)
    $("#home-layer").append('<div id="popmessage-home"><p class="popmessage-left"></p><p class="popmessage-middle"><p class="popmessage-right"></p></div>');

  progress_bar_fixups();
 
  /* reset pw fixups */ 
  $(".back-to-sign").css("bottom", "4px")
  $("#btn-forgot-password-reset").css ("bottom", "33px")
  /* fb like needs more space */
  $("#btn-like").css ("width", "80px");

  // $("#ep-title").css ("font-size", "18px")

  $("#guide-add-holder .msg").html ("Enter YouTube Channel or Playlist URL:");

  header();

  if ($.browser.msie)
    ie_teaser();
  }

function ie_teaser()
  {
  $("#ie-teaser-layer").show();
  $("#ie-teaser-browser .ie-teaser-chrome").unbind();
  $("#ie-teaser-browser .ie-teaser-chrome").click (function() { window.open ("http://www.google.com/chrome", "_blank"); });
  $("#ie-teaser-browser .ie-teaser-firefox").unbind();
  $("#ie-teaser-browser .ie-teaser-firefox").click (function() { window.open ("http://www.mozilla.org/firefox", "_blank"); });
  $("#ie-teaser-close, #btn-ie-teaser-ok").unbind();
  $("#ie-teaser-close, #btn-ie-teaser-ok").click (function() { $("#ie-teaser-layer").hide(); });
  }

function progress_bar_fixups()
  {
  /* fixups for progress bar so they reflect reality */
  // $("#btn-knob").css ("margin-left", "-9px");
  // $("#sub-ep").css ("position", "absolute");
  // $("#sub-ep").css ("top", "0px");
  // $("#sub-ep li").css ("position", "absolute");
  $("#sub-ep").css ("margin-left", "-9px");
  // $("#btn-favorite .gray-manage-tip-m").css ("visibility", "hidden")
  }

function set_username_clicks()
  {
  $("#signin").unbind();
  $("#signin").click (function()
    {
    var saved_thumbing = thumbing;
    new_signup (function()
      {
      if (saved_thumbing == 'home')
        home();
      });
    });
  $("#profile").unbind();
  $("#profile").click (function (event)
    {
    event.stopPropagation();
    if ($("#profile-dropdown").css ("display") != 'block')
      {
      $("#profile-dropdown").show();
      $("body").unbind();
      $("body").click (profile_dropdown_close);
      $("#profile-dropdown li").unbind();
      $("#profile-dropdown li").click (function (event)
        {
        event.stopPropagation();
        var id = $(this).attr ("id");
        log ('profile dropdown click: ' + id);
        profile_dropdown_close();
        if (id == 'mypage')
          curation (curatorid);
        else if (id == 'studio')
          seamless_exit ("/cms");
        else if (id == 'settings')
          settings();
        else if (id == 'logout')
          signout();
        });
      }
    else
      $("#profile-dropdown").hide();
    });
  }

function seamless_exit (url)
  {
  window.onbeforeunload = undefined;
  if (url.match (/^\//))
    window.location.href = location.protocol + '//' + location.host + url;
  else
    window.location.href = url;
  }

function profile_dropdown_close()
  {
  $("#profile-dropdown").hide();
  close_all_dropdowns();
  }

function retrieve_device_info()
  {
  device_id = getcookie ('device');
  if (!device_id)
    {
    var query = '/playerAPI/deviceRegister';
    if (user && username != 'Guest')
      query += '?user=' + user + rx();
    else
      query += rx ('?');
    var d = $.get (query, function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        retrieve_device_info();
      });
    return;
    }

  var d = $.get ('/playerAPI/deviceTokenVerify?device=' + device_id + rx(), function (data)
    {
    known_users = [];
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] != '0')
      {
      deletecookie ("device");
      retrieve_device_info();
      return;
      }
    for (var i = 2; i < lines.length; i++)
      {
      if (lines[i] != '')
        {
        var fields = lines[i].split ('\t');
        if (fields.length == 2)
          known_users.push ({ token: fields[0], name: fields[1], email: 'user@domain' });
        else if (fields.length >= 3)
          known_users.push ({ token: fields[0], name: fields[1], email: fields[2] });
        }
      }
    });
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
log ("REVERSE ENGINEER: " + episode_id + " FROM: " + program_line);
  for (var i = 1; i <= n_program_line; i++)
    {
    if (episode_id == youtube_of (program_line [i]))
      return program_line [i];
    }
  }

function track_episode (set_id, channel_id, episode_id)
  {
  log ("&&&&&&&&&&&&&&&&&& TRACK :: CHANNEL:" + channel_id + " EPISODE:" + episode_id);
  track_without_piwik (set_id, channel_id, episode_id);
  // piwik_track_episode (set_id, channel_id, episode_id)
  into_player_history (set_id, channel_id, episode_id);
  }

function into_player_history (set_id, channel_id, episode_id)
  {
  var th = thumbing.replace ('-wait', '');

  if (th == 'player')
    player_history [th][channel_id] = true;
  else if (th == 'store' && set_id)
    player_history [th][channel_id] = 
      { 'set': set_id, 'store_tab': store_tab, 'store_preview_type': store_preview_type, 'store_index': store_index,
        'store_cat': store_cat, 'store_recommend_index': store_recommend_index };
  }

function piwik_track_episode (set_id, channel_id, episode_id)
  {
  var channel = pool [channel_id];
  var pageTitle;
  var has_piwik = false;
  try { if (Piwik) has_piwik = true; } catch (error) {};
  if (has_piwik && 'piwik' in channel)
    {
    var full_episode_id = episode_id;
    if ((channel_id + '.' + episode_id) in programgrid)
      full_episode_id = channel_id + '.' + episode_id;

    var episode_mapped = 'ep' + full_episode_id;
    if (episode_mapped.match (/\./))
      episode_mapped = 'yt' + episode_id;
      
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
    var idSite, pageUrl;
    if (set_id)
      {
      if (! (set_id in set_pool))
        {
        log ("track_episode: set id not in pool: " + set_id);
        return;
        }
      if (! ('piwik' in set_pool [set_id]))
        {
        log ("track_episode: no piwik id known for set: " + set_id);
        return;
        }
      idSite = set_pool [set_id]['piwik'];
      pageTitle = channel ['name'] + '(ch' + channel ['id'] + ')/' + programinfo ['name'] + '(' + episode_mapped + ')';
      pageUrl = pageTitle;
      }
    else
      {
      idSite = channel ['piwik'];
      pageTitle = programinfo ['name'] + ' (' + episode_mapped + ')';
      pageUrl = pageTitle;
      }
    if (set_id)
      log ('track :: set=' + set_id + ' channel=' + channel_id + ' episode=' + episode_id + ' piwik=' + idSite);
    else
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

var track_timex;

var last_tracked_channel_id;
var last_tracked_episode_id;

function track_without_piwik (set_id, channel_id, episode_id)
  {
  if (channel_id == last_tracked_channel_id && episode_id == last_tracked_episode_id)
    return;

  last_tracked_channel_id = channel_id;
  last_tracked_episode_id = episode_id;

  var channel = pool [channel_id];

  var full_episode_id = episode_id;
  if ((channel_id + '.' + episode_id) in programgrid)
    full_episode_id = channel_id + '.' + episode_id;

  var episode_unmapped = get_current_program()['id'];
  var episode_mapped = 'ep' + full_episode_id;
  if (episode_mapped.match (/\./))
    {
    episode_unmapped = episode_id;
    episode_mapped = 'yt' + episode_id;
    }

  var subepisode;
  //\oo/\\ var programinfo = programgrid [full_episode_id];
  var programinfo = get_current_program();
  if (!programinfo)
    {
log ("tracking: no programinfo!");
    var pid = reverse_engineer_program_id (episode_unmapped);
    if (pid)
      {
      programinfo = programgrid [pid];
      episode_unmapped = pid;
      if ('umbrella' in programinfo)
        {
        episode_unmapped = programinfo ['umbrella'];
        subepisode = pid;
        }
      }
    }

  var prefix = '/view/';

  if (false && subepisode)
    {
    // pageTitle = prefix + 'ch' + channel ['id'] + '/' + episode_unmapped + '/' + subepisode + '/' + episode_mapped;
    pageTitle = prefix + 'ch' + channel ['id'] + '/' + episode_unmapped + '/' + episode_mapped;
    }
  else if (episode_mapped.match (/^yt/))
    pageTitle = prefix + 'ch' + channel ['id'] + '/' + episode_mapped;
  else
    pageTitle = prefix + 'ch' + channel ['id'] + '/' + episode_unmapped + '/' + episode_mapped;

  var acct = document.location.host.match (/(dev|stage|alpha)/) ? 'UA-31930874-1' : 'UA-21595932-1';

  var ch_pageTitle = prefix + 'ch' + channel ['id'];
  log ("GOOGLE ANALYTICS (ch): " + ch_pageTitle);
  _gaq.push (['_set', 'title', channel ['name']]);
  _gaq.push (['_trackPageview', ch_pageTitle]);

  log ("GOOGLE ANALYTICS (ep): " + pageTitle);
  _gaq.push (['_set', 'title', programinfo ['name']]);
  _gaq.push (['_trackPageview', pageTitle]);

  clearTimeout (track_timex);
  track_timex = setInterval ('add_extra_nobounce("' + channel_id + '","' + episode_id + '")', 1000 * 60 * 5)
  }

function add_extra_nobounce (channel_id, episode_id)
  {
  if (thumbing == 'player')
    {
    if (channel_id == last_tracked_channel_id && episode_id == last_tracked_episode_id)
      {
      if (ytmini [mini_player].getPlayerState() == 1)
        {
        /* video is still playing */
        _gaq.push (['_trackEvent', 'NoBounce', '10 second ping']);
        return;
        }
      }
    }
  clearInterval (track_timex);
  }

function track_eof()
  {
  _gaq.push (['_trackPageview', 'eof']);
  }

function track_search (terms)
  {
  var acct = document.location.host.match (/(dev|stage|alpha)/) ? 'UA-31930874-1' : 'UA-21595932-1';
  /* from Ivan: http://9x9.tv/?search=users' */
  var pageTitle = "?search=" + encodeURIComponent (terms);
  _gaq.push (['_setAccount', acct]);
  _gaq.push (['_set', 'title', terms]);
  _gaq.push (['_trackPageview', pageTitle]);
  }

var current_hash;
var player_history = { store: {}, player: {}, category: {}, catarrh: {} };

function hash_changed()
  {
  if (location.hash != current_hash)
    {
    if (location.hash == '#' && current_hash == '')
      {
      /* IE8 */
      return;
      }
    log ("HASH HAS CHANGED! was: " + current_hash + ", now " + location.hash);
    current_hash = location.hash;
    if (current_hash.match (/^#!(\d+)/))
      {
      var id = current_hash.match (/^#!(\d+)/)[1];
      if (! (id in pool))
        {
        log ('HASH NOT IN POOL! hash: ' + id);
        /* hope for the best */
        process_our_url();
        return;
        }
      hash_changed_player (id);
      }
    else if (current_hash == '#!guide')
      guide();
    else if (current_hash == '#!home')
      home();
    else if (current_hash == '#!settings')
      settings();
    else if (current_hash.match (/\!(about|aboot)/))
      {
      if (current_hash.match (/\!(about|aboot)=/))
        {
        var page = current_hash.match (/\!(about|aboot)=([^\&=]+)/) [2];
        log ("ABOOT: " + page);
        developer ("v-" + page);
        }
      else
        developer ("v-about");
      }
    else if (current_hash == '#!directory')
      browse();
    else if (current_hash.match (/\!curator=([^\&=]+)/))
      {
      var cur8 = current_hash.match (/\!curator=([^\&=]+)/) [1];
      curation (cur8);
      }
    }
  }

function hash_changed_player (id)
  {
  log ('hash changed player');

  var stackinfo = inside_a_stack (id);

  if (stackinfo)
    {
    /* channel in a known stack, so re-create it */
    player_stack = stackinfo ['stack'];
    player (stackinfo ['stackname'], stackinfo ['cursor']);
    }
  else if (id in pool)
    {
    /* known channel but not in any of the various stacks */
{
var foo = [];
for (var f in trending_stack)
  {
  if ('id' in trending_stack [f])
    foo.push (trending_stack [f]['id']);
  }
// alert ('###################### CHANNEL: ' + id + ", TRENDING STACK IS: " + foo);
}
    player_stack = [ undefined, pool [id] ];
    player ('special', 1); 
    }
  else
    {
    /* unknown channel */
    load_channel_then (id, function()
      {
      // alert ('###################### UNKNOWN CHANNEL: ' + id + ", TRENDING STACK IS: " + foo);
      player_stack = [ undefined, pool [id] ];
      player ('special', 1); 
      });
    }
  }

function inside_a_stack (id)
  {
  var cursor;

  cursor = first_position_with_this_id (id);

  if (cursor > 0)
    return ({ stackname: 'guide', stack: channelgrid, cursor: cursor })

  cursor = instack (id, trending_stack);
  if (cursor > 0)
    return ({ stackname: 'trending', stack: trending_stack, cursor: cursor })

  cursor = instack (id, recommended_stack);
  if (cursor > 0)
    return ({ stackname: 'recommended', stack: recommended_stack, cursor: cursor })

  cursor = instack (id, hottest_stack);
  if (cursor > 0)
    return ({ stackname: 'hottest', stack: hottest_stack, cursor: cursor })

  cursor = instack (id, featured_stack);
  if (cursor > 0)
    return ({ stackname: 'featured', stack: featured_stack, cursor: cursor })

  return undefined;
  }

function instack (id, stack)
  {
  for (var c in stack)
    {
    var channel = stack [c];
    if (channel ['id'] == id)
      return c;
    }
  return 0;
  }

function set_hash (newhash)
  {
  if (location.hash != newhash)
    {
    location = newhash == '' ? '#' : newhash;
    current_hash = newhash;
    }
  if (newhash != '')
    {
    // var url = location.protocol + '//' + location.host + '/' + newhash;
    // var url = location.protocol + '//' + location.host + '/view?ch=' + newhash.replace (/[\!\#]/g, '');
    var url = location.protocol + '//' + location.host + '/flview?ch=' + newhash.replace (/[\!\#]/g, '');
    if (program_cursor in program_line)
      {
      pid = program_line [program_cursor];
      if (pid in programgrid && 'umbrella' in programgrid [pid] && programgrid [pid]['umbrella'] != pid)
        pid = programgrid [pid]['umbrella'];
      if (pid.match (/\./))
        pid = pid.split (/\./) [1];
      url += '&' + 'ep=' + pid;
      }
    /* prevent comments box by hiding overflow */
    $("#fb-like-container").css ("overflow", "hidden");
    /* prevent fb button from abutting edge */
    // $("#btn-like").css("margin-right", "8px");
    /* center fb button vertically */
    // $("#btn-like").css ("top","-6px")
    // this seems to make it worse: url = url.replace ('#', '%23');
    if ($("#fb-like-container .fb-like").attr ("data-href") != url)
      {
      $("#fb-like-container").html ("");
      $("#fb-like-container").html ('<div class="fb-like" data-send="false" data-layout="button_count" data-colorscheme="dark" data-show-faces="false" data-font="arial" data-href="' + url + '"></div>');
      $("#fb-like-container").show();
      try { FB.XFBML.parse(); } catch (error) {};
      }
    $("#player-holder .favorite .favorite-body span").text (translations ['favorite'])
    if (player_real_channel in pool)
      {
      var channel = pool [player_real_channel];
      if (channel ['curatorid'] == curatorid && (channel ['nature'] == '11' || channel ['nature'] == '12'))
        $("#player-holder .favorite .favorite-body span").text (translations ['unfavorite'])
      }
    }
  }

function unload()
  {
  norelay = true;
  relay_post ("QUIT");
  report_submit_();
  }

var norelay = false;
var relay_started = false;
var last_pong;
var controlling;

function which_relay()
  {
  var which;

  if (location.host == 'www.9x9.tv' || location.host == '9x9.tv' || location.host == 'stage.9x9.tv' || location.host.match (/v31.9x9.tv/))
    which = "relay-prod.9x9.tv";
  else if (location.host == 'dev.9x9.tv')
    which = "relay-dev.9x9.tv";
  else if (location.host == 'demo.9x9.tv')
    which = "relay-demo.9x9.tv";
  else if (location.host == 'alpha.9x9.tv')
    which = "relay-alpha.9x9.tv";
  else if (location.host == 'puppy.9x9.tv')
    which = "relay-puppy.9x9.tv";
  else if (location.host == 'qa.9x9.tv')
    which = "relay-qa.9x9.tv";
  else if (location.host.match (/moveout/))
    which = "relay-moveout.9x9.tv";

  return which;
  }

function which_relay_port()
  {
  if (location.host == 'moveout.9x9.tv')
    return "910";
  else
    return "909";
  }

function relay_using_swfobject()
  {
  $("#relaydiv").html ('<div id="relayinside"></div>');
  $("#relaydiv").show();
  $("#relaydiv").css ("visibility", "visible");

  var which = which_relay();

  var relay_version = "4";

  var params = { allowScriptAccess: "always", wmode: "transparent" };
  var atts = { id: "relayy" };
  var url = "http://" + which + "/relay" + relay_version + ".swf";

  swfobject.embedSWF (url, "relayinside", "500px", "500px", "8", null, null, params, atts);

  /* hack */
  nopreload = true;
  }

function getflash (name)
  {
  try { if (relayy) return relayy; } catch (error) {};
  if (document.getElementById ("relayy"))
    return document.getElementById ("relayy");
  }

var relay_cx_timex;

function relay_banner (color, text)
  {
  $("#slogan span").html (translations ['fad'] + ' &middot; <span style="color: ' + color + '; font-size: 1.0em;">' + text + '</span>');
  }

function relay_loaded()
  {
  clearTimeout (relay_cx_timex);
  last_pong = new Date().getTime();
  log ('relay loaded, requesting connect');
  var f = getflash ("relay");
  if (f && f.start)
    f.start (which_relay(), which_relay_port());
  relay_cx_timex = setTimeout ('log ("relay connection failed!"); relay_loaded()', 15000);
  }

function relay_loaded_and_ready()
  {
  clearTimeout (relay_cx_timex);
  last_pong = new Date().getTime();
  $("#syncstatus").removeClass ("syncoff");
  // relay_banner ('orange', 'with 9x9 Sync');
  relay_started = true;
  relay_post ("DEVICE 9x9-PC " + device_id);
  relay_post ("SESSION " + device_id + ' ' + (timezero / 1000));
  relay_post ("TIMESTAMP " + (new Date().getTime() / 1000));
  if (user != '')
    relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));
  ping_timex = setInterval ("ping_relay()", 10000);
  $("#relaydiv").css ("visibility", "hidden");
  }

var ping_timex;

function ping_relay()
  {
  relay_post ("PING");
  if (last_pong)
    {
    var since = new Date().getTime() - last_pong;
    if (since > 30000)
      {
      last_pong = undefined;
      log ('last pong: ' + since + ' ago');
      $("#relaydiv").hide();
      setTimeout ("relay_reconnect()", 500);
      // relay_banner ('orange', 'connection timed out');
      $("#syncstatus").addClass ("syncoff");
      }
    }
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
        log ('relay received: ' + lines[i]);
        execute_relay_command (lines[i]);
        }
      }
  }

function relay_error (s)
  {
  log ("relay error: " + s);
  }

function relay_disconnected()
  {
  log ("relay disconnected");
  relay_started = false;
  controlling = undefined;
  clearInterval (ping_timex);
  if (!norelay)
    {
    $("#relaydiv").hide();
    setTimeout ("relay_reconnect()", 500);
    // relay_banner ('orange', 'disconnected');
    $("#syncstatus").addClass ("syncoff");
    }
  }

function relay_reconnect()
  {
  log ('attempt reconnect');
  relay_using_swfobject();
  }

function execute_relay_command (s)
  {
  var fields = s.split (/\s+/);
  var cmd = fields [0];
  var args = fields [1];

  if (cmd == 'IPG')
    {
    log ("RELAY :: IPG");
    if (thumbing != 'guide' && thumbing != 'guide-wait')
      guide_inner();
    }
  else if (cmd == 'IPG-POSITION')
    {
    log ("RELAY :: IPG-POSITION " + args);
    cursor_off (ipg_cursor);
    ipg_cursor = args;
    cursor_on (ipg_cursor);
    }
  else if (cmd == 'PLAY')
    {
    log ("RELAY :: PLAY " + fields[1] + ' ' + fields[2]);
    log ("relay PLAY not supported on this device");
    }
  else if (cmd == 'UPDATE')
    {
    log ("RELAY :: UPDATE");
    fetch_channels();
    }
  else if (cmd == 'BECOME')
    {
    log ("RELAY :: BECOME " + args);
    if (fields.length > 2)
      {
      username = fields [2];
      userid = undefined;
      }
    user = args;
    relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));
    set_username();
    solicit();
    via_share = false;
    /* wipe out the current guest account program+channel data */
    wipe();
    escape();
    activated = false;
    fetch_everything();
    }
  else if (cmd == 'ONLINE')
    {
    who = []
    for (var i = 1; i < fields.length; i++)
      if (fields[i] != '')
        {
        log ('online ' + i + ': ' + fields[i]);
        who.push (fields[i]);
        }
    update_who();
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
      redraw_help_sync();
      }
    else if (args.match (/(\d+):/))
      {
      var slot = parseInt (args.match (/(\d+):/) [1]);
      nodes_incoming [slot] = { name: fields[1], type: fields[2], ip: fields[3], devtype: fields[4] };
      }
    }
  else if (cmd == 'YOUARE')
    {
    if (thumbing == 'help' && help_tab == 'sync')
      {
      iam = args;
      help_sync_contacts();
      }
    }
  else if (cmd == 'TELL')
    {
    relay_post (s);
    }
  else if (cmd == 'REPORT')
    {
    if (fields[2] == 'ACK' && fields[1].match (/(\d+):/))
      {
      var slot = parseInt (fields[1].match (/(\d+):/) [1]);
      help_sync_ack (slot);
      }
    }

  relay_log ("received", s);
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
      relay_log ("sent", s);
      }
    else
      relay_log ("send failed", s);
    }
  }

function relay_post_position()
  {
  last_sent_ipg_cursor = ipg_cursor;
  relay_post ("IPG-POSITION " + ipg_cursor);
  }

function started_from_share()
  {
  var pathname = location.pathname;
  var split = pathname.split ('/');
  return (!shared_but_is_now_logged_in && split.length == 3 && split[2].match(/^[0-9]+$/));
  }

function get_ipg_id()
  {
  return via_share_path;
  // var split = location.pathname.split ('/');
  // return split[2];
  }

function user_or_ipg()
  {
  return 'user=' + user;
  }

function rx (delim)
  {
  if (!delim && delim != '') delim = '&';
  var now = new Date();
  var seconds = now.getTime();
  if (username != 'Guest' && userid)
    {
    if (delim == '')
      return '&' + 'rx=u' + userid + '&' + 'v=32';
    else
      return delim + 'rx=p' + seconds + 'u' + userid + '&' + 'v=32';
    }
  else
    {
    if (delim == '')
      return '';
    else
      return delim + 'rx=p' + seconds + '&' + 'v=32';
    }
  }

function mso()
  {
  return '';
  return (sitename == '') ? '' : ('&' + 'mso=' + sitename);
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
      $("#waiting-layer, #dir-waiting, #ch-waiting").hide();
      }
    return;
    }

  log ('(obtaining: standard)');

  var query = "/playerAPI/programInfo?channel=" + channel + mso() + '&' + user_or_ipg() + rx();

  var d = $.get (query, function (data)
    {
    parse_program_data (data);
    $("#waiting-layer, #dir-waiting, #ch-waiting").hide();
    if (callback) eval (callback);
    });
  }

function fetch_everything()
  {
  all_channels_fetched = false;
  all_programs_fetched = false;
  wipe();
  fetch_channels();
  add_to_known_users();
  }

function wipe()
  {
  channelgrid = {};
  // programgrid = {};
  set_ids = {};
  set_titles = {};
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

        parse_program_line (++count, lines[i]);
        }
      }

    log ('finished parsing program data');
    }
  else
    log_and_alert ('server returned nothing');
  }

function init_progress_bar()
  {
  var program = programgrid [program_line [program_cursor]];

  var html = '';
  var sub_html = '';

  for (var s in program ['segments'])
    {
    if (s > 0)
      {
      var segment = program ['segments'][s];
      var left = (segment ['starting'] * 100 / program ['total-duration']) + "%";
      var width = (segment ['duration'] * 100 / program ['total-duration']) + "%";
      var bg = s % 2 == 0 ? "rgba(255, 160, 0, 0.2)" : "rgba(165, 42, 42, 0.2)";
      bg = "rgba(165, 42, 42, 0.2)";
      html += '<li class="progress-titlecard" style="width: ' + width + '; left: ' + left + '; background-color: ' + bg + '"></li>';
      sub_html += '<li id="clip-' + s + '" style="left: ' + left + '">';
      sub_html += '<span class="gray-manage-tip-l">';
      sub_html += '<span class="gray-tip-top"></span>';
      sub_html += '<span class="gray-tip-content-l">';
      sub_html += '<span class="gray-tip-left-l"></span>';
      if (segment ['type'] == 'video')
        sub_html += '<img src="' + segment ['clip']['thumb'] + '" class="sub-ep-thumb">';
      else
        sub_html += '<div class="sub-ep-thumb box"></div>';
      sub_html += '<span class="sub-ep-title ellipsis">' + segment ['clip']['name'] + '</span>';
      sub_html += '<span class="gray-tip-right-l"></span></span>';
      sub_html += '</span>';
      sub_html += '</li>';
      }
    }

  $("#titlecards").html (html);
  $("#sub-ep").html (sub_html);

  progress_bar_fixups();
  ellipses();

  $("#sub-ep li").unbind();
  $("#sub-ep li").hover (function()
    {
    log ("SUB EP HOVER IN: " + $(this).attr("id"));
    var id = $(this).attr ("id").replace (/^clip-/, '');
    var program = get_current_program();
    var this_segment = program ['segments'][id];
    if (this_segment ['type'] == 'begin-title' || this_segment ['type'] == 'end-title')
      {
      log ('playing tiny titlecard: ' + id);
      var titlecard = this_segment ['titlecard'];
      $(this).children().children().children(".box").titlecard
        ({
        text: titlecard ['message'],
        align: 'align' in titlecard ? titlecard ['align'] : 'center',
        effect: 'effect' in titlecard ? titlecard ['effect'] : 'fade',
        duration: 'duration' in titlecard ? parseInt (titlecard ['duration']) : 7,
        fontSize: 'size' in titlecard ? parseInt (titlecard ['size']) : 20,
        fontColor: 'color' in titlecard ? titlecard ['color'] : '#ffffff',
        fontWeight: 'weight' in titlecard ? titlecard ['fontweight'] : 'normal',
        fontStyle: 'style' in titlecard ? titlecard ['style'] : 'normal',
        backgroundColor: 'bgcolor' in titlecard ? titlecard ['bgcolor'] : '#000000'
        });
      }
    },
  function()
    {
    log ("SUB EP HOVER OUT: " + $(this).attr("id"));
    });

  }

var episode_pool = {};

function parse_program_line (count, line)
  {
  var pinfo = line_to_program (line);
  // log (">> episode #" + i + ": " + pinfo ['id'] + " = " + pinfo ['name']);

  var total_duration = 0;

  var begin_titles = [];
  var end_titles = [];

  pinfo ['segments'] = [ '' ];

  pinfo ['sort'] = count;

  if (pinfo ['submeta'].match (/:/))
    {
    log ('HAS SUBMETA');
    var submetas = pinfo ['submeta'].split (/\n--/);
    for (var m in submetas)
      {
      if (typeof (submetas [m]) == 'string')
        {
        log ('META');
        var kvs = {};
        var mlines = submetas[m].split ('\n');
        for (j = 0; j < mlines.length; j++)
          {
          if (mlines[j].match (/:/))
            {
            var k = mlines[j].match (/^([^:]*):\s*(.*)/)[1];
            var v = mlines[j].match (/^([^:]*):\s*(.*)/)[2];
            kvs [k] = v;
            log ('  ' + k + '=' + v);
            }
          }
        if ('message' in kvs && 'subepisode' in kvs)
          {
          log ('might have titlecard, type is: ' + kvs ['type']);
          if (kvs ['type'].match (/begin/i))
            begin_titles [parseInt (kvs ['subepisode'])] = kvs;
          else if (kvs ['type'].match (/end/i))
            end_titles [parseInt (kvs ['subepisode'])] = kvs;
          }
        }
      }
    }
 
  if (pinfo ['url1'].match (/\|/))
    {
    // log ('id ' + pinfo ['id'] + ' url is: ' + pinfo ['url1']);

    var urls = pinfo ['url1'].split (/\|/);
    var names = pinfo ['name'].split (/\|/);
    var descs = pinfo ['desc'].split (/\|/);
    var thumbs = pinfo ['thumb'].split (/\|/);
    var durs = pinfo ['duration'].split (/\|/);
    var types = pinfo ['type'].split (/\|/);
    var snapshots = pinfo ['snapshot'].split (/\|/);

    episode_pool [pinfo ['id']] = pinfo;

    pinfo ['original-url'] = pinfo ['url1'];
    pinfo ['original-name'] = pinfo ['name'];
    pinfo ['original-desc'] = pinfo ['desc'];
    pinfo ['original-thumb'] = pinfo ['thumb'];
    pinfo ['original-duration'] = pinfo ['duration'];
    pinfo ['original-type'] = pinfo ['type'];
    pinfo ['original-snapshot'] = pinfo ['snapshot'];

    pinfo ['url'] = urls [0];
    pinfo ['name'] = names [0];
    pinfo ['desc'] = descs [0];
    pinfo ['thumb'] = thumbs [0];
    pinfo ['duration'] = durs [0];
    pinfo ['type'] = types [0];
    pinfo ['snapshot'] = snapshots [0];

    for (var j = 1; j < urls.length; j++)
      {
      var url = urls [j].replace (/;;$/, '');
      var dur = durs ? parseInt (durs [j].replace (/;0;/, '')) : 29;
      var url1 = canonicalize_youtube_url (url);
      // log ("SUBEPISODE PIECE: " + url1);
      var sub = { url: url1, duration: dur, name: names [j], desc: descs [j], thumb: thumbs [j], type: types [j], snapshot: snapshots [j] };
      if (j in begin_titles)
        {
        sub ['begin-title'] = begin_titles [j];
        pinfo ['segments'].push ({ type: 'begin-title', duration: begin_titles [j]['duration'], 
                                     titlecard: begin_titles [j], clip: sub, starting: total_duration });
        total_duration += parseFloat (begin_titles [j]['duration']);
        }
      pinfo ['segments'].push ({ type: 'video', duration: dur, clip: sub, starting: total_duration });
      total_duration += parseFloat (dur);
      if (j in end_titles)
        {
        sub ['end-title'] = end_titles [j];
        pinfo ['segments'].push ({ type: 'end-title', duration: end_titles [j]['duration'], 
                                     titlecard: end_titles [j], clip: sub, starting: total_duration });
        total_duration += parseFloat (end_titles [j]['duration']);
        }
      pinfo ['total-duration'] = total_duration;
      }
    }
  else
    {
    /* this shouldn't happen any more, there should always be a | in the URL. but it does happen. */
    var sub = { url: pinfo ['url1'], duration: pinfo ['duration'], name: pinfo ['name'], desc: pinfo ['desc'], thumb: pinfo ['thumb'], type: pinfo ['type'], snapshot: pinfo ['snapshot'] };
    if (1 in begin_titles)
      {
      pinfo ['begin-title'] = begin_titles [1];
      pinfo ['segments'].push ({ type: 'begin-title', duration: begin_titles [1]['duration'], titlecard: begin_titles [1], clip: sub, starting: total_duration });
      total_duration += parseFloat (begin_titles [j]['duration']);
      }
    pinfo ['segments'].push ({ type: 'video', duration: pinfo ['duration'], clip: sub, starting: total_duration });
    total_duration += parseFloat (pinfo ['duration']);
    if (1 in end_titles)
      {
      pinfo ['end-title'] = end_titles [1];
      pinfo ['segments'].push ({ type: 'end-title', duration: end_titles [1]['duration'], titlecard: end_titles [1], clip: sub, starting: total_duration });
      total_duration += parseFloat (end_titles [j]['duration']);
      }
    pinfo ['total-duration'] = total_duration;
    }

  programgrid [pinfo ['id']] = pinfo;
  }

function line_to_program (line)
  {
  var fields = line.split ('\t');
  var pinfo = { 'id': fields[1], 'channel': fields[0], 'type': fields[3], 'url1': fields[8],
                'url2': fields[9], 'url3': fields[10], 'url4': fields[11],
                 'name': fields[2], 'desc': fields [3], 'type': fields[4], 'thumb': fields[6],
                 'snapshot': fields[7], 'timestamp': fields[12], curcom: fields[13], 
                 'submeta': decodeURIComponent (fields[14]), 'duration': fields[5] };

  /* temporary fixups */
  for (var f in { url1:'', url2:'', url3:'', url4:'' })
    {
    if (f in pinfo && pinfo [f])
      pinfo [f] = pinfo [f].replace (/;;$/, '');
    }
  if (pinfo ['url4'] && !pinfo ['url1'])
    {
    pinfo ['url1'] = pinfo ['url4'];
    pinfo ['url4'] = '';
    }
  return pinfo;
  }

function canonicalize_youtube_url (url)
  {
  if (url.match (/youtube/))
    {
    if (url.match (/\/watch\?v=(...........)/))
      {
      var video_id = url.match (/\/watch\?v=(...........)/) [1];
      return 'http://www.youtube.com/watch?v=' + video_id;
      }
    }
  return url;
  }

function fetch_channels()
  {
  log ('******* obtaining channels');

  fetch_channels_then (function()
    {
    update_cart_bubble (channels_in_guide());
    all_channels_fetched = true;

    if (add_jumpstart_channel)
      {
      add_jumpstart_channel_inner();
      return;
      }

    after_fetch_channels (false);
    });
  }

function fetch_channels_then (callback)
  {
  var query = "/playerAPI/channelLineup?user=" + user + '&' + 'setInfo=true' + rx();
  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    log ('number of lines obtained: ' + lines.length);

    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('server error: ' + lines [0]);
      return;
      }
    process_channel_lineup (data);

    if (typeof (callback) == 'function')
      callback (fields[0]);
    else
      eval (callback);
    });
  }

function process_channel_lineup (data)
  {
  var lines = data.split ('\n');

  var n = 0;
  var conv = {};

  for (var y = 1; y <= 9; y++)
    for (var x = 1; x <= 9; x++)
      conv [++n] = "" + y + "" + x;

  var block_start_line = 2;

  for (var i = block_start_line; i < lines.length && lines [i] != '--'; i++)
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
      var channel = line_to_channel (lines[j]);
      if (channel ['pos'] == '0')
        continue;
      log ("channel line " + j + ": " + conv [channel ['pos']] + ' = ' + lines [j]);
      if (channel ['nature'] == '2')
        {
        notice_ok (thumbing, "This account contains obsolete channel data. Please DO NOT USE THIS ACCOUNT for testing or demo until the problem is fixed. Technical information: Channel " + channel ['id'] + " has type " + channel ['nature'], "");
        }
      else
        {
        channelgrid [conv [channel ['pos']]] = channel;
        pool [channel ['id']] = channel;
        }
      }
    else
      log ("ignoring channels line " + j + ": " + lines [j]);
    }
  }

var trending_stack = [];
var recommended_stack = [];
var recommended_stack_in_waiting = [];
var featured_stack = [];
var hottest_stack = [];

function process_channel_stack (stackname, data)
  {
  var count = 0;
  var stack = [];

  var lines = data.split ('\n');
  var fields = lines[0].split ('\t');

  if (fields[0] != '0')
    {
    log ('error with channel stack: ' + line);
    return;
    }

  for (var i = 2; i < lines.length; i++)
    {
    if (lines [i] != '' && lines [i] != '--')
      {
      log ("STACK " + stackname + ": " + lines [i]);
      var channel = line_to_channel (lines[i]);
      pool [channel ['id']] = channel;
      stack [++count] = channel;
      }
    }

  if (stackname == 'trending')
    trending_stack = stack;
  else if (stackname == 'recommended')
    {
    if (recommended_stack && recommended_stack.length > 0)
      recommended_stack_in_waiting = stack
    else
      recommended_stack = stack;
    }
  else if (stackname == 'hottest')
    hottest_stack = stack;
  else if (stackname == 'featured')
    featured_stack = stack;

  guide_stacks();
  }

function guide_stacks()
  {
  gt_cursor = 0;
  $("#gt-list").html (guide_stack_html ("trending", trending_stack));
  gt_bindings();

  gr_cursor = 0;
  $("#gr-list").html (guide_stack_html ("recommended", recommended_stack));
  gr_bindings();
  }

function guide_stack_html (stackname, stack)
  {
  var html = '';

  for (var i = 1; i < stack.length; i++)
    {
    var channel = stack [i];
    var ago = ageof (channel ['timestamp'], true);
    var funf = (first_position_with_this_id (channel ['id']) > 0) ? translations ['unfollow'] : translations ['follow'];
    html += '<li id="guide-' + stackname + '-' + i + '">';
    html += '<div class="guide-trending-box">';
    html += '<p class="guide-trending-tab"><span>' + funf + '</span></p>';
    html += '<img src="' + channel ['thumb1'] + '" class="thumbnail">';
    html += '<div class="btn-watch"></div>';
    html += '</div>';
    html += '<p class="min-title"><span>' + channel ['name'] + '</span></p>';
    html += '<p class="min-description ellipsis multiline">';
    html += '<span>' + channel ['desc'] + '</span>';
    html += '</p>';
    html += '<p class="min-meta">';
    var plural = channel ['count'] == 1 ? translations ['episode'] : translations ['episodes'];
    html += '<span>' + channel ['count'] + ' ' + plural + ' </span><span class="divider">|</span><span>' + ago + '</span>';
    html += '</p>';
    html += '<p class="min-curator"><span>' + translations ['curatorby'] + '</span><span class="name">' + channel ['curatorname'] + '</span></p>';
    html += '</li>';
    }

  return html;
  }

function home_trending()
  {
  redraw_home_trending (1);
  home_trending_inner (1);

  $("#home-up").unbind();
  $("#home-up").click (function() { home_trending_arrow_up(); start_home_autorotation(); });
  $("#home-up").hover (function()
    {
    $(this).parent().removeClass ("horizon-arrows");
    $(this).parent().addClass ("horizon-arrows-up");
    },
  function()
    {
    $(this).parent().removeClass ("horizon-arrows-up");
    $(this).parent().addClass ("horizon-arrows");
    });

  $("#home-down").unbind();
  $("#home-down").click (function() { home_trending_arrow_down(); start_home_autorotation(); });
  $("#home-down").hover (function()
    {
    $(this).parent().removeClass ("horizon-arrows");
    $(this).parent().addClass ("horizon-arrows-down");
    },
  function()
    {
    $(this).parent().removeClass ("horizon-arrows-down");
    $(this).parent().addClass ("horizon-arrows");
    });

  $("#trending-stories-right").unbind();
  $("#trending-stories-right").mouseover (function() { clearTimeout (home_timex); });
  $("#trending-stories-right").mouseout (function() { start_home_autorotation(); });
  }

function redraw_home_trending (starting_at)
  {
  var html = '';
  var count = 0;
  for (var t = 1; t <= 100; t++)
    {
    if (t >= starting_at && t in trending_stack && trending_stack [t]['id'])
      {
      var channel = trending_stack [t];
      var ago = ageof (channel ['timestamp'], true);
      var plural = channel ['count'] == 1 ? translations ['episode'] : translations ['episodes'];
      log ("trending: " + channel ['id']);
      html += '<li id="trending-' + t + '">';
      html += '<div class="r-frame">';
      html += '<p class="m-frame">';
      html += '<img src="' + channel ['thumb1'] + '" class="trending-stories-right-list-pic">';
      html += '<div class="epi">';
      html += '<p class="l"></p>';
      html += '<p class="c"><span>' + channel ['count'] + ' ' + plural + '</span></p>';
      html += '<p class="r"></p>';
      html += '</div>';
      html += '<p class="trending-stories-right-list-note">' + channel ['lasttitle'] + '</p>';
      html += '<p class="trending-stories-right-list-name">' + channel ['name'] + '</p>';
      html += '<p class="trending-stories-right-list-time">' + ago + '</p>';
      html += '</p>';
      html += '</div>';
      html += '</li>';
      if (++count >= 5) break;
      }
    }
  $("#trending-stories-right").html (html);

  $("#trending-stories-right li").unbind();
  $("#trending-stories-right li").click (function() { home_trending_click ($(this).attr("id")); });
  $("#trending-stories-right li").hover (function()
    {
    home_trending_click ($(this).attr("id"));
    },
  function()
    {
    });
  }

function home_trending_click (id)
  {
  id = id.replace (/^trending-/, '');
  log ('home trending click: ' + id);
  home_trending_inner (id);
  }

function home_trending_inner (id)
  {
  $("#trending-stories-right li").removeClass ("on");
  $("#trending-" + id).addClass ("on");
  var channel = trending_stack [id];
  var ago = ageof (channel ['timestamp'], true);
  var plural = channel ['count'] == 1 ? translations ['episode'] : translations ['episodes'];
  $("#trending-stories .mainpic").attr ("src", channel ['thumb1']);
  $("#trending-stories .trending-f").html (channel ['name']);
  $("#trending-f-episodes").html (channel ['count'] + ' ' + plural);
  $("#trending-f-ago-views").html (ago + ' | ' + channel ['viewcount'] + ' ' + translations ['views']);
  $("#trending-stories .trending-content-msg h2").html (channel ['lasttitle']);
  $("#trending-stories .trending-content-msg .msg-p-left").html (channel ['desc']);
  $("#trending-stories .trending-content-msg .msg-p-right").html (translations ['curatorby'] + ' <b>' + channel ['curatorname'] + '</b>');
  $("#trending-box-mid .trending-box-footer img").eq(0).attr ("src", channel ['thumb2']);
  $("#trending-box-mid .trending-box-footer img").eq(1).attr ("src", channel ['thumb3']);

  $("#trending-stories .trending-box .trending-box-tab span").text 
     ((first_position_with_this_id (channel ['id']) > 0) ? translations ['unfollow'] : translations ['follow']);
  // $("#homeleftbox .msg-pic").attr("src", channel ['curatorthumb']);
  // $("#homeleftbox .msg-pic").unbind();
  // $("#homeleftbox .msg-pic").click (function() { curation (channel ['curatorid']); });
  $("#trending-stories .trending-content-msg .msg-p-right b").unbind();
  $("#trending-stories .trending-content-msg .msg-p-right b").click (function() { curation (channel ['curatorid']); });
  $("#trending-stories .trending-box").unbind();
  $("#trending-stories .trending-box").click (home_trending_play);
  $("#trending-box .trending-box-tab").unbind();
  $("#trending-stories .trending-box .trending-box-tab").unbind();
  $("#trending-stories .trending-box .trending-box-tab").click (function (event)
    {
    event.stopPropagation();
    log ("home main follow pressed: " + channel ['id']);
    pop_with = "#popmessage-home";

    if (the_very_first_time)
      {
      play_error_noise();
      return;
      }

    function tr_callback()
       {
       var funf = ((first_position_with_this_id (channel ['id']) > 0) ? translations ['unfollow'] : translations ['follow']);
       $("#trending-stories .trending-box-tab span").html (funf);
       }

    if (first_position_with_this_id (channel ['id']) > 0)
      unfollow (channel ['id'], tr_callback);
    else
      browse_accept (channel ['id'], tr_callback);
    });
  }

function reset_home_trending_scope (id)
  {
  var current_first_id = parseInt ($("#trending-stories-right li").eq(0).attr("id").replace (/^trending-/,''));
  if (id < current_first_id)
    redraw_home_trending (id);
  else
    {
    while (id > current_first_id + 4)
      {
      current_first_id++;
      redraw_home_trending (current_first_id);
      }
    }
  }

function home_trending_arrow_up()
  {
  var id = $("#trending-stories-right li.on").attr ("id").replace (/^trending-/, '');
  var new_id = (id <= 1) ? trending_stack.length - 1 : --id;
  reset_home_trending_scope (new_id);
  home_trending_inner (new_id);
  }

function home_trending_arrow_down()
  {
  var id = $("#trending-stories-right li.on").attr ("id").replace (/^trending-/, '');
  var new_id = (parseInt (id) + 1 >= trending_stack.length) ? 1 : ++id;
  reset_home_trending_scope (new_id);
  home_trending_inner (new_id);
  }

function home_trending_play()
  {
  log ('home trending play');
  if (the_very_first_time)
    {
    play_error_noise();
    return;
    }
  var id = $("#trending-stories-right li.on").attr ("id").replace (/^trending-/, '');
  player_stack = trending_stack;
  player ('trending', parseInt (id));
  }

/* this stack is slightly different than the others, as it might have gaps! */
var home_subscriptions_stack = [];

function home_subscriptions()
  {
  var html = '';
  home_subscriptions_stack = [];

  if (username != 'Guest')
    {
    var fol = translations ['isfollowing'].replace (/%1/, username);
    $("#followings-wrap h1").html (fol + ' (<span id="home-follow-count">' + channels_in_guide() + '</span>/72)');
    home_subscriptions_stack = generate_updates_stack();
    }
  else
    {
    $("#followings-wrap h1").html (translations ['morerec']);
    for (var i in recommended_stack)
      home_subscriptions_stack [i] = recommended_stack [i];
    }

  /* safe to assume there are fewer than 99 channels in More Recommendations */
  for (var i = 1; i <= 99; i++)
    {
    if (i in home_subscriptions_stack)
      {
      var channel = home_subscriptions_stack [i];
      var ago = ageof (channel ['timestamp'], true);
      var funf = first_position_with_this_id (channel ['id']) > 0 ? translations ['unfollow'] : translations ['follow'];
      var eplural = (channel ['count'] == 1) ? translations ['episode'] : translations ['episodes'];
      var fplural = (channel ['subscriptions'] == 1) ? translations ['follower'] : translations ['followers'];
      var vplural = (channel ['viewcount'] == 1) ? translations ['view'] : translations ['views'];
      html += '<li id="home-channel-' + i + '">';
      html += '<p class="btn-action"><span>' + funf + '</span></p>';
      html += '<div class="thumb">';
      html += '<p class="icon-pl"></p>';
      html += '<div class="thumbnail-wrapper"><img src="' + channel ['thumb1'] + '" class="thumb1"></div>';
      html += '<img src="' + channel ['thumb2'] + '" class="thumb2">';
      html += '<img src="' + channel ['thumb3'] + '" class="thumb3">';
      html += '</div>';
      html += '<p class="channel-title">' + channel ['name'] + '</p>';
      html += '<p class="channel-meta">';
      html += '<span>' + channel ['count'] + '</span>' + eplural + '<br>';
      html += '<span>' + ago + '</span><br>';
      html += '<span>' + channel ['subscriptions'] + '</span>' + fplural + '<br>';
      html += '<span>' + channel ['viewcount'] + '</span>' + vplural;
      html += '</p>';
      html += '<p class="curator-name">' + translations ['curatorby'] + '<span>' + channel ['curatorname'] + '</span></p>';
      html += '</li>';
      }
    }
  $("#followings-box").html (html);

  /* resize thumbnails */
  if (true)
    {
    $("#followings-box .thumbnail-wrapper img").each (function()
      {
      load_thumbnail_156x88 (this, $(this).attr ("src"));
      });
    }

  $("#followings-box li").unbind();
  $("#followings-box li").click (function() { home_subscriptions_play ($(this).attr ("id")); });
  $("#followings-box li").hover (function()
    {
    $(this).children (".btn-action").show();
    },
  function()
    {
    $(this).children (".btn-action").hide();
    });

  $("#followings-box li .btn-action").unbind();
  $("#followings-box li .btn-action").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().attr ("id").replace (/^home-channel-/, '');
    var channel = home_subscriptions_stack [id];
    if (first_position_with_this_id (channel ['id']) > 0)
      unfollow (channel ['id'], "home_subscriptions()");
    else
      browse_accept (channel ['id'], "home_subscriptions()");
    });

if (false)
{
  $("#followings-wrap .followings-ar b, #followings-wrap .followings-pic").unbind();
  $("#followings-wrap .followings-ar b").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().parent().attr ("id").replace (/^home-channel-/, '');
    if (username != 'Guest')
      curation (channelgrid [id]['curatorid']);
    else
      curation (home_subscriptions_stack [id]['curatorid']);
    });
  $("#followings-wrap .followings-pic").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().attr ("id").replace (/^home-channel-/, '');
    if (username != 'Guest')
      curation (channelgrid [id]['curatorid']);
    else
      curation (home_subscriptions_stack [id]['curatorid']);
    });
}
  }

function load_thumbnail_156x88 (id, url, callback)
  {
  var img = new Image();
  img.onload = function()
    {
    if (img.width == img.height)
      $(id).css ({ width: "156px", height: "156px", 'margin-top': "-34px", 'margin-right': "0", 'margin-bottom': "0", 'margin-left': "0" });
    else
      $(id).css ({ width: "100%", height: "100%", 'margin-top': "0", 'margin-right': "0", 'margin-bottom': "0", 'margin-left': "0" });
    $(id).attr ("src", img.src);
    if (callback) callback();
    };
  img.src = url;
  }

function home_subscriptions_play (id)
  {
  if (username == 'Guest')
    {
    log ('more recommendations play: ' + id);
    id = id.replace (/^home-channel-/, '');
    player_stack = home_subscriptions_stack;
    player ('more', parseInt (id));
    }
  else
    {
    /* play from guide */
    log ('home subscriptions play: ' + id);
    id = id.replace (/^home-channel-/, '');
    player ("updates", parseInt (id));
    }
  }

var home_stack;
var home_stack_name;

function home_right_column()
  {
  redraw_home_right_column ("hottest");
  $("#home-menu li").unbind();
  $("#home-menu li").click (function() { redraw_home_right_column ($(this).attr ("id")); });
  }

function latest_recommended_stack()
  {
return recommended_stack; /* temporarily disabled */
  if (recommended_stack_in_waiting && recommended_stack_in_waiting.length > 0)
    {
    recommended_stack = recommended_stack_in_waiting;
    recommended_stack_in_waiting = undefined;
    /* would freshen the recommended list used in the guide, as well, but need to change logic */
    // gr_cursor = 0;
    // $("#gr-list").html (html);
    // gr_bindings();
    }
  return recommended_stack;
  }

function which_billboard_stack (name)
  {
  if (name == 'hottest')
    return hottest_stack;
  if (name == 'featured')
    return featured_stack;
  if (name == 'recommended')
    return recommended_stack;
  }

function redraw_home_right_column (stack)
  {
  $("#home-menu li").removeClass ("on");
  $("#" + stack).addClass ("on");
  $("#home-menu li .manage-tip").css ("visibility", "visible");

  home_stack_name = stack;

  if (stack == 'hottest')
    {
    /* until we have content for this API */
    home_stack = hottest_stack;
    $("#home-type").text (translations ['hottest']);
    $("#hottest .manage-tip").css ("visibility", "hidden");
    }
  else if (stack == 'featured')
    {
    home_stack = featured_stack;
    $("#home-type").text (translations ['featured']);
    $("#featured .manage-tip").css ("visibility", "hidden");
    }
  else if (stack == 'recommended')
    {
    home_stack = latest_recommended_stack();
    $("#home-type").text (translations ['recommended']);
    $("#recommended .manage-tip").css ("visibility", "hidden");
    }
 
  var html = ''; 
  for (var i = 1; i < home_stack.length; i++)
    {
    var channel = home_stack [i];
    html += player_column_shelf_html (channel, i, i);
    }
  $("#home-billboard").html (html);

  $("#home-billboard li").unbind();
  $("#home-billboard li").click (function() { home_right_column_click ($(this).attr("id")); });

  $("#home-billboard li .btn-quickfollow").unbind();
  $("#home-billboard li .btn-quickfollow").click (function (event)
    {
    event.stopPropagation();
    $(this).css ("visibility", "hidden");
    var id = $(this).parent().attr("id").replace (/^channel-/, '');
    log ('billboard quickfollow '+ home_stack_name + ': ' + id);
    var clicked_stack = which_billboard_stack (home_stack_name);
    var channel = clicked_stack [id];
    pop_with = "#popmessage-home";
    browse_accept (channel ['id']);
    });

  $("#home-billboard li .pl-curator").unbind();
  $("#home-billboard li .pl-curator").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().parent().attr("id").replace (/^channel-/, '');
    log ('billboard curator '+ home_stack_name + ': ' + id);
    var clicked_stack = which_billboard_stack (home_stack_name);
    var channel = clicked_stack [id];
    curation (channel ['curatorid']);
    });
  }

function home_right_column_click (id)
  {
  id = parseInt (id.replace (/^channel-/, ''));
  log ('home right column click: ' + id);
  if (home_stack_name == 'hottest')
    {
    player_stack = hottest_stack;
    player ('hottest', id);
    }
  else if (home_stack_name == 'featured')
    {
    player_stack = featured_stack;
    player ('featured', id);
    }
  else if (home_stack_name == 'recommended')
    {
    player_stack = recommended_stack;
    player ('recommended', id);
    }
  }

var gt_cursor = 0;
var gr_cursor = 0;

var thatt;

function gt_bindings()
  {
  $("#trending-up, #trending-down").unbind();
  $("#trending-up").click (function (event) { event.stopPropagation; start_guide_autorotation(); gt_prev() });
  $("#trending-down").click (function (event) { event.stopPropagation; start_guide_autorotation(); gt_next() });
  $("#gt-list li .guide-trending-box").unbind();
  $("#gt-list li .guide-trending-box").click (function() { thatt = $(this); gt_click ($(this).parent().attr ("id")); });

  gt_horizon_arrows();
  $("#trending-arrows").unbind();
  $("#trending-arrows").hover (function()
    {
    $(this).removeClass ("horizon-arrows-up horizon-arrows-down");
    $(this).addClass ("horizon-arrows");
    },
  function()
    {
    gt_horizon_arrows();
    });

  $("#guide-trending").unbind();
  $("#guide-trending").mouseover (function()
    {
    clearTimeout (guide_timex);
    $("#gt-list .guide-trending-tab").show();
    $("#gt-list .guide-trending-tab").unbind();
    $("#gt-list .guide-trending-tab").click (function (event)
      {
      var id = $(this).parent().parent().attr("id").replace (/^guide-trending-/, '');
      var channel = trending_stack [id];
      event.stopPropagation();
      pop_with = "";
      if (first_position_with_this_id (channel ['id']) > 0)
        {
        log ("quick unfollow on trending: " + channel ['id']);
        unfollow (channel ['id'], "refresh_trending_status()");
        }
      else
        {
        log ("quick follow on trending: " + channel ['id']);
        browse_accept (channel ['id'], "refresh_trending_status()");
        }
      });
    });
  $("#guide-trending").mouseout (function()
    {
    $("#gt-list .guide-trending-tab").hide();
    start_guide_autorotation();
    });

  $("#gt-list li .min-curator .name").unbind();
  $("#gt-list li .min-curator .name").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().parent().attr("id").replace (/^guide-trending-/, '');
    var channel = trending_stack [id];
    curation (channel ['curatorid']);
    });
  }

function gt_horizon_arrows()
  {
  var gt_num = $("#gt-list li").size() - 1;

  var up_ok = (gt_cursor < gt_num);
  var down_ok = (gt_cursor > 0);

  var class_to_use = "horizon-arrows";
  if (up_ok && !down_ok)
    class_to_use = "horizon-arrows-up";
  else if (!up_ok && down_ok)
    class_to_use = "horizon-arrows-down";

  $("#trending-arrows").removeClass ("horizon-arrows horizon-arrows-up horizon-arrows-down");
  $("#trending-arrows").addClass (class_to_use);
  }

function gt_move()
  {  
  var distance = "-" + 15 * (gt_cursor) + "em";
  $("#gt-list").animate ({ top: distance }, 400, "easeInOutExpo");
  gt_bindings();
  }

function gt_next()
  {
  log ("gt next");
  var gt_num = $("#gt-list li").size() - 1;
  gt_cursor = (gt_cursor >= gt_num) ? gt_num : gt_cursor + 1;
  gt_move();
  }

function gt_prev()
  {
  log ("gt prev");
  gt_cursor = (gt_cursor <= 0) ? 0 : gt_cursor - 1; 
  gt_move();
  }

function gt_click (id)
  {
  log ("gt click: " + id);
  clearTimeout (guide_timex);
  var id = id.replace (/^guide-trending-/, '');
  player_stack = trending_stack;
  player ('trending', parseInt (id));
  }

function gr_bindings()
  {
  $("#maylike-up, #maylike-down").unbind();
  $("#maylike-up").click (function (event) { event.stopPropagation; start_guide_autorotation(); gr_prev() });
  $("#maylike-down").click (function (event) { event.stopPropagation; start_guide_autorotation(); gr_next() });
  $("#gr-list li .guide-trending-box").unbind();
  $("#gr-list li .guide-trending-box").click (function() { gr_click ($(this).parent().attr ("id")); });

  gr_horizon_arrows();
  $("#maylike-arrows").unbind();
  $("#maylike-arrows").hover (function()
    {
    $(this).removeClass ("horizon-arrows-up horizon-arrows-down");
    $(this).addClass ("horizon-arrows");
    },
  function()
    {
    gr_horizon_arrows();
    });

  $("#guide-maylike").unbind();
  $("#guide-maylike").mouseover (function()
    {
    clearTimeout (guide_timex);
    $("#gr-list .guide-trending-tab").show();
    $("#gr-list .guide-trending-tab").unbind();
    $("#gr-list .guide-trending-tab").click (function (event)
      {
      var id = $(this).parent().parent().attr("id").replace (/^guide-recommended-/, '');
      var channel = recommended_stack [id];
      event.stopPropagation();
      pop_with = "";
      if (first_position_with_this_id (channel ['id']) > 0)
        {
        log ("quick unfollow on recommended: " + channel ['id']);
        unfollow (channel ['id'], "refresh_trending_status()");
        }
      else
        {
        log ("quick subscribe on recommended: " + channel ['id']);
        browse_accept (channel ['id'], "refresh_trending_status()");
        }
      });
    });
  $("#guide-maylike").mouseout (function()
    {
    $("#gr-list .guide-trending-tab").hide();
    start_guide_autorotation();
    });

  $("#gr-list li .min-curator .name").unbind();
  $("#gr-list li .min-curator .name").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().parent().attr("id").replace (/^guide-recommended-/, '');
    var channel = recommended_stack [id];
    curation (channel ['curatorid']);
    });
  }

function gr_horizon_arrows()
  {
  var gr_num = $("#gr-list li").size() - 1;

  var up_ok = (gr_cursor < gr_num);
  var down_ok = (gr_cursor > 0);

  var class_to_use = "horizon-arrows";
  if (up_ok && !down_ok)
    class_to_use = "horizon-arrows-up";
  else if (!up_ok && down_ok)
    class_to_use = "horizon-arrows-down";

  $("#maylike-arrows").removeClass ("horizon-arrows horizon-arrows-up horizon-arrows-down");
  $("#maylike-arrows").addClass (class_to_use);
  }

function gr_move()
  {  
  var distance = "-" + 15 * (gr_cursor) + "em";
  $("#gr-list").animate ({ top: distance }, 400, "easeInOutExpo");
  gr_bindings();
  }

function gr_next()
  {
  log ("gr next");
  var gr_num = $("#gr-list li").size() - 1;
  gr_cursor = (gr_cursor >= gr_num) ? gr_num : gr_cursor + 1;
  gr_move();
  }

function gr_prev()
  {
  log ("gr prev");
  gr_cursor = (gr_cursor <= 0) ? 0 : gr_cursor - 1; 
  gr_move();
  }

function gr_click (id)
  {
  log ("gr click: " + id);
  clearTimeout (guide_timex);
  var id = id.replace (/^guide-recommended-/, '');
  player_stack = recommended_stack;
  player ('recommended', parseInt (id));
  }

// 14 youtube real channel name 
// 15 subscription count 
// 16 view count 
// 17 tags, separated by comma. example "run,marathon" 
// 18 curator id 
// 19 curator name 
// 20 curator thumb

function line_to_channel (line)
  {
  var default_thumb = 'http://9x9ui.s3.amazonaws.com/mock22/images/guide_ch_default.png';

  var fields = line.split ('\t');

  if (!fields[1])
    alert ('bad channel line: "' +  line + '"');
  var channel = { pos: fields[0], id: fields[1], name: fields[2], desc: fields[3], thumb: fields[4], count: fields[5], type: fields[6], status: fields[7], nature: fields[8], extra: fields[9], timestamp: fields[10], sortorder: fields[11], piwik: fields[12], leftoff: fields[13], curator: fields[14], subscriptions: fields[15], viewcount: fields[16], tags: fields[17], curatorid: fields[18], curatorname: fields[19], curatordesc: fields[20], curatorthumb: fields[21], lasttitle: fields[24] };
  if (channel ['thumb'] && channel ['thumb'].match (/\|/))
    {
    var thz = channel ['thumb'].split (/\|/);
    channel ['thumb1'] = thz [0] ? thz [0] : default_thumb;
    channel ['thumb2'] = thz [1] ? thz [1] : default_thumb;
    channel ['thumb3'] = thz [2] ? thz [2] : default_thumb;
    }
  else
    {
    channel ['thumb1'] = channel ['thumb'] ? channel ['thumb'] : default_thumb;
    channel ['thumb2'] = default_thumb;
    channel ['thumb3'] = default_thumb;
    }
  if (! (channel ['curatordesc']))
    {
    // channel ['curatordesc'] = 'Default curator description';
    // channel ['curatordesc'] = '';
    }
/* TEMPORARY PATCH, THESE FIELDS ARE REVERSED */
if (channel ['pos'].match (/^f-/))
  {
  var temp = channel ['pos'];
  channel ['pos'] = channel ['id'];
  channel ['id'] = temp;
  }

  if (channel ['curatorthumb'] == '')
    channel ['curatorthumb'] = 'http://9x9ui.s3.amazonaws.com/brainy-mouse-small.jpg';
  return channel;
  }

function after_fetch_channels (ipg_flag)
  {
  log ('after fetch channels');

  log ('----- ipg_flag: ' + ipg_flag);
  log ('----- activated: ' + activated);
  log ('----- shared_but_is_now_logged_in: ' + shared_but_is_now_logged_in);
  log ('----- add_jumpstart_channel: ' + add_jumpstart_channel);
  log ('----- via_share: ' + via_share);

  if (via_share)
    {
    var query = "/playerAPI/loadShare?user=" + user + '&' + 'id=' + get_ipg_id() + mso() + rx();
    var d = $.get (query, function (data)
      {
      log ("LOAD SHARE, returned: " + data);
      player_mute_override = 'unmute';
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        {
        var fields = lines[2].split ('\t');
        jumpstart_channel = fields[0];
        jumpstart_program = fields[1];
        var channel_fields = lines[4].split ('\t');
        if (channel_fields[8] == '5')
          jumpstart_program = fields[0] + '.' + fields[1];
        after_fetch_channels_inner (ipg_flag);
        if (jumpstart_channel in pool)
          load_share_inner()
        else
          load_channel_then (jumpstart_channel, "load_share_inner()");
        }
      else
        notice_ok (thumbing, translations ['uncaught'] + " Code: " + fields[1], "store()");
      });
    }
  else
    after_fetch_channels_inner (ipg_flag);
  }

function load_share_inner()
  {
  program_lineup (jumpstart_channel);
  for (var i = 1; i <= n_program_line; i++)
    if (program_line[i] == jumpstart_program)
      program_cursor = i;
  store_last_channel = jumpstart_channel;
  store_last_program_index = program_cursor;
  log ('LOAD SHARE INNER: store_last_channel: ' + store_last_channel + ', store_last_program_index: ' + store_last_program_index);
  store();
  }

function after_fetch_channels_inner (ipg_flag)
  {
  if (!activated)
    activate();
  else if (shared_but_is_now_logged_in)
    {
    shared_but_is_now_logged_in = false; 
    af_thumbing = thumbing;
    // ask_question ("Congratuations! You have followed this channel successfully.", 
    //       "Watch now", "Smart Guide", "af_watch_now()", "switch_to_ipg()", 2);
    }
  else if (ipg_flag || custom_but_is_now_logged_in)
    {
    switch_to_ipg();
    }
  else
    {
    redraw_ipg();
    elastic();
    }

  if (do_this_after_fetch_channels)
    {
    var temp = do_this_after_fetch_channels;
    log ('doing this after fetch channels: ' + temp);
    do_this_after_fetch_channels = undefined;
    eval (temp);
    }

  if (!all_programs_fetched)
    setTimeout ("fetch_programs_piecemeal()", 10000);

  missing_youtube_thumbnails();
  }

function update_new_counters()
  {
  /* not needed with current UI */
  return;

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
      var nature = channelgrid [ch]['nature'];
      if (nature == '3' || nature == '4' || nature == '5')
        continue;
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
  var serialized = "channel=" + charray.join (',') + mso() + '&' + user_or_ipg() + rx();

  $.ajax ({ type: 'GET', url: "/playerAPI/programInfo", data: serialized, 
                dataType: 'text', success: fetch_piece_, error: fetch_piece_error_ });

  /* TODO */
  // pending_queries.push (d);
  }

function fetch_piece_ (data, textStatus, jqXHR)
  {
  var lines = data.split ('\n');
  var fields = lines[0].split ('\t');
  if (fields [0] == '0')
    {
    parse_program_data (data);
    // piece_count += charray.length;
    var n_channels = 0;
    for (var ch in channelgrid) { n_channels++; }
    // if (piece_count == n_channels)
    //  all_programs_fetched = true;
    }
  else
    log ('fetch piece failed: code ' + fields [0] + ': ' + fields[1]);
  }

function fetch_piece_error_ (jqXHR, textStatus, errorThrown)
  {
  log ('fetch piece failed: ' + textStatus);
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

  if (false && jumpstart_channel != '')
    {
    jumpstart();
    return;
    }

  current_channel = first_channel();
  program_cursor = 1;
  program_first = 1;
  current_program = first_program_in (current_channel);

  enter_channel ('player');

  document.onkeydown=kp;
  redraw_ipg();

  process_our_url();

  if (!process_our_url())
    finish_activation();
  }

function process_our_url()
  {
  var path = location.pathname;
  path = path.replace (/^\//, '');

  var has_custom_path = path != '' && path != '9x9' && path != '5f' && path != '9x9/' && path != '5f/' && !path.match (/^share\//);

  if (!custom_but_is_now_logged_in && (has_custom_path || (location.hash != '' && location.hash != '#')))
    {
    custom_url (path);
    return true;
    }

  return false;
  }

function finish_activation()
  {
  log ('finish activation');

  $("#blue").hide();

  $("body").focus();
  /* check_bandwidth(); */

  elastic();

  log ("FINISH ACTIVATION. location hash is: " + location.hash);

  if (location.hash.match (/\!guide/))
    guide();
  else if (location.hash.match (/\!home/))
    home();
  else if (location.hash.match (/\!directory/))
    browse();
  else if (location.hash.match (/\!settings/))
    settings();
  else if (location.hash.match (/\!resetpwd/))
    reset_password();
  else if (location.hash.match (/\!(aboot|about)/))
    {
    if (location.hash.match (/\!(about|aboot)=/))
      {
      var page = location.hash.match (/\!(about|aboot)=([^\&=]+)/) [2];
      log ("ABOOT: " + page);
      developer ("v-" + page);
      }
    else
      developer ("v-about");
    }
  else if ((location.hash).match (/\!curator=([^\&=]+)/))
    {
    var cur8 = (location.hash).match (/\!curator=([^\&=]+)/) [1];
    curation (cur8);
    }
  else if (location.hash.match (/\!share/))
    { /* do nothing here */ }
  else if (location.pathname == '/view' || location.pathname == '/flview')
    {
    jumpstart_inner();
    }
  else if (location.hash.match (/\!ch=/))
    {
    jumpstart_inner();
    }
  else if (location.hash.match (/\!\d+/))
    {
    var channel_id = location.hash.match (/\!(\d+)/) [1];
    if (thumbing == 'player' && player_real_channel == channel_id)
      {
      log ('we are already playing channel: ' + channel_id);
      if (program_line.length == 0)
        {
        /* this can happen during the fake first time login. repair for a race condition */
        program_lineup (channel_id);
        redraw_program_line();
        }
      }
    else
      {
      player_stack = [ undefined, pool [channel_id] ];
      player ("special", 1);
      }
    }
  else
    home();
  }

function custom_url (path)
  {
  log ('custom url: "' + path + '", with hash: "' + location.hash + '"');

  if ((location.hash).match (/\!landing=9x9$/))
    {
    /* backward compatibility */
    set_hash ('');
    return;
    }
  else if ((location.hash).match (/^#?_=_$/))
    {
    /* we were redirected from Facebook */
    set_hash ('');
    return;
    }
  else if ((location.hash).match (/\!share=([^\&=]+)/))
    {
    path = (location.hash).match (/\!share=([^\&=]+)/) [1];
    log ('share path: ' + path);
    via_share = true;
    via_share_path = path;
    after_fetch_channels();
    return;
    }
  else if ((location.hash).match (/\!(directory|home|about|aboot|curator|settings|resetpwd)/))
    {
    log ('special page: ' + location.hash);
    finish_activation();
    return;
    }
  else if ((location.hash).match (/\!landing=([^\&=]+)/))
    {
    path = (location.hash).match (/\!landing=([^\&=]+)/) [1];
    log ('landing path: ' + path);
    }
  else if (path == 'view' || path == 'flview' || location.hash != '')
    {
    view_landing();
    return;
    }
  else if (path == 'store')
    {
    store_landing();
    return;
    }

  log ('landing page: ' + path);

  var query = "/playerAPI/setInfo?landing=" + path + mso() + rx();
  var d = $.get (query, function (data)
    {
    log ("RESULT: |" + data + "|");
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields[0] == '0')
      {
      landing_pages [path] = {};
      landing_pages [path]['grid'] = [];

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
          {
          landing_page_override = fields [1];
          landing_pages [path]['setid'] = fields [1];
          }
        if (fields [0] == 'imageUrl')
          landing_pages [path]['setlogo'] = fields [1];
        if (fields [0] == 'piwik')
          landing_pages [path]['piwik'] = fields [1];
        }

      if ('piwik' in landing_pages [path])
        {
        set_pool [landing_pages [path]['setid']] = {};
        set_pool [landing_pages [path]['setid']]['piwik'] = landing_pages [path]['piwik'];
        }

      for (var k = j + 1; k < lines.length; k++)
        {
        if (lines [k] != '')
          {
          log ("Third block: " + lines [k]);
          var fields = lines[k].split ('\t');
          landing_pages [path]['grid'][parseInt (fields [0]) - 1] = fields [1];
          if (! (fields[1] in pool))
            {
            var channel = line_to_channel (lines[k]);
            pool [channel ['id']] = channel;
            }
          }
        }
      player_mute_override = 'unmute';
      store();
      }
    else
      {
      notice_ok (thumbing, translations ['badlanding'], "switch_to_ipg(); finish_activation()");
      }
    });
  }

function view_landing()
  {
  var path = location.search;

  if (path.match (/unsub/) && path.match (/receipt/))
    {
    var utoken = path.match (/receipt=([^\&=]+)/) [1];
    unsubscribe_from_mailings (utoken);
    return;
    }
  if (path.match (/with=ipg/) || location.hash.match (/#!guide/))
    {
    if (username == 'Guest' || username == '')
      signin_screen ("guide()");
    else
      guide();
    return;
    }

  var channel_id;
  if ((location.hash).match (/\!(ch|channel)=(\d+)/))
    channel_id = (location.hash).match (/\!(ch|channel)=(\d+)/) [2];
  else if (path.match (/(ch|channel)=(\d+)/))
    channel_id = path.match (/(ch|channel)=(\d+)/) [2];

  if ((location.hash).match (/#!(\d+)/))
    channel_id = (location.hash).match (/#!(\d+)/) [1];
 
  var episode_id;
  if ((location.hash).match (/\!(ep|episode)=([^\&=]+)/))
    episode_id = (location.hash).match (/\!(ep|episode)=([^\&=]+)/) [2];
  else if (path.match (/(ep|episode)=([^\&=]+)/))
    episode_id = path.match (/(ep|episode)=([^\&=]+)/) [2];

  jumpstart_channel = channel_id;
  jumpstart_program = episode_id;

  log ("VIEW LANDING: channel=" + channel_id + " episode=" + episode_id);

  load_channel_then (channel_id, "view_landing_inner()");
  }

function view_landing_inner()
  {
  log ('view landing inner');
  player_mute_override = 'unmute';
  load_programs_then (jumpstart_channel, "finish_activation()");
  }

function unsubscribe_from_mailings (utoken)
  {
  ask_question ("Would you like to opt-out of this email notification?",
          "Yes", "No", 'unsubscribe_from_mailings_inner("' + utoken + '")', "store()", 2);
  }

function unsubscribe_from_mailings_inner (utoken)
  {
  $("#waiting-layer").show();
  $.get ('/playerAPI/setUserPref?user=' + utoken + '&' + 'key=mailings' + '&' + 'value=no' + rx(), function (data)
    {
    $("#waiting-layer").hide();
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      notice_ok (thumbing, translations ['changessaved'], "store()");
    else
      notice_ok (thumbing, translations ['errupdate'] + ': ' + fields[1], "store()");
    });
  }

function store_landing()
  {
  log ('store landing jail');
  store_landing_jail = true;
  var path = location.search;
  if (path.match (/position=(\d+)/))
    ipg_cursor = path.match (/position=(\d+)/) [1];
  log ('ipg cursor set to: ' + ipg_cursor);
  $("#footer, #hint-layer, #hint-bubble").css ("visibility", "hidden");
  $("#nav").after ('<p id="btn-totv"><span>Back to TV &gt;</span></p>');
  $("#btn-totv").show();
  $("#nav").hide();
  $("#btn-totv").unbind();
  $("#btn-totv").click (function() { window.location.href = location.protocol + '//' + location.host + '/tv'; });
  store();
  }

function first_position_with_this_id (id)
  {
  for (var pos = 11; pos <= 99; pos++)
    if (pos in channelgrid && channelgrid [pos]['id'] == id)
      return pos;
  return 0;
  }

function jumpstart()
  {
  log ('JUMPSTART! channel: ' + jumpstart_channel + ' program: ' + jumpstart_program);

  /* nothing here? */
  if (jumpstart_channel == '') return;

  if (! (jumpstart_channel in pool))
    {
    jumpstart_try_again_with_channel();
    return;
    }

  program_lineup (jumpstart_channel);

  if (!jumpstart_program)
    jumpstart_program = program_line [1];

  jumpstart_program = dot_qualify_program_id (jumpstart_channel, jumpstart_program);
  // if (jumpstart_program in programgrid || !jumpstart_program)
  if (jumpstart_program in programgrid)
    {
    jumpstart_inner();
    return;
    }

  log ('loading programs in jumpstart channel ' + jumpstart_channel);
  load_programs_then (jumpstart_channel, "jumpstart_inner()");
  }

function basic_load_programs_then (channel_id, callback)
  {
  log ("BASIC LOAD PROGRAMS IN: " + channel_id);

  var nature = pool [channel_id]['nature'];

  if (nature != '3' && nature != '4' && nature != '5')
    {
    var query = "/playerAPI/programInfo?channel=" + channel_id + rx();
    var d = $.get (query, function (data)
      {
      parse_program_data (data);
      program_lineup (channel_id);
      eval (callback);
      });
    }
  else
    {
    if (fetch_youtube_or_facebook (channel_id, callback))
      return;
    eval (callback);
    }
  }

function load_programs_then (channel_id, callback)
  {
  var nature = pool [channel_id]['nature'];
  log ('jumpstart channel ' + channel_id + ' nature is: ' + nature);

  if (nature != '3' && nature != '4' && nature != '5')
    {
    $("#waiting-layer").show();
    var query = "/playerAPI/programInfo?channel=" + jumpstart_channel + mso() + '&' + user_or_ipg() + rx();
    var d = $.get (query, function (data)
      {
      $("#waiting-layer").hide();
      parse_program_data (data);

      program_lineup (channel_id);
      if ((jumpstart_program == undefined || jumpstart_program == '') && n_program_line > 0)
        jumpstart_program = program_line [1];

      if (jumpstart_program in programgrid)
        eval (callback);
      else if (('' + jumpstart_program + '-1') in programgrid)
        {
        jumpstart_program = '' + jumpstart_program + '-1'
        log ("sub-episodic program found: " + jumpstart_program);
        eval (callback);
        }
      else
        {
        log ('****************** jumpstart program is: ' + jumpstart_program + ' (apparently invalid)');
        notice_ok (thumbing, translations ['expired'], "guide()");
        }
      });
    }
  else
    fetch_youtube_or_facebook (channel_id, callback);
  }

function jumpstart_try_again_with_channel()
  {
  log ('jumpstart try again with channel');
  load_channel_then (jumpstart_channel, "jumpstart()");
  }

function load_channel_then (channel_id, callback)
  {
  var d = $.get ("/playerAPI/channelLineup?channel=" + channel_id + rx(), function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      log_and_alert ('server error: ' + lines [0]);
      notice_ok (thumbing, translations ['internal'] + '. Code: ' + lines[0], "guide()");
      return;
      }

    for (var i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var fields = lines[i].split ('\t');
        var channel = line_to_channel (lines[i]);
        pool [fields[1]] = channel;
        log ('added to pool: ' + fields[1]);
        }
      }

    eval (callback);
    });
  }

function jumpstart_inner()
  {
  if (!jumpstart_channel)
    {
    log ('jumpstart inner: no jumpstart channel!');
    return;
    }

  var grid = first_position_with_this_id (jumpstart_channel);

  program_lineup (jumpstart_channel);

  /* Youtubeisms */
  jumpstart_program = dot_qualify_program_id (jumpstart_channel, jumpstart_program);

  log ('-- js -- jumpstart_channel: ' + jumpstart_channel);
  log ('-- js -- jumpstart_program: ' + jumpstart_program);
  if (jumpstart_channel in pool && 'nature' in pool [jumpstart_channel])
    log ('-- js --    channel nature: ' + pool [jumpstart_channel]['nature']);
  log ('-- js --  at grid location: ' + grid);
  log ('-- js --    programs there: ' + programs_in_channel (grid));
  log ('-- js --   in program grid: ' + (jumpstart_program in programgrid));

  if (!jumpstart_program && n_program_line > 0)
    {
    jumpstart_program = program_line [1];
    log ('jumpstart had no episode, now set to: ' + jumpstart_program);
    }

  if (('' + jumpstart_program + '-1') in programgrid)
    {
    jumpstart_program = '' + jumpstart_program + '-1';
    log ('-- js --   program id fixed up to: ' + jumpstart_program);
    }

  if (! (jumpstart_program in programgrid))
    {
    log ('********************** jumpstart_inner: program expired: ' + jumpstart_program);
    log ("program line: " + program_line);
    notice_ok (thumbing, translations ['expired'], "guide()");
    return;
    }

  // set_channel_and_program (grid, jumpstart_program);
  prepare_real_channel (jumpstart_channel);
  current_program = jumpstart_program;
  for (var i = 1; i <= n_program_line; i++)
    if (program_line[i] == jumpstart_program)
      program_cursor = i;
  log ('program cursor: ' + program_cursor);
  redraw_program_line();
  jumpstarted_channel = jumpstart_channel;

  document.onkeydown=kp;
  $("#blue").hide();
  $("body").focus();

  var share_stack = [ undefined, pool [jumpstart_channel] ];

  store_last_channel = jumpstart_channel;
  store_last_program_index = program_cursor;

  jumpstart_channel = '';
  jumpstart_program = '';

  player_stack = share_stack;

  player ("share", 1);
  return;
 
  /* store_inner(); */
  // store_recommended (false);

  store_preview_type = 'share';
  store_cat = 0;
  store_recommend_index = 0;
  sharez = [];
  sharez [1] = {};
  sharez [1]['id'] = '0';
  sharez [1]['count'] = 1;
  sharez [1]['content'] = [];
  sharez [1]['content'][1] = pool [jumpstarted_channel];

  // store();
  /* store_set_channel (1, 1, jumpstarted_channel, program_cursor); */
  // store_recommend_off();
  }

function dot_qualify_program_id (ch, ep)
  {
  if ((ch + '.' + ep) in programgrid)
    {
    ep = ch + '.' + ep;
    log ('program id fixed up to: ' + ep);
    }
  return ep;
  }

function set_channel_and_program (channel, program)
  {
  log ('set channel (' + channel + ') and program (' + program + ')');

  current_channel = channel;

  /* temporarily first program */
  current_program = first_program_in (channel);
  program_cursor = 1;
  program_first = 1;

  enter_channel ('player');

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

function youtube_of (program)
  {
  if (program in programgrid)
    {
    var pinfo = programgrid [program];
    for (var field in { url1:0, url2:0, url3:0 })
      {
      if (field in pinfo && pinfo [field].match (/\?v=([^\&;]*)/))
        return pinfo [field].match (/\?v=([^\&;]*)/) [1];
      }
    }
  }

function recalculate_total_duration()
  {
  var total_duration = 0;

  var program = get_current_program();
  var segments = program ['segments'];

  for (var s in segments)
    {
    if (s > 0)
      total_duration += parseFloat (segments [s]['duration']);
    }

  program ['total-duration'] = total_duration;
  }

function get_current_program()
  {
  var pid = program_line [program_cursor];
  return programgrid [pid];
  }

function number_of_segments()
  {
  var program = get_current_program();
  return program ['segments'].length - 1;
  }

function get_current_segment()
  {
  var pid = program_line [program_cursor];
  var program = programgrid [pid];
  if (program)
    {
    var segments = program ['segments'];
    return segments [current_segment == 0 ? 1 : current_segment];
    }
  else
    {
    log ('get_current_segment(): no program! program_cursor=' + program_cursor + ', program_line=' + program_line);
    return undefined;
    }
  }

function get_current_clip()
  {
  var segment = get_current_segment();
  if (!segment)
    {
    log ('?! current segment is: ' + current_segment);
    var segments = get_current_program()['segments'];
    for (var i = 1; i < segments.length; i++)
      {
      var seg = segments [i];
      log ('segment ' + i + ': type=' + seg ['type'] + ', starting=' + seg ['starting'] + ', duration=' + seg ['duration']);
      }
    }
  if (segment ['type'] == 'video')
    return segment ['clip'];
  else
    return undefined;
  }

/* segment here should be optional, and refer to first */

function resolve_url (pid, segment)
  {
  var program = programgrid [pid];
  var segments = program ['segments'];
  var this_segment = segments [segment];
  if (this_segment ['type'] == 'video')
    {
    var clip = this_segment ['clip'];
    /* ignore remaining logic for now! */
    return clip ['url'];
    }
  else
    return undefined;
  }

function OLD_best_url (pid, segment)
  {
  var desired;
  var ret = '';

log ("best_url: " + pid + ', segment: ' + segment);

  if (! (program in programgrid))
    {
    log ('program "' + program + '" not in programgrid!');
    return '';
    }

  var program = programgrid [pid];
  var segments = program ['segments'];
  var this_segment = segments [segment];
  var clip = this_segment ['clip'];

  /* ignore remaining logic for now! */
  return clip ['url'];

  if (navigator.userAgent.match (/(GoogleTV|Droid Build)/i))
    desired = '(mp4|m4v)';

  else if (navigator.userAgent.match (/(Opera|Firefox)/))
    desired = 'webm';

  else if (navigator.userAgent.match (/(Safari|Chrome)/))
    desired = '(mp4|m4v)';

  var ext = new RegExp ('\.' + desired + '$');

  var p = programgrid [program];

  if (p ['url1'] && p ['url1'].match (ext))
    {
    ret = p ['url1'];
    }
  else if (p ['url2'] && p ['url2'].match (ext))
    {
    ret = p ['url2'];
    }
  else if (p ['url3'] && p ['url3'].match (ext))
    {
    ret = p ['url3'];
    }
  else if (p ['url4'] && p ['url4'].match (ext))
    {
    ret = p ['url4'];
    }
  else
    {
    for (var f in { url1:'', url2:'', url3:'', url4:'' })
      {
      var p = programgrid [program][f];
      if (p && ! (p.match (/^(|null)$/)))
        {
        ret = p;
        break;
        }
      }
    }

  if (ret.match (/;/))
    ret = ret.replace (/;.*/, '');

  return ret;
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
  $("#ear-left, #ear-right, #sg-bubble, #fb-bubble").hide();
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

  setTimeout ("switch_to_ipg()", 2500);
  stop_all_players();

  return;
  }

function ended_callback()
  {
  if (fake_timex)
    {
    log ('** cleared fake timex');
    clearTimeout (fake_timex);
    fake_timex = 0;
    }

  if (thumbing == 'program')
    {
    log ('** ended event fired, moving program right (cursor at ' + program_cursor + ')');
    /* program_right(); */
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

function empty_channel_timeout()
  {
  clear_msg_timex();
  log ('auto-switching from empty channel');
  channel_right();
  }

function update_bubble()
  {
  if (current_channel in channelgrid)
    {
    var channel_name = channelgrid [current_channel]['name'];
    if (channel_name.match (/^\s*$/)) { channel_name = '[no channel name]'; }
    $("#ep-layer-ch-title").html (channel_name);
    }
  else
    $("#ep-layer-ch-title").html ('');

  if (program_cursor in program_line)
    current_program = program_line [program_cursor];
  else
    current_program = undefined;

  if (current_program && current_program in programgrid)
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
  }

function prepare_channel()
  {
  n_program_line = 0;
  program_line = [];

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
  $("#ep-tip").hide();
  $("#ep-container").show();

  program_lineup (real_channel);

  if (thumbing != 'guide' && thumbing != 'guide-wait')
    $("#ep-layer").show();

  $("#player-ep-list .clickable").unbind();
  $("#player-ep-list .clickable").bind ('click', function() { player_ep_click ($(this).attr('id')); });
  $("#player-ep-list .clickable").hover (player_ep_hover_in, player_ep_hover_out);

  update_bubble();
  redraw_program_line();
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
    {
    log ('**** CHANNEL ' + real_channel + ' IS NOT IN POOL');
    return;
    }

  var nature = channel ['nature'];

  if (nature == '5' || nature == '8' || nature == '9' || (nature == '4' && channel ['sortorder'] != '2') || (nature == '6' && channel ['sortorder'] != '2'))
    {
    /* reverse order of position */
    log ('sort order: reverse position');
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [a]['sort']) - Math.floor (programgrid [b]['sort']) });
    program_line.unshift ('');
    }
  else if ((nature == '4' && channel ['sortorder'] == '2') || (nature == '6' && channel ['sortorder'] == '2'))
    {
    log ('sort order: position');
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [b]['sort']) - Math.floor (programgrid [a]['sort']) });
    program_line.unshift ('');
    }
  else if (channel ['sortorder'] == '2')
    {
    log ('sort order: reverse timestamp');
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [a]['timestamp']) - Math.floor (programgrid [b]['timestamp']) });
    program_line.unshift ('');
    }
  else
    {
    log ('sort order: timestamp');
    program_line = program_line.sort (function (a,b) { return Math.floor (programgrid [b]['timestamp']) - Math.floor (programgrid [a]['timestamp']) });
    program_line.unshift ('');
    }

  if (program_cursor > n_program_line)
    program_cursor = 1;
  }

function enter_channel (mode)
  {
  $("#epend-layer").hide();
  prepare_channel();
  redraw_program_line();
  thumbing = mode;
  }

function ep_html()
  {
  var html = '';
  var now = new Date();

  var bad_thumbnail = no_thumbnail_image;

  for (var i = program_first; i <= n_program_line && i < program_first + max_programs_in_line; i++)
    {
    if (i in program_line)
      {
      var program = programgrid [program_line [i]];

      var age = ageof (program ['timestamp'], false);
      var duration = durationof (program ['duration']);

      var classes = (i == program_cursor) ? 'on clickable' : 'clickable';
      var bg_ep = (i == program_cursor) ? 'bg_ep_off.png' : 'bg_ep_off.png'

      var thumbnail = 'umbrella-thumb' in program ? program ['umbrella-thumb'] : program ['thumb'];
      if (thumbnail == '' || thumbnail == 'null' || thumbnail == 'false')
        thumbnail = bad_thumbnail;

      var durtext = '<p class="duration"><span>' + duration + '</span>';
      if (duration == '0:00')
        durtext = '';

      // html += '<li class="' + classes + '" id="p-li-' + i + '"><img src="' + nroot + 'bg_ep_off.png" class="ep-off" id="ep-off-' + i + '"><img src="' + nroot + 'bg_ep_on.png" class="ep-on" id="ep-on-' + i + '"><img src="' + thumbnail + '" class="thumbnail">' + durtext + '</p></li>'
      html += '<li class="clickable" id="p-li-' + i + '"><img src="' + thumbnail + '" class="thumbnail"></li>';
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

  if (timestamp != '' && timestamp != 0 && timestamp != undefined && timestamp != 'null')
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

  var ret = (flag || age == 'long') ? (age + ' ' + ago_or_hence) : age;

  return (ret.match (/^long /) && language == 'zh') ? '許久以前' : ret;
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

/* old way, with 3x3 sets */
function first_empty_channel_setwise()
  {
  for (var set = 0; set <= 8; set++)
    {
    var corner = top_lefts [set];
    if (corner in set_ids && set_ids [corner] != 0)
      continue;
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

function first_empty_channel()
  {
  for (var x = 1; x <= 9; x++)
    for (var y = 1; y <= 9; y++)
      {
      var possible = x * 10 + y;
      if (! (possible in channelgrid))
        return possible;
      }
  log ("no empty channels!");
  return undefined;
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
    return undefined;
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

/* hope we can get rid of this */
function nth_program_in_real_channel (n, real_channel)
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
    log ('No programs in channel: ' + real_channel);
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

  return programs [n];
  }

function escape()
  {
  var layer;

  log ('escape!');
  $("#log-layer").hide();

  if (thumbing == 'guide-wait' || thumbing == 'player-wait' || thumbing == 'store-wait')
    return;

  if (thumbing == 'settings')
    {
    /* this screen is no longer an overlay, so don't close it when ESC hit. But close its overlays: */
    $("#settings-change-layer, #settings-notification-layer").hide();
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

  if (thumbing == 'email')
    {
    thumbing = saved_thumbing;
    $("#email-layer").hide();
    return;
    }

  if (thumbing == 'player')
    {
    player_minimize();
    return;
    }

  if (thumbing == 'nickname')
    {
    nickname_cancel();
    return;
    }

  if (thumbing == 'signin')
    {
    if ($("#forgot-password-layer").css ("display") == 'block')
      $("#forgot-password-layer").hide();
    else
      signin_close();
    return;
    }

  $("#msg-layer").hide();
  $("#epend-layer").hide();
  }

function notice_completed()
  {
  log ('confirm esc, setting: ' + after_confirm);
  $("#confirm-layer").hide();
  thumbing = after_confirm;
  if (after_confirm_function)
    eval (after_confirm_function);
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
  if ((thumbing == 'user' || thumbing == 'signin') 
         && keycode != 27 && keycode != 37 && keycode != 38 && keycode != 39 && keycode != 40 && keycode != 13)
    return;

  report ('k', keycode);

  if ($("#search-input").hasClass ("hasFocus"))
    {
    if (keycode == 13 || keycode == 121)
      perform_search ($("#search-input").val());
    return;
    }

  switch (keycode)
    {
    case 27:
      /* esc */
      if (store_landing_jail && (thumbing == 'store' || thumbing == 'store-wait'))
        window.location.href = location.protocol + '//' + location.host + '/tv';
      else if (thumbing == 'confirm')
        notice_completed();
      else
        escape();
      break;

    case 32:
      /* space */
    case 178:
      /* google TV play/pause */
      if (thumbing == 'store')
        store_play_or_pause();
      else if (thumbing == 'player')
        player_play_or_pause();
      break;

    case 13:
      /* enter */
    case 121:
      /* F10, remote control "OK" */
      if (thumbing == 'guide')
        {
        if (ipg_cursor in channelgrid)
          player ('guide');
        }
      else if (thumbing == 'confirm')
        escape();
      else if (thumbing == 'delete')
        delete_enter (delete_cursor);
      else if (thumbing == 'yes-or-no')
        yn_enter (yn_cursor);
      else if (thumbing == 'store' || thumbing == 'store-wait')
        {
        if ($("#search-input").hasClass ("hasFocus"))
          perform_search ($("#search-input").val());
        else if (store_tab == 'yourown')
          store_yourown_submit();
        }
      else if (thumbing == 'signin' && $("#signin-panel").css ("display") != 'none')
        {
        /* this shortcut lets you enter a password and then hit return */
        if ($("#signin-password").parent().hasClass ("on"))
          new_submit_login();
        }
      break;

    case 36:
      /* home key */
      switch_to_ipg();
      break;

    case 37:
      /* left arrow */
      if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        channel_left();
        enter_channel ('player');
        }
      else if (thumbing == 'guide')
        guide_left();
      else if (thumbing == 'browse')
        browse_left();
      else if (thumbing == 'user')
        user_left();
      else if (thumbing == 'delete')
        delete_left();
      else if (thumbing == 'yes-or-no')
        yn_left();
      else if (thumbing == 'player')
        flip_prev_episode();
      else if (thumbing == 'store')
        store_left();
      break;

    case 39:
      /* right arrow */
      if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        channel_right();
        enter_channel ('player');
        }
      else if (thumbing == 'guide')
        guide_right();
      else if (thumbing == 'browse')
        browse_right();
      else if (thumbing == 'user')
        user_right();
      else if (thumbing == 'delete')
        delete_right();
      else if (thumbing == 'yes-or-no')
        yn_right();
      else if (thumbing == 'player')
        flip_next_episode();
      else if (thumbing == 'store')
        store_right();
      break;

    case 38:
      /* up arrow */
      if (thumbing == 'program')
        {
        switch_to_ipg();
        }
      else if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        switch_to_ipg();
        }
      else if (thumbing == 'browse')
        browse_up();
      else if (thumbing == 'guide')
        guide_up();
      else if (thumbing == 'user')
        user_up()
      else if (thumbing == 'player')
        player_next();
      else if (thumbing == 'store')
        store_up();
      break;

    case 40:
      /* down arrow */
      if (thumbing == 'end')
        {
        $("#epend-layer").hide();
        enter_channel ('player');
        thumbing = 'program';
        }
      else if (thumbing == 'browse')
        browse_down();
      else if (thumbing == 'guide')
        guide_down();
      else if (thumbing == 'user')
        user_down()
      else if (thumbing == 'player')
        player_prev();
      else if (thumbing == 'store')
        store_down();
      break;

    case 33:
      /* PgUp */
      /* remote control channel up */
      if (thumbing == 'browse')
        browse_page_up();
      break;

    case 34:
      /* PgDn */
      /* remote control channel down */
      if (thumbing == 'browse')
        browse_page_down();
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
      break;

    case 77:
      /* M */
      if (store_landing_jail && (thumbing == 'store' || thumbing == 'store-wait'))
        {
        if (store_tab == 'yourown' || $("#search-input").hasClass ("hasFocus"))
          return;
        window.location.href = location.protocol + '//' + location.host + '/tv';
        }
      else
        log ('not in store landing jail');
      break;

    case 85:
      /* U */
      break;

    case 87:
      /* W */
      break;

    case 78:
      /* N */
      if (thumbing == 'ipg')
        ask_nickname();
      break;

    case 68:
      /* a */
      submit_user_report ("Automated comment");
      break;
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
      notice_ok (thumbing, translations ['allfull'], "");
      return;
      }
    }
  browse();
  }

function switch_to_ipg()
  {
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

function check_for_empty_clusters()
  {
  return;

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

      var d = $.get ("/playerAPI/setSetInfo?user=" + user + '&' + 'pos=' + pos + '&' + 'name=Set%20' + pos + mso() + rx(), function (data)
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

function top_left_to_server (pos)
  {
  for (var i in top_lefts)
    {
    if (pos == top_lefts [i])
      return parseInt (i)+1;
    }
  return -1;
  }

function redraw_ipg()
  {
  for (col = 1; col <= 8; col++)
    {
    var html = '';
    for (i = 1; i <= 9; i++)
      {
      var grid = col + "" + i;
      var channel = channelgrid [grid];
      if (channel)
        {
        var thumb = channel ['thumb'].split (/\|/)[0];
        html += '<li id="grid-' + grid + '" class="in draggable">';
        html += '<img src="' + thumb + '" class="thumbnail">';
        }
      else
        {
        html += '<li id="grid-' + grid + '" class="droppable">';
        html += '<p class="number"><span>' + grid + '</span></p>';
        }
      html += '<p class="btn-del-ch"><img src="' + nroot + 'btn_close.png" class="normal">';
      html += '<img src="' + nroot + 'btn_close_press.png" class="press"></p>';
      html += '<img src="' + nroot + 'bg_cell.png" class="bg-cell">';
      html += '<img src="' + nroot + 'icon_playlist.png" class="btn-watch">';
      html += '</li>';
      }
    $("#col-" + col + "-list").html (html);
    }

  redraw_ipg_events();
  }

function OLD_redraw_ipg()
  {
  var html = "", gggg = "";
  
  var bad_thumbnail = '<img src="' + no_thumbnail_image + '" class="thumbnail">';
  var add_channel = (language == 'zh' || language == 'zh-tw') ? 'add_channel_cn.png' : 'add_channel.png';

  for (var i in top_lefts)
    {
    if (!top_lefts.hasOwnProperty (i))
      continue;

    var ty = Math.floor (top_lefts[i] / 10);
    var tx = top_lefts[i] % 10;

    // var title = translations ['setnum'];
    // title = title.replace ('%d', parseInt (i) + 1);
    var title = 'Untitled';
    if (top_lefts [i] in set_titles)
      title = set_titles [top_lefts [i]];

    gggg += '<div id="box-' + top_lefts[i] + '" class="on">';
    gggg += '<img src="' + nroot + 'bg_folder.png" class="bg-set">';
    gggg += '<p class="set-title" id="title-' + top_lefts[i] + '"><span class="set-type">' + title + '</span></p>';
    gggg += '<ul>';

    html += '<div id="box-' + top_lefts[i] + '" class="on">';
    html += '<img src="' + nroot + 'bg_set.png" class="bg-set">';
    html += '<img src="' + nroot + 'icon_del_set.png" class="btn-del-set" id="del-set-' + top_lefts[i] + '">';
    html += '<div><p class="set-title" id="title-' + top_lefts[i] + '"><span class="set-type">' + title + '</span><img src="' + nroot + 'icon_downarrow.png" class="icon-downarrow"></p>';
    html += '<p class="zoom" id="magnifier-' + top_lefts[i] + '"><img src="' + nroot + 'icon_zoomin.png" class="icon-zoomin"><img src="' + nroot + 'icon_zoomout.png" class="icon-zoomout"></p></div>';
    html += '<ul>';

    var mov = '<img src="' + nroot + 'icon_move.png" class="icon-move">';
    var deloff = '<p class="btn-del-ch"><img src="' + nroot + 'btn_delete_off.png" class="off">';
    var delon = '<img src="' + nroot + 'btn_delete_on.png" class="on"></p>';
    var play = '<img src="' + nroot + 'icon_play.png" class="btn-preview">';

    var stuff = mov + deloff + delon + play;

    // var gmov = '<img src="' + nroot + 'icon_move.png" class="icon-move">';
    var gdeloff = '<p class="btn-del-ch"><img src="' + nroot + 'btn_delete_off.png" class="off">';
    var gdelon = '<img src="' + nroot + 'btn_delete_on.png" class="on"></p>';
    var gplay = '<img src="' + nroot + 'icon_play.png" class="btn-watch">';

    var gstuff = /* gmov + */ gdeloff + gdelon + gplay;

    for (y = ty; y < ty + 3; y++)
      for (x = tx; x < tx + 3; x++)
         {
         var yx = y * 10 + x;
         if (yx in channelgrid)
           {
           var channel = channelgrid [yx];
           var thumb = channel ['thumb'];
           if (thumb == '' || thumb == 'null' || thumb == 'false')
             thumb = no_thumbnail_image;

           var hasnew = channel ['updated'] > lastlogin; // channel ['new'] contains new count
           html += '<li class="clickable draggable" id="ipg-' + yx + '"><img src="' + thumb + '" class="thumbnail">' + stuff + '</li>';
           gggg += '<li class="clickable draggable" id="guide-' + yx + '"><img src="' + thumb + '" class="thumbnail">' + gstuff + '</li>';
           }
         else
           {
           html += '<li class="clickable droppable" id="ipg-' + yx + '"><img src="' + nroot + add_channel + '" class="add-ch"></li>';
           gggg += '<li class="clickable droppable" id="guide-' + yx + '"><img src="' + nroot + 'add_channel.png" class="add-ch"></li>';
           }
         }

    html += '</ul>';
    html += '</div>';

    gggg += '</ul>';
    gggg += '</div>';
    }

  $("#grid").html (gggg);

  if (ipg_cursor > 0)
    $("#guide-" + ipg_cursor).addClass ("on");

  ipg_metainfo();

  $("#guide-index .occupied").html (channels_in_guide() + "/" + 81);

  $("#grid .btn-del-ch").unbind();
  $("#grid .btn-del-ch").bind ('mouseup', function (event) { event.stopPropagation(); delete_this_channel ($(this).parent().attr("id")); });

  $("#grid .set-title").unbind();
  $("#grid .set-title").click (function() { title_click ($(this).attr ("id")); });

  $("#grid .clickable").unbind();
  // $("#grid .clickable").bind ('mouseup', function() { if (!dragging) guide_click ($(this).attr("id")); });
  $("#grid .clickable").click (function() { if (!dragging) guide_click ($(this).attr("id")); });
  $("#grid .clickable").hover (grid_hover_in, grid_hover_out);

  setup_draggables();
  }

function setup_draggables()
  {
  $(function()
    {
    log ('setup draggables');
    remove_draggables();
    $("#guide-holder .draggable").draggable({ zIndex: 9999, opacity: 1 });
    $("#guide-holder .droppable").droppable
         ({
             revert: "invalid",
             activate: function(event,ui) { ui.draggable.addClass ("noclick"); if (!dragging) { dragging = true; log ('dragstart'); $("#guide-bubble").hide(); } },
             deactivate: function(event,ui) { setTimeout ('drag_cleanup("' + ui.draggable.attr('id') + '")', 200); },
             accept: ".draggable",
             activeClass: "ui-state-hover",
             hoverClass: "ui-state-active",
             greedy: true,
             drop: function (event, ui)
                      { event.stopPropagation(); setTimeout ('move_channel ("' + ui.draggable.attr('id') + '", "' + $(this).attr('id') + '")', 20); }
         });
    });
  }

function remove_draggables()
  {
  $("#grid .draggable").draggable ("destroy");
  $("#grid .droppable").droppable ("destroy");
  }

function grid_hover_in()
  {
  log ('grid hover in: ' + $(this).attr("id"));
  var id = $(this).attr("id").replace (/^guide-/, '');
  ipg_metainfo (id);
  }

function grid_hover_out()
  {
  log ('grid hover out: ' + $(this).attr("id"));
  ipg_metainfo (ipg_cursor);
  }

function type_click (ev, ob)
  {
  if (n_browse == 0)
    {
    /* list not downloaded yet */
    return;
    }

  var offsets = $(ob).offset();

  var h = $(ob).height();
  var l = offsets.left - 5;
  var t = offsets.top - h;

  $(".set-options").show().css ({"left":l,"top":t});
  ev.stopPropagation();

  var cluster = $(ob).attr("id").replace (/^title-/, '');

  $(".set-options li").removeClass("on");
  if (cluster in set_types)
    $("#type-" + set_types [cluster]).addClass ("on");

  $(".set-options li").unbind();
  $(".set-options li").click (function()
    {
    var cat_id = $(this).attr("id").replace (/^type-/, '');
    var cat_name;
    if (cat_id == 0)
      cat_name = 'Anything';
    else if (cat_id == -1)
      cat_name = 'Friends and Family';
    else if (cat_id == -2)
      cat_name = 'My YouTube Favorites';
    else
      cat_name = categories_by_id [cat_id]['name'];

    set_types [cluster] = cat_id;
    set_titles [cluster] = cat_name;
    log ('category ' + cat_id + ': ' + cat_name);
    $(".set-options").hide();
    $(".set-options li").removeClass("on");
    $(this).addClass("on");
    redraw_ipg();

    for (var corner in top_lefts)
      if (top_lefts [corner] == cluster)
        {
        var d = $.get ("/playerAPI/setSetInfo?user=" + user + '&' + 'pos=' + (parseInt (corner) + 1) + '&' + 'set=' + cat_id + mso() + rx(), function (data)
          {
          });
        }
    });

  $("body").unbind();
  $("body").click (function() { $(".set-options").hide(); });
  }

function cursor_on (cursor)
  {
  // $("#ipg-" + cursor).addClass ((ipg_mode == 'edit') ? "editcursor" : "on");
  $("#ipg-" + cursor).addClass ("on");
  if (cursor in channelgrid && ('new' in channelgrid [cursor]) && channelgrid [cursor]['new'] > 0)
    $("#dot-" + cursor).attr ("src", nroot + "icon_reddot_on.png");
  }

function cursor_off (cursor)
  {
  // $("#ipg-" + cursor).removeClass ((ipg_mode == 'edit') ? "editcursor" : "on");
  $("#ipg-" + cursor).removeClass ("on");
  if (cursor in channelgrid && ('new' in channelgrid [cursor]) && channelgrid [cursor]['new'] > 0)
    $("#dot-" + cursor).attr ("src", nroot + "icon_reddot_off.png");
  }

function drag_cleanup (id)
  {
  id = id.replace (/^grid-/, '');
  $("#" + id).removeClass ("noclick");

  if (dragging)
    {
    dragging = false;
    log ("drag_cleanup, square: " + id);
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

  var copyflag = false;

  src = src.replace (/^grid-/, '');
  dst = dst.replace (/^grid-/, '');

  dst_cluster = which_cluster (dst);
  src_cluster = which_cluster (src);

  if (top_lefts [dst_cluster-1] in set_ids)
    {
    notice_ok (thumbing, translations ['liveedit'], "redraw_ipg()");
    return;
    }

  if (top_lefts [src_cluster-1] in set_ids)
    copyflag = true;

  log ('MOVE CHANNEL: ' + src + ' TO ' + dst + ' (id: ' + channelgrid [src]['id'] + ')');

  var query;
  if (copyflag)
    {
    var channel_id = channelgrid [src]['id'];
    query = '/playerAPI/copyChannel?user=' + user + mso() + '&' +
      'channel=' + channel_id + '&' + 'grid=' + server_grid(dst) + rx();
    }
  else
    query = '/playerAPI/moveChannel?user=' + user + mso() + '&' +
      'grid1=' + server_grid (src) + '&' + 'grid2=' + server_grid (dst) + rx();

  $("#waiting-layer").show();
  var d = $.get (query, function (data)
    {
    $("#waiting-layer").hide();

    log ('moveChannel raw result: ' + data);
    var fields = data.split ('\t');

    if (fields[0] == '0')
      {
      channelgrid [dst] = channelgrid [src];
      ipg_cursor = dst;
      $("#guide-" + ipg_cursor).addClass ("on");
      if (copyflag == '')
        delete (channelgrid [src]);
      check_for_empty_clusters();
      relay_post ("UPDATE");
      }
    else
      {
      notice_ok (thumbing, translations ['errormov'] + ': ' + fields [1], "");
      }

    redraw_ipg();
    elastic();

    ipg_sync();
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
  var youtube_completed = ('youtube_completed' in channel);
  var nature = channel ['nature'];

  log ('channel: ' + channel_id + ' :: was youtubed: ' + was_youtubed + ' youtube completed: ' + youtube_completed);
  if (was_youtubed && !youtube_completed && (nature == '3' || nature == '4' || nature == '5'))
    {
    log ('existing callback is: ' + fetch_yt_callbacks [channel_id]);
    log ('desired callback is: ' + callback);
    fetch_yt_callbacks [channel_id] += "; " + callback;
    return true;
    }

  channel ['youtubed'] = true;

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
var fb_fetch_timex;

function fetch_facebook_playlist (channel_id)
  {
  log ('fetch facebook playlist, channel id: ' + channel_id);

  var fbusername = pool [channel_id]['extra'];
  /* lingering slash at end */
  fbusername = fbusername.replace (/\/$/, '');

  metainfo_wait();

  var fields = fbusername.split ('/');
  fbusername = fields [fields.length - 1]; 

  log ("FETCHING FACEBOOK PLAYLIST: " + fbusername);

  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://graph.facebook.com/' + fbusername + '&' + 'callback=fb_fetched_1';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);

  fb_fetch_timex = setTimeout ('fb_fetched_1()', 15000);
  }

function fb_fetched_1 (data)
  {
  clearTimeout (fb_fetch_timex);
  fbdata = data;

  var channel;

  if (fbdata && 'link' in fbdata)
    {
    var link = fbdata ['link'].replace (/\/$/, '');
    var fields = link.split ('/');
    var expected_username = fields [fields.length - 1];

    var pattern = new RegExp ('^https?:\\/\\/(www\\.)?facebook\\.com\\/' + expected_username + '\\/?$', 'i');

    /* first, check the pool, it must receive precedence */
    for (var c in pool)
      {
      if (pool [c]['extra'].match (pattern))
        {
        log ('facebook fetched "' + fbdata ['link'] + '" channel info for pool channel: ' + c);
        channel = pool [c];
        break;
        }
      }

    if (!channel)
      {
      log ("******** unable to determine where channel goes: " + fbdata ['link']);
      $("#waiting-layer").hide();
      switch_to_ipg();
      return;
      }

    channel ['fbid'] = fbdata ['id'];
    }
  else
    {
    log ('******** link not in fbdata!');
    $("#waiting-layer").hide();
    notice_ok (thumbing, "An error on Facebook has occurred", "guide()");
    return;
    }

  if ('description' in fbdata)
    {
    channel ['desc'] = fbdata['description'].substring (0, 140);
    ipg_metainfo();
    }
  else if ('company_overview' in fbdata)
    {
    channel ['desc'] = fbdata['company_overview'].substring (0, 140);
    ipg_metainfo();
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
    $("#waiting-layer, #dir-waiting").hide();
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

        var sub = { url: link, duration: -1, name: title, desc: '', thumb: thumb };
        var segment = { type: 'video', duration: -1, clip: sub, starting: 0 };
        var segments = [ undefined, segment ];

        programgrid [program_id] = { 'channel': real_channel, 'url1': link, 
                                     'url2': '', 'url3': '', 'url4': '', 'name': title, 'desc': '', 'type': '',
                                     'thumb': thumb, 'snapshot': thumb, 'timestamp': timestamp, 'duration': -1,
                                     'fbmessage': fbmessage, 'fbid': fbid, 'fbfrom': fbfrom, 'sort': count, segments: segments };
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

  metainfo_wait();
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

  metainfo_wait();
  log ("FETCHING YOUTUBE CHANNEL: " + ytusername);
  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  // y.src = 'http://gdata.youtube.com/feeds/api/users/' + ytusername + '/uploads?v=2' + '&' + 'alt=json-in-script' + '&' + 'format=5' + '&' + 'orderby=published' + '&' + 'start-index=1' + '&' + 'max-results=50' + '&' + 'callback=yt_fetched';
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
      {
      name = ytid.match (/playlist:(.*)$/)[1];
      log ('is a playlist: ' + name);
      }

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
      log ("******** unable to determine where channel goes: " + name);
      return;
      }

    var entries = feed.entry || [];

    erase_programs_in (channel ['id']);

    for (var i = 0; i < entries.length; i++)
      {
      entry = entries[i];
      var id = entry.id.$t;
      var title = entry.title.$t;
      var updated = entry.updated.$t;

      var video_id = entry.media$group.yt$videoid.$t;
      var duration = entry.media$group.yt$duration.seconds;
      var desc = entry.media$group.media$description.$t;

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

      var url = 'http://www.youtube.com/watch?v=' + video_id;

      var sub = { url: url, duration: duration, name: title, desc: desc, thumb: thumb };
      var segment = { type: 'video', duration: duration, clip: sub, starting: 0 };
      var segments = [ undefined, segment ];

      var program_id = channel ['id'] + '.' + video_id;
      programgrid [program_id] = { id: program_id, 'channel': channel ['id'], 'url1': url,
                                   'url2': '', 'url3': '', 'url4': '', 'name': title, 'desc': desc, 'type': '',
                                   'thumb': thumb, 'snapshot': thumb, 'timestamp': ts, 'duration': duration, 'sort': i+1, segments: segments };
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

  /* not entirely sure if this assumption is always valid */
  if (ipg_cursor in channelgrid && channelgrid [ipg_cursor]['id'] == channel_id)
    {
    ipg_sync();
    ipg_metainfo();
    }

  $("#waiting-layer, #dir-waiting").hide();

  if (thumbing == 'guide-wait')
    thumbing = 'guide';

  if (channel_id in fetch_yt_callbacks)
    {
    var callback = fetch_yt_callbacks [channel_id];
    delete (fetch_yt_callbacks [channel_id]);
    pool [channel_id]['youtube_completed'] = true;
    log ("CALLBACK1 (" + channel_id + "): " + callback);
    eval (callback);
    }

  force_update_store_channel_bubble();
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
    if (programgrid [p]['channel'] == channel && ! (p.match (/\./)))
      delete (programgrid [p]);
    }
  }

function erase_all_unclean_youtube_channels()
  {
  for (var id in pool)
    {
    var ch = pool [id];
    var youtubed = ('youtubed' in ch);
    if ((ch ['nature'] == '3' || ch ['nature'] == '4' || ch ['nature'] == '5') && !youtubed)
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

function share_metainfo()
  {
  if (thumbing == 'sharelanding')
    {
    if (jumpstarted_channel in pool)
      {
      var channel = pool [jumpstarted_channel];
      $("#channel-title span").html (channel ['name']);
      $("#channel-description span").html (channel ['desc']);
      $("#eps-number").html (translations ['episodes'] + ': ' + channel ['count']);
      $("#updates").html (translations ['updated'] + ': ' + "Today");
      $("#channel-info").show();
      }
    else
      $("#channel-info").hide();
    }
  }

function ipg_metainfo (cursor)
  {
  log ('ipg metainfo');

  $("#chNum").html (channels_in_guide());
  $("#home-follow-count").html (channels_in_guide());

  if (!cursor)
    cursor = ipg_cursor;

  if (cursor in channelgrid)
    {
    var thumbnail = channelgrid [cursor]['thumb'];

    if (thumbnail == '' || thumbnail == 'null' || thumbnail == 'false')
      thumbnail = no_thumbnail_image;

    var name = channelgrid [cursor]['name'];
    if (name == '')
      name = '[no title]';

    /* new */
    $("#player-index .ch-title").html (name);
    $("#channel-info .ch-title span").html (name);

    var desc = channelgrid [cursor]['desc'];
    if (desc == undefined || desc == 'null')
      desc = '';

    /* new */
    $("#channel-info .ch-description span").html (desc);
    ellipses();

    var n_channels = 0;
    for (var c in channelgrid)
      {
      /* here, should filter out empty channels */
      n_channels++;
      }

    // $("#play-ch-index span").html (pos + " / " + n_channels);
    $("#play-ch-index span").html ("");

    var n_eps = programs_in_channel (cursor);
    var display_eps = n_eps;

    $("#channel-info .ch-epNum").hide();
    $("#channel-info .ch-updated").hide();

    $("#channel-info").show();

    // <p class="head"><span>Current Channel:</span></p>
    // <p class="ch-title"><span>Jazz</span></p>
    // <p class="ch-description"><span>Last installment from my "Making Money from Podcasting" series...</span></p>
    // <p class="ch-epNum"><span>Episode:</span><span class="amount">12</span></p>
    // <p class="ch-updated"><span>Updated:</span><span class="date new">Today</span></p>

    if (cursor == ipg_cursor)
      {
      log ('ipg-metainfo: fetching data for: ' + cursor);
      if (fetch_youtube_or_facebook (channelgrid [cursor]['id'], ""))
        return;
      }

    if (channelgrid [cursor]['count'] == undefined)
      {
      /* brackets quietly indicate a data inconsistency */
      if (debug_mode)
        display_eps = '[' + n_eps + ']';
      }
    else if (n_eps != channelgrid [cursor]['count'])
      {
      if (debug_mode)
        display_eps = channelgrid [cursor]['count'] + ' [' + n_eps + ']';

      if (ipg_mode != 'edit')
        {
        if (cursor == ipg_cursor && ! ('refetched' in channelgrid [cursor]))
          {
          channelgrid [cursor]['refetched'] = true;
          metainfo_wait();
          fetch_programs_in (channelgrid [cursor]['id']);
          }
        }
      }
    else
      display_eps = n_eps;

    if (n_eps > 0)
      {
      var first = first_program_in (cursor);
      var age = ageof (programgrid [first]['timestamp'], true);
      if (age != 'long ago')
        {
        $("#update-date").html (ageof (programgrid [first]['timestamp'], true));
        $("#update").show();
        }
      else
        $("#update").hide();
      /* new */
      if (age != 'long ago')
        {
        $("#updates").html (translations ['updated'] + ': ' + age);
        $("#channel-info .ch-updated span:nth-child(2)").html (age);
        $("#channel-info .ch-updated").show();
        $("#updates").show();
        }
      else
        {
        $("#updates").hide();
        $("#channel-info .ch-updated").hide();
        }
      }
    else
      {
      $("#channel-info .ch-updated").hide();
      }

    $("#channel-info .amount").html (display_eps);

    var plural = (n_eps == 1) ? translations ['episode'] : translations ['episodes'];
    $("#channel-info .ch-epNum span:nth-child(1)").html (plural + ':');
    $("#channel-info .ch-epNum").show();

    $("#channel-info").show();
    }
  else
    {
    if (cursor < 0)
      {
      $("#ch-thumb-img").attr ("src", "");
      $("#ch-name").html ('<p></p>');
      $("#ep-name").html ('<p></p>');
      }
    else
      {
      $("#ch-thumb-img").attr ("src", errorthumb);
      $("#ch-name").html ('<p>' + translations ['addchannel'] + '</p>');
      $("#ep-name").html ('<p>' + translations ['addtip'] + '</p>');
      }

    $("#description").html ('<p></p>');
    $("#ep-number").hide();
    $("#update").hide();

    if (thumbing == 'sharelanding')
      share_metainfo();
    else
      $("#channel-info").hide();
    }
  }

function metainfo_wait()
  {
  return;
  if (thumbing == 'guide')
    thumbing = 'guide-wait';
  }

function delete_set (id)
  {
  id = id.replace (/^del-set-/, '');
  log ('delete set: ' + id);
  if (id in set_ids && set_ids [id] != 0)
    {
    $("#waiting-layer").show();
    var cmd = "/playerAPI/unsubscribe?user=" + user + mso() + '&' + "set=" + set_ids [id] + rx();
    var d = $.get (cmd, function (data)
      {
      $("#waiting-layer").hide();
      var fields = data.split ('\t');
      if (fields [0] == '0')
        {
        delete (set_ids [id]);
        delete (set_titles [id]);
        channelgrid = {};
        relay_post ("UPDATE");
        fetch_channels();
        }
      else
        notice_ok (thumbing, "Error deleting set: " + fields [1], "ipg_exit_delete_mode()");
      });
    }
  else
    delete_set_the_hard_way (id);
  }

function delete_set_the_hard_way (id)
  {
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
         {
         if (channels != '')
           {
           channels += ',';
           seq += ',';
           }
         channels += channelgrid [y * 10 + x]['id'];
         seq += server_grid (y * 10 + x);
         }
       }

  $("#waiting-layer").show();

  var cmd = "/playerAPI/unsubscribe?user=" + user + mso() + '&' + "channel=" + channels + '&' + 'grid=' + seq + rx();
  var d = $.get (cmd, function (data)
    {
    $("#waiting-layer").hide();

    var fields = data.split ('\t');
    if (fields[0] != '0')
      {
      notice_ok (thumbing, "Error deleting: " + fields[1], "");
      return;
      }

    wipe();
    relay_post ("UPDATE");
    fetch_channels();
    });
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

  log ('ipg_sync, thumbing: ' + thumbing);

  if (thumbing == 'browse' || thumbing == 'browse-wait' || thumbing == 'add-youtube' || thumbing == 'tvpreview')
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

function episode_clicks_and_hovers()
  {
  log ('episode clicks and hovers');
  $("#player-ep-list .clickable").unbind();
  $("#player-ep-list .clickable").bind ('click', function() { player_ep_click ($(this).attr('id')); });
  $("#player-ep-list .clickable").hover (player_ep_hover_in, player_ep_hover_out);
  $("#arrow-left, #arrow-right").unbind();
  $("#arrow-left, #arrow-right").hover (arrow_hover_in, arrow_hover_out);
  $("#arrow-left, #arrow-right").click (arrow_click);
  $("#player-arrow-left, #player-arrow-right").unbind();
  $("#player-arrow-left, #player-arrow-right").click (player_arrow_click);
  }

function arrow_click()
  {
  var id = $(this).attr ("id");

  var old_program_first = program_first;

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

  if (program_first == old_program_first)
    {
    log ('program first has not changed');
    return;
    }

  program_cursor = program_first;
  redraw_program_line();
  player_metainfo();
  // $("#ep-list").html (ep_html());
  // $("#ep-list img").error(function () { $(this).unbind("error").attr("src", no_thumbnail_image); });
  // episode_clicks_and_hovers();

  log ('arrow click: playing episode #' + program_cursor);
  store_play_yt();

  report ('m', id + ' [' + thumbing + '] ' + id);
  }

function arrow_hover_in()
  {
  var id = $(this).attr ("id");
  if (id == 'arrow-left')
    $("#arrow-left").attr ("src", nroot + 'arrow_left_on.png');
  else if (id == 'arrow-right')
    $("#arrow-right").attr ("src", nroot + 'arrow_right_on.png');
  }

function arrow_hover_out()
  {
  var id = $(this).attr ("id");
  if (id == 'arrow-left')
    $("#arrow-left").attr ("src", nroot + 'arrow_left_off.png');
  else if (id == 'arrow-right')
    $("#arrow-right").attr ("src", nroot + 'arrow_right_off.png');
  }

function ipg_set_channel (grid)
  {
  var saved = thumbing;

  program_cursor = 1;
  program_first = 1;

  current_program = first_program_in (grid);

  current_channel = grid;
  enter_channel (thumbing);
  }

function ipg_program_tip()
  {
  }

function ipg_program_index()
  {
  $("#ep-list").html (ep_html());
  $("#ep-list li").removeClass ("on");
  $("#ep-layer").show();
  $("#ep-tip").hide();
  $("#ep-container").show();
  $("#ep-panel").attr ("src", nroot + 'ep_panel_on.png');
  }

function title_click (id)
  {
  id = id.replace (/^title-/, '');
  log ('title_click: ' + id);

  var title = set_titles [id];
  if (!title)
    title = "Untitled";
  log ('title now: ' + title);

  /* FIX: fixup, oops */
  // $("#rename-layer .rename-holder").attr ("id", "rename-holder");

  $("#rename-field").val (title);
  $("#rename-layer").show();

  $("#btn-rename-save").unbind();
  $("#btn-rename-save").bind ('click', function() { rename_set_save (id); });

  $("#btn-rename-cancel").unbind();
  $("#btn-rename-cancel").bind ('click', rename_set_cancel);

  $("#rename-input .textfield").unbind();
  $("#rename-input .textfield").keyup (function()
    {
    log ('rename input keydown');
    if ($("#rename-input .textfield").val().length > 0)
      $("#btn-rename-save").removeClass ("disable");
    else
      $("#btn-rename-save").addClass ("disable");
    });
  }

function rename_set_save (id)
  {
  log ('rename set!');
  $("#rename-layer").hide();

  for (var corner in top_lefts)
    if (top_lefts [corner] == id)
      {
      var newname = encodeURIComponent ($("#rename-field").val());
      var d = $.get ("/playerAPI/setSetInfo?user=" + user + '&' + 'pos=' + (parseInt (corner) + 1) + '&' + 'name=' + newname + mso() + rx(), function (data)
        {
        set_titles [id] = $("#rename-field").val();
        redraw_ipg();
        });
      }
  }

function rename_set_cancel()
  {
  $("#rename-layer").hide();
  }

var nn_save_thumbing;

function ask_nickname()
  {
  nn_save_thumbing = thumbing;
  thumbing = 'nickname';

  $("#rename-field").val ("Please type a nickname");
  $("#rename-layer").show();

  $("#btn-rename-save").unbind();
  $("#btn-rename-save").click (nickname_save);

  $("#btn-rename-cancel").unbind();
  $("#btn-rename-cancel").click (nickname_cancel);
  }

function nickname_save()
  {
  if ($("#rename-field").val() != 'Please type a nickname')
    relay_post ('NICKNAME ' + $("#rename-field").val());
  $("#rename-layer").hide();
  thumbing = nn_save_thumbing;
  }

function nickname_cancel()
  {
  $("#rename-layer").hide();
  thumbing = nn_save_thumbing;
  }

function channel_right()
  {
  current_channel = next_channel_square_setwise (current_channel);
  var real_channel = channelgrid [current_channel]['id'];

  log ('outer: current channel is: ' + current_channel);

  if (fetch_youtube_or_facebook (real_channel, "channel_right_inner(" + current_channel + ")"))
    return;

  channel_right_inner (current_channel);
  }

function channel_right_inner (channel)
  {
  current_channel = channel;
  enter_channel ('program');
  play_first_program_in (current_channel);
  relay_post ("PLAY " + channelgrid [current_channel]['id'] + ' ' + first_program_in (current_channel));
  }

function channel_left()
  {
  current_channel = previous_channel_square_setwise (current_channel);
  var real_channel = channelgrid [current_channel]['id'];

  if (fetch_youtube_or_facebook (real_channel, "channel_left_inner(" + current_channel + ")"))
    return;

  channel_left_inner (current_channel);
  }

function channel_left_inner (channel)
  {
  current_channel = channel;
  enter_channel ('program');
  play_first_program_in (current_channel);
  relay_post ("PLAY " + channelgrid [current_channel]['id'] + ' ' + first_program_in (current_channel));
  }

function truncated_name (name)
  {
  return (name.length > 60) ? (name.substring (0, 57) + '...') : name;
  }

function redraw_program_line()
  {
  log ('redraw program line');

  if (n_program_line == 0)
    {
    $("#player-ep-list").html ("");
    $("#arrow-left, #arrow-right, #player-arrow-left, #player-arrow-right, #player-ep-meta").hide();
    return;
    }

  $("#player-ep-meta").show();

  var redrawn = false;

  while (program_cursor < program_first)
    --program_first;

  while (program_cursor >= program_first + max_programs_in_line)
    ++program_first;

  $("#player-ep-list").html (ep_html());
  $("#player-ep-list img").error(function () { $(this).unbind("error").attr("src", no_thumbnail_image); });

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
    $("#arrow-left, #player-arrow-left").show();
  else
    $("#arrow-left, #player-arrow-left").hide();

  if (program_first + max_programs_in_line <= n_program_line)
    $("#arrow-right, #player-arrow-right").show();
  else
    $("#arrow-right, #player-arrow-right").hide();

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
      // log_and_alert ('404 Not Found');
      log_and_alert ("A temporary problem has been detected. If your session does not work properly, doing a reload may help. Code 404");
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

function deletecookie (id)
  {
  var expired = new Date (new Date().getTime() - 3600);
  document.cookie = id + "=; expires=" + expired.toGMTString();
  }

function setcookie (id, value)
  {
  document.cookie = id + "=" + value;
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

function sign_in_or_out()
  {
  if (username != 'Guest' && username != '')
    signout();
  else
    signin_screen();
  }

function signout()
  {
  suppress_success_dialog = false;

  /* blech */
  $("#account-dropdown").hide();

  if (username != 'Guest')
    {
    var d = $.get ("/playerAPI/signout?user=" + user + mso() + rx(), function (data)
      {
      set_hash ("");

      var lines = data.split ('\n');

      var fields = lines[0].split ('\t');
      if (fields [0] != '0')
        {
        log ('[signout] server error: ' + lines [0]);
        return;
        }

      log ('after signout, cookie is: ' + document.cookie);
      user = '';
      username = 'Guest';
      set_username();
      wipe();

      redraw_ipg();
      home();

      /* login(); */
      release_all();
      });
    }
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

function new_submit_login (callback_on_success)
  {
  var things = [];

  if (! $("#return-email").val().match (/\@/))
    {
    pw_signin_error (translations ['validmail']);
    return;
    }

  things.push ('email=' + encodeURIComponent ($("#return-email").val()));
  things.push ('password=' + encodeURIComponent ($("#return-password").val()));

  var serialized = things.join ('&') + mso() + rx();
  log ('login: ' + serialized);
  
  $("#waiting-layer").show();

  $.post ("/playerAPI/login", serialized, function (data)
    {
    $("#waiting-layer").hide();

    log ('new_submit_login raw data: ' + data);

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == "0")
      {
      $("#signin-layer").hide();
      set_hash ("");

      for (var i = 2; i < lines.length; i++)
        process_login_data_line (lines[i]);

      relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));

      set_username();
      log ('[explicit login] welcome ' + username + ', AKA ' + user);
      solicit();
      report ('u', 'login ' + user + ' ' + username);

      via_share = false;

      /* wipe out the current guest account program+channel data */
      wipe();

      if (false)
        {
        /* FORCE USE OF quickLogin. THIS WORKS, BUT IT IS RATHER SLOW. */
        do_this_after_fetch_channels = after_sign;
        after_sign = undefined;
        quicklogin (user);
        return;
        }

      activated = false;

      $("#waiting-layer").show();
      fetch_channels_then (function()
        {
        $("#waiting-layer").hide();
        if (reset_password_occurred)
          {
          /* nasty fixup -- required because new_signup callback fails when reentrant */
          reset_password_occurred = false;
          $("#signin-layer").hide();
          home();
          }
        if (typeof (callback_on_success) == 'function')
          callback_on_success();
        else
          eval (callback_on_success);
        });
      return;

      /* NOTREACHED */
      fetch_everything();

      if (saved_thumbing)
        {
        thumbing = saved_thumbing;
        saved_thumbing = undefined;
        }

      if (after_sign)
        {
        var temp = after_sign;
        after_sign = undefined;
        if (typeof (temp) == 'function')
          {
          log ('after sign: [function]');
          temp();
          }
        else
          {
          log ('after sign: ' + after_sign);
          eval (temp);
          }
        }
      }
    else if (fields[0] == '201')
      pw_signin_error (translations ['badpass']);
    else
      pw_signin_error (translations ['logfail'] + ': ' + fields [1]);
    })
  }

function process_login_data_line (line)
  {
  var fields = line.split ('\t');
  if (fields [0] == 'token')
    user = fields [1];
  else if (fields [0] == 'name')
    username = fields [1];
  else if (fields [0] == 'userid')
    userid = fields [1];
  else if (fields [0] == 'curator' || fields[0] == 'curatorid')
    curatorid = fields [1];
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
  else if (fields [0] == 'sphere')
    sphere = fields [1];
  else if (fields [0] == 'ui-lang')
    set_language (fields[1]);
  else if (fields [0] == 'resolution')
    yt_quality = fields [1];
  else if (fields [0] == 'preload')
    {
    preload = fields [1];
    if (preload != 'off')
      preload = 'on';
    }
  }

function notice_ok (whatnext, text, afterfunction)
  {
  after_confirm = whatnext;
  after_confirm_function = afterfunction;
  thumbing = 'confirm';
  $("#btn-confirm-close").unbind();
  $("#btn-confirm-close").click (notice_completed);
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

function new_submit_signup (callback_on_success)
  {
  var things = [];

  var gender = $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(3)").hasClass ("on") ? '1' : '0';

  things.push ('name=' + encodeURIComponent ($("#signup-name").val()));
  things.push ('email=' + encodeURIComponent ($("#signup-email").val()));
  things.push ('gender=' + gender);

  things.push ('password=' + encodeURIComponent ($("#signup-password").val()));
  // things.push ('captcha=' + encodeURIComponent (last_captcha));
  // things.push ('text=' + encodeURIComponent ($("#captcha-input .textfield").val()));

  if (! $("#signup-email").val().match (/\@/))
    {
    pw_signup_error (translations ['validmail']);
    return;
    }

  if ($("#signup-password").val() != $("#signup-password2").val())
    {
    pw_signup_error (translations ['passmatch']);
    return;
    }

  if ($("#signup-password").val().length < 6)
    {
    pw_signup_error (translations ['sixchar']);
    return;
    }

  var serialized = things.join ('&') + '&' + 'user=' + user + mso() + rx();
  log ('signup: ' + serialized);

  $("#waiting-layer").show();
  userid = undefined;

  $.post ("/playerAPI/signup", serialized, function (data)
    {
    $("#waiting-layer").hide();

    log ('signup response: ' + data);

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (fields [0] == "0")
      {
      $("#signin-layer").hide();

      set_hash ("");
      for (var i = 2; i < lines.length; i++)
        process_login_data_line (lines [i]);

      relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));

      set_username();
      log ('[login via signup] welcome ' + username + ', AKA ' + user);
      solicit();

      report ('u', 'signup ' + user + ' ' + username);

      via_share = false;

      /* wipe out the current guest account program+channel data */
      wipe();

      fetch_everything();

      if (saved_thumbing)
        {
        thumbing = saved_thumbing;
        saved_thumbing = undefined;
        }

      great_success();

      if (after_sign)
        {
        var temp = after_sign;
        log ('after sign: ' + after_sign);
        after_sign = undefined;
        eval (temp);
        }

      if (typeof (callback_on_success) == 'function')
        callback_on_success();
      else
        eval (callback_on_success);
      }
    else
      {
      if (fields[1])
        {
        if (fields[0] == '202')
          pw_signup_error (translations ['tooken']);
        else
          pw_signup_error (translations ['signupfail'] + ': ' + fields [1]);
        }
      else
        pw_signup_error (translations ['signupfail']);
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

function great_success()
  {
  if (suppress_success_dialog)
    return;

  var html = '';

  html += '<p class="greeting"><span>' + translations ['success'] + '</span></p>';
  html += '<p><span>' + translations ['signed1'] + '</span></p>';
  html += '<p><span>' + translations ['signed2'] + '</span></p>';
  html += '<ul class="action-list">';

  if (after_sign)
    html += '<li><p class="btn on" id="btn-success2store"><span>' + translations ['b_cont'] + '</span></p></li>';
  else
    html += '<li><p class="btn on" id="btn-success2store"><span>' + translations ['b_startadding'] + '</span></p></li>';

  html += '</ul>';

  $("#success-holder").html (html);
  $("#success-layer").show();

  $("#btn-success2store").unbind();
  $("#btn-success2store").click (function()
    {
    $("#success-layer").hide();
    if ($("#btn-success2store span") == 'Continue')
      return;
    else
      store();
    });
  }

function feedback (success, text)
  {
  $("#feedback, #yt-feedback").addClass (success ? "success" : "fail");
  $("#feedback, #yt-feedback").removeClass (success ? "fail" : "success");
  $("#feedback span, #yt-feedback span").html (text);
  $("#feedback, #yt-feedback").show();
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

  if (username == 'Guest')
    {
    log ("dirty: no longer logged in");
    return;
    }

  var cmd = "/playerAPI/programInfo?channel=" + channels + '&' + "user=" + user + mso() + rx();

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

  missing_youtube_thumbnails();
  }

function missing_youtube_thumbnails()
  {
  for (var c in channelgrid)
    {
    if (channelgrid [c]['thumb'].match (/processing\.png$/))
      youtube_thumbnail (c);
    }
  }

function youtube_thumbnail (grid)
  {
  var channel = channelgrid [grid];

  if (channel ['nature'] != '3')
    return;

  if (channel ['extra'] == '')
    return;

  log ("FETCHING YOUTUBE THUMBNAIL: " + channel ['extra']);

  var y = document.createElement ('script'); y.type = 'text/javascript'; y.async = true;
  y.src = 'http://gdata.youtube.com/feeds/api/users/' + channel ['extra'] + '?prettyprint=true' + 
              '&' + 'fields=author,title,updated,media:thumbnail' + '&' + 'alt=json-in-script' + '&' + 'callback=yt_thumbed';
  var s = document.getElementsByTagName ('script')[0]; s.parentNode.insertBefore (y, s);
  }

function yt_thumbed (data)
  {
  ytth = data;
  log ('got youtube thumb data');
  // ytth['entry']['media$thumbnail']['url']

  var channel;
  var name = data.entry.author[0].name.$t;
  name = name.toLowerCase();

  /* first, check the pool, it must receive precedence */
  for (var c in pool)
    {
    if (pool [c]['extra'].toLowerCase() == name)
      {
      log ('youtube fetched "' + name + '" channel info for pool channel: ' + c);
      channel = pool [c];
      break;
      }
    }

  if (channel)
    {
    channel ['thumb'] = data.entry.media$thumbnail.url;
    log ('new thumbnail: ' + channel ['thumb']);
    redraw_ipg();
    }
  else
    log ('cannot match youtube thumb data with a known channel');
  }

function pre_login()
  {
  log ('pre_login, obtaining brandInfo');

  var d = $.get ("/playerAPI/brandInfo?mso=" + brandinfo + rx(), function (data)
    {
    log ('obtained brandInfo');
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      for (var i = 2; i < lines.length; i++)
        {
        var fields = lines[i].split ('\t');
        if (fields[0] == 'read-only')
          {
          read_only = ( parseInt (fields[1]) > 0 );
          }
        else if (fields[0] == 'logoUrl')
          {
          log ('logo: ' + fields[1]);
          }
        else if (fields[0] == 'jingleUrl')
          {
          log ('jingle: ' + fields[1]);
          elastic();
          $("#blue").hide();
          $("#opening").css ("display", "block");

          // $("#splash").flash({ swf: fields[1], width: "100%", height: "100%", wmode: 'transparent' });

          /* temporary */
          jingle_timex = setTimeout ("jingle_completed()", 3000);
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
        else if (fields[0] == 'fbtoken')
          {
          fbtoken = fields[1];
          }
        else if (fields[0] == 'locale')
          {
          locale = fields[1];
          if (locale == 'en' || locale == 'zh')
            set_language (locale);
          }
        }
      login();

      /* this can be completed asynchronously */
      // var d = $.get ("/playerAPI/categoryBrowse?langCode=en" + mso() + rx(), function (data)
      //  {
      //  parse_categories (data);
      //  });
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
  log ('-- login --')
  var u = getcookie ("user");

  if (u)
    {
    log ('user cookie exists, checking');
    auxlogin (u);
    quicklogin (u);
    }
  else
    {
    if (first_time_user < 2)
      first_time_user = 1;

    if (via_share)
      log ('jumpstarting from this ipg: ' + get_ipg_id());

    /* first, try the veal */

    var gcookie = getcookie ("guest");

    if (gcookie)
      {
      var d = $.get ("/playerAPI/userTokenVerify?token=" + gcookie + mso() + rx(), function (data)
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
          deletecookie ("guest");
          become_a_guest();
          }
        });
      }
    else
      become_a_guest();
    }
  }

function auxlogin (u)
  {
  if (location.pathname == '/view' || location.pathname == '/flview')
    {
    log ('no auxlogin, since pathname is: ' + location.pathname);
    return;
    }

  if (location.hash == '' || location.hash == '#' || location.hash == '#!home')
    var d = $.get ("/playerAPI/auxLogin?token=" + u + rx(), function (data)
      {
      var blocks = data.split ('----\n');
      process_login_data (blocks[0]);
      set_username();
      process_channel_stack ("trending", blocks [1]);
      process_channel_stack ("hottest", blocks [2]);
      home();
      sound_effect_setup();
      });
  }

function quicklogin (u)
  {
  if (!the_very_first_time)
    $("#waiting-layer").show();

  var d = $.get ("/playerAPI/quickLogin?token=" + u + rx(), function (data)
    {
    $("#waiting-layer").hide();
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    log ('response to quickLogin: ' + lines[0]);

    if (fields[0] == '0')
      {
      proceed_with_quicklogin ('quicklogin via cookie', data);
      }
    else
      {
      if (u.length > 75)
        {
        log ("Facebook login seems to have failed? token=" + u)
        }
      if (debug_mode)
        log_and_alert ('User token was not valid');
      else
        log ('user token was not valid');
      deletecookie ("user");
      login();
      }
    });
  }

/* this should have a lot in common with quickLogin eventually */
function load_new_sphere_data()
  {
  log ('load new sphere data: ' + sphere);
  var d = $.get ("/playerAPI/category?lang=" + sphere + rx(), function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      process_top_level_categories (data);
      freshen_content();
      }
    });
  }

function set_username()
  {
  $("#user, #sg-user, #selected-profile").html (username);
  if (username == 'Guest')
    {
    $("#profile").hide();
    $("#signin").show();
    $("#btn-share").hide();
    }
  else
    {
    $("#signin").hide();
    $("#profile").show();
    $("#btn-share").show();
    }
  set_username_clicks();
  player_control_bindings();
  }

gblocks = [];
function proceed_with_quicklogin (how, data)
  {
  log ('user token was valid');
  first_time_user = 2;

  userid = undefined;

  var blocks = data.split ('----\n');
gblocks = blocks;

  process_login_data (blocks[0]);
  relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));

  set_username();
  solicit();

  log ('[' + how + '] welcome ' + username + ', AKA ' + user);

  add_to_known_users();

  if (false)
    {
    var query = '/playerAPI/listRecommended?' + 'lang=' + sphere + mso() + rx('');
    log ('slr_cache tamper: ' + query);
    slr_cache [query] = blocks[2];
    }

  if (false)
    {
    var lines = blocks[2].split ('\n');
    var first_row_fields = lines[2].split ('\t');
    var query = '/playerAPI/setInfo?set=' + first_row_fields[0] + '&' + 'lang=' + sphere + mso() + rx('');
    log ('rec_cache tamper: ' + query);
    rec_cache [query] = blocks[3];
    }

  process_featured_curators (blocks [2]);
  process_channel_stack ("trending", blocks [3]);
  process_channel_stack ("recommended", blocks [4]);
  process_channel_stack ("featured", blocks [5]);
  process_channel_stack ("hottest", blocks [6]);
  process_top_level_categories (blocks [7]);

  /* fetch_everything() replaced by this code */
  all_channels_fetched = false;
  all_programs_fetched = false;
  wipe();
  process_channel_lineup (blocks[1]);
  all_channels_fetched = true;
  if (add_jumpstart_channel)
    {
    add_jumpstart_channel_inner();
    return;
    }
  after_fetch_channels (false);

  /* now allow the user to do things */
  the_very_first_time = false;
  }

function process_login_data (data)
  {
  var lines = data.split ('\n');
  for (var i = 2; i < lines.length && lines [i] != '--'; i++)
    process_login_data_line (lines [i]);
  }

var featured_curators = [];

function process_featured_curators (data)
  {
  var html = '<li id="list-title">' + translations ['featcur'] + '</li>';
  var blocks = data.split ('--\n');

  var lines = blocks[1].split ('\n');
  var count = 0;
  for (var i = 0; i < lines.length && lines [i] != '--'; i++)
    {
    if (lines [i] != '')
      {
      var fields = lines [i].split ('\t');
      // ["1-1160", "twocw", "", "", "www.9x9.tv/curator/1", "0", "0", "0"]
      // curator id, curator name, curator description, curator image url, curator profile url, channel count, channel subscription count, follower count
      var curator = { id: fields[0], name: fields[1], desc: fields[2], thumb: fields[3], url: fields[4], 
                      count: fields[5], subscriptions: fields[6], followers: fields[7], topchan: fields[8] };
      featured_curators [++count] = curator;
      if (curator ['thumb'] != '')
        html += '<li id="featcur-' + count + '"><img src="' + curator ['thumb'] + '"></li>';
      else
        html += '<li id="featcur-' + count + '"><p><b>?</b></p></li>';
      }
    }
  $("#curator-list").html (html);

  if (blocks[2])
    {
    /* second block, containing any channels referenced in above "topchan" field */
    lines = blocks[2].split ('\n');
    for (var i = 0; i < lines.length; i++)
      if (lines [i] != '')
        {
        var channel = line_to_channel (lines[i]);
        pool [channel ['id']] = channel;
        }
    }

  featured_curator_events();
  }

function featured_curator_events()
  {
  $("#curator-list li").unbind();
  $("#curator-list li").mouseover (function()
    {
    $("#curator-list li").removeClass ("anchor").addClass ("fade");
    $(this).removeClass ("fade").addClass ("anchor");
    });
  $("#curator-list li").mouseout (function()
    {
    $("#curator-list li").removeClass ("fade");
    });
  $("#curator-list li").click (function() { featured_curator_click ($(this).attr("id")); });

  $("#curator-list img").unbind();
  $("#curator-list img").mouseover (function()
    {
    var bloc = $(this).offset();
    var bl = bloc.left - 4;
    $("#curator-bubble").show().css ({ left: bl });	
    var id = $(this).parent().attr("id").replace (/^featcur-/, '');
    log ("FEATURED CURATOR: " + id);
    var curator = featured_curators [id];
    $("#curator-info").attr ("data-id", curator ['id']);
    $("#curator-bubble #curator-thumb img").attr ("src", curator ['thumb']);
    $("#curator-name-bubble").text (curator ['name']);
    $("#curator-intro span").text (curator ['desc']);
    if (curator ['topchan'] && (curator ['topchan'] in pool))
      {
      var channel = pool [curator ['topchan']];
      log ("featured curator top channel: " + channel ['id']);
      var eplural = (channel ['count'] == 1) ? translations ['episode'] : translations ['episodes'];
      var vplural = (channel ['viewcount'] == 1) ? translations ['view'] : translations ['views'];
      var ago = ageof (channel ['timestamp'], true);
      $("#top-ch-name").text (translations ['mytopchannel'] + ': ' + channel ['name']);
      $("#curator-bubble .thumb1").attr ("src", channel ['thumb1']);
      $("#curator-bubble .thumb2").attr ("src", channel ['thumb2']);
      $("#curator-bubble .thumb3").attr ("src", channel ['thumb3']);
      $("#curator-ch-meta p").eq(1).html (channel ['count'] + ' ' + eplural + '<span class="divider">|</span>' + channel ['viewcount'] + ' ' + vplural);
      $("#curator-bubble .thumb .pl-sign span").html (ago);
      $("#curator-ch-meta, #curator-bubble .thumb1, #curator-bubble .thumb2, #curator-bubble .thumb3, #curator-bubble .icon-pl").show();
      }
    else
      {
      log ("featured curator has no top channel");
      $("#curator-ch-meta, #curator-bubble .thumb1, #curator-bubble .thumb2, #curator-bubble .thumb3, #curator-bubble .icon-pl").hide();
      }
    $("#icon-social").hide(); /* until it does something */
    ellipses();
    });
  $("#curator-list img").mouseout (function()
    {
    $("#curator-bubble").hide();
    });

  $("#curator-bubble").unbind();
  $("#curator-bubble").mouseover (function()
    {
    $(this).show();
    $("#curator-list li").addClass ("fade");
    });
  $("#curator-bubble").mouseout (function()
    {
    $(this).hide();
    $("#curator-list li").removeClass ("fade");
    $("#curator-info").unbind();
    $("#curator-info").click (function()
      {
      var id = $("#curator-info").attr ("data-id");
      log ('curator click: ' + id);
      $("#curator-bubble").hide();
      curation (id);
      });
    $("#curator-bubble .icon-pl").unbind();
    $("#curator-bubble .icon-pl").click (function()
      {
      var id = $("#curator-info").attr ("data-id");
      play_curator_channels (id);
      });
    });
  }

function play_curator_channels (id)
  {
  load_curator_then (id, function (curator_info, curator_channels)
    {
    // alert ("CURATOR: " + curator_info ['name'] + " CHANNELS: " + curator_info ['channels'].length);
    player_stack = curator_info ['channels'];
    var channel = curator_info ['channels'][1];
    if (channel ['id'].match (/^f-/))
      log_and_alert ("Top channel for this curator is magical! Cannot play.");
    else
      player ('curator-ch', 1);
    });
  }

function featured_curator_click (id)
  {
  id = id.replace (/^featcur-/, '');
  log ('featured curator click: ' + id);
  curation (featured_curators [id]['id']);
  }

var top_level_categories = [];

function process_top_level_categories (data)
  {
  var count = 0;
  var html = '';

  top_level_categories = [];
  $("#categ-list").html ("");

  var blocks = data.split (/^--$/m);

  var lines = blocks [0].split ('\n');
  var fields = lines[0].split ('\t');
  if (fields[0] != '0')
    return;

  var lines = blocks [2].split ('\n');
  for (var i = 0; i < lines.length; i++)
    {
    if (lines[i] != '')
      {
      var fields = lines[i].split ('\t');
      var cat = { id: fields[0], name: fields[1], count: fields[2], inside: fields[3], fetched: false, content: [], tags: [] };
      var name = cat ['name'];
      if (language == 'zh' && cat ['name'] in category_en_to_zh)
        name = category_en_to_zh [cat ['name']];
      if (language == 'en' && cat ['name'] in category_zh_to_en)
        name = category_zh_to_en [cat ['name']];
      top_level_categories [++count] = cat;
      html += '<li id="category-' + count + '"><p>' + name + '</p></li>';
      }
    }

  $("#categ-list").html (html);
  browse_init_clicks();
  }

function translate_top_level_categories()
  {
  $("#categ-list li p").each (function()
    {
    var name = $(this).text();
    if (language == 'zh' && name in category_en_to_zh)
      name = category_en_to_zh [name];
    if (language == 'en' && name in category_zh_to_en)
      name = category_zh_to_en [name];
    $(this).text (name);
    });
  }

function proceed_with_valid_login (how, lines)
  {
  log ('user token was valid');
  first_time_user = 2;

  userid = undefined;

  for (var i = 2; i < lines.length; i++)
    process_login_data_line (lines [i]);

  relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));

  set_username();
  solicit();

  log ('[' + how + '] welcome ' + username + ', AKA ' + user);
  fetch_everything();

  add_to_known_users();
  }

function add_to_known_users()
  {
  if (username != 'Guest')
    {
    for (var u in known_users)
      {
      if (known_users [u]['token'] == user)
        return;
      }
    log ('adding user to device: ' + user);
    var d = $.get ('/playerAPI/deviceAddUser?device=' + device_id + '&' + 'user=' + user + rx(), function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        retrieve_device_info();
      });
    }
  }

function become_a_guest()
  {
  username = 'Guest';
  userid = undefined;
  deletecookie ("user");
  user = '';
  set_username();

  log ('user cookie does not exist, obtaining one');

  args = via_share ? ('?ipg=' + get_ipg_id() + mso()) : ('?mso=' + sitename);

  var d = $.get ("/playerAPI/guestRegister" + args + rx(), function (data)
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

      /* proceed_with_valid_login ('guest login', lines); */
      // alert ('became a guest. quickLogin now. ' + u);
      auxlogin (u);
      quicklogin (u);
      }
    else if (fields [0] == '903')
      {
      activate();
      }
    else if (u)
      {
      log ('guest register failed, but user cookie now exists');
      user = u;
      username = u;
      set_username();
      solicit();
      via_share = false;
      relay_post ("CONTROLLER " + user + ' ' + encodeURIComponent (username));
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

var browse_menu;

function add()
  {
  if (ipg_mode == 'edit')
    ipg_exit_delete_mode();

  var current_cluster = which_cluster (ipg_cursor);
  var corner = top_lefts [current_cluster-1];

  if (! (corner in set_types))
    {
    log ('add, ' + corner + ' not in set_types');
    browse();
    return;
    }

  log ('add type: ' + set_types [corner]);

  if (set_types [corner] == 0)
    browse();
  else if (set_types [corner] == -1)
    browse();
  else if (set_types [corner] == -2)
    add_youtube();
  else
    tvpreview (set_types [corner], tvpreview_language, 1);
  }

var store_language = language;

function channels_in_guide()
  {
  var count = 0;
  for (var c in channelgrid)
    count++;
  return count;
  }

function guide()
  {
  guide_inner();
  relay_post ("IPG");
  }

function guide_inner()
  {
  if (username == 'Guest')
    {
    new_signup ("guide()");
    return;
    }

  log ('-- guide --');

  thumbing = 'guide';
  dragging = false;

  redraw_ipg();

  fresh_layer ("guide");
  set_hash ("#!guide");

  guide_stacks();

  $("#gt-list").css ("top", "0");
  $("#gr-list").css ("top", "0");

  if (!ipg_cursor || ! (ipg_cursor in channelgrid))
    ipg_cursor = first_channel();

  $("#guide-" + ipg_cursor).addClass ("on");

  // setup_draggables();

  stop_all_youtube_videos();
  stop_all_audio_tracks();

  ipg_metainfo();

  /* force a redraw of any pending recommended stack. can ignore result */
  if (recommended_stack_in_waiting)
    latest_recommended_stack();

  refresh_trending_status();

  start_guide_autorotation();

  $("#guide-add").unbind();
  $("#guide-add").click (add_your_own);
  }

function add_your_own()
  {
  var hint = "http://www.youtube.com/user/";
  input_init (hint, "#guide-add-url");

  $("#guide-add-layer").show();

  $("#guide-add-close").unbind();
  $("#guide-add-close").click (function()
    {
    $("#guide-add-layer").hide();
    });

  $("#guide-add-btn-holder").unbind();
  $("#guide-add-btn-holder").click (function()
    {
    var url = $("#guide-add-url").val();
    if (url != '' && url != hint && url.match (/^https?:\/\/(www.)?youtube\.com\//))
      store_yourown_submit_inner (url);
    else
      notice_ok (thumbing, 'Please provide a valid URL', "");
    });
  }

/* since these stacks are set earlier, they may now have obsolete data in them */
function refresh_trending_status()
  {
  $("#gt-list li").each (function()
    {
    var id = $(this).attr ("id").replace (/^guide-trending-/, '');
    var channel = trending_stack [id];
    var funf = (first_position_with_this_id (channel ['id']) > 0) ? translations ['unfollow'] : translations ['follow'];
    $(this).children (".guide-trending-box").children(".guide-trending-tab").children("span").html (funf);
    });
  $("#gr-list li").each (function()
    {
    var id = $(this).attr ("id").replace (/^guide-recommended-/, '');
    var channel = recommended_stack [id];
    var funf = (first_position_with_this_id (channel ['id']) > 0) ? translations ['unfollow'] : translations ['follow'];
    $(this).children (".guide-trending-box").children(".guide-trending-tab").children("span").html (funf);
    });
  }

var guide_timex;
var guide_rotations = 0;

function start_guide_autorotation()
  {
  clearTimeout (guide_timex);
  guide_timex = setTimeout ("autorotate_guide()", 6000);
  }

function autorotate_guide()
  {
  clearTimeout (guide_timex);
  if (++guide_rotations % 2 == 1)
    {
    var gt_num = $("#gt-list li").size() - 1;
    if (gt_cursor < gt_num)
      gt_next();
    else
      {
      gt_cursor = 0;
      gt_move();
      }
    }
  else
    {
    var gr_num = $("#gr-list li").size() - 1;
    if (gr_cursor < gr_num)
      gr_next();
    else
      {
      gr_cursor = 0;
      gr_move();
      }
    }
  if (thumbing == 'guide')
    guide_timex = setTimeout ("autorotate_guide()", 3000);
  }

var square_timex;

function OLD_square_in()
  {
  if (thumbing == 'guide' || thumbing == 'guide-wait')
    {
    clearTimeout (square_timex);
    $("#grid .clickable").removeClass ("on");
    $(this).addClass ("on");
    var id = $(this).attr ("id");
    ipg_cursor = id.replace (/^guide-/, '');
    ipg_metainfo();
    square_timex = setTimeout ("square_position()", 500);
    }
  }

function OLD_square_out()
  {
  // $(this).removeClass ("on");
  }

function square_position()
  {
  relay_post_position();
  }

/* one click model */
function guide_click (id)
  {
  guide_set_cursor (id);
  id = id.replace (/^guide-/, '');
  if (dragging)
    {
    log ('guide click: ' + id + ' -- but dragging is occurring, ignoring event');
    return;
    }
  log ('guide click: ' + id);
  if (ipg_cursor != last_sent_ipg_cursor)
    relay_post_position();
  if (id in channelgrid)
    player ('guide');
  }

/* two click model */
function OLD_guide_click (id)
  {
  id = id.replace (/^guide-/, '');
  log ('guide click: ' + id);
  if (id != ipg_cursor)
    {
    $("#guide-" + ipg_cursor).removeClass ("on");
    ipg_cursor = id;
    $("#guide-" + ipg_cursor).addClass ("on");
    ipg_metainfo();
    relay_post_position();
    }
  else
    {
    if (id in channelgrid)
      player ('guide');
    }
  }

function guide_up()
  {
  if (parseInt (ipg_cursor) > 20)
    {
    $("#guide-" + ipg_cursor).removeClass ("on");
    ipg_cursor = parseInt (ipg_cursor) - 10;
    $("#guide-" + ipg_cursor).addClass ("on");
    }

  log ("new ipg cursor: " + ipg_cursor);
  ipg_metainfo();
  relay_post_position();
  }

function guide_down()
  {
  if (parseInt (ipg_cursor) < 90)
    {
    $("#guide-" + ipg_cursor).removeClass ("on");
    ipg_cursor = parseInt (ipg_cursor) + 10;
    $("#guide-" + ipg_cursor).addClass ("on");
    }

  log ("new ipg cursor: " + ipg_cursor);
  ipg_metainfo();
  relay_post_position();
  }

function guide_left()
  {
  $("#guide-" + ipg_cursor).removeClass ("on");

  if (parseInt (ipg_cursor) == 11)
    ipg_cursor = 99;
  else if (parseInt (ipg_cursor) % 10 == 1)
    ipg_cursor = parseInt (ipg_cursor) - 2; /* 41 -> 39 */
  else
    ipg_cursor = parseInt (ipg_cursor) - 1;

  $("#guide-" + ipg_cursor).addClass ("on");
  ipg_metainfo();
  relay_post_position();
  }

function guide_right()
  {
  $("#guide-" + ipg_cursor).removeClass ("on");

  if (parseInt (ipg_cursor) == 99)
    ipg_cursor = 11;
  else if (parseInt (ipg_cursor) % 10 == 9)
    ipg_cursor = parseInt (ipg_cursor) + 2; /* 39 -> 41 */
  else
    ipg_cursor = parseInt (ipg_cursor) + 1;

  $("#guide-" + ipg_cursor).addClass ("on");
  ipg_metainfo();
  relay_post_position();
  }

function player_button()
  {
  if (recently_watched_channel_cursor)
    ipg_cursor = recently_watched_channel_cursor;
  player ('guide');
  }

var pop_with = '';

function player_pop_message (text)
  {
  if (pop_with != '')
    {
    $(pop_with + " .popmessage-middle").html (text);
    $(pop_with).fadeIn (300).delay (7000).fadeOut (300);
    }
  }

function player_column_title()
  {
  $("#pl-menu li").removeClass ("on");
  $("#pl-note").hide();

  if (player_mode == 'guide')
    {
    $("#myfollow2").addClass ("on");
    $("#pl-type").text (translations ['imfollowing']);
    $("#pl-note").text ('(' + translations ['followbychannel'] + ')');
    $("#pl-note").show();
    }
  else if (player_mode == 'updates')
    {
    $("#myfollow1").addClass ("on");
    $("#pl-type").text (translations ['imfollowing']);
    $("#pl-note").text ('(' + translations ['followbyupdate'] + ')');
    $("#pl-note").show();
    }
  else if (player_mode == 'browse')
    $("#pl-type").text (current_browse_cat ['name']);
  else if (player_mode == 'search')
    $("#pl-type").text (translations ['search'] + ': ' + last_search_term);
  else if (player_mode == 'hottest')
    $("#pl-type").text (translations ['hottest']);
  else if (player_mode == 'more')
    $("#pl-type").text ("More Recommendations");
  else if (player_mode == 'trending')
    {
    $("#trending").addClass ("on");
    $("#pl-type").text (translations ['trendstories']);
    }
  else if (player_mode == 'recommended')
    {
    $("#youmaylike").addClass ("on");
    $("#pl-type").text (translations ['youmaylike']);
    }
  else if (player_mode == 'featured')
    {
    $("#pl-type").text (translations ['featured']);
    }
  else if (player_mode == 'curator-ch' || player_mode == 'curator-follow')
    $("#pl-type").text (current_curator_name);
  else if (player_mode == 'special')
    $("#pl-type").text ("Special");
  else
    $("#pl-type").text (player_mode);
  }

/* player
   uses player_cursor except if guide mode, in which case uses ipg_cursor
   player_stack must be set up beforehand */

var player_mode = 'guide';
var player_real_channel;
var player_stack = [];
var player_cursor;

function player (mode, cursor)
  {
  log ('-- player --');
  thumbing = 'player';

  close_all_dropdowns();

  var channel;
  player_mode = mode;

  player_column_title();

  if ((player_mode == 'guide' || player_mode == 'updates') && channels_in_guide() == 0)
    {
    /* If a login is in process (Guest clicked on "9" button), we may not have the channels yet */
    fetch_channels_then (function() { player (mode, cursor); });
    return;
    }

  if (player_mode == 'updates')
    {
    player_stack = generate_updates_stack();
    }

  redraw_player_column();

  if (player_mode == 'guide')
    {
    if (cursor && cursor in channelgrid)
      ipg_cursor = cursor;

    if (!ipg_cursor || ! (ipg_cursor in channelgrid))
      ipg_cursor = first_channel();

    if (!ipg_cursor)
      {
      notice_ok (thumbing, translations ['nochanguide'], "store()");
      return;
      }
    current_channel = ipg_cursor;
    channel = channelgrid [ipg_cursor];

    player_real_channel = add_to_history (channel ['id']);
    enter_channel ('player');

    $("#channel-" + ipg_cursor).addClass ("on");
    scroll_player_column_to (ipg_cursor);
    }
  else
    {
    player_cursor = cursor;
    if (!player_cursor)
      player_cursor = 1;
    if (player_cursor in player_stack && player_stack [player_cursor] && 'id' in player_stack [player_cursor])
      {
      player_real_channel = add_to_history (player_stack [player_cursor]['id']);
      prepare_real_channel (player_real_channel);
      channel = pool [player_real_channel];
      $("#channel-" + cursor).addClass ("on");
      scroll_player_column_to (cursor);
      }
    else
      {
      log ("ASSERT: PLAYER CURSOR IS NOT IN PLAYER STACK! stack:" + player_mode + " player_cursor:" + player_cursor);
      player_real_channel = undefined;
      return;
      }
    }

  /* this may not have the data yet */
  goto_left_off_point();

  current_segment = 0;
  GNU_player_metainfo (player_real_channel);

  $("#loaded").css ("width", '100%');
  $("#played").css ("width", '0%');

  $("#player-video").css ({ height: "19.6875em", top: "50%" });

  $(".stage").hide();
  fresh_layer ("player");
  $("#player-ch-info, #video-layer, #control-bar").show();

  player_minimize();
  redraw_subscribe();

  $("#hint-layer, #hint-bubble, #search-layer, #guide-layer, #sync-dropdown, #flip-bubble, #add-bubble, #setbubble, #setbubbleclick").hide();

  mini_player = 1;
  player_mute = false;

  for (i = 1; i <= 3; i++)
    ytmini[i] = ytmini_video_id[i] = ytmini_why[i] = undefined;

  tvpreview_kickstart = false;
  store_yt_init();

  player_control_bindings();

  $("#play-next, #btn-flip-play").unbind();
  $("#play-next, #btn-flip-play").click (player_next);

  $("#play-prev").unbind();
  $("#play-prev").click (player_prev);

  $("#btn-play").hide();
  $("#btn-pause").show();

  $("#ch-source span").unbind();
  $("#ch-source span").click (visit_youtube_episode_externally);

  $("#video-source span").unbind();
  $("#video-source span").click (function() { window.open ("http://www.youtube.com/", "_blank"); });

  $("#curator-source span").unbind();
  $("#curator-source span").click (visit_youtube_channel_externally);

  $("#curator-photo, #curator-name").unbind();
  $("#curator-photo, #curator-name").click (visit_current_curator);

  flippr_buttons();
  column_buttons();
  search_buttons();

  player_metainfo();

  if (fetch_youtube_or_facebook (player_real_channel, "player_start_inner(" + ipg_cursor + ")"))
    return;

  if (use_programinfo (player_real_channel) && programs_in_real_channel (player_real_channel) < 1)
    {
    $("#waiting-layer").show();
    var cmd = "/playerAPI/programInfo?channel=" + player_real_channel + rx();
    var d = $.get (cmd, function (data)
      {
      $("#waiting-layer").hide();
      parse_program_data (data);
      player_start_inner (player_mode == 'guide' ? ipg_cursor : player_cursor);
      });
    }
  else
    player_start_inner (player_mode == 'guide' ? ipg_cursor : player_cursor);
  }

function flippr_buttons()
  {
  $("#flipr-playpause").unbind();
  $("#flipr-playpause").click (player_play_or_pause);
  $("#flipr-playpause").mouseover (function() { $("#flipr").addClass ("center"); });
  $("#flipr-playpause").mouseout (function() { $("#flipr").removeClass ("center"); });
  $("#flipr-playpause").mousedown(function() { $("#flipr").addClass ("center"); });
  $("#flipr-playpause").mouseup (function() { $("#flipr").removeClass ("center"); });

  $("#flipr-arrowup, #bar-arrowup").unbind();
  $("#flipr-arrowup, #bar-arrowup").click (player_prev);
  $("#flipr-arrowup, #bar-arrowup").mouseover (function() { $("#flipr, #bar-controller").addClass("up"); });
  $("#flipr-arrowup, #bar-arrowup").mouseout  (function() { $("#flipr, #bar-controller").removeClass("up"); });
  
  $("#flipr-arrowdown, #bar-arrowdown").unbind();
  $("#flipr-arrowdown, #bar-arrowdown").click (player_next);
  $("#flipr-arrowdown, #bar-arrowdown").mouseover (function() { $("#flipr, #bar-controller").addClass("down"); })
  $("#flipr-arrowdown, #bar-arrowdown").mouseout  (function() { $("#flipr, #bar-controller").removeClass("down"); });

  $("#flipr-arrowleft, #btn-ep-left").unbind();
  $("#flipr-arrowleft, #btn-ep-left").click (flip_prev_episode);
  $("#flipr-arrowleft, #btn-ep-left").mouseover (function() { $("#flipr").addClass ("left"); $("#btn-ep-left").addClass ("on"); });
  $("#flipr-arrowleft, #btn-ep-left").mouseout  (function() { $("#flipr").removeClass ("left"); $("#btn-ep-left").removeClass ("on"); });

  $("#flipr-arrowright, #btn-ep-right").unbind();
  $("#flipr-arrowright, #btn-ep-right").click (flip_next_episode);
  $("#flipr-arrowright, #btn-ep-right").mouseover (function() { $("#flipr").addClass ("right"); $("#btn-ep-right").addClass ("on"); });
  $("#flipr-arrowright, #btn-ep-right").mouseout  (function() { $("#flipr").removeClass ("right"); $("#btn-ep-right").removeClass ("on"); });
  }

function column_buttons()
  {
  $("#trending").unbind();
  $("#trending").click (player_trending);

  $("#youmaylike").unbind();
  $("#youmaylike").click (player_recommended);

  $("#myfollow1").unbind();
  $("#myfollow1").click (function()
    {
    if (username == 'Guest')
      new_signup ('player ("updates", 1)');
    else
      player ("updates", 1);
    });

  $("#myfollow2").unbind();
  $("#myfollow2").click (function()
    {
    if (username == 'Guest')
      new_signup ('player ("guide", 1)');
    else
      player ("guide", 1);
    });
  }

function reset_player_stack()
  {
  try { $("#pl-slider .slider-vertical").slider ("destroy"); } catch (error) { log ("error destroying pl-slider"); };
  $("#pl-list").html ("");
  $("#pl-list").css ("top", "0");
  }

function player_trending()
  {
  log ('trending');
  player_stack = trending_stack;
  reset_player_stack();
  player ("trending", 1);
  }

function player_recommended()
  {
  log ('recommended');
  player_stack = latest_recommended_stack();
  reset_player_stack();
  player ("recommended", 1);
  }

function player_featured()
  {
  log ('featured');
  player_stack = featured_stack;
  reset_player_stack();
  player ("featured", 1);
  }

function scroll_player_column_to (id)
  {
  var amt = $("#channel-" + id).position().top;
  log ('--scroll :: offset top: ' + amt);
  var h = $("#pl-list").height() - $("#pl-constrain").height();
  log ('--scroll :: height: ' + h);
  var pct = 100 * amt / h;
  log ('--scroll :: percent: ' + pct);
  $("#pl-slider .slider-vertical").slider ("value", 100-pct);
  }

function redraw_player_column()
  {
  var html = '';

  $("#pl-menu li .manage-tip").css ("visibility", "visible");

  if (player_mode == 'guide')
    $("#myfollow2 .manage-tip").css ("visibility", "hidden");
  else if (player_mode == 'updates')
    $("#myfollow1 .manage-tip").css ("visibility", "hidden");
  else if (player_mode == 'recommended')
    $("#youmaylike .manage-tip").css ("visibility", "hidden");
  else if (player_mode == 'trending')
    $("#trending .manage-tip").css ("visibility", "hidden");

  if (player_mode == 'guide')
    {
    var count = 0;
    for (var i = 11; i <= 99; i++)
      {
      if (channelgrid [i])
        {
        var channel = channelgrid [i];
        html += player_column_shelf_html (channel, i, i);
        }
      }
    }
  else
    {
    for (var i = 1; i < player_stack.length; i++)
      {
      var channel = player_stack [i];
      var display_number = (player_mode == 'updates') ? first_position_with_this_id (channel ['id']) : i;
      html += player_column_shelf_html (channel, i, display_number);
      }
    }

  player_column_highlight();

  $("#pl-list").html (html);

  $("#pl-list li").unbind();
  $("#pl-list li").click (function() { player_shelf_click ($(this).attr ("id")); });

  $("#pl-list li .btn-quickfollow").unbind();
  $("#pl-list li .btn-quickfollow").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().attr("id").replace (/^channel-/, '');
    log ('player quickfollow: ' + id);
    var channel = player_stack [id];
    pop_with = "#popmessage-player-list";
    browse_accept (channel ['id'], function() { log ("GREAT SUCCESS"); });
    });

  $("#pl-list li .pl-curator").unbind();
  $("#pl-list li .pl-curator").click (function (event)
    {
    event.stopPropagation();
    var id = $(this).parent().parent().attr("id").replace (/^channel-/, '');
    log ('player curator: ' + id);
    var channel = player_stack [id];
    curation (channel ['curatorid']);
    });

  setTimeout ("player_sidebar_adjust()", 10);
  }

function player_sidebar_adjust()
  {
  var wh = $(window).height();
  var sh = wh - 130;
  var bh = wh - 260;
  $("#pl-constrain").css ({ height: sh });
  $("#player-sidebar .slider-wrap").css ({ height: bh });
  scrollbar ("#pl-constrain", "#pl-list", "#pl-slider");
  log ('*** sidebar adjust');
  player_column_highlight();
  scroll_player_column_to (player_mode == 'guide' ? ipg_cursor : player_cursor);
  }

function player_column_highlight()
  {
  $("#pl-list li").removeClass ("on");
  if (player_mode == 'guide')
    $("#channel-" + ipg_cursor).addClass ("on");
  else
    $("#channel-" + player_cursor).addClass ("on");
  }

var updates_stack = [];

function generate_updates_stack()
  {
  updates_stack = [];
  for (var i = 11; i <= 99; i++)
    {
    if (i in channelgrid)
      updates_stack.push (channelgrid [i]);
    }
  updates_stack = updates_stack.sort 
     (function (a,b) { return Math.floor (b ['timestamp']) - Math.floor (a ['timestamp']) });
  updates_stack.unshift ('');
  return updates_stack;
  }
 
function redraw_subscribe()
  {
  if (thumbing == 'player')
    {
    redraw_player_subscribe_button();
    if (player_mode != 'guide')
      adjust_column_of_subscriptions ("#pl-list", player_stack);
    }
  else if (thumbing == 'home')
    adjust_column_of_subscriptions ("#home-billboard", which_billboard_stack (home_stack_name));
  }

function redraw_player_subscribe_button()
  {
  if (first_position_with_this_id (player_real_channel) > 0)
    {
    $("#btn-follow").addClass ("followed");
    $("#btn-follow span").text (translations ['unfollow']);
    $("#btn-follow").unbind();
    $("#btn-follow").click (function ()
      {
      var next_cursor;
      if (player_mode == 'guide' || player_mode == 'updates')
        {
        if (player_mode == 'guide')
          next_cursor = next_channel_square (ipg_cursor);
        else
          next_cursor = (player_cursor + 1 < player_stack.length) ? player_cursor + 1 : 1;
        }
      pop_with = "#popmessage-player-info";
      unfollow (player_real_channel, function()
        {
        /* switch to the next channel, as remembered above, since staying here loses navigation ability */
        if (next_cursor)
          player (player_mode, next_cursor);
        });
      });
    }
  else
    {
    $("#btn-follow").removeClass ("followed");
    $("#btn-follow span").text (translations ['followthischannel']);
    $("#btn-follow").unbind();
    $("#btn-follow").click (function ()
      {
      pop_with = "#popmessage-player-info";
      browse_accept (player_real_channel);
      });
    }
  }

function adjust_column_of_subscriptions (where, stack)
  { 
  $(where + " li").each (function()
    {
    var id = $(this).attr ("id").replace (/^channel-/, '');
    var channel = stack [id];
    if (first_position_with_this_id (channel ['id']))
      $(this).children (".btn-quickfollow").css ("visibility", "hidden");
    else
      $(this).children (".btn-quickfollow").css ("visibility", "visible");
    });
  }

function player_shelf_click (id)
  {
  id = id.replace (/^channel-/, '');
  log ("player shelf click: " + id);
  player (player_mode, parseInt (id));
  }

function player_column_shelf_html (channel, use_this_id, display_number)
  {
  var html = '';

  if (!channel) return "";
  var ago = ageof (channel ['timestamp'], true);

  html += '<li id="channel-' + use_this_id + '">';
  if (!first_position_with_this_id (channel ['id']))
    html += '<p class="btn-quickfollow" style="visibility: visible"></p>';
  else
    html += '<p class="btn-quickfollow" style="visibility: hidden"></p>';
  html += '<p class="pl-hilite"><span class="now-playing">' + translations ['nowplaying'] + '</span></p>';
  html += '<p class="pl-title-line"><span class="pl-number">' + display_number + '.</span><span class="pl-title">' + channel ['name'] + '</span></p>';
  html += '<p class="pl-curator-line"><span>' + translations ['curatorby'] + '</span><span class="pl-curator">' + channel ['curatorname'] + '</span></p>';
  html += '<p class="icon-pl"></p>';
  html += '<img src="' + channel ['thumb1'] + '" class="thumb1"><img src="' + channel ['thumb2'] + '" class="thumb2"><img src="' + channel ['thumb3'] + '" class="thumb3">';
  html += '<p class="pl-sign"><span>' + ago + '</span></p>';
  html += '</li>';

  return html;
  }

var favorited = {};

function favorite()
  {
  log ("favorite: " + player_real_channel);

  var channel = pool [player_real_channel];

  var program_id = program_line [program_cursor];
  var program = programgrid [program_id];

  var program_arg;

  if ('umbrella' in program)
    {
    /* this is a sub-episode. Use the parent episode number */
    program_arg = 'program=' + program ['umbrella'];
    }
  else if (program_id.match (/\./))
    {
    var video_id = program_id.match (/\.(.*)$/)[1];
    program_arg = 'video=' + video_id + '&' + 'name=' + encodeURIComponent (program ['name']) +
       '&' + 'image=' + encodeURIComponent (program ['thumb']) + '&' + 'duration=' + program ['duration'];
    }
  else
    program_arg = 'program=' + program_id;

  var deletion = (channel ['curatorid'] == curatorid && (channel ['nature'] == '11' || channel ['nature'] == '12'));
  var delflag = deletion ? '&' + 'delete=true' : '';

  var query = '/playerAPI/favorite?user=' + user + '&' + 'channel=' + channel ['id'] + '&' + program_arg + delflag + rx();
  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    pop_with = "#popmessage-player-info";
    if (fields[0] != '0')
      {
      log ("error favoriting: " + lines[0]);
      player_pop_message ((deletion ? "error unfavoriting: " : "error favoriting: ") + lines[0]);
      }
    else
      {
      if (! (user in favorited))
        favorited [user] = {};
      if (! (channel ['id'] in favorited [user]))
        favorited [user][channel ['id']] = {};
      if (deletion)
        favorited [user][channel ['id']][program_id] = false;
      else
        favorited [user][channel ['id']][program_id] = true;
      player_pop_message (deletion ? translations ['remfav'] : translations ['addfav']);
      /* refresh the dropdown */
      favorite_dropdown();
      }
    });
  }

function search_buttons()
  {
  $("#search-input").unbind();

  $("#search-input").focus( function()
    {
    $(this).addClass ("hasFocus");
    if ($("#search-input").val() == 'Search' || $("#search-input").val() == translations ['search'])
      $("#search-input").val ('');
    });

  $("#search-input").blur( function() 
    {
    $(this).removeClass ("hasFocus");
    if ($("#search-input").val() == '')
      $("#search-input").val (translations ['search']);
    });

  $("#btn-search").unbind();
  $("#btn-search").click (function()
    {
    var keyword = $("#search-input").attr ("value");
    perform_search (keyword);
    });
  }

function GNU_player_metainfo (real_channel)
  {
  var channel = pool [real_channel];

  $("#ch-title").text (channel ['name']);

  var curator_name = channel ['curatorname'] ? channel ['curatorname'] : "[no curator name]";

  $("#curator-photo img").attr ("src", channel ['curatorthumb']);
  $("#curator-name").html (curator_name);
  $("#curator-description span").html (channel ['curatordesc']);
  $("#ch-title").html (channel ['name']);

  if (! (program_cursor in program_line))
    return;
  var program_id = program_line [program_cursor];
  if (! (program_id in programgrid))
    {
    $("#player-ep-source").hide();
    return;
    }
  var program = programgrid [program_id];

  if (program)
    {
    var sub_ep_name = program ['name'];
    var clip = get_current_clip();
    if (clip && 'name' in clip)
      sub_ep_name = clip ['name'];

    var ago = ageof (program ['timestamp'], true);
    $("#ep-title, #player-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));
    if ('umbrella-name' in program)
      $("#ep-title").html (fixed_up_program_name (program ['umbrella-name']));
    // $("#ch-source").html (translations ['clipfrom'] + ' <span>' + sub_ep_name + '</span>');
    $("#ch-source").html ('<span>' + sub_ep_name + '</span>');
    $("#player-ep-meta .ep-age").html (ago);
    $("#player-ep-meta .ep-index").html ('(' + program_cursor + '/' + channel ['count'] + ')');
    }

  var by = (language == 'en') ? translations ['curatorby'] : '';

  if (channel ['nature'] == '3' || channel ['nature'] == '4')
    $("#curator-source").html (by + ' <span>' + channel ['extra'] + '</span>');
  else
    $("#curator-source").html (by + ' <span>' + channel ['curatorname'] + '</span>');

  $("#player-ep-source").show();
  }

function player_control_bindings()
  {
  $("#btn-play, #btn-pause").unbind();
  $("#btn-play, #btn-pause").click (player_play_or_pause);

  $("#btn-favorite").unbind();
  $("#btn-favorite").mouseover (favorite_dropdown);
  $("#btn-favorite").click (favorite);

  $("#btn-share").unbind();
  $("#btn-share").mouseover (share_dropdown);

return;
  $("#btn-sort").unbind();
  $("#btn-sort").click (function (event) { sort_dropdown(); event.stopPropagation(); });

  $("#btn-sync").unbind();
  $("#btn-sync").click (function (event) { event.stopPropagation(); sync(); });

  $("#btn-rez").unbind();
  $("#btn-rez").click (function (event) { event.stopPropagation(); player_resolution(); });
  }

function goto_left_off_point()
  {
  log ('goto left off point');

  if (player_mode != 'guide')
    return;

  var channel = channelgrid [ipg_cursor];
  program_cursor = undefined;

  var leftoff, leftoff_full;

  if ('viewed' in channel)
    {
    leftoff = channel ['viewed'];
    leftoff_full = dot_qualify_program_id (channel ['id'], channel ['viewed'])
    }
  else if ((channel ['sortorder'] == '2' || channel ['sortorder'] == '3') && 'leftoff' in channel && channel ['leftoff'] != '')
    {
    leftoff = channel ['leftoff'];
    leftoff_full = dot_qualify_program_id (channel ['id'], channel ['leftoff'])
    }

  if (leftoff)
    {
    for (var i = 1; i <= n_program_line; i++)
      {
      if (program_line [i] == leftoff_full || youtube_of (program_line [i]) == leftoff)
        {
        program_cursor = i;
        log ('left off episode ' + leftoff + ', at episode position ' + program_cursor);
        break;
        }
      }
    if (!program_cursor)
      log ('left off point not found in episode lineup');
    }

  if (!program_cursor)
    program_cursor = 1;

  var thumbnails_in_row = 9;
  if (thumbing == 'store' || thumbing == 'store-wait')
    thumbnails_in_row = 5;
  program_first = 1 + Math.floor ((program_cursor - 1) / thumbnails_in_row) * thumbnails_in_row;

  redraw_program_line();
  log ('program cursor set to: ' + program_cursor);

  $("#player-ep-list .clickable").removeClass ("on");
  $("#p-li-" + program_cursor).addClass ("on");
  }

function use_programinfo (real_channel)
  {
  var nature = pool [real_channel]['nature'];
  return (nature != '3' && nature != '4' && nature != '5');
  }

function player_metainfo()
  {
  GNU_player_metainfo (player_real_channel);

  $("#player-ep-meta .amount").html ('(' + program_cursor + '/' + n_program_line + ')');
  $("#player-ep-meta .ep-title").html ("");

  if (player_real_channel)
    set_hash ("#!" + player_real_channel);
  else
    set_hash ("");

  var next = next_channel_square_setwise (ipg_cursor);
  var prev = previous_channel_square_setwise (ipg_cursor);

  if (next && (next in channelgrid))
    {
    var channel = channelgrid [next];
    $("#next-title span").html (channel ['name']);
    $("#play-next .thumbnail").attr ("src", channel ['thumb']);
    }

  if (prev && (prev in channelgrid))
    {
    var channel = channelgrid [prev];
    $("#prev-title span").html (channel ['name']);
    $("#play-prev .thumbnail").attr ("src", channel ['thumb']);
    }

  ellipses();

  if (! (program_cursor in program_line))
    return;

  var program_id = program_line [program_cursor];

  if (! (program_id in programgrid))
    return;

  var program = programgrid [program_id];

  $("#player-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));

  if ('desc' in program && program ['desc'] != '')
    {
    $("#ep-description .content span").html (program ['desc']);
    $("#ep-description").show();
    }
  else
    $("#ep-description").hide();

  if ('fbmessage' in program && 'fbuser' in program && program ['fbmessage'] != '')
    {
    $("#comment .head span").html (program ['fbuser'] + ':');
    $("#comment .content span").html (program ['fbmessage']);
    $("#comment").show();
    }
  else if ('curcom' in program && program ['curcom'] != '')
    {
    $("#comment .head span").html (translations ['curcom']);
    $("#comment .content span").html (program ['curcom']);
    $("#comment").show();
    }
  else
    $("#comment").hide();

  // $("#ep-uploaded .content span").html (ageof (program ['timestamp'], true));
  $("#player-ep-meta .age").html ('- ' + ageof (program ['timestamp'], true));

  ellipses();
  }

function player_ep_hover_in()
  {
  var id = $(this).attr ("id");
  $("#player-ep-list .clickable").removeClass ("hover");
  $(this).addClass ("hover");
  id = id.replace (/^p-li-/, '');
  var program = programgrid [program_line [id]];
  var ago = ageof (program ['timestamp'], true);
  $("#player-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));
  $("#player-ep-meta .ep-age").html (ago);
  $("#player-ep-meta .ep-index").html ('(' + id + '/' + n_program_line + ')');
  }

function player_ep_hover_out()
  {
  var id = $(this).attr ("id");
  $(this).removeClass ("hover");
  if (program_cursor) 
    {
    $("#p-li-" + program_cursor).addClass ("on");
    var program = programgrid [program_line [program_cursor]];
    var ago = ageof (program ['timestamp'], true);
    $("#player-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));
    $("#player-ep-meta .ep-age").html (ago);
    $("#player-ep-meta .ep-index").html ('(' + program_cursor + '/' + n_program_line + ')');
    }
  }

function player_arrow_click()
  {
  var id = $(this).attr ("id");

  var old_program_first = program_first;

  if (id == 'player-arrow-left')
    {
    log ('PLAYER-ARROW-LEFT');
    program_first -= 9;
    if (program_first < 1)
      program_first = 1;
    }
  else if (id == 'player-arrow-right')
    {
    log ('PLAYER-ARROW-RIGHT');
    program_first += 9;
    if (program_first > n_program_line)
      program_first = n_program_line;
    }

  if (program_first == old_program_first)
    return;

  log ('program first now: ' + program_first);

  program_cursor = program_first;
  current_segment = 0;

  redraw_program_line();

  // $("#ep-list, #player-ep-list").html (ep_html());
  // $("#ep-list img, #player-ep-list img").error(function () { $(this).unbind("error").attr("src", no_thumbnail_image); });
  // episode_clicks_and_hovers();

  log ('arrow click: playing episode #' + program_cursor);

  store_play_yt();
  }

function share_url()
  {
  var epshare = '';
  var channel = pool [player_real_channel];

  if (program_cursor in program_line)
    {
    var program_id = program_line [program_cursor];
    if (program_id.match (/\./))
      program_id = program_id.match (/\.(.*)$/) [1];
    epshare = '&' + 'episode=' + program_id;
    }

  return location.protocol + '//' + location.host + '/view?channel=' + channel ['id'] + epshare;
  }

function share_dropdown()
  {
  log ('share dropdown');

  $("#share-url").val (share_url());

  $("#btn-share .btn-share-fb").unbind();
  $("#btn-share .btn-share-fb").click (switch_to_facebook);

  if (username == 'Guest')
    $("#btn-share .btn-share-mail").hide();
  else
    {
    $("#btn-share .btn-share-mail").show();
    $("#btn-share .btn-share-mail").unbind();
    $("#btn-share .btn-share-mail").click (email_layer);
    }
  }

function favorite_dropdown()
  {
  $("#btn-favorite .favor, #btn-favorite .unfavor, #btn-favorite .favorite-share, #btn-favorite .btn-share-fb").hide();

  if (current_program_is_favorited())
    {
    $("#btn-favorite .unfavor, #btn-favorite .favorite-share, #btn-favorite .btn-share-fb").show();
    $("#btn-favorite .btn-share-fb").unbind();
    $("#btn-favorite .btn-share-fb").click (switch_to_facebook);
    }
  else
    $("#btn-favorite .favor").show();
  }

function current_program_is_favorited()
  {
  var program_id = program_line [program_cursor];
  return ( user in favorited && 
           player_real_channel in favorited [user] && 
           favorited [user][player_real_channel][program_id] == true );
  }

function email_layer()
  {
  log ('email layer');

  saved_thumbing = thumbing;
  thumbing = 'email';

  try { ytmini [mini_player].pauseVideo(); } catch (error) {};

  $("#email-share-layer").show();

  input_init ('Enter email address', '#email-share-to');
  input_init ('Your message here', '#email-share-message');
  input_init ('Enter verification word', '#email-share-captcha');

  $("#email-share-close, #btn-email-share-cancel").unbind();
  $("#email-share-close, #btn-email-share-cancel").click (function() { $("#email-share-layer").hide(); thumbing = 'player'; });

  email_recaptcha();
  }

function email_recaptcha()
  {
  $("#btn-email-share-send").hide();
  $("#btn-email-send").addClass ("disable");
  var query = '/playerAPI/requestCaptcha?user=' + user + '&' + 'action=2' + rx();
  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      var fields = lines[2].split ('\t');
      last_captcha = fields[0];
      var image = 'http://9x9ui.s3.amazonaws.com/captchas/' + last_captcha;
      $("#email-share-layer .email-share-captch-graph").attr ("src", image);
      $("#btn-email-share-send").show();
      $("#btn-email-share-send").unbind();
      $("#btn-email-share-send").click (share_via_mail);
      $("#email-share-captch").unbind();
      $("#email-share-captch").click (email_recaptcha);

      // email_focus_blur ("#email-input", translations ['enteremail']);
      // email_focus_blur ("#msg-input", translations ['entermessage']);
      // email_focus_blur ("#email-captcha-input", translations ['s_captcha2']);
      }
    });
  }

email_hints = {};
function email_focus_blur (id, hint)
  {
  email_hints [id] = hint;
  $(id + " .textfield").val (hint);

  $(id).parent().unbind();
  $(id).parent().hover (function()
    {
    $(this).find (".textfield").focus();
    });

  $(id + " .textfield").unbind();
  $(id + " .textfield").focus (function()
    {
    $(id).parent().parent().find("p").removeClass ("on focus")
    $(id).addClass ("on");
    if ($(id + " .textfield").val() == hint)
      $(id + " .textfield").val ('');
    /* blur isn't called if we manually focused! fill in other hints */
    for (var f in email_hints)
      if (f != id && $(f + " .textfield").val() == '')
        $(f + " .textfield").val (email_hints [f]);
    });
  $(id + " .textfield").blur (function()
    {
    log ('blur ' + id);
    $(id).parent().parent().find("p").removeClass ("on focus")
    if ($(id + " .textfield").val() == '')
      $(id + " .textfield").val (hint);
    else
      log (id + " value is: " + $(id + " .textfield").val());
    });
  }

function share_via_mail()
  {
  log ('email send');

  // http://puppy.9x9.tv/playerAPI/shareByEmail?user=xx&toEmail=xx&toName=xx&subject=xx&content=xx

  var to = $("#email-share-to").val();
  var content = $("#email-share-message").val();
  var captcha = $("#email-share-captcha").val();
  var subject = 'Sharing with you a 9x9 Channel';

  if (! (to.match (/\@/)))
    {
    notice_ok (thumbing, 'Please enter an email address', "");
    return;
    }

  if (content == '' || content == 'Enter message')
    {
    notice_ok (thumbing, 'Please enter a message', "");
    return;
    }

  //content += '\n' + url;
  if (captcha == '' || captcha == 'Type the characters in the picture below')
    {
    notice_ok (thumbing, 'Please enter the letters in the challenge', "");
    return;
    }

  to      = encodeURIComponent (to);
  content = encodeURIComponent (content + '\n\n' + share_url() + '\n\nSent from my 9x9 channel browser\n');
  captcha = encodeURIComponent (captcha);
  subject = encodeURIComponent (subject);

  var query = '/playerAPI/shareByEmail?user=' + user + '&' + 'toEmail=' + to + '&' + 'subject=' + subject + '&' + 'content=' + content + '&' + 'captcha=' + last_captcha + '&' + 'text=' + captcha + rx();

  var d = $.get (query, function (data)
    {
    log ('shareByEmail result: ' + data);
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      $("#email-share-layer").show();
      thumbing = 'player';
      notice_ok (thumbing, "Sent!", "");
      }
    else if (fields[1].match (/captcha/i))
      notice_ok (thumbing, translations ['badcaptcha'], "");
    else
      notice_ok (thumbing, 'Error sharing: ' + fields[1], "");
    });
  }

function after_share_triple_button()
  {
  log ('after share triple button');
  $("#addsucc").html ("Your email has been sent successfully!");
  $("#btn-watchSet span").html (translations ['b_emailmore']);
  $("#btn-toFset span").html (translations ['done']);
  $("#btn-toSG span").html (translations ['b_guide']);
  $("#tribtn-layer").show();
  $("#btn-watchSet").unbind();
  $("#btn-watchSet").click (function() { $("#tribtn-layer").hide(); log ('triple: email more'); email_layer(); });
  $("#btn-toFset").unbind();
  $("#btn-toFset").click (function() { $("#tribtn-layer").hide(); log ('triple: done'); });
  $("#btn-toSG").unbind();
  $("#btn-toSG").click (function() { $("#tribtn-layer").hide(); log ('triple: guide'); guide(); });
  }

function sort_dropdown()
  {
  var channel = channelgrid [ipg_cursor];
  var nature = channel ['nature'];

  if ($("#sort-dropdown").css ("display") == 'block')
    {
    close_all_dropdowns();
    return;
    }

  $("#sort-dropdown li").removeClass ("on");
  close_all_dropdowns();

  if (channel ['sortorder'] == '3' || nature == '8' || nature == '9')
    {
    notice_ok (thumbing, 'Sorting disabled for this special channel type', "");
    return;
    }
  else if (channel ['sortorder'] == '0' || channel ['sortorder'] == '1' || channel ['sortorder'] == '' || ! ('sortorder' in channel))
    $("#sort-dropdown li:nth-child(2)").addClass ("on");
  else if (channel ['sortorder'] == '2')
    $("#sort-dropdown li:nth-child(3)").addClass ("on");

  log ('sort dropdown');

  $("#sort-dropdown").show();

  $("#sort-dropdown li:nth-child(2)").unbind();
  $("#sort-dropdown li:nth-child(2)").click (function (event)
    {
    event.stopPropagation();
    if (channel ['sortorder'] != '1')
      {
      channel ['sortorder'] = '1';
      save_sort_order();
      enter_channel ('player');
      store_play_yt();
      }
    close_all_dropdowns();
    });

  $("#sort-dropdown li:nth-child(3)").unbind();
  $("#sort-dropdown li:nth-child(3)").click (function (event)
    {
    event.stopPropagation();
    if (channel ['sortorder'] != '2')
      {
      channel ['sortorder'] = '2';
      save_sort_order();
      enter_channel ('player');
      store_play_yt();
      }
    close_all_dropdowns();
    });

  $("body").unbind();
  $("body").click (close_all_dropdowns);
  }

function save_sort_order()
  {
  var channel = channelgrid [ipg_cursor];
  var d = $.get ('/playerAPI/saveSorting?user=' + user + '&' + 'channel=' + channel ['id'] + '&' + 'sorting=' + channel ['sortorder'] + rx(), function (data)
    {
    log ('save sort order result: ' + data);
    relay_post ("UPDATE");
    });
  }

function general_pause()
  {
  if (current_tube == 'au')
    audio_pause();
  else
    try { ytmini [mini_player].pauseVideo(); } catch (error) {};
  }

function general_resume()
  {
  if (current_tube == 'au')
    audio_resume();
  else
    try { ytmini [mini_player].playVideo(); } catch (error) {};
  }

function player_play_or_pause()
  {
  if ($("#btn-play").css ("display") != 'none')
    {
    log ('play/pause: playing');
    $("#btn-play").hide();
    $("#btn-pause").show();
    relay_post ("RESUME");
    general_resume();
    }
  else if ($("#btn-pause").css ("display") != 'none')
    {
    log ('play/pause: pausing');
    $("#btn-pause").hide();
    $("#btn-play").show();
    relay_post ("PAUSE");
    general_pause();
    }

  $("#btn-play, #btn-pause").unbind();
  $("#btn-play, #btn-pause").click (player_play_or_pause);
  }

function player_full (ev)
  {
  ev.stopPropagation();
  log ('player fullscreen');
  close_all_dropdowns();
  $("#player-layer").addClass ("full");
  $("#control-bar").hide();
  player_mousemove_timex = setTimeout ("player_delayed_mousemove()", 2000);
  $("#control-bar").css ("background-color", "black");
  $("#player-video").css ({ height: $(window).height(), top: "0" });
  }

var control_bar_timex;
var player_mousemove_timex;

function player_delayed_minimizer()
  {
  $("body, #player-layer, #player_video, #ym0, #ym1, #ym2, #ym3, #ym4, #yams").unbind ('mousemove');
  $("body, #player-layer, #player_video, #ym0, #ym1, #ym2, #ym3, #ym4, #yams").mousemove (player_minimize);
  }

function player_delayed_mousemove()
  {
  $("body, #player-layer, #player_video, #ym0, #ym1, #ym2, #ym3, #ym4, #yams").unbind ('mousemove');
  $("body, #player-layer, #player_video, #ym0, #ym1, #ym2, #ym3, #ym4, #yams").mousemove (player_control_on);
  }

function OLD_player_minimize()
  {
  clearTimeout (control_bar_timex);
  clearTimeout (player_mousemove_timex);
  log ('player minimize');
  $("body, #player-layer, #player_video, #ym0, #ym1, #ym2, #ym3, #ym4, #yams").unbind();
  $("#player-layer").removeClass ("full");
  $("#control-bar").show();
  $("#player-video").css ({ height: "19.6875em", top: "50%" });
  player_control_bindings();
  }

var player_size = 'minimize';

function player_minimize()
  {
  player_size = 'minimize';
  var winHeight = $(window).height();
  var videoHeight = winHeight - 241;
  var videoWidth = $("#player-holder").width() - 147;
  var vloc = $("#video-placeholder").offset();
  var vl = vloc.left;
  var vt = vloc.top;
  $("#video-layer").css ({ "width": videoWidth, "height": videoHeight, "left": vl, "top": vt, "z-index": 49 });
  $("#video-constrain").css ({ "height": videoHeight - 43, "top": 0 });
  $("#player-ch-info, #player-ep-bar, #player-ep-source").css ({ "width": videoWidth });
  $("#btn-shrink").hide();
  $("#btn-expand").show();
  $("#btn-expand").unbind();
  $("#btn-expand").click (function()
    {
    player_fullscreen();
    $(window).trigger ('resize');
    });
  }

function player_fullscreen()
  {
  player_size = 'fullscreen';
  var playerHeight = $(window).height() - 43 - 43;
  $("#video-layer").css ({ width: "100%", height: "100%", left: 0, top: 0, 'z-index':100 });
  $("#video-constrain").css ({ height: playerHeight, top: 43 });
  $("#btn-expand").hide();
  $("#btn-shrink").show();
  $("#btn-shrink").unbind();
  $("#btn-shrink").click (function()
    {
    player_minimize();
    $(window).trigger ('resize');
    });
  }

function player_control_on()
  {
  clearTimeout (control_bar_timex);
  log ('control bar on');
  $("#control-bar").show();
  player_control_bindings();
  $("#player-video").height ($(window).height() - $("#control-bar").height());
  control_bar_timex = setTimeout ("player_control_off()", 5000);
  }

function player_control_off()
  {
  clearTimeout (control_bar_timex);
  log ('control bar off');
  $("#control-bar").hide();
  $("#player-video").height ($(window).height());
  }

function player_ep_click (id)
  {
  id = id.replace (/^p-li-/, '');
  log ('player episode click: ' + id);
  $("#p-li-" + program_cursor).removeClass ("on");
  program_cursor = id;
  $("#p-li-" + program_cursor).addClass ("on");
  player_metainfo();
  current_segment = 0;
  store_play_yt();
  }

function player_next()
  {
  log ('player next');

  var real_channel;
  $("#pl-list li").removeClass ("on");
  $("#pl-list li .pl-highlight span").removeClass ("on");

  if (player_mode == 'guide')
    {
    /* ipg_cursor = current_channel = next_channel_square_setwise (ipg_cursor); */
    ipg_cursor = current_channel = next_channel_square (ipg_cursor);
    scroll_player_column_to (ipg_cursor);
    $("#channel-" + ipg_cursor).addClass ("on");
    $("#channel-" + ipg_cursor + " .pl-highlight span").addClass ("on");
    real_channel = channelgrid [ipg_cursor]['id'];
    }
  else
    {
    if (player_cursor + 1 < player_stack.length) 
      player_cursor++;
    else
      player_cursor = 1;
    scroll_player_column_to (player_cursor);
    $("#channel-" + player_cursor).addClass ("on");
    $("#channel-" + player_cursor + " .pl-highlight span").addClass ("on");
    real_channel = player_stack [player_cursor]['id'];
    }

  var cb = "player_flip_inner(" + (player_mode == 'guide' ? ipg_cursor : player_cursor) + ")";
  basic_load_programs_then (real_channel, cb);
  }

function player_prev()
  {
  log ('player prev');

  var real_channel;
  $("#pl-list li").removeClass ("on");
  $("#pl-list li .pl-highlight span").removeClass ("on");

  if (player_mode == 'guide')
    {
    ipg_cursor = current_channel = previous_channel_square_setwise (ipg_cursor);
    scroll_player_column_to (ipg_cursor);
    $("#channel-" + ipg_cursor).addClass ("on");
    $("#channel-" + ipg_cursor + " .pl-highlight span").addClass ("on");
    real_channel = channelgrid [ipg_cursor]['id'];
    }
  else
    {
    if (player_cursor - 1 > 0)
      player_cursor--;
    else
      player_cursor = player_stack.length - 1;
    scroll_player_column_to (player_cursor);
    $("#channel-" + player_cursor).addClass ("on");
    $("#channel-" + player_cursor + " .pl-highlight span").addClass ("on");
    real_channel = player_stack [player_cursor]['id'];
    }

  var cb = "player_flip_inner(" + (player_mode == 'guide' ? ipg_cursor : player_cursor) + ")";
  basic_load_programs_then (real_channel, cb);
  }

function player_start_inner (cursor)
  {
  log ('player start inner, cursor: ' + cursor);

  var real_channel;
  if (player_mode == 'guide')
    real_channel = channelgrid [ipg_cursor]['id'];
  else
    real_channel = player_stack [player_cursor]['id'];

  store_channel = real_channel;

  if (programs_in_real_channel (real_channel) < 1)
    notice_ok (thumbing, 'No programs in this channel!', "");
  else
    player_flip_inner (cursor);

  header();

  if (ambient_volume == undefined)
    {
    player_mute = false;
    ambient_volume = 0.4;
    set_ambient_volume();
    }
  }

var recently_watched_channel_cursor;
var recently_watched_program_cursor;

function save_as_recently_watched()
  {
  recently_watched_channel_cursor = ipg_cursor;
  recently_watched_program_cursor = program_cursor;
  }

function player_flip_inner (cursor)
  {
  current_segment = 0;

  if (player_mode == 'guide')
    {
    ipg_cursor = current_channel = cursor;
    player_real_channel = add_to_history (channelgrid [ipg_cursor]['id']);
    enter_channel ('player');
    }
  else
    {
    player_real_channel = add_to_history (player_stack [player_cursor]['id']);
    prepare_real_channel (player_real_channel);
    redraw_program_line();
    }

  player_column_highlight();

  redraw_subscribe();
  goto_left_off_point();
  save_as_recently_watched();
  ipg_metainfo();
  player_metainfo();
  redraw_program_line();
  store_play_yt();
  player_show_yt_quality();
  store_yt_render_volume();
  }

function flip_next_episode()
  {
  log ('flip next episode');

  if (any_lingering_segments())
    return;

  if (program_cursor < n_program_line)
    {
    $("#p-li-" + program_cursor).removeClass ("on");
    $("#sp-" + program_cursor).removeClass ("on");
    program_cursor++;
    current_segment = 0;
    redraw_program_line();
    player_metainfo();
    if (thumbing == 'player' || thumbing == 'player-wait')
      save_as_recently_watched();
    $("#p-li-" + program_cursor).addClass ("on");
    $("#sp-" + program_cursor).addClass ("on");
    store_play_yt();
    }
  else
    {
    if (thumbing == 'player' || thumbing == 'player-wait')
      notice_ok (thumbing, translations ['nmo'], "guide()");
    else
      {
      log ('flip next episode: at final episode, flipping to next channel');
      flip_next();
      }
    // $("#msg-text").html (translations ['eoe']);
    // $("#msg-layer").show();
    // elastic();
    // setTimeout ("guide()", 3000);
    }
  }

function flip_prev_episode()
  {
  if (current_segment > 1)
    {
    current_segment -= 2;
    store_play_yt();
    return;
    }

  if (program_cursor > 1)
    {
    log ('flip prev episode');
    $("#p-li-" + program_cursor).removeClass ("on");
    program_cursor--;
    redraw_program_line();
    player_metainfo();
    save_as_recently_watched();
    $("#p-li-" + program_cursor).addClass ("on");
    store_play_yt();
    }
  else
    log ('flip prev episode: at first episode');
  }

function player_resolution()
  {
  if ($("#rez-dropdown").css ("display") == 'block')
    {
    close_all_dropdowns();
    return;
    }

  var html = '';
  if (ytmini [mini_player].getPlayerState() > 0)
    {
    var q = ytmini [mini_player].getPlaybackQuality();
    var avail = ytmini [mini_player].getAvailableQualityLevels();
    for (var i = 0; i < avail.length; i++)
      {
      if (avail [i] == q)
        html += '<li id="q-' + avail[i] + '" class="on">';
      else
        html += '<li id="q-' + avail[i] + '">';
      html += '<img src="' + nroot + 'icon_check.png" class="icon-check"><span>' + rez9x9 [avail[i]] + '</span></li>';
      } 
    close_all_dropdowns();
    $("#rez-dropdown").html (html);
    $("#rez-dropdown").show();
    $("#rez-dropdown li").unbind();
    $("#rez-dropdown li").click (function (event) { event.stopPropagation(); rez_click ($(this).attr ("id")); });
    $("body").unbind();
    $("body").click (close_all_dropdowns);
    }
  }

function rez_click (id)
  {
  id = id.replace (/^q-/, '');
  log ('rez click: ' + id);
  ytmini [mini_player].setPlaybackQuality (id);
  close_all_dropdowns();
  /* assume this worked immediately */
  player_show_yt_quality();
  /* if not, adjust accordingly */
  setTimeout ("player_show_yt_quality()", 1000);
  }

function player_show_yt_quality()
  {
  var q = '';
  try { var q = ytmini [mini_player].getPlaybackQuality(); } catch (error) {};
  $("#selected-rez").html (rez9x9 [q]);
  }

function sync()
  {
  var dd;

  if (thumbing == 'player' || thumbing == 'player-wait')
    dd = '#sync-dropdown';
  else if (thumbing == 'guide' || thumbing == 'guide-wait')
    dd = '#guide-sync-dropdown';
  else
    return;


  if ($(dd).css ("display") == 'block')
    {
    close_all_dropdowns();
    return;
    }

  close_all_dropdowns();

  $(dd).show();

  update_who();

  $("body").unbind();
  $("body").click (close_all_dropdowns);

  relay_post ("WHO");
  }

function close_all_dropdowns()
  {
  $("#sort-dropdown, #share-dropdown, #rez-dropdown, #sync-dropdown, #guide-sync-dropdown, #sitelocation-dropdown, #account-dropdown, #sitelang-dropdown, #language-dropdown, #region-dropdown, #profile-dropddown, #developer-dropdown").hide();
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

function update_who()
  {
  var html = '';
  for (var i in who)
    {
    if (who.hasOwnProperty (i))
      {
      var w = who [i];
      var id = w.match (/^\+?(\d+):/)[1];
      if (w.match (/^\+/))
        {
        w = w.replace (/^\+/, '');
        html += '<li id="sync-' + id + '" class="on">';
        }
      else
        html += '<li id="sync-' + id + '">';
      html += '<img src="' + nroot + 'icon_check.png" class="icon-check"><span>' + prettify (w) + '</span></li>';
      }
    }

  if (username == 'Guest')
    html = '<li id="plz-login">Please login</li>';
  else if (!relay_started)
    html = '<li>Relay not started</li>';
  else if (html == '')
    html = '<li>None</li>';

  $("#sync-dropdown, #guide-sync-dropdown").html (html);

  $("#sync-dropdown li, #guide-sync-dropdown li").unbind();
  $("#sync-dropdown li, #guide-sync-dropdown li").click (function (event) { event.stopPropagation(); sync_click ($(this).attr ("id")); });

  if (username == 'Guest')
    {
    $("#sync-dropdown li, #guide-sync-dropdown li").unbind();
    $("#sync-dropdown li, #guide-sync-dropdown li").click (signin_screen);
    }
  }

function sync_click (id)
  {
  id = id.replace (/^sync-/, '');
  for (var i in who)
    {
    var rx = new RegExp ('^\\+?' + id + ':');
    if (who.hasOwnProperty (i) && who[i].match (rx))
      {
      log ('player sync with: ' + who[i]);
      if (who[i].match (/^\+/))
        {
        relay_post ("RELEASE " + who[i]);
        controlling = undefined;
        }
      else
        {
        relay_post ("CONTROL " + who[i] + ' ' + user + ' ' + encodeURIComponent (username));
        controlling = who[i];
        }
      $("#sync-dropdown, #guide-sync-dropdown").hide();
      return;
      }
    }
  }

function release_all()
  {
  log ('release all');
  for (var i in who)
    if (who.hasOwnProperty (i) && who[i].match (/^\+/))
      {
      relay_post ("RELEASE " + who[i]);
      controlling = undefined;
      }
  }

function next_channel_to_preload()
  {
  if (thumbing == 'player' || thumbing == 'player-wait')
    {
    var grid = next_channel_square_setwise (ipg_cursor);
    if (grid)
      return channelgrid [grid]['id'];
    }
  else if (thumbing == 'store' || thumbing == 'store-wait')
    {
    if (store_direction == '!')
      {
      store_next_random_channel = select_a_random_channel_index();
      return store_channel_id_by_index (store_cat, store_next_random_channel);
      }
    else
      {
      var n_channels = channels_in_set_or_category (store_preview_type, store_cat)
      if (store_index < n_channels)
        return store_channel_id_by_index (store_cat, parseInt (store_index) + 1)
      else
        return store_channel_id_by_index (store_cat, 1);
      }
    }
  }

var ran_cache_id;
var ran_cache = [];

function select_a_random_channel_index()
  {
  var n_channels = channels_in_set_or_category (store_preview_type, store_cat);
  if (store_preview_type == 'share' || n_channels == 1)
    return 1;

  /* NOTE -- remove this block to restore randomness! */
  if (true)
    {
    if (parseInt (store_index) < n_channels)
      return parseInt (store_index) + 1;
    else
      return 1;
    }

  /* refill the cache if necessary */
  if (store_cat != ran_cache_id || ran_cache.length <= 1)
    {
    ran_cache = [];
    for (var i = 1; i <= n_channels; i++)
      ran_cache.push (i);

    /* remove the current channel */
    if (ran_cache.length > 1 && store_index < ran_cache.length)
      ran_cache.splice (store_index-1, 1);
    /* weird situation when first used, this prevents the first channel from appearing too often */
    if (store_cat != ran_cache_id && store_index != 1)
      ran_cache.splice (0, 1);
    ran_cache_id = store_cat;
    }

  log ('random cache: ' + ran_cache);

  var random_channel_pos = Math.floor (Math.random() * ran_cache.length);
  var random_channel = ran_cache [random_channel_pos];
  ran_cache.splice (random_channel_pos, 1);
  return random_channel;
 
  var tries = 0;
  var random_channel = store_index;
  while (random_channel == store_index && ++tries <= 12)
    random_channel = 1 + Math.floor (Math.random() * n_channels);
  return random_channel;
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
  log ('error noise');
  if (error_noise)
    try { error_noise.play(); } catch (error) {};
  }

function require_activation (callback)
  {
  if (!the_very_first_time)
    callback();
  else
    play_error_noise();
  }

function header()
  {
  $("#header, #footer").show();

  $("#guide").unbind();
  $("#guide").click (function() { require_activation (guide); });

  $("#player").unbind();
  $("#player").click (function() { require_activation (player_button); });

  $("#home").unbind();
  $("#home").click (function() { require_activation (home); });

  $("#browse").unbind();
  $("#browse").click (function() { require_activation (browse); });

  $("#btn-developer").unbind();
  $("#btn-developer").click (function (event) { event.stopPropagation(); developer_dropdown(); });

  $("#btn-sitelang").unbind();
  $("#btn-sitelang").click (function (event) { event.stopPropagation(); sitelang_dropdown(); });

  $("#btn-sitelocation").unbind();
  $("#btn-sitelocation").click (function (event) { event.stopPropagation(); sitelocation_dropdown(); });

  $("#footer-control").unbind();
  $("#footer-control").click (function()
    {
    if ($(this).hasClass("on"))
      {
      $(this).removeClass("on");
      $("#footer").show().animate ({ bottom: "0" }, 500);
      }
    else
      {
      $(this).addClass("on");
      $("#footer").animate ({ bottom: "-48" }, 500, function() { $(this).hide(); });
      }
    });

  search_buttons();
  footer_locale();
  set_username_clicks();
  }

function curate()
  {
  if (username == 'Guest')
    notice_ok (thumbing, translations ['mustlogin'], "");
  else
    window.open ("cms/admin", "_blank");
  }

function developer_dropdown()
  {
  if ($("#developer-dropdown").css ('display') == 'block')
    {
    close_all_dropdowns();
    return;
    }

  close_all_dropdowns();

  $("#developer-dropdown").show();

  $("#developer-dropdown li").unbind();
  $("#developer-dropdown li").click (function (event)
    {
    $("#developer-dropdown").hide();
    developer ($(this).attr ("data-doc"));
    });
  }

function sitelang_dropdown()
  {
  if ($("#sitelang-dropdown").css ('display') == 'block')
    {
    close_all_dropdowns();
    return;
    }

  close_all_dropdowns();

  $("#sitelang-dropdown li").removeClass ("on");
  if (language == 'zh')
    $("#sitelang-dropdown li:nth-child(2)").addClass ("on");
  else
    $("#sitelang-dropdown li:nth-child(1)").addClass ("on");
  $("#sitelang-dropdown").show();

  $("#sitelang-dropdown li").unbind();
  $("#sitelang-dropdown li:nth-child(1)").click (function (event)
    {
    event.stopPropagation();
    if (language != 'en')
      {
      set_language ('en');
      footer_locale();
      save_language_setting();
      refresh_after_language_change();
      }
    close_all_dropdowns();
    });
  $("#sitelang-dropdown li:nth-child(2)").click (function (event)
    {
    event.stopPropagation();
    if (language != 'zh')
      {
      set_language ('zh');
      footer_locale();
      save_language_setting();
      refresh_after_language_change();
      }
    close_all_dropdowns();
    });

  $("body").unbind();
  $("body").click (close_all_dropdowns);
  }

function save_language_setting()
  {
  if (username != 'Guest')
    var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' + 'key=ui-lang' + '&' + 'value=' + language + rx(), function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields [0] != '0')
        notice_ok (thumbing, 'Error saving language: ' + fields[1], "");
      });
  }

function refresh_after_language_change()
  {
  redraw_layer_if_possible();
  }

function sitelocation_dropdown()
  {
  if ($("#sitelocation-dropdown").css ("display") == 'block')
    {
    close_all_dropdowns();
    return;
    }

  close_all_dropdowns();
  var previous_sphere = sphere;

  footer_locale();

  $("#sitelocation-dropdown li").removeClass ("on");
  if (sphere == 'zh')
    $("#sitelocation-dropdown li:nth-child(2)").addClass ("on");
  else
    $("#sitelocation-dropdown li:nth-child(1)").addClass ("on");
  $("#sitelocation-dropdown").show();

  function site_location_change (event, new_sphere)
    {
    event.stopPropagation();
    var previous_sphere = sphere;
    log ('change location: ' + previous_sphere + ' -> ' + new_sphere);
    sphere = new_sphere;
    $("#selected-sitelocation").html ($("#sitelocation-dropdown li").eq((sphere == 'zh') ? 1 : 0).text());
    save_sphere_setting();
    close_all_dropdowns();
    load_new_sphere_data();
    }

  $("#sitelocation-dropdown li").unbind();
  $("#sitelocation-dropdown li:nth-child(1)").click (function (event)
    {
    site_location_change (event, 'en');
    });
  $("#sitelocation-dropdown li:nth-child(2)").click (function (event)
    {
    site_location_change (event, 'zh');
    });

  $("body").unbind();
  $("body").click (close_all_dropdowns);
  }

function save_sphere_setting()
  {
  if (username != 'Guest')
    var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' + 'key=sphere' + '&' + 'value=' + sphere + rx(), function (data)
      {
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields [0] != '0' && fields [0] != '903')
        notice_ok (thumbing, translations ['conferr'] + ': ' + fields[1], "");
      else
        /* load_new_sphere_data(); */ ;
      });
  }

function freshen_content()
  {
  log ('language changed: now freshen content');
  var most_recent_channel = player_history_ids [player_history_ids.length - 1];
  var mrc = most_recent_channel ? ('&' + 'channel=' + most_recent_channel) : "";
  var query = '/playerAPI/channelStack?stack=trending,recommend,hot,featured' + '&' + 'lang=' + sphere + '&' + 'user=' + user + mrc + rx();
  log ("freshen content: " + query);
  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      var blocks = data.split (/\n--/);
      process_channel_stack ('trending', '0\tGREAT-SUCCESS\n' + blocks[1]);
      process_channel_stack ('recommended', '0\tGREAT-SUCCESS\n' + blocks[2]);
      process_channel_stack ('hottest', '0\tGREAT-SUCCESS\n' + blocks[3]);
      process_channel_stack ('featured', '0\tGREAT-SUCCESS\n' + blocks[4]);
      if (thumbing == 'home')
        home();
      else if (thumbing == 'browse')
        browse_category (current_browse_index);
      else if (thumbing == 'player')
        {
        /* these two modes need to be reloaded */
        if (player_mode == 'trending')
          player_trending();
        else if (player_mode == 'recommended')
          player_recommended();
        }
      }
    });
  }

function developer (doc)
  {
  log ("developer layer");
  $(".stage").hide();
  $("#nav li").removeClass ("on");
  $("#developer-layer").show();
  set_hash ("#!aboot=" + doc.replace (/^v-/, ''));

  var query = '/playerAPI/staticContent?key=v-top&lang=' + language + rx();
  var d = $.get (query, function (data)
    {
    var blocks = data.split ('--\n');
    $("#developer-menu").html (blocks [1]);
    $("#developer-menu li").unbind();
    $("#developer-menu li").click (function() { developer_panel ($(this).attr ("data-doc")); });
    developer_panel (doc ? doc : $("#developer-menu li").eq(0).attr ("data-doc"));
    });
  }

var dblocks;
var xjs;
function developer_panel (doc)
  {
  thumbing = 'about';
  log ('developer panel: ' + doc);
  $("#developer-content").html ("");
  $("#developer-title h1").html ("");
  $("#developer-download").hide();
  var query = '/playerAPI/staticContent?key=' + doc + '&' + 'lang=' + language + rx();
  var d = $.get (query, function (data)
    {
    var blocks = data.split ('--\n');
dblocks = blocks;
    var stuff = blocks[1];
    stuff = stuff.replace (/"images\//gi, '"' + nroot);
    stuff = stuff.replace (/"thumbnail\//gi, '"' + nroot + '../thumbnail/');
    if (stuff.match (/<!--INIT([\s\S]*?)-->/))
      xjs = stuff.match(/<!--INIT([\s\S]*?)-->/)[1]
    $("#developer-content").html (stuff);
    $("#developer-constrain").css ({ height: $(window).height() - $("#developer-constrain").offset().top - $("#developer-layer .reco-shelf").height() });
    try { $("#developer-slider .slider-vertical").slider ("destroy"); } catch (error) {};
    $("#developer-slider").css ({ height: $(window).height() - $("#developer-constrain").offset().top - $("#developer-layer .reco-shelf").height() - 10 });
    scrollbar ("#developer-constrain", "#developer-list", "#developer-slider");
    setTimeout ('$("#developer-slider .slider-vertical").slider ("value", "100")', 5);
    $("#developer-menu li h1").removeClass ("on");
    $("#developer-menu li").each (function()
      {
      if ($(this).attr ("data-doc") == doc)
        $(this).children ("h1").addClass ("on");
      });
    set_hash ("#!aboot=" + doc.replace (/^v-/, ''));
    eval (xjs);
    header();
    });
  }

function account_dropdown()
  {
  $("#hint-layer, #hint-bubble").hide();

  if ($("#account-dropdown").css ("display") == 'block')
    {
    close_all_dropdowns();
    return;
    }

  close_all_dropdowns();

  var li1 = '<li id="btn-sign"><span>' + translations ['signin'] + '</span></li>';
  var li2 = '<li id="btn-settings"><span>' + translations ['settings'] + '</span></li>';
  var li3 = '<li id="btn-signout"><span>' + translations ['signout'] + '</span></li>';

  var html = '';
  if (username == 'Guest' && known_users && known_users.length > 0)
    {
    for (var u in known_users)
      {
      if ('token' in known_users [u])
        html += '<li class="user" id="user-' + known_users[u]['token'] + '"><p><span>' + known_users [u]['name'] + '</p></span><img src="' + nroot + 'icon_remove.png" class="btn-remove"></li>'
      }
    if (html != '')
      html += '<li class="divider"></li>';
    }

  html += li1 + li2 + li3;
  $("#account-dropdown").html (html);

  if (username == 'Guest' || username == '')
    {
    $("#account-dropdown #btn-signout, #account-dropdown #btn-settings").hide();
    $("#account-dropdown #btn-sign").show();
    }
  else
    {
    $("#account-dropdown #btn-sign").hide();
    $("#account-dropdown #btn-signout, #account-dropdown #btn-settings").show();
    }
  $("#account-dropdown").show();
  $("#account-dropdown #btn-sign").unbind();
  $("#account-dropdown #btn-sign").click (function (event) { event.stopPropagation(); $("#account-dropdown").hide(); signin_screen_fresh(); });
  $("#account-dropdown li.user").unbind();
  $("#account-dropdown li.user").click (function (event) { event.stopPropagation(); $("#account-dropdown").hide(); signin_this_user ($(this).attr ("id")); });
  $("#account-dropdown li.user .btn-remove").unbind();
  $("#account-dropdown li.user .btn-remove").click (function (event) { event.stopPropagation(); device_remove_user ($(this).parent().attr ("id")); });
  $("#account-dropdown #btn-signout").unbind();
  $("#account-dropdown #btn-signout").click (function (event) { event.stopPropagation(); $("#account-dropdown").hide(); signout(); });
  $("#account-dropdown #btn-settings").unbind();
  $("#account-dropdown #btn-settings").click (function (event) { event.stopPropagation(); $("#account-dropdown").hide(); settings(); });
  account_tabs();
  $("body").unbind();
  $("body").click (close_all_dropdowns);
  }

function device_remove_user (id)
  {
  id = id.replace (/^user-/, '');
  log ('device remove user: ' + id);
  var d = $.get ('/playerAPI/deviceRemoveUser?device=' + device_id + '&' + 'user=' + id + rx(), function (data)
    {
    var lines = data.split ('\n');
    var fields = lines [0].split ('\t');
    if (fields [0] == '0')
      {
      for (var i in known_users)
        if ('token' in known_users [i] && known_users [i]['token'] == id)
          delete (known_users [i]);
      account_dropdown();
      }
    });
  }

function signin_this_user (id)
  {
  id = id.replace (/^user-/, '');
  log ('signin this user: ' + id);
  $("#signin-password").val ('');
  for (var u in known_users)
    {
    if (known_users [u]['token'] == id)
      $("#return-email").val (known_users [u]['email'])
    }
  signin_screen();
  $("#btn-create-account").removeClass ("on");
  $("#btn-signin").addClass ("on");
  }

function signin_screen_fresh()
  {
return;
  $("#return-email").val('');
  signin_screen();
  $("#btn-signin").removeClass ("on");
  $("#btn-create-account").addClass ("on");
  }

var after_sign;
var reset_password_occurred = false;

function signin_screen (callback)
  {
return;
  $("#account-dropdown, #hint-layer, #hint-bubble, #setbubble").hide();
  saved_thumbing = thumbing;
  thumbing = 'signin';
  signin_tab();
  after_sign = callback;
  try { ytmini [mini_player].pauseVideo(); } catch (error) {};
  account_tabs();
  }

function account_tabs()
  {
  $("#signin").unbind();
  $("#signin").click (function (event) { event.stopPropagation(); signin_tab(); });
  $("#signup").unbind();
  $("#signup").click (function (event) { event.stopPropagation(); signup_tab(); });
  }

function signin_tab()
  {
  log ('signin');
  $("#account-dropdown, #signup-panel").hide();
  $("#signin-layer, #signin-panel").show();
  $("#signin-password").val ("");
  $("#signup").removeClass ("on");
  $("#signin").addClass ("on");
  $("#btn-signin-close").unbind();
  $("#btn-signin-close").click (signin_screen_close);
  $("#btn-create-account").unbind();
  $("#btn-create-account").click (signup_tab);
  $("#signin-panel #btn-signin").unbind();
  $("#signin-panel #btn-signin").click (new_submit_login);
  $("#signin-panel .textfield").unbind();
  $("#signin-panel .textfield").focus (function()
    {
    $("#signin-panel .textfield").parent().removeClass ("on");
    $(this).parent().addClass ("on");
    });
  $("#signin-panel .textfield").parent().parent().unbind();
  $("#signin-panel .textfield").parent().parent().hover (function()
    {
    $(this).find (".textfield").focus();
    });

  if ($("#return-email").val() != '')
    $("#signin-password").focus();
  else
    $("#return-email").focus();
  }

function signup_tab()
  {
  log ('signup');
  if (read_only)
    {
    notice_ok (thumbing, translations ['readonly'], "");
    return;
    }
  $("#account-dropdown, #signin-panel").hide();
  $("#signin-layer, #signup-panel").show();
  $("#signin").removeClass ("on");
  $("#signup").addClass ("on");
  $("#btn-signin-close").unbind();
  $("#btn-signin-close").click (signin_screen_close);
  $("#btn-recaptcha").click (signup_recaptcha);

  $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(2)").unbind();
  $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(2)").click (function()
    {
    log ('Male!');
    $("#signup-panel .input-list-right li:nth-child(1) p").removeClass ("on");
    $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(2)").addClass ("on");
    });

  $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(3)").unbind();
  $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(3)").click (function()
    {
    log ('Female!');
    $("#signup-panel .input-list-right li:nth-child(1) p").removeClass ("on");
    $("#signup-panel .input-list-right li:nth-child(1) p:nth-child(3)").addClass ("on");
    });

  $("#signup-birthyear").val (translations ['s_example'] + ': 1986');

  $("#signup-panel .textfield").unbind();
  $("#signup-panel .textfield").focus (function()
    {
    if ($(this).attr ("id") == 'signup-birthyear' && $(this).val() == translations ['s_example'] + ': 1986')
      $(this).val ("");
    else if ($("#signup-birthyear").val() == '')
      $("#signup-birthyear").val (translations ['s_example'] + ': 1986');

    $("#signup-panel .textfield").parent().removeClass ("on");
    $(this).parent().addClass ("on");
    });
  $("#signup-panel .textfield").parent().parent().unbind();
  $("#signup-panel .textfield").parent().parent().hover (function()
    {
    $(this).find (".textfield").focus();
    });

  $("#signup-email").focus();

  $("#signup-panel #btn-signup").unbind();
  $("#signup-panel #btn-signup").click (new_submit_signup);
  // to enable recaptcha, uncomment below line and comment out above line
  // signup_recaptcha();
  }

function signup_recaptcha()
  {
  $("#signup-panel #btn-signup").unbind();
  log ('requesting captcha');
  $("#captcha").html ('');
  var query = '/playerAPI/requestCaptcha?user=' + user + '&' + 'action=1' + rx();
  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      var fields = lines[2].split ('\t');
      last_captcha = fields[0];
      var image = 'http://9x9ui.s3.amazonaws.com/captchas/' + last_captcha;
      $("#captcha").html ('<img src="' + image + '">');
      }
    $("#signup-panel #btn-signup").click (new_submit_signup);
    });
  }

function signin_screen_close()
  {
  $("#signin-layer").hide();
  thumbing = saved_thumbing;
  }

var original_settings = {};

function settings()
  {
  fresh_layer ("settings");
  thumbing = 'settings';
  set_hash ("#!settings");

  $("#waiting-layer").show();
  var d = $.get ("/playerAPI/getUserProfile?user=" + user + rx(), function (data)
    {
    $("#waiting-layer").hide();

    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields [0] == '0')
      {
      for (var i = 2; i < lines.length; i++)
        {
        log ('user config: ' + lines[i]);
        var fields = lines[i].split ('\t');
        if (fields[0] == 'name')
          {
          original_settings ['name'] = fields[1];
          $("#settings-username").val (fields[1]);
          }
        if (fields[0] == 'email')
          {
          $("#settings-email").text (fields[1]);
          }
        if (fields[0] == 'image')
          {
          $(".profile181 img").attr ("src", "");
          load_constrained_image (".profile181 img", fields[1]);
          }
        if (fields[0] == 'description')
          {
          original_settings ['about'] = fields[1];
          $("#settings-about").text (fields[1]);
          }
        }
      upload_thumb_setup();
      }

    $("#btn-change-password").unbind();
    $("#btn-change-password").click (settings_change_password);

    $("#btn-notification").unbind();
    $("#btn-notification").click (settings_notification);

    $("#btn-save-profile").unbind();
    $("#btn-save-profile").click (settings_save);
    });
  }

var reset_email;
var reset_token;

function reset_password()
  {
  reset_email = location.hash.match (/\!e=([^!]*)/) [1];
  reset_token = location.hash.match (/\!pass=([^!]*)/) [1];
  settings_change_password (true);
  }

function settings_change_password (reset_flag)
  {
  if (reset_flag)
    $("#settings-panel-change li").eq(0).hide();
  else
    $("#settings-panel-change li").eq(0).hide();

  input_pw_init ($("#settings-old-pw"));
  input_pw_init ($("#settings-new-pw1"));
  input_pw_init ($("#settings-new-pw2"));

  $("#settings-change-layer").fadeIn(300);

  $("#btn-change-close, #btn-cancel-password").unbind();
  $("#btn-change-close, #btn-cancel-password").click (function() { $("#settings-change-layer").hide(); });

  $("#btn-change-return-password").unbind();
  $("#btn-change-return-password").click (reset_flag ? settings_reset_password_save : settings_change_password_save);
  }

function settings_change_password_save()
  {
  var kk = '';
  var vv = '';

  var old_pw = $("#settings-old-pw").val();
  var new_pw = $("#settings-new-pw1").val();
  var vfy_pw = $("#settings-new-pw2").val();

  if (old_pw != "Old Password" && new_pw != "New Password" && vfy_pw != "Repeat New Password")
    {
    if (new_pw != vfy_pw)
      {
      notice_ok (thumbing, translations ['passmatch'], "");
      return;
      }
    kk += ',password';
    vv += ',' + encodeURIComponent (new_pw);
    kk += ',oldPassword';
    vv += ',' + encodeURIComponent (old_pw);
    }

  kk = kk.replace (/^,/, '');
  vv = vv.replace (/^,/, '');

  if (kk != '')
    {
    $("#waiting-layer").show();
    var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' + 'key=' + kk + '&' + 'value=' + vv + rx(), function (data)
      {
      $("#waiting-layer").hide();
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        $("#settings-change-layer").hide();
      else
        notice_ok (thumbing, 'chpw error ' + fields[0] + ': ' + fields[1], "");
      });
    }
  }

function settings_reset_password_save()
  {
  var new_pw = $("#settings-new-pw1").val();
  var vfy_pw = $("#settings-new-pw2").val();

  if (new_pw != "New Password" && vfy_pw != "Repeat New Password")
    {
    if (new_pw != vfy_pw)
      {
      notice_ok (thumbing, translations ['passmatch'], "");
      return;
      }
    }
  else
    {
    notice_ok (thumbing, "Please enter a new password", "");
    return;
    }

  $("#waiting-layer").show();
  var d = $.get ('/playerAPI/resetpwd?email=' + reset_email + '&' + 'token=' + reset_token + '&' + 'password=' + new_pw + rx(), function (data)
    {
    $("#waiting-layer").hide();
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      $("#settings-change-layer").hide();
      reset_password_occurred = true;
      notice_ok (thumbing, 'Password reset!', 'new_signup("home()")');
      }
    else
      notice_ok (thumbing, 'chpw error ' + fields[0] + ': ' + fields[1], "");
    });
  }

function settings_save()
  {
  log ('settings save');

  var kk = '';
  var vv = '';

  if ($("#settings-username").val() != original_settings ['name'])
    {
    kk += ',name';
    vv += ',' + encodeURIComponent ($("#settings-username").val());
    }

  if ($("#settings-about").val() != original_settings ['about'])
    {
    kk += ',description';
    vv += ',' + encodeURIComponent ($("#settings-about").val());
    }

  kk = kk.replace (/^,/, '');
  vv = vv.replace (/^,/, '');

  if (kk != '')
    {
    $("#waiting-layer").show();
    var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' + 'key=' + kk + '&' + 'value=' + vv + rx(), function (data)
      {
      $("#waiting-layer").hide();
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        notice_ok (thumbing, 'settings saved: ' + kk, "");
      else
        notice_ok (thumbing, 'chpw error ' + fields[0] + ': ' + fields[1], "");
      });
    }
  }

function settings_notification()
  {
  $("#settings-layer").hide();
  $("#settings-notification-layer").fadeIn (300);
  setTimeout ('scrollbar ("#notification-constrain", "#notification-list", "#notification-slider")', 300);
  $("#btn-save-notification, #btn-cancel-notification").click (function()
    {
    $("#settings-notification-layer").hide();
    $("#settings-layer").fadeIn (300);
    });
  }

function OLD_settings()
  {
  saved_thumbing = thumbing;
  thumbing = 'settings';
  log ('-- settings --');
  general_panel();
  }

function settings_tab (id)
  {
  if (id == 'general')
    general_panel();
  else if (id == 'sharing')
    sharing_panel();
  else if (id == 'preload')
    preload_panel();
  else if (id == 'resolution')
    resolution_panel();
  }

function settings_close()
  {
  thumbing = saved_thumbing;
  $("#settings-layer").hide();
  }

function general_panel()
  {
  log ('general panel');
  original_settings = {};

  $("#waiting-layer").show();
  var d = $.get ("/playerAPI/getUserProfile?user=" + user + rx(), function (data)
    {
    $("#waiting-layer").hide();
    $("#settings-layer .input-panel").hide();
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields [0] == '0')
      {
      for (var i = 2; i < lines.length; i++)
        {
        log ('user config: ' + lines[i]);
        var fields = lines[i].split ('\t');
        if (fields[0] == 'name')
          {
          original_settings ['name'] = fields[1];
          $("#reset-name span").html (fields[1]);
          general_input (fields[1], "#reset-name-input");
          }
        if (fields[0] == 'email')
          {
          $("#reset-email span").html (fields[1]);
          }
        if (fields[0] == 'gender')
          {
          $("#reset-gender-input p").removeClass ("on");
          original_settings ['gender'] = (fields[1] == '1' ? translations ['s_female'] : translations ['s_male']);
          if (fields[1] == '1')
            $("#reset-gender-input p:nth-child(2)").addClass ("on")
          else
            $("#reset-gender-input p:nth-child(1)").addClass ("on")
          $("#reset-gender span").html (original_settings ['gender']);
          }
        if (fields[0] == 'year')
          {
          original_settings ['year'] = fields[1];
          $("#reset-birth span").html (fields[1]);
          general_input (fields[1], "#reset-birth-input");
          }
        if (fields[0] == 'lang')
          {
          if (language != fields[1])
            set_language (fields[1]);
          }
        }

      $("#settings-layer .input-panel").hide();
      $("#reset-name-input, #reset-pw-input, #reset-gender-input, #reset-birth-input, #language-dropdown").hide();
      $("#general-panel .input-list .editable").show();
      $("#general-panel, #settings-layer").show();
      $("#settings-tabs li").removeClass ("on");
      $("#general").addClass ("on");
      $("#general-panel .editable").click (function()
        {
        log ('settings click: ' + $(this).attr ("id"));
	$(this).hide();
	var target = "#" + $(this).attr ("id") + "-input";
	$(target).show();
        if ($(this).attr ("id") == 'reset-gender')
          {
          $("#reset-gender-input p").unbind();
          $("#reset-gender-input p:nth-child(1)").click (function() { general_gender (translations ['s_male']); });
          $("#reset-gender-input p:nth-child(2)").click (function() { general_gender (translations ['s_female']); });
          }
        if ($(this).attr ("id") == 'reset-pw')
          general_reset_pw_ui();
        });
      original_settings ['language'] = language;
      if (language == 'zh')
        $("#selected-language span").html ("中文");
      else
        $("#selected-language span").html ("English");
      $("#btn-language").unbind();
      $("#btn-language").click (function (event) { event.stopPropagation(); settings_language_dropdown(); });
      original_settings ['sphere'] = sphere;
      if (sphere == 'zh')
        $("#selected-region span").html ("中文頻道");
      else
        $("#selected-region span").html ("English Channels");
      $("#btn-region").unbind();
      $("#btn-region").click (function (event) { event.stopPropagation(); settings_sphere_dropdown(); });
      $("#btn-settings-close").unbind();
      $("#btn-settings-close").click (settings_close);
      $("#btn-general-save").unbind();
      $("#btn-general-save").click (general_save);
      $("#settings-tabs li").unbind();
      $("#settings-tabs li").click (function() { settings_tab ($(this).attr ("id")); });
      }
    });
  }
 
function general_reset_pw_ui()
  { 
  general_input ("Enter your old password", "#reset-pw-input p:nth-child(1)");
  general_input ("Enter a new password", "#reset-pw-input p:nth-child(2)");
  general_input ("Re-enter the new password", "#reset-pw-input p:nth-child(3)");
  }

function general_input (hint, input)
  {
  var field = input + " .textfield";
  $(field).val (hint);
  if ($.browser.msie) return;
  $(field).focus (function()
    {
    var txt = $(this).val();
    if (txt == hint)
        $(this).val("");
    $(this).parents("p").addClass ("on");
    });
  $(field).blur (function()
    {
    var txt = $(this).val();
    if (txt == "")
      $(this).val (hint);
    $(this).parents("p").removeClass ("on");
    });
  }

function general_gender (gender)
  {
  log ('gender: ' + gender);
  $("#reset-gender-input p").removeClass ("on")
  if (gender == translations ['s_female'])
    $("#reset-gender-input p:nth-child(2)").addClass ("on")
  else
    $("#reset-gender-input p:nth-child(1)").addClass ("on")
  }

function settings_language_dropdown()
  {
  $("#language-dropdown li").removeClass ("on");
  if (language == 'zh')
    $("#language-dropdown li:nth-child(2)").addClass ("on");
  else
    $("#language-dropdown li:nth-child(1)").addClass ("on");

  $("#language-dropdown").show();
  $("#language-dropdown li:nth-child(1)").unbind();
  $("#language-dropdown li:nth-child(1)").click (function (event)
    {
    event.stopPropagation();
    $("#selected-language span").html ("English");
    set_language ('en');
    $("#language-dropdown").hide();
    });
  $("#language-dropdown li:nth-child(2)").unbind();
  $("#language-dropdown li:nth-child(2)").click (function (event)
    {
    event.stopPropagation();
    $("#selected-language span").html ("中文");
    set_language ('zh');
    $("#language-dropdown").hide();
    });
  $("body").unbind();
  $("body").click (close_all_dropdowns);
  }

function settings_sphere_dropdown()
  {
  $("#region-dropdown li").removeClass ("on");
  if (sphere == 'zh')
    $("#region-dropdown li:nth-child(2)").addClass ("on");
  else
    $("#region-dropdown li:nth-child(1)").addClass ("on");
  close_all_dropdowns();

  $("#region-dropdown").show();

  $("#region-dropdown li:nth-child(1)").unbind();
  $("#region-dropdown li:nth-child(1)").click (function (event)
    {
    sphere = 'en';
    event.stopPropagation();
    $("#selected-region span").html ("English Channels");
    close_all_dropdowns();
    });
  $("#region-dropdown li:nth-child(2)").unbind();
  $("#region-dropdown li:nth-child(2)").click (function (event)
    {
    sphere = 'zh';
    event.stopPropagation();
    $("#selected-region span").html ("中文頻道");
    close_all_dropdowns();
    });
  $("body").unbind();
  $("body").click (close_all_dropdowns);
  }

function general_save()
  {
  var new_settings = {};

  var kk = '';
  var vv = '';

  if (original_settings ['name'] != $("#reset-name-input .textfield").val())
    {
    kk += ',name';
    new_settings ['name'] = $("#reset-name-input .textfield").val();
    vv += ',' + encodeURIComponent (new_settings ['name']);
    }
  if (original_settings ['year'] != $("#reset-birth-input .textfield").val())
    {
    kk += ',year';
    new_settings ['year'] = $("#reset-birth-input .textfield").val();
    vv += ',' + encodeURIComponent (new_settings ['year']);
    }

  var gender = $("#reset-gender-input p:nth-child(2)").hasClass ("on") ? translations ['s_female'] : translations ['s_male'];
  if (original_settings ['gender'] != gender)
    {
    kk += ',gender';
    vv += ',' + (gender == translations ['s_female'] ? "1" : "0");
    }

  // var new_sphere = ( $("#selected-region span").html() == '中文節目' ) ? "zh" : "en";
  if (sphere != original_settings ['sphere'])
    {
    kk += ',sphere';
    vv += ',' + sphere;
    }

  if (language != original_settings ['language'])
    {
    kk += ',ui-lang';
    vv += ',' + language;
    }

  var old_pw = $("#reset-pw-input p:nth-child(1) .textfield").val();
  var new_pw = $("#reset-pw-input p:nth-child(2) .textfield").val();
  var vfy_pw = $("#reset-pw-input p:nth-child(3) .textfield").val();

  if (old_pw != "Enter your old password" && new_pw != "Enter a new password" && vfy_pw != "Re-enter the new password")
    {
    if (new_pw != vfy_pw)
      {
      notice_ok (thumbing, translations ['passmatch'], "");
      return;
      }
    kk += ',password';
    vv += ',' + encodeURIComponent (new_pw);
    kk += ',oldPassword';
    vv += ',' + encodeURIComponent (old_pw);
    }

  kk = kk.replace (/^,/, '');
  vv = vv.replace (/^,/, '');

  if (kk != '')
    {
    $("#waiting-layer").show();
    var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' + 'key=' + kk + '&' + 'value=' + vv + rx(), function (data)
      {
      $("#waiting-layer").hide();
      general_reset_pw_ui();
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields [0] != '0')
        {
        notice_ok (thumbing, translations ['errupdate'] + ': ' + fields[1], "");
        return;
        }
      notice_ok (thumbing, translations ['changessaved'], "");
      if ('name' in new_settings)
        $("#user, #sg-user, #selected-profile").html (new_settings ['name']);
      if (sphere != original_settings ['sphere'])
        {
        if (sphere == 'zh')
          $("#selected-programlang span").html ('中文頻道');
        else
          $("#selected-programlang span").html ('English Channels');
        /* reload store underneath us, if necessary */
        if (saved_thumbing = 'store' || saved_thumbing == 'store-wait')
          {
          store_tab = undefined;
          store();
          }
        }
      });
    }
  else
    notice_ok (thumbing, translations ['nochanges'], "");
  }

function sharing_panel()
  {
  log ('sharing panel');
  $("#settings-tabs li").removeClass ("on");
  $("#sharing").addClass ("on");
  $("#settings-layer .input-panel").hide();
  $("#sharing-panel").show();
  $("#btn-sharing-save").unbind();
  }

function preload_panel()
  {
  log ('preload panel');
  $("#settings-tabs li").removeClass ("on");
  $("#preload").addClass ("on");
  $("#settings-layer .input-panel").hide();
  $("#preload-panel").show();

  $("#preload-panel .radio-item").removeClass ("on");
  /* obtained from userTokenVerify */
  $("#preload-" + preload).addClass ("on");

  $("#preload-panel .radio-item").unbind();
  $("#preload-panel .radio-item").click (function()
    {
    log ('preload: ' + $(this).attr ("id"));
    $("#preload-panel .radio-item").removeClass ("on");
    $(this).addClass ("on");
    });

  $("#btn-preload-save").unbind();
  $("#btn-preload-save").click (function()
    {
    preload = $("#preload-panel .radio-item.on").attr ("id").replace (/^preload-/, '');
    $("#waiting-layer").show();
    var d = $.get ('/playerAPI/setUserPref?user=' + user + '&' + 'key=preload' + '&' + 'value=' + preload + rx(), function (data)
      {
      $("#waiting-layer").hide();
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        notice_ok (thumbing, translations ['changessaved'], "");
      else
        notice_ok (thumbing, translations ['errupdate'] + ': ' + fields[1], "");
      });
    });
  }

function resolution_panel()
  {
  log ('resolution panel');
  $("#settings-tabs li").removeClass ("on");
  $("#resolution").addClass ("on");
  $("#settings-layer .input-panel").hide();
  $("#resolution-panel").show();

  $("#resolution-panel .radio-item").removeClass ("on");
  $("#rez-" + yt_quality).addClass ("on");

  $("#resolution-panel .radio-item").unbind();
  $("#resolution-panel .radio-item").click (function()
    {
    log ('resolution: ' + $(this).attr ("id"));
    $("#resolution-panel .radio-item").removeClass ("on");
    $(this).addClass ("on");
    });

  $("#btn-resolution-save").unbind();
  $("#btn-resolution-save").click (function()
    {
    yt_quality = $("#resolution-panel .radio-item.on").attr ("id").replace (/^rez-/, '');
    $("#waiting-layer").show();
    var d = $.get ('/playerAPI/setUserPref?user=' + user + '&' + 'key=resolution' + '&' + 'value=' + yt_quality + rx(), function (data)
      {
      $("#waiting-layer").hide();
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        notice_ok (thumbing, translations ['changessaved'], "");
      else
        notice_ok (thumbing, translations ['errupdate'] + ': ' + fields[1], "");
      });
    });
  }

var help_tab;
var retrieved_tutorial = false;
var tutorial_reset_function;

function help()
  {
  try { state(); } catch (error) {}; /* record state in case diagnostics needed */
  log ('-- help --');
  try { ytmini [mini_player].pauseVideo(); } catch (error) {};
  thumbing = 'help';
  $("#help-layer").show();
  $("#hint-layer, #hint-bubble").hide();
  $("#btn-help-close").unbind();
  $("#btn-help-close").click (help_close);
  $("#tutorial").unbind();
  $("#tutorial").click (tutorial);
  $("#faq").unbind();
  $("#faq").click (faq);
  $("#sync").unbind();
  $("#sync").click (help_sync);
  $("#report").unbind();
  $("#report").click (help_report);
  tutorial();
  }

function tutorial()
  {
  if (help_tab != 'tutorial')
    {
    $("#help-tabs li").removeClass ("on");
    $("#tutorial").addClass ("on");
    $("#help-holder .input-panel").hide();
    $("#tutorial-panel").show();
    help_tab = 'tutorial';
    if (!retrieved_tutorial)
      {
      log ('retrieving: tutorial');
      $("#tutorial-list").html ('');
      var query = '/playerAPI/staticContent?key=new-tutorial&lang=' + language + rx();
      var d = $.get (query, function (data)
        {
        $("#pool-waiting").hide();
        var lines = data.split ('\n');
        var fields = lines[0].split ('\t');
        if (fields[0] == '0')
          {
          data = data.replace (lines[0] + '\n' + lines[1] + '\n', '');
          data = data.replace (/src="images\//gi, 'src="' + nroot);
          if (data.match (/<!--RESET([\s\S]*?)-->/))
            {
            tutorial_reset_function = data.match(/<!--RESET([\s\S]*?)-->/)[1]
            }
          $("#tutorial-list").html (data);
          $("#tutorial-list .btn-hilite").each (function()
            {
            $(this).unbind();
            $(this).click (function() { eval ($(this).attr ("data-javascript")); });
            });
          retrieved_tutorial = true;
          tutorial_reset();
          }
        });
      }
    }
  tutorial_reset();
  }

function tutorial_reset()
  {
  try { eval (tutorial_reset_function); } catch (error) {};
  }

function help_close()
  {
  $("#help-layer").hide();
  tutorial_reset();
  store();
  }

function faq()
  {
  if (help_tab != 'faq')
    {
    tutorial_reset();
    $("#help-tabs li").removeClass ("on");
    $("#faq").addClass ("on");
    $("#help-holder .input-panel").hide();
    $("#faq-panel").show();
    help_tab = 'faq';
    if (! ($("#faq-list").html().match (/^\s*$/)))
      return;
    log ('retrieving: faq');
    var query = '/playerAPI/staticContent?key=faq&lang=' + language + rx();
    var d = $.get (query, function (data)
      {
      $("#pool-waiting").hide();
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields[0] == '0')
        {
        data = data.replace (lines[0] + '\n' + lines[1] + '\n', '');
        data = data.replace (/src="images\//gi, 'src="' + nroot);
        $("#faq-list").html (data);
        $("#faq-list h3").click (function()
          {
	  target = "#" + $(this).attr("id") + "-content";
	  if ($(target).css ("display") == "block")
            {
	    $("#faq-list p").removeClass ("on");
	    $(".accordion-block").hide();
	    $(target).hide();
	    scrollbar ("#faq-content", "#faq-list", "#faq-slider");
	    }
          else
            {
	    $("#faq-list p").removeClass ("on");
	    $(this).children("p").addClass ("on");
	    $(".accordion-block").hide();
	    $(target).show();
	    scrollbar ("#faq-content", "#faq-list", "#faq-slider");
	    }
          });
        }
      });
    }
  }

var nodes = [];
var nodes_incoming = [];

function help_sync()
  {
  if (help_tab != 'sync')
    {
    tutorial_reset();
    $("#help-tabs li").removeClass ("on");
    $("#sync").addClass ("on");
    $("#help-holder .input-panel").hide();
    $("#sync-panel").show();
    help_tab = 'sync';
    $("#sync-list").html ('');
    relay_post ('NODES');
    }
  }

function redraw_help_sync()
  {
  tutorial_reset();

  /* reset the scroll bar to the top, or sometimes the screen will be blank */
  $("#sync-list").css ("top", "0px");

  var html = '<span style="color: white">This device is: <span id="thisdevice" style="color: orange; font-size: 1em">[Wait...]</span></span><br>\n';
  html += '<span style="color: white">User token: <span style="color: orange; font-size: 1em">' + user + '</span></span><br>';
  html += '<span style="color: white">Device token: <span style="color: orange; font-size: 1em">' + device_id + '</span></span><br>';

  html += '<table border="1" columns="6">';
  for (var n in nodes)
    {
    if (nodes.hasOwnProperty (n) && 'name' in nodes [n])
      {
      var node = nodes [n];
      html += '<tr id="node-' + n + '">'
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
  html += '<h3 id="diag-relay"><p><span>9x9 Sync Log</span><span class="switch"><img src="' + nroot + 'icon_collapse.png" class="icon-collapse"><img src="' + nroot + 'icon_expand.png" class="icon-expand"></span></p></h3>';
  html += '<div id="diag-relay-content" class="accordion-block"></div>';
  html += '<h3 id="diag-videolog"><p><span>Videos Played this Session</span><span class="switch"><img src="' + nroot + 'icon_collapse.png" class="icon-collapse"><img src="' + nroot + 'icon_expand.png" class="icon-expand"></span></p></h3>';
  html += '<div id="diag-videolog-content" class="accordion-block"></div>';

  $("#sync-list").html (html);
  scrollbar ("#sync-content", "#sync-list", "#sync-slider");

  $("#sync-list h3").click (function()
    {
    target = "#" + $(this).attr("id") + "-content";
    if ($(target).css ("display") == "block")
      {
      $("#sync-list p").removeClass ("on");
      $(".accordion-block").hide();
      $(target).hide();
      scrollbar ("#sync-content", "#sync-list", "#sync-slider");
      }
    else
      {
      $("#sync-list p").removeClass ("on");
      $(this).children("p").addClass ("on");
      $(".accordion-block").hide();
      $(target).show();
      help_sync_expand ($(target).attr ("id"));
      scrollbar ("#sync-content", "#sync-list", "#sync-slider");
      }
    });

  relay_post ("WHOAMI");
  }

function help_sync_expand (id)
  {
  log ('sync expand: ' + id);

  if (id == 'diag-relay-content')
    {
    var html = '<p><span>';
    for (var i in relay_log_data)
      {
      if (relay_log_data.hasOwnProperty (i))
        {
        var when = new Date (relay_log_data [i]['timestamp']);
        var hh = ("00" + "" + when.getHours()).slice (-2);
        var mm = ("00" + "" + when.getMinutes()).slice (-2);
        var ss = ("00" + "" + when.getSeconds()).slice (-2);
        var fulltime = hh + ':' + mm + ':' + ss;
        html += fulltime + ' ' + relay_log_data [i]['direction'] + ' ' + relay_log_data [i]['command'] + '<br>';
        }
      }
    html += '</span></p>';
    $("#" + id).html (html);
    }
  else if (id == 'diag-videolog-content')
    {
    var html = '<p><span>';
    for (var i in video_log_data)
      {
      if (video_log_data.hasOwnProperty (i))
        {
        var when = new Date (relay_log_data [i]['timestamp']);
        var hh = ("00" + "" + when.getHours()).slice (-2);
        var mm = ("00" + "" + when.getMinutes()).slice (-2);
        var ss = ("00" + "" + when.getSeconds()).slice (-2);
        var fulltime = hh + ':' + mm + ':' + ss;
        html += fulltime + ' ' + video_log_data [i]['forum'] + ' ';
        html += video_log_data [i]['id'] + ' ' + video_log_data [i]['video'];
        html += ' <a href="http://www.youtube.com/watch?v=' + video_log_data [i]['video'] + '" target="_blank">[youtube]</a>';
        html += '<br>';
        }
      }
    html += '</span></p>';
    $("#" + id).html (html);
    }

  scrollbar ("#sync-content", "#sync-list", "#sync-slider");
  }

function help_sync_contacts()
  {
  $("#thisdevice").html (iam);
  for (var n in nodes)
    {
    if (nodes.hasOwnProperty (n) && 'name' in nodes [n])
      {
      var node = nodes [n];
      relay_post ('TELL ' + node ['name'] + ' TELL ' + iam + ' REPORT ' + node ['name'] + ' ACK');
      $("#node-" + n + " td:nth-child(5) span").html ("Contacting...");
      if (iam == node ['name'])
        $("#node-" + n + " td span").css ({ "font-weight": "bold", "color": "orange" });
      }
    }
  }

function help_sync_ack (node)
  {
  var node_data = nodes [node];
  $("#node-" + node + " td:nth-child(5) span").html ("Live!");
  if (iam == node_data ['name'])
    $("#node-" + node + " td span").css ({ "font-weight": "bold", "color": "orange" });
  }

function help_report()
  {
  if (help_tab != 'report')
    {
    tutorial_reset();
    $("#help-tabs li").removeClass ("on");
    $("#report").addClass ("on");
    $("#help-holder .input-panel").hide();
    $("#report-panel").show();
    $("#btn-report").unbind();
    $("#btn-report").click (submit_help_report);
    $("#problem-input .textfield").val ("");
    /* shouldn't borrow this function from email, but it does exactly what we want */
    email_focus_blur ("#problem-input", translations ['problemo'])
    help_tab = 'report';
    }
  }

function submit_help_report()
  {
  var comment = $("#problem-input .textfield").val();

  if (comment == '' || comment == 'Enter your problem')
    {
    notice_ok (thumbing, 'Please provide a comment', "");
    return;
    }

  comment = encodeURIComponent (comment);

  submit_user_report (comment);
  }

function submit_user_report (comment)
  {
  var session = Math.floor (timezero/1000);

  var query;
  if (username == 'Guest')
    query = '/playerAPI/userReport?device=' + device_id + '&' + 'session=' + session + '&' + 'comment=' + comment + rx();
  else
    query = '/playerAPI/userReport?device=' + device_id + '&' + 'user=' + user + '&' + 'session=' + session + '&' + 'comment=' + comment + rx();

  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');

    if (comment != 'Automated comment')
      {
      if (fields[0] == '0')
        {
        var response = 'Thank you for your report.';
        for (var i = 2; i < lines.length; i++)
          {
          var fields = lines[i].split ('\t');
          if (fields[0] == 'id')
            response = 'Thank you. Your ticket number is: <span style="color: orange">' + fields[1] + '</span>';
          }
        notice_ok (thumbing, response, "help_close()");
        /* flush this just in case */
        report_submit_();
        }
      else
        notice_ok (thumbing, 'Problem submitting your report: ' + fields[1], "help_close()");
      }
    });
  }

/* right panel */
var store_tab;

/* left panel */
var store_preview_type;

var store_cat;
var store_index;
var previous_store_index;
var store_channel;
var store_back_channel;
var store_cat_parent;
var store_direction = 'F';
var store_next_random_channel;
var store_flipping;
var landing_page_override;

/* save current place upon return to store */
var store_last_channel;
var store_last_program_index;

/* save current place upon return to recommended sets */
var store_recommend_index = 1;      /* category */
var store_recommend_leftoff = {};   /* channel in each category */

/* save current place upon return to channel directory */
var store_dir_which_channel = 1;

var allcats = {};
var recommends = [];

function store()
  {
  home();
  }

function set_sphere (region)
  {
  sphere = region;
  update_sphere_display();
  save_sphere_setting();
  close_all_dropdowns();
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    store_tab = undefined;
    store();
    }
  }

function update_sphere_display()
  {
  $("#programlang li").removeClass("on");
  if (sphere == 'zh')
    $("#programlang li").eq(1).addClass("on");
  else
    $("#programlang li").eq(0).addClass("on");
  }

function store_info()
  {
  if ($("#channel-bubble").css ("display") == 'none')
    {
    $("#btn-info").addClass ("on");
    $("#channel-bubble").show();
    scrollbar ("#chbubble-content", "#chbubble-list", "#chbubble-slider");
    }
  else
    {
    $("#btn-info").removeClass ("on");
    $("#channel-bubble").hide();
    }
  }

function store_sound()
  {
  if (player_mute)
    {
    player_mute = false;
    $("#btn-sound").addClass ("on");
    try { ytmini [mini_player].unMute(); } catch (error) {};
    if (current_tube == 'au')
      audio_unmute();
    }
  else
    {
    player_mute = true;
    $("#btn-sound").removeClass ("on");
    try { ytmini [mini_player].mute(); } catch (error) {};
    if (current_tube == 'au')
      audio_mute();
    }
  player_mute_override = undefined;
  user_has_set_muting = true;
  }

function store_recommended (startflag)
  {
  $("#recommended-content").html ('')
  switch_to_recommended_tab();
  $("#category-content, #yourown-content, .slider-wrap").hide();
  store_load_recommended (startflag);
  }

function switch_to_recommended_tab()
  {
  store_tab = 'recommended';
  store_preview_type = 'recommended';
  $("#tabs li").removeClass ("on");
  $("#recommended").addClass ("on");
  $("#yourown-content, #category-content").hide();
  $("#recommended-content").show();
  }

function store_category()
  {
  $("#category-content").html ('');
  switch_to_category_tab();
  $("#recommended-content, #yourown-content, .slider-wrap, #setbubble, #setbubbleclick").hide();
  // allcats = {};
  if ('0' in allcats)
    {
    log ('re-entering channel directory');
    redraw_store_cats();
    if (store_flipping)
      start_flipping_expand (store_flipping, store_dir_which_channel);
    }
  else
    store_load_category ('0');
  }

function switch_to_category_tab()
  {
  store_tab = 'category';
  $("#tabs li").removeClass ("on");
  $("#category").addClass ("on");
  $("#yourown-content, #recommended-content").hide();
  $("#category-content").show();
  }

function store_yourown()
  {
  store_tab = 'yourown';
  $("#tabs li").removeClass ("on");
  $("#yourown").addClass ("on");
  $("#recommended-content, #category-content, .slider-wrap, #setbubble, #setbubbleclick").hide();
  $("#yourown-content").show();
  $("#yourown-content #btn-add-yt").unbind();
  $("#yourown-content #btn-add-yt").click (store_yourown_submit);
  }

function store_yourown_submit()
  {
  log ('store submit');
  var url = $("#yourown-content #yt-input .textfield").val();

  if (url == '' || url.match (/https?:\/\/(www\.)?(youtube|facebook)\.com/i))
    {
    notice_ok (thumbing, 'Please provide a valid URL', "");
    return;
    }

  if (url.match (/^https?:\/\/(www\.)?(facebook|youtube)\.com\//) && url != 'http://www.youtube.com/user/')
    {
    if (username == 'Guest')
      {
      log ('yourown submit: user is not signed in');
      suppress_success_dialog = true;
      signin_screen ('store_yourown_via_signin("' + url + '")');
      return;
      }
    else
      store_yourown_submit_inner (url);
    }
  else
    notice_ok (thumbing, translations ['needurl'], "");
  }

function store_yourown_via_signin (url)
  {
  /* if 0 channels, wait for load -- user cannot delete system channel */
  if (channels_in_guide() == 0)
    do_this_after_fetch_channels = 'store_yourown_submit_inner("' + url + '")';
  else
    store_yourown_submit_inner (url);
  }

function store_yourown_submit_inner (url)
  {
  var position = ipg_cursor;
  if (!position || position in channelgrid)
    {
    position = first_empty_channel();
    if (!position)
      {
      notice_ok (thumbing, translations ['allfull'], "");
      return;
      }
    }

  var serialized = 'url=' + encodeURIComponent (url) + '&' + 'user=' + user + '&' + 'grid=' + server_grid (position) + rx();
  log ("POST channelSubmit: " + serialized);
  $.post ("/playerAPI/channelSubmit", serialized, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      {
      if (fields[0] == '351' && fields[1].match (/Channel is in your Guide/i))
        notice_ok (thumbing, translations ['alreadyin'], "");
      if (fields[0] == '903')
        notice_ok (thumbing, translations ['readonly'], "");
      else
        notice_ok (thumbing, 'Error submitting channel: ' + fields [1], "");
      return;
      }
    fields = lines[2].split('\t');

    var channel;
    var channel_id = fields[0];

    if (url.match (/^http:\/\/(www\.)?facebook\.com\//))
      channel = { 'id': channel_id, 'name': fields[1], 'thumb': fields[2], 'nature': '5', 'extra': url };
    else
      channel = { 'id': channel_id, 'name': fields[1], 'thumb': fields[2] };

    pool [channel_id] = channel;
    channelgrid [position] = channel;

    redraw_ipg();
    ipg_sync();

    add_dirty_channel (channel_id);

    report ('c', 'throw ' + position + ' ' + channel_id);

    missing_youtube_thumbnails();
    relay_post ("UPDATE");

    $("#guide-add-layer").hide();

    update_cart_bubble (channels_in_guide());
    });
  }

var slr_cache = {};

function store_load_recommended (startflag)
  {
  recommends = [];

  var query = '/playerAPI/listRecommended?' + 'lang=' + sphere + mso() + rx('');

  if (query in slr_cache)
    {
    store_load_recommended_inner (slr_cache [query], startflag);
    return;
    }

  $("#pool-waiting").show();
  var d = $.get (query, function (data)
    {
    $("#pool-waiting").hide();
    slr_cache [query] = data;
    store_load_recommended_inner (data, startflag);
    });
  }

function store_load_recommended_inner (data, startflag)
  {
  var lines = data.split ('\n');
  var rposition = 0;
  for (var i = 2; i < lines.length && lines[i] != '' && lines[i] != '--'; i++)
    {
    var fields = lines[i].split ('\t');
    if (!fields[3] || fields[3] == '')
      fields[3] = errorthumb;
    if (!fields[4] || fields[4] == '')
      fields[4] = '0';
    log ("LINE :: 0:" + fields[0] + " 1:" + fields[1] + " 2" + fields[2] + " 3:" + fields[3] + " 4:" + fields[4]);
    if (fields[1] != '')
      recommends [++rposition] = { id: fields[0], name: fields[1], desc: fields[2], thumb: fields[3], count: fields[4], content: [] };
    }
  redraw_store_recommended();
  if (landing_page_override && recommends.length > 0)
    {
    log ('landing page override: ' + landing_page_override);
    for (var i in recommends)
      {
      var cat = recommends [i];
      if (cat ['id'] == landing_page_override)
        {
        /* already a recommended set */
        if (i == 1)
          {
          recommended (i);
          return;
          }
        else
          {
          /* remove it, since it's not first. add it back in next block of code */
          recommends.splice (i, 1);
          }
        }
      }
    /* not a recommended set, insert it as the first one */
    for (var ell in landing_pages)
      {
      if (landing_pages.hasOwnProperty (ell))
        {
        var lp = landing_pages [ell];
        if (lp ['setid'] == landing_page_override)
          {
          var newset = { id: lp ['setid'], name: lp ['name'], desc: lp ['motto'], 
                         thumb: lp ['setlogo'], count: lp ['grid'].length, content: lp ['grid'] };
          recommends.splice (1, 0, newset);
          redraw_store_recommended();
          recommended (1);
          return;
          }
        }
      }
    }
  if (startflag && recommends.length > 0)
    recommended (store_recommend_index);
  }

var setbubble_timex;

function redraw_store_recommended()
  {
  var html = '<ul id="recommended-list">';

  for (var r in recommends)
    {
    if (recommends.hasOwnProperty (r))
      {
      var cat = recommends [r];
      var plural = " " + (cat ['count'] == 1 ? translations ['channel_lc'] : translations ['channels_lc']);
      html += '<li id="rec-' + r + '"><div class="set-icon">';
      html += '<p class="bg-set">';
      html += '<img src="' + nroot + 'bg_set_off.png" class="bg-set-off">';
      html += '<img src="' + nroot + 'bg_set_on.png" class="bg-set-on"></p>';
      html += '<img src="' + cat['thumb'] + '" class="thumbnail">';
      html += '<img src="' + nroot + 'set_corner.png" class="set-corner">';
      html += '<img src="' + nroot + 'set_front.png" class="set-front">';
      html += '<img src="' + nroot + 'set_back.png" class="set-back">';
      html += '</div>';
      html += '<p class="set-title"><span>' + cat['name'] + '</span></p>';
      html += '<p class="set-description"><span>' + cat['desc'] + '</span></p>';
      var color = (r == store_recommend_index) ? "brown" : "orange";
      html += '<p class="channel-number"><span style="color: ' + color + '">' + cat['count'] + plural + '</span></p>';
      html += '</li>';
      }
    }

  html += '</ul>';
  $("#recommended-content").html (html);

  scrollbar ("#recommended-content", "#recommended-list", "#recommended-slider");

  /* in case of broken thumbs */
  $("#recommended-content .thumbnail").error (function() { $(this).unbind("error").attr("src", errorthumb); });

  $("#recommended-content li").unbind();
  $("#recommended-content li").click (function (event) { recommended_click (event, $(this).attr("id")); });

  store_recommend_on();
  recommended_hover_bindings();
  }

function recommended_hover_bindings()
  {
  $("#recommended-content .set-icon").unbind();
  $("#recommended-content .set-icon").hover (function()
    {
    if ($("#setbubbleclick").css ('display') != 'none')
      {
      $("#setbubble").hide();
      return;
      }
    clearTimeout (setbubble_timex);
    var title = $(this).siblings (".set-title").text();
    var description = $(this).siblings (".set-description").text();
    $("#setbubble-title").text (title);
    $("#setbubble-description").text (description);
    var offset = $(this).offset();
    var left = offset.left- $("#setbubble").width() / 6;
    var top = offset.top - $("#setbubble").height();
    // $("#setbubble").css ({ left: left, top: top });
    log ('setbubble');
    setbubble_timex = setTimeout (function() { setbubble_fadein (left, top); });
    },
  function()
    {
    clearTimeout (setbubble_timex);
    // $("#setbubble").fadeOut();
    $("#setbubble").hide();
    });
  }

function setbubble_fadein (left, top)
  {
  clearTimeout (setbubble_timex);
  log ('setbubble fadeIn');
  if ($("#setbubbleclick").css ('display') == 'none')
    $("#setbubble").fadeIn().css ({ left: left, top: top }); 
  else
    $("#setbubble").hide();
  }

function update_store_recommend_counts_only()
  {
  for (var r in recommends)
    {
    if (recommends.hasOwnProperty (r))
      {
      var cat = recommends [r];
      var plural = " " + (cat ['count'] == 1 ? translations ['channel_lc'] : translations ['channels_lc']);
      var color = (r == store_recommend_index) ? "brown" : "orange";
      $("#recommended-list li").eq(r-1).children (".channel-number").children("span").html (cat ['count'] + plural);
      }
    } 
  $("#recommended-list li").children (".channel-number").children("span").css ("color", "orange");
  $("#recommended-list li").eq(store_recommend_index-1).children (".channel-number").children("span").css ("color", "brown");
  }

function store_recommend_on()
  {
  $("#recommended-content li").removeClass ("on");
  $("#rec-" + store_recommend_index).addClass ("on");
  $("#flashing").remove();
  add_previewing ($("#rec-" + store_recommend_index));
  }

/* turn off glowing */
function store_recommend_off()
  {
  $("#recommended-content li").removeClass ("on");
  $("#flashing").remove();
  $("#recommended-content .channel-number span").css ("color", "orange");
  }

function add_previewing (obj)
  {
  log ('add previewing: ' + $(obj).attr("id"));
  $(obj).append ('<p id="flashing"></p>');
  run_previewing ("#flashing");
  }
	
function run_previewing (obj)
  {
  if ($.browser.msie || $.browser.opera)
    {
    $(obj).addClass("on");
    $(obj).queue (function (next)
      {
      $(obj).animate ({ "backgroundColor": "#e5e5e5"}, 1500) .animate ({ "backgroundColor": "#bbb"}, 1500);
      $(obj).queue (arguments.callee);
      next();
      });
    }
  else
    {
    $(obj).addClass("on");
    }
  }

function store_unbind()
  {
  $("#recommended-content li").unbind();
  $("#btn-flip-preview, #btn-flip-next, #btn-flip-prev, #btn-flip-back").unbind();
  }

function store_bind()
  {
  $("#recommended-content li").unbind();
  $("#recommended-content li").click (function (event) { recommended_click (event, $(this).attr("id")); });
  recommended_hover_bindings();
  $("#btn-flip-preview, #btn-flip-next").unbind();
  $("#btn-flip-preview, #btn-flip-next").click (function (event) { event.stopPropagation(); flip_next(); });
  $("#btn-flip-back").unbind();
  $("#btn-flip-back").click (function (event) { event.stopPropagation(); flip_back(); });
  $("#btn-flip-prev").unbind();
  $("#btn-flip-prev").click (function (event) { event.stopPropagation(); flip_prev(); });
  }

function recommended_click (event, id)
  {
  event.stopPropagation();
  $("#hint-layer").hide();
  try { $("#" + id).effect ("transfer", { to: "#preview-video" }, 500); } catch (error) {};

  id = id.replace (/^rec-/, '');
  log ("RECOMMENDED CLICK, index: " + id);
  recommended (id);
  /* note that at this point though, there may be no channels available! but need to draw the header */
  $("#setbubbleclick").fadeIn (400);
  $("#setbubble").fadeOut();
  redraw_setbubbleclick();
  $("body").click (function() { $("#setbubbleclick").hide(); });
  }

function redraw_setbubbleclick()
  {
  }

function setbubbleclick_highlight()
  {
  }

var rec_cache = {};

function recommended (index)
  {
  if (! (index in recommends))
    log ('index "' + index + '" not in recommended sets!');
  var cat = recommends [index];
  if (!cat || !('id' in cat))
    {
    log ('recommended(): category not found (race condition)');
    return;
    }
  store_cat = cat ['id'];
  store_cat_parent = undefined;
  store_recommend_index = index;
  previous_store_index = -1;
  store_direction = 'F';

  log ('recommended: ' + index);

  $("#recommended-content li").removeClass ("on");
  $("#rec-" + index).addClass ("on");

  thumbing = 'store-wait';
  store_unbind();

  var query = '/playerAPI/setInfo?set=' + cat ['id'] + '&' + 'lang=' + sphere + mso() + rx('');
  if (query in rec_cache)
    {
    log ('recommended: using cache for ' + cat ['id']);
    recommended_inner (index, cat, rec_cache [query]);
    return;
    }

  $("#preview-waiting").show();
  log ('recommended: query: ' + query);
  var d = $.get (query, function (data)
    {
    $("#preview-waiting").hide();
    rec_cache [query] = data;
    recommended_inner (index, cat, data);
    });
  }

function recommended_inner (index, cat, data)
  {
  thumbing = 'store';
  store_bind();

  var lines = data.split ('\n');
  var fields = lines[0].split ('\t');
  if (fields[0] == '0')
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
        set_pool [cat ['id']] = {};
        set_pool [cat ['id']]['piwik'] = fields[1];
        }

      if (block == 3)
        {
        var channel = line_to_channel (lines[i]);
        // var channel = { 'id': fields[1], 'name': fields[2], 'desc': fields[3], 'thumb': fields[4], 'count': fields[5], 'type': fields[6], 'status': fields[7], 'nature': fields[8], 'extra': fields[9], 'timestamp': fields[10], 'sortorder': fields[11], 'piwik': fields[12], leftoff: fields[13], curator: fields[14] };
        if (parseInt (channel ['nature']) >= 3 && parseInt (channel ['nature']) <= 9)
          {
          recommends [index]['content'][++count] = channel;
          pool [channel['id']] = channel;
          }
        log ('channel ' + channel['id'] + ': ' + channel['name'] + ' nature: ' + channel['nature']);
        }
      }
    if (cat ['count'] != count)
      {
      /* count is out of date, update in place */
      cat ['count'] = count;
      // redraw_store_recommended();
      update_store_recommend_counts_only();
      }
    /* make sure the brown gets updated in the current set */
    // redraw_store_recommended();
    update_store_recommend_counts_only();

    redraw_setbubbleclick();

    store_preview_type = 'recommended';
    store_recommend_on();

    /* if we visited this set before, use the last accessed channel */
    var start_channel = 1;
    if (cat ['id'] in store_recommend_leftoff && store_recommend_leftoff [cat ['id']] in recommends [index]['content'])
      {
      start_channel = store_recommend_leftoff [cat ['id']]
      log ('recommend set ' + cat['id'] + ' leftoff channel: ' + start_channel);
      }
    else
      {
      if (cat ['id'] in store_recommend_leftoff)
        log ('recommend leftoff given for ' + cat['id'] + ', but ' + store_recommend_leftoff [cat ['id']] + ' is not in this set');
      else
        log ('no recommend leftoff for ' + cat['id'] + ', using: ' + start_channel);
      }

    if (start_channel in recommends [index]['content'])
      store_set_channel (cat ['id'], start_channel, recommends [index]['content'][start_channel]['id']);
    else
      notice_ok (thumbing, 'No channels in this recommended set!', "");
    }
  else
    notice_ok (thumbing, translations ['uncaught'] + " Code: " + fields[1], "");
  }

var slc_cache = {};

function store_load_category (id)
  {
  var short_cat = id;

  if (id.match (/-/))
    {
    var fields = id.split ('-');
    short_cat = fields [fields.length - 1];
    }
  log ('store load category: ' + short_cat);

  var cat_arg = (id == '0') ? '' : ('category=' + short_cat + '&');
  var query = '/playerAPI/category?' + cat_arg + 'lang=' + sphere + mso() + rx('');

  if (query in slc_cache)
    {
    log ('using cache for category: ' + id);
    store_load_category_inner (id, short_cat, slc_cache [query]);
    return;
    }

  log ('category query: ' + query);
  $("#pool-waiting").show();
  var d = $.get (query, function (data)
    {
    $("#pool-waiting").hide();
    slc_cache [query] = data;
    store_load_category_inner (id, short_cat, data);
    });
  }

function store_load_category_inner (id, short_cat, data)
  {
  parse_category_data (short_cat, data);
  allcats [id]['expanded'] = true;
  redraw_store_cats();
  }

function parse_category_data (category, data)
  {
  log ('parse category data, category: ' + category);
  var lines = data.split ('\n');
  var fields = lines[0].split ('\t');
  if (fields[0] == '0')
    {
    var start_line;
    var got_category;
    var got_piwik;
    for (var i = 2; i < lines.length; i++)
      {
      var fields = lines [i].split ('\t');
      if (lines [i] == '--')
        {
        start_line = i;
        break;
        }
      if (fields.length == 1)
        got_category = fields [0];
      else if (fields [0] == 'id')
        got_category = fields [1];
      else if (fields [0] == 'piwik')
        got_piwik = fields [1];
      }

log ('start line: ' + start_line);
log ('got category: ' + got_category);
log ('got piwik: ' + got_piwik);

    if (got_piwik)
      {
      set_pool [got_category] = {};
      set_pool [got_category]['piwik'] = got_piwik;
      }

    if (got_category == category)
      {
      var count = 0;
      var cat_count = 0;

      if (! (category in allcats))
        allcats [category] = { id: category, parent: '', name: '', count: -1, expanded: false, catcount: 0, content: [] };

      allcats [category]['content'] = [];
      if (lines [start_line + 1] != '--')
        {
        for (var i = start_line + 1; i < lines.length && lines[i] != '--'; i++)
          {
          cat_count++;
          // log ('[c1] set line: ' + lines[i]);
          var fields = lines[i].split ('\t');
          if (! ((category + '-' + fields[0]) in allcats))
            allcats [category + '-' + fields[0]] = { id: fields[0], parent: category, name: fields[1], count: fields[2], expanded: false, catcount: 0, content: [] };
          /* and add a stunted one, without a parent or content */
          if (! (fields[0] in allcats))
            allcats [fields[0]] = { id: fields[0], name: fields[1], count: fields[2] };
          }
        start_line = i;
        }
      else
        start_line++;

      log ('category ' + category + ' count now: ' + cat_count);
      allcats [category]['catcount'] = cat_count;
log ('start line: ' + start_line);
      for (var j = start_line + 1; j < lines.length && lines[j] != '' && lines[j] != '--'; j++)
        {
        // log ('[c2] channel line: ' + lines[j]);
        var fields = lines[j].split ('\t');
        var channel = { 'id': fields[1], 'name': fields[2], 'desc': fields[3], 'thumb': fields[4], 'count': fields[5], 'type': fields[6], 'status': fields[7], 'nature': fields[8], 'extra': fields[9] };
        /* do not add podcasts to this category, but save their data in channel pool */
        if (parseInt (channel ['nature']) >= 3 && parseInt (channel ['nature']) <= 9 )
          {
          allcats [category]['content'][++count] = channel;
          // log ('====> content added to ' + category + ': ' + channel);
          }
        pool [channel['id']] = channel;
        }

      if (allcats [category]['count'] != count && category != '0' && count > 0)
        {
        log ('fixup to category "' + category + '" count: ' + count + ' vs ' + allcats [category]['count']);
        allcats [category]['count'] = count;
        }
      // allcats [category]['expanded'] = true;
      }
    else
      log ('expected category: ' + category + ', but found category: ' + lines[2]);
    }
  else
    notice_ok (thumbing, translations ['uncaught'] + " Code: " + fields[1], "");
  }

function redraw_store_cats()
  {
  log ('redraw_store_cats');
  var html = '<ul class="level1-list" id="category-list">';

  for (var id1 in allcats)
    {
    var cat1 = allcats [id1];
    if (cat1 ['parent'] == '0')
      {
      var classes = cat1 ['expanded'] ? "switch on" : "switch";
      var plural = ' ' + (cat1 ['count'] == 1 ? translations ['channel_lc'] : translations ['channels_lc']);
      html += '<li id="cat-' + cat1 ['parent'] + '-' + cat1 ['id'] + '">';
      html += '<p class="lp1"><span>' + cat1 ['name'] + '</span><span class="amount">(' + cat1 ['count'] + plural + ')</span>';
      html += '<span class="' + classes + '"><img src="' + nroot + 'icon_collapse.png" class="icon-collapse">'
      html += '<img src="' + nroot + 'icon_expand.png" class="icon-expand"></span></p>';
      if (cat1 ['expanded'])
        {
        html += '<ul class="level2-list">';
        for (var id2 in allcats)
          {
          var cat2 = allcats [id2];
          if (cat2 ['parent'] == cat1 ['id'])
            {
            var classes = cat2 ['expanded'] ? "switch on" : "switch";
            var plural = ' ' + (cat2 ['count'] == 1 ? translations ['channel_lc'] : translations ['channels_lc']);
            html += '<li id="flip-' + cat1 ['id'] + '-' + cat2 ['id'] + '"><p class="lp2">';
            html += '<span class="icon-set"><img src="' + nroot + 'icon_set_off.png" class="off">';
            html += '<img src="' + nroot + 'icon_set_on.png" class="on"></span><span>' + cat2 ['name'] + '</span>';
            html += '<span class="amount">(' + cat2 ['count'] + plural + ')</span>';
            html += '<img id="random-' + cat1 ['id'] + '-' + cat2 ['id'] + '" src="' + nroot + 'btn_flip_S.png" class="btn-flip"></p></li>';
            if (cat2 ['expanded'])
              html += channels_in_expanded_category (cat1 ['id'], cat2 ['id']);
            }
          }
        html += '</ul>';
        }
      html += '</li>';
      }
    }

  $("#category-content").html (html);

  $("#channel" + store_cat + '-' + store_index + '-' + store_channel).addClass ("on");

  $("#category-content .level1-list li").unbind();
  $("#category-content .level1-list li").click (function (event) { store_click (event, $(this).attr ("id")); });

  $("#category-content .level2-list li").unbind();
  $("#category-content .level2-list li").click (function (event) { store_click (event, $(this).attr ("id")); });

  $("#category-content .level3-list li").unbind();
  $("#category-content .level3-list li").click (function (event) { store_click (event, $(this).attr ("id")); });

  $("#category-content .btn-flip").unbind();
  $("#category-content .btn-flip").click (function (event) { store_click (event, $(this).attr ("id")); });

  /* redrawing loses this important effect, and this is a bit of an ugly hack */
  if (store_preview_type == 'category')
    run_previewing (currently_flipping_div);

  $("#channel-" + store_cat_parent + '-' + store_cat + '-' + store_index + '-' + store_channel + ' p').addClass("on")

  scrollbar ("#category-content", "#category-list", "#category-slider");
  }

function channels_in_expanded_category (parent_id, id)
  {
  var html = '';
  var index = 0;

  log ('=====> channels in expanded category: ' + id);

  if (id in allcats && allcats [id]['content'].length > 0)
    {
    html += '<ul class="level3-list">';
    for (var ch in allcats [id]['content'])
      {
      if (parseInt (ch) > 0)
        {
        var channel = allcats [id]['content'][ch];
        html += '<li id="channel-' + parent_id + '-' + id + '-' + ch + '-' + channel ['id'] + '">';
        html += '<p class="lp3">';
        html += '<span class="icon-ch"><img src="' + nroot + 'icon_ch_off.png" class="off">';
        html += '<img src="' + nroot + 'icon_ch_on.png" class="on"></span>';
        html += '<span>' + channel ['name'] + '</span>';
        html += '</p></li>';
        }
      }
    html += '</ul>';
    return html;
    }
  else
    return '';
  }

function store_click (ev, id)
  {
  log ("STORE CLICK: " + id);
  ev.stopPropagation();

  if (id.match (/^cat-/))
    {
    id = id.replace (/^cat-/, '');
    if (allcats [id]['expanded'])
      {
      allcats [id]['expanded'] = false;
      log ('collapsing: ' + id);
      redraw_store_cats();
      }
    else
      {
      allcats [id]['expanded'] = true;
      log ('expanding: ' + id);
      store_load_category (id);
      }
    }
  else if (id.match (/^flip-/))
    {
    id = id.replace (/^flip-/, '');
    // allcats [id]['expanded'] = false;
    // $("#flip-" + id).siblings().removeClass ("on");
    // $("#flip-" + id).addClass ("on");
    start_flipping (id);
    }
  else if (id.match (/^random-/))
    {
    id = id.replace (/^random-/, '');
    if (allcats [id]['expanded'])
      flip_random();
    else
      start_flipping (id);
    }
  else if (id.match (/^channel-/))
    {
    // fields[0] = "channel-"
    // fields[1] = parent cat id
    // fields[2] = cat id
    // fields[3] = cat index
    // fields[4] = channel id
    var fields = id.split ('-');
    store_direction = 'F';
    $(".lp3").removeClass ("on");
    $("#ch-previewing").remove();
    var lp3 = $(id).find (".lp3");
    $(lp3).addClass ("on").append('<span id="ch-previewing">Previewing</span>');
    var must_redraw = fields [1] != store_cat_parent || fields [2] != store_cat;
    store_cat_parent = fields [1];
    store_set_channel (fields[2], fields[3], fields[4]);
    if (must_redraw)
      {
      $(".lp2").removeClass ("on");
      currently_flipping_div= "#flip-" + store_cat_parent + '-' + store_cat + ' p';
      run_previewing (currently_flipping_div);
      $(lp3).addClass ("on");
      }
    /* ugliness for browser back button. catarrh already set */
    player_history ['category'][fields[4]] = 
          { 'fullcat': store_cat_parent + '-' + store_cat, 'shortcat': store_cat, 'which_channel': fields[3] };
    }
  }

/* if glowing effect is used, this is what is glowing (used by redraw) */
var currently_flipping_div;

var flip_cache = {};

function start_flipping (fully_qualified_cat)
  {
  log ('start flipping: ' + fully_qualified_cat);

  store_flipping = fully_qualified_cat;

  if (allcats [fully_qualified_cat]['expanded'])
    {
    allcats [fully_qualified_cat]['expanded'] = false;
    redraw_store_cats();
    return;
    }

  /* start at first channel in category */
  start_flipping_expand (fully_qualified_cat, 1);
  }

function start_flipping_expand (fully_qualified_cat, which_channel)
  {
  /* the fully qualified cat has a parent ID prepended */
  var fields = fully_qualified_cat.split ('-');
  var short_cat = fields [1];
  store_cat_parent = fields [0];

  var query = '/playerAPI/category?category=' + short_cat + '&' + 'lang=' + sphere + mso() + rx('');

  if (query in flip_cache)
    {
    start_flipping_inner (fully_qualified_cat, short_cat, flip_cache [query], which_channel);
    return;
    }

  $("#waiting-layer").show();
  var d = $.get (query, function (data)
    {
    $("#waiting-layer").hide();
    flip_cache [query] = data;
    start_flipping_inner (fully_qualified_cat, short_cat, data, which_channel);
    });
  }

function start_flipping_inner (fully_qualified_cat, short_cat, data, which_channel)
  {
  parse_category_data (short_cat, data);
  allcats [fully_qualified_cat]['expanded'] = true;
  redraw_store_cats();
  $(".lp2, .lp3").removeClass("on");
  currently_flipping_div= "#flip-" + fully_qualified_cat + " p";
  run_previewing (currently_flipping_div);
  store_preview_type = 'category';
  previous_store_index = -1;
  store_direction = 'F';
  store_dir_which_channel = which_channel;
  store_set_channel (short_cat, which_channel);

  /* ugliness to retain state for browser back button */
  var real_channel = store_channel_id_by_index (short_cat, which_channel);
  player_history ['category'][real_channel] = { 'fullcat': fully_qualified_cat, 'shortcat': short_cat, 'which_channel': which_channel };
  player_history ['catarrh'][fully_qualified_cat] = { 'shortcat': short_cat, 'data': data };
  }

function force_update_store_channel_bubble()
  {
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    var channel = pool [store_channel];
    update_store_channel_bubble (channel);
    }
  }

function update_store_channel_bubble (channel)
  {
  if (!channel)
    {
    log ('update store channel bubble: no channel!');
    return;
    }
  var desc;
  if ('desc' in channel)
    desc = channel ['desc'];
  if (!desc || desc == '')
    desc = translations ['nodesc'];
  $("#chbubble-list p:nth-child(1) span").html (desc);
  var pirc = programs_in_real_channel (channel ['id']);
  if (pirc > 0)
    channel ['count'] = pirc;
  var eps;
  if (channel ['count'] == 1)
    eps = '1 ' + translations ['episode_lc'];
  else
    eps = channel ['count'] + ' ' + translations ['episodes_lc'];
  $("#chbubble-meta span:nth-child(1)").html (eps);
  var age;
  if (n_program_line > 0 && program_line [1] in programgrid)
    {
    /* use the most recent of the first and last programs, works for all orderings */
    var tstamp = programgrid [program_line [1]]['timestamp'];
    var last_program_time = programgrid [program_line [n_program_line]]['timestamp'];
    if (last_program_time > tstamp)
      tstamp = last_program_time;
    age = ageof (tstamp, true);
    }
  else
    age = ageof (channel ['timestamp'], true);
  $("#chbubble-meta span:nth-child(3)").html (translations ['updated'] + ': ' + age);

  var curator = (channel ['curator'] && channel ['curator'] != '') ? channel ['curator'] : channel ['extra'];
  $("#chbubble-curator").html (translations ['curator'] + ": " + curator);

  if ($("#channel-bubble").css ("display") == 'block')
    scrollbar ("#chbubble-content", "#chbubble-list", "#chbubble-slider");
  }

function store_channel_id_by_index (cat, index)
  {
  if (store_preview_type == 'category')
    {
    return allcats [cat]['content'][index]['id'];
    }
  else if (store_preview_type == 'recommended')
    {
    for (var r in recommends)
      if (recommends [r]['id'] == cat)
        {
        if ('content' in recommends[r] && index in recommends [r]['content'])
          return recommends [r]['content'][index]['id'];
        }
    }
  else if (store_preview_type == 'share')
    {
    return sharez [1]['content'][index]['id'];
    }
  else if (store_preview_type == 'search')
    {
    return searchresults_stack [index]['id'];
    }
  }

function store_set_channel (cat, index, id, program_index)
  {
  log ('store set channel, cat: ' + cat + ', index: ' + index + ', id supposed to be: ' + id);

  stop_special_audio();

  var id = store_channel_id_by_index (cat, index);
  log ('store set channel, id: ' + id);

  store_cat = cat;
  store_channel = id;
  store_index = index;

  if (program_index)
    program_cursor = program_index;
  else
    program_cursor = 1;

  var thumbnails_in_row = 9;
  if (thumbing == 'store' || thumbing == 'store-wait')
    thumbnails_in_row = 5;
  program_first = 1 + Math.floor ((program_cursor - 1) / thumbnails_in_row) * thumbnails_in_row;

  if (program_cursor < 1)
    alert ('program cursor is: ' + program_cursor);

  var channel = pool [id];
  log ('store_set_channel ' + id + ': nature: ' + channel ['nature']);

  if (channel ['nature'] == undefined)
    {
    alert ("channel " + channel ['id'] + " has no type!");
    return;
    }

  if (store_tab == 'recommended')
    {
    var n_channels = channels_in_set_or_category (store_preview_type, store_cat);
    var plural = " " + (n_channels == 1 ? translations ['channel_lc'] : translations ['channels_lc']);
    /* update recommended set thumbnail to this channel */
    $("#rec-" + store_recommend_index + " .thumbnail").attr ("src", channel ['thumb']);
    $("#rec-" + store_recommend_index + " .channel-number span"). html (store_index + ' of ' + n_channels + ' ' + plural); /* XYZ */
    store_recommend_leftoff [store_cat] = index;
    }
  else if (store_tab == 'category')
    {
    $("#category-list .lp3").removeClass ("on");
    $("#channel-" + store_cat_parent + '-' + cat + '-' + index + '-' + id + ' p').addClass ("on");
    store_dir_which_channel = index;
    }

  update_store_channel_bubble (channel);
  /* reset the bubble to the top */
  $("#chbubble-slider .slider-vertical").slider("value", "100");

  if (programs_in_real_channel (channel ['id']) < 1)
    {
    if (channel ['nature'] == '3' || channel ['nature'] == '4' || channel ['nature'] == '5')
      {
      fetch_youtube_or_facebook (channel ['id'], "store_set_channel_inner()");
      return;
      }

    var cmd = "/playerAPI/programInfo?channel=" + channel ['id'] + mso() + rx();
    var d = $.get (cmd, function (data)
      {
      parse_program_data (data);
      store_set_channel_inner();
      });
    }
  else
    store_set_channel_inner();
  }

function store_set_channel_inner()
  {
  var channel = pool [store_channel];

  program_lineup (channel ['id']);
  update_store_channel_bubble (channel);

  if (store_preview_type == 'category')
    {
    var category = allcats [store_cat];
    if (n_program_line != category ['content'][store_index]['count'])
      {
      /* the count is wrong in the right panel, redraw updated count in place */
      category ['content'][store_index]['count'] = n_program_line;
      redraw_store_cats();
      }
    }

  redraw_store_preview();

  if (parseInt (channel ['nature']) >= 3 && parseInt (channel ['nature']) <= 9)
    store_play_yt()

  update_store_channel_bubble (channel);
  }

function redraw_store_preview()
  {
  }

var flipr_bubble_timex;

function flip_preview_hover_in()
  {
  if (!$(this).hasClass ("disable"))
    {
    var fbw = $(this).width();
    var fbh = $("#flipr-bubble").height() - $(this).height() / 2;
    var gap= $("#flipr-bubble-tip").width() / 2;
    var btnPos = $(this).offset();
    var fbl = btnPos.left + fbw - gap;
    var fbt = btnPos.top + fbh;
    flipr_bubble_timex = setTimeout (function()
      { $("#flipr-bubble").fadeIn().css ({ left: fbl, top: fbt }); }, 50); }
    }

function flip_preview_hover_out()
  {
  clearTimeout (flipr_bubble_timex);
  $("#flipr-bubble").fadeOut();
  }

var top_notch;
var notch_height;
var n_notches;
var notch_offset;

function show_flip_bubble()
  {
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    var n_channels = channels_in_set_or_category (store_preview_type, store_cat);

    $("#flip-ch-index li").removeClass ("on");
    $("#flip-ch-index li:nth-child(" + store_index + ")").addClass ("on")
    setbubbleclick_highlight();
    $("#chOrder").html (store_index);
    $("#chNum").html (n_channels);

    $("#flip-bubble").show();
    flip_bubble_pos();
    $("#flip-bubble").draggable
      ({ containment: "#flip-bubble-constrain", axis: "y", drag: flip_drag, stop: flip_stop });

    calculate_flip_bubble_offsets();
    }
  else
    $("#flip-bubble").hide();
  }

function calculate_flip_bubble_offsets()
  {
  if (!$("#flip-ch-index li") || !$("#flip-ch-index li").eq(0))
    return;

  var offset = $("#flip-ch-index li").eq(0).offset();
  if (!offset)
    return;

  top_notch = $("#flip-ch-index li").eq(0).offset().top;
  notch_height = parseInt ($("#flip-ch-index li").eq(0).css("height"));
  n_notches = channels_in_set_or_category (store_preview_type, store_cat)
  // notch_offset = parseInt ( $("#flip-ch-index li").eq(0).offset().top - $("#flip-bubble-constrain").offset().top );
  // notch_offset = parseInt ($("#flip-bubble-constrain").offset().top);
  var ofs = parseInt ( $("#flip-ch-index li").eq(0).offset().top - $("#flip-bubble-constrain").offset().top );
  // notch_offset = parseInt ( $("#flip-ch-index li").eq(0).offset().top - (3 * ofs));
  notch_offset = ofs;
  }

function flip_drag (event, ui)
  {
log ('pos: ' + ui.offset.top);
  // var possible_ch = Math.floor (    ((parseInt (ui.offset.top) - notch_offset) / notch_height)         );

  var position_of_pointer = parseInt (ui.offset.top + ($("#flip-bubble").height() / 2));
  var possible_ch = Math.floor ((position_of_pointer - $("#flip-ch-index").offset().top) / notch_height) + 1;

log ('pos: ' + ui.offset.top + ' pointer at: ' + position_of_pointer + ' possible ch: ' + possible_ch);
  //// var possible_ch = Math.floor (( parseInt (ui.offset.top) - notch_offset - ($("#flip-bubble").height() ) ) / notch_height );
  if (possible_ch > n_notches)
    possible_ch = n_notches;
  if (possible_ch < 1)
    possible_ch = 1;
  log ('pos: ' + ui.offset.top + ' possible at: ' + possible_ch);
  $("#chOrder").html (possible_ch);
  $("#flip-ch-index li").removeClass ("on");
  $("#flip-ch-index li:nth-child(" + possible_ch + ")").addClass ("on")
  var id = store_channel_id_by_index (store_cat, possible_ch);
  var channel = pool [id];
  set_hash ("#!" + channel ['id']);
  $("#index-ch-title").html (channel ['name']);
  /* if the bubbleclick is active, make this drag there live as well */
  store_index = possible_ch;
  // redraw_setbubbleclick();
  setbubbleclick_highlight(); // XYZ
  }

function flip_stop (event, ui)
  {
  // var possible_ch = Math.floor (    ((parseInt (ui.offset.top) - notch_offset) / notch_height)         );
  var position_of_pointer = parseInt (ui.offset.top + ($("#flip-bubble").height() / 2));
  var possible_ch = Math.floor ((position_of_pointer - $("#flip-ch-index").offset().top) / notch_height) + 1;

  if (possible_ch > n_notches)
    possible_ch = n_notches;
  if (possible_ch < 1)
    possible_ch = 1;
  log ('stop at: ' + ui.offset.top + ' possible at: ' + possible_ch);

  store_index = possible_ch;
  store_direction = '!';
  store_set_channel (store_cat, possible_ch);
  $("#chOrder").html (possible_ch);
  }

var add_bubble_timex;
var add_bubble_failsafe_timex;

function add_button_hover_in()
  {
  if (!$(this).hasClass("disable"))
    {
    var bw = $(this).width();
    // var bh = $(this).height();
    var bh = ( $("#add-bubble").height() - $(this).height() ) / 2;
    var gap = $("#add-bubble-tip").width()/2;
    var btnPos = $(this).offset();
    var bl = btnPos.left + bw + gap;
    var bt = btnPos.top - bh;
    // add_bubble_timex = setTimeout (function() { $("#add-bubble").fadeIn().css ({ left: bl, top: bt }); }, 100);
    $("#add-bubble").fadeIn().css ({ left: bl, top: bt });
    add_bubble_failsafe_timex = setTimeout (function() { $("#add-bubble").fadeOut; }, 15000);
    }
  }

function add_button_hover_out()
  {
  clearTimeout (add_bubble_timex);
  $("#add-bubble").fadeOut();
  }

function redraw_store_add_button()
  {
  $("#btn-add-ch-L").unbind();
  $("#btn-add-ch-L").hover (add_button_hover_in, add_button_hover_out);
  $("#add-bubble").fadeOut();
  if (first_position_with_this_id (store_channel) > 0)
    {
    $("#btn-add-ch-L").addClass ("disable");
    $("#btn-add-ch-L .btn-text span").html (translations ['subscribed']);
    $("#btn-add-ch-L").bind ('click', function () { browse_play (store_channel); });
    }
  else
    {
    $("#btn-add-ch-L").removeClass ("disable");
    $("#btn-add-ch-L .btn-text span").html (translations ['addthischannel']);
    $("#btn-add-ch-L").bind ('click', function () { browse_accept (store_channel); });
    }
  run_glowing();
  }

function store_ep_hover_in()
  {
  var id = $(this).attr ("id");
  $("#preview-ep-list li").removeClass ("hover");
  $(this).addClass ("hover");
  id = id.replace (/^sp-/, '');
  var program = programgrid [program_line [id]];
  // $("#preview-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));
  $("#meta-ep-title").html (fixed_up_program_name (program ['name']));
  // $("#preview-ep-meta .amount").html (id + '/' + n_program_line);
  }

function store_ep_hover_out()
  {
  var id = $(this).attr ("id");
  $(this).removeClass ("hover");
  if (program_cursor) 
    {
    var program = programgrid [program_line [program_cursor]];
    // $("#preview-ep-meta .ep-title").html (fixed_up_program_name (program ['name']));
    $("#meta-ep-title").html (fixed_up_program_name (program ['name']));
    // $("#preview-ep-meta .amount").html (program_cursor + '/' + n_program_line);
    }
  }

var ep_timex;

function show_ep_title()
  {
  clearTimeout (ep_timex);
  if (!$("#ep-switcher").hasClass ("on"))
    {
    // $("#preview-ep-meta").fadeIn (function()
    //   { ep_timex = setTimeout (function() { $("#preview-ep-meta").fadeOut (400); }, 5000) });
    }
  // $("#preview-ep-meta").show(); /* replacement */
  }

function fade_ep_title()
  {
  // if (!$("#ep-switcher").hasClass ("on"))
  //   $("#preview-ep-meta").fadeOut (400);
  // $("#preview-ep-meta").show();
  }

var flip_timex;

function OLD_show_flip_bubble()
  {
  flip_bubble_pos();
  var $bubble = $("#flip-bubble");
  // clearTimeout (flip_timex);
  // $("#flip-bubble").fadeIn(function()
  //   { flip_timex = setTimeout (function() { $("#flip-bubble").fadeOut (400); }, 7000) });
  // $("#flip-bubble").fadeIn();
  show_flip_bubble();
  }

function flip_bubble_pos()
  {
  if ($("#flip-bubble").css ('display') == 'block')
    {
    var offset = $("#flip-ch-index li.on").offset();
    var fh = $("#flip-ch-index li.on").height();
    var l = offset.left - $("#flip-bubble").width() - 2;
    var t = offset.top + fh / 2 - $("#flip-bubble").height() / 2;
    $("#flip-bubble").css ({ left: l, top: t });
    }
  }

function ep_switcher()
  {
  if ($("#ep-switcher").hasClass ("on"))
    {
    $("#ep-switcher").removeClass ("on");
    // $("#preview-ep, #preview-ep-meta").hide();
    $("#preview-ep").hide();
    }
  else
    {
    clearTimeout (ep_timex);
    $("#ep-switcher").addClass ("on");
    $("#preview-ep, #preview-ep-meta").show();
    }
  }

function flip_random()
  {
  log ('flip random');

  store_back_channel = store_index;

  store_direction = '!';
  var n_channels = channels_in_set_or_category (store_preview_type, store_cat);

  /* if for some reason the next random channel is bad */
  if (!store_next_random_channel || store_next_random_channel > n_channels || store_next_random_channel == store_index)
    store_next_random_channel = select_a_random_channel_index();

  /* remove this if using actual random */
  store_next_random_channel = select_a_random_channel_index();

  store_set_channel (store_cat, store_next_random_channel);
  }

function flip_back()
  {
  log ('flip back');
  $("#hint-layer").hide();
  if (store_back_channel)
    {
    var temp = store_index;
    store_set_channel (store_cat, store_back_channel);
    store_back_channel = temp;
    }
  }

function flip_next()
  {
  log ('flip next');
  $("#hint-layer").hide();
  store_direction = 'F';
  store_back_channel = store_index;
  if (store_preview_type == 'share')
    {
    recommended (1);
    return;
    }
  var n_channels = channels_in_set_or_category (store_preview_type, store_cat);
  if (store_index < n_channels)
    store_set_channel (store_cat, parseInt (store_index) + 1);
  else
    store_set_channel (store_cat, 1);
  }

function flip_prev()
  {
  log ('flip prev');
  $("#hint-layer").hide();
  store_direction = 'R';
  store_back_channel = store_index;
  if (store_preview_type == 'share')
    {
    recommended (1);
    return;
    }
  var n_channels = channels_in_set_or_category (store_preview_type, store_cat);
  if (store_index > 1)
    store_set_channel (store_cat, parseInt (store_index) - 1);
  else
    store_set_channel (store_cat, n_channels);
  }

function store_ep_click (id)
  {
  id = id.replace (/^sp-/, '');
  log ('store ep click: ' + id);
  program_cursor = id;
  redraw_store_preview();
  store_play_yt();
  }

function store_arrow_left()
  {
  log ('store arrow left');
  if (program_first != 1)
    {
    program_first -= 3;
    if (program_first < 1)
      program_first = 1;
    if (program_cursor < program_first || program_cursor > program_first + 2)
      {
      program_cursor = program_first;
      store_play_yt();
      }
    redraw_store_preview();
    }
  }

function store_arrow_right()
  {
  log ('store arrow right');
  if (program_first + 3 <= n_program_line)
    {
    program_first += 3;
    if (program_cursor < program_first || program_cursor > program_first + 2)
      {
      program_cursor = program_first;
      store_play_yt();
      }
    redraw_store_preview();
    }
  }

function store_play_or_pause()
  {
  if (! ($("#btn-preview-play").hasClass ("on")))
    {
    log ('play/pause: playing');
    $("#btn-preview-play").addClass ("on");
    general_resume();
    }
  else
    {
    log ('play/pause: pausing');
    $("#btn-preview-play").removeClass ("on");
    general_pause();
    }
  }

function channels_in_set_or_category (tab, cat)
  {
  if (tab == 'category')
    {
    return allcats [cat]['count'];
    }
  else if (tab == 'recommended')
    {
    for (var r in recommends)
      {
      if (recommends [r]['id'] == cat)
        return recommends [r]['count'];
      }
    }
  else if (tab == 'share')
    {
    log ('channels in set or category: tab=' + tab + ', cat=' + cat);
    return sharez [1]['count'];
    }
  else if (tab == 'search')
    {
    var count = 0;
    for (var r in searchresults_stack)
      count++;
    return count;
    }
  }

var searchresults_stack = [];
var searchshelf_stack = []; /* curator shelves only */
var saved_store_preview_type;
var sblocks;
var curator_search_cursor;
var search_curator_page = 1;
var curator_count = 0; 
var last_search_term;

function perform_search (text)
  {
  log ('perform search: ' + text);
  last_search_term = text;

  $("#search-layer").removeClass ("lost").removeClass ("found");
  $("#search-layer").addClass ("found");

  fresh_layer ("search");
  thumbing = 'search';
  set_hash ("#!search");

  $("#no-result").hide();
  searchresults_stack = [];
  searchshelf_stack = [];

  if (store_preview_type != 'search')
    saved_store_preview_type = store_preview_type;

  $("#curator-result, #channel-result").html ('');
  $("#curator-found, #channel-found").html ('');
  // $("#search-term").html (text);
  if (language == 'zh')
    $("#result-summary ul li.term").html ('<span id="search-term">'  + text + '</span> ' + translations ['resultsfor']);
  else
    $("#result-summary ul li.term").html (translations ['resultsfor'] + ': <span id="search-term">' + text + '</span>');
  $("#result-summary").hide();

  /* blocks:
   *  0 - result code
   *  1 - number of results
   *  2 - curator results (curator format)
   *  3 - additions to channel pool from block 2 (channelLineup format)
   *  4 - channel results (channelLineup format)
   *  5 - suggestions
   */

  var d = $.get ("/playerAPI/search?text=" + encodeURIComponent (text) + rx(), function (data)
     {
     var html = '';
     log ('result: ' + data);
     var blocks = data.split (/^--$/m);
sblocks = blocks;

     // curator schema:
     // curator id, curator name, curator description, curator image url, curator profile url, channel count, channel subscription count, follower count

     /* add any additional channels referenced in block 2, to pool */
     var lines = blocks[3].split ('\n');
     for (var i = 0; i < lines.length; i++)
       {
       if (lines [i] != '')
         {
         var channel = line_to_channel (lines[i]);
         pool [channel ['id']] = channel;
         }
       }

     curator_count = 0; 
     var shelf_count = 0;
     curator_search_cursor = 1;

     $("#curator-result").css ("margin-left", "0px");
     $("#curator-result").css ("width", "200%");

     var html = '';
     var lines = blocks[2].split ('\n');
     for (var i = 0; i < lines.length; i++)
       {
       if (lines [i] != '')
         {
         curator_count++;
         var fields = lines [i].split ('\t');
         var cplural = (fields [5] == 1) ? translations ['channel'] : translations ['channels'];
         log ('curator result line: ' + fields);
         html += '<li>';
         html += '<div id="search-curator-' + curator_count + '" class="curator-result-item" data-curator-id="' + fields[0] + '">';
         if (curator_count > 1)
           html += '<p class="curator-divider"></p>';
         html += '<p class="searched-curator-photo"><img src="' + fields[3] + '"></p>'
         html += '<p class="curator-name">' + fields[1] + '</p>';
         html += '<p class="channel-popularity"><span class="viewer-number">' + fields[6];
         html += '</span>' + translations ['views'] + '<span class="divider">|</span><span class="follower-number">' + fields[7] + '</span>' + translations ['followers'] + '</p>';
         html += '<p class="channel-owned"><span class="channel-number">' + fields[5] + '</span>' + cplural + '</p>';
         html += '</div>';
         if (fields[8])
           {
           shelf_count++;
           var channel = pool [fields[8]];
           var is_empty_favorites = channel ['id'].match (/^f-/);
           var ago = ageof (channel ['timestamp'], true);
           searchshelf_stack [shelf_count] = channel;
           html += '<div id="search-shelf-' + shelf_count + '" class="curator-shelf-item" data-channel="' + channel ['id'] + '">';
           html += '<p class="pl-title-line"><span class="pl-title">' + channel ['name'] + '</span></p>';
           html += '<p class="pl-curator-line"><span>' + translations ['curatorby'] + '</span><span class="pl-curator">' + channel ['curatorname'] + '</span></p>';
           if (!is_empty_favorites)
             html += '<p class="icon-pl"></p>';
           html += '<img src="' + channel ['thumb1'] + '" class="thumb1">';
           html += '<img src="' + channel ['thumb2'] + '" class="thumb2">';
           html += '<img src="' + channel ['thumb3'] + '" class="thumb3">';
           if (!is_empty_favorites)
             html += '<p class="pl-sign"><span>' + ago + '</span></p>';
           html += '</div>';
           }
         html += '</li>';
         }
       }
     $("#curator-result").html (html);

     if (curator_count > 0)
       {
       $("#curator-result-constrain").show()
       $("#curator-result-clip").css ("visibility", "visible")
       }
     else
       {
       $("#curator-result-constrain").hide()
       $("#curator-result-clip").css ("visibility", "hidden")
       }

     /* main.css has this set to "200%", but results might be very wide */
     var single_item_width = parseInt ($("#curator-result li").eq(0).css ("width"));
     $("#curator-result").css ("width", single_item_width * (4 + curator_count));

     // $("#curator-found").html (curator_count);
     var cu_plural = (curator_count == 1) ? translations ['ncurator'] : translations ['ncurators'];
     $("#curator-result-clip .clip-center").html (curator_count + ' ' + cu_plural);

     var channel_count = 0;
     var html = '';
     var lines = blocks[4].split ('\n');
     for (var i = 0; i < lines.length; i++)
       {
       if (lines [i] != '')
         {
         channel_count++;
         var fields = lines [i].split ('\t');
         var channel = line_to_channel (lines[i]);
         pool [channel ['id']] = channel;
         searchresults_stack [channel_count] = channel;
         var ago = ageof (channel ['timestamp'], true);
         var plural = (channel ['count'] == 1) ? translations ['episode'] : translations ['episodes'];
         var curator = channel ['curator'];
         curator = curator.replace (/^YouTube\s+user:\s+/, '');
         html += '<li id="search-channel-' + channel_count + '">';
         html += '<p class="btn-follow">' + translations ['follow'] + '</p>';
         html += '<p class="icon-pl"></p>';
         html += '<img src="' + channel ['thumb1'] + '" class="thumb1">';
         html += '<img src="' + channel ['thumb2'] + '" class="thumb2">';
         html += '<img src="' + channel ['thumb3'] + '" class="thumb3">';
         html += '<p class="channel-title">' + channel ['name'] + '</p>';
         html += '<p class="channel-meta">';
         html += '<span>' + channel ['count'] + '</span>' + plural + '<br>';
         html += '<span>' + ago + '</span><br>';
         html += '<span>' + channel ['subscriptions'] + '</span>' + translations ['followers'] + '<br>';
         html += '<span>' + channel ['viewcount'] + '</span>' + translations ['views'];
         html += '</p>';
         html += '<p class="curator-name">' + translations ['curatorby'] + '<span>' + channel ['curatorname'] + '</span></p>';
         html += '</li>';
         }
       }

     $("#channel-result").html (html);

     var cu_plural = (curator_count == 1) ? translations ['ncurator'] : translations ['ncurators'];
     var ch_plural = (channel_count == 1) ? translations ['nchannel'] : translations ['nchannels'];
     $("#result-summary .numbers").html ('<span id="curator-found">' + curator_count + '</span>' + cu_plural + '<span class="divider">|</span><span id="channel-found">' + channel_count + '</span>' + ch_plural);
     $("#channel-result-clip .clip-center").html (channel_count + ' ' + ch_plural);

     $("#results-constrain").css ({ height: $(window).height() - $("#results-constrain").offset().top });
     try { $("#results-slider .slider-vertical").slider ("destroy"); } catch (error) {};
     $("#results-slider").css ({ height: $(window).height() - $("#results-constrain").offset().top - 20 });
     scrollbar ("#results-constrain", "#results-list", "#results-slider");
     try { $("#results-slider .slider-vertical").slider ("value", "100"); } catch (error) {};

     var plural = (channel_count == 1) ? translations ['resfound1'] : translations ['resfound2'];
     $("#result-head").html ('<span class="amount">' + channel_count + '</span><span>' + plural + '</span>');

     if (channel_count == 0)
       $("#no-result").show();
     else
       $("#no-result").hide();

     $("#btn-search-close").unbind();
     $("#btn-search-close").click (function() { $("#search-layer").hide(); store_preview_type = saved_store_preview_type; });
     $("#search-list li").click (function() { search_click ($(this).attr ("id")); });

     $("#result-summary").show();

     $("#curator-result li .curator-result-item").unbind();
     $("#curator-result li .curator-result-item").click (function()
       {
       var id = $(this).attr("id");
       log ('search curator click: ' + id);
       var curator_id = $("#" + id).attr ("data-curator-id");
       curation (curator_id);
       });

     $("#curator-result li .curator-shelf-item").unbind();
     $("#curator-result li .curator-shelf-item").click (function()
       {
       var id = $(this).attr("id").replace (/^search-shelf-/, '');
       var channel_id = $(this).attr ("data-channel");
       var channel = pool [channel_id];
       log ('search curator shelf click: ' + id);
       if (channel ['id'].match (/^f-/))
         {
         // notice_ok (thumbing, "This channel is an empty Favorites channel that cannot be played. If you think it won't be empty in the future, you can still Follow to it by visiting this curator's page and clicking Follow button on the channel box which contains this channel.", '');
         return;
         }
       player_stack = searchshelf_stack;
       player ('search', parseInt (id));
       });

     $("#curator-result li .curator-shelf-item .pl-curator").unbind();
     $("#curator-result li .curator-shelf-item .pl-curator").click (function (event)
       {
       event.stopPropagation();
       var id = $(this).parent().parent().attr("id");
       log ('search curator shelf curatorname click: ' + id);
       var channel_id = $("#" + id).attr ("data-channel");
       var channel = pool [channel_id];
       curation (channel ['curatorid']);
       });

     $("#channel-result li").unbind();
     $("#channel-result li").click (function() { search_channel_click ($(this).attr("id")); });

     // MediaQuery
     // $("#curator-result-constrain").width()
     // 1249 (5 results)
     // $("#curator-result-constrain").width()
     // 999 (4 results)

     redraw_curator_pagination();
     header();
     });

  /* this can be done concurrently with the search */
  track_search (text);

  stop_all_players();
  }

function set_search_curator_page (n)
  {
  n = parseInt (n);
  log ('search curator page was: ' + search_curator_page + ', now: ' + n);
  search_curator_page = n;
  var nitems = ( $("#curator-result-constrain").width() < 1000 ) ? 4 : 5;
  curator_search_cursor = 1 + nitems * (n - 1);
  var margin = -250 * (curator_search_cursor - 1);
  log ('new margin: ' + margin);
  $("#curator-result").animate ({ "margin-left": margin + "px" }, 1000);
  setTimeout ('$("#curator-result").css ("margin-left","' + margin + 'px")', 1000);
  redraw_curator_pagination();
  }

function redraw_curator_pagination()
  {
  var html = '';
  var nitems = ( $("#curator-result-constrain").width() < 1000 ) ? 4 : 5;
  if (curator_count > nitems)
    {
    html += '<li class="btn-pagination" id="btn-prev-curator">' + translations ['prev'] + '</li>';
    var npages = parseInt ((curator_count + nitems - 1) / nitems);
    for (var i = 1; i <= npages; i++)
      html += '<li class="page-number" id="curator-result-page-btn-' + i + '">' + i + '</li>';
    html += '<li class="btn-pagination" id="btn-next-curator">' + translations ['next'] + '</li>';
    }
  $("#curator-pagination").html (html);

  $("#curator-pagination .page-number").removeClass ("on");
  $("#curator-result-page-btn-" + search_curator_page).addClass ("on")

  $("#btn-prev-curator").unbind();
  $("#btn-prev-curator").click (function()
    {
    log ('curator prev');
    var nitems = ( $("#curator-result-constrain").width() < 1000 ) ? 4 : 5;
    if (search_curator_page > 1)
      set_search_curator_page (search_curator_page - 1);
    });

  $("#btn-next-curator").unbind();
  $("#btn-next-curator").click (function()
    {
    log ('curator next');
    var nitems = ( $("#curator-result-constrain").width() < 1000 ) ? 4 : 5;
    var npages = parseInt ((curator_count + nitems - 1) / nitems);
    if (search_curator_page + 1 <= npages)
      set_search_curator_page (search_curator_page + 1);
    });

  $("#curator-pagination .page-number").unbind();
  $("#curator-pagination .page-number").click (function()
    {
    var id = $(this).attr ("id");
    id = id.replace (/^curator-result-page-btn-/, '');
    log ('curator page ' + id);
    set_search_curator_page (id);
    });
  }

function search_channel_click (id)
  {
  log ('search channel click: ' + id);
  id = id.replace (/^search-channel-/, '');
  player_stack = searchresults_stack;
  player ('search', parseInt (id));
  }

function search_click (id)
  {
  id = id.replace (/^search-/, '');
  log ('search click: ' + id);
  store_preview_type = 'search'; /* should not be necessary */
  store_direction = 'F';
  store_set_channel ('search', id, searchresults_stack [id]['id']);
  $("#search-list li").removeClass ("on");
  $("#search-" + id).addClass ("on");
  }

function fixed_up_program_name (name)
  {
  if (name)
    {
    if (name.match (/YouTube user:? /i))
      name = name.replace (/YouTube user:? /i, '')
    if (name.match (/ channel$/i))
      name = name.replace (/ channel$/i, '')
    }
  else
    name = '';
  return name;
  }

var slider_sliding = false;
var stopevent;
var stopui;
function slider_stop (event, ui)
  {
  stopevent = event;
  stopui = ui;
  log ('slider stop, value: ' + $("#progress-bar").slider ("value") + ' event: ' + event);
  yt_mini_seek_by_percentage ($("#progress-bar").slider ("value"));
  }

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
      audio_stream [i].stop();
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

function stop_all_audio_tracks()
  {
  for (var i = 1; i <= 3; i++)
    if (audio_stream [i])
      audio_stream [i].stop();
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

function pause_and_mute_all_video()
  {
  for (var i = 1; i <= 3; i++)
    {
    try { ytmini[i].mute(); } catch (error) {};
    try { ytmini[i].pauseVideo(); } catch (error) {};
    }
  }

var tc_timex;
var tc_duration;
var tc_start;

function play_titlecard (which, titlecard)
  {
  var titlecard_is_playing = current_episode;

  log ('play ' + which + ' (duration ' + titlecard ['duration'] + ') titlecard: ' + titlecard ['message']);

  tc_timex = setInterval ("titlecard_ticker()", 20);
  tc_duration = titlecard ['duration'];
  tc_start = new Date().getTime();

  $("#tc").show();
  $('#tc').titlecard ('cancel');

  progress_and_volume_bar();

  $('#tc').titlecard
    ({
    text: 'message' in titlecard ? titlecard ['message'] : '',
    align: 'align' in titlecard ? titlecard ['align'] : 'center',
    effect: 'effect' in titlecard ? titlecard ['effect'] : 'fade',
    duration: 'duration' in titlecard ? parseInt (titlecard ['duration']) : 7,
    fontSize: 'size' in titlecard ? parseInt (titlecard ['size']) : 20,
    fontColor: 'color' in titlecard ? titlecard ['color'] : '#ffffff',
    fontWeight: 'weight' in titlecard ? titlecard ['fontweight'] : 'normal',
    fontStyle: 'style' in titlecard ? titlecard ['style'] : 'normal',
    backgroundColor: 'bgcolor' in titlecard ? titlecard ['bgcolor'] : '#000000',
    backgroundImage: 'bgimage' in titlecard ? titlecard ['bgimage'] : ''
    },
  function()
    {
    clearInterval (tc_timex);
    log ('titlecard callback!');
    if (current_episode != titlecard_is_playing)
      {
      log ("titlecard: we have moved on :: current_episode: " + current_episode + ", was playing: " + titlecard_is_playing);
      return;
      }
    else
      {
      /* titlecard has ended, go on to next segment */
      flip_next_episode();
      /* store_play_yt(); */
      }
    });

  pause_and_mute_everything();
  }

function titlecard_ticker()
  {
  var segment = get_current_segment();
  var program = get_current_program();

  if (segment ['type'] == 'begin-title' || segment ['type'] == 'end-title')
    {
    var diff = (new Date().getTime() - tc_start) / 1000;
    var pct = (segment ['starting'] + diff) * 100 / program ['total-duration'];;
    // log ('titlecard tick! duration=' + tc_duration + ' diff: ' + diff + ' pct: ' + pct);
    if (! $("#progress-bar").hasClass ("progdrag"))
      $("#btn-knob").css ("left", pct + '%');
    $("#played").css ("width", pct + '%');
    }
  else
    clearInterval (tc_timex);
  }

function any_lingering_segments()
  {
  var pid = program_line [program_cursor];
  var program = programgrid [pid];
  var segments = program ['segments'];
  if ((current_segment + 1) in segments)
    {
    log ('more segments remain (now at ' + current_segment + ')');
    store_play_yt();
    return true;
    }
  log ('no lingering segments');
  return false;
  }

/* needed to track for title-begin and title-end */
var current_episode;
var current_segment = 0;

function store_play_yt (starting_at)
  {
  /* in all cases, stop titlecard */
  $("#tc").hide();
  $('#tc').titlecard ('cancel');

  init_progress_bar();

  if (! (program_cursor in program_line))
    {
    log ('store_play_yt: cursor not in program line! program cursor is: ' + program_cursor);
    current_episode = -1;
    current_episode_phase = 0;
    current_segment = 0;
    return;
    }

  var pid = program_line [program_cursor];

  if (pid != current_episode)
    {
    current_episode = pid;
    current_episode_phase = 0;
    current_segment = 0;
    }

  current_segment++;

  var program = programgrid [pid];
  var segments = program ['segments'];

  /* we just updated the segment cursor, so update metadata */
  GNU_player_metainfo (player_real_channel);

  if (current_segment in segments)
    {
    if (starting_at) 
      log ('-- play -- episode: ' + pid + ', segment: ' + current_segment + ', starting at: ' + starting_at);
    else
      log ('-- play -- episode: ' + pid + ', segment: ' + current_segment);
    }
  else
    {
    log ('-- play -- episode: ' + pid + ' BUT OUT OF SEGMENTS! flipping next episode');
    flip_next_episode();
    return;
    }

  var segment = segments [current_segment];
  var clip = segment ['clip'];

  if (segment ['type'] == 'begin-title')
    {
    play_titlecard ('begin', segment ['titlecard']);
    return;
    }
  else if (segment ['type'] == 'end-title')
    {
    play_titlecard ('end', segment ['titlecard']);
    return;
    }

  if (clip ['type'] == '3')
    {
    play_this_audio (pid, current_segment, starting_at);
    preload_youtube_videos();
    /* if above line is turned off: */
    /* store_next_random_channel = select_a_random_channel_index(); */
    }
  else
    {
    stop_special_audio();
    play_this_youtube_video (pid, current_segment, starting_at);
    }

  /* be safe? */
  show_flip_bubble();
  }

var awt_timex;

function progdrag_end (why)
  {
  var pct = 100 * parseInt ($("#btn-knob").css ("left")) / $("#progress-bar").width();
  log ('progdrag ' + why + ': knob is at: ' + $("#btn-knob").css ("left") + ' = ' + pct + '%');

  var program = programgrid [program_line [program_cursor]];
  var seconds_offset = program ['total-duration'] * pct / 100;

  var which_segment = 1;

  for (var s in program ['segments'])
    {
    if (s > 1)
      {
      var segment = program ['segments'][s];
      var left = parseInt (segment ['starting']);
      var right = parseInt (segment ['starting']) + parseInt (segment ['duration']);
      if (seconds_offset >= left && seconds_offset < right)
        {
log ("found " + seconds_offset + " at: " + s + ", left: " + left + ", right: " + right);
        which_segment = s;
        break;
        }
      }
    }

  var segment =  program ['segments'][which_segment];

  log (">>>>> WHICH SEGMENT: " + which_segment + ", offset: " + seconds_offset);

  var pct_within = (seconds_offset - segment ['starting']) * 100 / segment ['duration'];

  log (">>>>> PERCENT WITHIN: " + pct_within);

  if (which_segment != current_segment)
    {
    current_segment = which_segment - 1;
    var starting_at = seconds_offset - segment ['starting'];
    store_play_yt (starting_at);
    }
  else
    {
    yt_mini_seek_by_percentage (pct);
    }
  }

function voldrag_end (why)
  {
  var distance = $("#btn-dragger").offset().left - $("#volume-bar").offset().left;
  /* because of CSS layout fluke, dragging to the end gives 88%, so use factor 1.135 to adjust for this */
  var pct = 1.135 * (parseInt (100 * distance / $("#volume-bar").width()));
  log ('volrag ' + why + ': dragger is at: ' + distance + ' = ' + pct + '%');
  ambient_volume = pct / 100.0;
  set_ambient_volume();
  }

function play_this_youtube_video (pid, segment, starting_at)
  {
  stop_all_audio_tracks();

  if (!starting_at)
    starting_at = 0;

  var url = resolve_url (pid, segment);
  if (!url.match (/youtube\.com/))
    {
    log ('store_play_yt: "' + pid + '" not a YouTube video');
    stop_all_youtube_videos();
    return;
    }

  var program = programgrid [pid];
  $("#meta-ep-title").html (fixed_up_program_name (program ['name']));
  show_ep_title();

  var video_id = url.match (/v=([^&;]+)/)[1];
  video_log (thumbing, pid, video_id);

  progress_and_volume_bar();

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
      setTimeout ("play_youtube_start()", 600);
      log ('using preload in ytmini' + i);
      mini_player = i;
      try { ytmini[i].seekTo (starting_at); } catch (error) {};
      try { ytmini[i].pauseVideo(); } catch (error) {};
      ytmini_why[i] = 'active';
      $("#ym" + i).css ("zIndex", "27");
      track_episode (undefined, player_real_channel, video_id);
      var youtube_thinks;
      try { youtube_thinks = ytmini[i].getVideoUrl(); } catch (error) {};
      log ('youtube thinks: ' + youtube_thinks);
      player_show_yt_quality();
      if (!youtube_thinks || !youtube_thinks.match (/\?/) || youtube_thinks.match (/[\?\&]v=(\&|$)/))
        {
        log ('**** YOUTUBE IS CONFUSED ABOUT URL: ' + youtube_thinks);
        try { ytmini [i].loadVideoById (video_id, starting_at, yt_quality); } catch (error) {};
        }
      used_preload = true;
      set_ambient_volume();
      setTimeout ('are_we_there_yet("' + thumbing + '","' + mini_player + '","' + video_id + '")', 15000);
      }
    else
      {
      // $("#ym" + i).css ("zIndex", "25");
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
      try { ytmini [mini_player].loadVideoById (video_id, starting_at, yt_quality); } catch (error) {};
      if (player_mute)
        { try { ytmini [mini_player].mute(); } catch (error) {}; }
      else
        { try { ytmini [mini_player].unMute(); } catch (error) {}; }
      set_ambient_volume();
      clearTimeout (awt_timex);
      awt_timex = setTimeout ('are_we_there_yet("' + thumbing + '","' + mini_player + '","' + video_id + '","' + starting_at + '")', 6000);
      ytmini_video_id [mini_player] = video_id;
      ytmini_why [mini_player] = 'active';
      $("#ym" + mini_player).css ("zIndex", "27");
      start_yt_mini_tick();
      track_episode (undefined, player_real_channel, video_id);
      }

    preload_youtube_videos();

    if ((thumbing == 'player' || thumbing == 'player-wait') && player_mode == 'guide')
      {
      /* FIX -- must filter out non-volitional PLAY here! or somewhere else, somehow */
      relay_post ('PLAY ' + channelgrid [ipg_cursor]['id'] + ' ' + video_id)
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
function are_we_there_yet (mode, player_id, video_id, starting_at)
  {
  if (should_attempt_restart ('are we there yet?', mode, player_id, video_id))
    {
    try { ytmini [mini_player].loadVideoById (video_id, starting_at, yt_quality); } catch (error) {};
    set_ambient_volume();
    awt_timex = setTimeout ('seriously_are_we_there_yet("' + thumbing + '","' + mini_player + '","' + video_id + '")', 8000);
    }
  }

function seriously_are_we_there_yet (mode, player_id, video_id, starting_at)
  {
  if (should_attempt_restart ('last chance!', mode, player_id, video_id))
    {
    // ask_question ("Problems have been encountered trying to play this video.",
    //    "Retry", "Reload page", 'are_we_there_yet("' + mode + '","' + player_id + '","' + video_id + '")', "reload_page()", 1);
    ask_question ("Problems have been encountered trying to play this video.",
          "Next video", "Reload page", "yt_error_timeout()", "reload_page()", 1);
    }
  }

function reload_page()
  {
  log ('reload page! bye');
  window.location.href = window.location.href;
  }


function should_attempt_restart (comment, mode, player_id, video_id)
  {
  if ((thumbing == 'store' || thumbing == 'player') && current_tube != 'au' && thumbing == mode && mini_player == player_id)
    {
    log (comment + ' ' + mode + ' ' + player_id + ' ' + video_id);

    var yt_state = -2;
    try { yt_state = ytmini [mini_player].getPlayerState(); } catch (error) {};

    var current_video_id = '';
    var video_url = '';
    try { video_url = ytmini [mini_player].getVideoUrl(); } catch (error) {};
    if (video_url.match (/[\?\&]v=([^\&]*)/))
      current_video_id = video_url.match (/[\?\&]v=([^\&]*)/)[1];

    log ('and presently at: ' + thumbing + ' ' + mini_player + ' ' + current_video_id);

    if (video_id == current_video_id && yt_state == -1)
      {
      log (comment + ' YouTube reports ' + video_id + ' as unstarted. starting again');
      return true;
      }
    else if (video_id == current_video_id)
      {
      log (comment + ' all is well!');
      return false;
      }
    else
      {
      if (yt_state == -1 && current_video_id == '' 
             && (ytmini_video_id [mini_player] == video_id || ytmini_video_id [mini_player] == ''))
        {
        log (comment + ' YouTube reports it is not playing ' + video_id);
        return true;
        }
      else
        {
        log (comment + ' moved on to another video (' + current_video_id + ')');
        return false;
        }
      }
    }
  return false;
  }

function preload_youtube_videos()
  {
return;
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
    var next_url = resolve_url (next_pid, 1);
    var next_video_id = next_url.match (/v=([^&]+)/)[1];
    stop_other_youtube_videos();
    if (thumbing != 'store' && thumbing != 'store-wait')
      {
      log ('will try to preload next episode of this channel');
      preload_one_youtube_video (next_video_id, 'next-episode', 0);
      }
    }

  /* preload first episode of next channel */
  var next_channel = next_channel_to_preload();
  var channel = pool [next_channel];
  if (!channel || channel ['nature'] == '2')
    {
    log ('next channel (' + next_channel + ') is not YouTube, no preload');
    return;
    }
  log ('will try to preload first episode of next channel ' + channel ['id']);
  if (programs_in_real_channel (channel ['id']) > 0)
    preload_next_yt (channel['id']);
  else
    fetch_programs_in (channel['id'], 'preload_next_yt("' + channel['id'] + '")');
  }

function preload_next_yt (id)
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
  var next_url = resolve_url (episode, 1);
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
      $("#ym" + i).css ("zIndex", 25);
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

function play_this_audio (pid, segment)
  {
  log ('play this audio: ' + pid);

  pause_and_mute_all_video();

  current_tube = 'au';

  var url = resolve_url (pid, segment);

  log ('audio url is: ' + url);

  if (false && (thumbing == 'player' || thumbing == 'player-wait'))
    {
    /* 1ft only -- 10ft uses 'fullscreen' */
    /* FIX -- must filter out non-volitional PLAY here! or somewhere else, somehow */
    relay_post ('PLAY ' + channelgrid [ipg_cursor]['id'] + ' ' + pid)
    channelgrid [ipg_cursor]['viewed'] = pid;
    report ('w', channelgrid [ipg_cursor]['id'] + '\t' + pid);
    }

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

function preload_one_audio (pid, segment)
  {
  log ('preload audio episode: ' + pid);

  var program = programgrid [pid];
  var url = resolve_url (pid, segment);

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

function progress_and_volume_bar()
  {
  // $("#btn-knob, #btn-dragger").unbind();
  // $("#progress-bar, #volume-bar").unbind();

  $("#btn-dragger").draggable ({ containment: "#volume-bar", axis: "x" });
  $("#volume-bar").droppable
       ({
           revert: function() { voldrag_end ("revert"); },
           revert: function() { voldrag_end ("deactivate"); },
           accept: "#btn-dragger",
           activeClass: "voldrag",
           tolerance: "touch",
           greedy: true,
           drop: function() { voldrag_end ("drop"); }
       });

  /* $("#btn-volume").click(function() { $(this).toggleClass("mute"); }); */

  if (true)
    {
    $("#btn-knob").draggable ({ containment: "#video-control", axis: "x" });
    $("#progress-bar").droppable
       ({
           revert: function() { progdrag_end ("revert"); },
           revert: function() { progdrag_end ("deactivate"); },
           accept: "#btn-knob",
           activeClass: "progdrag",
           tolerance: "touch",
           greedy: true,
           drop: function() { progdrag_end ("drop"); }
       });
     }
  }

function store_yt_init()
  {
  log ('setting up youtube mini');

  var vhtml = '';
  vhtml += '<div id="ym1" style="position: absolute; z-index: 27; height: 100%; width: 100%; top: 0; left: 0; visibility: visible">';
  vhtml += '<div id="ytapimini1"></div></div>';
  vhtml += '<div id="ym2" style="position: absolute; z-index: 25; height: 100%; width: 100%; top: 0; left: 0; visibility: visible">';
  vhtml += '<div id="ytapimini2"></div></div>';
  vhtml += '<div id="ym3" style="position: absolute; z-index: 25; height: 100%; width: 100%; top: 0; left: 0; visibility: visible">';
  vhtml += '<div id="ytapimini3"></div></div>';
  vhtml += '<div id="ym0" style="position: absolute; z-index: 26; height: 100%; width: 100%; top: 0; left: 0; visibility: visible; background-color: black">';
  vhtml += '<!--div id="ytapimini3"--></div></div>';
  vhtml += '<div id="ss" style="position: absolute; z-index: 28; height: 100%; width: 100%; top: 0; left: 0; display: none; background-color: black">';
  vhtml += '<div id="ym4" style="position: absolute; z-index: 29; height: 100%; width: 100%; top: 0; left: 0; display: none; background-color: black">';
  vhtml += '<!--div id="ytapimini3"--></div></div>';
  vhtml += '<div id="tc" style="position: absolute; z-index: 30; height: 100%; width: 100%; top: 0; left: 0; display: none; background-color: black; overflow: hidden"></div>';

  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    /* $("#player-video").html (''); */
    /* $("#preview-video").html (vhtml); */
    }
  else
    {
    /* $("#preview-video").html (''); */
    /* $("#player-video").html (vhtml); */
    $("#video-constrain").html (vhtml);
    }

  /* primary */
  var params = { allowScriptAccess: "always", wmode: wmode, disablekb: "1" };
  var atts = { id: "myytmini1" };
  var url = "http://www.youtube.com/apiplayer?version=3&enablejsapi=1&playerapiid=ytmini1";

  swfobject.embedSWF (url, "ytapimini1", "100%", "100%", "8", null, null, params, atts);

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

function pause_preloaded_video (slot, start_point)
  {
  if (slot != mini_player)
    {
    log ('pause preloaded video at: ' + start_point);
    try { ytmini[slot].pauseVideo(); } catch (error) {};
    try { ytmini[slot].seekTo (start_point); } catch (error) {};
    }
  }

function play_youtube_start()
  {
  for (var i = 1; i <= 3; i++)
    if (i != mini_player)
      {
      $("#ym" + i).css ("zIndex", "25");
      try { ytmini [i].mute(); } catch (error) {};
      }

  if (player_mute)
    { try { ytmini [mini_player].mute(); } catch (error) {}; }
  else
    { try { ytmini [mini_player].unMute(); } catch (error) {}; }
  try { ytmini [mini_player].playVideo(); } catch (error) {};
  $("#ym4").hide();

  start_yt_mini_tick();
  }

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
    log ('[tvpreview #' + id + '] yt state: ' + ytmini_previous_state [id] + ':' 
            + ystates [ytmini_previous_state [id]] + ' -> ' + state + ':' + ystates [state]);

  if (id == mini_player && current_tube != 'au')
    {
    if (state == 2)
      {
      $("#btn-preview-play").removeClass ("on");
      $("#btn-pause").hide();
      $("#btn-play").show();
      }
    else if (state != 3)
      {
      $("#btn-preview-play").addClass ("on");
      $("#btn-play").hide();
      $("#btn-pause").show();
      }
    }

  if (id == mini_player && current_tube != 'au' 
           && ytmini_previous_state [id] < 0 && state >= 1 && yt_mini_timex == 0)
    {
    log ('starting ticker');
    start_yt_mini_tick();
    }

  if (state == 0 && (ytmini_previous_state [id] == 1 || ytmini_previous_state [id] == 2 || ytmini_previous_state [id] == 3))
    {
    /* change this as soon as possible */
    ytmini_previous_state [id] = state;
    current_episode_phase = 4;
    log ('[tvpreview] yt eof');
    if (id == mini_player && current_tube != 'au')
      {
      track_eof();
      if (thumbing == 'store' || thumbing == 'store-wait' || thumbing == 'signin')
        {
        if (false && store_direction == 'R')
          {
          log ('auto-flipping to previous channel');
          flip_prev();
          }
        else
          {
          // log ('auto-flipping to next channel');
          // flip_next();
          log ('auto-flipping to next episode');
          flip_next_episode();
          }
        }
      else if (thumbing == 'player' || thumbing == 'player-wait')
        {
        log ('auto-flipping to next episode');
        flip_next_episode();
        }
      else
        log ('auto-flip: thumbing is: ' + thumbing);
      }
    return;
    }
  else if (state == 0 && ytmini_previous_state [id] == -1)
    {
    /* theoretically impossible, but sometimes happens under IE */
    ytmini_previous_state [id] = state;
    log ('[tvpreview] yt confused, ignoring this event');
    return;
    }
  else
    ytmini_previous_state [id] = state;
  }

function store_yt_volume()
  {
  var volume = 1;
  if (current_tube == 'au')
    {
    if (audio_stream [audio_player])
      try { volume = audio_stream [audio_player].volume / 100; } catch (error) {};
    }
  else
    {
    if (ytmini [mini_player])
      try { volume = ytmini [mini_player].getVolume() / 100; } catch (error) {};
    }
  return volume;
  }

function store_yt_set_volume (volume)
  {
  log ('store yt set volume: ' + volume);
  if (volume > 1)
    volume = 1;
  else if (volume < 0)
    volume = 0;
  if (current_tube == 'au')
    {
    if (audio_stream [audio_player])
      try { audio_stream [audio_player].setVolume (100 * volume); } catch (error) {};
    }
  else
    {
    if (ytmini [mini_player])
      try { ytmini [mini_player].setVolume (100 * volume); } catch (error) {};
    }
  ambient_volume = volume;
  }

function set_ambient_volume()
  {
  set_ambient_volume_inner();
  setTimeout ("set_ambient_volume_inner()", 200);
  }

function set_ambient_volume_inner()
  {
  if (ambient_volume != undefined)
    {
    // try { ytmini [mini_player].setVolume (100 * ambient_volume); } catch (error) {};
    store_yt_set_volume (ambient_volume);
    store_yt_render_volume();
    }
  }

function store_yt_render_volume()
  {
  var bars = Math.round (store_yt_volume() * 7);

  var html = '';
  // for (var i = 7; i >= 1; i--)
  for (var i = 1; i <= 7; i++)
    {
    if (i > bars)
      html += '<li class="volbar" id="vol-' + i + '"></li>'
    else
      html += '<li class="volbar on" id="vol-' + i + '"></li>';
    }

if (false)
{
  $("#volume-bars").html (html);
  $("#volume-bars .volbar").unbind();
  $("#volume-bars .volbar").click (store_yt_volume_click);

  $("#preview-volume-bars").html (html);
  $("#preview-volume-bars .volbar").unbind();
  $("#preview-volume-bars .volbar").click (store_yt_volume_click);
}

  $("#btn-dragger").css ("left", $("#volume-bar").position().left + (store_yt_volume() * $("#volume-bar").width()) + 'px');

  $("#btn-volume-up").unbind();
  $("#btn-volume-up").click (store_yt_volume_up);
  $("#btn-preview-volume-up").unbind();
  $("#btn-preview-volume-up").click (store_yt_volume_up);

  $("#btn-volume-down").unbind();
  $("#btn-volume-down").click (store_yt_volume_down);
  $("#btn-preview-volume-down").unbind();
  $("#btn-preview-volume-down").click (store_yt_volume_down);
  }

function store_yt_volume_click()
  {
  var id = $(this).attr ("id").replace (/^vol-/, '');
  log ('yt volume click: ' + id);
  store_yt_set_volume (id / 7.0);
  store_yt_render_volume();
  }

function store_yt_volume_up()
  {
  var volume = store_yt_volume();
  volume += 1/7;
  if (volume > 1.0) volume = 1.0;
  store_yt_set_volume (volume);
  store_yt_render_volume();
  }

function store_yt_volume_down()
  {
  var volume = store_yt_volume();
  volume -= 1/7;
  if (volume < 0) volume = 0;
  store_yt_set_volume (volume);
  store_yt_render_volume();
  }



function start_yt_mini_tick()
  {
  log ("*** START YT MINI TICK ***\n");
  clearInterval (yt_mini_timex);
  yt_mini_timex = setInterval ("yt_mini_tick()", 333);
  set_ambient_volume();
  }

var ystates = { '-2': 'fail', '-1': 'unstarted', 0: 'ended', 1: 'playing', 2: 'paused', 3: 'buffering', 4: 'four', 5: 'video cued' };

function yt_mini_tick()
  {
  update_yt_mini_progress_bar();

  var state = -2;
  try { state = ytmini [mini_player].getPlayerState(); } catch (error) {};

  if (state == -2 || state == -1 || state == 0)
    {
    log ('yt_mini_tick, STATE IS: ' + state + ' (' + ystates [state] + ')');
    clearTimeout (yt_mini_timex);
    yt_mini_timex = 0;
    }
  }

var gSegment;
function update_yt_mini_progress_bar()
  {
  var offset = 0, duration = 0;

  if (current_tube == 'au')
    {
    if (audio_stream [audio_player])
      {
      offset = audio_stream [audio_player].position / 1000;
      duration = audio_stream [audio_player].duration / 1000;
      }
    }
  else
    {
    if (ytmini [mini_player] && ytmini [mini_player].getCurrentTime)
      offset = ytmini [mini_player].getCurrentTime();

    if (ytmini [mini_player] && ytmini [mini_player].getDuration)
      try { duration = ytmini [mini_player].getDuration(); } catch (error) {};
    }

  var pct = 100 * offset / duration;

  if (current_tube == 'aux')
    log ('tick, offset: ' + offset + ', duration: ' + duration + ', pct: ' + pct);

  var segment = get_current_segment();
  var program = get_current_program();

  if (!segment || segment ['type'] != 'video')
    return;

  gSegment = segment;

  if (duration && duration > 0 && duration != segment ['duration'] && segment ['type'] == 'video')
    {
    /* database is off, make it reflect reality */
    log ('duration fixup: was ' + segment ['duration'] + ', now ' + duration);
    segment ['duration'] = duration;
    recalculate_total_duration();
    init_progress_bar();
    }

  var adjusted_pct = (segment ['starting'] + offset) * 100 / program ['total-duration'];
  // log ("TICK offset:" + offset + " starting:" + segment ['starting'] + " pct:" + adjusted_pct + " totaldur:" + program ['total-duration']);

  if (adjusted_pct >= 0)
    {
    $("#played").css ("width", adjusted_pct + '%');
    if (! $("#progress-bar").hasClass ("progdrag") && segment ['type'] == 'video')
      $("#btn-knob").css ("left", adjusted_pct + '%');
    }

  var t1 = formatted_time (offset);
  var t2 = formatted_time (duration);

  $("#played-length").text (t1);
  $("#total-length").text (t2);

  if (current_tube != 'au')
    player_show_yt_quality();
  }

function yt_mini_seek_by_percentage (percent)
  {
  var duration = 0;

  if (ytmini [mini_player] && ytmini [mini_player].getDuration)
    try { duration = ytmini [mini_player].getDuration(); } catch (error) {};

  try { ytmini [mini_player].seekTo (duration * percent / 100); } catch (error) {};
  }

function hover_in()
  {
  $(this).addClass ("hover");
  }

function hover_out()
  {
  $(this).removeClass ("hover");
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

function browse_play (channel_id)
  {
  stop_all_audio_tracks();
  stop_all_youtube_videos();

  if (store_landing_jail)
    {
    window.location.href = location.protocol + '//' + location.host + '/tv?view=' + channel_id;
    return;
    }

  if (channel_id)
    ipg_cursor = first_position_with_this_id (channel_id);
  else
    channel_id = channelgrid [ipg_cursor];
  log ('browse play: ' + channel_id + ', at ipg cursor: ' + ipg_cursor);
  player ('guide');
  }

function browse_accept_via_signin (channel)
  {
  browse_accept (channel, browse_accept_callback_f);
  }

var browse_accept_callback_f;

function browse_accept_callback()
  {
  if (typeof (browse_accept_callback_f) == 'function')
    browse_accept_callback_f();
  else
    eval (browse_accept_callback_f);
  }

function browse_accept (channel, callback)
  {
  var position;

  log ('browse_accept: ' + channel);
  browse_accept_callback_f = callback;;

  if (username == 'Guest')
    {
    log ('subscribe: user is not signed in');
    suppress_success_dialog = true;
    new_signup ("browse_accept_via_signin(" + channel + ")");
    return;
    }

  if (channels_in_guide() >= 72)
    {
    redraw_subscribe();
    notice_ok (thumbing, translations ['full'], "browse_accept_callback()");
    return;
    }

  if (first_position_with_this_id (channel) > 0)
    {
    redraw_subscribe();
    notice_ok (thumbing, translations ['alreadyin'], "browse_accept_callback()");
    return;
    }
  else
    {
    position = ipg_cursor;
    if (!position || isNaN (parseInt (position)) || position in channelgrid)
      {
      position = first_empty_channel();
      if (!position)
        {
        log_and_alert ('no free squares');
        browse_accept_callback();
        return;
        }
      }

    update_cart_bubble (1 + channels_in_guide());

    report ('c', 'subscribe [' + thumbing + '] ' + channel + ' ' + position);
    log ('subscribe: ' + channel +  ' at ' + position + '(ipg) = ' + server_grid (position) + '(server)');

    if (thumbing == 'tvpreview')
      {
      thumbing = 'tvpreview-wait';
      $("#waiting-layer").show();
      }
    else if (thumbing == 'store')
      {
      thumbing = 'store-wait';
      $("#preview-waiting").show();
      }

    var channel_info = pool [channel];

    if (parseInt (position) == 0 || server_grid (position) == undefined || server_grid (position) == 0)
      {
      log_and_alert ("Position is zero!");
      browse_accept_callback();
      return;
      }

    var cmd = "/playerAPI/subscribe?user=" + user + mso() + '&' + "channel=" + channel + '&' + "grid=" + server_grid (position) + rx();
    var d = $.get (cmd, function (data)
      {
      log ('subscribe raw result: ' + data);
      var lines = data.split ('\n');
      var fields = lines[0].split ('\t');
      if (fields [0] == '0')
        {
        redraw_store_add_button();

        var fields = lines[2].split ('\t');
        if (channel != fields [1])
          {
          transmogrify (fields[0], channel, fields[1]);
          }
        relay_post ("UPDATE");
        continue_acceptance (position, channel_info);
        }
      else
        {
        var text = translations ['suberr'] + ': ' + fields [1];
        if (fields[0] == '903')
          text = translations ['readonly'];
        notice_ok (thumbing, text, "");
        browse_accept_callback();
        }
      });
    }
  }

function continue_acceptance (position, channel_info)
  {
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

  redraw_store_add_button();
  redraw_ipg();
  home_subscriptions();
  elastic();
  calculate_empties();
  update_cart_bubble (channels_in_guide());

  dir_requires_update = true;

  /* obtain programs, if kept by 9x9 */

  if (channel ['nature'] != '3' && channel ['nature'] != '4' && channel ['nature'] != '5')
    {
    log ('obtaining programs for: ' + channel_info ['id']);
    var cmd = "/playerAPI/programInfo?channel=" + channel_info ['id'] + mso() + rx();
    var d = $.get (cmd, function (data)
      {
      sanity_check_data ('programInfo', data);
      parse_program_data (data);
      redraw_ipg();
      elastic();
      browse_accept_success (position, channel_info ['id']);
      });
    return;
    }

  browse_accept_success (position, channel_info ['id']);
  }

function browse_accept_success (position, channel_id)
  {
  redraw_subscribe();
  player_pop_message (translations ['hasbeenadded']);
  browse_accept_callback();
  }

function transmogrify (position, old_id, new_id)
  {
  log ("** CHANNEL HAS TRANSMOGRIFIED: " + old_id + " -> " + new_id);
  var channel = pool [old_id];
  pool [new_id] = channel;
  channel ['id'] = new_id;
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
    signin_screen();
    return;
    }

  if (first_position_with_this_id (jumpstarted_channel) > 0)
    {
    current_channel = first_position_with_this_id (jumpstarted_channel);
    log ('ALREADY SUBSCRIBED TO: ' + jumpstarted_channel + ', in grid location: ' + current_channel);
    notice_ok (thumbing, translations ['alreadysub'], "after_fetch_channels (true)");
    return;
    }

  log ('subscribing to jumpstart channel: ' + jumpstarted_channel);

  var cmd = "/playerAPI/subscribe?user=" + user + mso() + '&' + "channel=" + jumpstarted_channel + '&' + "grid=" + server_grid (position) + rx();
  var d = $.get (cmd, function (data)
    {
    $("#waiting-layer").hide();
    if (thumbing == 'ipg')
      thumbing = 'ipg-wait';
    current_channel = position;
    log ('subscribe raw result: ' + data);
    var fields = data.split ('\t');
    if (fields [0] == '0')
      {
      $("#waiting-layer").show();
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
  if (ipg_cursor in channelgrid)
    {
    var real_channel = channelgrid [ipg_cursor]['id'];

    if (channelgrid [ipg_cursor]['type'] == '2')
      {
      notice_ok (thumbing, translations ['syschan'], "");
      return;
      }

    pop_with = '';
    unfollow (real_channel)
    }
  }

function unfollow (real_channel, callback)
  {
  $("#waiting-layer").show();

  log ('unfollow: ' + real_channel);

  var gridloc = first_position_with_this_id (real_channel);
  if (!gridloc)
    {
    log ("unfollow: grid location is bad: " + gridloc);
    if (typeof (callback) == 'function') callback(); else eval (callback);
    return;
    }

  var servergridloc = server_grid (gridloc);

  var cmd = "/playerAPI/unsubscribe?user=" + user + mso() + '&' + "channel=" + real_channel + '&' + "grid=" + servergridloc + rx();
  var d = $.get (cmd, function (data)
    {
    $("#waiting-layer").hide();

    var fields = data.split ('\t');
    if (fields[0] != '0')
      {
      var text = "Error deleting: " + fields[1];
      if (fields[0] == '903')
        text = translations ['readonly'];
      notice_ok (thumbing, text, "");
      if (typeof (callback) == 'function') callback(); else eval (callback);
      return;
      }

    dir_requires_update = true;
    delete (channelgrid [gridloc]);
    redraw_ipg();
    elastic();

    update_cart_bubble (channels_in_guide());
    log ('unfollow successful');
    redraw_subscribe();
    home_subscriptions();
    player_pop_message (translations ['hasbeenremoved']);

    relay_post ("UPDATE");
    report ('c', 'unsubscribe [' + thumbing + '] ' + real_channel + ' ' + gridloc);
    ipg_metainfo();
    check_for_empty_clusters();
    if (typeof (callback) == 'function') callback(); else eval (callback);
    });
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

function unhide_player (player)
  {
  log ('unhide: ' + player);

  switch (player)
    {
    case "yt":

      $("#v, #fp1, #fp2, #ss").hide();
      $("#yt1, #yt2, #yt3").show();
      $("#yt1, #yt2, #yt3").css ("visibility", "visible");
      break;
    }
  }

function stop_all_other_players()
  {
  log ('stop all other players');

  if (current_tube != 'fp' || fp_player != 'player1')
    try { flowplayer ('player1').stop(); } catch (error) {};

  if (current_tube != 'fp' || fp_player != 'player2')
    try { flowplayer ('player2').stop(); } catch (error) {};

  for (var i = 1; i <= 3; i++)
    {
    if (ytmini [i])
      {
      try { ytmini [i].stopVideo(); } catch (error) {};
      ytmini_video_id [i] = undefined;
      }
    }
  }

function stop_all_players()
  {
  log ('stop all players');
  stop_all_youtube_videos();
  stop_all_audio_tracks();
  }

function onYouTubePlayerReady (playerId)
  {
  log ("yt ready, id is: " + playerId);
  if (playerId == 'ytmini1')
    {
    ytmini[1] = document.getElementById ("myytmini1");
    try { ytmini[1].addEventListener ('onStateChange', 'tvpreview_yt_state_1'); } catch (error) {};
    try { ytmini[1].addEventListener ('onError', 'yt_error_1'); } catch (error) {};
    if (tvpreview_kickstart)
      {
      log ('ytmini1 kickstart!');
      /* decrement the segment since it will get incremented */
      current_segment--;
      store_play_yt();
      }
    return;
    }
  else if (playerId == 'ytmini2')
    {
    ytmini[2] = document.getElementById ("myytmini2");
    try { ytmini[2].addEventListener ('onStateChange', 'tvpreview_yt_state_2'); } catch (error) {};
    try { ytmini[2].addEventListener ('onError', 'yt_error_2'); } catch (error) {};
    return;
    }
  else if (playerId == 'ytmini3')
    {
    ytmini[3] = document.getElementById ("myytmini3");
    try { ytmini[3].addEventListener ('onStateChange', 'tvpreview_yt_state_3'); } catch (error) {};
    try { ytmini[3].addEventListener ('onError', 'yt_error_3'); } catch (error) {};
    return;
    }
  log ('exit onYouTubePlayerReady: ' + playerId);
  }

function preload_next_youtube_episode()
  {
  if (program_cursor + 1 <= n_program_line)
    {
    var program = program_line [parseInt (program_cursor) + 1];
    var url = resolve_url (program, 1);
    if (url.match (/youtube\.com/))
      {
      var video_id = url.match (/v=([^&]+)/)[1];
      log ('preloading next youtube episode #' + (parseInt (program_cursor) + 1) + ': ' + video_id);
      load_youtube (video_id, false);
      }
    else
      log ('program at cursor ' + program_cursor + ' is not a YouTube: ' + url);
    }
  else
    log ('at end: not preloading next youtube episode');

  /* are we in the IPG */
  if (! (current_channel in channelgrid))
    return;
   
  /* first program of next channel */
  var next_channel = next_channel_square_setwise (current_channel);
  var channel = channelgrid [next_channel];

  if (channel ['nature'] != '3')
    {
    log ('next channel is not YouTube, no preload');
    return;
    }

  if (programs_in_real_channel (channel['id']) > 0)
    {
    preload_next_youtube_inner (channel['id']);
    return;
    }

  if (fetch_youtube_or_facebook (channel['id'], 'preload_next_youtube_inner("' + channel['id'] + '")'))
    return;
  }

function preload_next_youtube_inner (real_channel)
  {
  var episode = first_program_in_real_channel (real_channel);
  var next_url = resolve_url (episode, 1);
  if (next_url.match (/youtube\.com/))
    {
    var next_video_id = next_url.match (/v=([^&]+)/)[1];
    log ('(preload YouTube) next channel: ' + real_channel + ', first episode: '  + episode + ', video id: ' + next_video_id);
    load_youtube (next_video_id, false);
    }
  else
    log ('first program in ' + real_channel + ' is not a YouTube: ' + url);
  }

function yt_error_1 (code)
  {
  yt_error (1, code);
  }

function yt_error_2 (code)
  {
  yt_error (2, code);
  }

function yt_error_3 (code)
  {
  yt_error (3, code);
  }

function yt_error (player_id, code)
  {
  var errors = { 100: 'Video not found', 101: 'Embedding not allowed', 150: 'Video not found' };
  var errtext = translations ['ytvid'] + ': Code ' + code;

  if (yt_error_timex != 0)
    {
    log ('** YOUTUBE ERROR ' + code + ', BUT ALREADY PROCESSING AN ERROR (player id: ' + player_id + ') **');
    return;
    }

  // if (player_id == mini_player && code != 0 && thumbing != 'store' && thumbing != 'store-wait')
  if (player_id == mini_player && code != 0)
    {
    log ('** YOUTUBE ERROR ** ' + errtext + ' (player id: ' + player_id + ')');
  
    var next = (store_direction == 'F') ? 'flip_next()' : 'flip_prev()';

    // notice_ok (thumbing, translations ['ytvid'] + ': code ' + code, "yt_error_timeout()");
    ask_question (errtext, translations ['trynextep'], translations ['nextchan'], "yt_error_timeout()", "player_next()", 1);

    notify_server_bad_program();

    /* unload the chromeless player, or bad things happen. don't do this now */
    // $("#yt1").css ("display", "none");
    // ytplayer = undefined;

    /* replaced this with notice_ok above */
    // yt_error_timex = setTimeout ("yt_error_timeout()", 3500);

    }
  else if (player_id == mini_player && code == 0)
    {
    setTimeout ("yt_error_timeout()", 1500);
    }
  else
    {
    log ('** YOUTUBE ERROR ** ' + errtext + ' (player id: ' + player_id + ' -- ignoring!)');
    }
  }

function yt_error_timeout()
  {
  clear_msg_timex();
  current_episode_phase = 4;
  if (thumbing == 'store' || thumbing == 'store-wait')
    {
    if (true)
      {
      log ('after error: auto-flipping to next episode');
      flip_next_episode();
      }
    else
      {
      log ('after error: auto-flipping to next channel');
      if (store_direction == 'R')
        flip_prev();
      else
        flip_next();
      }
    }
  else if (thumbing == 'player' || thumbing == 'player-wait')
    {
    log ('after error: auto-flipping to next episode');
    flip_next_episode();
    }
  }

function notify_server_bad_program()
  {
  var current_program = program_line [program_cursor];
  var query = "/playerAPI/programRemove?user=" + user + mso() + '&' + 'program=' + current_program + rx();

  var d = $.get (query, function (data)
    {
    log ('programRemove returned: ' + data);
    });
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

function show_eps()
  {
  $("#ep-layer").css ("bottom", "1.75em");
  $("#ep-layer").show();

  episode_clicks_and_hovers();

  report ('e', 'episode-bar');
  }

function hide_sg_bubble()
  {
  $("#sg-bubble").hide();
  user_closed_episode_bubble = true;
  }

function show_sg_bubble()
  {
  return; /* turn off this, per Lily 20-May-2011 */
  $("#sg-bubble").show();
  $("#btn-bubble-del").unbind();
  $("#btn-bubble-del").click (hide_sg_bubble);
  }

function yes_or_no (question, ifyes, ifno, defaultanswer)
  {
  $("#qyes").html (translations ['qyes']);
  $("#qno").html (translations ['qno']);
  a_or_b (question, ifyes, ifno, defaultanswer)
  }

function ask_question (question, answer1, answer2, if1, if2, defaultanswer)
  {
  $("#qyes").html (answer1);
  $("#qno").html (answer2);
  a_or_b (question, if1, if2, defaultanswer)
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
    $("#btn-yesno-no").removeClass ("on");
    $("#btn-yesno-yes").addClass ("on");
    }
  else
    {
    $("#btn-yesno-yes").removeClass ("on");
    $("#btn-yesno-no").addClass ("on");
    }

  thumbing = 'yes-or-no';

  $("#yesno-layer").show();

  $("#btn-yesno-yes").unbind();
  $("#btn-yesno-yes").click (function() { yn_enter (1) });

  $("#btn-yesno-no").unbind();
  $("#btn-yesno-no").click (function() { yn_enter (2) });

  elastic();
  }

function guide_set_cursor (id)
  {
  id = id.replace (/^guide-/, '');
  $("#guide-" + ipg_cursor).removeClass ("on");
  ipg_cursor = parseInt (id);
  $("#guide-" + ipg_cursor).addClass ("on");
  }

function delete_this_channel (id)
  {
  guide_set_cursor (id);

  ipg_metainfo();
  relay_post_position();

  delete_yn();
  }

function delete_yn()
  {
  remove_draggables();

  if ($("#rename-layer").css ('display') == 'block')
    return;

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
  thumbing = 'guide';
  }

function delete_no()
  {
  thumbing = 'guide';
  }

function delete_set_yn (id)
  {
  id = id.replace (/^del-set-/, '');
  if (!cluster_is_empty (id))
    yes_or_no (translations ['deleteset'], 'delete_set_yes("' + id + '")', 'delete_set_no()', 2);
  else
    notice_ok (thumbing, translations ['emptyset'], "");
  }

function delete_set_yes (id)
  {
  delete_set (id);
  thumbing = 'ipg';
  }

function delete_set_no()
  {
  thumbing = 'ipg';
  }

function switch_to_facebook()
  {
  if (username == 'Guest')
    {
    notice_ok (thumbing, translations ['plzreg'], "");
    return;
    }

  if (FB)
    yes_or_no (translations ['sharing'], "fb_yes()", "fb_no()", 2);
  else
    notice_ok (thumbing, 'Facebook error, please try again later', "");
  }

var fbparms;

function fb_yes()
  {
  var channel = pool [player_real_channel];
  var program = get_current_program();

  var origthumb = program ['thumb'];

  var name = program ['name'];
  var desc = (program ['desc'] && program ['desc'] != '') ? program ['desc'] : "Browse your favorite videos on 9x9.tv";

  var host = location.host;

  if (channel ['nature'] == '5')
    thumb = decodeURIComponent (origthumb.split (/url=/)[1])
  else
    thumb = origthumb;

  var pid = program ['id'];
  if (pid.match (/^\d+\./))
    pid = pid.match (/^\d+\.(.*)$/)[1];

  var link = share_url();
  log ('share link: ' + link);

  link = link.replace (/www\./, '');
  // link = link.replace (/\&episode=.*$/, '');

  var uri = location.protocol + '//' + location.host + '/tanks';

  var parms = { method: 'feed', name: name, link: link, picture: thumb, description: desc, app_id: '110847978946712', show_error: true, redirect_uri: uri };
  // parms = { method: 'feed', name: name, link: link, picture: thumb, description: desc, app_id: '193650000771534', show_error: true };

fbparms = parms;

  popup_active = true;
  FB.ui (parms, function (response)
    {
    popup_active = false;
    $("#body").focus();
    log ('response: ' + response);
    log ('response.post_id: ' + response.post_id);
    if (response && response.post_id)
      {
      log ('published as: ' + response.post_id);
      notice_ok (thumbing, "Shared to Facebook successfully", "");
      }
    else
      log ('unsuccessful response, may not have been published');
    });
  }

function OLD_fb_yes()
  {
  var months = { 0: 'January', 1: 'February', 2: 'March', 3: 'April', 4: 'May', 5: 'June',
                 6: 'July', 7: 'August', 8: 'September', 9: 'October', 10: 'November', 11: 'December' };

  var now = new Date();
  var stringdate = months [now.getMonth()] + ' ' + now.getDate() + ', ' + now.getFullYear();

  var channel = channelgrid [ipg_cursor]; // was current_channel not ipg_cursor

  current_program = program_line [program_cursor];
  var origthumb = programgrid [current_program]['thumb'];
  var name = 'My 9x9 Channel Guide: ' + stringdate;
  var desc = 'My 9x9 Channel Guide. Easily browse your favorite video Podcasts on the 9x9 Player! Podcasts automatically download and update for you, bringing up to 81 channels of new videos daily.';

  var program = programgrid [current_program];

  name = program ['name'];
  desc = (program ['desc'] && program ['desc'] != '') ? program ['name'] : "Browse your favorite videos on 9x9";

  var host = location.host;

  if (channel ['nature'] == '5')
    {
    // decided not to do this
    // thumb = 'http://9x9fbcache.s3.amazonaws.com/' + current_program + '.' + ext;
    thumb = decodeURIComponent (origthumb.split (/url=/)[1])
    }
  else
    thumb = origthumb;

  if (sitename == '5f')
    host = host.replace ('9x9.tv', '5f.tv');

  var pid = current_program;
  if (pid.match (/^\d+\./))
    pid = pid.match (/^\d+\.(.*)$/)[1];

  var query = "/playerAPI/saveShare?user=" + user + mso() + '&' + 'channel=' + channel ['id'] + '&' + 'program=' + pid + rx();

  log ("SAVE SHARE: " + query);
  $("#waiting-layer").show();

  var d = $.get (query, function (data)
    {
    $("#waiting-layer").hide();
    log ('saveShare returned: ' + data);

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
        log ('response: ' + response);
        log ('response.post_id: ' + response.post_id);
        if (response && response.post_id)
          {
          log ('published as: ' + response.post_id);
          ask_question ("Shared to Facebook successfully.", "Resume watching", "Go to my guide", "", "guide()", 1);
          }
        else
          {
          log ('unsuccessful response, may not have been published');
          // notice_ok (thumbing, "Share to Facebook was not successful", "");
          }
        });
      }
    });
  }

function fb_no()
  {
  $("#ep-layer").show();
  }

function yn_left()
  {
  if (yn_cursor == 2)
    {
    yn_cursor = 1;
    $("#btn-yesno-no").removeClass ("on");
    $("#btn-yesno-yes").addClass ("on");
    }
  }

function yn_right()
  {
  if (yn_cursor == 1)
    {
    yn_cursor = 2;
    $("#btn-yesno-yes").removeClass ("on");
    $("#btn-yesno-no").addClass ("on");
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

function state()
  {
  var yt_state = -2;

  var states = { '-2': 'fail', '-1': 'unstarted', '0': 'ended', '1': 'playing', '2': 'paused', '3': 'buffering', '5': 'cued' };

  try
    {
    var channel = '[?]';
    if (thumbing == 'store' || thumbing == 'store-wait')
      channel = store_channel;
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
    log (star + ' youtube #' + i + ' state ' + yt_state + ': ' + states [yt_state] + ' ' + video_id);
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

/* ------------------------------------------------------ vv AUDIO vv ------------------------------------------------------ */

var sm_start_timex = [];

function sm_start (slot, id, url, callback)
  {
  if (slot in sm_start_timex)
    clearTimeout (sm_start_timex [slot]);

  if (audio_stream [slot])
    {
    audio_stream [slot].destruct();
    audio_stream [slot] = undefined;
    }

  if (url.match (/\+/))
    {
    url = url.replace (/\+/g, '%2B');
    log ("url fixed up to: " + url);
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
      clearInterval (audio_tick_timex);
      audio_tick_timex = setInterval ("audio_tick()", 333);
      },
    onfinish: function()
      {
      log ('sm: audio' + this.sID + ' ended');
      if (this.sID == audio_player)
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
        if (slot == audio_player)
          sm_start_timex [slot] = setTimeout ('sm_start_second_timeout("' + slot + '")', 1250);
        }
      }
    clearInterval (audio_tick_timex);
    audio_tick_timex = setInterval ("audio_tick()", 333);
    }
  }

function sm_start_second_timeout (slot)
  {
  if (current_tube == 'au' && slot == audio_player)
    {
    if (audio_ready [slot] == false)
      {
      log ('[2nd timeout] slot ' + slot + ' :: playState: ' + audio_stream [slot].playState + ', readyState: ' + audio_stream [slot].readyState + ', position: ' + audio_stream [slot].position);
      if (!audio_stream [slot].position)
        {
        log ('STILL the audio has not started! sending "play"');
        audio_stream [slot].play();
        sm_start_timex [slot] = setTimeout ('sm_start_second_timeout("' + slot + '")', 1250);
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

  $("#ss").html ('');
  $("#ss").hide();

  var program = programgrid [pid];
  var clip = get_current_clip();

  var html = '';
  html += '<div id="slide-container"></div>';

  $("#ss").html (html);

  var img = new Image();
  img.onload = function()
    {
    // var aspect = $(window).width() / $(window).height();
    var aspect = $("#ss").width() / $("#ss").height();
    var slide_aspect = this.width / this.height;
  
    var rsz;
    if (slide_aspect > aspect)
      rsz = ' style="display: block; width: 100%; height: auto; margin-left: auto; margin-right: auto;" ';
    else
      rsz = ' style="display: block; width: auto; height: 100%; margin-left: auto; margin-right: auto;" ';

    var html = '';
    html += '<div id="slide" style="display: none; position: absolute; top: 0; left: 0; width: 100%; height: 100%">';
    html += '<img src="' + clip ['snapshot'] + '" ' + rsz + '></div>';

    $("#slide-container").html (html);
    $("#slide").fadeIn (300);
    };

  /* trigger the above code to fixup after dimensions are known */
  img.src = clip ['snapshot'];

  $("#ss").show();

  log ('audio' + audio_player + ' play');
  audio_stream [audio_player].play();

  /* set the pause/play button */
  audio_onresume_inner();

  adjust_audio_mute();
  set_ambient_volume();

  fixup_audio_pause_button();
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
  }

function audio_ended()
  {
  clearInterval (audio_tick_timex);
  $("#ss").html ('');
  $("#ss").hide();
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
  if (player_mute)
    audio_mute();
  else
    audio_unmute();
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
  $("#ss").html ('');
  $("#ss").hide();
  audio_stop();
  current_tube = '';
  }

/* 1ft only */
function fixup_audio_pause_button()
  {
  if (current_tube == 'au' && audio_stream [audio_player])
    {
    if (audio_stream [audio_player].paused)
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
  }

/* ------------------------------------------------------ ^^ AUDIO ^^ ------------------------------------------------------ */

/* Cart */

function setup_cart()
  {
  $("#btn-cart, #cart-bubble").unbind();
  $("#btn-cart, #cart-bubble").click (guide);

  $("#btn-cart").hover (function()
    { $("#cart-bubble").addClass("big"); },
  function()
    { $("#cart-bubble").removeClass("big"); });
  }

function update_cart_bubble (new_count)
  {
  $("#ch-sum").html ('(' + new_count + ')');
  $("#home-follow-count").html (new_count);
  return;
  /* old animation, keep on ice */
  var old_count = $("#cart-bubble span").html();
  if (parseInt (old_count) != parseInt (new_count))
    {
    log ("** CART BOUNCE ** old: " + old_count + " new: " + new_count);
    $("#cart-bubble span").html (new_count);
    $("#btn-cart").effect ("bounce", 300);
    }
  }

function run_previewing (obj)
  {
  log ('run previewing: ' + obj);
  if ($.browser.msie || $.browser.opera)
    {
    $(obj).addClass("on");
    $(obj).queue (function (next)
      {
      $(obj).animate ({ "backgroundColor": "#e5e5e5"}, 1500).animate ({ "backgroundColor": "#bbb"}, 1500);
      $(obj).queue (arguments.callee);
      next();
      });
    }
  else
    $(obj).addClass("on");
  }

function run_glowing()
  {
  if (!$("#btn-add-ch-L").hasClass ("disable"))
    {
    if ($.browser.msie || $.browser.opera)
      {
      $("#btn-add-ch-L").queue ("fx", function (next)
        {
        $(this).animate ({ "opacity": "1.0" }, 1500) .animate ({ "opacity": "0.75" }, 1500);
        $(this).queue (arguments.callee);
        next();
        });
      }
    else
      $("#btn-add-ch-L").addClass ("on");
    }
  else
    {
    if ($.browser.msie || $.browser.opera)
      {
      $("#btn-add-ch-L").stop ("fx"); 
      $("#btn-add-ch-L").css ({ "box-shadow": "0 0 0 #000", "border": "#444 1px solid" });
      }
    else
      $("#btn-add-ch-L").removeClass ("on").css ("border", "#444 1px solid");
    }
  }

function ellipses()
  {
  $(".ellipsis").ellipsis();
  }

function visit_current_curator()
  {
  var channel = pool [player_real_channel];
  curation (channel ['curatorid']);
  }

function visit_youtube_episode_externally()
  {
  var pid = program_line [program_cursor];
  var url = resolve_url (pid, current_segment);
  var video_id = url.match (/[\?\&]v=([^\&]*)/)[1];
  if (video_id)
    window.open ("http://www.youtube.com/watch?v=" + video_id, "_blank");
  }

function visit_youtube_channel_externally()
  {
  var channel = pool [player_real_channel];
  if (channel ['nature'] == '3')
    window.open ("http://www.youtube.com/user/" + channel ['extra'], "_blank");
  else
    visit_current_curator();
  }

var current_curator_page;
var current_curator_name;

function curation (id)
  {
  log ("curation: " + id);

  fresh_layer ('curator');
  thumbing = 'curator';

  current_curator_page = id;
  current_curator_name = '';

  set_hash ('#!curator=' + id);

  $("#curator-activity li .number").html ('');
  $("#channel-list, #following-list").html ('');
  $("#channel-panel, #following-panel").hide();
  $("#curator-tabs li").removeClass ("on");

  $("#curator-layer").removeClass("curator visitor");
  $("#curator-layer").addClass ((id == curatorid) ? "curator" : "visitor").fadeIn(400);

  /* We do not presently offer this functionality */
  $("#curator-page .link").hide();

  load_curator_then (id, 'curation_inner("' + id + '")');

  stop_all_youtube_videos();
  stop_all_audio_tracks();
  }

function curation_inner (id)
  {
  log ('curation inner: ' + id);
  var curat = curator_pool [id];

  current_curator_name = curat ['name'];

  $("#curator-activity-followers").text (curat ['followers']);

  $("#curator-profile-name").text (curat ['name']);
  $("#curator-tabs #channel .name").text (curat ['name']);
  $("#curator-tabs #channel .number").text ('-=(' + curat ['channelcount'] + ')=-');
  $("#curator-tabs #following .name").text (curat ['name']);
  $("#curator-tabs #following .number").text ('-=(' + curat ['followers'] + ')=-');

  $("#curator-url").text (curat ['url']);
  $("#curator-declaration").text (curat ['desc']);
  // $("#curator-profile-photo img").attr ("src", curat ['thumb']);
  load_constrained_image ("#curator-profile-photo img", curat ['thumb'], function()
    {
    var h = $("#curator-profile-photo img").height();
    if (h > 181)
      $("#curator-profile-photo img").css ("height", 181);
    var w = $("#curator-profile-photo img").width();
    if (w > 181)
      $("#curator-profile-photo img").css ("width", 181);
    });
  $("#curator-ch-num").text ("(" + curat ['channelcount'] + ")");
  $("#curator-fol-num").text ("(" + curat ['followers'] + ")");

  if (language == 'en')
    {
    $("#curator-tabs .left div p").html
      ('<span class="name">' + curat ['name'] + '</span>\'s Channels<span id="curator-ch-num" class="number">(' + curat ['channelcount'] + ')</span>');
    $("#curator-tabs .right div p").html
      ('<span class="name">' + curat ['name'] + '</span>\'s Followings<span id="curator-ch-num" class="number">(' + curat ['followers'] + ')</span>');
    }
  else
    {
    $("#curator-tabs .left div p").html 
      ('<span class="name">' + curat ['name'] + '</span> 的頻道<span id="curator-ch-num" class="number">(' + curat ['channelcount'] + ')</span>');
    $("#curator-tabs .right div p").html 
      ('<span class="name">' + curat ['name'] + '</span> 的訂閱<span id="curator-ch-num" class="number">(' + curat ['followers'] + ')</span>');
    }

  load_curator_channels (id);

  $("#channel").unbind();
  $("#channel").click (channel_panel);

  $("#following").unbind();
  $("#following").click (following_panel);

  btn_manage();
  }

function btn_manage()
  {
  if (username != 'Guest' && $("#channel").hasClass ("on") && current_curator_page == curatorid)
    {
    $("#btn-manage").show();
    $("#btn-manage").unbind();
    $("#btn-manage").mouseover (function()
      {
      $("#curator-main .manage-tip").show();
      });
    $("#btn-manage").mouseout (function()
      {
      $("#curator-main .manage-tip").hide();
      });
    $("#btn-manage").click (function() { seamless_exit ("/cms/index.html"); });
    }
  else
    $("#btn-manage").hide();
  }

function channel_panel()
  {
  $("#curator-tabs li").removeClass ("on");
  $("#channel").addClass ("on");
  $("#following-panel").hide();
  $("#channel-panel").show();
  activate_channel_scrollbar();
  btn_manage();
  }

function following_panel()
  {
  $("#curator-tabs li").removeClass ("on");
  $("#following").addClass ("on");
  $("#channel-panel").hide();
  $("#following-panel").show();
  activate_following_scrollbar();
  btn_manage();
  }

var curator_pool = {};

function load_curator_then (id, callback)
  {
  if (id in curator_pool && id != curatorid)
    {
    if (typeof (callback) == 'function')
      callback (curator_pool [id]);
    else
      eval (callback);
    return;
    }

  var query = '/playerAPI/curator?curator=' + id + '&' + 'user=' + user + rx();
  var d = $.get (query, function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields[0] == '0')
      {
      var curat = line_to_curator (lines[2]);
      var channels = [ undefined ];
      for (var i = 4; i <= lines.length; i++)
        {
        if (lines[i] != '' && lines[i] != '--')
          {
          var channel = line_to_channel (lines[i]);
          pool [channel ['id']] = channel;
          channels.push (channel);
          }
        else
          break;
        }
      curat ['channels'] = channels;
      curator_pool [curat['id']] = curat;

      if (typeof (callback) == 'function')
        callback (curat);
      else
        eval (callback);
      }
    else
      alert ("playerAPI curator returned: " + lines[0]);
    });
  }

function line_to_curator (line)
  {
  var fields = line.split ('\t');
  // curator id, curator name, curator description, curator image url, curator profile url, channel count, channel subscription count, follower count
  var curat = { id: fields[0], name: fields[1], desc: fields[2], thumb: fields[3], url: fields[4], channelcount: fields[5], subs: fields[6], followers: fields[7] };
  return curat;
  }

var curator_ch_stack = [];
var curator_follow_stack = [];

function load_curator_channels (id)
  {
  curator_ch_stack = [];
  $("#channel-list, #following-list").html ('');

  channel_scrollbar_activated = false;
  following_scrollbar_activated = false;

  var query = '/playerAPI/curator?curator=' + id + '&' + 'user=' + user + rx();
  var d = $.get (query, function (data)
    {
    var blocks = data.split (/^--$/m);
    var lines = blocks[2].split ('\n');

    var ch_count = 0;
    var total_followers = 0;
    var html = '';

    for (i = 0; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        ch_count++;
        var channel = line_to_channel (lines[i]);
        pool [channel ['id']] = channel;
        curator_ch_stack [ch_count] = channel;
        log ('[channel panel] added to pool: ' + channel ['id']);

        total_followers += parseInt (channel ['subscriptions']);

        var funf = ( first_position_with_this_id (channel ['id']) > 0 ) ? translations ['unfollow'] : translations ['follow'];
        if (username != 'Guest' && id == curatorid && channel ['nature'] != '11' && channel ['nature'] != '12')
          funf = translations ['edit'];

        var ago = ageof (channel ['timestamp'], true);
        var playable = ! (channel ['id'].match (/^f-/));
        var li_classes = (channel ['nature'] == '11' || channel ['nature'] == '12') ? 'class="favorite"' : '';

        html += '<li id="curator-ch-' + ch_count + '" ' + li_classes + '>';
        html += '<p class="channel-title">' + channel ['name'] + '</p>';
        html += '<p class="btn-action"><span>' + funf + '</span></p>';
        html += '<div class="thumb">';
        if (playable)
          html += '<p class="icon-pl"></p>';
        html += '<img src="' + channel ['thumb1'] + '" class="thumb1">';
        html += '<img src="' + channel ['thumb2'] + '" class="thumb2">';
        html += '<img src="' + channel ['thumb3'] + '" class="thumb3">';
        html += '</div>';
        html += '<div class="channel-meta">';
        html += '<p>' + channel ['count'] + ' ' + translations ['episodes'] + '<span class="divider">|</span>' + ago + '</p>';
        // html += '<p class="next-update">Next Update: Tomorrow</p>';
        html += '</div>';
        html += '<p class="channel-description ellipsis multiline">' + channel ['desc'] + '</p>';
        if (channel ['nature'] == '11' || channel ['nature'] == '12')
          html += '<p class="default-channel-title">' + translations ['usersfav'].replace ('%1', channel ['curatorname']) + '</p>';
        else
          html += '<p class="default-channel-title"></p>';
        html += '<ul class="follower-list">';
        html += '<li></li>';
        html += '</ul>';
        html += '<div class="channel-popularity">';
        html += '<p>' + channel ['subscriptions'] + ' ' + translations ['followers'] + '</p>';
        html += '<p>' + channel ['viewcount'] + ' ' + translations ['views'] + '</p>';
        html += '</div>';
        html += '</li>';

        if (ch_count == 1 && username != 'Guest' && id == curatorid)
          {
          html += '<li class="new-channel">';
          html += '<div><p>' + translations ['createanewchannel'] + '</p></div>';
          html += '</li>';
          }
        }
      $("#channel-list").html (html);

      $("#channel-list li").unbind();
      $("#channel-list li").click (function() { curator_ch_click ($(this).attr ("id")); });

      $("#channel-list li .btn-action").unbind();
      $("#channel-list li .btn-action").click (function (event) { event.stopPropagation(); curator_ch_execute_follow ($(this).parent().attr ("id")); });

      $("#channel-list li.new-channel").unbind();
      $("#channel-list li.new-channel").click (function() { seamless_exit ("/cms/channel-add.html"); });

      $("#curator-activity-channels").text (ch_count);
      $("#curator-tabs #channel .number").text ('(' + ch_count + ')');

      /* activate_channel_scrollbar(); */
      setTimeout ('channel_panel()', 0);

      $("#curator-activity-followers").html (total_followers);
      }
    });

  var query = '/playerAPI/channelLineup?subscriptions=' + id + '&' + 'user=NOBODY' + rx();
  var d = $.get (query, function (data)
    {
    var html = '';
    var su_count = 0;
    var lines = data.split ('\n');
    for (i = 2; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        su_count++;

        var channel = line_to_channel (lines[i]);
        pool [channel ['id']] = channel;
        curator_follow_stack [su_count] = channel;
        log ('[following panel] added to pool: ' + channel ['id']);

        var ago = ageof (channel ['timestamp'], true);
        var funf = ( first_position_with_this_id (channel ['id']) > 0 ) ? translations ['unfollow'] : translations ['follow'];
        var eplural = channel ['count'] == 1 ? translations ['nepisode'] : translations ['episode'];

        html += '<li id="curator-follow-' + su_count + '">';
        html += '<p class="channel-title">' + channel ['name'] + '</p>';
        html += '<p class="btn-action"><span>' + funf + '</span></p>';
        html += '<p class="icon-pl"></p>';
        html += '<img src="' + channel ['thumb1'] + '" class="thumb1">';
        html += '<img src="' + channel ['thumb2'] + '" class="thumb2">';
        html += '<img src="' + channel ['thumb3'] + '" class="thumb3">';
        html += '<p class="followed-curator-photo"><img src="' + channel ['curatorthumb'] + '"></p>';
        html += '<div class="channel-meta">';
        html += '<p>' + channel ['count'] + ' ' + eplural + '<span class="divider">|</span>' + ago + '</p>';
        // html += '<p class="next-update">Next Update: Whenever</p>';
        html += '</div>';
        html += '<p class="followed-curator">' + translations ['curatorby'] + '<span>' + channel ['curatorname'] + '</span></p>';
        html += '</li>';
        }
      $("#following-list").html (html);

      $("#following-list li").unbind();
      $("#following-list li").click (function() { curator_follow_click ($(this).attr ("id")); });

      $("#following-list li .btn-action").unbind();
      $("#following-list li .btn-action").click (function (event)
        {
        $(this).hide();
        event.stopPropagation();
        curator_follow_follow ($(this).parent().attr ("id"));
        });

      $("#following-list li .followed-curator span").unbind();
      $("#following-list li .followed-curator span").click (function (event)
        {
        event.stopPropagation();
        var id = $(this).parent().parent().attr ("id").replace (/^curator-follow-/, '');
        var channel = curator_follow_stack [id];
        curation (channel ['curatorid']);
        });

      $("#following-list li .followed-curator-photo").unbind();
      $("#following-list li .followed-curator-photo").click (function (event)
        {
        event.stopPropagation();
        var id = $(this).parent().attr ("id").replace (/^curator-follow-/, '');
        var channel = curator_follow_stack [id];
        curation (channel ['curatorid']);
        });

      $("#curator-activity-following").text (su_count);
      $("#curator-tabs #following .number").text ('(' + su_count + ')');

      activate_following_scrollbar();
      }
    });
  }

var channel_scrollbar_activated = false;

function activate_channel_scrollbar()
  {
  if ($("#channel-list li").length > 0 && $("#channel-panel").css ("display") == 'block' && !channel_scrollbar_activated)
    {
    channel_scrollbar_activated = true;
    var h = $(window).height() - $("#channel-constrain").offset().top - 15;
    $("#channel-constrain").css ({ height: $(window).height() - $("#channel-constrain").offset().top });
    $("#channel-slider").css ({ height: h });
    scrollbar ("#channel-constrain", "#channel-list", "#channel-slider");
    log ('channel scrollbar activated');
    }
  }

var following_scrollbar_activated = false;

function activate_following_scrollbar()
  {
  if ($("#following-list li").length > 0 && $("#following-panel").css ("display") == 'block' && !following_scrollbar_activated)
    {
    following_scrollbar_activated = true;
    var h = $(window).height() - $("#following-constrain").offset().top - 15;
    $("#following-constrain").css ({ height: $(window).height() - $("#following-constrain").offset().top });
    $("#following-slider").css ({ height: h });
    scrollbar ("#following-constrain", "#following-list", "#following-slider");
    // $("#secret-message").text ("wh: " + $(window).height() + " fc-top: " + $("#following-constrain").offset().top + " fl-h: " + $("#following-list").height() + " h: " + h + " slider-h: " + $("#following-slider").height());
    log ('following scrollbar activated');
    }
  }

function curator_ch_click (id)
  {
  log ('curator ch click: ' + id);
  id = id.replace (/^curator-ch-/, '');
  var channel = curator_ch_stack [id];
  if (channel ['id'].match (/^f-/))
    {
    notice_ok (thumbing, "This channel is not yet playable. Please mark an episode as a Favorite first.", "");
    }
  else
    {
    player_stack = curator_ch_stack;
    var to_be_played = player_stack [parseInt (id)]['id'];
    /* remove any magic channels */
    for (var i = 1; i < player_stack.length; i++)
      {
      if (player_stack [i]['id'].match (/^f-/))
        player_stack.splice (i, 1);
      }
    /* now find our place again */
    for (var i = 1; i < player_stack.length; i++)
      {
      if (player_stack [i]['id'] == to_be_played)
        id = i;
      }
    player ('curator-ch', parseInt (id));
    }
  }

function curator_follow_click (id)
  {
  log ('curator follow click: ' + id);
  id = id.replace (/^curator-follow-/, '');

  if (username != 'Guest' && current_curator_page == curatorid)
    {
    var channel = curator_follow_stack [id];
    var grid = first_position_with_this_id (channel ['id']);
    if (grid)
      {
      player ("guide", grid);
      return;
      }
    }

  player_stack = curator_follow_stack;
  player ('curator-follow', parseInt (id));
  }

function curator_follow_follow (id)
  {
  log ('curator follow Follow click: ' + id);
  id = id.replace (/^curator-follow-/, '');
  var channel = curator_follow_stack [id];

  /* callback */
  function ccff_after()
    {
    var funf = ( first_position_with_this_id (channel ['id']) > 0 ) ? translations ['unfollow'] : translations ['follow'];
    $("#curator-follow-" + id + " .btn-action span").html (funf);
    $("#curator-follow-" + id + " .btn-action").show();
    }

  if (first_position_with_this_id (channel ['id']) > 0)
    {
    log ("UNFOLLOW: " + channel ['id']);
    unfollow (channel ['id'], ccff_after);
    }
  else
    {
    log ("FOLLOW: " + channel ['id']);
    browse_accept (channel ['id'], ccff_after);
    }
  }

function curator_ch_execute_follow (id)
  {
  id = id.replace (/^curator-ch-/, '');
  log ('curator follow click: ' + id);
  $("#curator-ch-" + id + " .btn-action").hide();
  var channel = curator_ch_stack [id];

  /* callback */
  function ccef_after()
    {
    var funf = ( first_position_with_this_id (channel ['id']) > 0 ) ? translations ['unfollow'] : translations ['follow'];
    $("#curator-ch-" + id + " .btn-action span").html (funf);
    $("#curator-ch-" + id + " .btn-action").show();
    }

  if (username != 'Guest' && current_curator_page == curatorid)
    {
    if (channel ['id'].match (/^f-/))
      notice_ok (thumbing, "This channel is not yet editable. Please mark an episode as a Favorite first.", "");
    else
      seamless_exit ("/cms/episode-list.html?id=" + channel ['id']);
    return;
    }

  pop_with = "";

  if (first_position_with_this_id (channel ['id']) > 0)
    {
    log ("UNFOLLOW: " + channel ['id']);
    unfollow (channel ['id'], ccef_after);
    }
  else
    {
    log ("FOLLOW: " + channel ['id']);
    browse_accept (channel ['id'], ccef_after);
    }
  }

var browse_stack = [];
var current_browse_cat;
var current_browse_index;
var current_browse_tag;

function browse()
  {
  fresh_layer ("browse");
  thumbing = 'browse';
  $("#tag-list, #ch-list").html ('');
  set_hash ("#!directory");
  stop_all_players();

  if (false)
    {
    /* Temporary: retrieving by &lang=zh on the server will give us incorrect ID's, so use a saved
       copy of the English top level categories and perform in-player translations on them */
    process_top_level_categories (gblocks [7]);
    }

  browse_init_clicks();
  browse_inner();
  }

function browse_inner()
  {
  browse_category (1);
  }

function browse_init_clicks()
  {
  $("#categ-list li").unbind();
  $("#categ-list li").click (function() { browse_top_category_click ($(this).attr ("id")); });

  $("#sort-list li").unbind();
  $("#sort-list li").click (function()
    {
    $("#sort-list li").removeClass ("on");
    $(this).addClass ("on");
    browse_fill_content (current_browse_cat, current_browse_tag);
    });
  }

function browse_top_category_click (id)
  {
  id = id.replace (/^category-/, '');
  log ('browse top category click: ' + id);
  browse_category (parseInt (id));
  }

function browse_category (index)
  {
  var cat = top_level_categories [index];
  if (true /* !cat ['fetched'] */)
    {
    /* always force a query to the server right now */

    cat ['tags'] = [];
    cat ['content'] = [];

    $("#waiting-layer").show();
    var query = "/playerAPI/categoryInfo?category=" + cat ['id'] + '&' + 'lang=' + sphere + rx();
    var d = $.get (query, function (data)
      {
      $("#waiting-layer").hide();
      var blocks = data.split (/^--$/m);
      // blocks[] 0: return code, 1: kv pairs, 2: tags, 3: channels

      /* popular tags */
      var lines = blocks [2].split ('\n');
      for (var i = 0; i < lines.length; i++)
        {
        if (lines [i] != '')
          cat ['tags'].push (lines [i]);
        }

      /* channels in category */
      var count = 0;
      var lines = blocks [3].split ('\n');
      for (var i = 0; i < lines.length; i++)
        {
        if (lines [i] != '')
          {
          var channel = line_to_channel (lines[i]);
          pool [channel ['id']] = channel;
          cat ['content'][++count] = channel;
          cat ['count'] = count;
          }
        }
      cat ['fetched'] = true;
      browse_category_inner (index, cat);
      });
    }
  else
    browse_category_inner (index, cat);
  }

function browse_all_with_tag (tag)
  {
  /* browses the ALL category for this tag */
  var all = top_level_categories [1];

  var cat = { id: all ['id'], name: all ['name'], count: 0, inside: 0, fetched: false, content: [], tags: [] };

  var query = '/playerAPI/tagInfo?name=' + encodeURIComponent (tag) + '&' + 'lang=' + sphere + rx();
  log ('browse_all_with_tag: ' + query);
  var d = $.get (query, function (data)
    {
    var blocks = data.split (/^--$/m);

    cat ['content'] = [];

    /* channels in category */
    var count = 0;
    var lines = blocks [2].split ('\n');
    for (var i = 0; i < lines.length; i++)
      {
      if (lines [i] != '')
        {
        var channel = line_to_channel (lines[i]);
        pool [channel ['id']] = channel;
        cat ['content'][++count] = channel;
        cat ['count'] = count;
        }
      }
    cat ['fetched'] = true;
    browse_category_inner (1, cat, true);
    });
  }

/* index is only used for highlighting the left column category name */

function browse_category_inner (index, cat, keep_tags_flag)
  {
  var html;

  if (cat ['count'] < 1)
    {
    notice_ok (thumbing, 'No channels in this category', '');
    return;
    }

  if (cat ['content'].length < 1)
    {
    notice_ok (thumbing, 'Category has count: ' + cat ['count'] + ', but no channels in list!', '');
    return;
    }

  browse_stack = [];
  current_browse_tag = undefined;
  current_browse_cat = cat;
  current_browse_index = index;

  $("#categ-list li").removeClass ("on");
  $("#category-" + index).addClass ("on");

  if (!keep_tags_flag)
    {
    html = '';
    for (var i in cat ['tags'])
      {
      if (cat ['tags'].hasOwnProperty (i))
        html += '<li id="tag-' + i + '"><p><span>' + cat ['tags'][i] + '</span></p></li>';
      }
    $("#tag-list").html (html);
    }

  $("#tag-list li").unbind();
  $("#tag-list li").click (function() { browse_tag_click ($(this).attr ("id")); });

  if (!keep_tags_flag)
    $("#tag-list li").removeClass ("on");

  /* at first, all channels */
  browse_fill_content (cat, undefined);
  }

function browse_fill_content (cat, tag)
  {
  var count = 0;
  var html = '';

  current_tag = tag;

  browse_stack = [];
  $("#ch-list").html ("");

  if (!cat || !cat ['content'])
    {
    log ('browse_fill_content: category contains nothing');
    return;
    }

  /* array starts at zero, prior to sorting */
  for (var i = 1; i <= cat ['count']; i++)
    {
    var channel = cat ['content'][i];

    if (tag)
      {
      var matches = 0;
      var fields = channel ['tags'].split (',');
      for (var j in fields)
        {
        if (fields[j] == tag)
          matches++;
        }
      if (matches < 1) continue;
      }
    browse_stack [count++] = channel;
    }

  switch ($("#sort-list li.on").attr("id"))
    {
    case 'sort-by-update':
      log ('sort by update');
      browse_stack = browse_stack.sort (function (a,b) 
        { return Math.floor (b ['timestamp']) - Math.floor (a ['timestamp']); });
      break;

    case 'sort-by-alpha':
      log ('sort by alpha');
      /* the monkey business with 'r' is necessary in Chrome, believe it or not */
      browse_stack = browse_stack.sort (function (a,b) 
        { var r = a['name'].localeCompare (b['name']); if (r < 0) return -1; if (r > 0) return 1; return 0; });
      break;

    case 'sort-by-sub':
      log ('sort by subscriptions');
      browse_stack = browse_stack.sort (function (a,b) 
        { return Math.floor (a ['subscriptions']) - Math.floor (b ['subscriptions']); });
      break;
    }

  /* now make array start at 1, to match html */
  browse_stack.unshift ('');

  for (var i = 1; i <= count; i++)
    {
    var channel = browse_stack [i];
    var subbed = (first_position_with_this_id (channel ['id']) > 0);
    var funf = subbed ? translations ['unfollow'] : translations ['follow'];
    var plural = (channel ['count'] == 1) ? translations ['episode'] : translations ['episodes'];
    if (subbed)
      html += '<li id="channel-' + i + '" class="followed">';
    else
      html += '<li id="channel-' + i + '">';
    html += '<p class="channel-title">' + channel ['name'] + '</p>';
    html += '<p class="btn-action"><span>' + funf + '</span></p>';
    html += '<div class="thumb">';
    html += '<p class="icon-pl"></p>';
    html += '<img src="' + channel ['thumb1'] + '" class="thumb1 th">';
    html += '<img src="' + channel ['thumb2'] + '" class="thumb2 th">';
    html += '<img src="' + channel ['thumb3'] + '" class="thumb3 th">';
    html += '</div>';
    html += '<p class="followed-curator-photo"><img src="' + channel ['curatorthumb'] + '"></p>';
    html += '<div class="channel-meta">';
    html += '<p>' + channel ['count'] + ' ' + plural + '<span class="divider">|</span>' + ageof (channel ['timestamp'], true) + '</p>';
    // html += '<p class="next-update">Next Update: Oh! Whenever</p>';
    html += '</div>';
    html += '<p class="followed-curator">' + translations ['curatorby'] + '<span>' + channel ['curatorname'] + '</span></p>';
    html += '</li>';
    }
  $("#ch-list").html (html);

  browse_fill_clicks();

  $("#browse-constrain").css ({ height: $(window).height() - $("#browse-main").offset().top });
  $("#browse-slider, #browse-constrain").css ({ height: $("#browse-constrain").height() - 15 });
  scrollbar ("#browse-constrain", "#browse-list", "#browse-slider");
  }

function browse_fill_clicks()
  {
  $("#ch-list li .th, #ch-list li .icon-pl, #ch-list li .btn-action, #ch-list li .followed-curator-photo, #ch-list li .followed-curator").unbind();
  $("#ch-list li .th, #ch-list li .icon-pl, #ch-list li .btn-action, #ch-list li .followed-curator-photo, #ch-list li .followed-curator").css ("cursor", "pointer");

  $("#ch-list li .th, #ch-list li .icon-pl").click (function() { browse_channel_click ($(this).parent().parent().attr ("id")); });

  $("#ch-list li .btn-action").click (function() 
    { $(this).unbind(); $(this).hide(); browse_follow_click ($(this).parent().attr ("id")); });

  $("#ch-list li .followed-curator-photo, #ch-list li .followed-curator").click (function()
    { browse_channel_curator_click ($(this).parent().attr ("id")); });

  browse_init_clicks();
  }

function browse_channel_click (id)
  {
  log ('browse channel click: ' + id);
  id = id.replace (/^channel-/, '');
  player_stack = browse_stack; 
  player ('browse', id);
  }

function browse_follow_click (id)
  {
  log ('browse follow click: ' + id);
  id = id.replace (/^channel-/, '');
  var channel = browse_stack [id];
  pop_with = "";
  if (first_position_with_this_id (channel ['id']) > 0)
    {
    /* unfollow */
    unfollow (channel ['id'], function() { after_browse_follow_click (id); });
    }
  else
    {
    /* follow */
    browse_accept (channel ['id'], 'after_browse_follow_click(' + id + ')');
    }
  }

function after_browse_follow_click (id)
  {
  var channel = browse_stack [id];
  var subbed = (first_position_with_this_id (channel ['id']) > 0);
  var funf = subbed ? translations ['unfollow'] : translations ['follow'];
  if (subbed)
    $("#ch-list #channel-" + id).addClass ("followed");
  else
    $("#ch-list #channel-" + id).removeClass ("followed");
  $("#ch-list #channel-" + id + " .btn-action span").text (funf);
  $("#ch-list #channel-" + id + " .btn-action").show();
  /* event may have been unbound by .hide() */
  browse_fill_clicks();
  }

function browse_channel_curator_click (id)
  {
  log ('browse channel curator click: ' + id);
  id = id.replace (/^channel-/, '');
  var channel = browse_stack [id];
  curation (channel ['curatorid']);
  }

function browse_tag_click (id)
  {
  $("#tag-list li").removeClass ("on");
  $("#" + id).addClass ("on");
  var tag = $("#" + id + " p span").text();
  // var tag = current_browse_cat ['tags'][id];
  id = id.replace (/^tag-/, '');
  log ('browse tag click: ' + id + ' tag: ' + tag);
  /* browse_fill_content (current_browse_cat, tag) */
  browse_all_with_tag (tag);
  }

/* GB */
function redraw_ipg_events()
  {
  $("#guide-holder li.in").unbind();
  $("#guide-holder li.in").mouseover (function()
    {
    var group = $(this).parents("ul").children("li");
    var grid = $(this).attr("id").replace (/^grid-/, '');
    var channel = channelgrid [grid];
    log ('mouseover ipg square: ' + grid);
    if (channel ['count'] < 1)
      {
      log ('channel blank: ' + grid);
      $("#bubble-normal").hide();
      $("#bubble-default").show();
      }
    else
      {
      $("#bubble-default").hide();
      $("#bubble-normal").show();
      }

    var plural = (channel ['count'] == 1) ? translations ['episode'] : translations ['episodes'];

    $("#guide-bubble .thumbnail1").attr ("src", channel ['thumb1']);
    $("#guide-bubble .thumbnail2").attr ("src", channel ['thumb2']);
    $("#guide-bubble .thumbnail3").attr ("src", channel ['thumb3']);
    $("#guide-bubble h3 span").html (channel ['name']);
    $("#ch-meta-count").html (channel ['count'] + ' ' + plural);
    $("#ch-meta-ago").html (ageof (channel ['timestamp'], true));
    $("#ch-meta-curator").html (channel ['curatorname']);
    $("#ch-brief span").html (channel ['desc']);

    $("#bubble-normal h3 span, #bubble-default h3 span").html (channel ['name']);

    $("#bubble-normal #ch-meta p span").eq(0).html (channel ['count'] + ' Episodes');
    $("#bubble-normal #ch-meta p span").eq(1).html (ageof (channel ['timestamp'], true));
    $("#bubble-normal #ch-meta p .name").html (channel ['curatorname']);

    $("#bubble-normal .thumbnail1").attr ("src", channel ['thumb1']);
    $("#bubble-normal .thumbnail2").attr ("src", channel ['thumb2']);
    $("#bubble-normal .thumbnail3").attr ("src", channel ['thumb3']);

    $("#guide-bubble #ch-meta p span").eq(2).html (translations ['curatorby']);

    /* if the screen is a magic size, the guide bubble is to vanish, and fixed-location guide-bubble-l used */
    if ($("#guide-bubble-l").css ("display") == 'block')
      $("#guide-bubble").hide();
    else
      $("#guide-bubble").show();

    ellipses();

    var order = $(group).index(this)+1;
    $("#guide-bubble .bg").hide();
    switch (order)
      {
      case 1:
      case 2: 
      case 3: guide_bubble_1 (this); 
              break;
      case 4: 
      case 5: 
      case 6: guide_bubble_2 (this);
              break;
      case 7: 
      case 8: 
      case 9: guide_bubble_3 (this);
              break;
      }
    }).mouseout (function() 
      {
      var grid = $(this).attr("id").replace (/^grid-/, ''); 
      log ('mouseout: ' + grid);
      guide_hide_bubble();
    }).click (function()
      {
      if (!$(this).hasClass ("noclick"))
        {
        var grid = $(this).attr ("id").replace (/^grid-/, '');
        player ("guide", grid);
        }
      });

  $("#guide-holder li.in .btn-del-ch").unbind();
  $("#guide-holder li.in .btn-del-ch").click (function (event)
    {
    event.stopPropagation();
    ipg_cursor = $(this).parent().attr("id").replace (/^grid-/, "");
    log ("delete channel from grid: " + ipg_cursor);
    unsubscribe_channel();
    });

  setup_draggables();
  ellipses();
  }
  
function guide_bubble_1 (obj)
  {
  var bloc = $(obj).children(".thumbnail").offset();
  var bw = $(obj).children(".thumbnail").width();
  var bl = bloc.left+bw+3;
  var bt = bloc.top;	
  // $("#guide-bubble").show().css ({"left":bl,"top":bt});
  $("#guide-bubble").css ({"left":bl,"top":bt});
  $("#guide-bubble #bg1").show();
  }
  
function guide_bubble_2 (obj)
  {
  var bloc = $(obj).children(".thumbnail").offset();
  var bw = $(obj).children(".thumbnail").width();
  var bh = $(obj).children(".thumbnail").height();
  var bbh = $("#guide-bubble").height();
  var bl = bloc.left+bw+3;
  var bt = bloc.top+bh/2-bbh/2+(0.1875*16);	
  // $("#guide-bubble").show().css ({"left":bl,"top":bt});
  $("#guide-bubble").css ({"left":bl,"top":bt});
  $("#guide-bubble #bg2").show();
  }
  
function guide_bubble_3 (obj)
  {
  var bloc = $(obj).children(".thumbnail").offset();
  var bw = $(obj).children(".thumbnail").width();
  var bh = $(obj).children(".thumbnail").height();
  var bbh = $("#guide-bubble").height();
  var bl = bloc.left+bw+3;
  var bt = bloc.top+bh-bbh+(0.3125*16);	
  // $("#guide-bubble").show().css ({"left":bl,"top":bt}); 
  $("#guide-bubble").css ({"left":bl,"top":bt}); 
  $("#guide-bubble #bg3").show();
  }

function guide_hide_bubble()
  {
  $("#guide-bubble").hide().css ({"left":"-100","top":"-100"});
  }

function fresh_layer (id)
  {
  $(".stage").hide();
  $("#" + id + "-layer").show();
  $("#nav li").removeClass ("on");
  $("#" + id).addClass ("on");
  header();
  clearTimeout (guide_timex);
  clearTimeout (home_timex);
  current_episode_phase = 0;
  }

function home()
  {
  fresh_layer ("home");
  thumbing = 'home';
  $("#home-layer").css ("top", "0px");
  $("#homerightbox").css ("padding-bottom", "60px");
// $("#home-list").css ("padding-bottom", "60px");
  home_trending();
  home_subscriptions();
  home_right_column();
  stop_all_players();
  set_hash ("#!home");
  var h = $(window).height() - $("#home-constrain").offset().top;
// $("#secret-message").text ("wh: " + $(window).height() + " home-constrain.top: " + $("#home-constrain").offset().top + " home-constrain.h: " + $("#home-constrain").height() + " homerightbox.h: " + $("#homerightbox").height());
  $("#home-constrain").css ({ height: $(window).height() - $("#home-constrain").offset().top });
  $("#home-slider").css ({ height: $(window).height() - $("#home-constrain").offset().top - 20 });
  if (typeof (scrollbar_HOME) == 'function')
    scrollbar_HOME ("#home-constrain", "#home-list", "#home-slider");
  else
    scrollbar ("#home-constrain", "#home-list", "#home-slider");
  start_home_autorotation();
  }

var home_timex;

function start_home_autorotation()
  {
  clearTimeout (home_timex);
  home_timex = setTimeout ("autorotate_home()", 6000);
  }

function autorotate_home()
  {
  clearTimeout (home_timex);
  if (thumbing == 'home')
    {
    home_trending_arrow_down();
    rotate_banner();
    home_timex = setTimeout ("autorotate_home()", 6000);
    }
  }

function rotate_banner()
  {
  var banidx = $("#banner li:visible").index() + 1;
  if (banidx > 3) banidx = 0;
  $("#banner li").hide();
  $("#banner li").eq (banidx).show();
  $("#home-layer .app-store").unbind();
  $("#home-layer .app-store").click (function()
    {
    window.open ('https://itunes.apple.com/qa/app/9x9.tv/id443352510', '_blank');
    });
  }

var signin_saved_thumbing;

function new_signup (callback)
  {
  signin_saved_thumbing = thumbing;
  thumbing = 'signin';
  after_sign = callback;
  $("#signin-layer").show();
  $("#signin-layer").css ("top", "0px");
  pw_error_reset();
  signup_field_init (callback);
  pause_and_mute_everything();
  }

function signup_field_init (callback)
  {
  input_init (translations ['email'], "#return-email");
  input_init (translations ['yourname'], "#signup-name");
  input_init (translations ['email'], "#signup-email");
  
  input_pw_init ("#return-password");
  input_pw_init ("#signup-password");
  input_pw_init ("#signup-password2");

  $("#btn-signin-close").unbind();
  $("#btn-signin-close").click (signin_close);

  $("#btn-home-sign-in").unbind();
  $("#btn-home-sign-in").click (function() 
    {
    new_submit_login (function()
      {
      thumbing = signin_saved_thumbing;
      redraw_store_add_button();
      if (typeof (callback) == 'function')
        callback();
      else
        eval (callback);
      });
    });

  $("#btn-home-create-account").unbind();
  $("#btn-home-create-account").click (verify_and_submit_signup);

  $("#btn-home-sign-in-fb").unbind();
  $("#btn-home-sign-in-fb").click (function()
    {
    log ('redirect to facebook login! bye');
    seamless_exit ('/playerAPI/fbLogin');
    });

  $("#signin-layer .forgot").unbind();
  $("#signin-layer .forgot").click (forgot);

  $("#signup-checkbox").removeClass ("on");
  $("#signup-checkbox").unbind();
  $("#signup-checkbox").click (function() { pw_error_reset(); $(this).toggleClass ("on"); });
  }

function verify_and_submit_signup()
  {
  if (! ($("#signup-checkbox").hasClass ("on")))
    {
    pw_signup_error (translations ['plzaccept']);
    return;
    }

  new_submit_signup (function()
    {
    thumbing = signin_saved_thumbing;
    redraw_store_add_button();
    if (thumbing == 'home')
      home();
    });
  }

function pw_signin_error (text)
  {
  pw_error_reset();
  $("#signin-layer .signin-email-error").html (text);
  $("#signin-layer .signin-email-error").show();
  }

function pw_signup_error (text)
  {
  pw_error_reset();
  $("#signin-layer .signup-message-error").html (text);
  $("#signin-layer .signup-message-error").show();
  }

function forgot_pw_error (text)
  {
  pw_error_reset();
  $("#forgot-password-layer .msg-error").html (text);
  $("#forgot-password-layer .msg-error").show();
  }

function pw_error_reset()
  {
  $("#signin-layer .signin-email-error").hide();
  $("#signin-layer .signup-message-error").hide();
  $("#forgot-password-layer .msg-error").hide();
  /* why was this made invisible? */
  $("#forgot-password-layer .msg-error").css ("visibility", "visible");
  }

function signin_close()
  {
  thumbing = signin_saved_thumbing;
  $("#signin-layer, #forgot-password-layer").hide();
  }

function forgot()
  {
  pw_error_reset();

  $("#forgot-password-msg").hide();
  $("#forgot-password-layer").show();

  input_init (translations ['email'], "#forgot-pw");

  $("#btn-forgot-password-close, #forgot-password-layer .back-to-sign").unbind();
  $("#btn-forgot-password-close, #forgot-password-layer .back-to-sign").click (function()
    {
    $("#forgot-password-layer").hide();
    });

  $("#btn-forgot-password-reset").unbind();
  $("#btn-forgot-password-reset").click (function()
    {
    var email = $("#forgot-pw").val();
    if (email.match (/\@/))
      {
      var query = '/playerAPI/forgotpwd?email=' + email + '&' + rx();
      var d = $.get (query, function (data)
        {
        var lines = data.split ('\n');
        var fields = lines[0].split ('\t');
        if (fields[0] == '0')
          {
          $(".forgot-password-msg .msg").html ("Success!<br>You should receive an email shortly.<br>" +
                 'If you still need assistance, please <a href="#!about=contact">contact us</a>.');
          $(".forgot-password-msg").show();
          }
        else
          forgot_pw_error ("Error: " + fields[1]); 
        });
      }
    else
      forgot_pw_error ("Please enter your e-mail");
    });
  }

function input_init (hint, input)
  {
  $(input).val (hint);
  $(input).focus (function()
    {
    txt = $(this).val();
    if (txt == hint)
        $(this).val ("");
    pw_error_reset();
    })
  .blur (function()
    {
    txt = $(this).val();
    if (txt == "")
      $(this).val (hint);
    });
  }

function input_pw_init (field)
  {
  $(field).val ('');
  $(field).focus (function()
    {
    $(field).siblings (".hint").hide();
    pw_error_reset();
    })
  .blur (function()
    {
    txt = $(field).val();
    if (txt == "")
      $(field).siblings (".hint").show();
    });
  }

var history_timex;

function add_to_history (real_channel)
  {
  player_history_ids.push (real_channel);

  if (history_timex)
    clearTimeout (history_timex);
  history_timex = setTimeout ("freshen_recommended()", 3000);

  return real_channel;
  }

function freshen_recommended()
  {
return; /* temporarily disabled */
  var most_recent_channel = player_history_ids [player_history_ids.length - 1];
  var query = '/playerAPI/channelStack?stack=recommend' + '&' + 'user=' + user + '&' + 'channel=' + most_recent_channel + rx();
  log ("freshen recommended: " + query);
  var d = $.get (query, function (data)
    {
    process_channel_stack ('recommended', data);
    });
  }

var up_parms = { prefix: 'player', type: 'image', size: 1024 * 1024, acl: 'public-read' };
var up_s3attr;
var up_timestamp;

function upload_thumb_setup()
  {
  var parms = { prefix: 'player', type: 'image', size: 1024 * 1024, acl: 'public-read' };
  var d = $.get ('/api/s3/attributes?' + jQuery.param (parms), function (data)
    {
    up_s3attr = data;
    up_timestamp = (new Date()).getTime();
    // $("#upload-buttonry").remove();
    // $("#right").append ('<p id="upload-buttonry" style="position: relative; top: 300px"></p>');
    up_init();
    });
  }

function up_file_start()
  {
  log ("upload file start");
  }

function up_progress (file, completed, total)
  {
  log ('upload progress :: name:' + file.name + ' completed:' + completed + ' total:' + total);
  $("#imagebox-upload-box .imagebox-upload-wrap-p").html (file.name + "<br><br><br>");
  var pct = 100 * completed / total;
  $("#imagebox-upload-wrap #bar").css ("width", (2 * pct) + "%");
  $("#per").html (pct + "%");
  }

function load_constrained_image (id, url, callback)
  {
  var img = new Image();
  img.onload = function()
    {
    if (img.width > img.height)
      $(id).css ({ width: "100%", height: "auto", 'margin-left': "auto", 'margin-right': "auto" });
    else
      $(id).css ({ width: "auto", height: "100%", 'margin-left': "auto", 'margin-right': "auto" });
    $(id).attr ("src", img.src);
    if (callback) callback();
    };
  img.src = url;
  }

function up_success (file, data, response)
  {
  var url = 'http://' + up_s3attr ['bucket'] + '.s3.amazonaws.com/' + up_parms ['prefix'] + '-' + up_parms ['type'] + '-' + up_timestamp + file.type;

  log ("upload success, url: " + url);

  load_constrained_image (".profile181 img", url);
  $("#imagebox-upload-box").hide();

  var d = $.get ('/playerAPI/setUserProfile?user=' + user + '&' + 'key=image' + '&' + 'value=' + url + rx(), function (data)
    {
    var lines = data.split ('\n');
    var fields = lines[0].split ('\t');
    if (fields [0] != '0')
      notice_ok (thumbing, 'Error saving thumbnail to your profile: ' + fields[1], "");
    });
  }

function up_error (file, code, message)
  {
  if (code == -200)
    log ("user cancelled");
  else
    log ("upload failure: " + message);
  }

/* see dox at: http://demo.swfupload.org/Documentation/ */

function up_file_queue (file)
  {
  if (!file.type)
    file.type = nn.getFileTypeByName(file.name);
			
  var filename = up_parms ['prefix'] + '-' + up_parms ['type'] + '-' + up_timestamp + file.type;

  var postParams =
    {
    AWSAccessKeyId: up_s3attr ['id'],
    key: filename,
    acl: up_parms ['acl'],
    policy: up_s3attr ['policy'],
    signature: up_s3attr ['signature'],
    'content-type': up_parms ['type'],
    success_action_status: "201"
    };

  log ('start upload: ' + filename);

  swfupload.setPostParams (postParams);
  swfupload.startUpload (file.id);
  swfupload.setButtonDisabled (true);

  $("#imagebox-upload-box").show();
  $("#imagebox-upload-wrap #bar").css ("width", "0%");
  $("#imagebox-upload-box .imagebox-upload-wrap-p").html ("");
  $("#per").html ("");
  }

function up_file_queue_error (file, code, message)
  {
  log ("upload file queue error: " + code);
  }

var usettings;
var swfupload;
function up_init()
  {
  var settings =
    {
    flash_url: '/cms/javascripts/swfupload/swfupload.swf',
    upload_url: 'http://' + up_s3attr ['bucket'] + '.s3.amazonaws.com/',
    file_size_limit: up_parms ['size'],
    file_types: '*.jpg; *.jpeg; *.png; *.gif; *.JPG; *.JPEG; *.PNG; *.GIF',
    file_types_description: 'Thumbnail',
    file_post_name: 'file',
    button_placeholder: $('#btn-upload').get(0),
    button_image_url: '/cms/images/btn-upload.png',
    button_width: '129',
    button_height: '29',
    button_text: '<span class="uploadstyle">' + translations ['uploadimage'] + '</span>',
    button_text_style: '.uploadstyle { color: #777777; font-family: Helvetica; font-size: 15px; text-align: center; top: 300px } .uploadstyle:hover { color: #999999; }',
    button_action: SWFUpload.BUTTON_ACTION.SELECT_FILE,
    button_cursor: SWFUpload.CURSOR.HAND,
    button_window_mode: SWFUpload.WINDOW_MODE.OPAQUE,
    http_success: [ 201 ],
    file_dialog_start_handler: up_file_start,
    upload_progress_handler: up_progress,
    upload_success_handler: up_success,
    upload_error_handler: up_error,
    file_queued_handler: up_file_queue,
    file_queue_error_handler: up_file_queue_error,
    debug: true
    };

usettings = settings;
  swfupload = new SWFUpload (settings);
  }
