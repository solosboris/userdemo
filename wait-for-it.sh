#!/usr/bin/env bash
# wait-for-it.sh
# Original: https://github.com/vishnubob/wait-for-it

set -e

hostport=(${1//:/ })
host=${hostport[0]}
port=${hostport[1]}
shift

timeout="${WAITFORIT_TIMEOUT:-60}"
shift

WAITFORIT_cmd=("$@")

wait_for() {
    for i in `seq $timeout` ; do
        if nc -z "$host" "$port" >/dev/null 2>&1 ; then
            exec "${WAITFORIT_cmd[@]}"
            return 0
        fi
        sleep 1
    done
    echo "Timeout occurred after waiting $timeout seconds for $host:$port" >&2
    return 1
}

wait_for