#!/bin/bash

echo "Configuring..."
source configure.sh

echo "Building..."
./gradlew clean assembleRelease

[ -z "$BUILD_TAG" ] || python3.5 gen_manifest.py

[ $? -eq 0 ] || echo "Build Failed!"
