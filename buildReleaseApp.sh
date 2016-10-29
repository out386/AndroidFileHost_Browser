#!/bin/bash

echo "Configuring..."
source configure.sh

echo "Building..."
./gradlew clean assembleRelease

[ -f app/app-release.apk ] || cp app/build/outputs/apk/app-release.apk app/app-release.apk
[ -f app/app-release.apk ] && echo "APK Compiled" || exit 1

echo "Build finished!"
