#!/bin/bash

echo "Configuring..."
source configure.sh $1

echo "Starting build..."
./gradlew assembleAppDebug

[ $? -eq 0 ] || echo "Build failed"
