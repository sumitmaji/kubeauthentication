#!/bin/bash

source configuration

source util.sh

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
        -u | --user)
        shift
        DOCKER_USER=$1
        ;;
        -p | --password)
        shift
        DOCKER_PASSWORD=$1
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

SECRET_NAME=regcred
kubectl get secret $SECRET_NAME >/dev/null 2>&1 || kubectl create secret docker-registry $SECRET_NAME --docker-server=$REGISTRY --docker-username=$DOCKER_USER --docker-password=$DOCKER_PASSWORD
helm uninstall $RELEASE_NAME
helm install $RELEASE_NAME $PATH_TO_CHART \
  --set ingress.annotations="$(
                                 cat <<EOF
                                 kubernetes.io/ingress.class: nginx
                                 nginx.ingress.kubernetes.io/proxy-redirect-from: "default"
                                 nginx.ingress.kubernetes.io/configuration-snippet: |
                                   proxy_redirect https://master.cloud.com https://master.cloud.com;
                                   proxy_redirect http://master.cloud.com https://master.cloud.com;
                                   #proxy_redirect ~^https(.*)(redirect_uri=)(http)(.*) https$1$2https$4; #Edit the location header
                             EOF
                               )" \
  --namespace $RELEASE_NAME \
  --create-namespace

patchCertManager "$RELEASE_NAME" "$RELEASE_NAME" $(defaultSubdomain)