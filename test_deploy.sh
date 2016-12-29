version_name=v$(cat app/build.gradle | grep "versionName" | cut -d '"' -f2)
version_code=$(cat app/build.gradle | grep -m 1 "versionCode" | awk  '{print $2}')
apk="app/build/outputs/apk/afh-browser_beta_$version_code.apk"

tg_message="New commits were pushed to the test branch of AFHBrowser.
Commits are (here)[https://github.com/out386/AndroidFileHost_Browser/commits/tree].
Here is the apk:"

curl "https://api.telegram.org/bot$tg_bot_key/sendmessage" --data "text=$tg_message&chat_id=$tg_out_id"
curl "https://api.telegram.org/bot$tg_bot_key/sendmessage" --data "text=$tg_message&chat_id=$tg_msf_id"

curl -F chat_id=$tg_out_id -F document=@$apk "https://api.telegram.org/bot$tg_bot_key/senddocument"
curl -F chat_id=$tg_msf_id -F document=@$apk "https://api.telegram.org/bot$tg_bot_id/senddocument"
