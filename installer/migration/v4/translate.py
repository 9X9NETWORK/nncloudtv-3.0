#!/usr/bin/python
# -*- coding: utf-8 -*-

def get_sphere(text):
   text = text.strip()
   if text == 'Worldwide':
      return "other"
   elif text == "US":
      return "en"
   elif text == "英":
      return "en"
   else:
      return "zh"

def get_lang(text):
   text = text.strip()
   if text == 'EN':
      return "en"
   elif text == "others":
      return "other"
   else:
      return "zh"

def get_daypartingSystagId(daypart):
   if daypart == "morning":
      return 48 
   elif daypart == "daytime":
      return 49  
   elif daypart == "slack":
      return 50
   elif daypart == "evening":
      return 51
   elif daypart == "primetime":
      return 52
   elif daypart == "latenight":
      return 53
   elif daypart == "nightowl":
      return 54

def get_tzuchi_daypartingSystagId(daypart):
   if daypart == "morning":
      return 58 
   elif daypart == "daytime":
      return 59  
   elif daypart == "slack":
      return 60
   elif daypart == "evening":
      return 61
   elif daypart == "primetime":
      return 62
   elif daypart == "latenight":
      return 63
   elif daypart == "nightowl":
      return 64

def get_systagId(meow):
   category = meow.strip()
   print "category: " + category + "."
   if category == "All":
      return 1
   elif category == "Animals & Pets ":
     return 2       
   elif category == "Art & Design ": 
     return 3
   elif category == "Autos & Vehicles ":
     return 4 
   elif category == "Cartoons & Animation ":
     return 5  
   elif category == "Comedy":
     return 6                
   elif category == "Fashion; Food & Living ":
     return 7
   elif category == "Gaming ":
     return 8 
   elif category == "How-To ":
     return 9       
   elif category == "Education & Lectures ":
     return 10 
   elif category == "Music":
     return 11 
   elif category == "News ":
     return 12 
   elif category == "Nonprofits & Faith ":
     return 13 
   elif category == "People; Blogs & Shorts ":
     return 14
   elif category == "Science ":
     return 15 
   elif category == "Sports & Health ":
     return 16 
   elif category == "Tech & Apps ":
     return 17 
   elif category == "TV & Film":
     return 18 
   elif category == "Others ":
     return 19 
   elif category == "全部 ":
     return 1 
   elif category == "寵物與動物":
     return 2 
   elif category == "藝術設計":
     return 3 
   elif category == "動力機械":
     return 4 
   elif category == "卡通動畫":
     return 5 
   elif category == "搞笑小品":
     return 6 
   elif "時尚生活與美食" in category:
     return 7 
   elif "時尚美食與生活" in category:
     return 7
   elif category == "電玩遊戲":
     return 8 
   elif category == "DIY教學":
     return 9 
   elif category == "教育講座":
     return 10 
   elif category == "音樂":
     return 11 
   elif category == "新聞":
     return 12 
   elif category == "宗教與非營利":
     return 13 
   elif category == "人物與網誌":
     return 14 
   elif category == "科學知識":
     return 15 
   elif category == "運動與健康":
     return 16 
   elif category == "科技3C":
     return 17 
   elif category == "電視與電影":
     return 18 
   elif category == "其他":
     return 19
   elif category == "華視":
     return 0
   else:
     return -1
