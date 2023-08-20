rm -d -r "wiles-web-frontend/dist/wiles-web-frontend/"
rm -d -r "Wiles Web Backend/src/main/resources/static"
cd wiles-web-frontend
ng build --base-href /
cd ..
cp -R "wiles-web-frontend/dist/wiles-web-frontend/" "Wiles Web Backend/src/main/resources/static/"