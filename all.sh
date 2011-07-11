#!/bin/sh
rm -rf ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
mvn -o eclipse:eclipse clean install && ./copy.sh
