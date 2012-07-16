#!/bin/sh
LANG=C

echo "Twitter4J Android Packager"
echo "Prettified by @kennydude"
echo "--------------------------"
echo "usage: ./andropack.sh all - Builds everything"
echo "       ./andropack.sh core - Builds core (use any module to build only it)"

mkdir twitter4j-build

build(){
	echo "> Building $1..."
	cp -r twitter4j-$1 twitter4j-build
	
	echo "Converting to Android JSON..."
	if [ -d twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/org ]
	then
		rm -Rf twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/org
		sed -i "" -e 's/reader = asReader();/\/\/reader = asReader();/' twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/http/HttpResponse.java
		sed -i "" -e 's/new JSONTokener(reader)/asString()/' twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/http/HttpResponse.java
		sed -i "" -e 's/import org.json.JSONTokener;/\/\/import twitter4j.internal.org.json.JSONTokener;/' twitter4j-build/twitter4j-$1/src/main/java/twitter4j/internal/http/HttpResponse.java
	fi
	
	cd twitter4j-build/twitter4j-$1
	find . -type f |while read file; do sed -e 's/import org.json/import org.json/' $file > $file.tmp && mv $file.tmp $file; done
	sed -i "" -e 's/<dependencies>/<dependencies><dependency><groupId>org.json<\/groupId><artifactId>json<\/artifactId><version>20090211<\/version><scope>provided<\/scope><\/dependency>/' pom.xml
	
	echo "Building $1..."
	mvn clean compile jar:jar -Dmaven.test.skip=true

	cp target/twitter4j-$1-*.jar ../../
	cd ../..
}

section()
{
	if [ "$2" = "all" ]
	then
		build "core"
	else
		if [ "$1" = "$2" ]
		then
			build $1
		else
			echo "> Skipping $1"
		fi
	fi
}

section "core" $1
section "media-support" $1
section "async" $1
section "stream" $1
section "appengine" $1
section "examples" $1

echo "> Cleanup"
rm -rf twitter4j-build
echo "> Done :)"
