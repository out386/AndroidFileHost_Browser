#!/bin/bash

echo "Configuring..."
source configure.sh

echo "Building..."
if [ $(which gradle) != "" ];
then
gradle clean
gradle :app:assembleRelease
else
./gradlew clean assembleRelease
fi

[ $? -eq 0 ] || echo "Build Failed!"
