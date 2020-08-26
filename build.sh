#!/usr/bin/env bash
set -e

eval $(minikube docker-env)

# Apply Kafka Strimzi manifests
kubectl apply -f deploy/strimzi/install/namespaces.yaml
kubectl apply -f deploy/strimzi/install/cluster-operator -n strimzi
kubectl apply -f deploy/strimzi/install/cluster-operator/020-RoleBinding-strimzi-cluster-operator.yaml -n kafka
kubectl apply -f deploy/strimzi/install/cluster-operator/032-RoleBinding-strimzi-cluster-operator-topic-operator-delegation.yaml -n kafka
kubectl apply -f deploy/strimzi/install/cluster-operator/031-RoleBinding-strimzi-cluster-operator-entity-operator-delegation.yaml -n kafka
kubectl apply -f deploy/kafka/install/cluster.yaml -n kafka

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

mvn clean package
imageid=$(docker images ophzynk:latest -q)
docker tag $imageid ophzynk:$imagetag
