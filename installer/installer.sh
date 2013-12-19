#!/bin/sh
#
# installer.sh - developement installer script
#

script_path=`readlink -f "$0"`
script_dir=`dirname "$script_path"`
cd "$script_dir"

cd ..
sudo service jetty stop
sudo service memcached stop
mvn clean compile
mvn datanucleus:enhance
mvn install
mvn war:war
sudo cp target/root.war /usr/share/jetty/webapps/root.war
sudo service jetty start
sudo service memcached start

