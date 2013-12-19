clear
echo "install"
sudo service jetty stop
sudo service memcached stop
cd nncloudtv/
mvn clean compile
mvn datanucleus:enhance
mvn war:war
cd ../
sudo cp nncloudtv/target/root.war /usr/share/jetty/webapps/root.war
sudo service jetty start
sudo service memcached start
