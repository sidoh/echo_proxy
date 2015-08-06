#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo_proxy_tmpdir() {
  cd $DIR/..
  mkdir -p tmp
  cd tmp

  pwd
}

echo_proxy_pidfile() {
  echo "$(echo_proxy_tmpdir)/echo_proxy.pid"
}

echo_proxy_locate() {
  pidfile=$(echo_proxy_pidfile)
  if [[ -e "$pidfile" ]] && [[ $(ps -p $(cat "$pidfile") -o 'pid=' | wc -l) -gt 0 ]]; then
    cat "$pidfile"
  fi
}

echo_proxy_logdir() {
  cd $DIR/..
  mkdir -p log
  cd log

  pwd
}

main() {
  echo_proxy_locate
}

[[ "$0" == "$BASH_SOURCE" ]] && main