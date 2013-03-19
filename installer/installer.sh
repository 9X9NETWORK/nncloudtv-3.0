o "install"
sudo service jetty stop
sudo service memcached stop
cd nncloudtv/
mvn clean compile
mvn datanucleus:enhance
mvn war:war
cd ../
cd nncms/
mvn clean compile war:war
cd ../
sudo cp nncloudtv/target/root.war /usr/share/jetty/webapps/root.war
sudo cp nncms/target/cms.war /usr/share/jetty/webapps/cms.war
sudo service jetty start
sudo service memcached start
