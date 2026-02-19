#!/bin/bash

USERNAME=$1
if [ -z "$USERNAME" ]; then
    echo "Remember to introduce a username as a command-line argument"
    echo "Example: java -cp classes src.ChatClientMain <username>"
    echo "or ./run_client.sh <username>"
    exit 1
fi
java -cp classes src.ChatClientMain $USERNAME
# Run client main class with username