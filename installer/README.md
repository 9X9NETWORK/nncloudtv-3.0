Build and upload procedures
===========================

Build and upload
----------------

go to build.9x9.tv

````bash
[ubuntu@build]$ cd /home/ubuntu/files/nncloudtv
[ubuntu@build]$ svn update
[ubuntu@build]$ cd installer
[ubuntu@build]$ python build.py # build war file, the script will prompt options 
[ubuntu@build]$ python upload.py
````

[upload to desired machine, the script will prompt options] 
[ps. on production deployment machine(moveout-log.9x9.tv), files are placed under /var/www/updates]

Deploy
------

[alpha, alpha_log.9x9.tv]

````bash
[ubuntu@alpha-log]$ cd /home/ubuntu/bin
[ubuntu@alpha-log]$ deploy_war.sh
````

[stage, stage.9x9.tv]

````bash
[ubuntu@stage]$ cd /home/ubuntu/files
[ubuntu@stage]$ bash -x ./installer.sh 
````

[production deploy machine, moveout-log.9x9.tv]

````bash
[ubuntu@moveout-log]$ cd /home/ubuntu/bin
[ubuntu@moveout-log]$ deploy_all_wars.sh
````

Verify
------

go to **http://{{domain-name}}/version/current**

