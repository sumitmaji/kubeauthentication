#!/bin/bash

source configuration

: ${PATH_TO_CHART:=chart}

while [ $# -gt 0 ]; do
    case "$1" in
        -i | --id)
        shift
        CLIENT_ID=$1
        ;;
        -s | --secret)
        shift
        CLIENT_SECRET=$1
        ;;
    esac
shift
done

if [ -z "$CLIENT_ID" ]
then
	echo "Please provide client id"
	exit 0
fi


if [ -z "$CLIENT_SECRET" ]
then
	echo "Please provide client secret"
	exit 0
fi



#CLIENT_ID=`echo $CLIENT_ID | base64 | tr -d '\n'`
#CLIENT_SECRET=`echo $CLIENT_SECRET | base64 | tr -d '\n'`

#if [[ "$(docker images -q $REPO_NAME 2> /dev/null)" == "" ]]; then
./build.sh
./tag_push.sh
#fi

sed -i "s/__CLIENT_ID__/${CLIENT_ID}/g" chart/values.yaml
sed -i "s/__CLIENT_SECRET__/${CLIENT_SECRET}/g" chart/values.yaml

helm uninstall $RELEASE_NAME
helm install $RELEASE_NAME $PATH_TO_CHART