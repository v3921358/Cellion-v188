#!/bin/bash

# Colours
RED='\033[0;31m'
NC='\033[0m' # No Color

# Screen Name
SCREEN_NAME="nox_screen"

function checkScreens(){
	printf "${RED}----------------------------------------------------\n"
	echo "--------------------- WARNING ----------------------"
	echo "----------------------------------------------------"

	printf "Screen with the name ${SCREEN_NAME} already exists!\nContinuing will terminate the existing screen${NC}\n"
	printf "${NC}"

	read -p "Press (y/Y) to continue " -n 1 -r
	echo    # (optional) move to a new line
	if [[ ! $REPLY =~ ^[Yy]$ ]]
	then
		echo "Leaving Nox Manager... Goodbye"
		[[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
	fi

	echo "Attempting to Terminate Existing Screen..."
	# kill all with name
#	screen -ls | egrep "\.${SCREEN_NAME}[[:space:]]+\(Detached\)" | cut -d. -f1 | xargs kill -9

	screen -ls | grep "${SCREEN_NAME}" | awk "sys {screen -X -S $1 quit}"
	sleep 3s

	screen -ls | grep "${SCREEN_NAME}" | awk "sys {screen -X -S $1 kill}"
	sleep 3s
	screen -wipe

	sleep 1s
	echo "Existing Screen Terminated!"
	sleep 1s
}

function runStartBash(){
	echo "----------------------------------------------------"
	echo "-------------- Starting Maple Server ---------------"
	echo "----------------------------------------------------"

	read -p "Press (y/Y) to continue " -n 1 -r
	echo    # (optional) move to a new line
	if [[ ! $REPLY =~ ^[Yy]$ ]]
	then
		echo "Leaving Nox Manager... Goodbye"
		[[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
	fi

	echo "Attempting to Start Server..."
	screen -d -m -S $SCREEN_NAME bash -c "cd /home/cloudrexion/nox/ && ./start.sh"
	sleep 1s
	resumeScreen
}

function resumeScreen(){
	screen -r $SCREEN_NAME
}

function checkJava(){
	echo "Checking for rouge Java process...".
	sleep 3s

	sleep 1s
	if pgrep -x "java" > /dev/null
	then
		killJava
	else
		echo "Java Not Found..."
	fi
}

function killJava(){
	printf "${RED}----------------------------------------------------\n"
	echo "--------------------- WARNING ----------------------"
	echo "----------------------------------------------------"

	printf "Rouge Java Process Found!\nContinuing will terminate the existing process${NC}\n"

	read -p "Press (y/Y) to continue " -n 1 -r
	echo    # (optional) move to a new line
	if [[ ! $REPLY =~ ^[Yy]$ ]]
	then
		echo "Leaving Nox Manager... Goodbye"
		[[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
	fi

	killall -9 java
}

# Main
echo "----------------------------------------------------"
echo "----------- Nox Manager by Poppy v1.0.1 ------------"
echo "----------------------------------------------------"
echo ""
sleep 1s

if screen -list | grep -q $SCREEN_NAME; then
	echo "Found a screen with the name ${SCREEN_NAME}"
	read -p "Press (y/Y) to resume or any key to continue... " -n 1 -r
	echo    # (optional) move to a new line
	if [[ $REPLY =~ ^[Yy]$ ]]
	then
		resumeScreen
		[[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
	fi

	# check and ask to kill
	checkScreens
	# check for java process
	checkJava
	# run server
	runStartBash
else
	# check for java process
	checkJava
	# run server
	runStartBash
fi

# ensure colour resets
printf "${NC}"

