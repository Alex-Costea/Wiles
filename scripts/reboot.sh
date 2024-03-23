cd ..
sudo killall -9 java
git pull
sh setup_server.sh
cd "wiles-web-backend/target"
sudo --preserve-env=PATH env nohup java -jar -Dspring.profiles.active=prod Wiles-Web-Backend-0.0.1-SNAPSHOT.jar  &
