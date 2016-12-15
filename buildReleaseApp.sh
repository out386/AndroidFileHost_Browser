#!/bin/bash

echo "Configuring..."
source configure.sh

echo "Building..."
./gradlew clean assembleRelease

[ $? -eq 0 ] || echo "Build Failed!"
