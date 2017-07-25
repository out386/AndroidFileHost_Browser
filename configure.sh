#!/bin/bash

function welcome() {
  echo "This is the configuration script to start building AFH Browser."
  echo "Please make sure to have a private key created in order to proceed."
  echo "If you don't have one, create one using i. e. Android Studio"
  echo "Then come back to this script and enter your credentials"
}

function goodbye() {
  echo "Congratz! Seems that the configuration process was successful."
}

welcome

if [ "$1" == "--clearPw" ]; then
  export KEY_PASSWD=""
  export KEYSTORE_PASSWD=""
fi

KEYSTOREPWD=""
KEYPWD=""

if [ "$1" == "--gpw" ]; then
  export KEY_PASSWD="$2"
  export KEYSTORE_PASSWD="$3"
fi

if [ -z $KEY_PASSWD ]; then
  echo "First of all the keystore needs to be configured."
  echo -en "\nKeystore password: "
  read -s -p "" KEYSTOREPWD
  echo -en "\nKey      password: "
  read -s -p "" KEYPWD
  export KEY_PASSWD="$KEYPWD"
  export KEYSTORE_PASSWD="$KEYSTOREPWD"
  echo -en "\n"
fi

goodbye
