#!/bin/sh
#
# installer.sh - build & deploy for develpement/test site
#

jetty="jetty"
script_path=`readlink -f "$0"`
script_dir=`dirname "$script_path"`
cd "$script_dir"

echo
echo "*************************"
echo "*                       *"
echo "*  9x9 DevOp Installer  *"
echo "*                       *"
echo "*************************"
echo
echo -n "Checking your sudo permission ... "
sudo id
if test $? -eq 1; then
    echo "failed."
    exit
fi

cd ..
mvn -DskipTests \
    clean \
    compile \
    datanucleus:enhance \
    install war:war \
&& sudo cp target/root.war /usr/share/$jetty/webapps/root.war \
&& (sudo service $jetty restart; sudo service memcached restart)

