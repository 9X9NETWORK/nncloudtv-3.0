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

cd ..

mvn -Dmaven.test.skip=true clean\
    compile \
    datanucleus:enhance \
    install war:war \
&& test "$1" != "-n" \
&& cp -v target/nncloudtv-0.0.1-SNAPSHOT.war target/root.war

