#!/bin/sh
rm -rf ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/masquerade.sim.application*.jar
pushd ../masquerade.sim.application
mvn -Dmaven.test.skip=true -o install
cp -v target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
popd
