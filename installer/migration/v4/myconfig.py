import sys

for arg in sys.argv:
    print arg

def getSqlconfig(env):
   env = env.strip()
   print "env for sql:" + env
   if env == 'prod':
      return "letlet"
   else:
      return ""

def getUrlRoot(env):
   env = env.strip()
   print "env:" + env
   if env == 'prod':
     return "http://stage.9x9.tv"
   else:
     return "http://localhost:8080"
