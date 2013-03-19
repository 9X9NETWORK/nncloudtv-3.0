# initialize tag information
# 1) basic value for hot and thrending
# 2) give channel tags

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import string
import codecs

url = "http://stage.9x9.tv/playerAPI/flush"
urllib2.urlopen(url).read()
url = "http://stage.9x9.tv/playerAPI/programCache?channel=8600"
urllib2.urlopen(url).read()
