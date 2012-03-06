#!/bin/sh
cd masquerade.sim.application && mvn clean install $@ && cd .. && cp masquerade.sim.application/target/masquerade*.jar masquerade.sim.webapp/src/main/webapp/WEB-INF/bundles/ && cd masquerade.sim.webapp && mvn install
