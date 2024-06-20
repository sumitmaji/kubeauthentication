#!/bin/bash

source configuration

source util

: ${PATH_TO_CHART:=chart}


./build.sh
./tag_push.sh

helm status $RELEASE_NAME -n $RELEASE_NAME && helm uninstall $RELEASE_NAME -n $RELEASE_NAME && kubectl delete ns $RELEASE_NAME


kubectl create ns $RELEASE_NAME
helm install $RELEASE_NAME $PATH_TO_CHART \
  --set image.repository=$(fullRegistryUrl)/kubeauthentication \
  --namespace $RELEASE_NAME

patchCertManager "$RELEASE_NAME" "$RELEASE_NAME" $(defaultSubdomain)

echo "Waiting for services to be up!!!!"
kubectl --timeout=180s wait --for=condition=Ready pods --all --namespace "$RELEASE_NAME"