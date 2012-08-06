/* piwik analytics */

var piwik_api_url     = piwik_host() + 'index.php';
var piwik_js_url      = piwik_host() + 'piwik.js';
var piwik_tracker_url = piwik_host() + 'piwik.php';
var piwik_auth_token  = '23ed70e585b18033d7150f917232d1f4';

var piwik_is_loaded     = false;
var piwik_tracker       = null;
var piwik_tracking_site = null;
var piwik_is_engaging   = false; // mutex

function piwik_host()
  {
  var host = "http://";
  
  host += $('#piwik').val();
  host += '/';
  
  return host;
  }

function piwik_initialize()
  {
  $.getScript(piwik_js_url, function()
    {
    piwik_is_loaded = true;
    log ('piwik script is loaded');
    piwik_log_version();
    piwik_engage();
    });
  }

function piwik_call_api (params, callback)
  {
  var api_url = piwik_api_url + '?jsoncallback=?';

  params.module = 'API';
  params.format = 'JSON';
  params.token_auth = piwik_auth_token;

  if (typeof (callback) == 'function')
    $.getJSON(api_url, params, callback);
  else
    $.getJSON(api_url, params);
  }

function piwik_log_version()
  {
  var params = { method: 'ExampleAPI.getPiwikVersion' };
  piwik_call_api (params, function (data)
    {
    log ('piwik version: ' + data.value);
    });
  }

function piwik_get_widget_url (params)
  {
  params.widget = 1;
  params.token_auth = piwik_auth_token;

  var url = piwik_api_url + '?';
  for (name in params)
    {
    url += encodeURIComponent(name) + '=' + encodeURIComponent(params[name]) + '&';
    }
  var locale = $('#locale').val();
  if (locale == 'zh' || locale == 'zh-tw')
    {
    url += 'language=zh-tw';
    }
  return url.replace(/\&$/,'');
  }

function piwik_get_site_url (channel, episode)
  {
  var url = 'http://' + location.host + '/';
  if (channel != null)
    {
    url += 'view?channel=' + channel;
    if (episode != null)
      url += '&episode=' + episode;
    }
  else
    {
    url += '9x9';
    }
  log ('piwik site url: ' + url);
  return url;
  }

function piwik_start_analytics (site, channel, episode)
  {
  if (!piwik_is_loaded)
    {
    log ('piwik ooops');
    return;
    }
  try
    {
    if (piwik_tracking_site != site)
      {
      piwik_tracker = Piwik.getTracker(piwik_tracker_url, site);
      piwik_tracking_site = site;
      log ('piwik start tracking: ' + site);
      }
    if (episode != null && channel != null)
      {
      log ('piwik track episode: ' + episode);
      var episode_url = piwik_get_site_url (channel, episode);
      piwik_tracker.setDocumentTitle (episode);
      piwik_tracker.setCustomUrl (episode_url);
      }
    piwik_tracker.trackPageView();
    }
  catch (err)
    {
    log ('piwik error: ' + err.id + ' ' + err.name);
    }
  }

function piwik_engage (channel, episode)
  {
  if (piwik_is_engaging)
    {
    log ('piwik so what?');
    return;
    }

  piwik_is_engaging = true;
  log ('piwik_engage: ' + channel + ' ' + episode);
  if (channel == null)
    {
    var virtual_url = piwik_get_site_url(); // without parameters, http://9x9.tv/9x9
    var params =
      {
      method: 'SitesManager.getSitesIdFromSiteUrl',
      url:    virtual_url
      };
    piwik_call_api (params, function (sites)
      {
      if (sites.length > 0)
        {
        piwik_start_analytics (sites[0].idsite);
        piwik_is_engaging = false;
        }
      else
        {
        var params =
          {
          method:   'SitesManager.addSite',
          urls:     virtual_url,
          siteName: '9x9'
          };
        piwik_call_api (params, function(data)
          {
          var site = data.value;
          if (site != null)
            {
            log ('piwik virtual site is created: ' + site);
            piwik_start_analytics (site);
            }
          piwik_is_engaging = false;
          });
        }
      });
    return;
    }

  var channel_url = piwik_get_site_url (channel);
  var params =
    {
    method: 'SitesManager.getSitesIdFromSiteUrl',
    url:    channel_url
    };
  piwik_call_api (params, function (sites)
    {
    if (sites.length > 0)
      {
      piwik_start_analytics (sites[0].idsite, channel, episode);
      piwik_is_engaging = false;
      }
    else
      {
      var params =
        {
        method:   'SitesManager.addSite',
        urls:     channel_url,
        siteName: channel
        };
      piwik_call_api (params, function(data)
        {
        var site = data.value;
        if (site != null)
          {
          log ('piwik site is created: ' + channel + ' -> ' + site);
          piwik_start_analytics (site, channel, episode);
          }
        piwik_is_engaging = false;
        });
      }
    });
  }
