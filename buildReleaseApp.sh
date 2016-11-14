#!/bin/bash

echo "Configuring..."
source configure.sh

echo "Building..."
./gradlew clean assembleRelease

[ -f app/app-release.apk ] || cp app/build/outputs/apk/afh-browser_beta*.apk app/
[ -f app/afh-browser_beta*.apk ] && echo "APK Compiled" || exit 1

echo "Build finished!"
