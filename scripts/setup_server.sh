#!/bin/sh
sh compile_base.sh
cd ..
rm -d -r wiles-web-frontend/build/
rm -d -r wiles-web-backend/src/main/resources/static
cd wiles-web-frontend  || exit
npm install
npm run build
cd ..
cp -R wiles-web-frontend/build/ wiles-web-backend/src/main/resources/static/
cd wiles-web-backend  || exit
mvn clean install