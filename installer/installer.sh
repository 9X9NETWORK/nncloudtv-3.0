#!/bin/sh
#
# installer.sh - build & deploy for develpement/test site
#

jetty="jetty"
script_path=`readlink -f "$0"`
script_dir=`dirname "$script_path"`

cd "$script_dir"

echo
echo "     *****************************"
echo "   **                          *"
echo " **    9x9 DevOps Installer  **"
echo "   **                          *"
echo "     *****************************"
echo
test "$1" != "-n" \
    && echo -n "Checking your sudo permission ... " \
    && sudo id \
    && if test $? -eq 1; then echo "failed."; exit; fi \
    && echo

cd ..

if test "$1" = "-x"; then # speed up
    mvn -Dmaven.test.skip=true compile war:war \
    && sudo cp -v target/root.war /usr/share/$jetty/webapps/root.war \
    && sudo service $jetty restart
    exit
fi

mvn -Dmaven.test.skip=true clean\
    compile \
    datanucleus:enhance \
    install war:war \
&& test "$1" != "-n" \
&& sudo cp -v target/root.war /usr/share/$jetty/webapps/root.war \
&& (sudo service $jetty restart; sudo service memcached restart)

