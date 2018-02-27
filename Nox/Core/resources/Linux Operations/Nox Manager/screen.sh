#!/bin/bash
echo "Starting Nox Screen Script by Poppy"
echo "Prepping..."
sleep 2s

echo "Checking for previous screen..."
sleep 1s
screen -D -RR nox_screen -X quit || true
sleep
