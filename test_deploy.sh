#!/bin/bash

# This script sends test APKs via a telegram bot. Useful for testing.

version_name=v$(cat app/build.gradle | grep "versionName" | cut -d '"' -f2)
version_code=$(cat app/build.gradle | grep -m 1 "versionCode" | awk  '{print $2}')
apk="app/build/outputs/apk/afh-browser_beta_$version_code.apk"

tg_message="New commits were pushed to the test branch of AFHBrowser.
Commits are [here](https://github.com/out386/AndroidFileHost_Browser/commits/test).
Here is the apk:"

# Notify via Telegram
curl -F chat_id="$tg_out_id" -F parse_mode="markdown" -F text="$tg_message" "https://api.telegram.org/bot$tg_bot_key/sendMessage"
curl -F chat_id="$tg_msf_id" -F parse_mode="markdown" -F text="$tg_message" "https://api.telegram.org/bot$tg_bot_key/sendMessage"

# Send the APK directly to the Telegram bot
curl -F chat_id="$tg_out_id" -F document=@$apk "https://api.telegram.org/bot$tg_bot_key/sendDocument"
curl -F chat_id="$tg_msf_id" -F document=@$apk "https://api.telegram.org/bot$tg_bot_id/sendDocument"
