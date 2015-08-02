#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

mvn install:install-file -Dfile=lib/alexa-skills-kit-1.0.120.0.jar -DgroupId=amazon -DartifactId=amazon-alexa -Dversion=1.0.120.0 -Dpackaging=jar
mvn clean compile exec:java -Dexec.mainClass="org.sidoh.echo_proxy.ProxyServer" -Dexec.args="$@"
