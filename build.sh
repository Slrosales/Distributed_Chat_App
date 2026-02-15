#!/bin/bash

echo "Cleaning old classes..."
rm -rf classes lib
mkdir -p classes lib

echo "Compiling all source files..."
javac -d classes src/*.java

echo "Creating lib/ChatClient.jar..."
jar cvf lib/ChatClient.jar -C classes src/ChatClientMain.class

echo "Creating lib/ChatServer.jar..."
jar cvf lib/ChatServer.jar -C classes src/ChatServerMain.class

echo "Done!"