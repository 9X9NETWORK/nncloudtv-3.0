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

url = "http://localhost:8080/playerAPI/flush"
urllib2.urlopen(url).read()

