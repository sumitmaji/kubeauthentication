#!/bin/bash

source configuration
source util.sh
docker tag $IMAGE_NAME $(fullRegistryUrl)/$REPO_NAME
docker push $(fullRegistryUrl)/$REPO_NAME