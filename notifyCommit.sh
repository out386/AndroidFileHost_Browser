#!/bin/bash
commit_message=$(git log --format=oneline -n 1 $CIRCLE_SHA1)
tmp_sha=$CIRCLE_SHA1" "
commit_message_final=${commit_message#$tmp_sha}

tg_message="New commit for AFHBrowser.

$commit_message_final

[View it on Github](https://github.com/out386/AndroidFileHost_Browser/commit/$CIRCLE_SHA1)"

# Notify via Telegram
curl -F chat_id="$tg_out_id" -F parse_mode="markdown" -F text="$tg_message" "https://api.telegram.org/bot$tg_bot_key/sendMessage"
curl -F chat_id="$tg_msf_id" -F parse_mode="markdown" -F text="$tg_message" "https://api.telegram.org/bot$tg_bot_key/sendMessage"
