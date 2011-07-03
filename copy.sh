#!/bin/sh
mkdir -p ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.sim.model/target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.sim.core/target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.sim.application/target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.sim.channel/target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.sim.channel.jms/target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
cp -v ../masquerade.jms.activemq/target/*.jar ../masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/
