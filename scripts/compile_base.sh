cd ..
cd wiles-base
mvn clean
mvn kotlin:compile
mvn install -Dproject.build.sourceDirectory=out
