#!/bin/bash

USERNAME=$1
# First argument is username

java -cp classes src.ChatClientMain $USERNAME
# Run client main class with username