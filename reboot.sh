sudo killall -9 java
cd "Wiles Web Backend/target"
sudo nohup java -jar -Dspring.profiles.active=prod Wiles-Web-Backend-0.0.1-SNAPSHOT.jar  &
