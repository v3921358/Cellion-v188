var status = 0; 
var result = -1;
var win = 0;
var options = ["Rock", "Paper", "Scissors"]

var coinId = 04310189;
var gameSelection = -1;
function start() 
{ 
	status = -1; 
	action(1, 0, 0); 
} 

function action(mode, type, selection) 
{ 
	if (mode == -1) 
	{ 
		cm.dispose(); 
	} 
	else 
	{ 
		if (status == 0 && mode == 0) 
		{
			cm.sendOk("Ok, see you later!"); 
			cm.dispose(); 
			return; 
		} 
		if (mode == 1) 
		{ 
			status++; 
		} 
		else	
		{ 
			status--; 
		} 
		if (status == 0) 
		{
			cm.sendSimple("Welcome to minigames! What game would you like to play? \r\n#L0#Rock, Paper Scissors#l\r\n\#L1#Number Guesser#l");
		}
		else if(status == 1)
		{
			gameSelection = selection;
		}
		if(gameSelection == 0)
		{
			playRockPaperScissors(selection);
		}
		else if(gameSelection == 1)
		{
			playNumberGuesser(selection);
		}

	}
}

function playRockPaperScissors(selection)
{
	var bigWin = 5;
	var smallWin = 2;
	if(status == 1)
	{
		cm.sendSimple("What will you choose?\r\n#L0#Rock#l\r\n#L1#Paper#l\r\n#L2#Scissors#l");
		cm.gainItem(coinId, -1);
	}
	else if(status == 2)
	{
		result = Math.random() * 3
		if(result == 0)
		{
			win == 4;
		}
		if(selection > win)
		{
			cm.sendOk(options[selection].toString() + " beats " + options[result].toString() + ", you win!\r\n" + bigWin.toString() + " dankassminigame points awarded!");
			cm.gainItem(04034414,bigWin);
			cm.dispose();
		}
		else if(selection == win)
		{
			cm.sendOk(options[selection].toString() + " ties " + options[result].toString() + ", you tied.\r\n" + smallWin.toString() + " dankassminigame points awarded!");
			cm.gainItem(04034414,smallWin);
			cm.dispose();
		}
		else
		{
			cm.sendOk(options[selection].toString() + " loses to " + options[result].toString() + ", you lose.\r\n No points awarded.");
			cm.dispose();
		}
	}
}

function numberGuesser(selection)
{
	var bigWin = 5;
	var smallWin = 2;
	if(status == 1)
	{
		cm.getText("Guess what number I'm thinking of between 1 and 50!");
	}
	if(status == 2)
	{
		var value = parseInt(selection);
		if(value < 1 || value > 50)
		{
			cm.sendOk("Not a valid value!");
			return;
		}
		else
		{
			cm.gainItem(coinId, -1);
			var result =  Math.floor(Math.random() * (50 - 1 + 1)) + 1;
			if(result > value + 5 || result < value - 5)
			{
				cm.sendOk("You guessed was " + value.toString() + ", Number I was thinking was " + result.toString() + "\r\n\You lose!");
				cm.dispose();				
			}
			else if(result != value)
			{
				cm.sendOk("Number guessed was " + value.toString() + ", Number I was thinking was " + result.toString() + "\r\n\You were close.. You win a small prize of " + smallWin.toString() + " dankassminigame points!");
				cm.gainItem(04034414,smallWin);
				cm.dispose();	
			}
			else
			{
				cm.sendOk("Number guessed was " + value.toString() + ", Number I was thinking was " + result.toString() + "\r\n\You guessed it! You've won a big prize of " + bigWin.toString() + " dankassminigame points!");
				cm.gainItem(04034414,bigWin);
				cm.dispose();	
			}
		}	
	}
}
	