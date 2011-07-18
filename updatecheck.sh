#!/bin/sh
mvn versions:display-dependency-updates versions:display-plugin-updates | grep -A 4 "ave newer"
