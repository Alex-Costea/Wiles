#!/bin/sh
./compile_base.sh
cd ..
rm -d -r wiles-web-frontend/dist/
rm -d -r wiles-web-backend/src/main/resources/static
cd wiles-web-frontend  || exit
npm install
npm run build
cd ..
cp -R wiles-web-frontend/dist/ wiles-web-backend/src/main/resources/static/
cd wiles-web-backend  || exit
mvn clean install