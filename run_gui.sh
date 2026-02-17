#!/bin/bash
if [ -z "$1" ]; then
    echo "Remember to introduce a username as a command-line argument"
    echo "Example: java -cp classes src.ChatGUI <username>\nor\n./run_gui.sh <username>"
    exit 1
fi

# Use port 3000 if no second argument is provided
PORT=${2:-3000}

java -cp classes src.ChatGUI "$1" localhost "$PORT"