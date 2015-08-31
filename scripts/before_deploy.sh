#!/bin/bash
set -ev
echo "TRAVIS ENV: $TRAVIS_JOB_ID $TRAVIS_BUILD_NUMBER $TRAVIS_BUILD_ID"

if [ -z ${TRAVIS_BUILD_NUMBER+x} ]; then 
	echo "TRAVIS_BUILD_NUMBER is unset"; 
	TRAVIS_BUILD_NUMBER="LOCAL"
else 
	echo "TRAVIS_BUILD_NUMBER is set to '$TRAVIS_BUILD_NUMBER'"; 
fi

for file in ./*/target/*.jar; do
	#extension="${file##*-}"
	filename="${file%-*}"
	echo $filename 
	echo "$filename-$(date +%Y%m%d-%H%M%S)-${TRAVIS_BUILD_NUMBER}.jar"
	mv -nv -- "$file" "$filename-$(date +%Y%m%d-%H%M%S)-${TRAVIS_BUILD_NUMBER}.jar"
done
