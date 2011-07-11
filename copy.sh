#!/bin/sh
rm -rf ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
mkdir -p ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.sim.webapp/target/bundles/* ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
