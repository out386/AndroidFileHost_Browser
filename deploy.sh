export version_name=v$(cat app/build.gradle | grep "versionName" | cut -d '"' -f2)

./bin/linux/amd64/github-release release\
    --user out386 \
    --repo AndroidFileHost_Browser \
    --tag $version_name \
    --name $version_name \

./bin/linux/amd64/github-release upload \
    --user out386 \
    --repo AndroidFileHost_Browser \
    --tag $version_name \
    --name "AFH_Browser_$version_name.apk"  \
    --file app/build/outputs/apk/afh-browser_beta_*.apk

export tg_message="A new release $version_name was just made for AFHBrowser. Will do this for commits later."

curl "https://api.telegram.org/bot$tg_bot_key/sendmessage" --data "text=$tg_message&chat_id=$tg_out_id"
curl "https://api.telegram.org/bot$tg_bot_key/sendmessage" --data "text=$tg_message&chat_id=$tg_msf_id"
