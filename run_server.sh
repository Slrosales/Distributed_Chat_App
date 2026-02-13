#!/bin/bash

PORT=3000

echo "Killing any process using port $PORT..."
lsof -ti :$PORT | xargs kill -9 2>/dev/null

sleep 1

export CLASSPATH=$(pwd)/classes

echo "Starting rmiregistry on port $PORT..."
rmiregistry $PORT &

sleep 1

echo "Starting ChatServerMain..."
java -Djava.rmi.server.hostname=localhost -cp classes src.ChatServerMain $PORT