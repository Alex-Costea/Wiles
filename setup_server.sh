rm -d -r "wiles-web-frontend/build/"
rm -d -r "Wiles Web Backend/src/main/resources/static"
cd wiles-web-frontend
npm run build
cd ..
cp -R "wiles-web-frontend/build/" "Wiles Web Backend/src/main/resources/static/"
cd "Wiles Web Backend"
sh update_server.sh