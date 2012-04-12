#ec2 devel deployment script, not intended for production
clear
echo "install"
md5sum root.war
service jetty stop
rm /usr/share/jetty/webapps/root.war
cp root.war /usr/share/jetty/webapps/root.war
service jetty start
sleep 3
wget http://localhost:8080/version/current
sleep 1
cat current
rm current
