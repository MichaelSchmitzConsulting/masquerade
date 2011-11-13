#!/bin/sh
rm -r ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
mvn clean $@ eclipse:eclipse install && ./copy.sh
