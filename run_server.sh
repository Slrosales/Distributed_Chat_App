#!/bin/bash

PORT=3000
JAVA_BIN=${JAVA_BIN:-$(command -v java)}
RMIREGISTRY_BIN=${RMIREGISTRY_BIN:-$(command -v rmiregistry)}

if [ -z "$JAVA_BIN" ]; then
	echo "Error: java not found in PATH"
	exit 1
fi

if [ -z "$RMIREGISTRY_BIN" ]; then
	echo "Error: rmiregistry not found in PATH"
	exit 1
fi

echo "Killing any process using port $PORT..."
lsof -ti :$PORT | xargs kill -9 2>/dev/null

sleep 1

export CLASSPATH=$(pwd)/classes

echo "Starting rmiregistry on port $PORT..."
env -u LD_LIBRARY_PATH "$RMIREGISTRY_BIN" $PORT &

sleep 1

echo "Starting ChatServerMain..."
env -u LD_LIBRARY_PATH "$JAVA_BIN" -Djava.rmi.server.hostname=localhost -cp classes src.ChatServerMain $PORT