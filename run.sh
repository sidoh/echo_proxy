#!/bin/bash

mvn install:install-file -Dfile=lib/alexa-skills-kit-1.0.120.0.jar -DgroupId=amazon -DartifactId=amazon-alexa -Dversion=1.0.120.0 -Dpackaging=jar
mvn clean compile exec:java -Dexec.mainClass="org.sidoh.echo_proxy.ProxyServer"
