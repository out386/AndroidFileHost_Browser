#!/bin/bash

# This script uploads the APK resulting from a CI build to Github Releases
version_name=v$(cat app/build.gradle | grep "versionName" | cut -d '"' -f2)

# github-release (https://github.com/aktau/github-release) is downloaded by the CI

# Create a release for Github
./bin/linux/amd64/github-release release\
    --user out386 \
    --repo AndroidFileHost_Browser \
    --tag $version_name \
    --name $version_name \

# Upload the APK to Github
./bin/linux/amd64/github-release upload \
    --user out386 \
    --repo AndroidFileHost_Browser \
    --tag $version_name \
    --name "AFH_Browser_$version_name.apk"  \
    --file app/build/outputs/apk/afh-browser_beta_*.apk

tg_message="A new release $version_name was just made for AFHBrowser. [Here's the apk](https://github.com/out386/AndroidFileHost_Browser/releases/download/$version_name/AFH_Browser_$version_name.apk)"

# Notify via Telegram
curl -F chat_id="$tg_out_id" -F parse_mode="markdown" -F text="$tg_message" "https://api.telegram.org/bot$tg_bot_key/sendMessage"
curl -F chat_id="$tg_msf_id" -F parse_mode="markdown" -F text="$tg_message" "https://api.telegram.org/bot$tg_bot_key/sendMessage"
