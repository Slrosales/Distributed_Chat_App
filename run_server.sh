#!/bin/bash
PORT=3000

echo "Free port: $PORT..."
fuser -k $PORT/tcp 2>/dev/null 
pkill -f rmiregistry

export CLASSPATH=$(pwd)/classes

echo "Starting rmiregistry on port $PORT..."
rmiregistry $PORT &

sleep 2

echo "Starting HelloServer..."
java -Djava.rmi.server.hostname=localhost -cp classes src.HelloServer $PORT