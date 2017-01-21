#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/..

mvn clean compile exec:java -Dexec.mainClass="org.sidoh.echo_proxy.ProxyServer" -Dexec.args="$DIR/../config/config.yml"
