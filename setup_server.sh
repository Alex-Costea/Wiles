rm -d -r "wiles-web-frontend/dist/wiles-web-frontend/"
rm -d -r "Wiles Web Backend/src/main/resources/static/online"
cd wiles-web-frontend
ng build --base-href /online/
cd ..
cp -R "wiles-web-frontend/dist/wiles-web-frontend/" "Wiles Web Backend/src/main/resources/static/online/"