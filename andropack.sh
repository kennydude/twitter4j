#!/bin/sh
LANG=C

echo "Twitter4J Android Packager"
echo "Prettified by @kennydude"
echo "--------------------------"

mkdir twitter4j-build

build(){
	cp -r twitter4j-$1 twitter4j-build
	
	echo "Converting to Android JSON..."
	if [ -d twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/org ]
	then
		rm -Rf twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/org
	fi
	
	cd twitter4j-build/twitter4j-$1
	find . -type f |while read file; do sed -e 's/import twitter4j.internal.org.json/import org.json/' $file > $file.tmp && mv $file.tmp $file; done
	sed -i "" -e 's/<dependencies>/<dependencies><dependency><groupId>org.json<\/groupId><artifactId>json<\/artifactId><version>20090211<\/version><scope>provided<\/scope><\/dependency>/' pom.xml
	
	echo "Building $1..."
	mvn clean compile jar:jar -Dmaven.test.skip=true

	cp target/twitter4j-$1-*.jar ../../
	cd ../..
}

echo "> Core"

build "core"

echo "> Cleanup"
rm -rf twitter4j-build
echo "> Done :)"
