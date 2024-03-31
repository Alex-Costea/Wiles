sh setup_server.sh
cd ..
cd wiles-web-backend/target  || exit
sudo --preserve-env=PATH env java -jar Wiles-Web-Backend-DEVELOPMENT.jar
