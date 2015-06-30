#!/bin/bash

proxy_url=$1
if [ -z "$proxy_url" ]; then
  echo "Syntax is: $(basename $0) <proxy_url> [port = 8888]"
  exit 1
fi

port=$2

mvn install:install-file -Dfile=lib/alexa-skills-kit-1.0.120.0.jar -DgroupId=amazon -DartifactId=amazon-alexa -Dversion=1.0.120.0 -Dpackaging=jar
mvn clean compile exec:java -Dexec.mainClass="org.sidoh.echo_proxy.ProxyServer" -Dexec.args="'$proxy_url' $port"
