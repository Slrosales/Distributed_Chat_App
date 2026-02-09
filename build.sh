#!/bin/bash

# 1. Limpiar y crear carpetas
echo "Cleaning old classes..."
rm -rf classes lib
mkdir -p classes lib

# 2. Compilar TODO junto
# Como tienen 'package src', javac creará automáticamente la carpeta 'classes/src'
echo "Compiling all source files..."
javac -d classes src/*.java

# 3. Crear el JAR de la interfaz
# IMPORTANTE: El JAR debe incluir la carpeta 'src' para que el paquete se mantenga
echo "Creating lib/Hello.jar..."
jar cvf lib/Hello.jar -C classes src/Hello.class

echo "Done!"