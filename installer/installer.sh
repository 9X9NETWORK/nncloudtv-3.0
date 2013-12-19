#!/bin/sh
#
# installer.sh - developement installer script
#

script_path=`readlink -f "$0"`
script_dir=`dirname "$script_path"`
cd "$script_dir"

cd ..
mvn -DskipTests \
    clean \
    compile \
    datanucleus:enhance \
    install war:war \
&& sudo cp target/root.war /usr/share/jetty/webapps/root.war \
&& (sudo service jetty restart; sudo service memcached restart)

