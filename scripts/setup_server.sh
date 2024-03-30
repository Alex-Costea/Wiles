sh compile_base.sh
cd ..
rm -d -r "wiles-web-frontend/build/"
rm -d -r "wiles-web-backend/src/main/resources/static"
cd wiles-web-frontend
npm install
npm run build
cd ..
cp -R "wiles-web-frontend/build/" "wiles-web-backend/src/main/resources/static/"
cd "scripts"
sh update_server.sh