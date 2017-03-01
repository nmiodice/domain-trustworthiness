BUILD_DIR="$PWD/build/"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"


cd config
mvn clean
mvn assembly:assembly
cp target/*jar-with-dependencies.jar $BUILD_DIR
cd ..

mvn install:install-file \
   -Dfile=$BUILD_DIR/config-1.0-SNAPSHOT-jar-with-dependencies.jar \
   -DgroupId=com.iodice \
   -DartifactId=config \
   -Dversion=1.0 \
   -Dpackaging=jar \
   -DgeneratePom=true


cd crawler
mvn clean
mvn assembly:assembly
cp target/*jar-with-dependencies.jar $BUILD_DIR
cd ..

cd webserver
mvn clean
mvn assembly:assembly
cp target/*jar-with-dependencies.jar $BUILD_DIR
cd ..
