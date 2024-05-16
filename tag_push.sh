#!/bin/bash

source configuration
source util
docker tag $IMAGE_NAME $(fullRegistryUrl)/$REPO_NAME
docker push $(fullRegistryUrl)/$REPO_NAME