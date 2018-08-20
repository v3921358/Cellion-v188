#!/bin/bash

	if pgrep -x "java" > /dev/null
	then
		echo "Running"
	else
		echo "Stopped"
	fi
