#!/bin/bash

echo "Cleaning old classes..."
rm -rf classes lib
mkdir -p classes lib

echo "Compiling all source files..."
javac -d classes src/*.java

echo "Creating lib/Hello.jar..."
jar cvf lib/Hello.jar -C classes src/Hello.class

echo "Done!"