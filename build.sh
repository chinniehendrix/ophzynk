#!/usr/bin/env bash
set -e

eval $(minikube docker-env)

dirty=$(git status --porcelain)
if [ "$dirty" == "" ]
then
    if [[ $(git tag) ]]; then
        imagetag="$(git describe --tags master)"
    else
        imagetag="$(git rev-parse --short HEAD)"
    fi
else
    if [[ $(git tag) ]]; then
        imagetag="$(git describe --tags master)-dirty"
    else
        imagetag="$(git rev-parse --short HEAD)-dirty"
    fi
fi

mvn dockerfile:build 
imageid=$(docker images ophzynk:latest -q)
docker tag $imageid ophzynk:$imagetag
